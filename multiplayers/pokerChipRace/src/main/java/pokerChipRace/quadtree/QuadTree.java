package pokerChipRace.quadtree;

import cgcollections.arrays.FastArray;
import pokerChipRace.entities.Entity;

public class QuadTree {
  public static final int MAX_LEVEL = 3;
  
  QuadTree parent= null;
  QuadTree nodes[] = null;
  FastArray<Entity> values = new FastArray<>(Entity.class, 200);
  int level;
  double x0,y0,x1,y1;
  
  public QuadTree(double x0, double y0, double x1, double y1) {
    this(null, 0, x0, y0, x1, y1);
  }
  QuadTree(QuadTree parent, int level, double x0, double y0, double x1, double y1) {
    this.parent = parent;
    this.level = level;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    if (level < MAX_LEVEL) {
      double centerX = (x0+x1)/2;
      double centerY = (y0+y1)/2;
      
      nodes = new QuadTree[4];
      nodes[0] = new QuadTree(this, level+1, x0,y0, centerX, centerY);
      nodes[1] = new QuadTree(this, level+1, centerX,y0, x1, centerY);
      nodes[2] = new QuadTree(this, level+1, x0,centerY, centerX, y1);
      nodes[3] = new QuadTree(this, level+1, centerX,centerY, x1, y1);
    }
  }
  
  public QuadTree parent() {
    return parent;
  }
  
  public FastArray<Entity> values() {
    return values;
  }
  
  /** insert into the subtree, 
   * return false if it cant hold it 
   * else try
   * */
  public boolean insert(Entity entity) {
    boolean fitted = true;
    if (entity.x < x0 && entity.x > x1) { fitted = false; }
    if (entity.y < y0 && entity.y > y1) { fitted = false; }
    if (entity.x+entity.vx < x0 && entity.x+entity.vx > x1) { fitted = false; }
    if (entity.y+entity.vy < y0 && entity.y+entity.vy > y1) { fitted = false; }

    if (!fitted) {
      if (level == 0) {
        values.add(entity);
        return true;
      }
      return false;
    }
    QuadTree child = getPotentialChild(entity);
    if (child == null || !child.insert(entity)) {
      values.add(entity);
    }
    return true;
  }

  /** 
   * get all entities that CAN collide with entity
   * @param list (ensure it is large enough
   * @param e
   */
  public void retrieve(FastArray<Entity> list, Entity entity) {
    for (int i=0;i<values.length;i++) {
      list.add(values.elements[i]);
    }
    QuadTree qt = getPotentialChild(entity);
    if (qt != null) {
      qt.retrieve(list, entity);
    }
  }
  
  public QuadTree search(Entity entity) {
    if (nodes == null) {
      return findInValues(entity);
    } else {
      QuadTree qt = getPotentialChild(entity);
      if (qt == null) {
        return findInValues(entity);
      } else {
        qt = qt.search(entity);
        if (qt == null) {
          return findInValues(entity);
        }
        return qt;
      }
    }
  }
  private QuadTree findInValues(Entity e) {
    for (int i=0;i<values.length;i++) {
      if (values.elements[i] == e) {
        return this;
      }
    }
    return null; // not found
  }
  
  public void clear() {
    values.clear();
    if (nodes != null) {
      nodes[0].clear();
      nodes[1].clear();
      nodes[2].clear();
      nodes[3].clear();
    }
  }
  
  QuadTree getPotentialChild(Entity e) {
    if (nodes == null) return null;
    
    int xi = e.x <= (x0+x1) / 2 ? 0 : 1;
    int yi = e.y <= (y0+y1) / 2 ? 0 : 1;

    int xxi = (e.x+e.vx) <= (x0+x1) / 2 ? 0 : 1;
    int yyi = (e.y+e.vy) <= (y0+y1) / 2 ? 0 : 1;
    
    if (xi != xxi || yi != yyi) {
      return null;
    } else {
      return nodes[xi+2*yi];
    }
  }
  
}
