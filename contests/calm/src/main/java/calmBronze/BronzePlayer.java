package calmBronze;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BronzePlayer {
  static int itemsF[][] = new int[11][7];
  static Table tables[][] = new Table[11][7];
  static List<Table> tablesList = new ArrayList<>();
  private static int turnsRemaining;

  static int playerX, playerY;
  private static int playerItem;

  private static int partnerX,partnerY;
  private static int partnerItem;

  static int dishX = -1;
  static int dishY = -1;
  static int bbX = -1, bbY = -1;
  static int icX = -1, icY = -1;
  static int sbX = -1, sbY = -1;
  static int bellX = -1, bellY = -1;
  static int cutX = -1, cutY = -1;
  static int nextDish = 0;
  static int doughX = -1, doughY = -1;
  static int ovenX = -1, ovenY = -1;

  private static String ovenContents;
  private static int ovenItem;
  private static int ovenTimer;

  private static int tableCount = 0;
  private static int preparing = 0;
  private static int lastDish;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int numAllCustomers = in.nextInt();
    for (int i = 0; i < numAllCustomers; i++) {
      String customerItem = in.next(); // the food the customer is waiting for
      int customerAward = in.nextInt(); // the number of points awarded for
                                        // delivering the food
    }
    in.nextLine();
    readWorld(in);
    
    System.err.println("Table count : " + tableCount);
    // game loop
    while (true) {
      cleanItems();
      turnsRemaining = in.nextInt();
      
      playerX = in.nextInt();
      playerY = in.nextInt();
      String playerItemStr = in.next();
      playerItem = Item.getFromString(playerItemStr);
      System.err.println("My  string : " + playerItemStr);
      System.err.println("I have : " + Integer.toBinaryString(playerItem));
      
      partnerX = in.nextInt();
      partnerY = in.nextInt();
      String partnerItemStr = in.next();
      partnerItem = Item.getFromString(partnerItemStr);
      System.err.println("His string : " + partnerItemStr);
      System.err.println("He has : " + Integer.toBinaryString(partnerItem));
      
      int numTablesWithItems = in.nextInt();
      for (int i = 0; i < numTablesWithItems; i++) {
        int tableX = in.nextInt();
        int tableY = in.nextInt();
        String item = in.next();
        itemsF[tableX][tableY] = Item.getFromString(item);
        tables[tableX][tableY].item = Item.getFromString(item);
      }
      
      ovenContents = in.next();
      ovenItem = Item.getFromString(ovenContents);
      ovenTimer = in.nextInt();
      
      int numCustomers = in.nextInt(); // the number of customers currently

      nextDish = 0;
      String nextDishStr= "";
      System.err.println("Commandes clients: ");
      boolean perfectMatch = false;
      for (int i = 0; i < numCustomers; i++) {
        String customerItemStr = in.next();
        System.err.println(customerItemStr);
        int customerItem = Item.getFromString(customerItemStr);
        int customerAward = in.nextInt();
        
        if (lastDish == customerItem) {
          System.err.println("Ce qu'on preparait est toujours dispo");
          perfectMatch = true;
          nextDish = customerItem;
          nextDishStr = customerItemStr;
        }
        if (!perfectMatch && dishIsCompatible(customerItem, playerItem)) {
          System.err.println("on a un truc tout fait à amener !");
          perfectMatch = true;
          nextDish = customerItem;
          nextDishStr = customerItemStr;
        }
        if (!perfectMatch && (nextDish == 0 || !Item.has(customerItem, Item.BLUEBERRIES_TART))) {
          System.err.println("En dernier ressort ...");
          nextDish = customerItem;
          nextDishStr = customerItemStr;
        }
      }

      lastDish = nextDish;
      if (dishIsCompatible(nextDish, playerItem)) {
        System.err.println("Go à la sonnette !");
        goGet(bellX, bellY);
        continue;
      }
      
      System.err.println("Debug : trying order : " + nextDishStr);
      
      System.err.println("1. Construire produit 2nd tiers");
      System.err.println("Currently Preparing 2ndTiers : "+Item.toString(preparing));
      
      if (preparing != 0 && (nextDish & preparing) == 0) {
        System.err.println("on preparait un truc qu'on a abandonné => preparing = 0");
        preparing = 0;
      }
      
      if ((preparing == 0 ||  preparing == Item.CHOPPED_STRAWBERRIES)
          && needChoppedStrawBerries() 
          && (playerHas(Item.STRAWBERRIES) || !worldOrMeHas(Item.CHOPPED_STRAWBERRIES))) {
        System.err.println("Preparer des fraises coupées...");
        preparing = Item.CHOPPED_STRAWBERRIES;
        getStrawberries();
        continue;
      } else if (preparing == Item.CHOPPED_STRAWBERRIES) {
        preparing = 0;
      }
      
      if ((preparing == 0 ||  preparing == Item.CROISSANT)
          && needCroissant() 
          && (playerHas(Item.DOUGH) || !worldOrMeHas(Item.CROISSANT))) {
        System.err.println("Preparer un croissant ...");
        preparing = Item.CROISSANT;
        getCroissant();
        continue;
      } else if (preparing == Item.CROISSANT) {
        preparing = 0;
      }
      
      if ((preparing == 0 ||  preparing == Item.BLUEBERRIES_TART)
          && needTart() 
          && (playerHas(Item.DOUGH) || playerHas(Item.CHOPPED_DOUGH) || 
              playerHas(Item.RAW_TART) || !worldOrMeHas(Item.BLUEBERRIES_TART))) {
        System.err.println("Preparer une tarte aux myrtilles...");

        preparing = Item.BLUEBERRIES_TART;
        getTart();
        continue;
      } else if (preparing == Item.BLUEBERRIES_TART) {
        preparing = 0;
      }
      
      preparing = 0;
      System.err.println("2. Ramasser finished products");

      if (playerHas(Item.DOUGH) || playerHas(Item.CHOPPED_DOUGH) || playerHas(Item.RAW_TART)) {
        System.err.println("Se debarasser de notre produit non fini");
        goGetWithDropping(playerX, playerY);
        continue;
      }
      
      List<Table> tableAScoop = new ArrayList<>();
      if (playerHas(Item.DISH)) {
        System.err.println("j'ai une assiette, est-ce qu'elle est compatible avec le plat?");
        if (dishIsCompatible(playerItem, nextDish)) {
          System.err.println("ok, on passe à la suite");
        } else {
          System.err.println("pas compatible, il faut throw");
          goGetWithDropping(playerX, playerY);
          continue;
        }
      } else {
        // pas d'assiete en main, trouver l'assiette la plus proche compatible
        Table best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Table table : tablesList) {
          if (!table.hasDish()) continue;
          
          double score = 1000 - table.dist(playerX, playerY);
          if ((table.item & nextDish) != Item.DISH) {
            // il y a des choses sur cette assiette ! verifier si on complete
            if ((table.item & playerItem) != 0) {
              System.err.println("Assiette avec ingredient en double, on passe");
              continue; 
            }
            if (dishIsCompatible(table.item | playerItem, nextDish)) {
              System.err.println("big bonus  pour assiette compatible");
              score+= 1_000_000;
            }
          }
          if (score > bestScore) {
            bestScore= score;
            best = table;
          }
        }
        if (best!= null) {
          System.err.println("La meilleur assiette est : " + best.pos.x+","+best.pos.y);
          tableAScoop.add(best);
        } else {
          System.err.println("Pas d'assiette dispo ? on continue");
        }
      }
      
      if (playerHas(Item.DISH) || playerItem == 0) {
      if (needBlueBerries()) {
        System.err.println("Besoin de blueberries ...");
        Table table = tablesList.stream()
            .sorted(Table.closestToMe)
            .filter(t -> t.hasBlueberries())
            .findFirst().orElse(null);
        if (table != null) {
          tableAScoop.add(table);
        } else {
          System.err.println("Pas de blueberries dispo ? on continue");
        }
      }

      if (needIceCream()) {
        System.err.println("Besoin de icecream ...");
        Table table = tablesList.stream()
            .sorted(Table.closestToMe)
            .filter(t -> t.hasIceCream())
            .findFirst().orElse(null);
        if (table != null) {
          tableAScoop.add(table);
        } else {
          System.err.println("Pas de blueberries dispo ? on continue");
        }
      }

      if (needChoppedStrawBerries()) {
        System.err.println("Besoin de chopped strawberries ...");
        Table table = tablesList.stream()
            .sorted(Table.closestToMe)
            .filter(t -> t.item == Item.CHOPPED_STRAWBERRIES)
            .findFirst().orElse(null);
        if (table != null) {
          tableAScoop.add(table);
        } else {
          System.err.println("Pas de chopped strawberries dispo ? on continue");
        }
      }
      
      
      if (needTart()) {
        System.err.println("Besoin de blueBerrie tart...");
        Table table = tablesList.stream()
            .sorted(Table.closestToMe)
            .filter(t -> t.item == Item.BLUEBERRIES_TART)
            .findFirst().orElse(null);
        if (table != null) {
          tableAScoop.add(table);
        } else {
          System.err.println("Pas de blueBerrie tart dispo ? on continue");
        }
      }

      if (needCroissant()) {
        System.err.println("Besoin de croissant...");
        Table table = tablesList.stream()
            .sorted(Table.closestToMe)
            .filter(t -> t.item == Item.CROISSANT)
            .findFirst().orElse(null);
        if (table != null) {
          tableAScoop.add(table);
        } else {
          System.err.println("Pas de croissant dispo ? on continue");
        }
      }
      }
      
      Table next = tableAScoop.stream()
          .sorted(Table.closestToMe)
          .findFirst()
          .orElse(null);
      if (next != null) {
        System.err.println("La table la plus proche : ("+ next.pos.x+","+next.pos.y+") with "+Item.toString(next.item));
        goGet(next);
      } else {
        System.err.println("Je ne sais pas quoi faire, wait");
        System.out.println("WAIT");
      }
    }
  }


  private static boolean dishIsCompatible(int nextDish, int nextDish2) {
    int delta = nextDish ^ nextDish2;
    return (delta & nextDish2) == delta;
  }

  private static boolean worldOrMeHas(int item) {
    return playerHas(item) || worldHas(item);
  }

  private static void getTart() {
    System.err.println("Tarte logique");
    
    if (ovenItem == Item.RAW_TART || ovenItem == Item.BLUEBERRIES_TART) {
      System.err.println("go to oven with rawtart or wait for tart");
      goGet(ovenX, ovenY);
      return;
    }
    
    if (worldHas(Item.BLUEBERRIES_TART)) {
      goGet(Item.BLUEBERRIES_TART);
      return;
    } else {
      // bake tart
      if (playerItem != 0 
          && playerItem != Item.DOUGH 
          && playerItem != Item.CHOPPED_DOUGH
          && playerItem != Item.RAW_TART) {
        System.err.println("  Un truc dans les mains, on s'en débarrasse");
        goGetWithDropping(doughX, doughY);
      } else {
        if (playerItem == 0) {
          System.err.println("  on va chercher un dough");
          goGet(Item.DOUGH | Item.EQUIPMENT_DOUGH);
          return;
        } else if (playerItem == Item.DOUGH) {
          System.err.println("  on va coupe rle dough");
          goGet(cutX, cutY);
          return;
        } else if (playerItem == Item.CHOPPED_DOUGH) {
          System.err.println("  on va chercher des blueberries");
          goGet(Item.BLUEBERRIES | Item.EQUIPMENT_BLUEBERRIES);
          return;
        } else if (playerItem == Item.RAW_TART){
          System.err.println("  on va cuire la tarte");
          goGet(ovenX, ovenY);
          return;
        }
      }
      System.err.println("TODO BAKE TART !");
      System.out.println("WAIT");
      return;
    }
  }

  private static void goGet(Table table) {
    goGet(table.pos.x, table.pos.y);
    return;
  }

  private static void goGet(int itemOnTable) {
    // TODO get closest
    System.err.println("  -> y'a une truc quelquepart");
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        if ((itemsF[x][y] & itemOnTable) != 0) {
          System.err.println("  get already made item "+Integer.toBinaryString(itemOnTable)+" at "+x+","+y);
          goGet(x, y);
          return;
        }
      }
    }
  }

  private static boolean needTart() {
    return Item.has(nextDish, Item.BLUEBERRIES_TART) && !(
        playerHas(Item.BLUEBERRIES_TART) || worldHas(Item.BLUEBERRIES_TART));
  }

  private static boolean playerHas(int item) {
    return Item.has(playerItem, item);
  }

  private static void cleanItems() {
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        itemsF[x][y] = itemsF[x][y] & Item.CLEAN_MASK;
        if (tables[x][y] != null) {
          tables[x][y].item = tables[x][y].item & Item.CLEAN_MASK;
        }
      }
    }
  }

  private static void getCroissant() {
    System.err.println("Logique croissant");

    if (ovenItem == Item.DOUGH || ovenItem == Item.CROISSANT) {
      System.err.println("go to oven with dough or wait for croissant");
      goGet(ovenX, ovenY);
      return;
    }
    
    
    // si on a pas encore pris de DOUGH et qu'il y a un croissant quelquepart -> go get it
    if (!Item.has(playerItem, Item.DOUGH)) {
      // Check if there is a croissant
      for (int y = 0; y < 7; y++) {
        for (int x = 0; x < 11; x++) {
          if ((itemsF[x][y] & Item.CROISSANT) != 0) {
            System.err.println("On va chercher le croissant direct");
            goGet(x, y);
            return;
          }
        }
      }
    }

    // sinon si on a un truc, mais pas de DOUGH, il faut le poser
    if (playerItem != 0 && playerItem != Item.DOUGH) {
      // besoin de poser ce qu'on porte,
      // trouver la table sur la route qui est vide

      int x = doughX;
      int y = doughY;
      goGetWithDropping(x, y);
      return;
    }

    if (playerItem == 0) {
      System.err.println("On va au dough");
      goGet(doughX, doughY);
    } else {
      System.err.println("ici on a un DOUGH, il faut aller au four");
      goGet(ovenX, ovenY);
    }
  }

  private static void goGetWithDropping(int x, int y) {
    Table table = hasEmptyTable(playerX, playerY);
    if (table != null) {
      // drop it now !
      System.err.println("Trouver une table vide pour poser " + Integer.toBinaryString(playerItem));
      System.out.println("USE "+table.pos+" drop on table");
      return;
    } else {
      System.err.println("On va a "+x+","+y+", on a un truc, mais pas de table vide");
      goGet(x, y); // TODO on espere qu'on trouvera une table vide sur le chemin du DOUGH
      return;
    }
  }

  private static Table hasEmptyTable(int x, int y) {
    for (int dy=-1;dy<=1;dy++) {
      for (int dx=-1;dx<=1;dx++) {
        if (tables[x+dx][y+dy] != null && tables[x+dx][y+dy].item == 0) {
          return tables[x+dx][y+dy];
        }
      }
    }
    return null;
  }

  private static boolean needCroissant() {
    return Item.has(nextDish, Item.CROISSANT) 
        && !Item.has(playerItem, Item.CROISSANT);
  }

  private static void getIceCream() {
    System.err.println("going to get IC");

    goGet(icX, icY);
  }

  private static boolean needIceCream() {
    return Item.has(nextDish,Item.ICE_CREAM) 
        && !Item.has(playerItem, Item.ICE_CREAM);
  }

  private static void getBlueBerries() {
    System.err.println("going to get BB");
    goGet(bbX, bbY);
  }

  private static boolean needBlueBerries() {
    return Item.has(nextDish, Item.BLUEBERRIES)
        && !Item.has(playerItem, Item.BLUEBERRIES);
  }

  private static boolean needDish() {
    return !Item.has(playerItem, Item.DISH);
  }

  private static void getDish() {
    System.err.println("dish logique");
    System.err.println("going to get dish at " + dishX + "," +dishY);
    goGet(dishX, dishY);
  }

  private static void readWorld(Scanner in) {
    for (int y = 0; y < 7; y++) {
      String kitchenLine = in.nextLine();
      System.err.println("Line : " + kitchenLine);
      for (int x = 0; x < 11; x++) {
        char c = kitchenLine.charAt(x);
        switch (c) {
        case 'D':
          dishX = x;
          dishY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_DISH);
          break;
        case 'B':
          bbX = x;
          bbY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_BLUEBERRIES);
          break;
        case 'I':
          icX = x;
          icY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_ICE_CREAM);
          break;
        case 'W':
          bellX = x;
          bellY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_BELL);
          break;
        case 'S':
          sbX = x;
          sbY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_STRAWBERRY);
          break;
        case 'H':
          doughX = x;
          doughY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_DOUGH);
          break;
        case 'C':
          cutX = x;
          cutY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_CHOPPING_BOARD);
          break;
        case 'O':
          ovenX = x;
          ovenY = y;
          createTableAndItems(x,y,Item.EQUIPMENT_OVEN);
          break;
        case '#': // emptyTable
          tableCount ++;
          tables[x][y] = new Table(x,y);
          tablesList.add(tables[x][y]);
          break;
        case '.': // floor
        case '0': // player 0
        case '1': // player 1
          break;
        default:
          throw new RuntimeException("Equipment inconnu : " + c);
        }
      }
    }
  }

  private static void createTableAndItems(int x, int y, int equipment) {
    itemsF[x][y] = equipment;
    tables[x][y] = new Table(x,y);
    tables[x][y].item = equipment;
    tablesList.add(tables[x][y]);
  }


  private static boolean needChoppedStrawBerries() {
    return Item.has(nextDish,Item.CHOPPED_STRAWBERRIES)
        && (!Item.has(playerItem, Item.CHOPPED_STRAWBERRIES));
  }

  private static boolean worldHas(int item) {
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        if ((itemsF[x][y] & item) != 0) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static void getStrawberries() {
    // TODO logic to abandon chopped strwaberry ?
    if (playerHas(Item.STRAWBERRIES)) {
      goGet(cutX, cutY);
      return;
    }
    
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        if ((itemsF[x][y] & Item.CHOPPED_STRAWBERRIES) != 0) {
          System.err.println("Potential chopped strawbery here with some other things");
        }
      }
    }
    
    
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        if (itemsF[x][y] == Item.CHOPPED_STRAWBERRIES) {
          System.err.println("get already made chopped strawberries");
          goGet(x, y);
          return;
        }
      }
    }

    if (!Item.has(playerItem, Item.STRAWBERRIES)) {
      if (playerItem != 0) {
        // need to drop what we go
        Table table = hasEmptyTable(playerX, playerY);
        if (table != null) {
          System.out.println("USE "+table.pos+" drop pour les fraises");
          return;
        } else {
          System.err.println("going to get SB, en esperant une table libre");
          goGet(sbX, sbY);
          return;
        }
      } else {
        System.err.println("going to get SB, on est free");
        goGet(sbX, sbY);
        return;
      }
    } else {
      System.err.println("going to cut Cut, on a des fraises");

      goGet(cutX, cutY);
      return;
    }
  }

  public static void goGet(int x, int y) {
    if (Math.abs(x - playerX) <= 1 && Math.abs(y - playerY) <= 1) {
      System.out.println("USE " + x + " " + y);
    } else {
      System.out.println("MOVE " + x + " " + y);
    }
  }
}
