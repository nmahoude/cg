package c4l.entities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import c4l.GameState;
import c4l.Order;

public class SampleTest {

  private Robot me;

  @Before
  public void setup() {
    me = new Robot(0);
  }

  @Test
  public void relativeCost_noExpertise() throws Exception {
    me.storage = new int[] { 0, 1, 2, 3, 4 };
    me.expertise = new int[] {0, 0, 0, 0, 0};
    
    Sample sample = new Sample(0, new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.relativeCost(me), is(10));
  }

  @Test
  public void relativeCost_expertiseOnly() throws Exception {
    me.storage= new int[] {0, 0, 0, 0, 0};
    me.expertise = new int[] { 4, 3, 2, 1 ,0 };
    
    Sample sample = new Sample(0, new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.relativeCost(me), is(10));
  }

  @Test
  public void relativeCost_mix() throws Exception {
    me.storage= new int[] {0, 3, 0, 1, 0};
    me.expertise = new int[] { 4, 0, 2, 0 ,0 };
    
    Sample sample = new Sample(0, new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.relativeCost(me), is(10));
  }
  
  @Test
  public void relativeCost_moreThanNeeded() throws Exception {
    me.storage= new int[] {0, 1, 2, 3, 4 };
    me.expertise = new int[] { 4, 3, 2, 1 ,0 };
    
    Sample sample = new Sample(0, new int[] { 4, 4, 4, 4, 4}, 0, MoleculeType.A);

    assertThat(sample.relativeCost(me), is(0));
  }

  @Test
  public void orderByPoints() throws Exception {
    GameState state = new GameState();
    Sample s1 = new Sample(0, new int[] { 4, 4, 4, 4, 4}, 1, MoleculeType.A);
    Sample s2 = new Sample(1, new int[] { 4, 4, 4, 4, 4}, 20, MoleculeType.A);
    Sample s3 = new Sample(2, new int[] { 4, 4, 4, 4, 4}, 10, MoleculeType.A);
    List<Sample> list = new ArrayList<>();
    list.add(s1);
    list.add(s2);
    list.add(s3);
    
    list.sort(Sample.pointsWonSorter(state, state.robots[0], Order.DESC));
    
    assertThat(list.get(0), is(s2));
    assertThat(list.get(1), is(s3));
    assertThat(list.get(2), is(s1));
  }
}
