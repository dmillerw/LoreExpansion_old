package dmillerw.lore.common.lore.data.json;

import com.google.common.collect.Lists;
import com.google.gson.*;
import dmillerw.lore.common.lore.data.Commands;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class CommandDeserializer implements JsonDeserializer<Commands> {

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
