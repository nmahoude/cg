package c4l.molecule;

import java.util.Arrays;
import java.util.Comparator;

import c4l.GameState;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;

public class OldNonFinishedOptimizer {
  private static final int SUM_NOT_NULL = 3;
  private static final int MAX = 4;
  private static final int STORAGE= 5;
  private static final int EXPERTISE= 6;
  private static final int SUM_NEEDED= 7;
  private static final int LAST = 8;

  private GameState state;
  private Robot me;
  private Pair[] mInfo = new Pair[LAST];

  
  public MoleculeType optimize(GameState state, Robot me) {
    this.state = state;
    this.me = me;

    int totalAvailables = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      mInfo[i] = new Pair(i);
      totalAvailables+=state.availables[i];
    }
    fillInfos();
    
    // order on max
    Arrays.sort(mInfo, new Comparator<Pair>() {
      @Override
      public int compare(Pair p1, Pair p2) {
        return - Integer.compare(
            p1.values[SUM_NOT_NULL],
            
            p2.values[SUM_NOT_NULL]);
      }
    });
    
    return mInfo[0].type;
  }

  private void fillInfos() {
    for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
      Pair info = mInfo[j];
      info.values[SUM_NOT_NULL] = 0;
      info.values[MAX] = 0;
      info.values[SUM_NEEDED] = 0;
      
      for (int i=0;i<3;i++) {
        /*SUM*/info.values[SUM_NOT_NULL]+= info.values[SUM_NOT_NULL]>0 ? 1 : 0;
        if (info.values[i] < 99) {
          /*MAX*/info.values[MAX]= Math.max(info.values[i], info.values[MAX]);
        }
      }
      /*STORAGE*/info.values[STORAGE] = me.storage[j];
      /*EXPERTISE*/info.values[EXPERTISE] = me.expertise[j];
    }
  }
}
