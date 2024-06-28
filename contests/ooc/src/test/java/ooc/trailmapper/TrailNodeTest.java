package ooc.trailmapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.BitSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.Direction;
import ooc.P;
import ooc.PlayerTest;

public class TrailNodeTest {

	
	private TrailNode init;

	@BeforeAll
	public static void beforeAll() {
	  PlayerTest.emptyMap();
	}
	
	@BeforeEach
	public void setup() {
	}
	
	@Test
	void moveNorthThanSouthThenNoMorePotentialPositions() throws Exception {
		init = new TrailNode(P.get(7,7));
		TrailNode current = init.move(Direction.NORTH.direction);
		TrailNode result = current.move(Direction.SOUTH.direction);
		
		assertThat(result).isNull();
	}

	@Test
	void move15xNorthThenNoMorePotentialPositions() throws Exception {
		init = new TrailNode(P.get(7,14));
		TrailNode current = init;
		
		for (int i=0;i<15;i++) {
			current = current.move(Direction.NORTH.direction);
		}
		
		assertThat(current).isNull();
	}
	
	@Test
  void dropMineShouldSetMinesAroundCurrentPos() throws Exception {
    TrailNode node = new TrailNode(P.get(7, 7));
    node.dropMine();

    assertThat(node.mines.get(P.get(8, 7).o)).isTrue();
    assertThat(node.mines.get(P.get(6, 7).o)).isTrue();
    assertThat(node.mines.get(P.get(7, 8).o)).isTrue();
    assertThat(node.mines.get(P.get(7, 6).o)).isTrue();
  }
	
  @Test
  void shouldKeepTrailsWhereTriggeredMineIsPossible() throws Exception {
    TrailNode node = new TrailNode(P.get(7, 7));
    node.dropMine();
    
    TrailNode result = node.trigger(new BitSet(), P.get(8, 7));
    
    assertThat(result).isEqualTo(node);
  }

  @Test
  void shouldFilterTrailsWhereTriggeredMineIsImpossible() throws Exception {
    TrailNode node = new TrailNode(P.get(7, 7));
    node.dropMine();
    
    TrailNode result = node.trigger(new BitSet(), P.get(10, 10));
    
    assertThat(result).isNull();
  }

	
  
  @Test
  void whenMyTorpedoDeals0DamageAtAnotherPosition_ThenRealityIsKept() throws Exception {
    TrailNode node = new TrailNode(P.get(11, 10));
    
    node.damage(P.get(7, 7));
    
    TrailNode result = node.checkDamageDealt(0);
    
    assertThat(result).isEqualTo(node);
  }

  @Test
  void whenMyTorpedoHitsAtExactLocation_ThenPositionsIsKept() throws Exception {
    TrailNode node = new TrailNode(P.get(7, 7));
    
    node.damage(P.get(7, 7));
    assertThat(node.getDamageTaken()).isEqualTo(2);  
  }

  @Test
  void whenMyTorpedoHitsAtNeighborLocation_ThenPositionIsKept() throws Exception {
    TrailNode node = new TrailNode(P.get(7, 7));
    
    node.damage(P.get(6, 7));
    assertThat(node.getDamageTaken()).isEqualTo(1);  
  }
  
  @Test
  void when2HitsFor1PointsAnd2Torpedoes_ThenPositionKept() throws Exception {
    TrailNode node = new TrailNode(P.get(0, 11));
    
    node.resetDamageTaken();
    node.damage(P.get(0, 10));
    node.move(Direction.EAST.direction);
    node.fireTorpedo(P.get(2, 11));

    
    assertThat(node.getDamageTaken()).isEqualTo(2);
  }
  
}
