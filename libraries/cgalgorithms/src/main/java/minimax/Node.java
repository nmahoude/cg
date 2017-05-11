package minimax;

import java.util.Collection;

public interface Node {

  boolean isEndNode();

  int evaluate();

  Collection<Node> getChildren();

}
