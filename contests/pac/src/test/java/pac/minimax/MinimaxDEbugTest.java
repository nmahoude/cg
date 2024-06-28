package pac.minimax;

import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.agents.PacmanType;
import pac.map.Pos;

public class MinimaxDEbugTest {
  private static final String EOF = "\r";
  private State state;
  private Pacman pacman;
  private Pacman opp;
  
  @BeforeAll
  public static void setup() {
    prepareFirstMap();
  }

  @BeforeEach
  public void init() {
    state = new State();
    state.init();
    state.read(new Scanner(""+
        "28 22 "+
        "8 "+
        "0 1 4 3 ROCK 3 7 "+
        "0 0 29 3 ROCK 0 0 "+
        "1 1 3 13 PAPER 4 8 "+
        "1 0 29 13 PAPER 4 8 "+
        "2 1 28 11 SCISSORS 3 7 "+
        "3 1 0 7 ROCK 3 7 "+
        "4 1 25 3 PAPER 3 7 "+
        "4 0 7 3 PAPER 3 7 "+
        "31 "+
        "6 3 1 "+
        "8 3 1 "+
        "9 3 1 "+
        "10 3 1 "+
        "11 3 1 "+
        "12 3 1 "+
        "13 3 1 "+
        "3 12 1 "+
        "3 11 1 "+
        "26 11 1 "+
        "25 11 1 "+
        "29 11 1 "+
        "30 11 1 "+
        "31 11 1 "+
        "32 7 1 "+
        "31 7 1 "+
        "30 7 1 "+
        "29 7 1 "+
        "5 7 1 "+
        "24 3 1 "+
        "23 3 1 "+
        "22 3 1 "+
        "21 3 1 "+
        "20 3 1 "+
        "19 3 1 "+
        "26 3 1 "+
        "28 3 1 "+
        "25 8 1 "+
        "25 9 1 "+
        "5 3 10 "+
        "27 3 10 "+
        ""));
    pacman = state.pacmen[0];
    opp = state.pacmen[5];
  }
  
  @Test
  void _22_9_is_not_victory() throws Exception {
    Player.DEBUG_MINIMAX = true;

    Minimax ng = new Minimax(4);
    MMNode result = ng.searchMinimizing(state, Minimax.NORMAL, state.pacmen[2], state.pacmen[5+1]);
    
    for (MMNode child : result.getChilds()) {
      System.err.println("    "+child.toString(0)+" = "+child.score);
    }
  }
  
  
  private static void prepareFirstMap() {
    Scanner in = new Scanner(""+
        "33 15"+EOF+
        "#################################"+EOF+
        "### #     # # # # # # #     # ###"+EOF+
        "### ##### # # # # # # # ##### ###"+EOF+
        "# #           #   #           # #"+EOF+
        "# # ### ##### # # # ##### ### # #"+EOF+
        "#     #   #           #   #     #"+EOF+
        "### # # # # # # # # # # # # # ###"+EOF+
        "      #     # #   # #     #      "+EOF+
        "### # # ##### ##### ##### # # ###"+EOF+
        "#   #                       #   #"+EOF+
        "# ### ##### # # # # # ##### ### #"+EOF+
        "#       #     #   #     #       #"+EOF+
        "### # # # # ######### # # # # ###"+EOF+
        "    # #   #           #   # #    "+EOF+
        "#################################"+EOF+
        ""
        );
    Player.map.read(in);
  }
  
  
  class PacmanBuilder {
    private Pacman pacman;

    public PacmanBuilder(Pacman pacman) {
      this.pacman = pacman;
    }

    public PacmanBuilder withType(PacmanType type) {
      this.pacman.type = type;
      return this;
    }

    public PacmanBuilder withCooldown(int cd) {
      pacman.cooldown = cd;
      if (cd < 5) {
        pacman.speedTurnsLeft = 0;
      }
      
      return this;
    }

    public PacmanBuilder withSpeed(int turnsLeft) {
      pacman.speedTurnsLeft = turnsLeft;
      pacman.cooldown = 5 + turnsLeft;
      return this;
    }

    public PacmanBuilder withPos(int x, int y) {
      pacman.pos = Pos.get(x, y);
      return this;
    }
    
  }
  private PacmanBuilder builder(Pacman pacman) {
    return new PacmanBuilder(pacman);
  }

  
  private Pos p(int x, int y) {
    return Pos.get(x, y);
  }
}
