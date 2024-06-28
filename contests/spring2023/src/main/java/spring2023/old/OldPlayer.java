package spring2023.old;

//Modified version of built on 2023-05-29T01:16:20.954+02:00[Europe/Paris]

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class FastReader {

  private static final int BUFFER_SIZE = 1 << 16;

  private DataInputStream din;

  private byte[] buffer;

  private int bufferPointer, bytesRead;

  public FastReader() {
    this(System.in);
  }

  public FastReader(InputStream in) {
    din = new DataInputStream(System.in);
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public FastReader(byte inputs[]) {
    buffer = new byte[inputs.length];
    System.arraycopy(inputs, 0, buffer, 0, inputs.length);
    bufferPointer = 0;
    bytesRead = inputs.length;
  }

  public static FastReader fromString(String input) {
    return new FastReader(input.getBytes());
  }

  public static FastReader fromFile(String filename) throws IOException {
    FastReader reader = new FastReader();
    reader.din = new DataInputStream(new FileInputStream(filename));
    reader.buffer = new byte[BUFFER_SIZE];
    reader.bufferPointer = reader.bytesRead = 0;
    return reader;
  }

  public String readLine() {
    byte[] buf = new byte[64];
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n')
        break;
      buf[cnt++] = (byte) c;
    }
    return new String(buf, 0, cnt);
  }

  public int nextInt() {
    int ret = 0;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();
    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');
    if (neg)
      return -ret;
    return ret;
  }

  public long nextLong() {
    long ret = 0;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();
    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');
    if (neg)
      return -ret;
    return ret;
  }

  public double nextDouble() {
    double ret = 0, div = 1;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();
    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');
    if (c == '.') {
      while ((c = read()) >= '0' && c <= '9') {
        ret += (c - '0') / (div *= 10);
      }
    }
    if (neg)
      return -ret;
    return ret;
  }

  private void fillBuffer() {
    try {
      bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
      if (bytesRead == -1)
        buffer[0] = -1;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private byte read() {
    if (bufferPointer == bytesRead)
      fillBuffer();
    return buffer[bufferPointer++];
  }

  public void close() {
    if (din == null)
      return;
    try {
      din.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String nextString() {
    return next();
  }

  public String next() {
    byte c;
    StringBuilder sBuf = new StringBuilder(64);
    do {
      c = read();
    } while (c <= ' ');
    do {
      if (c == '\n' || c == ' ')
        break;
      sBuf.append((char) c);
    } while ((c = read()) != -1);
    return sBuf.toString();
  }

  public String nextLine() {
    byte c;
    StringBuilder sBuf = new StringBuilder(64);
    do {
      c = read();
    } while (c <= ' ');
    do {
      if (c == '\n')
        break;
      sBuf.append((char) c);
    } while ((c = read()) != -1);
    return sBuf.toString();
  }

  public void nextLinePass() {
    byte c;
    do {
      c = read();
    } while (c <= ' ');
    do {
      if (c == '\n')
        break;
    } while ((c = read()) != -1);
  }

  public byte nextByte() {
    return nextBytes()[0];
  }

  public byte[] nextBytes() {
    byte[] buf = new byte[64];
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n' || c == ' ')
        break;
      buf[cnt++] = (byte) c;
    }
    return buf;
  }

  public char[] nextChars() {
    char[] buf = new char[64];
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n' || c == ' ') {
        buf[cnt++] = '\n';
        break;
      } else {
        buf[cnt++] = (char) c;
      }
    }
    buf[cnt++] = '\n';
    return buf;
  }
}

class Cell {

  public int x = Integer.MIN_VALUE;

  public int y = Integer.MIN_VALUE;

  public static final Cell VOID = new Cell(-1);

  public final int index;

  public int type;

  public int resources;

  int length;

  public List<Cell> neighbors = new ArrayList<>();

  public Cell[] neighborsArray = new Cell[6];

  public int myAnts;

  public int vAnts;

  public int oppAnts;

  public boolean isMyBase;

  public boolean isOppBase;

  public int beacon;

  Cell(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return "[" + index + "]";
  }
}

class Path {

  Cell origin;

  Cell target;

  List<Cell> path = new ArrayList<>();

  public Path(Cell source, Cell cell) {
    this.origin = source;
    this.target = cell;
  }
}

class Map {

  public static final int MAX_CELLS = 125;

  public static final int MAX_DIST = 125;

  public static Cell cells[];

  public static int distances[][] = new int[MAX_CELLS][MAX_CELLS];

  public static Path paths[][] = new Path[MAX_CELLS][MAX_CELLS];

  public static void init(int numberOfCells) {
    cells = new Cell[numberOfCells];
    for (int i = 0; i < numberOfCells; i++) {
      cells[i] = new Cell(i);
    }
  }

  public static void calculateBaseDistances() {
    for (Cell source : cells) {
      for (Cell cell : cells) {
        distances[source.index][cell.index] = Integer.MAX_VALUE;
        paths[source.index][cell.index] = new Path(source, cell);
      }
      List<Cell> toVisit = new ArrayList<>();
      List<Cell> visited = new ArrayList<>();
      toVisit.add(source);
      distances[source.index][source.index] = 0;
      int length = 0;
      while (!toVisit.isEmpty()) {
        Set<Cell> nextLayerToVisit = new HashSet<>();
        length++;
        while (!toVisit.isEmpty()) {
          Cell current = toVisit.remove(0);
          visited.add(current);
          for (Cell neighbor : current.neighbors) {
            if (toVisit.contains(neighbor))
              continue;
            if (visited.contains(neighbor))
              continue;
            nextLayerToVisit.add(neighbor);
            distances[source.index][neighbor.index] = length;
            reconstructPath(source, neighbor, length, paths[source.index][neighbor.index].path);
          }
        }
        toVisit.clear();
        toVisit.addAll(nextLayerToVisit);
      }
    }
  }

  private static void reconstructPath(Cell source, Cell end, int length, List<Cell> path) {
    Cell current = end;
    path.add(end);
    int l = distances[source.index][current.index];
    while (l != 0) {
      for (Cell n : current.neighbors) {
        if (distances[source.index][n.index] == l - 1) {
          l--;
          current = n;
          path.add(0, n);
          break;
        }
      }
    }
  }

  public static List<Cell> bestPath(Cell source, Cell target) {
    return paths[source.index][target.index].path;
  }
}

class Action {

  static Action actions[] = new Action[Map.MAX_CELLS];

  static {
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      actions[i] = new Action(i);
    }
  }

  public static void reset() {
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      actions[i].strength = 0;
    }
  }

  final int index;

  int strength;

  public Action(int index) {
    this.index = index;
    this.strength = 0;
  }

  public String debugString() {
    return "BEACON " + index + " " + strength;
  }

  public static Action get(int i) {
    return actions[i];
  }

  public Action withStrength(int s) {
    this.strength = s;
    return this;
  }
}

