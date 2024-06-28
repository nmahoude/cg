package fall2022.minimax;

import static fall2022.minimax.MM2DNeutralResult.attack;
import static fall2022.minimax.MM2DResult.keep;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MM2DFightDeciderTest {

	@Test
	void justMeVersusNobody() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(0).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(0).free(1).reinforce(0).withWon(),
				keep(1).free(0).reinforce(0) 
		);
	}

	@Test
	void justMeVersusNobodyButICannotMe() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(1).movable(0).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(0).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(0).free(0).reinforce(0) 
		);
	}
	
	
	@Test
	void justMeVersusOneOpp() throws Exception {
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(1).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(0).reinforce(0) 
		);
	}

	@Test
	void justMeVersusOneOppButOneBlocked() throws Exception {
		MM2DEntry me = MM2DEntry.blocked(1).movable(1).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(1).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(0).free(1).reinforce(0) 
		);
	}

	@Test
	void justMeVersus2Opp() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);
		
		
		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).isEmpty();
	}

	@Test
	void justMeVersus2OppWithOneBlock() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(1).movable(1).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);
		
		
		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(0).reinforce(0) 
		);
	}

	@Test
	void MeWith2Versus2OppWithOneBlock() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(1).movable(2).reinforcement(0);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);
		
		
		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(1).reinforce(0) 
		);
	}

	@Test
	void MeAndReinforcementVersus2Opp() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(1);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(0).reinforce(1) 
		);
	}

	@Test
	void MeAndLotReinforcementVersus2Opp() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(3);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(0).free(1).reinforce(2)
		);
	}

	@Test
	void MeAndLotReinforcementVersus2OppWithOneRein() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(1).reinforcement(3);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(1);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(0).free(1).reinforce(2)
		);
	}
	
	@Test
	void MoreArmy() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(3).reinforcement(1);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(1);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(2).reinforce(1) 
		);
	}
	
	@Test
	void canWinAndShouldStopHere() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(10).reinforcement(1);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(5).reinforcement(0);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(4).free(6).reinforce(1).withWon(), 

				keep(5).free(5).reinforce(0)
				 
		);
	}

	@Test
	void lotOfOppReinforcement() throws Exception {
		// units, spawn, reinforcement
		MM2DEntry me = MM2DEntry.blocked(0).movable(4).reinforcement(1);
		MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(10);

		List<MM2DResult> results = new MM2DFightDecider().resolve(me, opp);

		assertThat(results).containsExactlyInAnyOrder(
				keep(1).free(3).reinforce(1)
				 
		);
	}

	@Nested
	class WithNeutral {
		
		@Test
		void empty() throws Exception {
			// units, spawn, reinforcement
			MM2DEntry me = MM2DEntry.blocked(0).movable(0).reinforcement(0);
			MM2DEntry opp = MM2DEntry.blocked(0).movable(0).reinforcement(0);

			List<MM2DNeutralResult> results = new MM2DFightDecider().resolveNeutral(me, opp);

			assertThat(results).containsExactlyInAnyOrder(
					attack(0).free(0)
			);
		}
		
		@Test
		void iCanWin() throws Exception {
			// units, spawn, reinforcement
			MM2DEntry me = MM2DEntry.blocked(1).movable(2).reinforcement(0);
			MM2DEntry opp = MM2DEntry.blocked(0).movable(1).reinforcement(0);

			List<MM2DNeutralResult> results = new MM2DFightDecider().resolveNeutral(me, opp);

			assertThat(results).containsExactlyInAnyOrder(
					attack(2).free(0).withWon()
			);
		}
		
		@Test
		void ICantWinButICanPreventHimToo() throws Exception {
			// units, spawn, reinforcement
			MM2DEntry me = MM2DEntry.blocked(1).movable(2).reinforcement(0);
			MM2DEntry opp = MM2DEntry.blocked(0).movable(2).reinforcement(0);

			List<MM2DNeutralResult> results = new MM2DFightDecider().resolveNeutral(me, opp);

			assertThat(results).containsExactlyInAnyOrder(
					attack(2).free(0)
			);
		}
		
		@Test
		void NeedToSpawn() throws Exception {
			// units, spawn, reinforcement
			MM2DEntry me = MM2DEntry.blocked(0).movable(0).reinforcement(1);
			MM2DEntry opp = MM2DEntry.blocked(0).movable(0).reinforcement(0);

			List<MM2DNeutralResult> results = new MM2DFightDecider().resolveNeutral(me, opp);

			assertThat(results).containsExactlyInAnyOrder(
					attack(0).free(0)
			);
		}
		
		
	}

}
