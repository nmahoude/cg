package c4l.fsm;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import c4l.GameState;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

public class FSMDiagnosisTest {

  public static class findSamplesFillingAScienceProject {
    FSM fsm;
    FSMDiagnosis diag ;
    private GameState state;
    private Robot me;
    static int[] empty = new int[] {0, 0, 0, 0, 0};
    
    @Before
    public void setup() {
      state = new GameState();
      me = state.robots[0];

      fsm = new FSM(state, me);
      diag = new FSMDiagnosis(fsm);
    }
    
    @Test
    public void noProjectFilled() throws Exception {
      ScienceProject sp = new ScienceProject();
      sp.expertiseNeeded = new int[] {0, 0, 0, 0, 1};
      state.scienceProjects.add(sp);
      
      me.expertise = new int[] {0, 0, 0, 0 ,0};
      Sample s1 = new Sample(0, empty, 0 , MoleculeType.A);
      Sample s2 = new Sample(1, empty, 0 , MoleculeType.B);
      Sample s3 = new Sample(2, empty, 0 , MoleculeType.C);
      Sample s4 = new Sample(3, empty, 0 , MoleculeType.D);
      
      List<Sample> result = diag.findSamplesFillingAScienceProject(Arrays.asList(s1, s2, s3, s4));
      
      assertThat(result.size(), is(0));
    }
    
    @Test
    public void canFilledOne() throws Exception {
      ScienceProject sp = new ScienceProject();
      sp.expertiseNeeded = new int[] {0, 0, 0, 0, 1};
      state.scienceProjects.add(sp);
      
      me.expertise = new int[] {0, 0, 0, 0 ,0};
      Sample s5 = new Sample(0, empty, 0 , MoleculeType.E);
      
      List<Sample> result = diag.findSamplesFillingAScienceProject(Arrays.asList(s5));
      
      assertThat(result, hasItem(s5));
    }

    @Test
    public void canFilledOneWithExpertise() throws Exception {
      ScienceProject sp = new ScienceProject();
      sp.expertiseNeeded = new int[] {0, 0, 0, 0, 2};
      state.scienceProjects.add(sp);
      
      me.expertise = new int[] {0, 0, 0, 0 ,1};
      Sample s5 = new Sample(0, empty, 0 , MoleculeType.E);
      
      List<Sample> result = diag.findSamplesFillingAScienceProject(Arrays.asList(s5));
      
      assertThat(result, hasItem(s5));
    }

    @Test
    public void cantDoIfAlreadyDone() throws Exception {
      ScienceProject sp = new ScienceProject();
      sp.expertiseNeeded = new int[] {0, 0, 0, 0, 2};
      sp.doneBy = 1;
      state.scienceProjects.add(sp);
      
      me.expertise = new int[] {0, 0, 0, 0 ,1};
      Sample s5 = new Sample(0, empty, 0 , MoleculeType.E);
      
      List<Sample> result = diag.findSamplesFillingAScienceProject(Arrays.asList(s5));
      
      assertThat(result, not(hasItem(s5)));
    }
  }

  public static class chooseSampleToCompleteScienceProject {
    FSM fsm;
    FSMDiagnosis diag ;
    private GameState state;
    private Robot me;
    static int[] empty = new int[] {0, 0, 0, 0, 0};

    @Before
    public void setup() {
      state = new GameState();
      me = state.robots[0];

      fsm = new FSM(state, me);
      diag = new FSMDiagnosis(fsm);
    }

    @Test
    public void _found() throws Exception {
      ScienceProject sp = new ScienceProject();
      sp.expertiseNeeded = new int[] {0, 0, 0, 0, 2};
      state.scienceProjects.add(sp);
      
      me.expertise = new int[] {0, 0, 0, 0 ,1};
      Sample s5 = new Sample(0, empty, 0 , MoleculeType.E);
      
      boolean result = diag.chooseSampleToCompleteScienceProject(Arrays.asList(s5));
      
      assertThat(result, is(true));
    }
  }
}
