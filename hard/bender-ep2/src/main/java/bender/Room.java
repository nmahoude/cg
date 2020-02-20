package bender;
import java.util.Scanner;

public class Room {
  public static final Room exit = createExit();
  public static Room[] rooms;
  int number;
  Room door1;
  Room door2;
  int money;
  boolean visited = false;
  public int incomming = 0;

  public Room(int number) {
    this.number = number;
  }

  private static Room createExit() {
    Room room = new Room(-1);
    room.door1 = exit;
    room.door2 = exit;
    room.money = 0;
    return room;
  }

  public void read(Scanner in) {
    number = in.nextInt();
    money = in.nextInt();
    
    String room1 = in.next();
    String room2 = in.next();
    if ("E".equals(room1)) {
      this.door1 = exit; 
    } else {
      this.door1 = rooms[Integer.parseInt(room1)]; 
    }
    if ("E".equals(room2)) {
      this.door2 = exit; 
    } else {
      this.door2 = rooms[Integer.parseInt(room2)]; 
    }
  }
}
