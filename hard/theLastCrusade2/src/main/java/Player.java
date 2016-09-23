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
  static class RoomShape {
    static RoomShape rooms[];
    static {
      rooms = new RoomShape[13+1];
      rooms[0] = new RoomShape(0);
      rooms[1] = new RoomShape(1, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.LEFT, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[2] = new RoomShape(2, new Path(Path.Exit.LEFT, Path.Exit.RIGHT), new Path(Path.Exit.RIGHT, Path.Exit.LEFT));
      rooms[3] = new RoomShape(3, new Path(Path.Exit.TOP, Path.Exit.DOWN));
      rooms[4] = new RoomShape(4, new Path(Path.Exit.TOP, Path.Exit.LEFT), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[5] = new RoomShape(5, new Path(Path.Exit.TOP, Path.Exit.RIGHT), new Path(Path.Exit.LEFT, Path.Exit.DOWN));
      rooms[6] = new RoomShape(6, new Path(Path.Exit.LEFT, Path.Exit.RIGHT), new Path(Path.Exit.RIGHT, Path.Exit.LEFT));
      rooms[7] = new RoomShape(7, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[8] = new RoomShape(8, new Path(Path.Exit.LEFT, Path.Exit.DOWN), new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[9] = new RoomShape(9, new Path(Path.Exit.TOP, Path.Exit.DOWN), new Path(Path.Exit.LEFT, Path.Exit.DOWN));
      rooms[10]= new RoomShape(10, new Path(Path.Exit.TOP, Path.Exit.LEFT));
      rooms[11]= new RoomShape(11, new Path(Path.Exit.TOP, Path.Exit.RIGHT));
      rooms[12]= new RoomShape(12, new Path(Path.Exit.RIGHT, Path.Exit.DOWN));
      rooms[13]= new RoomShape(13 ,new Path(Path.Exit.LEFT, Path.Exit.DOWN));
    }

    List<Path> paths = new ArrayList<>();
    int index;
    
    public RoomShape(int index, Path... paths ) {
      this.index = index;
      for (Path p : paths) {
        this.paths.add(p);
      }
    }

    public static RoomShape get(int roomIndex) {
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
  static class Room {
    RoomShape shape;
    final boolean canMove;
    final int x;
    final int y;
    
    public Room(int x, int y, int shape, boolean canMove) {
      super();
      this.x = x;
      this.y = y;
      this.shape = RoomShape.get(shape);
      this.canMove = canMove;
    }
  }

  static class SimulationStepData {
    Room[][] map;
    int XI, YI;
    String pos;
  }
  static class SimulationStep {
    String command;
    
    SimulationStep(SimulationStepData data) {
      Room currentRoom = data.map[data.XI][data.YI];
      Path exitPath = currentRoom.shape.exitPathFrom(data.pos);
      
      int forseenX = data.XI+exitPath.to.getDeltaX();
      int forseenY = data.YI+exitPath.to.getDeltaY();
    }
  }
  
  
  public static void main(String args[]) {
    Room[][] map;
    List<Room> movingRooms = new ArrayList<>();
    
    Scanner in = new Scanner(System.in);
    int width = in.nextInt(); // number of columns.
    int height = in.nextInt(); // number of rows.
    
    map = new Room[width][height];
    
    in.nextLine();
    for (int y = 0; y < height; y++) {
      String oneRow = in.nextLine();
      String[] roomsIndex = oneRow.split(" ");
      for (int x=0;x<width;x++) {
        map[x][y] = new Room(x, y, Math.abs(Integer.parseInt(roomsIndex[x])), Integer.parseInt(roomsIndex[x]) > 0);
        if (Integer.parseInt(roomsIndex[x]) > 0) {
          movingRooms.add(map[x][y]);
        }
      }
    }
    // upper rooms first
    sortMovableRooms(movingRooms);
    
    int EX = in.nextInt(); // the coordinate along the X axis of the exit (not useful for this first mission, but must be read).

    // game loop
    while (true) {
      int XI = in.nextInt();
      int YI = in.nextInt();
      String POS = in.next();
      
      int R = in.nextInt(); // the number of rocks currently in the grid.
      for (int i = 0; i < R; i++) {
          int XR = in.nextInt();
          int YR = in.nextInt();
          String POSR = in.next();
      }
      
      Room currentRoom = map[XI][YI];
      Path exitPath = currentRoom.shape.exitPathFrom(POS);
      int x = XI+exitPath.to.getDeltaX();
      int y = YI+exitPath.to.getDeltaY();
      
      System.out.println(""+x+" "+y);
    }
  }


  private static void sortMovableRooms(List<Room> movingRooms) {
    Collections.sort(movingRooms, new Comparator<Room>() {
      @Override
      public int compare(Room room1, Room room2) {
        if (room1.y != room2.y) {
          return Integer.compare(room1.y, room2.y);
        } else {
          return Integer.compare(room1.x, room2.x);
        }
      }
    });
  }
}