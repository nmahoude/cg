package spring2022;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class FogResolverTest {

  
  @Test
  void unitShouldGoInFogAndBeBackInState() throws Exception {
    State state = State.fromInput("""
        ^3 168 3 204
        ^5
        ^0 1 4558 4356 0 1 -1 -1 -1 -1 -1
        ^1 1 5057 2841 0 0 -1 -1 -1 -1 -1
        ^2 1 13171 5510 0 0 -1 -1 -1 -1 -1
        ^5 2 1953 3756 0 0 -1 -1 -1 -1 -1
        ^58 0 14721 6859 12 0 12 322 237 1 2
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^0
        turn87
        *************************
        *     ATTACKER V2       *
        *************************
        ^turn 87
        ^mind 2
        """, null);
    
    
    FastReader missing58 = FastReader.fromString("""
        ^3 170 3 210
        ^4
        ^0 1 5312 4623 0 0 -1 -1 -1 -1 -1
        ^1 1 4265 2730 0 0 -1 -1 -1 -1 -1
        ^2 1 12788 4807 0 0 -1 -1 -1 -1 -1
        ^5 2 2344 3058 0 0 -1 -1 -1 -1 -1
       """.replace("^", ""));

    state.read(missing58);

    assertThat(state.findUnitById(58)).isNotNull();
  }
}
