package dmillerw.lore.lore.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dmillerw.lore.lore.data.Lore;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author dmillerw
 */
public class LoreDeserializer implements JsonDeserializer<Lore> {

	@Override
	public Lore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Lore data = new Lore();

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();

			if (key.equalsIgnoreCase("page")) {
				data.page = element.getAsInt();
			} else if (key.equalsIgnoreCase("dimension")) {
				data.dimension = element.getAsInt();
			} else if (key.equalsIgnoreCase("title")) {
				data.title = element.getAsString();
			} else if (key.equalsIgnoreCase("body")) {
				data.body = element.getAsString();
			} else if (key.equalsIgnoreCase("sound")) {
				data.sound = element.getAsString();
			} else if (key.equalsIgnoreCase("commands")) {
				data.commands = context.deserialize(element, new TypeToken<Lore.CommandWrapper>(){}.getType());
			} else if (key.equalsIgnoreCase("autoplay")) {
				data.autoplay = element.getAsBoolean();
			}
		}

		return data;
	}
}
