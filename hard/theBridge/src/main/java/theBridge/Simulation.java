package theBridge;

import com.sun.java.swing.plaf.windows.resources.windows;

public class Simulation {
  Road road = new Road();
  
  int motoX = 0;
  int motosX[] = new int[4];
  int motosSpeed = 0;
  private boolean isFinished = false;

  public Simulation() {
  }
  
  public void copyFrom(Simulation simulation) {
    this.road = simulation.road;
    for (int i=0;i<motosX.length;i++) {
      motosX[i] = simulation.motosX[i];
    }
    motoX = simulation.motoX;
    motosSpeed = simulation.motosSpeed;
    isFinished = simulation.isFinished;
  }
  
  public void init(String...rows) {
    road.init(rows);
  }
  
  public void simulate(Move move) {
    if (move == Move.Wait) {
      // nothing
    }
    if (move == Move.Speed) {
      motosSpeed++;
    }
    if (move == Move.Slow && canSlow()) {
      motosSpeed--;
    }
    
    if (move == Move.Up && !canMoveUp()) {
      move = Move.Wait;
    }
    if (move == Move.Down && !canMoveDown()) {
      move = Move.Wait;
    }
    
    for (int y=0;y<road.height;y++) {
      int currentPos = motosX[y];
      if (currentPos == -1) {
        continue; // missing moto
      }
      int nextXPos = currentPos+motosSpeed;
      boolean isDead = false;
      if ((move == Move.Wait || move == Move.Slow || move == Move.Speed) 
          && !checkWait(currentPos, nextXPos, y)) {
        isDead = true;
      }
      if (move == Move.Jump && !checkJump(currentPos, nextXPos, y)) {
        isDead = true;
      }
      if (nextXPos < 0 || y < 0 || currentPos < 0) {
        System.err.println("ERREUR : "+currentPos + " "+nextXPos+ " / "+y);
      }

      if (move == Move.Up && !checkUp(currentPos, nextXPos, y)) {
        isDead = true;
      }        
      if (move == Move.Down && !checkDown(currentPos, nextXPos, y)) {
        isDead = true;
      }        
      if (isDead) {
        motosX[y] = -1;
      } else {
        motosX[y] = nextXPos;
      }
    }
    if (move == Move.Up) {
      moveMotosUp();
    } else if (move == Move.Down) {
      moveMotosDown();
    }
    
    motoX+=motosSpeed;
  }

  private void moveMotosDown() {
    for (int i=2;i>=0;i--) {
      motosX[i+1] = motosX[i];
    }
    motosX[0] = -1;
  }

  private void moveMotosUp() {
    for (int i=0;i<4-1;i++) {
      motosX[i] = motosX[i+1];
    }
    motosX[3] = -1;
  }

  public boolean canSlow() {
    return motosSpeed > 0;
  }

  private boolean checkDown(int currentPos, int nextXPos, int y) {
    for (int x=currentPos;x<=nextXPos;x++) {
      if (x >= road.width) {
        return true;
      } else if ((x != nextXPos && road.cells[x][y] == 0)
          || (road.cells[x][y+1] == 0)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkUp(int currentPos, int nextXPos, int y) {
    for (int x=currentPos;x<=nextXPos;x++) {
      if (x >= road.width) {
        return true;
      } else if ((x != nextXPos && road.cells[x][y] == 0)
          || (road.cells[x][y-1] == 0)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkWait(int currentPos, int nextXPos, int y) {
    for (int x=currentPos;x<=nextXPos;x++) {
      if (x >= road.width) {
        return true;
      } else if (road.cells[x][y] == 0) {
        return false;
      }
    }
    return true;
  }

  public boolean checkJump(int currentPos, int nextXPos, int y) {
    if (nextXPos >= road.width) {
      return true;
    } else if (road.cells[nextXPos][y] == 0) {
      return false;
    }
    return true;
  }

  public boolean canMoveDown() {
    return motosX[road.height-1] == -1;
  }
  
  public boolean canMoveUp() {
    return motosX[0] == -1;
  }
  

  public void updateSpeed(int s) {
    motosSpeed = s;
  }

  public void updateMoto(int x, int y, int a) {
    if (a == 1) {
      motoX = x;
      motosX[y] = x;
    }
  }

  public void reinit() {
    for (int y=0;y<motosX.length;y++) {
      motosX[y] = -1;
    }
    motosSpeed = 0;
  }

  public int aliveMoto() {
    int alive = 0;
    for (int y=0;y<motosX.length;y++) {
      alive += motosX[y] == -1 ? 0 : 1;
    }
    return alive;
  }

  public int getMotosSpeed() {
    return motosSpeed;
  }

  public boolean isFinished() {
    return isFinished ;
  }

  public int getMotosX() {
    return motoX;
  }

}
