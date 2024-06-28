package fall2023;

import java.util.Objects;

public class Rectangle implements Zone {
  private static final Rectangle NONE = new Rectangle(-1000, -1000,0, 0);
  
  
  public static final Rectangle FULL = new Rectangle(0, 0, 10000, 10000);
  public static final Rectangle TYPE0 = new Rectangle(0, 2500, 10_000, 2500);
  public static final Rectangle TYPE1 = new Rectangle(0, 5000, 10_000, 2500);
  public static final Rectangle TYPE2 = new Rectangle(0, 7500, 10_000, 2500);
  public static final Rectangle UGLY = new Rectangle(0, 2500, 10_000, 7500);
  public static final Rectangle UGLY_START = new Rectangle(0, 5000, 10_000, 5000);

  public static final Rectangle byTypes[] = { TYPE0, TYPE1, TYPE2, UGLY };

  public int x;
  public int y;
  public int width;
  public int height;
  private final Pos center = new Pos(-1, -1);
  
  public Rectangle() {
    this(0, 0, 10_000, 10_000);
  }

  public Rectangle(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public Rectangle(Rectangle model) {
    this.x = model.x;
    this.y = model.y;
    this.width = model.width;
    this.height = model.height;
  }

  public void reset() {
    this.x = 0;
    this.y = 0;
    this.width = 10_000;
    this.height = 10_000;
  }
  
  
  public Pos center() {
    center.x = this.x + width  /2;
    center.y = this.y + height /2;
    return center;
  }
  
  public void copyFrom(Zone modelZone) {
    Rectangle model = (Rectangle)modelZone;
    this.x = model.x;
    this.y = model.y;
    this.width = model.width;
    this.height = model.height;
  }

  public long pack() {
    long rectangleData = 0L;
    rectangleData |= (long) x & 0xFFFF;
    rectangleData |= ((long) y & 0xFFFF) << 16;
    rectangleData |= ((long) width & 0xFFFF) << 32;
    rectangleData |= ((long) height & 0xFFFF) << 48;
    return rectangleData;
  }

  public void unpack(long rectangleData) {
    x = (int) (rectangleData & 0xFFFF); // Extract x
    y = (int) ((rectangleData >> 16) & 0xFFFF); // Extract y
    width = (int) ((rectangleData >> 32) & 0xFFFF); // Extract width
    height = (int) ((rectangleData >> 48) & 0xFFFF); // Extract height
  }

  
  @Override
  public Zone newInstance() {
    return new Rectangle(this);
  }
  
  public boolean intersect(Rectangle other) {
    int x1 = Math.max(this.x, other.x);
    int y1 = Math.max(this.y, other.y);
    int x2 = Math.min(this.x + this.width, other.x + other.width);
    int y2 = Math.min(this.y + this.height, other.y + other.height);

    if (x1 <= x2 && y1 <= y2) {
      this.x = x1;
      this.y = y1;
      this.width = x2 - x1;
      this.height = y2 - y1;
      return true;
    } else {
      this.copyFrom(NONE);
      return false;
    }
  }

  public boolean hasIntersection(Rectangle other) {
    int x1 = Math.max(this.x, other.x);
    int y1 = Math.max(this.y, other.y);
    int x2 = Math.min(this.x + this.width, other.x + other.width);
    int y2 = Math.min(this.y + this.height, other.y + other.height);

    if (x1 <= x2 && y1 <= y2) {
      return true;
    } else {
      return false;
    }
  }

  public void set(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")|" + width + "," + height + "|";
  }

  public void expand(int range) {
    int x1 = Math.max(0, this.x - range);
    int x2 = Math.min(10_000, this.x + this.width + range);
    int y1 = Math.max(0, this.y - range);
    int y2 = Math.min(10_000, this.y + this.height + range);

    this.x = x1;
    this.y = y1;
    this.width = x2 - x1;
    this.height = y2 - y1;

  }

  public void exact(int x, int y) {
    this.x = x;
    this.y = y;
    this.width = 1;
    this.height = 1;
  }

  public double calculateCircleCoverage(int circleX, int circleY, int radius) {
    double rectArea = width * height;

    int circleLeft = circleX - radius;
    int circleRight = circleX + radius;
    int circleTop = circleY - radius;
    int circleBottom = circleY + radius;

    int rectLeft = x;
    int rectRight = x + width;
    int rectTop = y;
    int rectBottom = y + height;

    int overlapLeft = Math.max(rectLeft, circleLeft);
    int overlapRight = Math.min(rectRight, circleRight);
    int overlapTop = Math.max(rectTop, circleTop);
    int overlapBottom = Math.min(rectBottom, circleBottom);

    // Check if there's no overlap or circle is outside rectangle
    if (overlapLeft >= overlapRight || overlapTop >= overlapBottom) {
      return 0;
    }

    double overlapArea = (overlapRight - overlapLeft) * (overlapBottom - overlapTop);

    // Calculate percentage of circle coverage in rectangle
    return (overlapArea / rectArea) * 100;
  }

  private static Rectangle circleWork = new Rectangle();

  public boolean intersectionWithCircle(int circleX, int circleY, int radius) {
    if (this.width == 1 && this.height == 1) {
      // consider a point, I can do a circle point interesection !
      int dist2 = (this.x - circleX)*(this.x - circleX) + (this.y - circleY)*(this.y - circleY);
      if (dist2 <= radius*radius) {
        return true;
      } else {
        this.copyFrom(NONE);
        return false;
      }
    }
    
    // TODO ptet pas assez précis, je peux faire mieux, mais c'est rapide et pas mal
    // approchant
    circleWork.set(circleX - radius, circleY - radius, 2 * radius, 2 * radius);
    return this.intersect(circleWork);
  }

  
  @Override
  /** 
   * ratio between a circle and this rectangle
   */
  public double surfaceRatio(Pos pos, int radius) {
    int in = 0, out = 0;
        
    int r2 = radius * radius;
    for (int dx=x;dx<x+width;dx+=10) {
      for (int dy=y;dy<y+height;dy+=10) {
        int dist2 = (dx-pos.x)*(dx-pos.x) + (dy-pos.y)*(dy-pos.y);
        if (dist2 <= r2) {
          in++;
        } else {
          out++;
        }
      }
    }

    if (in + out == 0) return 0.0;
    return  (1.0 * in / (in + out));
  }
  
  
  public boolean hasPotentialIntersectionWithCircle(int circleX, int circleY, int radius) {
    if (this.width == 1 && this.height == 1) {
      // consider a point, I can do a circle point interesection !
      int dist2 = (this.x - circleX)*(this.x - circleX) + (this.y - circleY)*(this.y - circleY);
      return dist2 <= radius*radius;
    }
    
    circleWork.set(circleX - radius, circleY - radius, 2 * radius, 2 * radius);
    return hasIntersection(circleWork);
  }

  public boolean contains(Pos p) {
    return contains(p.x, p.y);
  }
  
  public boolean contains(int x, int y) {
    return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
  }

  public int surface() {
    return width * height;
  }

  /** 
   * Crop this if rect is overlapping
   * @param rect
   */
  public void cropInRectangle(Rectangle rect) {
    if (!hasIntersection(rect)) return;
    
    Rectangle intersection = new Rectangle(this);
    intersection.intersect(rect);
    
    
    if (rect.x <= this.x && rect.x+rect.width >= this.x + this.width) {
      int y0 = y;
      int y1 = y+height;
      
      if (intersection.y == y0) {
        y0 = intersection.y + intersection.height;
      }
      
      if (intersection.y + intersection.height == y1) {
        y1 = intersection.y;
      }
      
      this.y = y0;
      this.height = y1-y0;
    } else if (rect.y <= this.y && rect.y+rect.height>= this.y + this.height) {
      int x0 = x;
      int x1 = x+width;
      
      if (intersection.x == x0) {
        x0 = intersection.x + intersection.width;
      }
      
      if (intersection.x + intersection.width== x1) {
        x1 = intersection.x;
      }
      
      this.x = x0;
      this.width = x1-x0;
    }
    
    
  }
  
  
  /**
   * Reduce the zone to remove all points IN the circle
   * 
   * not perfect : Using a bunch of rectangle croping
   */
  public void cropInsideCircle(int cx, int cy, int radius) {
 
    Rectangle toCrop = new Rectangle();
    for (int angle = 0;angle < 90;angle+=5) {
      int dx = (int)(Math.cos(angle * Math.PI / 180) * radius);
      int dy = (int)(Math.sin(angle * Math.PI / 180) * radius);
      
      toCrop.set(cx-dx, cy-dy, 2*dx, 2*dy);
      cropInRectangle(toCrop);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(height, width, x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Rectangle other = (Rectangle) obj;
    return height == other.height && width == other.width && x == other.x && y == other.y;
  }

  public void cropOutsideCircle(int cx, int cy, int radius) {
    int xMin = Integer.MAX_VALUE;
    int xMax = Integer.MIN_VALUE ;
    int yMin = Integer.MAX_VALUE;
    int yMax = Integer.MIN_VALUE;
    
    if (cy >= this.y && cy <= this.y + this.height) {
      xMin = (int)(Math.max(x, cx - radius));
      xMax = (int)(Math.min(x+width, cx + radius));
    }
    
    if (cx >= this.x && cx <= this.x + this.width) {
      yMin = (int)(Math.max(y, cy - radius));
      yMax = (int)(Math.min(y+height, cy + radius));
    }
    
    int deltaY = Math.abs(cy - y);
    if (deltaY <= radius) {
      double dx = Math.sqrt(radius * radius - deltaY * deltaY);
      if (cx - dx >= this.x && cx-dx <= this.x + this.width) {
        xMin = (int)Math.min(xMin, cx - dx);
      }
      if (cx + dx >= this.x && cx+dx <= this.x + this.width) {
        xMax = (int)Math.max(xMax, cx + dx);
      }
    }    
    
    deltaY = Math.abs(cy - (y+height));
    if (deltaY <= radius) {
      double dx = Math.sqrt(radius * radius - deltaY * deltaY);
      if (cx - dx >= this.x && cx-dx <= this.x + this.width) {
        xMin = (int)Math.min(xMin, cx - dx);
      }
      if (cx + dx >= this.x && cx+dx <= this.x + this.width) {
        xMax = (int)Math.max(xMax, cx + dx);
      }
    }    

    int deltaX = Math.abs(cx - x);
    if (deltaX <= radius) {
      double dy = Math.sqrt(radius * radius - deltaX * deltaX);
      if (cy - dy >= this.y && cy-dy <= this.y + this.height) {
        yMin = (int)Math.min(yMin, cy - dy);
      }
      if (cy + dy >= this.y && cy+dy <= this.y + this.height) {
        yMax = (int)Math.max(yMax, cy + dy);
      }
    }    
    
    deltaX = Math.abs(cx - (x+width));
    if (deltaX <= radius) {
      double dy = Math.sqrt(radius * radius - deltaX * deltaX);
      if (cy - dy >= this.y && cy-dy <= this.y + this.height) {
        yMin = (int)Math.min(yMin, cy - dy);
      }
      if (cy + dy >= this.y && cy+dy <= this.y + this.height) {
        yMax = (int)Math.max(yMax, cy + dy);
      }
    }    

    if (xMin == Integer.MAX_VALUE) xMin = cx-radius;
    if (xMax == Integer.MIN_VALUE) xMax = cx+radius;
    if (yMin == Integer.MAX_VALUE) yMin = cy-radius;
    if (yMax == Integer.MIN_VALUE) yMax = cy+radius;

    Rectangle work = new Rectangle(xMin, yMin, xMax-xMin, yMax-yMin);
    this.intersect(work);
  }

  public void vSymmetry() {
    this.x = Simulator.WIDTH - this.x - this.width;
  }

  /**
   * Expand the rectangle from the center to each point with length 
   * @param center
   * @param length
   */
  public void expandFromPos(Pos center, int length) {
    int xMin = x;
    int xMax = x+width;
    int yMin = y;
    int yMax = y + height;
    
    Pos current;
    Vec direction;

    current = new Pos(x,y);
    direction = new Vec(current, center);
    direction.normalize(length);
    xMin = Math.max(0, Math.min(xMin,  current.x + direction.vx));
    xMax = Math.min(Simulator.WIDTH, Math.max(xMax,  current.x + direction.vx));
    yMin = Math.max(0, Math.min(yMin,  current.y + direction.vy));
    yMax = Math.min(Simulator.HEIGHT, Math.max(yMax,  current.y + direction.vy));
    
    current = new Pos(x+width,y);
    direction = new Vec(current, center);
    direction.normalize(length);
    xMin = Math.max(0, Math.min(xMin,  current.x + direction.vx));
    xMax = Math.min(Simulator.WIDTH, Math.max(xMax,  current.x + direction.vx));
    yMin = Math.max(0, Math.min(yMin,  current.y + direction.vy));
    yMax = Math.min(Simulator.HEIGHT, Math.max(yMax,  current.y + direction.vy));
    
    current = new Pos(x,y+height);
    direction = new Vec(current, center);
    direction.normalize(length);
    xMin = Math.max(0, Math.min(xMin,  current.x + direction.vx));
    xMax = Math.min(Simulator.WIDTH, Math.max(xMax,  current.x + direction.vx));
    yMin = Math.max(0, Math.min(yMin,  current.y + direction.vy));
    yMax = Math.min(Simulator.HEIGHT, Math.max(yMax,  current.y + direction.vy));

    current = new Pos(x+width,y+height);
    direction = new Vec(current, center);
    direction.normalize(length);
    xMin = Math.max(0, Math.min(xMin,  current.x + direction.vx));
    xMax = Math.min(Simulator.WIDTH, Math.max(xMax,  current.x + direction.vx));
    yMin = Math.max(0, Math.min(yMin,  current.y + direction.vy));
    yMax = Math.min(Simulator.HEIGHT, Math.max(yMax,  current.y + direction.vy));

    this.x = xMin;
    this.width = xMax - xMin;
    this.y = yMin;
    this.height= yMax - yMin;
  }

  /**
   * return a rectangle that englobe this & other
   * @param intersection
   */
  public void maxOf(Rectangle other) {
    int xMin = Math.min(x, other.x);
    int xMax = Math.max(x+width, other.x + other.width);
    int yMin = Math.min(y, other.y);
    int yMax = Math.max(y + height, other.y + other.height);
    
    this.x = xMin;
    this.width = xMax - xMin;
    this.y = yMin;
    this.height= yMax - yMin;
  }
  

  
}
