package acoiaf;

import fast.read.FastReader;

public class State {
	private static final int WIDTH = 12;
	private static final int HEIGHT = 12;

	public static final int NONE = -99;
	public static final int SOLDIER = -1;
	public static final int HQ = 0;
	public static final int MINE = 1;
	public static final int TOWER = 2;
	public static final int VOID = 3;

	static int turn = -1;
	
	
	public static Pos mines[] = new Pos[25];
	public static int minesFE = 0;

	public int gold[] = new int[2];
	public int income[] = new int[2];

	public int[] owner = new int[12*12];
	public int[] unitId = new int[12*12];
	public int[] level = new int[12*12];
	public int[] buildingType = new int[12*12];

	public void copyFrom(State model) {
		this.gold[0] = model.gold[0];
		this.gold[1] = model.gold[1];
		this.income[0] = model.income[0];
		this.income[1] = model.income[1];
		
		System.arraycopy(model.owner, 0, this.owner, 0, 144);
		System.arraycopy(model.unitId, 0, this.unitId, 0, 144);
		System.arraycopy(model.level, 0, this.level, 0, 144);
		System.arraycopy(model.buildingType, 0, this.buildingType, 0, 144);
	}
	
	
	
	public static void readInit(FastReader in) {
		int numberMineSpots = in.nextInt();
		for (int i = 0; i < numberMineSpots; i++) {
			mines[minesFE++] = Pos.get(in.nextInt(), in.nextInt());
		}
	}

	public void readOptional(FastReader in) {
		State.turn = in.nextInt();
	}

	public void saveOptional() {
		System.err.println("INTERNAL");
		System.err.println("^"+turn);
	}
	
	public void read(FastReader in) {
		Action.resetCache();
		turn++;
		
		resetGrids();

		gold[0] = in.nextInt();
		income[0] = in.nextInt();

		gold[1] = in.nextInt();
		income[1] = in.nextInt();

		saveGlobal();
		saveOptional();
		System.err.println("TURN");
		System.err.println("^ "+gold[0]+" "+income[0]+" "+gold[1]+" "+income[1]);
		for (int y = 0; y < 12; y++) {
			char[] line = in.nextChars();
			
			System.err.print("^");
			for (int x = 0; x < 12; x++) {
				System.err.print(line[x]);
				/*
				 * void (#): not a playable cell. neutral (.): doesn't belong to any player.
				 * captured (O or X): belongs to a player. inactive (o or x): belongs to a
				 * player but inactive.
				 */
				int offset = Pos.get(x, y).offset;
				unitId[offset] = -1;
				buildingType[offset] = VOID;
				
				if (line[x] == 'O' || line[x] == 'o') {
					owner[offset] = O.ME;
				} else if (line[x] == 'X' || line[x] == 'x') {
					owner[offset] = O.OPP;
				} else if (line[x] == '.'){
					owner[offset] = O.NEUTRAL; // neutral
				} else {
					owner[offset] = O.VOID;
				}
			}
			System.err.println();
		}

		int buildingCount = in.nextInt();
		System.err.println("^"+buildingCount);
		for (int i = 0; i < buildingCount; i++) {
			int o = in.nextInt();
			int bType = in.nextInt();
			int x = in.nextInt();
			int y = in.nextInt();
			
			System.err.println("^ "+o+" "+bType+" "+x+" "+y);
			int offset = Pos.get(x, y).offset;
			this.owner[offset] = o;
			this.buildingType[offset] = bType;
		}

		int unitCount = in.nextInt();
		System.err.println("^ "+unitCount );
		for (int i = 0; i < unitCount; i++) {
			int owner = in.nextInt();
			int unitId = in.nextInt();
			int level = in.nextInt();
			int x = in.nextInt();
			int y = in.nextInt();

			System.err.println("^"+owner+" "+unitId+" "+level+" "+x+" "+y);
			int offset = Pos.get(x, y).offset;
			this.owner[offset] = owner;
			this.unitId[offset] = unitId;
			this.level[offset] = level;
			this.buildingType[offset] = SOLDIER;
		}
	}

	private static void saveGlobal() {
		System.err.println("GLOBAL");
		System.err.print("^"+minesFE+" ");
		for (int i = 0; i < minesFE; i++) {
			System.err.print(mines[i].x+" "+mines[i].y+" ");
		}
		System.err.println();

	}

	private void resetGrids() {
		for (int i = 0; i < 144; i++) {
			buildingType[i] = NONE;
		}
	}

	public void debugPackedState() {
		System.err.println("debugPackedState : TODO");
	}

	public boolean canMove(Pos p) {
		return owner[p.offset] != VOID;
	}
}
