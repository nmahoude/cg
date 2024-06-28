package pac.minimax;

import static org.assertj.core.data.Offset.offset;

import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.agents.PacmanType;
import pac.map.Pos;

public class MinimaxTest {
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
        "73 65 "+
        "2 "+
        "0 1 11 8 ROCK 1 5 "+
        "0 0 11 12 PAPER 1 5 "+
        "0 "+
        ""));
    pacman = state.pacmen[0];
    opp = state.pacmen[5];
  }
  
  @Test
  void eatItWhenHeIsTrapped() throws Exception {
  	Player.DEBUG_MINIMAX = true;
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(10).withType(PacmanType.PAPER);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(10).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(2);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.PESSIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(10_000,  offset(10.0)/*depth*/);
  }
  

  @Test
  void eatItWhenHeIsTrappedAndICanSwitch() throws Exception {
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(0).withType(PacmanType.ROCK);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(10).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(4);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.PESSIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(10_000,  offset(10.0)/*depth*/);
  }
  
  @Test
  void eatItWhenHeIsTrappedAndICanSwitchOrIWouldBeEaten() throws Exception {
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(0).withType(PacmanType.SCISSORS);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(10).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(4);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.PESSIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(10_000,  offset(10.0)/*depth*/);
  }

  @Test
  void statusQuoWhenPessimistic_and_HeIsTrappedAndICanSwitchAndHeCouldSwitch() throws Exception {
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(0).withType(PacmanType.SCISSORS);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(0).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(4);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.PESSIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(0,  offset(10.0)/*depth*/);
  }

  @Test
  void eatItWhenNormal_and_HeIsTrappedAndICanSwitchAndHeCouldSwitch() throws Exception {
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(0).withType(PacmanType.SCISSORS);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(0).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(4);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.NORMAL, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(5000 /* can kill it, but not sure, be can't be dead */,  offset(10.0)/*depth*/);
  }

  @Test
  void eatItWhenOptimistic_and_HeIsTrappedAndICanSwitchAndHeCouldSwitch() throws Exception {
    builder(pacman).withPos(3, 2).withSpeed(0).withCooldown(0).withType(PacmanType.SCISSORS);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(0).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(4);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.OPTIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(10_000,  offset(10.0)/*depth*/);
  }
  
  @Test
  void eatItWhenFartherButWithSpeedAndHeIsTrapped() throws Exception {
    builder(pacman).withPos(3, 3).withSpeed(1).withCooldown(10).withType(PacmanType.PAPER);
    builder(opp).withPos(3, 1).withSpeed(0).withCooldown(10).withType(PacmanType.ROCK);

    Minimax ng = new Minimax(2);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.PESSIMISTIC, pacman, opp).score;
    
    Assertions.assertThat(searchMinimizing).isEqualTo(10_000,  offset(10.0) /*depth*/);
  }
  
  
  @Test
  void debug() throws Exception {
    Player.DEBUG_MINIMAX = true;
    builder(pacman).withPos(3, 1).withSpeed(5).withCooldown(10).withType(PacmanType.SCISSORS);
    builder(opp).withPos(3, 3).withSpeed(0).withCooldown(0).withType(PacmanType.PAPER);

    Minimax ng = new Minimax(2);
    double searchMinimizing = ng.searchMinimizing(state, Minimax.NORMAL, pacman, opp).score;
    
  }
  
  private static void prepareFirstMap() {
    Scanner in = new Scanner(""+
        "33 14"+EOF+
        "#################################"+EOF+
        "### #       # ##### #       # ###"+EOF+
        "### ##### # # ##### # # ##### ###"+EOF+
        "###       #           #       ###"+EOF+
        "### ### # ### # # # ### # ### ###"+EOF+
        "#   #   # #     #     # #   #   #"+EOF+
        "# # # # # # # ##### # # # # # # #"+EOF+
        "# #   #     #       #     #   # #"+EOF+
        "##### ### # # # # # # # ### #####"+EOF+
        "#     #   # #   #   # #   #     #"+EOF+
        "# ### # # # ### # ### # # # ### #"+EOF+
        "#   #   #       #       #   #   #"+EOF+
        "### # # ### # ##### # ### # # ###"+EOF+
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
