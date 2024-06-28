package fall2022.minimax;

import java.util.List;

import fall2022.State;
import fall2022.TITest;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

public class MinimaxTest {

	
	public static void main(String[] args) {
		System.out.println("Test minimax ");
		
		String input = """
Global 
^13 6
Optional State : 
^ 5
^14 10
^16384 16384 20480 12288 12288 8192 16384 0 8192 16384 0 0 0 
^8192 20480 8192 0 19462 21510 17442 16912 16896 12288 8192 16384 0 
^16384 16384 9223 11273 3079 19462 13318 18944 16896 16896 18944 0 0 
^0 0 19462 7175 17414 19462 12816 18944 12800 20992 18944 16384 16384 
^0 16384 9222 13318 17414 17426 16896 20992 18944 8704 8192 20480 8192 
^0 0 0 16384 8192 0 16384 8192 12288 12288 20480 16384 16384		    
""";
		
		State state = TITest.read(input);
		// get the frontier
		TimeTraveler tt = new TimeTraveler();
		tt.init(state);
		List<Ilot> ilots = Ilot.build(state);
		Territory territory = new Territory();
		territory.calculateTerritories(ilots.get(0), tt);
		
		System.out.println("Frontier is "+territory.frontier);
		new Minimax().process(state, territory.frontier);
		
	}
}
