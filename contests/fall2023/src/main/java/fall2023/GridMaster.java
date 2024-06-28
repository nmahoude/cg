package fall2023;

public class GridMaster implements Zone {
  private static final int RESOLUTION = 1000;
  private static final int INRE = 10;

  Pos center = new Pos(-1, -1);
  private int[] grid = new int[RESOLUTION * RESOLUTION];
  
  
  public boolean isSet(int x, int y) {
    return grid[x + RESOLUTION * y] == 1;
  }
  public void set(int x, int y) {
    grid[x+ RESOLUTION * y] = 1;
  }

  public void clear(int x, int y) {
    grid[x + RESOLUTION *y] = 0;
  }
  @Override
  public void reset() {
    for (int i=0;i<grid.length;i++) {
      grid[i] = 0;
    }
  }
  
  
  int ddx[] = new int[] { 1, -1, 0, 0};
  int ddy[] = new int[] { 0, 0, 1, -1};
  
  @Override
  public void expand(int range) {
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        int dx = x;
        int dy = y;
        for (int d=0;d<4;d++) {
          for (int r=0;r<range;r+=INRE) {
            dx +=ddx[d];
            dy += ddy[d];
            if (dx < 0 || dx >= RESOLUTION) break;
            if (dy < 0 || dy >= RESOLUTION) break;
            
            if (grid[dx + 1000 * dy] != 0) break;
            grid[dx + 1000 * dy] = 1;
          }
        }
      }
    }
  }
  @Override
  public boolean intersect(Rectangle rectangle) {
    boolean intersect = false;
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1 && !rectangle.contains(x, y)) {
          grid[x+ RESOLUTION*y] = 0;
        } else {
          intersect = true;
        }
      }
    }
    
    return intersect;
  }
  @Override
  public void exact(int creatureX, int creatureY) {

    reset();
    int x = creatureX / INRE;
    int y = creatureY / INRE;
    grid[x + RESOLUTION * y] = 1;
  }
  @Override
  public void cropOutsideCircle(int cx, int cy, int radius) {

    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          if ((x - cx) *(x-cx)+ (y-cy)*(y-cy) > radius * radius) {
            grid[x+ RESOLUTION * y ] = 0;
          }
        }
      }
    }
  }
  @Override
  public void cropInsideCircle(int cx, int cy, int radius) {
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          if ((x - cx) *(x-cx)+ (y-cy)*(y-cy) <= radius * radius) {
            grid[x+ RESOLUTION * y ] = 0;
          }
        }
      }
    }
  }
  @Override
  public boolean hasPotentialIntersectionWithCircle(int cx, int cy, int radius) {
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          if ((x - cx) *(x-cx)+ (y-cy)*(y-cy) <= radius * radius) {
            return true;
          }
        }
      }
    }
    return false;
  }
  @Override
  public int surface() {
    int surface = 0;
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          surface += INRE * INRE;
        }
      }
    }
    return surface;
  }
  
  @Override
  public Pos center() {
    long totX = 0;
    long totY = 0;
    int count = 0;
    
    
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          totX += x;
          totY += y;
          count++;
        }
      }
    }
    center.set((int)(totX / count), (int)(totY / count));
    return center;
  }
  
  @Override
  public void copyFrom(Zone zone) {
    GridMaster gm = (GridMaster)zone;
    System.arraycopy(gm.grid , 0, this.grid, 0, RESOLUTION * RESOLUTION);
    
  }
  
  public long pack() {
    int x0 = 10000;
    int x1 = 0;
    int y0 = 10000;
    int y1 = 0;
    
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
     
          x0 = Math.min(x0,  x * INRE);
          x1 = Math.max(x1,  x * INRE);
          
          y0 = Math.min(y0,  y * INRE);
          y1 = Math.max(y1,  y * INRE);
        }
      }
    }
    
    long rectangleData = 0L;
    rectangleData |= (long) x0 & 0xFFFF;
    rectangleData |= ((long) y0 & 0xFFFF) << 16;
    rectangleData |= ((long) (x1-x0) & 0xFFFF) << 32;
    rectangleData |= ((long) (y1-y0) & 0xFFFF) << 48;
    return rectangleData;
  }

  public void unpack(long rectangleData) {
    int x0 = (int) (rectangleData & 0xFFFF); // Extract x
    int y0 = (int) ((rectangleData >> 16) & 0xFFFF); // Extract y
    int width0 = (int) ((rectangleData >> 32) & 0xFFFF); // Extract width
    int height0 = (int) ((rectangleData >> 48) & 0xFFFF); // Extract height
    
    reset();
    for (int x=x0/INRE;x<(x0+width0)/INRE;x++) {
      for (int y=y0/INRE;y<(y0+height0)/INRE;y++) {
        grid[x + RESOLUTION * y] = 1;
      }      
    }
  }
  
  @Override
  public Zone newInstance() {
    GridMaster gridMaster = new GridMaster();
    gridMaster.copyFrom(this);
    return gridMaster;
  }
  
  @Override
  public void maxOf(Rectangle intersection) {
    // TODO ?
  }
  
  @Override
  public boolean contains(Pos pos) {
    int x = pos.x/ INRE;
    int y = pos.y / INRE;
    return grid[x + RESOLUTION * y] == 1;
  }
  
  @Override
  public double surfaceRatio(Pos pos, int lightRadius) {
    int surface = 0;
    int surfaceIn = 0;
    for (int x=0;x<RESOLUTION;x++) {
      for (int y=0;y<RESOLUTION;y++) {
        if (grid[x + RESOLUTION * y] == 1) {
          surface += INRE * INRE;
          
          if ((x - pos.x) *(x-pos.x)+ (y-pos.y)*(y-pos.y) < lightRadius * lightRadius) {
            surfaceIn++;
          }
        }
      }
    }
    if (surface == 0) {
      return 0.0;
    }
    return 1.0 * surfaceIn / surface;
  }
}
