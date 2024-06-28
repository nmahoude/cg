package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class StateTest {


  @Nested
  static class UpdateFromSymetryShould {
    private State previous;
    private State current;

    @BeforeEach
    void setup() {
      State.turn = 35;
      previous = new State();
      current = new State();
      current.previousState = previous;
      
      State.setCanBeInitial();
    }

    @Test
    void addFishWhenAlterEgoDiscoveredEvenAfter10TurnIfNoFear() throws Exception {
      State.turn = 10;
      current.fishes.add(fish(10, 6000, 2000));
      State.canBeInitial[10] = false;
      
      current.myDrones[0].pos.set(6500, 2000);
      current.myDrones[0].currentScans.scan(10);
      
      
      List<Fish> newFishes = current.updateFromSymetry();
      
      assertThat(newFishes).contains(new Fish(11));
      Fish f5 = newFishes.get(0);
      assertThat(f5.pos).isEqualTo(new Pos(4000,2000));
    }
    
    @Test
    void dontAddFishWhenAlterEgoDiscoveredIfFearInPast() throws Exception {
      State.turn = 10;
      current.fishes.add(fish(10, 6000, 2000));
      State.canBeInitial[10] = false;
      
      previous.oppDrones[0].pos.set(6000, 3399); // just in fear radius of fish
      
      current.myDrones[0].pos.set(6500, 2000);
      current.myDrones[0].currentScans.scan(10);
      
      
      List<Fish> newFishes = current.updateFromSymetry();
      
      assertThat(newFishes).doesNotContain(new Fish(11));
    }

    @Test
    void dontAddFishWhenAlterEgoDiscoveredIfFearOfOtherFishInPast() throws Exception {
      State.turn = 10;
      current.fishes.add(fish(10, 6000, 2000)); // alter ego is 4000, 2000
      State.canBeInitial[10] = false;
      
      previous.oppDrones[0].pos.set(2601, 2000); // just in fear radius of fish
      
      current.myDrones[0].pos.set(6500, 2000);
      current.myDrones[0].currentScans.scan(10);
      
      List<Fish> newFishes = current.updateFromSymetry();
      
      assertThat(newFishes).doesNotContain(new Fish(11));
    }
    
    @Test
    void addFishWhenAlterEgoDiscovered() throws Exception {
      current.fishes.add(fish(4, 6000, 2000));
      
      State.turn = 1;
      List<Fish> newFishes = current.updateFromSymetry();
      
      assertThat(newFishes).contains(new Fish(5));
      Fish f5 = newFishes.get(0);
      assertThat(f5.pos).isEqualTo(new Pos(4000,2000));
    }
  }
  
  
  @Nested
  static class UpdateFromPreviousStateShould {
    private State previous;
    private State current;

    @BeforeEach
    void setup() {
      State.turn = 35;
      previous = new State();
      current = new State();
      current.previousState = previous;
      
    }
    
    @Test
    void dontAddFictiveFishesWhenCalculatedPosIsOutOfTriangulation() throws Exception {
      previous.fishes.add(fish(4, 1000, 2000));

      current.currentTriangulations[4] = new Rectangle(0,0,900,900);
      
      current.updateFromPreviousState(Collections.emptyList());
      
      assertThat(current.fishes).doesNotContain(new Fish(4));
    }
    
    
    @Test
    void dontAddFictiveFishesWhenOppScanItAndFishWasNotInItsRange() throws Exception {
      previous.fishes.add(fish(4, 1000, 2000));
      
      current.oppDrones[0].pos.set(6000, 2000);
      current.oppDrones[0].currentScans.scan(4);
      
      current.updateFromPreviousState(Collections.emptyList());
      
      assertThat(current.fishes).doesNotContain(new Fish(4));
    }

    @Test
    void dontAddFictiveFishesWhenIDidNotScannedItButWasInMyRange() throws Exception {
      previous.fishes.add(fish(4, 6000, 2000));
      
      current.myDrones[0].pos.set(6000, 2000);
      
      current.updateFromPreviousState(Collections.emptyList());
      
      assertThat(current.fishes).doesNotContain(new Fish(4));
    }
    
    @Test
    void dontAddFictiveFishWhenNotPresentAnymore() throws Exception {
      previous.fishes.add(fish(4, 2000, 2000));
      current.fishPresent[4] = false;
      
      current.updateFromPreviousState(Collections.emptyList());
      
      assertThat(current.fishes).doesNotContain(new Fish(4));
    }
  }
  
  
  private static Fish fish(int id, int x, int y) {
    return fish(id,x,y,0,0);
  }
  private static Fish fish(int id, int x, int y, int vx, int vy) {
    Fish f = new Fish(id);
    f.pos.set(x,y);
    f.speed.set(vx, vy);
    return f;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public static State fromString(String input) {
    State state = new State();
    
    FastReader in = FastReader.fromString(cleanInput(input));
    State.readPackedInit(in);
    state.readOptional(in);
    state.readPacked(in);

    return state;
  }
  
  
  public static String cleanInput(String input) {
    String cleanInput = Stream.of(input.split("\n"))
      .map(String::trim)
      .filter(s -> s.length() != 0 && s.charAt(0) == '^')
      .map(s -> s.replace("^", " ").concat("\n")) // remove ^
      .collect(Collectors.joining());
    
    return cleanInput;
  }

}
