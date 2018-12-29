package theGreatDispatch;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
  static Box boxes[];
  static Box boxToTruck[];
  
  static Truck trucks[] = new Truck[100];
  static {
    for (int i=0;i<100;i++) {
      trucks[i] = new Truck(i);
    }
  }
  static int boxCount;
  static double target;
  private static Random random = ThreadLocalRandom.current();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Player player = new Player();
    player.play(in);
  }

  public void play(Scanner in) {
    boxCount = in.nextInt();
    boxes = new Box[boxCount];
    boxToTruck = new Box[boxCount];
    
    double sum = 0;
    for (int i = 0; i < boxCount; i++) {
      double weight = in.nextDouble();
      double volume = in.nextDouble();
      sum += weight;
      Box box = new Box(i, weight, volume);
      boxes[i] = box;
      boxToTruck[i] = box; // to keep order
    }
    
    target = sum / 100;
    System.err.println("Boxes count : "+boxCount);
    System.err.println("avg weight sum = " + sum + " / " + target);

    Arrays.sort(boxes, new Comparator<Box>() {
      @Override
      public int compare(Box b1, Box b2) {
        return Double.compare(b2.weight, b1.weight);
      }
    });

    ffd();
    
    long start = System.currentTimeMillis();
    boolean needRandom = false;
    long duration = 0;
    int sim = 0;
    while(duration < 49_000) {
      sim++;
      if ((sim & (512-1)) == 0) {
        duration = System.currentTimeMillis() - start; 
      }
      
      Truck highestTruck = trucks[0];
      Truck lowestTruck = trucks[0];

      if (needRandom) {
        int first = Player.random.nextInt(100);
        int second;
        do {
          second = Player.random.nextInt(100);
        } while (second == first);
        
        highestTruck = trucks[first];
        lowestTruck = trucks[second];
      } else {
        for (Truck truck : trucks) {
          if (truck.weight > highestTruck.weight) {
            highestTruck = truck;
          }
          if (truck.weight < lowestTruck.weight) {
            lowestTruck = truck;
          }
        }
      }
//      debugTrucks(highestTruck, lowestTruck);
      if (highestTruck == lowestTruck) {
        break;
      }
      needRandom = !Trucks.rearrangeTrucks(highestTruck, lowestTruck);
      
//      System.err.println("new delta : "+delta());
//      debugTrucks(highestTruck, lowestTruck);
    }
    
//    knaspsacked();
    
    
    output();
  }

  private void debugTrucks(Truck highestTruck, Truck lowestTruck) {
    System.err.println("Delta = "+ (highestTruck.weight - lowestTruck.weight));
    System.err.println("----------------");
    System.err.println("Highest : " + highestTruck.id+" with weight: "+highestTruck.weight);
    highestTruck.debug();
    System.err.println("Lowest : " + lowestTruck.id+" with weight: "+lowestTruck.weight);
    lowestTruck.debug();
  }

  private void knaspsacked() {
    int startIndex = boxes.length;
    int remainingBoxes = boxCount;
    int truckId = 0;
    while (startIndex > 0) {
      Box mainBox;
      do {
        mainBox = boxes[--startIndex];
      } while (mainBox.destinationTruckId != -1);
      
      double disposableWeight = target - mainBox.weight;
      double disposableVolume = 100.0 - mainBox.volume;

      //System.err.println("take main box : " + mainBox);
      //System.err.println("Disposable v="+disposableVolume+" -- dW = "+disposableWeight);
      KnapSack ks = new KnapSack();
      double result = ks.fillPackage(disposableWeight, disposableVolume, boxes, startIndex-1);
      //System.err.println("Knapsack ended with result "+result);
      //System.err.println("Nb of boxes in the truck : "+ ks.my_pack.size());
      if (result != KnapSack.NOT_FOUND) {
        System.err.println("Can't find arrangement for truck "+truckId);
        double sumWeightAssigned = 0;
        double sumWeight = 0;
        for (Box box : boxes) {
          if (box.destinationTruckId != -1) {
            sumWeightAssigned+= box.weight;
          } else {
            sumWeight += box.weight;
          }
        }
        System.err.println("Weight assigned " + sumWeightAssigned+" mean= "+(1.0 * sumWeightAssigned / (truckId)));
        System.err.println("Weight unassigned "+sumWeight+" mean= "+(1.0 * sumWeight / (100-truckId)));
      }
      mainBox.destinationTruckId = truckId;
      for (Box box : ks.my_pack) {
        box.destinationTruckId = truckId;
      }
      remainingBoxes-= (ks.my_pack.size()+1);
      System.err.println("Filled truck n° "+truckId+" with "+(ks.my_pack.size()+1)+" boxes");
      System.err.println("Remaining boxes : "+ remainingBoxes);
      truckId++;
    }
  }

  private double delta() {
    double heaviest = Double.NEGATIVE_INFINITY;
    double lightest = Double.POSITIVE_INFINITY;
    for (int i=0;i<100;i++) {
      if (heaviest < trucks[i].weight) {
        heaviest = trucks[i].weight;
      }
      if (lightest > trucks[i].weight) {
        lightest = trucks[i].weight;
      }
    }
    return heaviest - lightest;
  }

  private void debugDelta() {
    System.err.println("Delta is : " + delta());
  }

  private void ffd() {
    for (Box box : boxes) {
      for (int t=0;t<100;t++) {
        if ((trucks[t].volume + box.volume <= 100) 
            && (trucks[t].weight + box.weight < target)) {
          trucks[t].addBox(box);
          box.destinationTruckId = t;
          break;
        }
      }
      if (box.destinationTruckId == -1) {
        // oups no box that fit, find any trucks
        int best = -1;
        double bestScore = Double.POSITIVE_INFINITY;
        for (int t=0;t<100;t++) {
          if ((trucks[t].volume + box.volume <= 100)) {
            double weight = trucks[t].weight + box.weight;
            if (Math.abs(weight-target) < bestScore) {
              bestScore = Math.abs(weight-target);
              best = t;
            }
          }
        }
        trucks[best].addBox(box);
        box.destinationTruckId = best;
      }
    }
  }

  /**
   * Ranger dans la boite la mieux remplie qui peut le contenir
   */
  private void bfd() {

    for (Box box : boxes) {
      int best = -1;
      double bestScore = Double.POSITIVE_INFINITY;
      for (int t=0;t<100;t++) {
        if ((trucks[t].volume + box.volume <= 100) 
            && (trucks[t].weight + box.weight < target)) {
          double dWeight = target - (trucks[t].weight + box.weight);
          if (dWeight < bestScore) {
            best = t;
            bestScore = dWeight;
          }
        }
      }
      if (best != -1) {
        trucks[best].addBox(box);
        box.destinationTruckId = best;
      }
      
      
      if (box.destinationTruckId == -1) {
        // oups no box that fit, find any trucks
        best = -1;
        bestScore = Double.POSITIVE_INFINITY;
        for (int t=0;t<100;t++) {
          if ((trucks[t].volume + box.volume <= 100)) {
            double weight = trucks[t].weight + box.weight;
            if (Math.abs(weight-target) < bestScore) {
              bestScore = Math.abs(weight-target);
              best = t;
            }
          }
        }
        trucks[best].addBox(box);
        box.destinationTruckId = best;
      }
    }
  }

  
  private void output() {
//    for (Box box : boxes) {
//      System.out.print(box.destinationTruckId+ " ");
//    }    
    for (int i=0;i<boxToTruck.length;i++) {
      System.out.print(boxToTruck[i].destinationTruckId+ " ");
    }
    System.out.println();
  }
}
