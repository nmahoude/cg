package cotc.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CubeCoordinate {
  @SafeVarargs
  static final <T> String join(T... v) {
      return Stream.of(v).map(String::valueOf).collect(Collectors.joining(" "));
  }

  static int[][] directions = new int[][] { { 1, -1, 0 }, { +1, 0, -1 }, { 0, +1, -1 }, { -1, +1, 0 }, { -1, 0, +1 }, { 0, -1, +1 } };
  int x, y, z;

  public CubeCoordinate(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
  }

  Coord toOffsetCoordinate() {
      int newX = x + (z - (z & 1)) / 2;
      int newY = z;
      return new Coord(newX, newY);
  }

  CubeCoordinate neighbor(int orientation) {
      int nx = this.x + directions[orientation][0];
      int ny = this.y + directions[orientation][1];
      int nz = this.z + directions[orientation][2];

      return new CubeCoordinate(nx, ny, nz);
  }

  int distanceTo(CubeCoordinate dst) {
      return (Math.abs(x - dst.x) + Math.abs(y - dst.y) + Math.abs(z - dst.z)) / 2;
  }

  @Override
  public String toString() {
      return join(x, y, z);
  }

}
