package sg22._actionsextractor;

import java.io.FileNotFoundException;

import cgfx.frames.Frame;
import cgfx.frames.GameReader;

public class Extractor {

  public static void main(String[] args) throws FileNotFoundException {
    
    
    GameReader gameReader = new GameReader();
    gameReader.readReplayFromFile("referee.json");
    
    for (int i=0;i<gameReader.frames.size();i++) {
      Frame frame = gameReader.frames.get(i);
      String stdout = frame.stdout();
      
      
      //System.err.println(frame.agentId+" => "+frame.stdout);
      if (frame.agentId() == 1) {
        System.out.println("actions.add(Action.from(\""+frame.stdout()+"\"));");
        
        
      }
      
    }
    
    
    
  }
}
