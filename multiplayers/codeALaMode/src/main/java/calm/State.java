package calm;

import java.util.Scanner;

public class State {
  private int itemsFE;
  private P itemsPos[] = new P[77];
  private int itemsMask[] = new int[77];
  private int ovenTimer;
  private int ovenContents;
  
  public void read(Scanner in) {
    itemsFE = 0;

    int numTablesWithItems = in.nextInt(); // the number of tables in the kitchen that currently hold an item
    for (int i = 0; i < numTablesWithItems; i++) {
      int tableX = in.nextInt();
      int tableY = in.nextInt();
      P pos = P.get(tableX, tableY);
      String item = in.next();
      addItemToTables(pos, Item.toMask(item));
    }
    
    ovenContents = Item.toMask(in.next());
    ovenTimer = in.nextInt();
    
    int numCustomers = in.nextInt(); // the number of customers currently waiting for food
    for (int i = 0; i < numCustomers; i++) {
      String customerItem = in.next();
      int customerAward = in.nextInt();
    }
  }

  public void addItemToTables(P pos, int itemMask) {
    itemsPos[itemsFE] = pos;
    itemsMask[itemsFE] = itemMask;
    itemsFE++;
  }
  
  public void readInit(Scanner in) {
    int numAllCustomers = in.nextInt();
    for (int i = 0; i < numAllCustomers; i++) {
      String customerItem = in.next(); // the food the customer is waiting for
      int customerAward = in.nextInt(); // the number of points awarded for delivering the food
    }
    in.nextLine();
    for (int i = 0; i < 7; i++) {
      String kitchenLine = in.nextLine();
    }
  }
}
