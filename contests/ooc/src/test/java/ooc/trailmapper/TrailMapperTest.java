package ooc.trailmapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.OOCMapTest;
import ooc.P;
import ooc.Player;

public class TrailMapperTest {

	private TrailMapper mapper;


	@BeforeEach
	public void setup() {
	  OOCMapTest.emptyMap(Player.map);
		mapper = new TrailMapper(Player.map);
		mapper.clearCurrentLayer();
	}
	
	@Test
	void whenAggregatingRealitiesThenTrailIsCleared() throws Exception {
		TrailNode node1 = TNCache.pop();
		node1.currentPos = P.get(7, 7);
		node1.trail.set(100);
		
		TrailNode node2 = TNCache.pop();
		node2.currentPos = P.get(7, 7);
		node2.trail.set(101);

		setupRealities(node1, node2);
		
		
		mapper.aggregatePositions();
		
		assertThat(mapper.currentLayer[0].currentPos).isEqualTo(P.get(7, 7));
		assertThat(mapper.currentLayer[0].trail.isEmpty()).isTrue();
	}

	@Test
	void whenAggregatingRealitiesThenOneTrailRemainingPerPosition() throws Exception {
		TrailNode node1 = TNCache.pop();
		node1.currentPos = P.get(7, 7);
		node1.trail.set(100);
		
		TrailNode node2 = TNCache.pop();
		node2.currentPos = P.get(7, 7);
		node2.trail.set(101);

		setupRealities(node1, node2);
		
		
		mapper.aggregatePositions();
		
		assertThat(mapper.currentLayerFE).isEqualTo(1);
	}
	
	@Test
	void whenAggregatingRealitiesThenMinesAreAggregatedToo() throws Exception {
		TrailNode node1 = TNCache.pop();
		node1.currentPos = P.get(7, 7);
		node1.mines.set(100);
		
		TrailNode node2 = TNCache.pop();
		node2.currentPos = P.get(7, 7);
		node2.mines.set(101);

		setupRealities(node1, node2);
		
		
		mapper.aggregatePositions();

		assertThat(mapper.currentLayer[0].currentPos).isEqualTo(P.get(7, 7));
		assertThat(mapper.currentLayer[0].mines.get(100)).isTrue();
		assertThat(mapper.currentLayer[0].mines.get(101)).isTrue();
	}
	
	
	@Test
	void whenOnlyOnePositionAndDirectHitThenExpectedDamageIs2() throws Exception {
		setupRealities(at(7,7));
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(7, 7));
		
