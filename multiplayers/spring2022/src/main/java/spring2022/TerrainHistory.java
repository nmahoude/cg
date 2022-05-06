package spring2022;

public class TerrainHistory {
  private static final int STEPS = 200;
  private static final int WIDTH = State.WIDTH / STEPS + 1;
  private static final int HEIGHT = State.HEIGHT / STEPS + 1;
  int cells[][];
  
  public TerrainHistory() {
    cells = new int[WIDTH][HEIGHT ];
    
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        cells[x][y] = Player.turn;
      }
    }  
  
  }

  public void update(State state) {
    for (int yy=0;yy<HEIGHT;yy++) {
      for (int xx=0;xx<WIDTH;xx++) {
        int x = xx * STEPS;
        int y = yy * STEPS;
        Pos pos = Pos.get(x,y);
       
        if (State.myBase.isInRange(pos, State.BASE_VIEW_DIST)) {
          cells[xx][yy] = Player.turn; 
          continue;
        }
        
        for (int i=0;i<3;i++) {
          Hero hero = state.myHeroes[i];
          if (hero.isInRange(pos, 2200)) {
            cells[xx][yy] = Player.turn;
            break;
          }
        }
      }
    }
  }
  
  public void debug() {
    for (int yy=0;yy<HEIGHT;yy++) {
     
      for (int xx=0;xx<WIDTH;xx++) {
        int since = (Player.turn - cells[xx][yy]) / 10; 
        System.err.print( since < 10 ? ""+since : "*");
      }
      System.err.println();
    }
  }
  
  
}
