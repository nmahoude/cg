package utg2019.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import trigonometryInt.Point;
import utg2019.sim.Action;
import utg2019.sim.Item;
import utg2019.world.World;

public class ReplayReader {

  private static final String FILENAME = "/replay.json";
  private static int frameCount;

  public static void main(String[] args) {
    init();
    
    try {
    InputStream inputStream = ReplayReader.class.getResourceAsStream(FILENAME);
    
    JsonObject jsonObject = new JsonParser()
                                    .parse(new JsonReader(new InputStreamReader(inputStream)))
                                    .getAsJsonObject();

    
    frameCount = 0;
    int player = 0;
    JsonArray array = jsonObject.getAsJsonArray("frames");
    for (int i=0;i<array.size();i++) {
      JsonObject frame = array.get(i).getAsJsonObject();
      int agentId = frame.get("agentId").getAsInt();
      
      if (agentId != -1) {
        System.out.println("Read actions for player "+player);
        Action action[] = readActions(frame);
        player = 1 - player; // swap player
      }
      if (frame.get("keyframe").getAsBoolean()) {
        World world = readFrame(frame);
      }
    }
    
    
    } catch(Exception e) {
      System.out.println("ERREUR : "+e);
      e.printStackTrace();
    }
    System.out.println("THE END");
  }

  private static void init() {
    Point.init(30, 15);
  }

  private static Action[] readActions(JsonObject frame) {
    String stdout = frame.get("stdout").getAsString();
    String actionStr[] = stdout.split("\n");
    Action actions[] = new Action[5];
    for (int i=0;i<5;i++) {
      try {
        actions[i] = readAction(actionStr[i]);
      } catch(Exception e) {
        e.printStackTrace();
        actions[i] = Action.doWait();
      }
    }
    return actions;
  }

  private static Action readAction(String actionStr) {
    System.out.println("    read : "+actionStr);
    String split[] = actionStr.split(" ");
    String order = split[0];
    int x,y;
    
    switch(order) {
      case "WAIT":
      case "null":
        return Action.doWait();
      case "REQUEST":
        return Action.request(Item.valueOf(split[1].trim()));
      case "MOVE":
        x = Integer.parseInt(split[1]);
        y = Integer.parseInt(split[2]);
        return Action.move(Point.get(x, y));
      case "DIG":
        x = Integer.parseInt(split[1]);
        y = Integer.parseInt(split[2]);
        return Action.dig(Point.get(x, y));
      default:
          throw new RuntimeException("Unknwon order "+actionStr);
    }
  }

  private static World readFrame(JsonObject frame) {
    System.out.println("read frame #"+frameCount);
    frameCount++;

    World world = new World();

    String viewStr = frame.get("view").getAsString();
    String graphicsStr = viewStr.substring(3);
    JsonObject graphics = new JsonParser()
                            .parse(graphicsStr)
                            .getAsJsonObject();
    
    JsonObject global = graphics.get("global").getAsJsonObject();
    String vehicles = graphics.get("frame").getAsJsonObject().get("graphics").getAsString();
    readFrameVehicles(world, vehicles);
    
    return world;
  }

  private static void readFrameVehicles(World world, String vehiclesStr) {
    Scanner in = new Scanner(vehiclesStr);
    int u1 = in.nextInt();
    int u2 = in.nextInt();
    int u3 = in.nextInt();
    
    for (int i=0;i<5;i++) {
      int r1 = in.nextInt();
      int r2 = in.nextInt();
      int r3 = in.nextInt();
      int r4 = in.nextInt();
      int r5 = in.nextInt();
    }
  }
}
