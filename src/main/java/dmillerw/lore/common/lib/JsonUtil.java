package dmillerw.lore.common.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dmillerw.lore.common.lore.data.Commands;
import dmillerw.lore.common.lore.data.json.CommandDeserializer;

public class JsonUtil {

    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(Commands.class, new CommandDeserializer());

            gson = gsonBuilder.setPrettyPrinting().create();
        }
        return gson;
    }
}
