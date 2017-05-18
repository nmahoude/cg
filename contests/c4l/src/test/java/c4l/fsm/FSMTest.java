package c4l.fsm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import c4l.entities.Module;
import c4l.entities.Robot;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeOptimizerNode;

public class FSMTest {

  
  public static class GetBestMoleculeTest {
    Robot me;
    
    @Before
    public void setup() {
      me = new Robot();
      me.target = Module.MOLECULES;
    }
  }
}
