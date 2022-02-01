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
	
	static Pos mines[] = new Pos[25];
	static int minesFE = 0;

	int gold[] = new int[2];
	int income[] = new int[2];
	
	int[] owner = new int[144];
    int[] unitId  = new int[144];
    int[] level  = new int[144];
    int[] buildingType  = new int[144];
	
	public static void readInit(FastReader in) {
        int numberMineSpots = in.nextInt();
        for (int i = 0; i < numberMineSpots; i++) {
            mines[minesFE++] = Pos.get(in.nextInt(), in.nextInt());
        }
	}

	public void read(FastReader in) {
		resetGrids();
		
		gold[0] = in.nextInt();
        income[0] = in.nextInt();
        
		gold[1] = in.nextInt();
        income[1] = in.nextInt();

        for (int y = 0; y < 12; y++) {
        	char[] line = in.nextChars();
        	for (int x = 0; x < 12; x++) {
        		/*
        		 * void (#): not a playable cell.
					neutral (.): doesn't belong to any player.
					captured (O or X): belongs to a player.
					inactive (o or x): belongs to a player but inactive.
        		 */
        		int offset = Pos.get(x, y).offset;
        		if (line[x] == 'O' || line[x] == 'o') {
        			owner[offset] = 0;
        		} else if (line[x] == 'X' || line[x] == 'x') {
        			owner[offset] = 1;
        		} else {
        			owner[offset] = -1;
        			if (line[x] == '#') {
        				owner[offset] = -99;
        				buildingType[offset] = VOID;
        			}
        		}
        	}
        }
        
        int buildingCount = in.nextInt();
        for (int i = 0; i < buildingCount; i++) {
            int o = in.nextInt();
            int bType = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            
            int offset = Pos.get(x, y).offset;
            this.owner[offset] = o;
            this.buildingType[offset] = bType;
        }
        
        int unitCount = in.nextInt();
        for (int i = 0; i < unitCount; i++) {
            int owner = in.nextInt();
            int unitId = in.nextInt();
            int level = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();

            int offset = Pos.get(x, y).offset;
            this.owner[offset]  = owner;
            this.unitId[offset] = unitId;
            this.level[offset] = level;
            this.buildingType[offset] = SOLDIER;
        }
	}

	private void resetGrids() {
		for (int i=0;i<144;i++) {
			buildingType[i] = NONE;
		}
	}

}