interface AStarCost {

  public double costOf(Cell current, Cell next);

  public double ofStart(Cell start);
}

class SimpleCost implements AStarCost {

  @Override
  public double ofStart(Cell start) {
    return 0;
  }

  public double costOf(Cell current, Cell next) {
    if (next.vAnts > 0) {
      return 0.1 - 0.0001 * next.vAnts;
    } else if (next.resources > 0) {
      return 0.9;
    } else {
      return 1.0;
    }
  }
}

class AStar {

  Cell cameFrom[] = new Cell[Map.MAX_CELLS];

  double gScore[] = new double[Map.MAX_CELLS];

  double fScore[] = new double[Map.MAX_CELLS];

  public List<Cell> path = new ArrayList<>();

  private double bestScore;

  Set<Cell> starts = new HashSet<>(5);

  public double search(Cell start, Cell target) {
    return search(new SimpleCost(), start, target);
  }

  public double search(AStarCost cost, Cell start, Cell target) {
    starts.clear();
    starts.add(start);
    return search(cost, starts, target);
  }

  public double search(Set<Cell> starts, Cell target) {
    return search(new SimpleCost(), starts, target);
  }

  public double search(AStarCost costFunction, Set<Cell> starts, Cell target) {
    reset();
    PriorityQueue<Cell> openSet = new PriorityQueue<>((o1, o2) -> Double.compare(fScore[o1.index], fScore[o2.index]));
    for (Cell start : starts) {
      gScore[start.index] = costFunction.ofStart(start);
      fScore[start.index] = 2 * Map.distances[start.index][target.index];
      openSet.add(start);
    }
    while (!openSet.isEmpty()) {
      Cell current = openSet.poll();

      if (current == target) {
        rebuildPath(target);
      }
      for (Cell next : current.neighbors) {
        double tentativeGScore = gScore[current.index] + costFunction.costOf(current, next);
        if (tentativeGScore < gScore[next.index]) {
          gScore[next.index] = tentativeGScore;
          fScore[next.index] = gScore[next.index] + 2 * Map.distances[next.index][target.index];
          cameFrom[next.index] = current;
          openSet.remove(next);
          openSet.add(next);
        }
      }
    }
    return bestScore;
  }

  private void rebuildPath(Cell target) {
    path.clear();
    path.add(target);
    Cell current = target;
    while (cameFrom[current.index] != null) {
      path.add(0, cameFrom[current.index]);
      current = cameFrom[current.index];
    }
    bestScore = fScore[target.index];
  }

  private void reset() {
    bestScore = Double.MAX_VALUE;
    path.clear();
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      cameFrom[i] = null;
      gScore[i] = Double.MAX_VALUE;
      fScore[i] = Double.MAX_VALUE;
    }
  }
}

class MyMaximumAnts implements AStarCost {

  @Override
  public double costOf(Cell current, Cell next) {
    return 1.0 / next.myAnts;
  }

  @Override
  public double ofStart(Cell start) {
    return 0;
  }
}

class OppMaximumAnts implements AStarCost {

  @Override
  public double costOf(Cell current, Cell next) {
    return 1.0 / next.oppAnts;
  }

  @Override
  public double ofStart(Cell start) {
    return 0;
  }
}

public class OldPlayer {

  public static State state = new State();

  public static int turn = 0;

  public static AI3 ai = new AI3();

  public static boolean LOCAL_DEBUG = false;

  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);
    new OldPlayer().play(in);
  }

  int beacons[] = new int[Map.MAX_CELLS];

  private void play(FastReader in) {
    State.readInit(in);
    while (true) {
      turn++;
      state.read(in);
      resetBeacons();
      long start = System.currentTimeMillis();
      ai.think(state, beacons);
      long end = System.currentTimeMillis();
      System.err.println("Thinking in " + (end - start) + " ms");
      String output = "";
      for (Cell c : Map.cells) {
        if (beacons[c.index] > 0) {
          output += "BEACON " + c.index + " " + beacons[c.index] + ";";
        }
      }
      if (output.isEmpty()) {
        System.out.println("WAIT");
      } else {
        System.out.println(output);
      }
    }
  }

  private void resetBeacons() {
    Action.reset();
    for (int i = 0; i < Map.MAX_CELLS; i++) {
      beacons[i] = 0;
    }
  }
}
