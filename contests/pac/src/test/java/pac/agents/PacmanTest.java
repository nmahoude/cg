package pac.agents;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import pac.Player;
import pac.State;
import pac.map.Pos;

public class PacmanTest {

  
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
  void disabled() throws Exception {
    assertThat("All rules disables because they need oracle updating ...").isEqualTo("TODO");
  }
  
  @Disabled
  void shouldNotBeScaredWhenEnnemyAt2CellsWhenICanEatHimAndHeIsNotOnCooldown() throws Exception {
    builder(pacman).withPos(11, 10).withSpeed(5).withType(PacmanType.SCISSORS);
    builder(opp).withPos(11, 12).withSpeed(5).withType(PacmanType.PAPER);
    
    pacman.updateMind(state);

    assertThat(pacman.mind).isNotEqualTo(Mind.FRIGHTEN);
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
