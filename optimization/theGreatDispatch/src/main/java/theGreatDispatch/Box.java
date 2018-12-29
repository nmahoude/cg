package theGreatDispatch;

public class Box {
  public final double weight;
  public final double volume;
  public int destinationTruckId = -1;
  public final int index;

  public Box(int index, double weight, double volume) {
    this.index = index;
    this.weight = weight;
    this.volume = volume;
  }
  
  
  @Override
  public String toString() {
    return String.format("(w=%f, v=%f)", weight, volume);
  }


  public void reset() {
    destinationTruckId = -1;
  }
}
