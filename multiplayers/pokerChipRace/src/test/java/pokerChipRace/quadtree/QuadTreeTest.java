package pokerChipRace.quadtree;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pokerChipRace.entities.Entity;

public class QuadTreeTest {

  QuadTree qt;
  
  @Before
  public void setup() {
    qt = new QuadTree(0, 0, 100, 100);
  }
  
  @Test
  public void insert_too_big() throws Exception {
    Entity e = new Entity(0,0);
    e.update(0, -10, -10, 20, 200, 200);
    qt.insert(e);

    QuadTree result = qt.search(e);
    
    assertThat(result, is(qt));
  }

  @Test
  public void insert_small() throws Exception {
    Entity e = new Entity(0,0);
    e.update(0, 0, 0, 1, 1, 1);
    qt.insert(e);

    QuadTree result = qt.search(e);
    
    assertThat(result.parent().parent().parent(), is(qt));
  }


}
