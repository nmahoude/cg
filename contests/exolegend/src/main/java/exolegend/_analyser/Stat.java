package exolegend._analyser;

import java.util.List;

public class Stat {
  List<Integer> values;
  private String nature;
  
  public Stat(String nature, List<Integer> values) {
    super();
    this.nature = nature;
    this.values = values.stream().sorted(Integer::compareTo).toList();
  }

  public void display() {
    System.out.println(nature);
    
    System.out.print("Min = "+values.stream().min(Integer::compareTo).orElse(-1));
    System.out.print(" , ");
    System.out.print("Moy = "+values.stream().mapToInt(i -> i).sum() / values.size());
    System.out.print(" , ");
    System.out.print("Max = "+values.stream().max(Integer::compareTo).orElse(-1));
    System.out.print(" , ");
    System.out.print("Mean = "+values.get(values.size() / 2));
    System.out.println();
  }
}