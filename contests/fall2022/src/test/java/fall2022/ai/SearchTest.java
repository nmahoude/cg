package fall2022.ai;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fall2022.Pos;
import fall2022.State;
import fall2022.TITest;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.ai2.AI2;
import fall2022.ai.ai2.Defense;

public class SearchTest {

	
	private static final double[] EMPTY_DANGER = new double[Pos.MAX_OFFSET];
	private State state;
	private List<Ilot> ilots = Collections.emptyList();
	private TimeTraveler tt;

	@BeforeEach
	public void setup() {
		state = TITest.read("""
	Global 
	^15 7
	Optional State : 
	^ 8
	^36 35
	^ 8192 12288  8192  9234 13319 21510  7175 14857 14849  8704 12800 12800 18960 20992  8192 
	^    0 16384 20480 12289  5129  5127 17414  7187 13318     0  4609  8704  8704  6657 18960 
	^20480 12288 16384 12288 11271  9223 15367 11273  7187     0  4617     0 14849 14857 14849 
	^16384 20480  8192 10241  9225  5129 11271  7175  8713  2561     0 16896  4609 18961 16896 
	^16384 16384 16384  3078  7175  9223 17414 13319 11273 12801 16896  8705 12809  8705 20992 
	^18432  8192  8192     0  7177     0 13318 11271 13321  6657  8736 16896 16897 16896     0 
	^ 8192 20480 18432 12288     0  8192 20480 21554 10769 20992 16896  8704  8704 12816  8720
	""");

		tt = new TimeTraveler();
		tt.init(state);
		
		new AI2().think(state);
		
	}
	
	@Test
	@DisplayName("Already filled !")
	void easy() throws Exception {
		Search search = new Search();
		search.goalNeeded[Pos.from(7,1).o] = 1;
		search.goalNeeded[Pos.from(8,2).o] = 1;
	
		search.search(state, tt, ilots , Arrays.asList(
				Pos.from(7,1), 
				Pos.from(8,2)), EMPTY_DANGER);
		
	}

	@Test
	@DisplayName("Need one spawn!")
	void oneSpawn() throws Exception {
		Search search = new Search();
		search.goalNeeded[Pos.from(7,1).o] = 2;
		search.goalNeeded[Pos.from(8,2).o] = 1;
	
		state.myMatter =10;
		
		search.search(state, tt, ilots, Arrays.asList(
				Pos.from(7,1), 
				Pos.from(8,2)), EMPTY_DANGER);
		
	}

	@Test
	@DisplayName("Need one spawn on each cell!")
	void oneSpawnOnEachCell() throws Exception {
		Search search = new Search();
		search.goalNeeded[Pos.from(7,1).o] = 2;
		search.goalNeeded[Pos.from(8,2).o] = 2;
	
		state.myMatter = 20;
		
		List<Pos> frontier = Arrays.asList(
				Pos.from(7,1), 
				Pos.from(8,2));
		search.search(state, tt, ilots, frontier, EMPTY_DANGER);
		
	}
	
