package bruteforce;

public class Main {
  public static void main(String[] args) {
    String grid = """
8 5
1 1 2 1 0 0 0 0
3 0 1 0 0 1 0 2
5 0 3 0 0 1 2 6
1 3 1 1 0 0 0 0
4 0 0 0 0 0 2 3
        """;
  
  State state = new State();
  state.read(grid);
  state.solve();
  
  
  }
}
