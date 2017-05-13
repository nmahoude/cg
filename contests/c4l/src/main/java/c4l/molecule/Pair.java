package c4l.molecule;

import c4l.entities.MoleculeType;

public class Pair {
  public Pair(int i) {
    type = MoleculeType.values()[i];
  }
  public MoleculeType type;
  public int values[] = new int[10];
}
