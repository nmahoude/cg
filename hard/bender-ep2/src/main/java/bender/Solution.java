package bender;

import java.util.Scanner;

public class Solution {
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt();
    Room.rooms = new Room[N];
    for (int i=0;i<N;i++) {
      Room.rooms[i] = new Room(i);
    }
    if (in.hasNextLine()) {
      in.nextLine();
    }
    for (int i = 0; i < N; i++) {
      Room.rooms[i].read(in);
    }

    Room benderIn = Room.rooms[0];
    int maxMoney = exploreRooms(benderIn, 0);
    System.out.println(""+maxMoney);
  }

  private static int exploreRooms(Room benderIn, int current) {
    if (benderIn == Room.exit) {
      return 0;
    }
    int max = current + benderIn.money;
    if (benderIn.incomming > max) return max;
    benderIn.incomming = max;
    benderIn.visited = true;
    max = Math.max(max, 
                  Math.max(!benderIn.door1.visited ? exploreRooms(benderIn.door1, max) : 0, 
                   !benderIn.door2.visited ? exploreRooms(benderIn.door2, max) : 0)); 
    benderIn.visited = false;
    return max;
  }
}
