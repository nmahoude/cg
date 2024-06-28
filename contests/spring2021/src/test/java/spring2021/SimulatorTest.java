package spring2021;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SimulatorTest {

	
	private State state;

	@BeforeAll
	public static void initCells() {
		CellTest.initRandomCells();
	}
	
	@BeforeEach
	public void setup() {
		state = new State();
		initStaticState();
	}
	
	private void initStaticState() {
		for (int i=0;i<37;i++) {
			State.richness[i] = 0;
		}
	}

	@Test
	void growATree_MakeTheTreeBiggerAndDormant() throws Exception {
		growTreeAt(10, 1);
		
		Simulator.simulate(state, Action.grow(10), 0);
		
		assertThat(state.trees[10]).isEqualTo(2);
		assertThat(state.isDormant(10)).isTrue();
	}

	@Test
	void growATree_RaiseTreeCountOfNewSizeAndReduceOldSize() throws Exception {
		growTreeAt(11, 1);
		Assertions.assertThat(state.treesCount[5*0+1]).isEqualTo(1);
		Assertions.assertThat(state.treesCount[5*0+2]).isEqualTo(0);
		
		Simulator.simulate(state, Action.grow(11), 0);
		
		assertThat(state.treesCount[5*0+1]).isEqualTo(0);
		assertThat(state.treesCount[5*0+2]).isEqualTo(1);
	}
	
	@Test
	void complete_cutTheThreeAndReduceCountOfSize3() throws Exception {
		growTreeAt(25,3);
		
		assertThat(state.trees[25]).isEqualTo(3);
		assertThat(state.treesCount[5*0+3]).isEqualTo(1);

		Simulator.simulate(state, Action.complete(25), 0);
		
		assertThat(state.trees[25]).isEqualTo(-1);
		assertThat(state.treesCount[5*0+3]).isEqualTo(0);
	}
	
	@Test
	void seed_createSeedAndBothAreDormantAnd() throws Exception {
		growTreeAt(1,1);
		Simulator.simulate(state, Action.seed(1,3), 0);
		
		assertThat(state.trees[1]).isEqualTo(1);
		assertThat(state.trees[3]).isEqualTo(0);
		assertThat(state.isDormant(1)).isTrue();
		assertThat(state.isDormant(3)).isTrue();
	}
	
	@Test
	void seed_createSeedAndCellIsMine() throws Exception {
		growTreeAt(1,1);
		Simulator.simulate(state, Action.seed(1,3), 0);
		
		assertThat(state.trees[3]).isEqualTo(0);
		assertThat(state.isMine(3)).isTrue();
	}
	
	@Nested
	class Costs{
		@Test
		void sendASeedCostTheNumberOfActualSeeds() throws Exception {
			state.treesCount[5*0+0] = 17;
			setMySun(100);
			
			Simulator.simulate(state, Action.seed(1, 3), 0);
			
			assertThat(state.sun[0]).isEqualTo(100-17);
			
		}

		@Test
		void firstSeedIsFree() throws Exception {
			state.treesCount[5*0+0] = 0;
			setMySun(100);
			
			Simulator.simulate(state, Action.seed(1, 3), 0);
			
			assertThat(state.sun[0]).isEqualTo(100);
			
		}

		@Test
		void secondSeedCost1() throws Exception {
			state.treesCount[5*0+0] = 0;
			setMySun(100);
			
			Simulator.simulate(state, Action.seed(1, 3), 0);
			Simulator.simulate(state, Action.seed(1, 4), 0);
			
			assertThat(state.sun[0]).isEqualTo(99);
			
		}

		
		@Test
		void growSeedToTree1CostOnePlusNumberOfSize1Trees() throws Exception {
			state.treesCount[5*0+1] = 10;
			setMySun(100);
			
			Simulator.simulate(state, Action.grow(3), 0);
			
			assertThat(state.sun[0]).isEqualTo(100-1-10);
		}
		@Test
		void growTree2ToTree3Cost7PlusNumberOfSize3Trees() throws Exception {
			state.treesCount[5*0+3] = 3;
			setMySun(100);
			
			growTreeAt(10, 2);
			
			Simulator.simulate(state, Action.grow(10), 0);
			
			assertThat(state.sun[0]).isEqualTo(100-7-3);
		}
		
		@Test
		void completeATreeCosts4() throws Exception {
			setMySun(100);
			growTreeAt(36, 3);
			
			Simulator.simulate(state, Action.complete(36), 0);
			
			assertThat(state.sun[0]).isEqualTo(100-4);
		}
	}
	
	@Nested
	class Scores {
		@Test
		void completeTreeGiveNutrientsPointsAndDecreaseNutrients() throws Exception {
			setRichnessAt(25, 1);
			setMyScore(3);
			state.nutrients = 16;
			
			growTreeAt(25,3);
			
			Simulator.simulate(state, Action.complete(25), 0);
			
			assertThat(state.nutrients).isEqualTo(15);
			assertThat(state.score[0]).isEqualTo(3+16);
		}

		@Test
		void richness_1_GiveBonus_0() throws Exception {
			setRichnessAt(25, 1);
			setMyScore(3);
			state.nutrients = 16;
			
			growTreeAt(25,3);
			
			Simulator.simulate(state, Action.complete(25), 0);
			
			assertThat(state.score[0]).isEqualTo(3+16+0);
			
		}
		
		@Test
		void richness_2_GiveBonus_2() throws Exception {
			setRichnessAt(25, 2);
			setMyScore(3);
			state.nutrients = 16;
			
			growTreeAt(25,3);
			
			Simulator.simulate(state, Action.complete(25), 0);
			
			assertThat(state.score[0]).isEqualTo(3+16+2);
			
		}

		@Test
		void richness_3_GiveBonus_4() throws Exception {
			setRichnessAt(25, 3);
			setMyScore(3);
			state.nutrients = 16;
			
			growTreeAt(25,3);
			
			Simulator.simulate(state, Action.complete(25), 0);
			
			assertThat(state.score[0]).isEqualTo(3+16+4);
			
		}

		
		@Test
		void scoresAThirdOfSRemainingSunPointsAndTheEndOfGame() throws Exception {
			state.day = 23;
			state.score[0] = 111;
			state.sun[0] = 33;
			
			Simulator.endTurn(state);
			
			assertThat(state.score[0]).isEqualTo(122);
		}
		
	}
	@Nested
	class EndTurn {
		@Test
		void allDormantAreWokenUpWhenNewDay() throws Exception {
			state.setDormant(0);
			state.setDormant(10);
			state.setDormant(36);
			
			Simulator.endTurn(state);
			
			assertThat(state.isDormant(0)).isFalse();
			assertThat(state.isDormant(10)).isFalse();
			assertThat(state.isDormant(36)).isFalse();
		}
	}
	
	@Nested
	class Richness {
		@Test
		void plantingASeedOnaRichness4CellAdd4ToTotal() throws Exception {
			setRichnessAt(17, 4);
			
			Simulator.simulate(state, Action.seed(16,17), 0);
			
			assertThat(state.currentTotalRichness).isEqualTo(4);
		}
		@Test
		void growingATreeOnaRichness4CellAddNothingToTotal() throws Exception {
			state.currentTotalRichness = 121;
			setRichnessAt(17, 4);
			growTreeAt(17, 2);
			Simulator.simulate(state, Action.grow(17), 0);
			
			assertThat(state.currentTotalRichness).isEqualTo(121);
		}
		@Test
		void completeATreeOnaRichness4CellRemove4toTotal() throws Exception {
			state.currentTotalRichness = 121;
			setRichnessAt(17, 4);
			growTreeAt(17, 3);
			Simulator.simulate(state, Action.complete(17), 0);
			
			assertThat(state.currentTotalRichness).isEqualTo(117);
		}
	}
	
	@Nested
	class SunPoints {
		@Test
		void countPointsWithoutShadows() throws Exception {
			state.day = 5;
			growTreeAt(0,1);
			growTreeAt(10,2);
			growTreeAt(25,1);
			growTreeAt(15,3);
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(7);
			assertThat(state.sun[1]).isEqualTo(0);
		}
		
		@Test
		void addSunsAtTheEndOfTurn() throws Exception {
			state.day = 5;
			growTreeAt(0,1);
			growTreeAt(10,2);
			growTreeAt(25,1);
			growTreeAt(15,3);
			setMySun(5);
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(5+7);
			assertThat(state.sun[1]).isEqualTo(0);
		}
		
		@Test
		void size1toNextCell_shadow() throws Exception {
			setMySun(0);
			state.day = 5;
			growTreeAt(0,2); // 2 points
			growTreeAt(1,1); // 0 point
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(2);
		}
		
		@Test
		void size1to2CellsFurther_noshadow() throws Exception {
			setMySun(0);
			state.day = 5;
			growTreeAt(0,1);
			growTreeAt(7,1);
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(2);
		}
		@Test
		void size2to2CellsFurther_noshadow() throws Exception {
			setMySun(0);
			state.day = 5;
			growTreeAt(0,2); // 2 points
			growTreeAt(1,1); // 0 points
			growTreeAt(7,1); // 0 points
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(2);
		}

		@Test
		void size3to3CellsFurther_noshadow() throws Exception {
			setMySun(0);
			state.day = 5;
			growTreeAt(0,2); // 2 points
			growTreeAt(1,2); // 0 points
			growTreeAt(7,2); // 0 points
			growTreeAt(19,2); // 0 points
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(2);
		}

		@Test
		void size3to3CellsFurther_noshadowIfAsBig() throws Exception {
			setMySun(0);
			state.day = 5;
			growTreeAt(0,2); // 2 points
			growTreeAt(1,2); // 0 points
			growTreeAt(7,2); // 0 points
			growTreeAt(19,3); // 3 points
			
			Simulator.endTurn(state);
			
			assertThat(state.sun[0]).isEqualTo(5);
		}
	}
	
	private void growTreeAtForHim(int cellIndex, int size) {
		growTreeAt(cellIndex, size, 1);
	}
	
	private void growTreeAt(int cellIndex, int size) {
		growTreeAt(cellIndex, size, 0);
	}
	private void growTreeAt(int cellIndex, int size, int index) {
		if (state.trees[cellIndex] != 0) {
			state.treesCount[5*index + state.trees[cellIndex]]--;
		}
		state.trees[cellIndex] = size; // size one tree
		state.treesCount[5*index+size]++;
		state.setMine(cellIndex);
	}
	
	private void setMyScore(int score) {
		state.score[0] = score;
	}
	
	private void setMySun(int sun) {
		state.sun[0] = sun;
	}
	
	private void setRichnessAt(int index, int value) {
		State.richness[index] = value;
	}

}
