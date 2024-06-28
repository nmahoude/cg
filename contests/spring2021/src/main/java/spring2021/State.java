package spring2021;

public class State {
	static final int[] cost = new int[] {1, 3, 7, 4 };
	public static final int richness[] = new int[38];
	public static final boolean[] forbidenSeedCells= new boolean[38];

	
	public final int trees[] = new int[38];
	private final boolean mine[] = new boolean[38];
	private long dormantsMask = 0L;
	
	public int day;
	
	public final int[] sun = new int[2];
	public final int[] score = new int[2];
	public final int[] treesCount = new int[2*5];
	public boolean oppIsWaiting;
	public int nutrients;

	public int currentTotalRichness; // represent richness of my seeds/trees
	public int currentSeedDeltaRichness;

	public final int newSeeds[]= new int[37];
	public int newSeedsFE = 0;
	
	public boolean hasSeed = false;
	public boolean hasGrow0to1 = false;
	public boolean hasGrow1to2 = false;
	public boolean hasGrow2to3 = false;

	// swap players
	public void swap() {
		for (int i=0;i<37;i++) {
			mine[i] = !mine[i];
		}
		int swp;
		
		swp = sun[0];
		sun[0] = sun[1];
		sun[1] = swp;
		
		swp = score[0];
		score[0] = score[1];
		score[1] = swp;

		for (int i=0;i<4;i++) {
			swp = treesCount[0*5+i];
			treesCount[0*5+i] = treesCount[1*5+i];
			treesCount[1*5+i] = swp;
		}
	}
	
	public void init() {
	    for (int i = 0; i < 37; i++) {
	        this.trees[i] = -1;
	        this.mine[i] = false;
	    }
	    dormantsMask = 0L;
	    for (int p=0;p<2;p++) {
	    	treesCount[5*p+0] = 0;
	    	treesCount[5*p+1] = 0;
	    	treesCount[5*p+2] = 0;
	    	treesCount[5*p+3] = 0;
	    }
	    currentTotalRichness = 0;
	    currentSeedDeltaRichness = 0;
	    newSeedsFE = 0;
	    
	    resetGrows();
	}

	public void resetGrows() {
		hasSeed = false;
		hasGrow0to1 = false;
		hasGrow1to2 = false;
		hasGrow2to3 = false;
	}
	
	public void copyFrom(State model) {
		this.day = model.day;
		this.nutrients = model.nutrients;
		this.currentTotalRichness = model.currentTotalRichness;
		this.currentSeedDeltaRichness = model.currentSeedDeltaRichness;
		
		for (int p=0;p<2;p++) {
			this.sun[p] = model.sun[p];
			this.score[p] = model.score[p];
			for (int i=0;i<4;i++) {
				this.treesCount[5*p+i] = model.treesCount[5*p+i];
			}
		}
		this.oppIsWaiting = model.oppIsWaiting;
	
		
		for (int i=0;i<37;i++) {
			this.trees[i] = model.trees[i];
			this.mine[i] = model.mine[i];
		}
		this.dormantsMask = model.dormantsMask;
		this.newSeedsFE = model.newSeedsFE;
		for (int i=0;i<newSeedsFE;i++) {
			this.newSeeds[i] = model.newSeeds[i];
		}
		
    this.hasSeed = model.hasSeed;
    this.hasGrow0to1 = model.hasGrow0to1;
    this.hasGrow1to2 = model.hasGrow1to2;
    this.hasGrow2to3 = model.hasGrow2to3;

	}

	public int costToGrow(int index, int size) {
		return cost[size]+treesCount[5*index+(size+1)];
	}

	public int costToSeed(int index) {
		return treesCount[5*index+0];
	}

	public int costToComplete(int index) {
		return 4;
	}

	public int seedCount(int index) {
		return treesCount[5*index+0];
	}
	
	
	public void debugInfos() {
		System.err.println("   Scores "+score[0]+" - " + score[1] );
		System.err.println("   Suns   "+sun[0]+" - " + sun[1] );
		System.err.println("   eval => "+Player.evaluator.evaluate(this));
	}
	
	public void resetDormants() {
		dormantsMask = 0L;
	}
	public void setDormant(int index) {
		dormantsMask |= (1L << index);
	}
	public boolean isDormant(int index) {
		return (dormantsMask & (1L << index)) != 0;
	}

	public void setMine(int index) {
		mine[index] = true;
	}
	public void unsetMine(int index) {
		mine[index] = false;
	}
	public boolean isMine(int index) {
		return mine[index];
	}
	
	public void updateForbidenSeedsCells(int forbidenRange) {
		for (int i=0;i<37;i++) {
//			if (i == 0) continue; // center cell always possible 
			if (!mine[i]) continue;
			
			for (int day = 0;day<6;day++) {
				for (int dx=0;dx<forbidenRange;dx++) {
					int shadowIndex = Cell.shadowIndexes[i][day][dx];
//					if (shadowIndex == 0) continue; //center cell don't cast forbiden shadows
					forbidenSeedCells[shadowIndex] = true;
				}
			}
		}	
	}

	public void initForbidenSeedsCells() {
		for (int i=0;i<37;i++) {
			forbidenSeedCells[i] = false;
		}
	}

	public int richnessBonus(int index) {
		return 2*(State.richness[index]-1);
	}
}
