package ooc.ai;

import java.util.List;

import ooc.Oracle;
import ooc.State;
import ooc.ai.search.Node;
import ooc.orders.Order;
import ooc.orders.Orders;
import ooc.trailmapper.TrailMapper;

public interface AI {

	List<Order> think(Node node);

	double scoreState(Node node);

	TrailMapper getOppMapper();

	Orders think(State state, Oracle oracle);

}
