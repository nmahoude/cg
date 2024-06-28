package fall2023._fx.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cgfx.frames.Frame;
import cgfx.wrappers.GameWrapper;
import fall2023.Drone;
import fall2023.Fish;
import fall2023.Player;
import fall2023.Pos;
import fall2023.State;
import fast.read.FastReader;

public class F23GameWrapper extends GameWrapper {

  public State state = new State();
  public List<Fish> fishes = new ArrayList<>();
  public List<Pos> path = new ArrayList<>();
  
  public static Pos[][] allPositions = new Pos[4][200];
  static {
    for (int d=0;d<4;d++) {
      for (int t=0;t<200;t++) {
        allPositions[d][t] = new Pos(-1,-1);
      }
    }
  }
  
  public F23GameWrapper() {
    state.previousState = new State();
  }
  
  
  @Override
  protected void _applyAction(String arg0) {
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    return null;
  }

  @Override
  public List<String> getActionsFromString(String pasteString) {
    String[] actions = pasteString.split(";");
    List<String> actionsList = new ArrayList<>();
    for (String a : actions) {
      actionsList.add(a);
    }
    return actionsList;
  }

  @Override
  protected void _copyFrom(GameWrapper arg0) {
  }

  @Override
  protected void _resetFromBase() {
  }

  @Override
  protected void _think() {
  }

  @Override
  protected void readGlobalInput(String input) {
    State.readInit(FastReader.fromString(cleanInput(input)));
  }

  @Override
  protected void readTurnInput(String input) {
    FastReader in = FastReader.fromString(input);
    State.readPackedInit(in);

    Player.state = state;
    state.readOptional(in);
    state.readPacked(in);
  }

  public void readFull(String input) {
    FastReader in = FastReader.fromString(cleanInput(input));
    State.readPackedInit(in);

    Player.state = state;
    state.readOptional(in);
    state.readPacked(in);
  }
  
  @Override
  public void readFrame(Frame frame) {
    super.readFrame(frame);
    
    for (Drone d: state.dronesById) {
      allPositions[d.id][State.turn].copyFrom(d.pos);
    }
    
    fishes .clear();
    // read fishes from view
    System.err.println("Read the view ! " + frame.view());
    String view = frame.view();
    if (view.indexOf("global") != -1) {
      view = view.substring(view.indexOf("frame"));
    }
    int indexOfGraphics = view.indexOf("\"graphics\":\"");
    if (indexOfGraphics != -1) {
      String graphics = view.substring(indexOfGraphics + 12);
      System.err.println("Graphics : "+graphics);
      String[] splitted = graphics.split("\\\\n");
      int creatureCount = Integer.parseInt(splitted[0]);
      for (int i=0;i<creatureCount + State.uglyCount;i++) {
        try (Scanner in = new Scanner(splitted[i+1])) {
          Fish fish = new Fish(creatureCount + i);
          fish.id = in.nextInt();
          fish.pos.x = in.nextInt();
          fish.pos.y  = in.nextInt();
          fish.speed.vx = in.nextInt();
          fish.speed.vy = in.nextInt();
          fishes.add(fish);
        }
      }
      
    }
  }
  
}
