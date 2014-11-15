package dmillerw.lore.common.lore.json;

import com.google.gson.*;
import dmillerw.lore.common.lore.data.LoreTags;

import java.lang.reflect.Type;

/**
 * @author dmillerw
 */
public class TagSerializer implements JsonSerializer<LoreTags> {

    @Override
    public JsonElement serialize(LoreTags src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("default", new JsonPrimitive(src.defaultTag));
        return jsonObject;
    }
}
