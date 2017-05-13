package c4l.entities;

public enum MoleculeType {
  A(0), B(1), C(2), D(3), E(4);

  public final int index;

  private MoleculeType(int index) {
    this.index = index;
  }
}
