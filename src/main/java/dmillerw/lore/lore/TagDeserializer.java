package dmillerw.lore.lore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author dmillerw
 */
public class TagDeserializer implements JsonDeserializer<LoreData.DeserializedLoreTag> {

	@Override
	public LoreData.DeserializedLoreTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		LoreData.DeserializedLoreTag tag = new LoreData.DeserializedLoreTag();

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();

			if (key.equalsIgnoreCase("default")) {
				tag.defaultTag = element.getAsString();
			} else {
				try {
					int dimension = Integer.parseInt(key);
					tag.mapping.put(dimension, element.getAsString());
				} catch (NumberFormatException ex) {
					// Invalid, should log
				}
			}
		}

		return tag;
	}
}
