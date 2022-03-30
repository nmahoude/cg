

import java.util.Scanner;

class Player {
  static int turn = 0;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    // System.out.println("hunegleelvyyjrnjdrvrtmeqsijfizbh" );
    // turn = 4;

    System.out.println("wmbmfvnydvsyekxxghxbnihqgqwlahtt");
    turn = 7-1;

    // game loop
    while (true) {
      turn ++;
        int width = in.nextInt();
        int height = in.nextInt();
        System.err.println(width+ " "+height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cell = in.nextInt();
                System.err.print(cell);
                System.err.print(" ");
            }
            System.err.println();
        }

        switch(turn) {
        case 1: 
          System.out.println("7 4 L +");
          System.out.println("3 0 D -");
          System.out.println("6 4 L -");
          break;
        case 2:
          System.out.println("4 2 D -");
          System.out.println("4 4 L -");
          System.out.println("7 4 L -");
          break;
        case 3:
          System.out.println("1 4 U -");
          System.out.println("1 2 D -");
          System.out.println("1 0 D -");
          System.out.println("3 3 L -");
          break;
        case 4:
            o(3,0,"D","-");
            o(6,2,"L", "-");
            o(2,2,"R","-");
            o(5,2,"L","-");
            break;
        case 5:
            o(2,0,"R","-");
            o(0,2,"R", "-");
            o(3,3,"R", "+");
            o(4,2,"D","+");
            o(4,3,"U","-");
            break;
        case 6:
                  System.out.println("4 1 R -\r\n"
              + "1 4 L -\r\n"
              + "2 4 U -\r\n"
              + "3 0 D -\r\n"
              + "3 3 U -");
            break;
        case 7:
          System.out.println("0 3 R -\r\n"
              + "3 3 L -\r\n"
              + "2 3 R -\r\n"
              + "6 0 D -\r\n"
              + "3 1 R -\r\n"
              + "2 1 R -");
          break;
        case 8:
            System.out.println("5 1 L -\r\n"
                + "3 1 R +\r\n"
                + "4 0 D -\r\n"
                + "0 1 R -\r\n"
                + "1 1 R -\r\n"
                + "1 0 D -");
            break;
        case 9:
            System.out.println("4 1 R +\r\n"
                + "2 1 L +\r\n"
                + "0 1 R -\r\n"
                + "4 3 R +\r\n"
                + "5 3 L -\r\n"
                + "5 2 U -\r\n"
                + "6 0 L -");
            break;
          default:
          System.out.println("0 0 U +");
          break;
        }
    }
}
    private static void o(int x, int y, String dir, String op) {
        System.out.println(x+" "+y+" "+dir+" "+op);
    }
}
