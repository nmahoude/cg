package gitc;

import java.util.ArrayList;
import java.util.List;

import gitc.entities.Factory;

public class KnapSack {

  static List<Factory> my_pack;

  static double getFactoryWeight(Factory factory) {
    return factory.units+1;
  }
  
  static int getFactoryReward(Factory factory) {
    return factory.productionRate;
  }
  public static int fillPackage(double weight, List<Factory> item, List<Factory> optimalChoice, int n){
      //base case
      if(n == 0 || weight == 0)
          return 0;

      if(getFactoryWeight(item.get(n-1)) > weight) {
          List<Factory> subOptimalChoice = new ArrayList<>();
          int optimalCost =fillPackage(weight, item, subOptimalChoice, n-1);
          optimalChoice.addAll(subOptimalChoice);
          return optimalCost;
      }
      else{
          List<Factory> includeOptimalChoice = new ArrayList<>();
          List<Factory> excludeOptimalChoice = new ArrayList<>();
          int include_cost = getFactoryReward(item.get(n-1)) + fillPackage(weight-getFactoryWeight(item.get(n-1)), item, includeOptimalChoice, n-1);
          int exclude_cost = fillPackage(weight, item, excludeOptimalChoice, n-1);
          if(include_cost > exclude_cost){
              optimalChoice.addAll(includeOptimalChoice);
              optimalChoice.add(item.get(n - 1));
              return include_cost;
          }
          else{
              optimalChoice.addAll(excludeOptimalChoice);
              return exclude_cost;
          }
      }
  }

  private static void printOptimalChoice(ArrayList<Factory> itemList, double weight) {
      my_pack = new ArrayList<>();
      fillPackage(weight, itemList, my_pack, itemList.size());
      System.out.println("Best choice for weight: " + weight);
      for(int i = 0; i < my_pack.size(); i++) {
          System.out.println(my_pack.get(i));
      }
  }

  public static void main(String args[]) {
    ArrayList<Factory> itemList = new ArrayList<>();
    itemList.add(buildFactory(2, 1));
    itemList.add(buildFactory(5, 6));
    itemList.add(buildFactory(3, 2));
    itemList.add(buildFactory(4, 4));
    itemList.add(buildFactory(7, 7));

    printOptimalChoice(itemList, 9);
    printOptimalChoice(itemList, 10);
    printOptimalChoice(itemList, 11);
}

  
  private static Factory buildFactory(int weight, int reward) {
    Factory factory = new Factory(0,1);
    factory.units = weight;
    factory.productionRate = reward;
    return factory;
  }

  /**
   * Resolve the knapsack problem for the entry factories
   * 
   * @param allFactories
   * 
   * @return the best repartition of attacks factory to take as much production
   *         as possible
   */
  public List<Factory> knapsackResolution(int W, List<Factory> allFactories) {
    List<Factory> knapsack;

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
