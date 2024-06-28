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

public class MinimaxDEbugTest2 {
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
        "130 125 "+
        "4 "+
        "0 1 4 1 ROCK 3 7 "+
        "1 1 11 13 PAPER 5 9 "+
        "2 1 13 3 SCISSORS 3 7 "+
        "1 0 12 1 PAPER 4 8 "+
        "1 "+
        "5 1 1 "+
        ""));
    pacman = state.pacmen[0];
    opp = state.pacmen[5];
  }
  
  @Test
  void aller_en_19_4_est_ok() throws Exception {
    Player.DEBUG_MINIMAX = true;

    Minimax ng = new Minimax(4);
    MMNode result = ng.searchMinimizing(state, Minimax.NORMAL, state.pacmen[2], state.pacmen[5+1]);
    
    for (MMNode child : result.getChilds()) {
      System.err.println("    "+child.toString(0)+" = "+child.score);
    }
  }
  
  
  private static void prepareFirstMap() {
    Scanner in = new Scanner(""+
        "31 17"+EOF+
        "###############################"+EOF+
        "###   # #             # #   ###"+EOF+
        "### ### # ### # # ### # ### ###"+EOF+
        "#         ### # # ###         #"+EOF+
        "# # ##### ### # # ### ##### # #"+EOF+
        "# #     #     # #     #     # #"+EOF+
        "##### # # ### ### ### # # #####"+EOF+
        "# #   #     #     #     #   # #"+EOF+
        "# # # ### # # # # # # ### # # #"+EOF+
        "#         #   # #   #         #"+EOF+
        "##### ### ### ### ### ### #####"+EOF+
        "#     #   #   # #   #   #     #"+EOF+
        "# ### # # # # # # # # # # ### #"+EOF+
        "# ###   #             #   ### #"+EOF+
        "# ### # # # ####### # # # ### #"+EOF+
        "#     # # #         # # #     #"+EOF+
        "###############################"+EOF+
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
