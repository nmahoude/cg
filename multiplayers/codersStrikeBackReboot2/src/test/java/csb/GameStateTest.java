package csb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Test;

import csb.entities.Pod;

public class GameStateTest {

  @Test
  public void lapLength() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    
    assertThat(state.lapLength, is(40.0));
    assertThat(state.cpLengths[0], is(10.0));
    assertThat(state.cpLengths[1], is(10.0));
    assertThat(state.cpLengths[2], is(10.0));
    assertThat(state.cpLengths[3], is(10.0));
  }
  
  @Test
  public void podDistanceToFinishLine_start() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    Pod pod = new Pod(0, null);
    pod.nextCheckPointId = 1;
    double dist = state.distToFinishLine(pod);
    
    assertThat(dist, is(40.0*3));
  }
  
  @Test
  public void podDistanceToFinishLine_lap1() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    Pod pod = new Pod(0, null);
    pod.lap = 1;
    
    double dist = state.distToFinishLine(pod);
    
    assertThat(dist, is(40.0*2));
  }
  
  @Test
  public void podDistanceToFinishLine_lastLap() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    Pod pod = new Pod(0, null);
    pod.lap = 2;
    
    double dist = state.distToFinishLine(pod);
    
    assertThat(dist, is(40.0));
  }
  
  @Test
  public void podDistanceToFinishLine_lastCheckpoint() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    Pod pod = new Pod(0, null);
    pod.lap = 2;
    pod.nextCheckPointId = 0;
    pod.x = 0.0;
    pod.y = 10.0;
    double dist = state.distToFinishLine(pod);
    
    assertThat(dist, is(10.0));
  }

  @Test
  public void podDistanceToFinishLine_almostFinished() throws Exception {
    GameState state = new GameState();
    state.readCheckpoints(new Scanner("4 0 0    10 0   10  10    0 10 "));
    Pod pod = new Pod(0, null);
    pod.lap = 2;
    pod.nextCheckPointId = 0;
    pod.x = 0.0;
    pod.y = 2.0;
    double dist = state.distToFinishLine(pod);
    
    assertThat(dist, is(2.0));
  }
}
