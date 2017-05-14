package c4l.fsm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import c4l.molecule.MoleculeOptimizerNode;

public class FSMTest {

  
  public static class GetBestMolecule {
    @Test
    public void cant_do_any_combination() throws Exception {
      MoleculeOptimizerNode root = new MoleculeOptimizerNode();
      
      root.createSample(0, new int[]{0, 1, 1, 1, 1},1);
      root.createSample(1, new int[]{3, 0, 3, 0, 2},10);
      root.createSample(2, new int[]{0, 0, 0, 3, 0},1);
      root.createStorage(  new int[]{0, 0, 5, 0, 5});
      root.createExpertise(new int[]{0, 0, 0, 0, 1});
      root.createAvailable(new int[]{0, 2, 1, 6, 1});
      
      root.start();
      
      assertThat(root.score, is(0.0));
    }
  }
}
