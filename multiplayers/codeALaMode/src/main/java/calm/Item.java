package calm;

public class Item {
  public P pos;
  public int mask;
  
  public Item(P pos) {
    this.pos = pos;
  }

  public void reset(int itemMask) {
    this.mask = itemMask;
  }

  @Override
  public String toString() {
    return ItemMask.output(mask)+"@"+pos.toString();
  }
  
  public boolean hasStrawberries() {
    return (mask & ItemMask.STRAWBERRIES) != 0;
  }

  public boolean isEqDishwasher() {
    return (mask & ItemMask.EQUIPMENT_DISH) != 0;
  }

  public boolean isEqStrawberries() {
    return (mask & ItemMask.EQUIPMENT_STRAWBERRY) != 0;
  }

  public boolean hasChoppedStrawberries() {
    return (mask & ItemMask.CHOPPED_STRAWBERRIES) != 0;
  }

  public boolean hasCroissant() {
    return (mask & ItemMask.CROISSANT) != 0;
  }

  public boolean hasIceCream() {
    return (mask & ItemMask.ICE_CREAM) != 0;
  }

  public boolean hasBlueBerries() {
    return (mask & ItemMask.BLUEBERRIES) != 0;
  }

  public boolean hasBlueBerriesTart() {
    return (mask & ItemMask.BLUEBERRIES_TART) != 0;
  }

  public boolean isEmpty() {
    return mask == 0;
  }

  public boolean isEqChopper() {
    return (mask & ItemMask.EQUIPMENT_CHOPPING_BOARD) != 0;
  }

  public boolean hasDough() {
    return (mask & ItemMask.DOUGH) != 0;
  }

  public boolean isEqOven() {
    return (mask & ItemMask.EQUIPMENT_OVEN) != 0;
  }

  public boolean isEqDough() {
    return (mask & ItemMask.EQUIPMENT_DOUGH) != 0;
  }

  public boolean hasPlate() {
    return (mask & ItemMask.DISH) != 0;
  }

  public boolean isEqBell() {
    return (mask & ItemMask.EQUIPMENT_BELL) != 0;
  }

  public boolean isEqIceCream() {
    return (mask & ItemMask.EQUIPMENT_ICE_CREAM) != 0;
  }

  public boolean isEqBlueberries() {
    return (mask & ItemMask.EQUIPMENT_BLUEBERRIES) != 0;
  }

  public boolean hasBlueberryTart() {
    return (mask & ItemMask.BLUEBERRIES_TART) != 0;
  }

  public boolean hasChoppedDough() {
    return (mask & ItemMask.CHOPPED_DOUGH) != 0;
  }

  public boolean hasRawTart() {
    return (mask & ItemMask.RAW_TART) != 0;
  }

  public boolean isFloor() {
    return (mask & ItemMask.FLOOR) != 0;
  }

  public boolean hasChoppedStrawberriesIngredients() {
    return hasStrawberries() ;
  }

  public boolean hasCroissantIngredients() {
    return hasDough();
  }

  public boolean hasBlueberryTartIngredients() {
    return hasDough() || hasChoppedDough() || hasRawTart() || hasBlueBerries();
  }

  public void copyFrom(Item model) {
    this.pos = model.pos;
    this.mask = model.mask;
  }

  public int getValue() {
    int value = 0;
    if (hasBlueBerriesTart()) value+=30;
    if (hasCroissant()) value+= 15;
    if (hasChoppedStrawberries()) value+=10;
    value += Integer.bitCount(mask);
    return value;
  }

  public boolean isCompatibleWith(Item item) {
    return (this.mask & ~item.mask) == 0;
  }

}
