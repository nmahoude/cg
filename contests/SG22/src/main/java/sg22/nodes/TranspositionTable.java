package sg22.nodes;

import sg22.Player;
import sg22.State;

public class TranspositionTable {
  private static final int MAX_ENCONDING = 65_536*2;
  
  static Node[] empty = new Node[MAX_ENCONDING];
  static Node[] nodes = new Node[MAX_ENCONDING];
  public static int totalCollisions = 0;
  
  public static void clear() {
    System.arraycopy(empty, 0, nodes, 0, MAX_ENCONDING);
    totalCollisions = 0;
  }
  
  
  public static void put(Node node) {
    int offset = calculateOffset(node.s);
    
    nodes[offset] = node;
  }


  private static int calculateOffset(State s) {
    if (s.agents[0].location == -1) return 0;
    
    int offset = 0;
    offset <<= 3;
    offset += s.agents[0].location ;
    
    offset <<= 1;
    offset += s.giveCount;
    
    offset <<= 3;
    offset += s.phase.index ;

    for (int i=0;i<=8;i++) {
      offset <<=1;
      offset += s.hand.getHandCount(i) == 0 ? 0 : 1; // 1 bit encoding ..
    }
    
    
    if (offset < 0 || offset > MAX_ENCONDING) {
      System.err.println("Transposition error " + offset);
      System.err.println("Encoding : "+Integer.toBinaryString(offset));
      s.debugLite();
      System.err.println(" Location = "+s.agents[0].location);
      System.err.println(" give = "+s.giveCount);
      System.err.println(" phase = "+s.phase.index);
      
      
      
      throw new RuntimeException();
    }
    return offset;
  }
  
  public static Node get(Node node) {
    int offset = calculateOffset(node.s);
    
    Node cacheNode = nodes[offset];
    if (cacheNode != null) {
      if (!isSameNode(cacheNode, node)) {
        nodes[offset] = node; // replace the node
        return null; 
        
      }
      
      totalCollisions++;
      if (Player.DEBUG_COLLISION) {
        System.err.println("************");
        System.err.println("Collision ! ");
        System.err.println("************");
        node.debugActionList();
        node.s.debugLite();
        System.err.println("Versus");
        cacheNode.debugActionList();
        cacheNode.s.debugLite();
      }
      
      return cacheNode;
    } else {
      return null;
    }
  }


  private static boolean isSameNode(Node cacheNode, Node node) {
    if (cacheNode.s.agents[0].location != node.s.agents[0].location) return false;
    if (cacheNode.s.agents[0].score != node.s.agents[0].score) return false;
    
    if (cacheNode.s.agents[0].permanentArchitectureStudyCards != node.s.agents[0].permanentArchitectureStudyCards) return false;
    if (cacheNode.s.agents[0].permanentDailyRoutineCards != node.s.agents[0].permanentDailyRoutineCards) return false;
    if (cacheNode.s.playCount != node.s.playCount) return false;
    
    for (int i=0;i<=8;i++) {
      if (cacheNode.s.hand.getHandCount(i)  != node.s.hand.getHandCount(i)) {
        return false;
      }
    }      
    
    return true;
  }
  
}
