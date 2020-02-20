package coderoyale.pathfinding;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import coderoyale.Player;
import coderoyale.Pos;
import coderoyale.sites.Site;

public class PathFindingTest {

  
  @Before
  public void setup() {
    Player.allSites.clear();
  }
  
  @Test
  public void noCollision() throws Exception {
    Route route = PathFinding.getPath(new Pos(0,0), new Pos(1920, 1000));
    
    Segment firstSegment = route.segmentsSoFar.get(0);
    assertThat(firstSegment.from.x, is(0.0));
    assertThat(firstSegment.from.y, is(0.0));
    assertThat(firstSegment.to.x, is(1920.0));
    assertThat(firstSegment.to.y, is(1000.0));
  }
  
  @Test
  @Ignore
  public void oneCollisionOnCenter() throws Exception {
    Player.allSites.add(new Site(0, 960, 500, 100));
    
    Route route = PathFinding.getPath(new Pos(0,0), new Pos(1920, 1000));
    
    Segment firstSegment = route.segmentsSoFar.get(0);
    assertThat(firstSegment.from.x, is(0.0));
    assertThat(firstSegment.from.y, is(0.0));
    assertThat(firstSegment.to.x, is(1920.0));
    assertThat(firstSegment.to.y, is(1000.0));
  }
}
