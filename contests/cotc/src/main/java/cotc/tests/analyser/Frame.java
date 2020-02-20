package cotc.tests.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cotc.GameState;
import cotc.Team;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class Frame {
  private Game game;
  public List<String> inputs = new ArrayList<>();

  public Frame(Game game) {
    this.game = game;
  }
  
  public void readInputsFrom(String[] inputs, int start) {
    for (int i=start;i<inputs.length;i++) {
      this.inputs.add(inputs[i]);
    }
  }
  
  public GameState frameToState() {
    int i=0;
    GameState state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
    
    /** Read the ships */
    /*****************/
    
    i++; // player 0 id
    for (int s=0;s<game.shipCount;s++) {
      String shipLine = inputs.get(i++);
      System.out.println("My ship  " + shipLine);
      Scanner in = new Scanner(shipLine);
      int id = in.nextInt();
      int row = in.nextInt();
      int column = in.nextInt();
      int orientation = in.nextInt();
      int health = in.nextInt();
      int speed = in.nextInt();

      
      Ship ship = new Ship(id, row, column, orientation, 0);
      ship.update(row, column, orientation, speed, health, 0);
      state.updateShip(ship);
      in.close();
    }
    i++; // player 1 id
    for (int s=0;s<game.shipCount;s++) {
      String shipLine = inputs.get(i++);
      System.out.println("His ship " + shipLine);
      Scanner in = new Scanner(shipLine);
      int id = in.nextInt();
      int row = in.nextInt();
      int column = in.nextInt();
      int orientation = in.nextInt();
      int health = in.nextInt();
      int speed = in.nextInt();

      
      Ship ship = new Ship(id, row, column, orientation, 1);
      ship.update(row, column, orientation, speed, health, 1);
      state.updateShip(ship);
      in.close();
    }
    
    // canonballs
    int cannonCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<cannonCount;c++) {
      String cannonString = inputs.get(i++);
//      System.out.println("Cannon : "+cannonString);
      Scanner in = new Scanner(cannonString);

      int id = in.nextInt(); 
      int row = in.nextInt(); 
      int column = in.nextInt();
      int srcRow = in.nextInt();
      int srcCol = in.nextInt();
      int initialRemainingTurns = in.nextInt(); 
      int remainingTurns = in.nextInt();
      int srcBoat = in.nextInt();
      
      CannonBall ball = new CannonBall(id, row, column, null, remainingTurns);
      ball.update(row, column);
      state.cannonballs.add(ball);
      in.close();
    }
    
    // mines
    int mineCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<mineCount;c++) {
      String mineString = inputs.get(i++);
//      System.out.println("Mine : "+mineString);
      Scanner in = new Scanner(mineString);
      
      int id = in.nextInt(); 
      int row = in.nextInt();
      int column = in.nextInt();
      
      Mine mine = new Mine(id, row, column);
      mine.update(row, column);
      state.mines.add(mine);
      in.close();
    }    
    
    // barrels
    int barrelCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<barrelCount;c++) {
      String barrelString = inputs.get(i++);
//      System.out.println("Barrel : "+barrelCount);
      Scanner in = new Scanner(barrelString);
      
      int id = in.nextInt(); 
      int row = in.nextInt(); 
      int column = in.nextInt();
      int health = in.nextInt();
      
      Barrel barrel = new Barrel(id, row, column, health);
      state.barrels.add(barrel);
      in.close();
    }
    
    state.backup();
    return state;
  }
}
