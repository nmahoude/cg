package xmashrush2;

import fast.read.FastReader;
import xmashrush2.ai.Cell;

public class Agent {
	public Pos pos = Pos.from(0,0);
	public int cell;
	public int item;

	public int score;
	
	int[] questItems = new int[3];
	int questItemsFE = 0;
	
	public void read(FastReader in) {
		int numPlayerCards = in.nextInt(); // the total number of quests for a player (hidden and revealed)
		score = 12 - numPlayerCards;
		
		int playerX = in.nextInt();
		int playerY = in.nextInt();
		
		pos = Pos.from(playerX, playerY);
		
		cell = Cell.tileToInt(in.nextChars());
		
		this.item = -1;
	}

	public void copyFrom(Agent model) {
		this.pos = model.pos;
		this.item = model.item;
		this.cell = model.cell;
		this.score = model.score;
		
		System.arraycopy(model.questItems, 0, questItems, 0, 3);
		questItemsFE = model.questItemsFE;
	}

	public void apply(PushAction action) {
		pos = pos.applyPushOnPos(action);
	}

	public void clearQuestItems() {
		questItemsFE = 0;
	}

	public void addQuest(int questItemIndex) {
		// TODO optimise this by using a single bitmask for all questItems
		questItems[questItemsFE++] = questItemIndex;
	}

	public boolean needs(int itemIndex) {
		for (int i=0;i<questItemsFE;i++) {
			if (questItems[i] == itemIndex) return true;
		}
		return false;
	}

	public void debugQuestItems() {
		System.err.println("Items : ");
		for (int i=0;i<questItemsFE;i++) {
			System.err.print(Item.name(questItems[i]));
			System.err.println(" , ");
		}
		System.err.println();
	}

	public void getQuestItem(int itemIndex) {
		this.score++;

		// TODO shorten the list instead of puting a -1 in a quest item ?
		for (int i=0;i<questItemsFE;i++) {
			if (questItems[i] == itemIndex) {
				questItems[i] = -666;
			}
		}
	}

	public boolean fullOfQuestItems() {
		int count = 0;
		for (int i=0;i<questItemsFE;i++) {
			if (questItems[i] != -1) count++;
		}		
		return count == 3;
	}
}
