package ww.game;

import java.util.Properties;

import ww.GameState;

public class Controller {
  Referee referee = new Referee();
  
  // P1
  GameState state1 = new GameState();
  // P2
  GameState state2 = new GameState();
  
  
  public void doOneMatch() throws Exception{
    referee.initReferee(2, new Properties());
    
  }
}
