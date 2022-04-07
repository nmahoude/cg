package cgfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class GameReader {
	List<String> stderr = new ArrayList<>();
	List<String> inputs = new ArrayList<>();

	public void readReplayFromString(String replay) {
		Gson gson = new Gson();
		JsonObject fromJson = gson.fromJson(replay, JsonObject.class);
		read(fromJson);
	}

	
	public void readReplayFromFile(String filename) throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		//File file = new File(classLoader.getResource("referee_input.txt").getFile().replaceAll("%20", " "));
		File file = new File(classLoader.getResource(filename).getFile().replaceAll("%20", " "));
		
		InputStream targetStream = new FileInputStream(file);
		JsonReader reader = new JsonReader(new FileReader(file.getPath()));
		Gson gson = new Gson();
		JsonObject fromJson = gson.fromJson(reader, JsonObject.class);
		
		read(fromJson);
	}

	private void read(JsonObject fromJson) {
		stderr.clear();
		inputs.clear();
		
		
		JsonElement frames = fromJson.get("frames");
		frames.getAsJsonArray().forEach(
				el -> {
					JsonObject object = el.getAsJsonObject();
					JsonElement jsonElement = object.get("stderr");
					if (jsonElement != null) {
						String complete = jsonElement.getAsString();
						stderr.add(complete);

						StringBuffer buf = new StringBuffer();
						String[] lines = complete.split("\n");
						for (String line : lines) {
							if (line.startsWith("^")) {
								buf.append(line.substring(1)+" \r\n");
							}
						}
						inputs.add(buf.toString());
					}
				}
		);
	}

	/**
	 * 0 is the setup + 1st turn
	 * 1+ is a turn
	 * @param turn
	 * @return
	 */
	public String getInput(int turn) {
		return inputs.get(turn);
	}
	
	public String getStderr(int turn) {
		return stderr.get(turn);
	}


  public int getMaxTurn() {
    return inputs.size()-1;
  }
}
