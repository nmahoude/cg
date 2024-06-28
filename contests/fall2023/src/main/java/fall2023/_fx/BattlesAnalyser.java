package fall2023._fx;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cgfx.frames.Frame;
import cgfx.frames.GameReader;
import fall2023.State;
import fall2023._fx.BattlesReader.FullGame;
import fall2023._fx.BattlesReader.MatchInfo;
import fall2023._fx.BattlesReader.Player;
import fast.read.FastReader;

public class BattlesAnalyser {
  private static final String REMEMBER_ME = "3359544cac158596f273ed5ebc3cd0a1a43eb1";
  private static final String MY_USER_ID = "335954";

  private static final Player me = new Player("nmahoude", "whatever");
  private static final Path base = Paths.get(new File("target/battleAnalyser").getAbsolutePath());
  
  static class ResultStat {
    Player versus;
    boolean win;
    
    int myTurnFirstLvl2;
    int oppTurnFirstLvl2;
    public Object pos;
    public int myScore;
    public int oppScore;
    public int turns;
    public int my1stLvl2;
    public int opp1stLvl2;
    public String gameId;
    public int myLightUse;
    public int oppLightUse;
  }
  
  
  static List<ResultStat> stats = new ArrayList<>();
  
  public static void main(String[] args) throws IOException {
    System.out.println("FC2023 - Analysis of battles !");
    System.out.println();

    List<BattlesReader.MatchInfo> matchInfos = new BattlesReader(base, REMEMBER_ME, MY_USER_ID).read();
    
    
    List<Integer> pointsWhenILose = matchInfos.stream()
            .filter(i -> i.isWon() && !i.p0().equals(me))
            .map(mi -> mi.fullGame().scores().get(1))
            .toList();
    
    Stat stat = new Stat("Points when I lose", pointsWhenILose);
    stat.display();
    
    
    Stat stat2 = new Stat("Points when I win", matchInfos.stream().filter(i -> i.isWon() && i.p0().equals(me)).map(mi -> mi.fullGame().scores().get(0)).toList());
    stat2.display();
    
    System.out.println("Match when loser has 0 points");
    for (MatchInfo mi : matchInfos.stream().filter(mi -> mi.fullGame().scores().get(1) == 0).toList()) {
      System.err.println(mi + " - "+mi.fullGame().scores());
    }

    System.setErr(new PrintStream("target/temp.txt"));
 // first player to scan a type 2 fish
    
    for (MatchInfo mi : matchInfos) {
    	
      System.out.println("Reading game "+mi.gameId);
      
      FullGame game = mi.fullGame();

      var rStat = new ResultStat();
      stats.add(rStat);
      
      rStat.gameId = mi.gameId;
      rStat.versus = mi.p0().equals(me) ? mi.p1() : mi.p0();
      rStat.pos = mi.p0().equals(me) ? "p1" : "p2";
      rStat.myScore = game.score(me);
      rStat.oppScore = game.score(rStat.versus);
      rStat.win = rStat.myScore > rStat.oppScore;
      
      GameReader reader = new GameReader();
      reader.readReplayFromString(game.content);

      rStat.turns = reader.frames.size();

      State state = new State();
      State previous = new State();
      state.previousState = previous;
      
      int turn = 0;
      for (Frame frame : reader.myFrames) {
        turn++;
        FastReader in = FastReader.fromString(frame.cleanStderr());
        State.readPackedInit(in);
        previous.copyFrom(state);
        state.readOptional(in);
        state.readPacked(in);

        if (previous.myDrones[0].battery > state.myDrones[0].battery) {
          rStat.myLightUse++;
        }
        if (previous.myDrones[1].battery > state.myDrones[1].battery) {
          rStat.myLightUse++;
        }

        if (previous.oppDrones[0].battery > state.oppDrones[0].battery) {
          rStat.oppLightUse++;
        }
        if (previous.oppDrones[1].battery > state.oppDrones[1].battery) {
          rStat.oppLightUse++;
        }
        
        
        
        if (rStat.my1stLvl2 == 0 && state.myDrones[0].currentScans.hasLevel(2) || state.myDrones[1].currentScans.hasLevel(2)) {
          rStat.my1stLvl2 = turn;
        }
  
        if (rStat.opp1stLvl2 == 0 && state.oppDrones[0].currentScans.hasLevel(2) || state.oppDrones[1].currentScans.hasLevel(2)) {
          rStat.opp1stLvl2 = turn;
        }
        
      }
    }
    
    printStats();
    
  }

  private static void printStats() {
    for( var stat : stats) {
      System.out.println(
                      stat.gameId+";"+
                      stat.versus.nickname()+";"+
                      stat.versus.playerAgentId()+";"+
                      stat.pos+";"+
                      stat.turns+";"+
                      stat.win+";"+
                      stat.myScore+";"+
                      stat.oppScore+";"+
                      stat.my1stLvl2+";"+
                      stat.opp1stLvl2+";"+
                      stat.myLightUse+";"+
                      stat.oppLightUse+";"+
                      "");
    }
  }
  
}
