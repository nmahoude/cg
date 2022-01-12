package xmashrush2;

import java.util.List;

import fast.read.FastReader;
import xmashrush2.ai.Cell;

public class State {
	
	private int turnType;
	public int[] cells = new int[49];
	public int[] items = new int[49];
	public Agent[] agents = new Agent[] { new Agent(), new Agent() };

	public State() {
	}

	public void copyFrom(State model) {
		this.turnType = model.turnType;
		System.arraycopy(model.cells, 0, this.cells, 0, 49);
		System.arraycopy(model.items, 0, this.items, 0, 49);
		this.agents[0].copyFrom(model.agents[0]);
		this.agents[1].copyFrom(model.agents[1]);
	}

	public void read(FastReader in) {
		turnType = in.nextInt();

		for (int y = 0; y < 7; y++) {
			for (int x = 0; x < 7; x++) {
				char[] tile = in.nextChars();
				int encodedTile = Cell.tileToInt(tile);

				cells[offset(x, y)] = encodedTile;
				items[offset(x, y)] = -1;
			}
		}
		for (int i = 0; i < 2; i++) {
			agents[i].read(in);
		}
		int numItems = in.nextInt(); // the total number of items available on board and on player tiles
		for (int i = 0; i < numItems; i++) {
			int itemIndex = Item.indexFromName(in.nextChars());
			int itemX = in.nextInt();
			int itemY = in.nextInt();
			int itemPlayerId = in.nextInt();
			itemIndex += 100 * itemPlayerId;

			if (itemX == -1) {
				agents[0].item = itemIndex;
			} else if (itemX == -2) {
				agents[1].item = itemIndex;
			} else {
				Pos pos = Pos.from(itemX, itemY);
				items[pos.offset] = itemIndex;
			}
		}

		
		agents[0].clearQuestItems();
		agents[1].clearQuestItems();
		int numQuests = in.nextInt(); // the total number of revealed quests for both players
		for (int i = 0; i < numQuests; i++) {
			int questItemIndex = Item.indexFromName(in.nextChars());
			int questPlayerId = in.nextInt();
			questItemIndex += 100 * questPlayerId;
			
			agents[questPlayerId].addQuest(questItemIndex);
		}
	}

	private int offset(int x, int y) {
		return x + 7 * y;
	}

	public boolean moveTurn() {
		return turnType != 0;
	}

	public void apply(PushAction action0, PushAction action1) {
		if (!action0.isCompatibleWith(action1)) {
			return;
		}
		if (action0.isRow()) {
			apply(0, action0);
			apply(1, action1);
		} else {
			apply(1, action1);
			apply(0, action0);
		}
	}

	private void apply(int agentIndex, PushAction action) {
		if (action == null)
			return;

		int tempCell;
		int tempItem;

		switch (action.dir) {
		case UP:
			tempCell = cells[Pos.offsetOf(action.offset, 0)];
			tempItem = items[Pos.offsetOf(action.offset, 0)];

			for (int i = 0; i < 6; i++) {
				cells[Pos.offsetOf(action.offset, i)] = cells[Pos.offsetOf(action.offset, i + 1)];
				items[Pos.offsetOf(action.offset, i)] = items[Pos.offsetOf(action.offset, i + 1)];
			}
			cells[Pos.offsetOf(action.offset, 6)] = agents[agentIndex].cell;
			items[Pos.offsetOf(action.offset, 6)] = agents[agentIndex].item;

			agents[agentIndex].cell = tempCell;
			agents[agentIndex].item = tempItem;
			
			moveAgents(action);
			break;
		case DOWN:
		   	tempCell = cells[Pos.offsetOf(action.offset, 6)];
			tempItem = items[Pos.offsetOf(action.offset, 6)];
			
			for (int i=6;i>0;i--) {
				cells[Pos.offsetOf(action.offset, i)] = cells[Pos.offsetOf(action.offset, i - 1)];
				items[Pos.offsetOf(action.offset, i)] = items[Pos.offsetOf(action.offset, i - 1)];
			}
			cells[Pos.offsetOf(action.offset, 0)] = agents[agentIndex].cell;
			items[Pos.offsetOf(action.offset, 0)] = agents[agentIndex].item;

			agents[agentIndex].cell = tempCell;
			agents[agentIndex].item = tempItem;

			moveAgents(action);
	      break;

	    case RIGHT:
		   	tempCell = cells[Pos.offsetOf(6, action.offset)];
			tempItem = items[Pos.offsetOf(6, action.offset)];
			
			for (int i=6;i>0;i--) {
				cells[Pos.offsetOf(i, action.offset)] = cells[Pos.offsetOf(i - 1, action.offset)];
				items[Pos.offsetOf(i, action.offset)] = items[Pos.offsetOf(i - 1, action.offset)];
			}
			cells[Pos.offsetOf(0, action.offset)] = agents[agentIndex].cell;
			items[Pos.offsetOf(0, action.offset)] = agents[agentIndex].item;

			agents[agentIndex].cell = tempCell;
			agents[agentIndex].item = tempItem;

			moveAgents(action);

			break;
	    case LEFT:
	    	tempCell = cells[Pos.offsetOf(0, action.offset)];
	    	tempItem = items[Pos.offsetOf(0, action.offset)];

	    	for (int i = 0; i < 6; i++) {
	    		cells[Pos.offsetOf(i, action.offset)] = cells[Pos.offsetOf(i + 1, action.offset)];
	    		items[Pos.offsetOf(i, action.offset)] = items[Pos.offsetOf(i + 1, action.offset)];
	    	}
	    	cells[Pos.offsetOf(6, action.offset)] = agents[agentIndex].cell;
	    	items[Pos.offsetOf(6, action.offset)] = agents[agentIndex].item;

	    	agents[agentIndex].cell = tempCell;
	    	agents[agentIndex].item = tempItem;

			moveAgents(action);

		      break;
		default:
			break;
		}
	}

	private void moveAgents(PushAction action) {
		for (int i=0;i<2;i++) {
			Agent agent = agents[i];
			agent.apply(action);
			if (agent.needs(items[agent.pos.offset])) {
				items[agent.pos.offset] = -1;
				agent.getQuestItem(items[agent.pos.offset]);
			}
		}
	}

	public void debugGrid() {
		System.err.println("  01234567  01234567");
		for (int y = 0; y < 7; y++) {
			System.err.print(y + " ");
			for (int x = 0; x < 7; x++) {
				int directions = cells[x + y * 7];
				System.err.print(directionsToChar(directions));
			}
			System.err.print("  ");
			for (int x = 0; x < 7; x++) {
				String c = Item.letter(items[x + 7 * y]);
				System.err.print(c);
			}
			System.err.println();
		}

	}

	static String directionsStr[] = new String[] { " ", "╵", "╶", "└", "╷", "│", "┌", "├", "╴", "┘", "─", "┴", "┐", "┤",
			"┬", "┼" };

	private char directionsToChar(int directions) {
		return directionsStr[directions].charAt(0);
	}

	public void debugQuestItems() {
		agents[0].debugQuestItems();
		agents[1].debugQuestItems();
	}

	public void applyMoves(List<Pos> route) {
		for (Pos pos : route) {
			if (this.agents[0].needs(this.items[pos.offset])) {
				this.items[pos.offset] = -1;
				this.agents[0].score ++;
			}
		}
	}
}
