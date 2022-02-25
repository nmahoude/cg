package botg;

import fast.read.FastReader;

public class Item {

  String name;
  int cost;
  int damage;
  int health;
  int maxHealth;
  int mana;
  int maxMana;
  int moveSpeed;
  int manaRegeneration;
  int isPotion;

  public static Item from(FastReader in) {
    Item item = new Item();
    
    item.name = in.next(); // contains keywords such as BRONZE, SILVER and BLADE, BOOTS connected by "_" to help you sort easier
    item.cost = in.nextInt(); // BRONZE items have lowest cost, the most expensive items are LEGENDARY
    item.damage = in.nextInt(); // keyword BLADE is present if the most important item stat is damage
    item.health = in.nextInt();
    item.maxHealth = in.nextInt();
    item.mana = in.nextInt();
    item.maxMana = in.nextInt();
    item.moveSpeed = in.nextInt(); // keyword BOOTS is present if the most important item stat is moveSpeed
    item.manaRegeneration = in.nextInt();
    item.isPotion = in.nextInt(); // 0 if it's not instantly consumed

    
    return item;
  }

}
