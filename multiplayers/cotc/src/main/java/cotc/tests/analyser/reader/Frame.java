package cotc.tests.analyser.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cotc.GameState;
import cotc.Team;
import cotc.entities.Action;
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
    
    /*******************/
    /** Read the ships */
    /*******************/
    i++; // player 0 id
    for (int s=0;s<game.shipCount;s++) {
      String shipLine = inputs.get(i++);
      readShip(state, 0, shipLine);
    }

    i++; // player 1 id
    for (int s=0;s<game.shipCount;s++) {
      String shipLine = inputs.get(i++);
      readShip(state, 1, shipLine);
    }

    int cannonBallCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<cannonBallCount;c++) {
      String cannonString = inputs.get(i++);
      readCannonball(state, cannonString);
    }
    
    int mineCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<mineCount;c++) {
      String mineString = inputs.get(i++);
      readMine(state, mineString);
    }    
    
    // barrels
    int barrelCount = Integer.parseInt(inputs.get(i++));
    for (int c=0;c<barrelCount;c++) {
      String barrelString = inputs.get(i++);
      readBarrel(state, barrelString);
    }
    
    state.backup();
    return state;
  }

  private void readBarrel(GameState state, String barrelString) {
    Scanner in = new Scanner(barrelString);
    
    int id = in.nextInt(); 
    int row = in.nextInt(); 
    int column = in.nextInt();
    int health = in.nextInt();
    in.close();
    
    Barrel barrel = new Barrel(id, row, column, health);
    state.barrels.add(barrel);
  }

  private void readMine(GameState state, String mineString) {
    Scanner in = new Scanner(mineString);
    
    int id = in.nextInt(); 
    int row = in.nextInt();
    int column = in.nextInt();
    in.close();
    
    Mine mine = new Mine(id, row, column);
    mine.update(row, column);
    state.mines.add(mine);
  }

  private void readCannonball(GameState state, String cannonString) {
    Scanner in = new Scanner(cannonString);

    int id = in.nextInt(); 
    int row = in.nextInt(); 
    int column = in.nextInt();
    int srcRow = in.nextInt();
    int srcCol = in.nextInt();
    int initialRemainingTurns = in.nextInt(); 
    int remainingTurns = in.nextInt();
    int srcBoat = in.nextInt();
    in.close();
    
    CannonBall ball = new CannonBall(id, row, column, null, remainingTurns);
    ball.update(row, column);
    state.cannonballs.add(ball);
  }

  private void readShip(GameState state, int owner, String shipLine) {
    Scanner in = new Scanner(shipLine);
    int id = in.nextInt();
    int row = in.nextInt();
    int column = in.nextInt();
    int orientation = in.nextInt();
    int health = in.nextInt();
    int speed = in.nextInt();
    Action action = Action.valueOf(in.next());
    in.close();
    
    if (health <=0 ) return;
    
    Ship ship = new Ship(id, row, column, orientation, owner);
    ship.update(row, column, orientation, speed, health, owner);
    ship.action = action;
    state.updateShip(ship);
  }
}
