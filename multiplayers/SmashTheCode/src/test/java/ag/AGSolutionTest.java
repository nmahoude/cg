package ag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import stc.GameTest;
import stc2.BitBoard;
import stc2.BitBoardTest;
import stc2.Game;

public class AGSolutionTest {

  @Test
  public void testRandomKeysDistribution() throws Exception {
    int[] keys = new int[24];
    int count = 100_000_000;
    for (int i=0;i<count;i++) {
      int key = AGSolution.getRandomKey();
      keys[key] +=1;
    }
    
    assertThat(keys[2], is(0));
    assertThat(keys[22], is(0));
    for (int i=0;i<24;i++) {
      if (i==2 || i==22) continue;
      assertThat(Math.abs(keys[i]-count/22) < Math.sqrt(count), is(true));
      System.err.println(""+keys[i]+" delta2Mean : "+Math.abs(keys[i]-count/22));
    }
  }
  
}
