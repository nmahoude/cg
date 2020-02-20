package knapsack;

import java.util.ArrayList;
import java.util.List;

public class KnapSack<T> {

  List<T> my_pack;
  private KnapSackStone<T> stone;

  public KnapSack(KnapSackStone<T> stone) {
    this.stone = stone;
  }

  public int fillPackage(double disposableWeight, List<T> items, List<T> optimalChoice, int numberOfElements) {
    // base case
    if (numberOfElements == 0 || disposableWeight == 0)
      return 0;

    if (stone.getWeight(items.get(numberOfElements - 1)) > disposableWeight) {
      List<T> subOptimalChoice = new ArrayList<>();
      int optimalCost = fillPackage(disposableWeight, items, subOptimalChoice, numberOfElements - 1);
      optimalChoice.addAll(subOptimalChoice);
      return optimalCost;
    } else {
      List<T> includeOptimalChoice = new ArrayList<>();
      List<T> excludeOptimalChoice = new ArrayList<>();
      int include_cost = stone.getValue(items.get(numberOfElements - 1)) + fillPackage(disposableWeight - stone.getWeight(items.get(numberOfElements - 1)), items, includeOptimalChoice, numberOfElements - 1);
      int exclude_cost = fillPackage(disposableWeight, items, excludeOptimalChoice, numberOfElements - 1);
      if (include_cost > exclude_cost) {
        optimalChoice.addAll(includeOptimalChoice);
        optimalChoice.add(items.get(numberOfElements - 1));
        return include_cost;
      } else {
        optimalChoice.addAll(excludeOptimalChoice);
        return exclude_cost;
      }
    }
  }

  public void printOptimalChoice(ArrayList<T> itemList, double weight) {
    my_pack = new ArrayList<>();
    fillPackage(weight, itemList, my_pack, itemList.size());
    System.out.println("Best choice for weight: " + weight);
    for (int i = 0; i < my_pack.size(); i++) {
      System.out.println(my_pack.get(i));
    }
  }
}
