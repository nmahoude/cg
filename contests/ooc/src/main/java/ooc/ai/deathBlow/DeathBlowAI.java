package ooc.ai.deathBlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ooc.Direction;
import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.ai.AI;
import ooc.ai.search.Node;
import ooc.ai.search.thinkers.MoveThinker;
import ooc.charge.ChargeAI;
import ooc.orders.Charge;
import ooc.orders.Order;
import ooc.orders.Orders;
import ooc.trailmapper.TrailMapper;

/** look for the final blow */
public class DeathBlowAI implements AI, ChargeAI {

	Orders orders = new Orders();
	private Oracle oracle;
	MoveThinker moves = new MoveThinker();
	
	
	public DeathBlowAI() {
    moves.setChargeAI(this);
  }
	
	public Orders think(State state, Oracle oracle) {
		this.oracle = oracle;
		
		orders.clear();
		
    Player.search.process(this, state);
    Player.search.fillBestOrders(orders);
    
    return orders;
	}
	
	@Override
	public List<Order> think(Node node) {
		if (node.hasTorpedo()) return Collections.emptyList();
		
		List<Order> orders = new ArrayList<>();
		if (!node.hasMove()) {
			orders.addAll(moves.calculateOrders(oracle, node.state));
		}
		if (!node.hasSilence() && node.state.cooldowns.silenceCooldown() == 0) {
			orders.addAll(allPossibleSilences(node.state));
		}
		if (node.state.cooldowns.torpedoCooldown() == 0) {
			// find the killing torpedo ?
			orders.addAll(allTorpedoes(node.state));
		}
		return orders;
	}

	private Collection<? extends Order> allTorpedoes(State state) {
		List<Order> orders = new ArrayList<>();
		for (P p : Player.map.torpedoTargetsByPos.get(state.myPos)) {
			orders.add(Order.torpedo(p));
		}
		return orders;
	}

	private Collection<? extends Order> allPossibleSilences(State state) {
		List<Order> orders = new ArrayList<>();
		
		for (int d=0;d<4;d++) {
			P current = state.myPos;
			for (int l=1;l<=4;l++) {
				current = current.neighbors[d];
				if (Player.map.isIsland(current) || state.isVisitedCells(current)) break;
				orders.add(Order.silence(Direction.from(d), l));
			}
		}
		
		return orders;
	}

	@Override
	public double scoreState(Node node) {
		
		if (node.state.myLife > 0 && node.state.maximalOppLife() <= 0) {
			return 1.0;
		}
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public TrailMapper getOppMapper() {
		return oracle.oppMapper;
	}

	@Override
	public Charge calculateCharge(State state) {
		if (state.cooldowns.torpedoCooldown() != 0) {
			return Charge.TORPEDO;
		} else {
			return Charge.SILENCE;
		}
	}

}
