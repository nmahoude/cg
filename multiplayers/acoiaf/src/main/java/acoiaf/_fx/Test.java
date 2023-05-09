package acoiaf._fx;

import java.io.FileNotFoundException;

import cgfx.frames.GameReader;

public class Test {

  
  public static void main(String[] args) throws FileNotFoundException {
    
    GameReader reader = new GameReader();
    reader.readReplayFromFile("game.json");
    
    System.out.println("There is "+reader.getMaxTurn()+" frames");
    
    
  }
}
