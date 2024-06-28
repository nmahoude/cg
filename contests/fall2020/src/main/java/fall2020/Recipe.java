package fall2020;

import fall2020.fast.FastReader;
import fall2020.optimizer.OInv;
import fall2020.optimizer.ORecipe;
import fall2020.optimizer.OptiGraph;

public class Recipe {
  public int gameId; // fix
  public ORecipe recipe;
  public int tomeIndex; // fix
  public int taxCount; // fix (not used)
  public boolean castable; // fix (not used)
  public boolean repeatable; // fix (not used)

  public int price; // fix sur un tour

  public void read(FastReader in) {
    int a = in.nextInt();
    int b = in.nextInt();
    int c = in.nextInt();
    int d = in.nextInt();

    recipe = OptiGraph.getRecipe(a,b,c,d);
        
    price = in.nextInt();
    
    tomeIndex = in.nextInt();
    taxCount = in.nextInt();
    castable = in.nextInt() != 0;
    repeatable = in.nextInt() != 0;
    
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("\"%3d BREW %d %d %d %d %d %d %d %d %d\"+EOF+",gameId,a,b,c,d, price, tomeIndex, taxCount, castable?1:0, repeatable?1:0));
    }
  }

  public double distance(OInv newInv) {
    int total = 0;
    for (int i=0;i<4;i++) {
      total += (i+1) * Math.min(0, newInv.inv[i]+recipe.delta[i]);
    }
    return -total;
  }

  public double distanceV2(int[] currentInv) {
    int needed[] = new int[4];
    int ply = 0;
    
    for (int i=0;i<4;i++) {
      needed[i] = -recipe.delta[i];
    }    
    
    for (int i=3;i>0;i--) {
      if (currentInv[i] - needed[i] >= 0) {
        // nice
      } else {
        needed[i-1] += (needed[i] - currentInv[i]); // TODO il y a d'autre recettes ...
        ply += needed[i] - currentInv[i];
      }
    }
    
    return ply + Math.max(0, needed[0]-currentInv[0] / 2); // TODO /2 car il y a un spell qui donne 2 t0
  }

  public String debug() {
    return String.format("R#%s [%d %d %d %d]", gameId, -recipe.delta[0], -recipe.delta[1], -recipe.delta[2], -recipe.delta[3]);
  }
  
  @Override
  public String toString() {
    return debug();
  }
}
