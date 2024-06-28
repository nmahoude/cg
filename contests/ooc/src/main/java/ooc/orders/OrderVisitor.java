package ooc.orders;

public interface OrderVisitor {

  public void usedMove(Order order);
  public void usedSurface(Order order);
  public void usedTorpedo(Order order);
  public void usedSonar(Order order);
  public void usedSilence(Order order);
  public void usedMine(Order order);
  public void usedTrigger(Order order);

}
