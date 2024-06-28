package utg2019;

public enum Owner {
  ME(0),OTHER(1);
  
  public final int index;
  
  Owner(int index) {
    this.index = index;
  }
}
