package c4l.sample;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

public class SampleOptimizerTest {
  GameState state;
  Robot me;
  
  @Before
  public void setup() {
    state = new GameState();
    me = state.robots[0];
    me.target = Module.DIAGNOSIS;
  }
  
  @Test
  public void only3Samples() throws Exception {
    SampleOptimizer optimizer = new SampleOptimizer();
    Sample s1 = new Sample(0, new int[] {0, 0, 0, 0, 0}, 10, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] {0, 0, 0, 0, 0}, 10, MoleculeType.A);
    Sample s3 = new Sample(2, new int[] {0, 0, 0, 0, 0}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s1, s2, s3));

    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples.size(), is(3));
  }

  @Test
  public void bagIsFull_soCantTakeSamples() throws Exception {
    SampleOptimizer optimizer = new SampleOptimizer();
    Sample s1 = new Sample(0, new int[] {1, 1, 1, 1, 1}, 10, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] {1, 1, 1, 1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(2, new int[] {1, 0, 0, 0 ,1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s1, s2, s3));
    state.availables = new int[] { 10, 10 ,10, 10, 10};
    me.storage = new int[] {10, 0, 0, 0, 0};
    
    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples.size(), is(0));
  }

  @Test
  public void oneSampleInThePool_noCarried() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(0, new int[] {1, 1, 1,1, 1}, 20, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s1));

    SampleOptimizer optimizer = new SampleOptimizer();
    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(s1));
  }

  @Test
  public void threeSampleInThePool_noCarried() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(0, new int[] {1, 1, 1,1, 1}, 5, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    Sample s3 = new Sample(2, new int[] {1, 1, 1,1, 1}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s1, s2, s3));

    SampleOptimizer optimizer = new SampleOptimizer();
    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(s2));
    assertThat(info.samples, hasItem(s3));
  }

  @Test
  public void fourSampleInThePool_3forScience() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(0, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.B);
    Sample s3 = new Sample(2, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.C);
    Sample s4 = new Sample(3, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.D);
    state.availableSamples.addAll(Arrays.asList(s1, s2, s3, s4));

    state.scienceProjects.add(new ScienceProject(new int[] { 5, 5, 0, 5, 0}));
    SampleOptimizer optimizer = new SampleOptimizer();
    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(s1));
    assertThat(info.samples, hasItem(s2));
    assertThat(info.samples, hasItem(s4));
  }
  
  @Test
  public void fourSampleInThePool_noCarried_choiceToMake() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 0, 0,0, 0}, 10, MoleculeType.A);
    Sample s1 = new Sample(1, new int[] {1, 0, 0,0, 0}, 5, MoleculeType.A);
    Sample s2 = new Sample(2, new int[] {1, 0, 0,0, 0}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 0, 0,0, 0}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));

    SampleOptimizer optimizer = new SampleOptimizer();

    SampleInfo info=optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(s0));
    assertThat(info.samples, hasItem(s2));
    assertThat(info.samples, hasItem(s3));
  }

  @Test
  // all pool sample are far better
  public void fourSampleInThePool_3Carried_choiceToMake() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.A);
    Sample s1 = new Sample(1, new int[] {1, 0, 0, 0 ,0}, 5, MoleculeType.A);
    Sample s2 = new Sample(2, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));

    Sample c0 = new Sample(10, new int[] {1, 0, 0, 0 ,0}, 1, MoleculeType.A);
    Sample c1 = new Sample(11, new int[] {1, 0, 0, 0 ,0}, 1, MoleculeType.A);
    Sample c2 = new Sample(12, new int[] {1, 0, 0, 0, 0}, 1, MoleculeType.A);
    c0.carriedBy = 0; c1.carriedBy = 0; c2.carriedBy = 0;
    me.carriedSamples.addAll(Arrays.asList(c0, c1, c2));
    
    SampleOptimizer optimizer = new SampleOptimizer();
    optimizer.samples.addAll(state.availableSamples);
    optimizer.samples.addAll(me.carriedSamples);

    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(s0));
    assertThat(info.samples, hasItem(s2));
    assertThat(info.samples, hasItem(s3));
  }
  
  @Test
  public void preferetoKeepSamples() throws Exception {
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s0 = new Sample(0, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.A);
    Sample s1 = new Sample(1, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    Sample s2 = new Sample(2, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    Sample s3 = new Sample(3, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    state.availableSamples.addAll(Arrays.asList(s0, s1, s2, s3));

    // give less points, but are already here
    Sample c0 = new Sample(10, new int[] {1, 0, 0, 0 ,0}, 9, MoleculeType.A);
    Sample c1 = new Sample(11, new int[] {1, 0, 0, 0 ,0}, 9, MoleculeType.A);
    Sample c2 = new Sample(12, new int[] {1, 0, 0, 0, 0}, 9, MoleculeType.A);
    c0.carriedBy = 0; c1.carriedBy = 0; c2.carriedBy = 0;
    me.carriedSamples.addAll(Arrays.asList(c0, c1, c2));
    
    SampleOptimizer optimizer = new SampleOptimizer();
    optimizer.samples.addAll(state.availableSamples);
    optimizer.samples.addAll(me.carriedSamples);

    SampleInfo info= optimizer.optimize(state, me);
    
    assertThat(info.samples, hasItem(c0));
    assertThat(info.samples, hasItem(c1));
    assertThat(info.samples, hasItem(c2));
  }
  
  @Test
  // the big sample in the pool should not be taken because it complete the science project
  public void chooseLessDirectPointsForAScienceProject() throws Exception {
    state.scienceProjects.add(new ScienceProject(new int[] { 0, 1, 0, 0, 0})); // science project is B!
    
    state.availables = new int[] {5, 5, 5, 5, 5};
    Sample s1 = new Sample(1, new int[] {1, 0, 0, 0 ,0}, 5, MoleculeType.B);
    state.availableSamples.addAll(Arrays.asList(s1));

    Sample c0 = new Sample(10, new int[] {1, 0, 0, 0, 0}, 10, MoleculeType.A); // all A 
    Sample c1 = new Sample(11, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    Sample c2 = new Sample(12, new int[] {1, 0, 0, 0 ,0}, 10, MoleculeType.A);
    c0.carriedBy = 0; c1.carriedBy = 0; c2.carriedBy = 0;
    me.carriedSamples.addAll(Arrays.asList(c0, c1, c2));
    
    SampleOptimizer optimizer = new SampleOptimizer();

    SampleInfo info= optimizer.optimize(state, me);
    
    // optimiser should say to take s1 (to complete the project)
    assertThat(info.samples, hasItem(s1));
  }
}
