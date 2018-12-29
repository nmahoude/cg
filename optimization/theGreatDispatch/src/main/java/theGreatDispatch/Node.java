package theGreatDispatch;

public class Node {
  int depth; // which is box id too !

  public boolean expand() {
    if (depth == Player.boxes.size()) {
      System.err.println("Found a solution !");
      return true;
    }

    Node node = new Node();
    node.depth = depth+1;
//    System.err.println("Expanding node at depth "+depth);
    for (int i=0;i<Math.min(99, depth+1);i++) {
      if ((Player.trucks[i].volume + Player.boxes.get(depth).volume < 100) 
          || (Player.trucks[i].weight + Player.boxes.get(depth).weight > Player.target + 0.0006)){
        Player.trucks[i].addBox(Player.boxes.get(depth));
        boolean result = node.expand();
        Player.trucks[i].removeBox(Player.boxes.get(depth));
      }
    }
    return false;
  }
}
