package stc2;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Ignore;
import org.junit.Test;

public class SimulationPerformanceTest {
  @Test
  //@Ignore
  public void OneMillion() throws Exception {
    BitBoard boardModel = new BitBoard();
    BitBoardTest.prepareBoard(boardModel,
        "0.2.45",
        ".1.34.",
        "0.2.4.",
        "012345",
        ".1234.",
        "..2345",
        "0123..",
        "012345",
        ".123..",
        "012.45",
        "5.3.10",
        "......");

    BitBoard board = new BitBoard();
    Simulation sim = new Simulation();
    
    long time1 = System.currentTimeMillis();
    for (int i=0;i<1_500_000;i++) {
      board.copyFrom(boardModel);
      sim.board = board;
      sim.putBalls(
          ThreadLocalRandom.current().nextInt(5)+1,
          ThreadLocalRandom.current().nextInt(5)+1,
          ThreadLocalRandom.current().nextInt(4),
          ThreadLocalRandom.current().nextInt(6)
          );
    }
    long time2 = System.currentTimeMillis();

    System.out.println(""+(time2-time1));

  }

}
