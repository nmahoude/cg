package ww;

import java.util.Scanner;

import org.junit.Before;

import ww.sim.Simulation;

public class PlayerTest {
  GameState state ;
  Simulation simulation;
  
  @Before 
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
  
    simulation = new Simulation();
    Player.state = state;
  }
  
}
