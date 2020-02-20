import java.util.*;
import java.io.*;
import java.math.*;

class Player {

  static class Path {
    enum Exit {
      TOP {
        @Override public int getDeltaX() { return 0; }
        @Override public int getDeltaY() { return -1;}
      }, 
      DOWN {
        @Override public int getDeltaX() { return 0; }
        @Override public int getDeltaY() { return 1;}
      },
      RIGHT {
        @Override public int getDeltaX() { return 1; }
        @Override public int getDeltaY() { return 0; }
      },
      LEFT {
        @Override public int getDeltaX() { return -1; }
        @Override public int getDeltaY() { return 0; }
      };

      public abstract int getDeltaX();

      public abstract int getDeltaY();
    }
    Exit from, to;
    Path(Exit from, Exit to) {
      this.from = from;
      this.to = to;
    }
  }
  static class Room {
    static Room rooms[];
    static {
      rooms = new Room[13+1];
      rooms[0] = new Room(0);
      rooms[1] = new Room(1, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.LEFT, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[2] = new Room(2, new Path(Path.Exit.LEFT, Path.Exit.RIGHT), new Path(Path.Exit.RIGHT, Path.Exit.LEFT));
      rooms[3] = new Room(3, new Path(Path.Exit.TOP, Path.Exit.DOWN));
      rooms[4] = new Room(4, new Path(Path.Exit.TOP, Path.Exit.LEFT), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[5] = new Room(5, new Path(Path.Exit.TOP, Path.Exit.RIGHT), new Path(Path.Exit.LEFT, Path.Exit.DOWN));
      rooms[6] = new Room(6, new Path(Path.Exit.LEFT, Path.Exit.RIGHT), new Path(Path.Exit.RIGHT, Path.Exit.LEFT));
      rooms[7] = new Room(7, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[8] = new Room(8, new Path(Path.Exit.LEFT, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[9] = new Room(9, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.LEFT, Path.Exit.DOWN));
      rooms[10]= new Room(10, new Path(Path.Exit.TOP, Path.Exit.LEFT));
      rooms[11]= new Room(11, new Path(Path.Exit.TOP, Path.Exit.RIGHT));
      rooms[12]= new Room(12, new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[13]= new Room(13 ,new Path(Path.Exit.LEFT, Path.Exit.DOWN));
    }

    List<Path> paths = new ArrayList<>();
    int index;
    
    public Room(int index, Path... paths ) {
      this.index = index;
      for (Path p : paths) {
        this.paths.add(p);
      }
    }

    public static Room get(int roomIndex) {
      return rooms[roomIndex];
    }

    public Path exitPathFrom(String pos) {
      for (Path p : paths) {
        if (p.from.toString().equals(pos)) {
          return p;
        }
      }
      return null; // TODO ooups
    }
  }

  static Room[][] map;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int width = in.nextInt(); // number of columns.
    int height = in.nextInt(); // number of rows.
    
    map = new Room[width][height];
    
    in.nextLine();
    for (int y = 0; y < height; y++) {
      String oneRow = in.nextLine();
      String[] roomsIndex = oneRow.split(" ");
      for (int x=0;x<width;x++) {
        map[x][y] = Room.get(Integer.parseInt(roomsIndex[x]));
      }
    }
    int EX = in.nextInt(); // the coordinate along the X axis of the exit (not useful for this first mission, but must be read).

    // game loop
    while (true) {
      int XI = in.nextInt();
      int YI = in.nextInt();
      String POS = in.next();

      Room currentRoom = map[XI][YI];
      Path exitPath = currentRoom.exitPathFrom(POS);
      int x = XI+exitPath.to.getDeltaX();
      int y = YI+exitPath.to.getDeltaY();
      
      System.out.println(""+x+" "+y);
    }
  }
}