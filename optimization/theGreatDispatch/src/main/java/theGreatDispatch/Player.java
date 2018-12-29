package theGreatDispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Player {
  static List<Box> boxes = new ArrayList<>();
  static int boxToTruck[];
  
  static Truck trucks[] = new Truck[100];
  static {
    for (int i=0;i<100;i++) {
      trucks[i] = new Truck();
    }
  }
  static int boxCount;
  static double target;

  public static void main(String args[]) {

    Scanner in = new Scanner(System.in);
    Player player = new Player();
    player.play(in);
  }

  public void play(Scanner in) {
    boxCount = in.nextInt();

    boxToTruck = new int[boxCount];
    
    double sum = 0;
    for (int i = 0; i < boxCount; i++) {
      double weight = in.nextDouble();
      double volume = in.nextDouble();
      sum += weight;
      Box box = new Box(i, weight, volume);
      boxes.add(box);
    }
    
    target = sum / 100;
    System.err.println("Boxes count : "+boxes.size());
    System.err.println("avg weight sum = " + sum + " / " + target);

    boxes = boxes.stream().sorted((b1, b2) -> { return Double.compare(b2.volume, b1.volume); }).collect(Collectors.toList());
    
    Node node0 = new Node();
    node0.depth = 0;
    node0.expand();
    
    
    //monteCarlo();
    
    output();
  }

  private void monteCarlo() {
    System.err.println("Target is " + target);
    // order boxes
    long start = System.currentTimeMillis();
    long duration = 0;
    int sim = 0;
    double bestScore = Double.POSITIVE_INFINITY;
    while (duration < 45_000) {
      sim++;
      if ((sim & (512-1)) == 0) {
        duration = System.currentTimeMillis() - start;
      }
      
      Collections.shuffle(boxes);
      for (Box box : boxes) {
        box.reset();
      }
      for (int i=0;i<100;i++) {
        trucks[i].reset();
      }
      ffd();
      
      double delta = delta();
      if (delta < bestScore) {
        bestScore = delta;
        for (Box box : boxes) {
          boxToTruck[box.index] = box.destinationTruckId;
        }
      }
    }
    
//    boxes = boxes.stream()
//        .sorted((b1, b2) -> { return Integer.compare(b1.index, b2.index); } )
//        .collect(Collectors.toList());
//    
//    debugDelta();
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
      System.out.print(boxToTruck[i]+ " ");
    }
    System.out.println();
  }
}
