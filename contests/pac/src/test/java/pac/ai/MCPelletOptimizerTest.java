package pac.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.agents.PacmanType;
import pac.map.Pos;

public class MCPelletOptimizerTest {
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
    
    pacman = state.pacmen[0];
    opp = state.pacmen[5];
  }
  
  
  @Test
  void shouldNotCross() throws Exception {
    state.read(new Scanner(""+
        "8 8 "+
        "4 "+
        "0 1 10 7 ROCK 4 8 "+
        "1 1 9 6 PAPER 4 8 "+
        "2 1 5 9 SCISSORS 4 8 "+
        "3 1 8 1 ROCK 4 8 "+
        "13 "+
        "6 9 1 "+
        "7 9 1 "+
        "5 8 1 "+
        "5 7 1 "+
        "5 6 1 "+
        "5 5 1 "+
        "5 10 1 "+
        "7 1 1 "+
        "11 1 1 "+
        "9 7 10 "+
        "25 7 10 "+
        "15 10 10 "+
        "19 10 10 "+
        ""));
    
    
    Pos[][] positions = new MCPelletOptimizer().optimize(state);
    
    assertThat(positions[0][0] == p(9,7) && positions[1][0] == p(9,7)).isFalse();
  }
  
  
  private static void prepareFirstMap() {
    Scanner in = new Scanner(""+
        "35 12"+EOF+
        "###################################"+EOF+
        "###   #     #   # #   #     #   ###"+EOF+
        "##### # # ##### # # ##### # # #####"+EOF+
        "        #                 #        "+EOF+
        "### ### ##### ####### ##### ### ###"+EOF+
        "    #                         #    "+EOF+
        "### # # # ### ####### ### # # # ###"+EOF+
        "#   #   #   #         #   #   #   #"+EOF+
        "# # # ##### # ####### # ##### # # #"+EOF+
        "# #     #                 #     # #"+EOF+
        "# ### # # ### # # # # ### # # ### #"+EOF+
        "###################################"+EOF+
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
