package fall2022.ai.poubelle;

import java.util.HashSet;
import java.util.Set;

import fall2022.Logger;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.sim.Sim;

public class AggressionDetector {


	public static boolean detect(State work, Ilot ilot, TimeTraveler tt, Territory territory) {
		if (!ilot.isDisputed()) return false;
		
		Set<Pos> considered = new HashSet<>();
		
		State lastOriginal = new State();
		lastOriginal.copyFrom(work);
		Sim.tenTurn(lastOriginal);


		// un peu opti/pessi/miste (comme si on allait tout mettre sur cet ilot)
		int blues = lastOriginal.myMatter;
		int reds = lastOriginal.oppMatter;
		
		int redOrBlueFrontier = 0;
		for (Pos frontier : territory.frontier) {
			boolean hasRedOrBlue = false;
			for (Pos n : frontier.meAndNeighbors4dirs) {
				if (!work.isNeutral(n)) hasRedOrBlue = true;
				
				if (!considered.contains(n)) {
					considered.add(n);
					if (work.isMine(n)) blues += work.u[n.o];
					else if (work.isOpp(n)) reds += work.u[n.o];
				}
			}
			if (hasRedOrBlue) redOrBlueFrontier++;
		}

		if (blues >= reds && blues >= redOrBlueFrontier) {
			Logger.info("Aggro on ilot "+ilot.p[0]+", ...");
			return true;
		} else {
			Logger.info("Defense on ilot "+ilot.p[0]+", ...");
			return false;
		}
	}

}
