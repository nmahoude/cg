package c4l.molecule.minimax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import c4l.GameState;
import minimax.Node;

/**
 * Values is : 
 * 0-2 First rows are the samples without molecule i already carry
 *  99 if the sample is free
 * 3 is the sum of present molecule in first 3 rows (0->3)
 * 4 is the max
 * 5 is disposable units
 * @author nmahoude
 *
 */
public class MoleculeNode implements Node {
  private static final int SUM = 3;
  private static final int MAX = 4;
  private static final int DISPO = 5;
  
  int values[] = new int[6*GameState.MOLECULE_TYPE];
  private int freeSlots;
  private int totalDispo = 0;
  Collection<Node> children = null;
  
  public MoleculeNode(int freeSlots, int totalDispo, int[] values) {
    this.freeSlots = freeSlots;
    this.totalDispo = totalDispo;
    System.arraycopy(values, 0, this.values, 0, 6*GameState.MOLECULE_TYPE);
  }
  
  @Override
  public boolean isEndNode() {
    return freeSlots == 0 || totalDispo == 0;
  }

  @Override
  public int evaluate() {
    int counts[] = new int[3];
    for (int i=0;i<3;i++) {
      counts[i] = 0;
      for (int m=0;m<GameState.MOLECULE_TYPE;m++) {
        counts[i] += values[GameState.MOLECULE_TYPE * i + m];
      }
    }
    
    if (counts[0] == 0) {
      return 100;
    } else if (counts[1] == 0) {
      return 60;
    } else  if (counts[2] == 0) {
      return 40;
    }
    return 0;
  }

  @Override
  public Collection<Node> getChildren() {
    return null;
  }
  
  @Override
  public Collection<Node> getChildren(boolean isMaximizing) {
    if (children == null) {
      children = new ArrayList<Node>();
      // build the children
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (enoughDisposableMolecules(i)) {
          MoleculeNode child = new MoleculeNode(freeSlots, totalDispo, values);
          child.play(i, isMaximizing);
        }
      }
    }
    return children;
  }

  private void play(int index, boolean isMaximizing) {
    if (isMaximizing) {
      // me
      freeSlots--;
      for (int i=0;i<3;i++) {
        int value = values[i*GameState.MOLECULE_TYPE+ index];
        if (value > 0) {
          values[i*GameState.MOLECULE_TYPE+ index]--;
          if (value == 1) {
            values[SUM*GameState.MOLECULE_TYPE+ index]--;
          }
        }
      }
    } 
    values[DISPO*GameState.MOLECULE_TYPE + index]--;
    totalDispo--;
  }

  private boolean enoughDisposableMolecules(int i) {
    return values[DISPO*GameState.MOLECULE_TYPE + i] > 0;
  }
  
  
}
