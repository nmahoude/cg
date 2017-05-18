package c4l.entities;

import java.util.Arrays;
import java.util.List;

public enum MoleculeType {
  A(0), B(1), C(2), D(3), E(4);

  public final int index;
  public static final List<MoleculeType> all = Arrays.asList(A, B, C, D, E);
  
  private MoleculeType(int index) {
    this.index = index;
  }

  public static MoleculeType get(int xp) {
    if (xp <0) return null;
    return values()[xp];
  }
}
