package ww.paths;

import cgcollections.arrays.FastArray;
import ww.Cell;

public class VoronoiInfo {
  FastArray<Cell> cellsList = new FastArray<>(Cell.class, 100);
  FastArray<Cell> nextCellsList = new FastArray<>(Cell.class, 100);
}
