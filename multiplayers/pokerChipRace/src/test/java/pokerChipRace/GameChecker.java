package pokerChipRace;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class GameChecker {

  Game game = new Game();

  public static void main(String[] args) throws IOException {
    GameChecker gc =new GameChecker();
    gc.read2();
  }
  private void read2() {
    JsonReader jsonReader = Json.createReader(GameChecker.class.getClassLoader().getResourceAsStream("play.json"));
    
    //get JsonObject from JsonReader
    JsonObject jsonObject = jsonReader.readObject();   
    JsonObject success = jsonObject.getJsonObject("success");
    JsonArray frames = success.getJsonArray("frames");
    for (JsonValue f: frames) {
      JsonObject frameObject = (JsonObject)f;
//      System.out.println("VIEW = "+frameObject.getString("view"));
      
      Frame frame = new Frame(game);
      
      frame.order = frameObject.getString("stdout", "");
      frame.view = frameObject.getString("view");
      frame.agentId = frameObject.getInt("agentId");
      frame.keyframe = frameObject.getBoolean("keyframe");
      
      frame.frameToTUCheck();
    }
  }
}
