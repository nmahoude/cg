package calm.aifsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import calm.Desert;
import calm.Item;
import calm.ItemMask;
import calm.P;
import calm.Player;
import calm.State;
import calm.ai.Order;

public class FSMAI {
  private State state;
  PrepareDesert current;
  
  public Order think(State state) {
    this.state = state;
    
   
    List<Desert> mayBeDoing = desertHeMayBeDoing(state);
    Desert desert = null;
    if (mayBeDoing.size() <= 1) {
      System.err.println("Deciding on new desert based on his sole possibility ...");
      desert = chooseDesert(state, mayBeDoing);
    }
    if (desert == null) {
      System.err.println("Choosing the best desert whatever");
      desert = chooseDesert(state, Collections.emptyList());
    }
    System.err.println("*** preparing "+desert);
    System.err.println("*** my hands "+state.me.hands);
    Order order = null;
    
    if (desert == null) {
      System.err.println("Can't prepare anything, dropping");
      current = null;
      return state.me.getRidOff();
    } else {
      if (current == null || current.desert.item.mask != desert.item.mask) {
        System.err.println("FSM - changing desert !");
        current = new PrepareDesert(state, desert);
      } else {
        System.err.println("FSM - still the same desert");
      }
      
      return current.execute();
    }
  }
  
  
  List<Desert> desertHeMayBeDoing(State state) {
    Item clean = new Item(P.INVALID);
    List<Desert> deserts = new ArrayList<>();
    if (!state.him.hands.isEmpty()) {
      int mayBeDoing = 0;
      for (int i=0;i<3;i++) {
        Desert desert = state.deserts[i];
        
        // TODO do better to know what we are preparing
        clean.reset(state.him.hands.mask);
        if (clean.hasDough()) {
          clean.mask =ItemMask.CROISSANT;
        }
        if (clean.hasChoppedDough() || clean.hasRawTart()) {
          clean.mask =ItemMask.BLUEBERRIES_TART;
        }
        if (clean.hasStrawberries()) {
          clean.mask =ItemMask.CHOPPED_STRAWBERRIES;
        }
      
        
        if (clean.mask != 0 && (clean.mask & ~desert.item.mask) == 0 ) {
          System.err.println("he may be doing "+desert);
          deserts.add(desert);
        }
      }
    }
    return deserts;
  }
  
  private Desert chooseDesert(State state, List<Desert> oppDoing) {
    // TODO check if other is doing a desert
    Desert best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    Item clean = new Item(P.INVALID);
    
    for (int i=0;i<3;i++) {
      Desert desert = state.deserts[i];
      if (oppDoing.contains(desert)) continue; // do not try to do the same desert
      
      // TODO do better to know what we are preparing
      clean.reset(state.me.hands.mask);
      clean.mask = (clean.mask & ~ItemMask.DOUGH);
      clean.mask = (clean.mask & ~ItemMask.CHOPPED_DOUGH);
      clean.mask = (clean.mask & ~ItemMask.RAW_TART);
      clean.mask = (clean.mask & ~ItemMask.STRAWBERRIES);
    
      if ((clean.mask & ~desert.item.mask) == 0 ) {
        System.err.println("Desert award "+desert.award+" for "+desert.toString());
        int score = desert.award;
        if (current != null && current.desert.item.mask == desert.item.mask) {
          score += 100;
        }
        
        
        if (Player.turnsRemaining > 0) {
          if (best == null || score > bestScore) {
            best = desert;
            bestScore = score;
          }
        } else {
          if (best == null || score < bestScore) {
            best = desert;
            bestScore = score;
          }
        }
      }
    }
    
    return best;
  }
}
