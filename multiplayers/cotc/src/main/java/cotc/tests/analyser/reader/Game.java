package cotc.tests.analyser.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
  public List<Frame > frames = new ArrayList<>();
  
  String gameName;
  int playerCount;
  int shipCount;
  private String options;
  
  public void readFrame(String[] inputs) {
    int i=0;
    String keyframe =  inputs[i++];
    if (keyframe.equals("KEY_FRAME 0")) {
      gameName = inputs[i++];
      playerCount = Integer.parseInt(inputs[i++]);
      options = inputs[i++];
      
      Scanner in = new Scanner(options);
      in.nextInt();
      in.nextInt();
      shipCount = in.nextInt();
      in.nextInt();
      
      in.close();
    }
    Frame frame = new Frame(this);
    frame.readInputsFrom(inputs, i);
    frames.add(frame);
  }
}
