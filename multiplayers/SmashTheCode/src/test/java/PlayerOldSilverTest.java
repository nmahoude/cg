import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PlayerOldSilverTest {
  private static final boolean debug_performance = true;
  PlayerOldSilver player = new PlayerOldSilver();
  private PlayerOldSilver.SimulationWeight weights;

  @Before
  public void setup() {
    player = new PlayerOldSilver();
    
    weights = new PlayerOldSilver.SimulationWeight(
        1,
        -1,
        -1,
        1,
        1
        );
  }

  private void prepareBoard(PlayerOldSilver.Board board, String... rows) {
    int index = 0;
    for (String row : rows) {
      board.updateRow(index++, row);
    }
    board.updateNeighboursCache();
  }

  @Test
  public void updateScore_370() {
    PlayerOldSilver.updateOponentScore(370);
    
    assertThat(PlayerOldSilver.currentThreatSkulls, is(5));
  }
  
  @Test
  public void put2Color_rotation0() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, "......");
    board.updateRow(1, "......");
    board.updateRow(2, "......");
    board.updateRow(3, "......");

    board.placeBlock(new PlayerOldSilver.Block(1, 4), 0, 0);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("14...."));
  }

  @Test
  public void put2Color_rotation1() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, "......");
    board.updateRow(1, "......");
    board.updateRow(2, "......");
    board.updateRow(3, "......");

    board.placeBlock(new PlayerOldSilver.Block(1, 4), 0, 1);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("4....."));
    assertThat(board.row(3), is("1....."));
  }

  @Test
  public void put2Color_rotation2() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, "......");
    board.updateRow(1, "......");
    board.updateRow(2, "......");
    board.updateRow(3, "......");

    board.placeBlock(new PlayerOldSilver.Block(1, 4), 1, 2);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("41...."));
  }

  @Test
  public void put2Color_rotation3() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, "......");
    board.updateRow(1, "......");
    board.updateRow(2, "......");
    board.updateRow(3, "......");

    board.placeBlock(new PlayerOldSilver.Block(1, 4), 0, 3);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("1....."));
    assertThat(board.row(3), is("4....."));
  }

  @Test
  public void getFuturePosWhenEmpty() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, ".43333");
    board.updateRow(1, ".43333");
    board.updateRow(2, ".33333");
    board.updateRow(3, ".33333");

    int row = board.futurePos(0);
    assertThat(row, is(0));
  }

  @Test
  public void getFuturePosWhenNotEmpty() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    board.updateRow(0, ".43333");
    board.updateRow(1, ".43333");
    board.updateRow(2, "133333");
    board.updateRow(3, "133333");

    int row = board.futurePos(0);
    assertThat(row, is(2));
  }

  @Test
  public void countNeighbours_square() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "44....",
        "44....");

    int nCount = board.getCacheNeighbours(0, 0);
    assertThat(nCount, is(4));
  }

  @Test
  public void countNeighbours_randomSquare() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "...44.",
        "...44.",
        "......");

    int nCount = board.getCacheNeighbours(3, 1);
    assertThat(nCount, is(4));

  }

  @Test
  public void countNeighbours_rectangle() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        ".4444.",
        ".4444.",
        "......");

    int nCount = board.getCacheNeighbours(3, 2);
    assertThat(nCount, is(8));
  }

  @Test
  public void countNeighbours_convulated() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        ".4..44",
        ".44.4.",
        "..444.",
        "444.4.");

    int nCount = board.getCacheNeighbours(3, 1);
    assertThat(nCount, is(13));
  }

  @Test
  public void countNeighbours_full() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "444444",
        "444444",
        "444444",
        "444444");

    int nCount = board.getCacheNeighbours(3, 2);
    assertThat(nCount, is(24));
  }

  @Test
  public void destroy_simpleLineH() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "......",
        "4444..");
    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,0)));

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void destroy_simpleLineV() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "4.....",
        "4.....",
        "4.....",
        "4.....");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,0)));

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void destroy_square() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        ".44...",
        ".44...",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(1,1)));

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void destroyFirst_DontDestroyNotPlacedBlocks() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        ".44...",
        ".44...",
        "......");

    board.placedBlockX1 = 0;
    board.placedBlockY1 = 0;
    board.placedBlockX2 = 0;
    board.placedBlockY2 = 1;
    
    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,0)));

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is(".44..."));
    assertThat(board.row(2), is(".44..."));
    assertThat(board.row(3), is("......"));
  }
  
  @Test
  public void destroy_square_withSkulls() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "440...",
        "440...");

    board.killNeighbours(4, 0, 0);
    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void destroy_emptyBoard() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,0)));

    assertThat(board.getPoints(), is(0));
  }

  @Test
  public void destroy_fullBoardWithoutMatch() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "133113",
        "134513",
        "124523",
        "224522");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,0)));

    assertThat(board.getPoints(), is(0));
  }

  @Test
  public void destroy_squareGive40Points() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "..33..",
        "..33..",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(2,2)));

    assertThat(board.getPoints(), is(40));
  }

  @Test
  public void destroy_6x2Give40Points() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "..333.",
        "..333.",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(2,1)));
    assertThat(board.getPoints(), is(120));
  }

  @Test
  public void destroy_BigBlockLimitedToGB8() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "333333",
        "333333",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(0,1)));
    assertThat(board.getPoints(), is(960));
  }

  @Test
  public void destroy_2colorsSimple() throws Exception {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        ".4433.",
        ".4433.",
        "......");

    board.destroyGroups(Arrays.asList(new PlayerOldSilver.P(1,1), new PlayerOldSilver.P(3, 1)));

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void updateBoard_listOfDisplacedBlock() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "33....",
        "......");

    List<PlayerOldSilver.P> toCheck = board.update();

    assertThat(toCheck.contains(new PlayerOldSilver.P(0,0)), is(true));
    assertThat(toCheck.contains(new PlayerOldSilver.P(1,0)), is(true));
  }

  @Test
  public void updateBoard_easy() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "33....",
        "......");

    board.update();

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("33...."));
  }

  @Test
  public void updateBoard_sparse() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "..3...",
        "3...3.",
        "...3..",
        ".3...3");

    board.update();

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("333333"));
  }

  @Test
  public void updateBoard_AllDifferent() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "2.3.3.",
        "351...",
        "...341",
        ".3...3");

    board.update();

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("253.31"));
    assertThat(board.row(3), is("331343"));
  }

  @Test
  public void bestCol_1_Iteration_easy() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "......",
        "3.....",
        "3.....");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { new PlayerOldSilver.Block(3, 3) };
    
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);

    assertThat(s.simulatedResult().board.row(0), is("......"));
    assertThat(s.simulatedResult().board.row(1), is("......"));
    assertThat(s.simulatedResult().board.row(2), is("......"));
    assertThat(s.simulatedResult().board.row(3), is("......"));
  }

  @Test
  public void bestCol_1_Iteration_medium() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 4);
    prepareBoard(board,
        "......",
        "4.....",
        "3.....",
        "3.....");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { new PlayerOldSilver.Block(3, 3) };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);

    assertThat(s.simulatedResult().board.row(0), is("......"));
    assertThat(s.simulatedResult().board.row(1), is("......"));
    assertThat(s.simulatedResult().board.row(2), is("......"));
    assertThat(s.simulatedResult().board.row(3), is("4....."));
  }
  
  @Test
  public void bestCol_mediumCase_2Iter() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 6);
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "..5...",
        "..5...");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { new PlayerOldSilver.Block(5, 5), new PlayerOldSilver.Block(5, 5) };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);

    assertThat(board.row(0), is("......"));
    assertThat(board.row(1), is("......"));
    assertThat(board.row(2), is("......"));
    assertThat(board.row(3), is("......"));
  }

  @Test
  public void testChain() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 6);
    prepareBoard(board,
        "......",
        "......",
        "......",
        ".4....",
        "55....",
        "444...");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { new PlayerOldSilver.Block(5, 5), new PlayerOldSilver.Block(5, 5) };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);

    assertThat(s.simulatedResult().board.row(0), is("......"));
    assertThat(s.simulatedResult().board.row(1), is("......"));
    assertThat(s.simulatedResult().board.row(2), is("......"));
    assertThat(s.simulatedResult().board.row(3), is("......"));
    assertThat(s.simulatedResult().board.row(4), is("......"));
    assertThat(s.simulatedResult().board.row(5), is("......"));
  }
  
  @Test
  public void boardIsFull() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 6);
    prepareBoard(board,
        "123451",
        "234512",
        "123451",
        "234512",
        "123451",
        "234512");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { new PlayerOldSilver.Block(5, 5), new PlayerOldSilver.Block(5, 5) };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);
  }

  
  @Test
  public void case1_iter1() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 12);
    
    PlayerOldSilver.Simulation s = case1(board, 1);
    
    assertThat(s.firstStep().column, is(4));
    assertThat(s.firstStep().rotation, is(2));
  }

  @Test
  public void case1_iter2_bestDecisionIsDifferentThanIter1() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 12);

    PlayerOldSilver.Simulation case1 = case1(board, 2);
    
    assertThat(case1.firstStep().column, is(4));
    assertThat(case1.firstStep().rotation, is(2));
    assertThat(case1.firstStep().nextStep().column, is(1));
    assertThat(case1.firstStep().nextStep().rotation, is(0));
  }

  @Test
  public void case1_iter4_bestDecisionIsDifferentThanIter1() {
    int MAX = 1;
    long time1 = System.currentTimeMillis();
    for (int i=0;i<MAX;i++) {
      PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 12);
  
      PlayerOldSilver.Simulation case1 = case1(board, 3);
      
    }
    long time2 = System.currentTimeMillis();
    assertThat(time2-time1, lessThan(100L));
  }

  private PlayerOldSilver.Simulation case1(PlayerOldSilver.Board board, int iter) {
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "..1...",
        "3544..",
        "10400.",
        "400000",
        "000000",
        "020110",
        "011242",
        "212521");

    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { 
        new PlayerOldSilver.Block(3,4), 
        new PlayerOldSilver.Block(5,4), 
        new PlayerOldSilver.Block(5,3), 
        new PlayerOldSilver.Block(1,1), 
        new PlayerOldSilver.Block(3,5), 
        new PlayerOldSilver.Block(3,5), 
        new PlayerOldSilver.Block(2,2), 
        new PlayerOldSilver.Block(5,1) 
    };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, iter);
    return s;
  }
  
   /***
   * DEBUG
   */
  @Test
  public void calcOpponentPoints() {
    PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 12);
    prepareBoard(board,
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "......",
        "..2...",
        ".22415",
        "333445");


    PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { 
        new PlayerOldSilver.Block(3,4), 
        new PlayerOldSilver.Block(5,1)
        };
    PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 1);

    assertThat(s.firstStep().totalPoints, is(360));
  }

  
  @Test
  public void whyDontYouCloseTheBlock() {
      PlayerOldSilver.Board board = new PlayerOldSilver.Board(6, 12);
      prepareBoard(board,
          "......",
          "......",
          "......",
          "......",
          "......",
          "......",
          "......",
          "......",
          "...53.",
          "34253.",
          "000500",
          "043044");
      
      PlayerOldSilver.Block[] blocks = new PlayerOldSilver.Block[] { 
          new PlayerOldSilver.Block(4,4), 
          new PlayerOldSilver.Block(5,5), 
          new PlayerOldSilver.Block(2,1), 
          new PlayerOldSilver.Block(5,5), 
          new PlayerOldSilver.Block(3,5), 
          new PlayerOldSilver.Block(2,2), 
          new PlayerOldSilver.Block(5,1) 
      };
      PlayerOldSilver.currentThreatSkulls = 4;
      
      PlayerOldSilver.Simulation s = new PlayerOldSilver.Simulation(weights, board, blocks, 2);
  }

  // @Test
  // public void debug_AOOB1() {
  // Player.Board board = new Player.Board(6, 6);
  // board.updateRow(0, ".3....");
  // board.updateRow(1, "34....");
  // board.updateRow(2, "44....");
  // board.updateRow(3, "33....");
  // board.updateRow(4, "444...");
  // board.updateRow(5, "3322..");
  //
  // Player.Block[] blocks = new Player.Block[] { new Player.Block(3, 3), new
  // Player.Block(5, 5) };
  // int bestCol[] = board.getBestChoice(blocks, 1);
  //
  // assertThat(bestCol[1], is(2));
  // }
  //
  // @Test
  // public void debug_colIs0_isBad() {
  // Player.Board board = new Player.Board(6, 6);
  // board.updateRow(0, "......");
  // board.updateRow(1, "......");
  // board.updateRow(2, "......");
  // board.updateRow(3, "......");
  // board.updateRow(4, "......");
  // board.updateRow(5, "....44");
  //
  // Player.Block[] blocks = new Player.Block[] {
  // new Player.Block(3, 3),
  // new Player.Block(2, 1),
  // new Player.Block(3, 4),
  // new Player.Block(1, 3),
  // };
  // int bestCol[] = board.getBestChoice(blocks, 1);
  //
  // assertThat(bestCol[2], is(2));
  // assertThat(bestCol[1], is(1));
  // }
  //
