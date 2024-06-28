package ooc.sim;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.Cooldown;
import ooc.Direction;
import ooc.Oracle;
import ooc.Player;
import ooc.P;
import ooc.State;
import ooc.orders.Charge;
import ooc.orders.Order;
import ooc.trailmapper.TNCache;
import ooc.trailmapper.TrailMapper;
import ooc.trailmapper.TrailNode;

public class SimulatorTest {

	private State oldState, newState;
	private Simulator sim;
	private TrailMapper mapper;

	@BeforeEach
	public void setup() {
		Player.oracle = new Oracle(Player.map);

		oldState = new State();
		oldState.myLife = 6;
		oldState.myPos = P.get(7, 7);
		oldState.map = Player.map;
		
		newState = new State();
		sim = new Simulator(true);
		mapper = new TrailMapper(Player.map);
	}

	@Test
	void whenMoveNorthOrderThenPositionIsMoved() throws Exception {
		oldState.myPos = P.get(7, 7);

		boolean result = sim.run(oldState, newState, mapper, Order.move(Direction.NORTH, Charge.SILENCE));

		assertThat(newState.myPos).isEqualTo(P.get(7, 6));
	}

	@Test
	void whenSurfaceThenVisitedCellsIsCleared() throws Exception {
		setVisited(oldState, P.get(7, 7));
		setVisited(oldState, P.get(8, 10));
		setVisited(oldState, P.get(12, 3));

		boolean result = sim.run(oldState, newState, mapper, Order.surface(oldState.myPos));

		assertThat(newState.visitedCells.get(oldState.myPos.o)).isTrue();
		assertThat(newState.visitedCells.get(P.get(8, 10).o)).isFalse();
	}

	@Test
	void whenSurfaceThenLifeIsDecremented() throws Exception {
		oldState.myLife = 12;
		
		boolean result = sim.run(oldState, newState, mapper, Order.surface(oldState.myPos));

		assertThat(newState.myLife).isEqualTo(11);
	}

	@Test
	void whenMoveThenPosMovedAndCellIsVisited() throws Exception {
		oldState.myPos = P.get(7, 7);
		boolean result = sim.run(oldState, newState, mapper, Order.move(Direction.NORTH, Charge.MINE));

		assertThat(newState.myPos).isEqualTo(P.get(7, 6));
		assertThat(newState.isVisitedCells(P.get(7, 6))).isTrue();
	}

	@Test
	void whenSilenceThenPosMovedAndCellsAreVisited() throws Exception {
		oldState.myPos = P.get(7, 7);
		oldState.cooldowns.charge(Cooldown.SILENCE, 0);
		
		boolean result = sim.run(oldState, newState, mapper, Order.silence(Direction.EAST, 4));

		assertThat(newState.myPos).isEqualTo(P.get(11, 7));
		assertThat(newState.isVisitedCells(P.get(8, 7))).isTrue();
		assertThat(newState.isVisitedCells(P.get(9, 7))).isTrue();
		assertThat(newState.isVisitedCells(P.get(10, 7))).isTrue();
		assertThat(newState.isVisitedCells(P.get(11, 7))).isTrue();
	}

	@Test
	void whenFireDirectTorpedoOnOppWith1RealityThenOppLoses2Points() throws Exception {
    oldState.cooldowns.charge(Cooldown.TORPEDO, 0);

		setOppCurrentLife(10);
		setOppAtPos(P.get(7, 7));

		sim.run(oldState, newState, mapper, Order.torpedo(P.get(7, 7)));

		assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(2.0);
	}

	@Test
	void whenFireNeighborTorpedoOnOppWith1RealityThenOppLoses1Points() throws Exception {
	  oldState.cooldowns.charge(Cooldown.TORPEDO, 0);

		setOppCurrentLife(10);
		setOppAtPos(P.get(7, 7));

		sim.run(oldState, newState, mapper, Order.torpedo(P.get(6, 6)));

		assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(1.0);
	}

	@Test
	void whenFireNeighborTorpedoOnOppWith2RealityThenOppLoses1Points() throws Exception {
	  oldState.cooldowns.charge(Cooldown.TORPEDO, 0);

		setOppCurrentLife(10);
		setOppAtPos(P.get(7, 7), P.get(7, 6));

		sim.run(oldState, newState, mapper, Order.torpedo(P.get(6, 6)));

		assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(1.0);
	}

	@Test
	void whenFireNeighborTorpedoOnOppWith1RealityNextAnd1AwayThenOppLosesHalfPoints() throws Exception {
	  oldState.cooldowns.charge(Cooldown.TORPEDO, 0);

		setOppCurrentLife(10);
		setOppAtPos(P.get(7, 7), P.get(1, 1));

		sim.run(oldState, newState, mapper, Order.torpedo(P.get(6, 6)));

		Assertions.assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(0.5);
	}

	@Test
	void whenUsingSonarThenSonarCooldownIsResetted() throws Exception {
	  oldState.cooldowns.charge(Cooldown.SONAR, 0);
		
		sim.run(oldState, newState, mapper, Order.sonar(4));
		
		assertThat(newState.cooldowns.sonarCooldown()).isEqualTo(Cooldown.MAX_SONAR_COOLDOWN);
	}
	
	@Test
	void whenUsingMineThenMineCooldownIsResetted() throws Exception {
	  oldState.cooldowns.charge(Cooldown.MINE, 0);
		
		sim.run(oldState, newState, mapper, Order.mine(Direction.NORTH));
		
		assertThat(newState.cooldowns.mineCooldown()).isEqualTo(Cooldown.MAX_MINE_COOLDOWN);
	}

	@Test
	void whenUsingMineNorthThenStateHasMineNorth() throws Exception {
	  oldState.cooldowns.charge(Cooldown.MINE, 0);
		oldState.myPos = P.get(7, 7);
		
		sim.run(oldState, newState, mapper, Order.mine(Direction.NORTH));
		
		assertThat(newState.myMines.get(P.get(7, 6).o)).isTrue();
	}

	@Test
	void whenTriggeringMineThenDamageAreDoneToOppAndMineIsGone() throws Exception {
		oldState.myPos = P.get(7, 7);
		oldState.oppLife = 10;
		setOppAtPos(P.get(1, 1));
		
		oldState.myMines.set(P.get(1,1).o);
		
		sim.run(oldState, newState, mapper, Order.trigger(P.get(1, 1)));
		
		assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(2);
		assertThat(newState.myMines.get(P.get(1, 1).o)).isFalse();
	}
		
	@Test
	void whenTriggeringMineThenDamageAreDoneToMe() throws Exception {
		oldState.myPos = P.get(7, 7);
		oldState.myLife = 10;
		oldState.oppLife = 10;
		setOppAtPos(P.get(1, 1));
		
		oldState.myMines.set(P.get(7,7).o);
		
		sim.run(oldState, newState, mapper, Order.trigger(P.get(7, 7)));
		
		assertThat(newState.torpedoHitStat.potentialDamage).isEqualTo(0);
		assertThat(newState.myLife).isEqualTo(8);
	}
	
	private void setOppAtPos(P... pos) {
		mapper.clearCurrentLayer();

		for (P p : pos) {
			TrailNode reality = TNCache.pop();
			reality.currentPos = p;
			mapper.currentLayer[mapper.currentLayerFE++] = reality;
		}

		mapper.teardownTurn();
	}

	private void setOppCurrentLife(int i) {
		oldState.oppLife = 10;
	}

	private void setVisited(State state, P pos) {
		state.visitedCells.set(pos.o);
	}

}
