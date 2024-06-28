package ooc.ai.search;

import java.util.ArrayList;
import java.util.List;

import ooc.Player;
import ooc.State;
import ooc.ai.AI;
import ooc.orders.Order;
import ooc.orders.Orders;
import ooc.sim.Simulator;

public class Search {
	private static final Simulator SIMULATOR = new Simulator(false);
	
  private static final int MAX_NODES = 10_000;
  static Node nodes[] = new Node[MAX_NODES];
	public static int nodesFE = 0;
	static {
	  for (int i=0;i<MAX_NODES;i++) {
	    nodes[i] = new Node(0);
	  }
	}
	
	
	public Node root = new Node(0);

	private AI ai;

	public void process(AI ai, State init) {
	  this.ai = ai;
	  nodesFE = 0;

	  decal = 0;
		root.mask = 0;
		root.reset();
		root.state.copyFrom(init);
		root.order = Order.NONE;
    if (Player.D_SEARCH && Player.turn >= Player.D_SEARCH_TURN) {
      ai.scoreState(root);
      System.err.println("ROOT"+SearchAI.scorer.debug());
    }
		computeNode(root);
	}

	private void computeNode(Node currentNode) {
	  List<Order> possibleOrders = ai.think(currentNode);
		for (Order order : possibleOrders) {
			visitNode(currentNode, order);
		}
	}

	public static int decal;
	private void visitNode(Node currentNode, Order order) {
	  decal+=2;
	  
		Node node = nodes[nodesFE++];
		node.reset();
		node.mask = currentNode.mask | order.tag.mask;
    node.parent = currentNode;
    node.order = order;

    int MAX_DEBUG_DEPTH = 3 * 2;
    if (Player.D_PERF && decal <= MAX_DEBUG_DEPTH) {
      Player.time(String.format("%"+decal+"s", "") +"visiting "+order);
    }
    
    SIMULATOR.run(currentNode.state, node.state, ai.getOppMapper(), order);
    if (Player.D_PERF && decal <= MAX_DEBUG_DEPTH) {
      Player.time(String.format("%"+decal+"s", "") +"end of sim "+order);
    }
    node.score = ai.scoreState(node);
		
    if (Player.D_SEARCH && Player.turn >= Player.D_SEARCH_TURN && decal <= MAX_DEBUG_DEPTH) {
      System.err.println(String.format("%"+decal+"s", "") + ""+node.order.output()
                                + SearchAI.scorer.debug()
                                );
    }
		currentNode.addChild(node);
		
		if (node.state.myLife > 0) {
		  computeNode(node);
		}
		if (Player.D_PERF && decal <= MAX_DEBUG_DEPTH) {
      Player.time(String.format("%"+decal+"s", "") +"end of childs "+order);
    }
		decal-=2;
	}

	public void debug() {
		root.debug();
		
	}
	public Orders fillBestOrders(Orders orders) {
		orders.clear();
		
		Node bestNode = root.findBestNode();

		Node current = bestNode;
		List<Order> orderList = new ArrayList<>();
		while (current != root) {
		  orderList.add(0, current.order);
			current = current.parent;
		}

		if (Player.D_SEARCH) {
  		System.err.println("BEST SEARCH NODE : with score "+bestNode.score);
		}
		for (Order o : orderList) {
		  if (Player.D_SEARCH) {
		    System.err.println("  -> "+o.output());
		  }
		  orders.addOrder(o);
		}
		return orders;
	}
}