//  @Test
//  public void debug_reallyBestChoice() {
//    Player.Board board = new Player.Board(6, 6);
//    board.updateRow(0, "......");
//    board.updateRow(1, "......");
//    board.updateRow(2, "......");
//    board.updateRow(3, "4.4...");
//    board.updateRow(4, "4.4...");
//    board.updateRow(5, "221133");
//
//    Player.Block[] blocks = new Player.Block[] {
//        new Player.Block(4, 4),
//        new Player.Block(4, 4),
//        new Player.Block(3, 3),
//        new Player.Block(5, 5),
//    };
//   board.simulate(blocks, 2);
//  
//   assertThat(board.bestColumn, is(1));
//   assertThat(board.bestColumn, is(1));
//   }
  
  // @Test
  // public void debug_performanceTimeOut1() throws Exception {
  // if (!debug_performance) return;
  // Player.Board board = new Player.Board(6, 12);
  // board.updateRow(0, "......");
  // board.updateRow(1, "......");
  // board.updateRow(2, "......");
  // board.updateRow(3, "...35.");
  // board.updateRow(4, "1.445.");
  // board.updateRow(5, "2.154.");
  // board.updateRow(6, "2.151.");
  // board.updateRow(7, "3.544.");
  // board.updateRow(8, "5.534.");
  // board.updateRow(9, "1.1112");
  // board.updateRow(10, "132442");
  // board.updateRow(11, "124251");
  //
  // Player.Block[] blocks = new Player.Block[] {
  // new Player.Block(3, 4),
  // new Player.Block(1, 5),
  // new Player.Block(5, 4),
  // new Player.Block(5, 2),
  // new Player.Block(3, 3),
  // new Player.Block(5, 5),
  // new Player.Block(3, 4),
  // new Player.Block(3, 4),
  // };
  // long millis1 = System.currentTimeMillis();
  // int bestCol[] = board.getBestChoice(blocks, 4);
  // long millis2 = System.currentTimeMillis();
  //
  // //best is 1.6
  // assertThat(millis2-millis1 , lessThan(1500L));
  // }
  //
  // @Test
  // public void debug_performanceTimeOut2() throws Exception {
  // if (!debug_performance) return;
  // Player.Board board = new Player.Board(6, 12);
  // board.updateRow(0, "......");
  // board.updateRow(1, "......");
  // board.updateRow(2, "......");
  // board.updateRow(3, "......");
  // board.updateRow(4, "......");
  // board.updateRow(5, "......");
  // board.updateRow(6, "......");
  // board.updateRow(7, "......");
  // board.updateRow(8, "......");
  // board.updateRow(9, "......");
  // board.updateRow(10, "......");
  // board.updateRow(11, "......");
  //
  // Player.Block[] blocks = new Player.Block[] {
  // new Player.Block(4,3),
  // new Player.Block(2,2),
  // new Player.Block(3,3),
  // new Player.Block(5,3),
  // new Player.Block(4,1),
  // new Player.Block(2,5),
  // new Player.Block(1,3),
  // };
  // board.putBlock(0, new Player.Block(4,5), 3);
  //
  // long millis1 = System.currentTimeMillis();
  // int bestCol[] = board.getBestChoice(blocks, 4);
  // long millis2 = System.currentTimeMillis();
  //
  // //best is 1.6
  // assertThat(millis2-millis1 , lessThan(100L));
  // }

}
