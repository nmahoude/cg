package ooc.ai.search;

import java.util.ArrayList;
import java.util.List;

import ooc.State;
import ooc.orders.Order;
import ooc.orders.OrderTag;

public class Node {
  public Node parent;
	public Order order; 
	public State state = new State();
	int mask;
	double score;
	
	private List<Node> childs = new ArrayList<>(20);

  public Node(int mask) {
    this.mask = mask;
  }
  
  public void reset() {
  	childs.clear();
  }
	
  public void addChild(Node node) {
		childs.add(node);
	}

	
  public Node findBestNode() {
    this.score = Double.NEGATIVE_INFINITY;
    Node best = this.findBestNode(this);
    return best;
  }

	private Node findBestNode(Node currentBest) {
    if (score > currentBest.score) {
      currentBest = this;
    }
    for (Node n : childs) {
      currentBest = n.findBestNode(currentBest);
    }
    return currentBest;
  }

	@Override
	public String toString() {
		return order.toString();
	}

  public boolean hasSilence() {
    return (mask & OrderTag.SILENCE.mask) != 0;
  }
  public boolean hasMove() {
    return (mask & OrderTag.MOVE.mask) != 0;
  }
  public boolean hasSurface() {
    return (mask & OrderTag.SURFACE.mask) != 0;
  }
  public boolean hasTorpedo() {
    return (mask & OrderTag.TORPEDO.mask) != 0;
  }
  public boolean hasMine() {
    return (mask & OrderTag.MINE.mask) != 0;
  }
  public boolean hasSonar() {
    return (mask & OrderTag.SONAR.mask) != 0;
  }
  public boolean hasTrigger() {
    return (mask & OrderTag.TRIGGER.mask) != 0;
  }

	public void debug() {
		debug("");
	}

	private void debug(String decal) {
		System.err.println(""+order+" score => "+score);
		
		for (Node n : childs) {
			n.debug(decal+"  ");
		}
	}

  public String debugOrders() {
    Node current =this;
    String result = "";
    while (current.order != null && current.order.tag != null && current != null) {
      result = current.order+" "+result;
      current = current.parent;
    }
    return result;
  }
}
