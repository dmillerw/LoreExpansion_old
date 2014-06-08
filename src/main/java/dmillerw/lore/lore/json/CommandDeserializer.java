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
public class CommandDeserializer implements JsonDeserializer<Lore.CommandWrapper> {

	@Override
	public Lore.CommandWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Lore.CommandWrapper wrapper = new Lore.CommandWrapper();

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			String key = entry.getKey();
			JsonElement element = entry.getValue();

			if (key.equalsIgnoreCase("pickup")) {
				wrapper.pickup = context.deserialize(element, new TypeToken<String[]>(){}.getType());
			}
		}

		return wrapper;
	}

}
