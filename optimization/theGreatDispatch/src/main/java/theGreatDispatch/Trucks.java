package theGreatDispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Trucks {
  static Truck truck1 = new Truck(-1);
  static Truck truck2 = new Truck(-2);
  
  public static boolean rearrangeTrucks(Truck myTruck1, Truck myTruck2) {
    List<Box> allBoxes = new ArrayList<>();
    allBoxes.addAll(myTruck1.boxes);
    allBoxes.addAll(myTruck2.boxes);
    
    truck1.reset();
    truck2.reset();
    
    allBoxes.sort(new Comparator<Box>() {
      @Override
      public int compare(Box b1, Box b2) {
        return Double.compare(b2.weight, b1.weight);
      }
    });

    
    for (Box box : allBoxes) {
      boolean t1OK = (truck1.volume + box.volume) <= 100;
      boolean t2OK = (truck2.volume + box.volume) <= 100;
      if (!t1OK && !t2OK) {
        myTruck1.reassignBoxes();
        myTruck2.reassignBoxes();
        return false;
      }
      if (!t1OK) {
        truck2.addBox(box);
      } else if (!t2OK) {
        truck1.addBox(box);
      } else {
        // we have a choice here !
        if (truck1.weight < truck2.weight) {
          truck1.addBox(box);
        } else {
          truck2.addBox(box);
        }
      }
    }
    
    if (arrangementIsBetter(myTruck1, myTruck2)) {
      myTruck1.reset();
      myTruck2.reset();
      truck1.boxes.forEach(box -> myTruck1.addBox(box));
      truck2.boxes.forEach(box -> myTruck2.addBox(box));
      return true;
    } else {
      // reassign all boxes to trucks (for id !)
      myTruck1.reassignBoxes();
      myTruck2.reassignBoxes();
      
      return false;
    }
    
  }

  private static boolean arrangementIsBetter(Truck myTruck1, Truck myTruck2) {
    return Math.abs(truck1.weight - truck2.weight) < Math.abs(myTruck1.weight-myTruck2.weight);
  }

}
