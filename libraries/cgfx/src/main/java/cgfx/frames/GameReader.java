package cgfx.frames;

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

/**
 * Read a full game from a json file 
 * and transform it into frames
 * 
 * @author nmahoude
 *
 */
public class GameReader {
	public List<Frame> frames = new ArrayList<>();
	
	
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
		
		JsonElement frames = fromJson.get("frames");
		frames.getAsJsonArray().forEach(
				el -> {
				  
				  Frame frame = new Frame();

				  JsonObject content = el.getAsJsonObject();
				  frame.agentId = content.get("agentId").getAsInt();
				  
				  JsonElement stdoutElement = content.get("stdout");
          if (stdoutElement != null) {
            frame.stdout = stdoutElement.getAsString().trim();
          }
				  
					JsonElement stdErrElement = content.get("stderr");
					if (stdErrElement != null) {
						String complete = stdErrElement.getAsString();
						frame.stderr = complete;
					}
					
          this.frames.add(frame);
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
	  return frames.get(turn).cleanStderr();
	}
	
	public String getStderr(int turn) {
    return frames.get(turn).stderr;
	}


  public int getMaxTurn() {
    return frames.size()-1;
  }


}
