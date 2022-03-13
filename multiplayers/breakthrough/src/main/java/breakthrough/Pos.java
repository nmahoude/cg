package breakthrough;

public class Pos {
  private static Pos positions[] = new Pos[8*8];
  static {
    for (int y=0;y<8;y++) {
      for (int x=0;x<8;x++) {
        positions[x+8*y] = new Pos(x,y);
      }
    }
  }
  
  public final int x;
  public final int y;
  public final int offset;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x + 8*y;
  }
  
  @Override
  public String toString() {
    return String.format("(%c%d)", (char)(x+'a'), (Player.firstPlayer ? y+1 : 8-y));
  }
  
  public static Pos from(int x, int y) {
    return positions[x+8*y];
  }

  public static Pos from(char letter, char row) {
    int x = letter - 'a';
    int y = row - '1';
    if (!Player.firstPlayer) {
      y = 7-y;
    }
    return positions[x+8*y];
  }

  public Pos flip() {
    int newY = 7 - this.y;
    return positions[x+8*newY];
  }

  public String output() {
    char lx = (char)('a'+x);
    char ly = (char)('1'+ (Player.firstPlayer ? y : 7-y));
    return ""+lx+""+ly;
  }

  public static Pos from(int offset) {
    return positions[offset];
  }
}
