package c4l.molecule;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

public class MoleculeOptimizerNodeTest {

  MoleculeOptimizerNode root;
  int index = 0;
  Robot me;
  
  @Before
  public void setup() {
    me = new Robot(0);
    me.target = Module.MOLECULES;
    
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
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30, MoleculeType.A);
    
    createSample(1, new int[]{2, 0, 0, 2, 3}, 10, MoleculeType.A);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10, MoleculeType.A);
    
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    
    root.freeStorage = 1;
    
    root.start(0, new int[]{0, 0, 1, 0, 0}, new ArrayList<>(), me);
    
    assertThat(root.score, is(closeTo(30.0, 1.0)));// take into account getPercentage
  }

  @Test
  public void depth0_chooseBest_sameCombination() throws Exception {
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30, MoleculeType.A);
    createSample(1, new int[]{0, 0, 1, 0, 0}, 40, MoleculeType.A);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10, MoleculeType.A);
    
    createStorage(new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    
    root.freeStorage = 1;
    
    root.start(0, new int[]{0, 0, 1, 0, 0}, new ArrayList<>(), me);
    
    assertThat(root.score, is(closeTo(40.0, 1.0)));// take into account getPercentage
  }
  
  @Test
  public void depth0_chooseBest_differentCombination() throws Exception {
    createSample(0, new int[]{0, 0, 1, 0, 0}, 30, MoleculeType.A);
    createSample(1, new int[]{0, 0, 0, 1, 0}, 40, MoleculeType.A);
    createSample(2, new int[]{0, 3, 0, 2, 3}, 10, MoleculeType.A);
    
    createStorage(new int[]{9, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    
    
    root.start(0, new int[]{0, 0, 1, 1, 0}, new ArrayList<>(), me);
    
    assertThat(root.score, is(closeTo(40.0, 1.0))); // take into account getPercentage
  }

  @Test
  public void searchAllMolecules() throws Exception {
    createSample(0, new int[]{0,0,5,0,0},20, MoleculeType.A);
    createSample(4, new int[]{0,0,0,0,5},20, MoleculeType.A);
    createSample(2, new int[]{0,5,3,0,0},20, MoleculeType.A);
    createStorage(  new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    root.freeStorage = 6;
    
    root.start(0, new int[]{6, 6, 6, 6, 6}, new ArrayList<>(), me);
    
    assertThat(root.children.size(), is(5));
  }
  
  @Test
  public void getTheA() throws Exception {
    createSample(0, new int[]{0, 2, 0, 0, 2},1, MoleculeType.A);
    createSample(2, new int[]{4, 0, 0, 0, 0},10, MoleculeType.A);
    createStorage(  new int[]{0, 0, 0, 0, 3});
    createExpertise(new int[]{0, 0, 0, 0, 1});
    
    root.start(0, new int[]{0, 2, 6, 6, 3}, new ArrayList<>(), me);
    
    assertThat(root.combo.getNeededMolecules(), hasItems(MoleculeType.B));
  }
  
  @Test
  public void noMoleculeNeeded_for40points() throws Exception {
    createSample(7, new int[]{6, 0, 0, 0, 0},30, MoleculeType.A);
    createSample(16, new int[]{2, 3, 0, 0, 2},10, MoleculeType.A);
    createSample(17, new int[]{3, 2, 2, 0, 0},10, MoleculeType.A);
    createStorage(  new int[]{4, 0, 0, 2, 1});
    createExpertise(new int[]{2, 0, 0, 2, 1});
    
    root.start(0, new int[]{0, 3, 4, 3, 3}, new ArrayList<>(), me);
    
    assertThat(root.score, closeTo(40.0, 1.0)); // take percentage complete into account
  }

  
  @Test
  public void moleculeNeeded() throws Exception {
    createSample(19, new int[]{0, 7, 3, 0, 0},50, MoleculeType.A);
    createStorage(  new int[]{3, 5, 0, 2, 0});
    createExpertise(new int[]{1, 1, 1, 1, 1});
    
    root.start(0, new int[]{3, 1, 6, 4, 6}, new ArrayList<>(), me);
    
    boolean needToGetMolecule = root.getBestChild().infos.isEmpty();
    for (MoleculeInfo info : root.getBestChild().infos) {
      if (info.getNeededMolecules().size()> 0) {
        needToGetMolecule = true;
        break;
      }
    }
    assertThat(needToGetMolecule, is (true));
  }
  @Test
  public void comboIsNotNull() throws Exception {
    createSample(0, new int[]{6, 0, 0, 0, 0},30, MoleculeType.A);
    createSample(2, new int[]{0, 0, 3, 2, 2},10, MoleculeType.A);
    createSample(4, new int[]{1, 4, 2, 0, 0},20, MoleculeType.A);
    createStorage(  new int[]{6, 1, 3, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});

    root.start(0, new int[]{0, 0, 0, 6, 6}, new ArrayList<>(), me);
    
    assertThat(root.getBestChild(), is(not(nullValue())));
    assertThat(root.getBestChild().infos, is(not(nullValue())));
  }
  
  @Test
  public void dontAskFor_D() throws Exception {
    createSample(11, new int[]{4, 2, 0, 0, 1},20, MoleculeType.A);
    createSample(0, new int[]{2, 0, 0, 2, 3},10, MoleculeType.A);
    createSample(3, new int[]{0, 2, 2, 3, 0},10, MoleculeType.A);
    createStorage(  new int[]{1, 0, 0, 5, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    
    root.start(0,new int[]{5, 6, 6, 0, 6}, new ArrayList<>(), me);

    assertThat(root.combo.infos.get(0).getNeededMolecules().indexOf(MoleculeType.D), is(-1));
  }

  @Test
  public void DontAskFor_A() throws Exception {
    createSample(1, new int[]{0, 2, 3, 0, 3},20, MoleculeType.A);
    createSample(3, new int[]{0, 3, 0, 2, 3},10, MoleculeType.A);
    createStorage(  new int[]{5, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 1});

    root.start(0,new int[]{1, 6, 6, 6, 6}, new ArrayList<>(), me);

    assertThat(root.combo.canFinishAtLeastOneSample(), is(false));
  }
  
  @Test
  public void chooseBestCombination() throws Exception {
    createSample(0, new int[]{0, 0, 3, 2, 2},10, MoleculeType.A);
    createSample(2, new int[]{0, 0, 5, 0, 0},20, MoleculeType.A);
    createSample(4, new int[]{0, 0, 0, 5, 3},20, MoleculeType.A);
    createStorage(  new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});
    
    root.start(0, new int[]{5, 5, 5, 5, 5}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (1));
    assertThat(combo.infos.get(0).getNeededMolecules().size(), is(1));
    assertThat(combo.infos.get(0).getNeededMolecules().indexOf(MoleculeType.C), is(not(-1)));
  }
  
  @Test
  public void shouldPick_for_2() throws Exception {
    createSample(8, new int[] {1, 1, 1, 1, 0},1, MoleculeType.A);
    createSample(10, new int[]{1, 0, 2, 2, 0},1, MoleculeType.A);
    createStorage(  new int[] {1, 1, 3, 3, 0});
    createExpertise(new int[] {3, 0, 1, 0, 0});
    
    root.freeStorage = 0;
    root.start(0, new int[] {3, 2, 2, 0, 4}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (2));
  }

  @Test
  public void shouldPick_for_2_but_no_more_time() throws Exception {
    createSample(8, new int[] {1, 1, 1, 1, 0},1, MoleculeType.A);
    createSample(10, new int[]{1, 0, 2, 2, 0},1, MoleculeType.A);
    createStorage(  new int[] {1, 1, 3, 3, 0});
    createExpertise(new int[] {3, 0, 1, 0, 0});
    
    root.start(199, new int[] {3, 2, 2, 0, 4}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (0));
  }

  @Test
  public void shouldPick_for_2_just_enough_time() throws Exception {
    createSample(8, new int[] {1, 1, 1, 1, 0},1, MoleculeType.A);
    createSample(10, new int[]{1, 0, 2, 2, 0},1, MoleculeType.B);
    createStorage(  new int[] {1, 1, 3, 3, 0});
    createExpertise(new int[] {3, 0, 1, 0, 0});
    
    root.start(195, new int[] {3, 2, 2, 0, 4}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (2));
  }

  
  @Test
  public void cant_finish_sample_because_bag_is_full() throws Exception {
    createSample(25, new int[]{0, 5, 0, 0, 0},20,MoleculeType.B);
    createSample(18, new int[]{0, 0, 0, 0, 6},30,MoleculeType.E);
    createSample(15, new int[]{0, 0, 0, 0, 5},20,MoleculeType.E);
    createStorage(  new int[]{3, 4, 1, 0, 2});
    createExpertise(new int[]{2, 0, 3, 5, 2});

    
    root.start(0, new int[]{2, 1, 4, 2, 2}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (0));
  }

  @Test
  public void doScience() throws Exception {
    createSample(16, new int[]{0, 7, 0, 0, 0},20,MoleculeType.B);
    createStorage(  new int[]{0, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 0});

    ScienceProject sp= new ScienceProject(new int[] { 0, 1, 0, 0, 0});

    root.start(0, new int[]{10, 10, 10, 10 ,10}, Arrays.asList(sp), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (1));
    assertThat(combo.infos.get(0).sampleId, is(16));
  }
  
  @Test
  public void preferTheScienceProject() throws Exception {
    createSample(15, new int[]{0, 2, 0, 0, 0},30,MoleculeType.E);
    createSample(16, new int[]{0, 2, 0, 0, 0},20,MoleculeType.B);
    createSample(18, new int[]{0, 2, 0, 0, 0},30,MoleculeType.E);
    createStorage(  new int[]{2, 2, 2, 2, 2});
    createExpertise(new int[]{0, 0, 0, 0, 0});

    ScienceProject sp= new ScienceProject(new int[] { 0, 1, 0, 0, 0});

    root.start(0, new int[]{0, 1, 0, 0 , 0}, Arrays.asList(sp), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (1));
    assertThat(combo.infos.get(0).sampleId, is(16));
  }

  @Test
  public void preferTheScienceProject_with_2_samples() throws Exception {
    createSample(15, new int[]{0, 2, 0, 0, 0},30,MoleculeType.E);
    createSample(16, new int[]{0, 2, 0, 0, 0},20,MoleculeType.B);
    createStorage(  new int[]{2, 2, 2, 2, 2});
    createExpertise(new int[]{0, 0, 0, 0, 0});

    ScienceProject sp= new ScienceProject(new int[] { 0, 1, 0, 0, 0});

    root.start(0, new int[]{0, 1, 0, 0 , 0}, Arrays.asList(sp), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (1));
    assertThat(combo.infos.get(0).sampleId, is(16));
  }

  @Test
  public void doNotBeleiveTheOneYouGot() throws Exception {
      createSample(29, new int[]{0, 0, 7, 0, 0},40,MoleculeType.D);
      createSample(30, new int[]{0, 7, 3, 0, 0},50,MoleculeType.C);
      createSample(31, new int[]{5, 3, 0, 3, 3},30,MoleculeType.C);
      createStorage(  new int[]{5, 3, 0, 1, 1});
      createExpertise(new int[]{2, 4, 2, 4, 2});

      root.start(0, new int[]{0, 0, 0, 0, 0}, new ArrayList<>(), me);

      MoleculeComboInfo combo = root.getBestChild();
      assertThat(combo.infos.size(), is (2));
      assertThat(combo.infos.get(0).sampleId, is(31));
      assertThat(combo.infos.get(1).sampleId, is(30));
  }
  
  
  @Test
  public void xp_given_by_first_allows_second_sample() throws Exception {
    createSample(8, new int[] {2, 0, 0, 0 ,0},1, MoleculeType.B);
    createSample(10, new int[]{0, 1, 0, 0, 0},1, MoleculeType.A);
    createStorage(  new int[] {2, 0, 0, 0, 0});
    createExpertise(new int[] {0, 0, 0, 0, 0});
    
    root.start(0, new int[] {0, 0, 0, 0, 0}, new ArrayList<>(), me);

    MoleculeComboInfo combo = root.getBestChild();
    assertThat(combo.infos.size(), is (2));
  }
  
  @Test
  public void cantFindAWaybecauseBagOfMoleculeFull() throws Exception {
      createSample(10, new int[]{0, 0, 1, 3, 1},1,MoleculeType.E);
      createSample(15, new int[]{1, 0, 2, 2, 0},1,MoleculeType.B);
      createSample(16, new int[]{0, 2, 0, 2, 0},1,MoleculeType.C);
      createStorage(  new int[]{4, 1, 3, 1, 1});
      createExpertise(new int[]{3, 1, 0, 0, 1});

      root.start(0, new int[]{1, 3, 2, 1, 4}, new ArrayList<>(), me);
      MoleculeComboInfo combo = root.getBestChild();

      assertThat(combo.infos.size(), is (0));
  }

  @Test
  public void percentCompletionWhenAllComplete() throws Exception {
    createSample(2, new int[]{0,1,0,0,0},30, MoleculeType.A);
    createSample(0, new int[]{0,0,1,0,0},20, MoleculeType.A);
    createSample(4, new int[]{0,0,0,1,0},20, MoleculeType.A);
    
    createStorage(  new int[]{0, 1, 0, 1, 0});
    createExpertise(new int[]{0, 0, 1, 0, 0});
    
    assertThat(root.getPercentCompletion(), is(1.0));
  }
  
  @Test
  @Ignore
  public void percentCompletionWhenNoneComplete() throws Exception {
    createSample(2, new int[]{0,2,0,0,0},30, MoleculeType.A);
    createSample(0, new int[]{0,0,2,0,0},20, MoleculeType.A);
    createSample(4, new int[]{0,0,0,2,0},20, MoleculeType.A);
    
    createStorage(  new int[]{4, 0, 0, 0, 0});
    createExpertise(new int[]{0, 0, 0, 0, 2});
    
    assertThat(root.getPercentCompletion(), is(0.0));
  }
  
  private void createStorage(int[] storage) {
    me.storage = storage;
  }
  
  private void createExpertise(int[] xp) {
    me.expertise = xp;
  }

  private void createSample(int id, int[] costs, int health, MoleculeType a) {
    me.carriedSamples.add(new Sample(id, costs, health, a));
  }
  
}
