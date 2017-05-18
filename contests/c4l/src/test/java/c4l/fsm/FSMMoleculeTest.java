package c4l.fsm;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import c4l.GameState;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;

public class FSMMoleculeTest {

  private GameState state;
  private Robot me;
  private FSM fsm;
  private FSMMolecule mole;

  @Before
  public void setup() {
    state = new GameState();
    me = state.robots[0];
    fsm = new FSM(state, me);
    mole = new FSMMolecule(fsm);
  }
  
  @Test
  public void sparseMolecule() throws Exception {
    state.availables = new int[] { 0, 0, 0, 0, 0};
    
    MoleculeType molecule = mole.getSparserMolecule(new int[] { 0, 0, 0, 0, 0});
    assertThat(molecule, is(nullValue()));
  }
  
  @Test
  public void sparseMolecule_allSame() throws Exception {
    int[] needed    = new int[] { 1, 1, 1, 1, 1};
    state.availables = new int[] { 1, 1, 1, 1, 1};
    
    MoleculeType molecule = mole.getSparserMolecule(needed);
    assertThat(molecule, is(MoleculeType.A));
  }

  @Test
  public void sparseMolecule_lessB() throws Exception {
    int[] needed    = new int[] { 1, 1, 1, 1, 1};
    state.availables = new int[] { 2, 1, 2, 2, 2};
    
    MoleculeType molecule = mole.getSparserMolecule(needed);
    assertThat(molecule, is(MoleculeType.B));
  }
  
  @Test
  public void sparseMolecule_lessAvailableBut0Needed() throws Exception {
    int[] needed    = new int[] { 1, 0, 1, 1, 1};
    state.availables = new int[] { 2, 1, 2, 2, 2};
    
    MoleculeType molecule = mole.getSparserMolecule(needed);
    assertThat(molecule, is(MoleculeType.A));
  }
  
  @Test
  public void sparseMolecule_Same_choose_closerToZero() throws Exception {
    int[] needed    = new int[] { 2, 0, 1, 2, 1};
    state.availables = new int[] { 3, 1, 2, 3, 3};
    
    MoleculeType molecule = mole.getSparserMolecule(needed);
    assertThat(molecule, is(MoleculeType.C));
  }
}
