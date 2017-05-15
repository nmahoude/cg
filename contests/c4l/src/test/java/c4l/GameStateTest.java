package c4l;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import c4l.entities.ScienceProject;

public class GameStateTest {
  private static final int[] empty = new int[] {0, 0, 0, 0, 0};
  GameState state;
  
  @Before
  public void setup() {
    state = new GameState();
  }
  
  @Test
  public void distanceToScienceProjects() throws Exception {
    ScienceProject sp = new ScienceProject();
    sp.expertiseNeeded = new int[] { 1, 1, 1, 1, 1};
    state.scienceProjects.add(sp);
    state.robots[0].expertise = empty;
    
    assertThat(state.distanceToScienceProjects(state.robots[0], empty), is(5));
  }

  @Test
  public void distanceToScienceProjects_withExpertise() throws Exception {
    ScienceProject sp = new ScienceProject();
    sp.expertiseNeeded = new int[] { 2, 2, 2, 2, 2};
    state.scienceProjects.add(sp);
    state.robots[0].expertise = new int[] {1, 1, 1, 1, 1};
    
    assertThat(state.distanceToScienceProjects(state.robots[0], empty), is(5));
  }

  @Test
  public void distanceToScienceProjects_withGain() throws Exception {
    ScienceProject sp = new ScienceProject();
    sp.expertiseNeeded = new int[] { 2, 2, 2, 2, 2};
    state.scienceProjects.add(sp);
    state.robots[0].expertise = new int[] {1, 1, 1, 1, 1};
    
    assertThat(state.distanceToScienceProjects(state.robots[0], new int[] {0, 0, 0, 0 ,1}), is(4));
  }
  
  @Test
  public void distanceToScienceProjects_exactXP() throws Exception {
    ScienceProject sp = new ScienceProject();
    sp.expertiseNeeded = new int[] { 1, 1, 1, 1, 1};
    state.scienceProjects.add(sp);
    state.robots[0].expertise = new int[] {1, 1, 1, 1, 1};
    
    assertThat(state.distanceToScienceProjects(state.robots[0], empty), is(0));
  }

  @Test
  public void distanceToScienceProjects_moreXP() throws Exception {
    ScienceProject sp = new ScienceProject();
    sp.expertiseNeeded = new int[] { 1, 1, 1, 1, 1};
    state.scienceProjects.add(sp);
    state.robots[0].expertise = new int[] {2, 1, 1, 1, 1};
    
    assertThat(state.distanceToScienceProjects(state.robots[0], empty), is(0));
  }
}
