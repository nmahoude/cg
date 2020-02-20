package calm;

import java.util.Scanner;

public class Agent {
  int x,y;
  String item;
  
  public void read(Scanner in) {
    x = in.nextInt();
    y = in.nextInt();
    item = in.next();

  }
}
