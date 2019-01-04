package theGreatDispatch;

import java.util.ArrayList;
import java.util.List;

public class KnapSack {
  static double NOT_FOUND = +1_000_000;
  private static final double EPSILON = 0.00000001;
  List<Box> my_pack = new ArrayList<>();

  /** return the disposableVolume if found, -1 else */
  public double fillPackage(double disposableWeight, double disposableVolume, Box boxes[], int numberOfElements) {
    // base case
    if (numberOfElements == 0
        || disposableVolume <= EPSILON) {
      return NOT_FOUND;
    } else if (disposableWeight <= EPSILON) {
      //System.err.println("Found one solution!");
      return disposableVolume;
    }

    if (boxes[numberOfElements-1].destinationTruckId != -1 
        || boxes[numberOfElements-1].weight > disposableWeight
        || boxes[numberOfElements-1].volume > disposableVolume) {
      // only take without this box
      double result = fillPackage(disposableWeight, disposableVolume, boxes, numberOfElements - 1);
      if (result != NOT_FOUND) {
        return result;
      } else {
        return NOT_FOUND;
      }
    } else {
      // try the 2 options (with and without), with first
      // with
      double resultWith = fillPackage(disposableWeight - boxes[numberOfElements - 1].weight, disposableVolume - boxes[numberOfElements - 1].volume, boxes, numberOfElements - 1);
      // without
      double resultWithout = fillPackage(disposableWeight, disposableVolume, boxes, numberOfElements - 1);
      if (resultWith == NOT_FOUND && resultWithout == NOT_FOUND) {
        // can't find a way
        return NOT_FOUND;
      } else {
        //System.err.println("Two solutions to sort : "+ resultWith +" "+resultWithout);
        if (resultWith > resultWithout) {
          my_pack.add(boxes[numberOfElements - 1]);
          return resultWith;
        } else {
          return resultWithout; // we didn't took this box but the volum is better, so we don't add it
        }
      }
    }
  }
  
  /** return the disposableVolume if found, -1 else */
  public double fillPackageWithoutVolume(double disposableWeight, Box boxes[], int numberOfElements) {
    // base case
    if (numberOfElements == 0) {
      return NOT_FOUND;
    } else if (disposableWeight <= EPSILON) {
      //System.err.println("Found one solution!");
      return 1;
    }

    if (boxes[numberOfElements-1].destinationTruckId != -1 
        || boxes[numberOfElements-1].weight > disposableWeight) {
      // only take without this box
      double result = fillPackageWithoutVolume(disposableWeight, boxes, numberOfElements - 1);
      if (result != NOT_FOUND) {
        return result;
      } else {
        return NOT_FOUND;
      }
    } else {
      // try the 2 options (with and without), with first
      // with
      double resultWith = fillPackageWithoutVolume(disposableWeight - boxes[numberOfElements - 1].weight, boxes, numberOfElements - 1);
      if (resultWith != NOT_FOUND) {
        my_pack.add(boxes[numberOfElements - 1]);
        return resultWith;
      }
      // without
      double resultWithout = fillPackageWithoutVolume(disposableWeight, boxes, numberOfElements - 1);
      if (resultWithout != NOT_FOUND) {
        return resultWithout;
      } else {
        return NOT_FOUND;
      }
    }
  }
}