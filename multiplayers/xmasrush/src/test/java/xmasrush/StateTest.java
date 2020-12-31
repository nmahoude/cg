package xmasrush;

import static cgutils.test.TestOutputer.EOF;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import xmasrush.ai.push.Direction;
import xmasrush.ai.push.PushAction;
import xmasrush.sim.Simulator;

public class StateTest {


  @Test
  void checkDirections() throws Exception {
    String input = ""
        +"0"+EOF
        +"0110"+EOF
        +"0111"+EOF
        +"0011"+EOF
        +"0111"+EOF
        +"1001"+EOF
        +"1010"+EOF
        +"1101"+EOF
        +"0111"+EOF
        +"1010"+EOF
        +"0111"+EOF
        +"1010"+EOF
        +"1101"+EOF
        +"0011"+EOF
        +"1100"+EOF
        +"1010"+EOF
        +"0011"+EOF
        +"1011"+EOF
        +"1010"+EOF
        +"0101"+EOF
        +"1001"+EOF
        +"1010"+EOF
        +"1010"+EOF
        +"0101"+EOF
        +"0110"+EOF
        +"1111"+EOF
        +"1001"+EOF
        +"0101"+EOF
        +"1010"+EOF
        +"1010"+EOF
        +"0110"+EOF
        +"0101"+EOF
        +"1010"+EOF
        +"1110"+EOF
        +"1100"+EOF
        +"1010"+EOF
        +"0011"+EOF
        +"1100"+EOF
        +"0111"+EOF
        +"1010"+EOF
        +"1101"+EOF
        +"1010"+EOF
        +"1101"+EOF
        +"0111"+EOF
        +"1010"+EOF
        +"0110"+EOF
        +"1101"+EOF
        +"1100"+EOF
        +"1101"+EOF
        +"1001"+EOF
        +"12 0 0 0110"+EOF
        +"12 6 6 1001"+EOF
        +"24"+EOF
        +"CANDY 6 3 0"+EOF
        +"SWORD 2 1 0"+EOF
        +"FISH 5 1 0"+EOF
        +"SCROLL 5 4 0"+EOF
        +"KEY 3 2 1"+EOF
        +"POTION 6 1 1"+EOF
        +"ARROW 2 0 0"+EOF
        +"KEY 3 4 0"+EOF
        +"FISH 1 5 1"+EOF
        +"POTION 0 5 0"+EOF
        +"BOOK 3 0 0"+EOF
        +"DIAMOND 6 2 0"+EOF
        +"SHIELD 5 5 0"+EOF
        +"CANE 1 6 1"+EOF
        +"DIAMOND 0 4 1"+EOF
        +"CANE 5 0 0"+EOF
        +"CANDY 0 3 1"+EOF
        +"SWORD 4 5 1"+EOF
        +"MASK 1 4 0"+EOF
        +"BOOK 3 6 1"+EOF
        +"SHIELD 1 1 1"+EOF
        +"ARROW 4 6 1"+EOF
        +"MASK 5 2 1"+EOF
        +"SCROLL 1 2 1"+EOF
        +"6"+EOF
        +"SCROLL 0"+EOF
        +"SHIELD 0"+EOF
        +"POTION 0"+EOF
        +"SCROLL 1"+EOF
        +"SHIELD 1"+EOF
        +"POTION 1"+EOF
        ;
    
    State state = new State();
    state.read(new FastReader(input.getBytes()));
    
    Simulator sim =new Simulator();
    sim.apply(state, PushAction.actions(0, Direction.RIGHT), null);
    state.debugGrid();
    
    sim.unapply(state, PushAction.actions(0, Direction.RIGHT), null);
    state.debugGrid();
    
  }
}
