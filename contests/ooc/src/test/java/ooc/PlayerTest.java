package ooc;

import java.util.Scanner;

public class PlayerTest {

  private static final String EOF = "\r";

  public static void emptyMap() {
    Player.map.read(new Scanner(
          "15 15 0"+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        ));
  }

  
}
