package ooc.sim;

import ooc.Direction;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.orders.Order;
import ooc.orders.OrderTag;
import ooc.trailmapper.TrailMapper;

public class Simulator {
  public boolean debug =false;
  private boolean real;
	
  public Simulator(boolean real) {
    this.real = real;
  }
  
	public boolean run(State init, State state, TrailMapper mapper, Order order) {
		state.copyFrom(init);
		return applySimulation(state, mapper, order);
	}

  public boolean applySimulation(State state, TrailMapper mapper, Order order) {
		if (order.tag == OrderTag.MOVE) {
			Direction direction = Direction.from(order.value);
			int charge = order.pos.x;

			return move(state, direction, charge);
			
		} else if (order.tag == OrderTag.SURFACE) {
			state.resetVisitedCells();
			state.myLife = Math.max(0,  state.myLife-1);
			state.setVisitedCell(state.myPos);
      state.fastDetector.surface(order.value);
			return true;
		} else if (order.tag == OrderTag.SILENCE) {
			if (state.cooldowns.silenceCooldown() != 0) return false;
			state.cooldowns.resetSilence();
      state.fastDetector.silence(state.myPos, state.visitedCells);
			
			int dir = order.value;
			int length = order.pos.x;
			for (int l=0;l<length;l++) {
				state.myPos = state.myPos.neighbors[dir];
				state.setVisitedCell(state.myPos);
			}
			return true;
		} else if (order.tag == OrderTag.TORPEDO) {
			if (state.cooldowns.torpedoCooldown()!= 0) return false;
			state.cooldowns.resetTorpedo();

			state.myLife -= getDamageWhenFireAt(state, order.pos);
			
			mapper.updateStatisticalDamageAt(state.torpedoHitStat, order.pos);
			
      state.fastDetector.torpedo(order.pos);
      return true;
		} else if (order.tag == OrderTag.MINE) {
			if (state.cooldowns.mineCooldown() != 0) return false;
			state.cooldowns.resetMine();
			if (real) {
			  state.myMines.set(state.myPos.neighbors[order.value].o);
			  State.myTrueMinesPos.add(state.myPos.neighbors[order.value]);
			}
			
      state.fastDetector.mine(Direction.from(order.value));
      return true;
		} else if (order.tag == OrderTag.TRIGGER) {
			state.myLife -= getDamageWhenFireAt(state, order.pos);
			state.myMines.clear(order.pos.o);
			
			if (real) {
			  State.myTrueMinesPos.remove(order.pos);
			}
			mapper.updateStatisticalDamageAt(state.torpedoHitStat, order.pos);
			
      state.fastDetector.trigger(Player.oracle.myMapper.trailCountByMineAtPos, 20*order.pos.o, Math.min(19,Player.oracle.myMapper.trailCountByMineAtPosFE[order.pos.o]));
      return true;

		} else if (order.tag == OrderTag.SONAR) {
			if (state.cooldowns.sonarCooldown() != 0) return false;
			state.cooldowns.resetSonar();
			
      state.fastDetector.sonar(order.value);
      return true;
		} else {
			return false;
		}
  }

	public boolean move(State state, Direction direction, int charge) {
		state.myPos = state.myPos.neighbors[direction.direction];
		if (Player.map.isIsland(state.myPos)) return false;
		if (state.isVisitedCells(state.myPos)) return false;
		
		state.fastDetector.move(direction);
		state.setVisitedCell(state.myPos);
		state.cooldowns.charge(charge);
		return true;
	}

	private int getDamageWhenFireAt(State state, P target) {
		if (target == P.I) return 0;

		int distToTorpedo = state.myPos.blastDistance(target);
    if (distToTorpedo == 0) {
        return 2;
    } else if (distToTorpedo == 1) {
        return 1;
    } else {
        return 0;
    }
	}
}
