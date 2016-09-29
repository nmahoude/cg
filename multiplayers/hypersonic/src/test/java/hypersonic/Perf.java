package hypersonic;


public class Perf {
  public static void main(String[] args) {
    Player.APlayer me;
    Player.APlayer opponent;

    
    Player.Game game = new Player.Game(13, 11);
    Player.MCTS.game = game;
    
    me = new PlayerTest.PlayerBuilder(game.currentState)
        .withId(0)
        .withPos(0, 0)
        .build();
    
    opponent = new PlayerTest.PlayerBuilder(game.currentState)
        .withId(1)
        .withPos(12, 10)
        .build();
    
    PlayerTest.buildBoard(game,
        "   0 0  0  0 ",
        " X0X X X X X ",
        "0  0 0   0   ",
        " X0X X0X X X ",
        "    0   0    ",
        " X0X0X0X X X ",
        "             ",
        "0X X X0X X X ",
        "             ",
        " X X X X X X ",
        "             "
          );
    
   
    // game ai
    Player.MCTSAI ai = new Player.MCTSAI() {
      @Override
      int getAffordableSimulationCount() {
        return 300_000;
      }
    };
    ai.game = game;
    
    ai.compute();
    
  }
  
}
