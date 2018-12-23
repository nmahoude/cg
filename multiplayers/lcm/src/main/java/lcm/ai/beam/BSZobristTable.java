package lcm.ai.beam;

public class BSZobristTable {
  private static final int LENGTH = (int)(Math.pow(2, 12))-1;
  BSNode[] nodes = new BSNode[LENGTH+1];
  
  public void init() {
    for (int i = LENGTH;i>=0;i-- ) {
      nodes[i] = null;
    }
  }
  
  public BSNode get(long hash) {
    int modulo = (int)(hash & LENGTH);
    return nodes[modulo];
  }
  
  public void put(long hash, BSNode node) {
    int modulo = (int)(hash & LENGTH);
    nodes[modulo] = node;
  }
  
  
}
