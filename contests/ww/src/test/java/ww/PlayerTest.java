package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
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
