import java.util.Arrays;

public class GeneticPlay {
  private static final int MAX_STEPS = 100;
  public static void main(String[] args) {
    new GeneticPlay().play();
  }
  void play() {
    int[] points = new int[100];
    Player.Board[] sBoards = new Player.Board[100];
    Player.SimulationWeight[] sWeights = new Player.SimulationWeight[100];
    
    for (int i=0;i<100;i++) {
      sBoards[i] = new Player.Board(6,12);
      sWeights[i] = new Player.SimulationWeight(
          Math.random()*2 - 1.0,
          Math.random()*2 - 1.0,
          Math.random()*2 - 1.0,
          Math.random()*2 - 1.0,
          Math.random()*2 - 1.0
          );
      points[i] = 0;
    }
    Player.Block[] blocks = new Player.Block[8];
    init(blocks);
    
    for (int step=0;step < MAX_STEPS;step++) {
      System.out.println("Step: "+step);
      for (int i=0;i<100;i++) {
        if (sBoards[i] != null) {
          try {
            Player.Simulation simulation = new Player.Simulation(sWeights[i], sBoards[i], blocks, 3);
            points[i] += simulation.firstStep().points;
            sBoards[i] = simulation.firstStep().board;
          } catch(Exception e) {
            sBoards[i] = null;
          }
        }
      }
      shift(blocks);
    }

    
    for (int i=0;i<100;i++) {
      int maxIndex = getIndexOfMax(points);
      System.out.println("Simulation "+maxIndex);
      System.out.println("  points : "+points[maxIndex]);
      sWeights[maxIndex].debug();

      points[maxIndex] = -1;
    }
  }

  private static int getIndexOfMax(int[] points) {
    int max = 0;
    int maxIndex = 0;
    
    for (int i=0;i<points.length;i++) {
      if (points[i] > max) {
        maxIndex = i;
        max = points[i];
      }
    }
    return maxIndex;
  }
  private Player.Block createRandomBlock() {
    int color1 = (int)(Math.random() * 6);
    int color2 = (int)(Math.random() * 6);
    return new Player.Block(color1, color2);
  }
  private void init(Player.Block[] blocks) {
    for (int i=0;i<8;i++) {
      blocks[i] = createRandomBlock();
    }
  }
  private void shift(Player.Block[] blocks) {
    for (int i=0;i<8-1;i++) {
      blocks[i] = blocks[i+1];
    }
    blocks[8-1] = createRandomBlock();
  }
}
