package dmillerw.lore.common.lore.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dmillerw.lore.common.lore.data.LoreTags;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author dmillerw
 */
public class TagDeserializer implements JsonDeserializer<LoreTags> {

	@Override
	public LoreTags deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		LoreTags tag = new LoreTags();

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
					if (key.equalsIgnoreCase("global")) {
						tag.mapping.put(Integer.MAX_VALUE, element.getAsString());
					}
				}
			}
		}

		return tag;
	}
}
