package minimax;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class MinimaxTest {

  static class NodeTest implements Node {
    int score = 0;
    
    List<Node> childs = new ArrayList<>();
    
    public NodeTest() {
    }
    
    public NodeTest(int score) {
      this.score = score;
    }

    @Override
    public boolean isEndNode() {
      return childs.isEmpty() ;
    }

    @Override
    public int evaluate() {
      return score;
    }

    @Override
    public Collection<Node> getChildren() {
      return childs;
    }
    @Override
    public Collection<Node> getChildren(boolean maximizingScore) {
      // TODO Auto-generated method stub
      return null;
    }
  }
  
  @Test
  public void simpleDepth2Tree() throws Exception {
    NodeTest root = new NodeTest();
    NodeTest a1 = new NodeTest();
    NodeTest a2 = new NodeTest();
    
    NodeTest a11 = new NodeTest(10);
    NodeTest a12 = new NodeTest(20);
    
    NodeTest a21 = new NodeTest(5);
    NodeTest a22 = new NodeTest(100);
    
    // build tree
    root.childs.add(a1);
    root.childs.add(a2);
    
    a1.childs.add(a11);
    a1.childs.add(a12);
    
    a2.childs.add(a21);
    a2.childs.add(a22);
    
    
    Minimax minimax = new Minimax();
    int score = minimax.alphaBeta(root, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
  
    assertThat(score, is (10));
  }
  
}
