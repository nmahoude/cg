package fall2023;

public interface Zone {

  void reset();
  void expand(int range);
  boolean intersect(Rectangle rectangle);
  void exact(int creatureX, int creatureY);
  void cropOutsideCircle(int x, int y, int radius);
  void cropInsideCircle(int x, int y, int radius);
  boolean hasPotentialIntersectionWithCircle(int x, int y, int radius);
  int surface();
  Pos center();
  
  void copyFrom(Zone zone);
  void unpack(long nextLong);
  long pack();
  boolean contains(Pos pos);
  double surfaceRatio(Pos pos, int lightRadius);
  Zone newInstance();
  void maxOf(Rectangle intersection);

  
}
