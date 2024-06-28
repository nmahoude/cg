package spring2023;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class StateTest {

  
  
  
  public static State buildFromInput(String input) {
    
    String cleanInput = Stream.of(input.split("\n"))
        .map(String::trim)
        .filter(s -> s.length() != 0 && s.charAt(0) == '^')
        .map(s -> s.replace("^", " ").concat("\n")) // remove ^
        .collect(Collectors.joining());
    FastReader in = FastReader.fromString(cleanInput);
    
    State.readInit(in);
    State state = new State();
    state.readOptional(in);
    state.readPacked(in);
    
    return state;
  }
  
  @Test
  void testReadingOfAnts() throws Exception {
    State state = buildFromInput(""" 
*** INIT ***
^31
^ 2 0 1 3 -1 2 4 -1 
^ 0 0 5 7 3 0 -1 14 0 0 0 -1 13 6 8 4 1 0 7 -1 9 -1 0 1 1 0 -1 0 2 8 -1 10 0 0 15 17 7 1 14 22 0 0 2 13 21 16 18 8 0 0 17 -1 -1 3 1 5 0 0 4 2 6 18 -1 -1 0 0 -1 -1 -1 11 -1 3 0 0 12 -1 4 -1 -1 -1 
^ 0 0 9 -1 -1 19 13 -1 0 0 20 14 -1 10 -1 -1 2 0 -1 11 19 21 6 2 2 0 22 5 1 -1 12 20 0 0 23 -1 17 5 22 30 0 0 6 21 29 24 -1 18 0 0 -1 25 -1 7 5 15 0 0 8 6 16 -1 26 -1 0 0 11 -1 -1 27 21 13 0 0 28 22 14 12 -1 -1 
^ 0 0 13 19 27 29 16 6 0 0 30 15 5 14 20 28 2 0 -1 -1 -1 15 30 -1 2 0 16 29 -1 -1 -1 -1 2 0 -1 -1 -1 -1 17 -1 2 0 -1 18 -1 -1 -1 -1 1 0 19 -1 -1 -1 29 21 1 0 -1 30 22 20 -1 -1 2 0 21 27 -1 -1 24 16 2 0 -1 23 15 22 28 -1 
^ 
^1
^ 15 16 
*** OPTIONAL ***
^2 218
*** TURN
^ 4 0 
^ 43 0 0 13 13 0 0 0 0 0 0 0 0 53 53 300000 10000 0 0 0 
^ 0 0 0 100040 41 51 51 23 23 52 600049 
*** END
        """);
    
    assertThat(state.cells[15].myAnts).isEqualTo(3);
    assertThat(state.cells[23].myAnts).isEqualTo(1);
    assertThat(state.cells[28].myAnts).isEqualTo(0);
    assertThat(state.cells[30].myAnts).isEqualTo(6);
  }
  
  
  
}
