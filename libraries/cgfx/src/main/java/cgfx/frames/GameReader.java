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
	public List<Frame> myFrames = new ArrayList<>();
	
	
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
				  
          JsonElement viewElement = content.get("view");
          if (viewElement != null) {
            frame.view = viewElement.getAsString().trim();
          }

          JsonElement stdErrElement = content.get("stderr");
					if (stdErrElement != null) {
						String complete = stdErrElement.getAsString();
						frame.stderr = complete;
					}
					
					if (this.frames.size() > 1 
					    && !this.frames.get(this.frames.size()-1).view.contains("graphics")) {
					  this.frames.get(this.frames.size()-1).view = frame.view;
					}
          this.frames.add(frame);
          if (!frame.stderr.isEmpty()) {
            this.myFrames.add(frame);
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
	  return myFrames.get(turn).stderr();
	}
	
  public String getCleanInput(int turn) {
    return myFrames.get(turn).cleanStderr();
  }

  public String getStderr(int turn) {
    return myFrames.get(turn).stderr;
	}

  public int getMaxTurn() {
    return myFrames.size();
  }

  public Frame getFrame(int index) {
    return frames.get(index);
  }

  public Frame getMyFrame(int index) {
    return myFrames.get(index);
  }

}
