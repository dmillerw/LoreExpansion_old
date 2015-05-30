package dmillerw.lore.common.lib;

import com.google.common.collect.Lists;
import com.google.gson.*;
import dmillerw.lore.common.lore.data.Commands;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class JsonUtil {

    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(Commands.class, new CommandDeserializer());
            gsonBuilder.registerTypeAdapter(Commands.CommandEntry.class, new CommandEntryDeserializer());

            gson = gsonBuilder.setPrettyPrinting().create();
        }
        return gson;
    }

    public static class CommandDeserializer implements JsonDeserializer<Commands> {

        @Override
        public Commands deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<Commands.CommandEntry> entries = Lists.newArrayList();

            if (json.isJsonPrimitive()) { // Single command
                entries.add(new Commands.CommandEntry(new String[] {json.getAsString()}, 0));
            } else if (json.isJsonArray()) { // Array of either Strings, or command objects
                JsonArray array = json.getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    entries.addAll(Arrays.asList(deserialize(array.get(i), typeOfT, context).commands)); // Such a hack
                }
            } else if (json.isJsonObject()) { // Single command object
                entries.add((Commands.CommandEntry) context.deserialize(json.getAsJsonObject(), Commands.CommandEntry.class));
            }

            Commands commands = new Commands();
            commands.commands = entries.toArray(new Commands.CommandEntry[entries.size()]);
            return commands;
        }
    }

    public static class CommandEntryDeserializer implements JsonDeserializer<Commands.CommandEntry> {

        @Override
        public Commands.CommandEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new Commands.CommandEntry(new String[] {json.getAsString()}, 0);
            } else if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                List<String> commands = Lists.newArrayList();

                for (int i=0; i<array.size(); i++) {
                    JsonElement element = array.get(i);
                    if (element.isJsonPrimitive()) { // We assume it's filled with strings, and skip everything else
                        commands.add(element.getAsString());
                    }
                }

                return new Commands.CommandEntry(commands.toArray(new String[commands.size()]), 0);
            } else if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();

                if (!object.has("commands"))
                    throw new JsonParseException("CommandEntry is missing 'commands' key");

                JsonElement element = object.get("commands");
                String[] commands;

                if (element.isJsonPrimitive()) {
                    commands = new String[] {element.getAsString()};
                } else {
                    commands = context.deserialize(element, String[].class);
                }

                int delay = object.has("delay") ? object.get("delay").getAsInt() : 0;

                return new Commands.CommandEntry(commands, delay);
            } else {
                throw new JsonParseException("Cannot get CommandEntry from " + json.getClass().getSimpleName());
            }
        }
    }
}
