package fall2022.ai;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.Pos;
import fall2022.State;
import fast.read.FastReader;

public class AIReplayer implements AI{

	String[] commands = new String[] {
			"MOVE 1 19 8 18 8;MOVE 1 20 7 19 7;MOVE 1 20 9 19 9;MOVE 1 21 8 21 9;SPAWN 1 20 7;WAIT;",
			"BUILD 21 8;MOVE 1 18 8 17 8;MOVE 1 19 7 18 7;MOVE 1 19 9 18 9;MOVE 1 21 9 21 10;MOVE 1 20 7 19 7;WAIT;",
			"BUILD 21 9;MOVE 1 17 8 16 8;MOVE 1 18 9 17 9;MOVE 1 18 7 17 7;MOVE 1 19 7 19 6;MOVE 1 21 10 20 10;WAIT;",
			"BUILD 18 9;MOVE 1 16 8 15 8;MOVE 1 17 7 16 7;MOVE 1 17 9 16 9;MOVE 1 20 10 19 10;MOVE 1 19 6 19 5;SPAWN 1 16 8;WAIT;",
			"BUILD 17 7;MOVE 1 15 8 14 8;MOVE 1 16 7 15 7;MOVE 1 16 9 15 9;MOVE 1 16 8 16 9;MOVE 1 19 5 18 5;MOVE 1 19 10 18 10;SPAWN 1 16 7;WAIT;",
			"BUILD 15 8;MOVE 1 14 8 13 8;MOVE 1 15 9 14 9;MOVE 1 15 7 14 7;MOVE 1 16 9 16 10;MOVE 1 16 7 15 7;MOVE 1 18 5 17 5;MOVE 1 18 10 17 10;SPAWN 1 15 7;SPAWN 1 14 8;WAIT;",
			"BUILD 19 5;MOVE 1 13 8 12 8;MOVE 1 14 7 13 7;MOVE 1 14 9 13 9;MOVE 1 16 10 15 10;MOVE 1 15 7 15 6;MOVE 1 17 5 16 5;MOVE 1 15 7 15 6;MOVE 1 14 8 14 9;MOVE 1 17 10 16 10;SPAWN 1 13 8;SPAWN 1 17 5;WAIT;",
			"BUILD 19 6;MOVE 1 13 9 12 9;MOVE 1 13 7 12 7;MOVE 1 13 8 12 8;MOVE 1 14 9 14 10;MOVE 1 15 6 14 6;MOVE 1 15 6 15 5;MOVE 1 16 5 16 4;MOVE 1 15 10 14 10;MOVE 1 17 5 17 4;MOVE 1 16 10 15 10;SPAWN 1 13 7;SPAWN 1 12 8;MOVE 1 12 8 11 8;WAIT;",
			"MOVE 1 12 8 11 8;MOVE 1 13 7 12 7;MOVE 1 14 10 13 10;MOVE 1 14 6 13 6;MOVE 1 15 5 14 5;MOVE 1 14 10 13 10;MOVE 1 16 4 15 4;MOVE 1 15 10 14 10;MOVE 1 17 4 17 3;SPAWN 1 12 7;SPAWN 1 12 9;MOVE 1 12 9 12 10;MOVE 1 12 7 11 7;WAIT;"
	};
	
	@Override
	public List<Action> think(State originalStateReadOnly) {
		List<Action> actions = new ArrayList<>();
		String actionsStr[] = commands[State.turn].split(";");
		for (int i=0;i<actionsStr.length;i++) { 
			FastReader in = FastReader.fromString(actionsStr[i]+"\n");
		
			String type = in.nextString();
			if (type.equals("MOVE")) {
				Action move = Action.move(in.nextInt(), Pos.from(in.nextInt(), in.nextInt()), Pos.from(in.nextInt(),in.nextInt()), "REPLAYER");
				actions.add(move);
			} else if (type.equals("SPAWN")) {
				Action spawn = Action.spawn(in.nextInt(), Pos.from(in.nextInt(), in.nextInt()), "REPLAYER");
				actions.add(spawn);
			} else if (type.equals("BUILD")) {
				Action build = Action.build(Pos.from(in.nextInt(), in.nextInt()), "REPLAYER");
				//actions.add(build);
			}
		
		}
		
		
		return actions;
	}
	
	
	
	
	
}
