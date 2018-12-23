package offline;

import lcm.Player;

public class TestOneMatch {
  static int wins[] = new int[2];
  static int repartition[][] = new int[2][2];

  
  public static void main(String[] args) {
    Player.DEBUG_INPUT = false;
    Player.DEBUG_CUT_BEAM = false;
    Player.DEBUG_BEAM_RESULT = false;
    Player.DEBUG_BEAM_RESULT = false;
    Player.DEBUG_PERF = false;
    
    int  winner;
    MatchMaker mm;
    Player player0;
    Player player1;

    for (int i=0;i<100_000;i++) {
      if (i % 100 == 0) {
        System.err.println("Intermediaire (game "+i+") :" );
        stats();
      }
      long seed = System.currentTimeMillis();
      //System.err.println("Game "+(i+1)+ " seed : " + seed);
      mm = new MatchMaker(seed);
      
      player0 = new PlayerRef();
      player1 = new Player();

      winner = mm.doMatch(player0, player1);
      wins[winner]++;
      repartition[0][0] += winner == 0 ? 1 : 0;
      repartition[1][1] += winner == 1 ? 1 : 0;
      
      mm = new MatchMaker(seed);
      
      player0 = new PlayerRef();
      player1 = new Player();

      winner = mm.doMatch(player1, player0);
      wins[1-winner]++;
      repartition[1][0] += winner == 0 ? 1 : 0;
      repartition[0][1] += winner == 1 ? 1 : 0;
    }

    System.err.println("FINAL : ");
    stats();
  }

  private static void stats() {
    for (int i=0;i<2;i++) {
      System.err.println("  -------------------");
      System.err.println("  PLAYER " + i + (i == 0 ? "REF" : ""));
      System.err.println("  Won  : "+ wins[i] +" => "+ (100.0*wins[i] / (wins[0]+wins[1]))+" %");
      
      System.err.println("  win in 1st : " + repartition[i][0]);
      System.err.println("  win in 2nd : " + repartition[i][1]);
      System.err.println("  -------------------");
    }
     //MatchMaker.printStats();
  }
}
