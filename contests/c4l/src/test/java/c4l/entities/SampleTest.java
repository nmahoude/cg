package c4l.entities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SampleTest {

  @Test
  public void totalMoleculeNeededASC_noExpertise() throws Exception {
    Robot me = new Robot();
    me.storage = new int[] { 0, 1, 2, 3, 4 };
    me.expertise = new int[] {0, 0, 0, 0, 0};
    
    Sample sample = new Sample(new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.neededMoleculesFor(me), is(10));
  }

  @Test
  public void totalMoleculeNeededASC_expertiseOnly() throws Exception {
    Robot me = new Robot();
    me.storage= new int[] {0, 0, 0, 0, 0};
    me.expertise = new int[] { 4, 3, 2, 1 ,0 };
    
    Sample sample = new Sample(new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.neededMoleculesFor(me), is(10));
  }

  @Test
  public void totalMoleculeNeededASC_mix() throws Exception {
    Robot me = new Robot();
    me.storage= new int[] {0, 3, 0, 1, 0};
    me.expertise = new int[] { 4, 0, 2, 0 ,0 };
    
    Sample sample = new Sample(new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.neededMoleculesFor(me), is(10));
  }
  
  @Test
  public void totalMoleculeNeededASC_moreThanNeeded() throws Exception {
    Robot me = new Robot();
    me.storage= new int[] {0, 1, 2, 3, 4 };
    me.expertise = new int[] { 4, 3, 2, 1 ,0 };
    
    Sample sample = new Sample(new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.neededMoleculesFor(me), is(0));
  }

}