	@Test
	@DisplayName("Lot of way to fill goal of 8,1!")
	void moveOrSpawn() throws Exception {
		Search search = new Search();
		search.goalNeeded[Pos.from(7,1).o] = 0;
		search.goalNeeded[Pos.from(8,2).o] = 0;
		search.goalNeeded[Pos.from(8,1).o] = 2;
	
		state.myMatter = 20;
		
		List<Pos> frontier = Arrays.asList(
				Pos.from(8,1), 
				Pos.from(7,1),
				Pos.from(8,2));
		search.search(state, tt, ilots, frontier, EMPTY_DANGER);
		
	}
	
	
	@Test
	void testName() throws Exception {
		state = TITest.read(""" 
Global 
^22 11
Optional State : 
^ 12 0
^17 14
^    0     0 16384     0     0 20480 16384  8192 16384 16384 12832 13330 17414 17414  8192 16384 20480     0     0 16384     0     0 
^16384 18432 16384     0     0 12288 16384 20480 18432 12288  8736  9250 12288 19462 21510 16384 12288     0     0 16384 18432 16384 
^16384 16384     0 18432 12288 18944 18944 16896 16896 12800 21024 21522 13318 17414 17414 19462  7174     0  6144     0 16384 16384 
^18432 16384 18432 16384     0 12800     0 16896  8704 18944 21024 21554 19462  9222 11271     0 12288  5126 16384 18432 16384 18432 
^18432 16384 18432     0     0     0 20992 12800 20992  8704  8752  9266  9222 15367  7177 15367 17414  5126 21510 18432 16384 18432 
^    0 18432 12800 16896     0  8704 18944 16896     0 16896  8720  9234 17414     0 11271 19462     0     0     0 12288 18432     0 
^ 8192 20480 16896     0     0 16896 16896 16896  8704 18944 16896 17426 19462  9222 17414 16384 16384     0     0 16384 20480  8192 
^    0 20480  8704  8704 12800  8704 16896 20992     0 20992  8192  9250 20480     0 20480 16384  8192 12288  8192  8192 20480     0 
^12288     0  8192 20480 18432  8704 16896 16896 16384     0 20480 20480     0 16384 16384 16384  8192 18432 20480  8192     0 12288 
^18432     0 16384 16384  8192 12800  8704 18944 18944 16896  8704  8720 16384 18432 18432  8192 12288  8192 16384 16384     0 18432 
^12288  8192  8192  8192 20480 12816     0 12288 12288     0 18432 18432     0 12288 12288     0 12288 20480  8192  8192  8192 12288		    
		    """);
		
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		Territory t = new Territory();
		List<Ilot> ilots = Ilot.build(state);
		t.calculateTerritories(ilots.get(0), tt);
		
		Search search = new Search();
		long start = System.currentTimeMillis();
		
		search.search(state, tt, ilots, t.frontier, t.blueDangers);
		
		System.out.println("Search in "+(System.currentTimeMillis() - start));
	}
	
	
	public static void main(String[] args) {
		// PERf ! 
		
		State state = TITest.read("""
Global 
^22 11
Optional State : 
^ 12 0
^17 14
^    0     0 16384     0     0 20480 16384  8192 16384 16384 12832 13330 17414 17414  8192 16384 20480     0     0 16384     0     0 
^16384 18432 16384     0     0 12288 16384 20480 18432 12288  8736  9250 12288 19462 21510 16384 12288     0     0 16384 18432 16384 
^16384 16384     0 18432 12288 18944 18944 16896 16896 12800 21024 21522 13318 17414 17414 19462  7174     0  6144     0 16384 16384 
^18432 16384 18432 16384     0 12800     0 16896  8704 18944 21024 21554 19462  9222 11271     0 12288  5126 16384 18432 16384 18432 
^18432 16384 18432     0     0     0 20992 12800 20992  8704  8752  9266  9222 15367  7177 15367 17414  5126 21510 18432 16384 18432 
^    0 18432 12800 16896     0  8704 18944 16896     0 16896  8720  9234 17414     0 11271 19462     0     0     0 12288 18432     0 
^ 8192 20480 16896     0     0 16896 16896 16896  8704 18944 16896 17426 19462  9222 17414 16384 16384     0     0 16384 20480  8192 
^    0 20480  8704  8704 12800  8704 16896 20992     0 20992  8192  9250 20480     0 20480 16384  8192 12288  8192  8192 20480     0 
^12288     0  8192 20480 18432  8704 16896 16896 16384     0 20480 20480     0 16384 16384 16384  8192 18432 20480  8192     0 12288 
^18432     0 16384 16384  8192 12800  8704 18944 18944 16896  8704  8720 16384 18432 18432  8192 12288  8192 16384 16384     0 18432 
^12288  8192  8192  8192 20480 12816     0 12288 12288     0 18432 18432     0 12288 12288     0 12288 20480  8192  8192  8192 12288 
		    """);
		
		new AI2().think(state); // init some static values:/
		
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		List<Ilot> ilots = Ilot.build(state);
		Territory t = new Territory();
		t.calculateTerritories(ilots.get(0), tt);
		
		Search search = new Search();
		Defense.setGoalsNeeded(state, tt, t, search);
		
		for (int i=0;i<100;i++) {
			search.search(state, tt, ilots, t.frontier, EMPTY_DANGER);
		}
		
		System.err.println("Warmup done");

		for (int i=0;i<10_000;i++) {
			search.search(state, tt, ilots, t.frontier, EMPTY_DANGER);
		}

	}
}
