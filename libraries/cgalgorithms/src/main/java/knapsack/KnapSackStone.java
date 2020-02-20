package knapsack;

public interface KnapSackStone<T>  {
  double getWeight(T t);
  int getValue(T t);
}
