package pokerChipRace;

import java.util.Locale;
import java.util.Scanner;

import pokerChipRace.entities.Entity;

public class Frame {
  public Game game;
  
  public int agentId;
  public int frameId;
  public String view;
  public String order;
  public boolean keyframe;

  public Frame(Game game) {
    this.game = game;
  }
  
  public GameState frameToState() {
    GameState state = new GameState();

    Scanner in = new Scanner(view).useLocale(Locale.ENGLISH); ;
    
    frameId = in.nextInt();
    if (agentId == -1) {
      // init frame, read some informations
      in.nextLine();
      in.nextLine();
      in.nextLine();
    }
    
    int entityCount = in.nextInt();
    
    for (int i=0;i<entityCount;i++) {
      int id = in.nextInt(); // Unique identifier for this entity
      int owner = in.nextInt(); // The owner of this entity (-1 for neutral
                                // droplets)
      float radius = in.nextFloat(); // the radius of this entity
      float x = in.nextFloat(); // the X coordinate (0 to 799)
      float y = in.nextFloat(); // the Y coordinate (0 to 514)
      //float vx = in.nextFloat(); // the speed of this entity along the X axis
      //float vy = in.nextFloat(); // the speed of this entity along the Y axis      
      float angle = in.nextFloat(); // ????
      int droplets = in.nextInt();
      if (droplets > 0) {
        in.nextLine();
      }
      
      Entity entity = state.getChip(id);
      entity.update(owner, x, y, radius, 0, 0);
    }
    
    in.close();
    
    return state;
  }
  
  public GameState frameToTUCheck() {
    GameState state = new GameState();

    Scanner in = new Scanner(view).useLocale(Locale.ENGLISH); ;
    
    frameId = in.nextInt();
    if (agentId == -1) {
      // init frame, read some informations
      in.nextLine();
      in.nextLine();
      in.nextLine();
    }
    
    int entityCount = in.nextInt();
    if (agentId == -1) {
      System.out.println("int entityCount = "+entityCount+";");
    } else {
      outputOrders();
      if (keyframe == true) {
        System.out.println("simulation.simulate();");
      }
    }

    
    for (int i=0;i<entityCount;i++) {
      int id = in.nextInt(); // Unique identifier for this entity
      int owner = in.nextInt(); // The owner of this entity (-1 for neutral
                                // droplets)
      float radius = in.nextFloat(); // the radius of this entity
      float x = in.nextFloat(); // the X coordinate (0 to 799)
      float y = in.nextFloat(); // the Y coordinate (0 to 514)
      float angle = in.nextFloat(); // ????
      int droplets = in.nextInt();
      if (droplets > 0) {
        //float vx = in.nextFloat(); // the speed of this entity along the X axis
        //float vy = in.nextFloat(); // the speed of this entity along the Y axis      
        in.nextLine();
      }
      
      if (agentId == -1) {
        System.out.println("readEntity("+id+","+owner+","+radius+","+x+","+y+");");
      } else {
        if (keyframe == true) {
          System.out.println("checkEntity("+id+","+owner+","+radius+","+x+","+y+");");
        }
      }
    }
    
    in.close();
    
    return state;
  }

  
  
  public void outputOrders() {
    Scanner in = new Scanner(order).useLocale(Locale.ENGLISH);
    int i=0;
    while (in.hasNextLine()) {
      String order = in.nextLine();
      if (order.toUpperCase().contains("WAIT")) {
        System.out.println("applyOrder("+agentId+","+i+",\"WAIT\");");
      } else {
        Scanner in2 = new Scanner(order).useLocale(Locale.ENGLISH);
        double x = in2.nextDouble();
        double y = in2.nextDouble();
        System.out.println("applyOrder("+agentId+","+i+","+x+","+y+");");
        in2.close();
      }
    }
    in.close();
  }
}
