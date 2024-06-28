package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.Logger;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.sim.Sim;

//
// if the cell is blue, empty and will disapear
// we can build a recycler here is it cost 1 cell (the one on the recycler !)
// 
// so it's free if we get more than 10 matter for the recycler
// 
// TODO maybe check if we can spare a robot ?
//
public class FreeRecyclers {

	public List<Action> think(State work, TimeTraveler tt, Ilot ilot) {
		
		List<Action> actions = new ArrayList<>();

		for (int i=0;i<ilot.pFE;i++) {
			if (work.myMatter < O.COST) break;
			
			Pos pos = ilot.p[i];
			if (!work.canBuild(pos)) continue;
			if (tt.sliceAt(10).state.canBuild(pos)) continue; // will not disappear
			
			// ok here the cell is empty and will disappear, check if it is free to build one recycler here
			boolean isFree = true;
			for (Pos n : pos.neighbors4dirs) {
				if (tt.sliceAt(10).state.s[n.o] == 0) continue; // will disappear anyway
				if (work.s[n.o] > work.s[pos.o]) continue; // will survive
				
				isFree = false;
				break;
			}

			if (!isFree) continue; // it is not free :/ TODO maybe another cell is ok ? :)
			
			State temp = new State();
			temp.copyFrom(work);
			Action build = Action.build(pos, "Free recycler");
			temp.apply(build);
			Sim.tenTurn(temp);
			
			int totalOriMatter = tt.sliceAt(10).state.myMatter;
			int myMatter = temp.myMatter;
			if (myMatter > totalOriMatter) {
				// build it !
				Logger.info("Can build a free recycler @"+pos+"!! => ori:"+totalOriMatter+" vs w/build"+ myMatter);
				actions.add(build);
				work.apply(build);
			} else {
				Logger.warning("could have build a free recycler @"+pos+" but not enough gain => ori:"+totalOriMatter+" vs w/build"+ myMatter);
			}
		}
		
		
		
		return actions;
	}
}
