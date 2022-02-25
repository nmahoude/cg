package botg;

import java.util.ArrayList;
import java.util.List;

import fast.read.FastReader;

public class State {
  List<Item> items = new ArrayList<>();
  
  int roundType;
  int enemyGold;
  int gold;

  public void readInit(FastReader in) {
    int myTeam = in.nextInt();
    int bushAndSpawnPointCount = in.nextInt(); // useful from wood1, represents the number of bushes and the number of places where neutral units can spawn
    for (int i = 0; i < bushAndSpawnPointCount; i++) {
        String entityType = in.next(); // BUSH, from wood1 it can also be SPAWN
        int x = in.nextInt();
        int y = in.nextInt();
        int radius = in.nextInt();
    }
    int itemCount = in.nextInt(); // useful from wood2  
    for (int i = 0; i < itemCount; i++) {
        Item item = Item.from(in);
        items.add(item);
    }
  }

  public void read(FastReader in) {
    gold = in.nextInt();
    enemyGold = in.nextInt();
    roundType = in.nextInt();
    int entityCount = in.nextInt();
    for (int i = 0; i < entityCount; i++) {
      int unitId = in.nextInt();
      int team = in.nextInt();
      String unitType = in.next(); // UNIT, HERO, TOWER, can also be GROOT from wood1
      int x = in.nextInt();
      int y = in.nextInt();
      int attackRange = in.nextInt();
      int health = in.nextInt();
      int maxHealth = in.nextInt();
      int shield = in.nextInt(); // useful in bronze
      int attackDamage = in.nextInt();
      int movementSpeed = in.nextInt();
      int stunDuration = in.nextInt(); // useful in bronze
      int goldValue = in.nextInt();
      int countDown1 = in.nextInt(); // all countDown and mana variables are useful starting in bronze
      int countDown2 = in.nextInt();
      int countDown3 = in.nextInt();
      int mana = in.nextInt();
      int maxMana = in.nextInt();
      int manaRegeneration = in.nextInt();
      String heroType = in.next(); // DEADPOOL, VALKYRIE, DOCTOR_STRANGE, HULK, IRONMAN
      int isVisible = in.nextInt(); // 0 if it isn't
      int itemsOwned = in.nextInt(); // useful from wood1
    }
  }

}
