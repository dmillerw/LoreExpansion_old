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
public class LoreDeserializer implements JsonDeserializer<LoreData.DeserializedLore> {

	@Override
	public LoreData.DeserializedLore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		LoreData.DeserializedLore data = new LoreData.DeserializedLore();

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();

			if (key.equalsIgnoreCase("page")) {
				data.page = element.getAsInt();
			} else if (key.equalsIgnoreCase("dimension")) {
				data.dimension = element.getAsInt();
			} else if (key.equalsIgnoreCase("title")) {
				data.title = element.getAsString();
			} else if (key.equalsIgnoreCase("lore")) {
				data.lore = element.getAsString();
			} else if (key.equalsIgnoreCase("sound")) {
				data.sound = element.getAsString();
			}
		}

		return data;
	}
}
