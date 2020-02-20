package trigonometry;

/**
 * Oriented bounding box 2D
 * 
 * @author nmahoude
 *
 * @see http://www.flipcode.com/archives/2D_OBB_Intersection.shtml
 */
public class OBB2D {
  Vector corner[] = new Vector[4];
  Vector axis[] = new Vector[2];
  double origin[] = new double[2];

  public OBB2D(Point start, Point end, double radius) {
   this(start, end.sub(start), radius);
  }

  @Override
  public String toString() {
    return ""+corner[0]+"\n"
             +corner[1]+"\n"
             +corner[2]+"\n"
             +corner[3]+"\n";
  }

  public OBB2D(Point start, Vector dir, double radius) {
    Vector axis1 = dir;
    Vector axis2 = new Vector(axis1.vy, -axis1.vx).normalize().dot(radius);
    
    Vector p1 = new Vector(start.x, start.y);
    Vector p2 = new Vector(start.x+dir.vx, start.y+dir.vy);
    
    corner[0] = p1.add(axis2);
    corner[1] = p2.add(axis2);
    corner[2] = p2.sub(axis2);
    corner[3] = p1.sub(axis2);
    computeAxes();
  }

  public OBB2D(Vector corners[]) {
    corner[0] = corners[0];
    corner[1] = corners[1];
    corner[2] = corners[2];
    corner[3] = corners[3];
    computeAxes();
  }
  
  /** Returns true if other overlaps one dimension of this. */
  boolean overlaps1Way(final OBB2D other) {
      for (int a = 0; a < 2; ++a) {
          double t = other.corner[0].dot(axis[a]);

          // Find the extent of box 2 on axis a
          double tMin = t;
          double tMax = t;

          for (int c = 1; c < 4; ++c) {
              t = other.corner[c].dot(axis[a]);

              if (t < tMin) {
                  tMin = t;
              } else if (t > tMax) {
                  tMax = t;
              }
          }

          // We have to subtract off the origin

          // See if [tMin, tMax] intersects [0, 1]
          if ((tMin > 1 + origin[a]) || (tMax < origin[a])) {
              // There was no intersection along this dimension;
              // the boxes cannot possibly overlap.
              return false;
          }
      }

      // There was no dimension along which there is no intersection.
      // Therefore the boxes overlap.
      return true;
  }


  /** Updates the axes after the corners move.  Assumes the
      corners actually form a rectangle. */
  private void computeAxes() {
      axis[0] = corner[1].sub(corner[0]); 
      axis[1] = corner[3].sub(corner[0]); 

      // Make the length of each axis 1/edge length so we know any
      // dot product must be less than 1 to fall within the edge.

      for (int a = 0; a < 2; ++a) {
        double ratio = 1.0 / axis[a].squareLength();
          axis[a] = axis[a].dot(ratio);
          origin[a] = corner[0].dot(axis[a]);
      }
  }

  /** Returns true if the intersection of the boxes is non-empty. */
  public boolean overlaps(final OBB2D other) {
      return overlaps1Way(other) && other.overlaps1Way(this);
  }

}
