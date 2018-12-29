package theGreatDispatch;

import java.util.ArrayList;
import java.util.List;

public class Truck {
  final int id;
  double volume;
  double weight;
  
  List<Box> boxes = new ArrayList<>();
  
  public Truck(int i) {
    this.id = i;
  }

  public void addBox(Box box) {
    this.volume += box.volume;
    this.weight += box.weight;
    box.destinationTruckId = id;
    boxes.add(box);
  }
  
  public void removeBox(Box box) {
    this.volume -= box.volume;
    this.weight -= box.weight;
    box.destinationTruckId = -1;
    boxes.remove(box);
  }
  
  @Override
  public String toString() {
    return String.format("truck(w=%f, v=%f)", weight, volume);
  }

  public void reset() {
    volume = 0;
    weight = 0; 
    boxes.clear();
  }

  public void debug() {
    System.err.println("Id: "+id+" w="+weight+", v="+volume);
    System.err.println(""+boxes.size()+" boxes: "+boxes);
  }

  public void reassignBoxes() {
    boxes.forEach(box->box.destinationTruckId = id);
  }
}
