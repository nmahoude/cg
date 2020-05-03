package calm;

public class Desert {
  public Item item = new Item(P.INVALID);
  public int award;
  
  public void set(int mask, int customerAward) {
    this.item.reset(mask);
    this.award = customerAward;
  }
  
  @Override
  public String toString() {
    return ""+item;
  }
}
