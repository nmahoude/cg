package util;

import java.util.List;

import org.junit.Test;

public class PCacheTest {

  @Test
  public void useCache() throws Exception {
    List<P> positions = PCache.getListOfMove(P.get(1,4), P.get(7,3));
    System.err.println(positions);
  }
}
