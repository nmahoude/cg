package god;

import java.util.ArrayList;
import java.util.List;

import god.entities.Zone;

public class KnapSack<T extends KnapsackUnit> {

  List<T> my_pack;

  double getWeight(T zone) {
    return zone.getWeight();
  }
  
  double getReward(T zone) {
    // TODO be the inverse of distance
    return zone.getReward();
  }
  
  public double fillPackage(double weight, List<T> items, List<T> optimalChoice, int n){
      //base case
      if(n == 0 || weight == 0)
          return 0;

      if(getWeight(items.get(n-1)) > weight) {
          List<T> subOptimalChoice = new ArrayList<>();
          double optimalCost =fillPackage(weight, items, subOptimalChoice, n-1);
          optimalChoice.addAll(subOptimalChoice);
          return optimalCost;
      }
      else{
          List<T> includeOptimalChoice = new ArrayList<>();
          List<T> excludeOptimalChoice = new ArrayList<>();
          double include_cost = getReward(items.get(n-1)) + fillPackage(weight-getWeight(items.get(n-1)), items, includeOptimalChoice, n-1);
          double exclude_cost = fillPackage(weight, items, excludeOptimalChoice, n-1);
          if(include_cost > exclude_cost){
              optimalChoice.addAll(includeOptimalChoice);
              optimalChoice.add(items.get(n - 1));
              return include_cost;
          }
          else{
              optimalChoice.addAll(excludeOptimalChoice);
              return exclude_cost;
          }
      }
  }

  private void printOptimalChoice(ArrayList<T> itemList, double weight) {
      my_pack = new ArrayList<>();
      fillPackage(weight, itemList, my_pack, itemList.size());
      System.out.println("Best choice for weight: " + weight);
      for(int i = 0; i < my_pack.size(); i++) {
          System.out.println(my_pack.get(i));
      }
  }


  public List<T> knapsackResolution(int W, List<Zone> allFactories) {
    List<T> knapsack;

    // Input:
    // Values (stored in array v)
    // Weights (stored in array w)
    // Number of distinct items (n)
    // Knapsack capacity (W)
    int n = allFactories.size();
    int w[] = new int[n];
    int v[] = new int[n];

    int m[][] = new int[W][W];
    for (int j = 0; j <= W; j++) {
      m[0][j] = 0;
    }

    for (int i = 1; i < n; i++) {
      for (int j = 0; j < W; j++) {
        if (w[i] > j) {
          m[i][j] = m[i - 1][j];
        } else {
          m[i][j] = Math.max(m[i - 1][j], m[i - 1][j - w[i]] + v[i]);
        }
      }
    }
    return null;
  }
}
