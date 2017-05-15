package c4l;

import org.junit.Before;
import org.junit.Test;

import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import minimax.Minimax;

public class PlayerV1Test {

  PlayerV1 player;
  GameState state;
  Robot me;
  
  @Before
  public void setup() {
    player = new PlayerV1();
    state = player.state;
    me = state.robots[0];
  }
  
  @Test
  public void test_minimax() throws Exception {
    state.availables = new int[] { 2, 2, 0, 0, 0};
    me.carriedSamples.add(new Sample(new int[] { 1, 1, 0, 0, 0}, 20, MoleculeType.A));
    me.storage = new int[] { 0, 0, 0, 0, 0};
    me.expertise = new int[] { 0, 0, 0, 0, 0};
    
    player.doMinimax();
  }
}
