package fall2022.ai.poubelle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fall2022.Action;
import fall2022.Pos;
import fall2022.State;
import fall2022.TITest;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.poubelle.RecyclerMatterUpgrader2;

public class RecyclerMatterUpgrader2Test {
	RecyclerMatterUpgrader2 mru = new RecyclerMatterUpgrader2();
	
	@Test
	@DisplayName("Don't build at (13,3) je perds plein de cases !")
	void dontBuildToCutOneSelf() throws Exception {
		State state = TITest.read("""
Global 
^16 8
Optional State : 
^ 18
^33 28
^20480 16384     0 12288     0  8192  8192  2048 20480 12288     0  2048  2049     0 18432 16384 
^    0 12816 20480 10240     0 12288     0     0     0  5126     0     0  9225  3081 10241 16384 
^16384 12816  6144     0  8704 16896  2561     0  5129 11271  3090  9299 10761  7187     0 20480 
^16912  8720     0 10752  2560  2561  2569  5129     0     0     0 13318     0 13318 20480 16384 
^16912 20992  4608     0 16896 12800     0     0  3079     0     0     0     0     0 16384 16384 
^20480     0     0     0     0  4608     0     0  9225     0     0     0     0 16384 12288 16384 
^16912 21008 12800  2576 16896 16912  4624 16384  8193 10240     0     0 18432 20480 12288     0 
^16384 18432     0  8720  8704 12800 12816 20480 18432  8192  8192     0 12288     0 16384 20480
		    """);
		
		List<Ilot> ilots= Ilot.build(state);
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		
		List<Action> commands = mru.think(state, ilots, tt);
		
		TITest.apply(state, commands);
		
		assertThat(state.rec[Pos.from(13,3).o]).isZero();
		
	}

}
