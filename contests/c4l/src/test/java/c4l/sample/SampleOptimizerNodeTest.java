package c4l.sample;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

public class SampleOptimizerNodeTest {

  private GameState state;

  @Before
  public void setup() {
    state = new GameState();
    state.robots[0].target = Module.DIAGNOSIS;
  }
  
  @Test
  @Ignore
  public void noSampleInThePool() throws Exception {
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    assertThat(true, is(true));
  }

  @Test
  @Ignore
  public void oneSampleInThePool_noCarried() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(0, new int[] {1, 1, 1,1, 1}, 20, MoleculeType.A);
    state.availableSamples.add(s1);
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    assertThat(root.getBestChild().currentSamples, hasItem(s1));
  }

  @Test
  @Ignore
  public void threeSampleInThePool_noCarried() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(0, new int[] {1, 1, 1,1, 1}, 5, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(2, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s1, s2, s3));
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    assertThat(root.getBestChild().currentSamples, hasItem(s1));
    assertThat(root.getBestChild().currentSamples, hasItem(s2));
    assertThat(root.getBestChild().currentSamples, hasItem(s3));
  }

  @Test
  @Ignore
  public void fourSampleInThePool_noCarried_choiceToMake() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s1 = new Sample(1, new int[] {1, 1, 1,1, 1}, 5, MoleculeType.A);
    Sample s2 = new Sample(2, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    assertThat(root.getBestChild().currentSamples, hasItem(s0));
    assertThat(root.getBestChild().currentSamples, hasItem(s2));
    assertThat(root.getBestChild().currentSamples, hasItem(s3));
  }

  @Test
  @Ignore
  // all pool sample are far better
  public void fourSampleInThePool_3Carried_choiceToMake() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s1 = new Sample(1, new int[] {1, 1, 1,1, 1}, 5, MoleculeType.A);
    Sample s2 = new Sample(2, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));

    Sample c0 = new Sample(10, new int[] {1, 1, 1,1, 1}, 1, MoleculeType.A);
    Sample c1 = new Sample(11, new int[] {1, 1, 1,1, 1}, 1, MoleculeType.A);
    Sample c2 = new Sample(12, new int[] {1, 1, 1,1, 1}, 1, MoleculeType.A);
    state.robots[0].carriedSamples.addAll(Arrays.asList(c0, c1, c2));
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    assertThat(root.getBestChild().currentSamples, hasItem(s0));
    assertThat(root.getBestChild().currentSamples, hasItem(s2));
    assertThat(root.getBestChild().currentSamples, hasItem(s3));
  }
  
  @Test
  @Ignore
  // the big sample in the pool should not be taken because
  public void oneBigSampleInPool_3Carried_choiceToMake() throws Exception {
    state.scienceProjects.add(new ScienceProject(new int[] { 0, 1, 0, 0, 0})); // science project is B!
    
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A); // all A except s1 which give B
    Sample s1 = new Sample(1, new int[] {1, 1, 1,1, 1}, 5, MoleculeType.B);
    Sample s2 = new Sample(2, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));

    Sample c0 = new Sample(10, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A); // all A 
    Sample c1 = new Sample(11, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample c2 = new Sample(12, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.robots[0].carriedSamples.addAll(Arrays.asList(c0, c1, c2));
    
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, state.robots[0]);
    
    // optimiser should say to take s1 (to complete the project)
    assertThat(root.getBestChild().currentSamples, hasItem(s1));
  }
  
}
