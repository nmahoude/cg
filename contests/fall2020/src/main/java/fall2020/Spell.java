package fall2020;

import fall2020.fast.FastReader;
import fall2020.optimizer.OSpell;
import fall2020.optimizer.OptiGraph;

public class Spell {

  public int gameId; // fix
  public OSpell spell;
  public byte type; // fix (not used)
  
  public int taxCount; // not fix
  private int tomeIndex; // not fix
  public boolean castable; // TODO remove : not fix, but handles in player castables mask
  
  
  public void read(FastReader in) {
    int a = in.nextInt();
    int b = in.nextInt();
    int c = in.nextInt();
    int d = in.nextInt();
    
    spell = OptiGraph.getSpell(a, b, c, d);
    
    int price = in.nextInt();
    
    tomeIndex = in.nextInt();
    taxCount = in.nextInt();
    castable = in.nextInt() != 0;
    boolean repeatable = in.nextInt() != 0;
    
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("\"%3d %s %2d %2d %2d %2d %2d %2d %2d %2d %2d\"+EOF+",gameId,type(), a,b,c,d, price, tomeIndex, taxCount, castable?1:0, repeatable?1:0));
    }
  }

  private String type() {
    if ('C'/* CAST */ == type) {
      return "CAST";
    } else if ('O'/* OPPONENT_CAST */ == type) {
      return "OPPONENT_CAST";
    } else if ('L'/* "LEARN" */ == type) {
      return "LEARN";
    } else {
      return "OOOPS";
    }
  }

  public boolean canBeCast(int[] currentInv) {
    return false;
  }

  @Override
  public String toString() {
    return String.format("S#%d [%d %d %d %d]", gameId, spell.delta[0], spell.delta[1], spell.delta[2], spell.delta[3] );
  }
}
