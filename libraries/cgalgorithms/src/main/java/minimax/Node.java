package minimax;

import java.util.Collection;

public interface Node {

  boolean isEndNode();

  int evaluate();

  Collection<Node> getChildren();
  Collection<Node> getChildren(boolean maximizingScore);
  void setBestChild(Node node);
}
