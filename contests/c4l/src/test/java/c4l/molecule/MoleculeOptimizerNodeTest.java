package c4l.molecule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MoleculeOptimizerNodeTest {

  MoleculeOptimizerNode root;
  int index = 0;
  
  @Before
  public void setup() {
    root = new MoleculeOptimizerNode();
    for (int i=0;i<root.values.length;i++) {
      root.values[i] = 99;
    }
    index = 0;
  }

  @Test
  public void createStorage_empty() throws Exception {
    root.createStorage(new int[] { 0,0,0,0,0});
    assertThat(root.freeStorage, is (10));
  }
  
  @Test
  public void createStorage_full() throws Exception {
    root.createStorage(new int[] { 3,0, 4, 0, 3});
    assertThat(root.freeStorage, is (0));
  }
  
  
  @Test
  public void depth0_easyChoice() throws Exception {
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30);
    
    createSample(1, new int[]{2, 0, 0, 2, 3}, 10);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10);
    
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    createAvailable(new int[]{0, 0, 1, 0, 0});
    
    root.freeStorage = 1;
    
    root.start();
    
    assertThat(root.score, is(30.0));
  }

  @Test
  public void depth0_chooseBest_sameCombination() throws Exception {
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30);
    createSample(1, new int[]{0, 0, 1, 0, 0}, 40);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10);
    
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    createAvailable(new int[]{0, 0, 1, 0, 0});
    
    root.freeStorage = 1;
    
    root.start();
    
    assertThat(root.score, is(40.0));
  }
  
  @Test
  public void depth0_chooseBest_differentCombination() throws Exception {
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30);
    createSample(1, new int[]{0, 0, 0, 1, 0}, 40);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10);
    
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    createAvailable(new int[]{0, 0, 1, 1, 0});
    
    root.freeStorage = 1;
    
    root.start();
    
    assertThat(root.score, is(40.0));
  }

  @Test
  public void dontSearchNotNeededMolecules() throws Exception {
    createSample(0, new int[]{0,0,5,0,0},20);
    createSample(4, new int[]{0,0,0,0,5},20);
    createSample(2, new int[]{0,5,3,0,0},20);
    createStorage(  new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    createAvailable(new int[]{6, 6, 6, 6, 6});
    root.freeStorage = 6;
    root.start();
    
    assertThat(root.children.size(), is(3));
  }
  @Test
  @Ignore
  public void TestDePerf() throws Exception {
    createSample(2, new int[]{0,6,0,0,0},30);
    createSample(0, new int[]{0,0,0,5,0},20);
    createSample(4, new int[]{1,4,2,0,0},20);
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    createAvailable(new int[]{6, 6, 6, 6, 6});
    root.freeStorage = 10;
    root.start();
    
    assertThat(root.score, is(not(0.0)));
  }
  
  private void createStorage(int[] storage) {
    root.createStorage(storage);
  }
  
  private void createExpertise(int[] xp) {
    root.createExpertise(xp);
  }

  private void createAvailable(int[] available) {
    root.createAvailable(available);
  }

  private void createSample(int id, int[] costs, int health) {
    root.createSample(index, costs, health);
    index++;
  }
  
}
