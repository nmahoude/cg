package sg22.ais;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import sg22.Player;
import sg22.State;
import sg22.Actions.Action;
import sg22.nodes.Node;

public class PlayCardAITest {

  @Test
  void playIt() throws Exception {
    State state = new State();

    state.read(FastReader.fromString("""
        ^ PLAY_CARD
        ^ 12
        ^ APPLICATION      1 4 0 4 0 0 0 0 0
        ^ APPLICATION      9 0 4 0 0 4 0 0 0
        ^ APPLICATION     22 0 0 0 0 4 4 0 0
        ^ APPLICATION     14 0 0 4 0 4 0 0 0
        ^ APPLICATION     24 0 0 0 0 4 0 0 4
        ^ APPLICATION     27 0 0 0 0 0 0 4 4
        ^ APPLICATION      0 4 4 0 0 0 0 0 0
        ^ APPLICATION      5 4 0 0 0 0 0 4 0
        ^ APPLICATION     13 0 0 4 4 0 0 0 0
        ^ APPLICATION     20 0 0 0 4 0 0 4 0
        ^ APPLICATION     16 0 0 4 0 0 0 4 0
        ^ APPLICATION      4 4 0 0 0 0 4 0 0
        ^ 6 0 1 0
        ^ 1 0 0 0
        ^ 5
        ^ HAND            0 0 0 1 0 1 0 0 1 2
        ^ DRAW            0 0 0 0 0 3 0 0 0 2
        ^ AUTOMATED       0 0 0 0 0 0 0 0 2 0
        ^ OPPONENT_CARDS  1 1 0 0 0 1 0 0 1 3
        ^ OPPONENT_AUTOMATED  0 0 0 0 0 0 0 0 2 0
        ^ 0 
        """.replace("^ ", "")));

    state.readRemember(FastReader.fromString(
        "^ REMEMBER 0 0 1 -1 ".replace("^ ", "")));

    Player.DEBUG_RELEASES = true;
    Player.turn = 20;
    Player.start = System.currentTimeMillis() + 1_000_000;
    
    Node root = Node.root(state);
    root.compute();
    PlayCardAI ai = new PlayCardAI();
    Action action = ai.think(root);

    System.err.println("RESULT : "+action);

  }
}
