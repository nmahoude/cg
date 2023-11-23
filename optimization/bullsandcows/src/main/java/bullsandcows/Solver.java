package bullsandcows;

public class Solver {
  private int numberLength;
  private int[][] positionByDigits;
  private int count;


  public void init(int numberLength) {
    this.numberLength = numberLength;
  }

  public void solve(int[][] positionByDigits) {
    this.positionByDigits = positionByDigits;
    int[] disponibles = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    count = 0;
    fill(0, 0, disponibles);
    System.err.println("Possibilities : "+count);
  }

  private void fill(int depth, int current, int[] disponibles) {
    
    if (depth == numberLength) {
      // System.err.println(""+current+" is possible");
      count++;
      return;
    }
    
    for (int i=0;i<10;i++) {
      if (disponibles[i] == 0) continue;
      if (positionByDigits[i][depth] == Player.NOTUSED) continue;
      
      
      current = current*10 + i;
      disponibles[i] = 0;
      fill(depth+1, current, disponibles);
      current = current / 10;
      disponibles[i] = 1;
    }
  }

  

}
