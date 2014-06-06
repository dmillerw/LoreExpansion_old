package dmillerw.lore.lore;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author dmillerw
 */
public class TagSerializer implements JsonSerializer<LoreData.DeserializedLoreTag> {

	@Override
	public JsonElement serialize(LoreData.DeserializedLoreTag src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("default", new JsonPrimitive(src.defaultTag));
		return jsonObject;
	}
}