		assertThat(stat.potentialDamage).isEqualTo(2.0);
	}
	
	@Test
	void whenOnlyOnePositionAndIndirectHitThenExpectedDamageIs1() throws Exception {
		setupRealities(at(7,7));
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(7, 8));
		
		assertThat(stat.potentialDamage).isEqualTo(1.0);
	}

	
	@Test
	void whenClusterOfPositionsExpectedDamageIsGreaterThanOne() throws Exception {
		/*
		 * x..
		 * xx.
		 * ..x
		 */
		setupRealities(at(7,7), 
									 at(7,8), at(8,8), 
									 at(9,9));
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(8, 8));
		
		assertThat(stat.potentialDamage).isEqualTo(5.0 / 4);
	}
	
	@Test
	void whenFullClusterButOnExteriorThanMinimalDamageIsZero() throws Exception {
		/*
		 * xxx
		 * xxx
		 * xxx and one elsewhere
 		 */
		setupRealities(at(7,7), at(8,7), at(9, 7),
									 at(7,8), at(8,8), at(9,8),
									 at(7,9), at(8,9), at(9,9),
									 
									 at(0,0));
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(8, 8));
		
		assertThat(stat.minimalDamage).isEqualTo(0);
	}
	
	@Test
	void whenFullClusterThenMinimalDamageIsOne() throws Exception {
		/*
		 * xxx
		 * xxx
		 * xxx
 		 */
		setupRealities(at(7,7), at(8,7), at(9, 7),
									 at(7,8), at(8,8), at(9,8),
									 at(7,9), at(8,9), at(9,9)
									 );
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(8, 8));
		
		assertThat(stat.minimalDamage).isEqualTo(1);
	}
	
	@Test
	void whenOnlyOnePositionAndFireDirectlyThenMinimalDamageIsTwo() throws Exception {
		/*
		 *    
		 *  x 
		 *    
 		 */
		setupRealities(at(7,7)
									 );
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(7, 7));
		
		assertThat(stat.minimalDamage).isEqualTo(2);
	}

  @Test
  void whenOnlyOnePositionAndDirectTorpedoAndTriggerThenMinimalDamageIs_4() throws Exception {
    setupRealities(at(7,7)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // trigger
    
    assertThat(stat.minimalDamage).isEqualTo(4);
  }

  @Test
  void whenOnlyOnePositionAndDirectTorpedoAndNearTriggerThenMinimalDamageIs_3() throws Exception {
    setupRealities(at(7,7)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // direct torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(7, 8)); // near trigger
    
    assertThat(stat.minimalDamage).isEqualTo(3);
  }

  @Test
  void whenOnlyOnePositionAndNearTorpedoAndDirectTriggerThenMinimalDamageIs_3() throws Exception {
    setupRealities(at(7,7)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 8)); // near torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // direct trigger
    
    assertThat(stat.minimalDamage).isEqualTo(3);
  }

  @Test
  void when_2_PositionsAndDirectTorpedoAndDirectTriggerThenMinimalDamageIs_2() throws Exception {
    setupRealities(at(7,7),
                   at(10,10)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(10, 10)); // trigger
    
    assertThat(stat.minimalDamage).isEqualTo(2);
  }

  @Test
  void when_2_PositionsAndNearTorpedoAndDirectTriggerThenMinimalDamageIs_1() throws Exception {
    setupRealities(at(7,7),
                   at(10,10)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 8)); // torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(10, 10)); // trigger
    
    assertThat(stat.minimalDamage).isEqualTo(1);
  }

  @Test
  void when_2_PositionsAndDirectTorpedoAndNearTriggerThenMinimalDamageIs_1() throws Exception {
    setupRealities(at(7,7),
                   at(10,10)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(10, 9)); // trigger
    
    assertThat(stat.minimalDamage).isEqualTo(1);
  }
  
  @Test
  void when_3_PositionsAndDirectTorpedoAndDirectTriggerThenMinimalDamageIs_0() throws Exception {
    setupRealities(at(0,0), 
                   at(7,7),
                   at(10,10)
                   );
    
    mapper.teardownTurn();
    
    TorpedoHitStat stat = new TorpedoHitStat();
    mapper.updateStatisticalDamageAt(stat , P.get(7, 7)); // torpedo
    mapper.updateStatisticalDamageAt(stat , P.get(10, 10)); // trigger
    
    assertThat(stat.minimalDamage).isEqualTo(0);
  }
  
  @Test
	void whenClusterOfPositionsButOtherPositionsThenExpectedDamageIsLowerThanOne() throws Exception {
		/*
		 * x..
		 * xx.
		 * ..x
		 */
		setupRealities(at(7,7), 
									 at(7,8), at(8,8), 
									 at(9,9),
									 // other positions to lower expected damage
									 at(0,0), at(1,0), at(2, 0), at(3,0), at(4, 0)
				);
		
		mapper.teardownTurn();
		
		TorpedoHitStat stat = new TorpedoHitStat();
		mapper.updateStatisticalDamageAt(stat , P.get(8, 8));
		
		assertThat(stat.potentialDamage).isLessThan(1.0);
	}

	private TrailNode at(int x, int y) {
		TrailNode node = TNCache.pop();
		node.currentPos = P.get(x, y);
		return node;
	}

	private void setupRealities(TrailNode... nodes) {
		for (TrailNode node : nodes) {
			mapper.currentLayer[mapper.currentLayerFE++] = node;
		}
		mapper.calculatePotentialPositions();
	}

}
