package tge;

import java.util.Scanner;

public class Player {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int w = in.nextInt(); // width of the board
    int h = in.nextInt(); // height of the board
    int playerCount = in.nextInt(); // number of players (2 or 3)
    int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)

    // game loop
    while (true) {
        for (int i = 0; i < playerCount; i++) {
            int x = in.nextInt(); // x-coordinate of the player
            int y = in.nextInt(); // y-coordinate of the player
            int wallsLeft = in.nextInt(); // number of walls available for the player
        }
        int wallCount = in.nextInt(); // number of walls on the board
        for (int i = 0; i < wallCount; i++) {
            int wallX = in.nextInt(); // x-coordinate of the wall
            int wallY = in.nextInt(); // y-coordinate of the wall
            String wallOrientation = in.next(); // wall orientation ('H' or 'V')
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");


        // action: LEFT, RIGHT, UP, DOWN or "putX putY putOrientation" to place a wall
        System.out.println("RIGHT");
    }
}
}
