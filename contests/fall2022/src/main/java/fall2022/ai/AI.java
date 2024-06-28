package fall2022.ai;

import java.util.List;

import fall2022.Action;
import fall2022.State;

public interface AI {

	List<Action> think(State originalStateReadOnly);

}