package fall2023;

public class Scan {
  private final static long[] rowCupMask = {
      0b101010000L,
      0b1010100000L,
      0b101010000000000L,
      0b1010100000000000L
  };
  
  private final static long[] colCupMask = {
      0b110000110000L,
      0b11000011000000L,
      0b1100001100000000L
  };

  private static final int ROW_DECAL = 17;
  private static final int COL_DECAL = 21;

  private static final int ALL_BITS = 30;
  
  long scaned;
  private int score;

  public Scan() {
    scaned = 0;
    score = 0;
  }
  
  public Scan(Scan model) {
    this.scaned = model.scaned;
    this.score = model.score;
  }


  private void setBit(int index) {
    scaned |= (1L << index);
  }
  
  
  public void scan(int id) {
    if (this.contains(id)) {
      return;
    }
    setBit(id);
    score += (1 + State.fType[id]);
    
    checkColsAndRows();
  }
  
  private void checkColsAndRows() {
    for (int i=0;i<4;i++) {
      if (this.checkRowCup(i)) {
        this.setBit(ROW_DECAL + i);
      }
    }
    
    for (int i=0;i<3;i++) {
      if (this.checkColCup(i)) {
        this.setBit(COL_DECAL + i);
      }
    }

  }


  public void updateFirsts(Scan opp) {
    for (int i=0;i<ALL_BITS;i++) {
      if (this.contains(i) && !opp.contains(i)) {
        this.setBit(32 + i);
      }
    }
  }
  
  public boolean contains(int id) {
    return (scaned & (1L << id)) != 0;
  }
  public void clear() {
    scaned = 0L;
    score = 0;
  }
  
  public void copyFrom(Scan model) {
    this.scaned = model.scaned;
    this.score = model.score;
  }
  
  
  public boolean equals(Scan other) {
    return this.scaned == other.scaned;
  }
  
  @Override
  public String toString() {
    return ""+scaned;
  }

  public boolean isFirst(int id) {
    return contains(id + 32);
  }
  
  private boolean checkColCup(int col) {
    return (this.scaned & colCupMask[col]) == colCupMask[col];
  }

  private boolean checkRowCup(int row) {
    return (this.scaned & rowCupMask[row] ) == rowCupMask[row];
  }

  public boolean hasRowCup(int row) {
    return contains(ROW_DECAL+row);
  }

  public boolean hasRowCupFirst(int row) {
    return contains(32 + ROW_DECAL+row);
  }
  
  public boolean hasColCup(int col) {
    return contains(COL_DECAL+col);
  }

  public boolean hasColCupFirst(int col) {
    return contains(32 + COL_DECAL+col);
  }

  public void append(Scan currentScans) {
    scaned |= currentScans.scaned;
    checkColsAndRows();
  }

  public int score() {
    int points = 0;
    for (int l=4;l<16;l++) {
      if (this.contains(l)) {
        points += (1 + State.fType[l]);
        if (this.contains(32+l)) points += (1 + State.fType[l]);
      }
    }
    for (int i=0;i<4;i++) {
      if (this.contains(ROW_DECAL + i)) {
        points+= 3;
        if (this.contains(32 + ROW_DECAL + i)) points+=3;
      }
    }
    
    for (int i=0;i<3;i++) {
      if (this.contains(COL_DECAL + i)) {
        points+= 4;
        if (this.contains(32 + COL_DECAL + i)) points+=4;
      }
    }
    
    
    
    return points;
  }

  public boolean isEmpty() {
    return scaned == 0L;
  }


  public boolean hasLevel(int level) {
    return (scaned & colCupMask[level]) != 0;
  }

  /**
   * complete all fishes
   */
  public void fill(State state, Drone[] drones) {
    // to be sure if the fish is not present but a drone has it
    this.append(drones[0].currentScans);
    this.append(drones[1].currentScans);
    for (int i=4;i<16;i++) {
      if (!state.fishPresent[i]) continue;
      this.scan(i);
    }
  }


  public void debug() {
    for (int l=4;l<16;l++) {
      if (this.contains(l)) System.err.print(""+l+", ");
    }
  }

  public boolean isFull(State state) {
    for (int i=4;i<16;i++) {
      if (!state.fishPresent[i]) continue;
      if (!contains(i)) return false;
    }
    return true;
  }
  
  
}
