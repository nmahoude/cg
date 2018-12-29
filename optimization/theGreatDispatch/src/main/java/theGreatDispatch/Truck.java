package theGreatDispatch;

public class Truck {
  int id;
  double volume;
  double weight;
  
  public void addBox(Box box) {
    this.volume += box.volume;
    this.weight += box.weight;
    box.destinationTruckId = id;
  }
  
  public void removeBox(Box box) {
    this.volume -= box.volume;
    this.weight -= box.weight;
    box.destinationTruckId = -1;
  }
  
  @Override
  public String toString() {
    return String.format("truck(w=%f, v=%f)", weight, volume);
  }

  public void reset() {
    volume = 0;
    weight = 0; 
  }
}
