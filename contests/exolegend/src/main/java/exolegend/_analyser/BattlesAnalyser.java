package exolegend._analyser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import exolegend._analyser.BattlesReader.MatchInfo;
import exolegend._analyser.BattlesReader.Player;

public class BattlesAnalyser {
  private static final String REMEMBER_ME = System.getProperty("REMEMBER_ME");
  private static final String MY_USER_ID = System.getProperty("MY_USER_ID");


  private static final Player me = new Player("nmahoude", "whatever");
  
  
  private static final Path base = Paths.get(new File("target/battleAnalyser").getAbsolutePath());
  
  static class ResultStat {
    Player player;
    int wins;
    int loses;
    public int draws;
    
    public int total() {
      return wins+loses+draws;
    }
  }
  
  public static void main(String[] args) throws IOException {
    System.out.println("EXO2024 - Analysis of battles !");
    System.out.println();

    List<BattlesReader.MatchInfo> matchInfos = new BattlesReader(base, REMEMBER_ME, MY_USER_ID).read();
    
    
    List<Integer> pointsWhenILose = matchInfos.stream()
            .filter(BattlesAnalyser::whenILose)
            .map(mi -> mi.fullGame().scores().get(1))
            .toList();
    
    Stat stat = new Stat("Points when I lose", pointsWhenILose);
    stat.display();
    
    
    Stat stat2 = new Stat("Points when I won", matchInfos.stream()
        .filter(BattlesAnalyser::whenIWon)
        .map(mi -> mi.fullGame().scores().get(0))
        .toList());
    stat2.display();
    
    System.out.println("Match when loser has 0 points");
    for (MatchInfo mi : matchInfos.stream().filter(mi -> mi.fullGame().scores().get(1) == 0).toList()) {
      System.err.println(mi + " - "+mi.fullGame().scores());
    }
    
    
    
    
  }

  private static boolean whenILose(MatchInfo i) {
    return i.isWon() && !i.p0().equals(me);
  }
  private static boolean whenIWon(MatchInfo i) {
    return i.isWon() && i.p0().equals(me);
  }
}
