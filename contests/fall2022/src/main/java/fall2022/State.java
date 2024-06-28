package fall2022;

import fast.read.FastReader;

public class State {
	public static int turn = -1;
	public static int WIDTH;
	public static int HEIGHT;
	
	public static Pos center;
	private static int STALL;
	public static int hasChangedSinceLast[] = new int[Pos.MAX_OFFSET];
	
	public static int totalCellCount = 0;

	public int myUnitCount = 0;
	public int oppUnitCount = 0;
	public 	int myRecyclerCount = 0;
	
	public int myCellCount = 0;
	public int oppCellCount = 0;
	
	
	public 	int myMatter;
	public 	int oppMatter;
	public 	int[] s = new int[Pos.MAX_OFFSET];
	public 	int[] o = new int[Pos.MAX_OFFSET];
	public 	int[] u = new int[Pos.MAX_OFFSET];
	public 	int[] mu = new int[Pos.MAX_OFFSET];
	public 	int[] rec = new int[Pos.MAX_OFFSET];
	
	public 	int[] lm = new int[Pos.MAX_OFFSET];
	
	public 	int[] ou = new int[Pos.MAX_OFFSET];
	public 	int[] oo = new int[Pos.MAX_OFFSET];

	public 	int[] attack = new int[Pos.MAX_OFFSET];

	public State() {
	}

	
	public int units(Pos p) {
		return u[p.o];
	}
	
	public int movableUnits(Pos p) {
		return mu[p.o];
	}
	
	public int owner(Pos p) {
		return o[p.o];
	}
	
	
	public void copyFrom(State model) {
	  this.myUnitCount = model.myUnitCount;
	  this.oppUnitCount = model.oppUnitCount;
	  this.myMatter = model.myMatter;
	  this.oppMatter = model.oppMatter;
	  this.myRecyclerCount = model.myRecyclerCount;
	      
	  this.myCellCount = model.myCellCount;
	  this.oppCellCount = model.oppCellCount;
	  
	  System.arraycopy(model.s, 0, this.s , 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.o, 0, this.o, 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.u, 0, this.u , 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.mu, 0, this.mu , 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.ou, 0, this.ou, 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.rec, 0, this.rec, 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.lm, 0, this.lm, 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.oo, 0, this.oo, 0, Pos.MAX_OFFSET);
	  System.arraycopy(model.attack, 0, this.attack, 0, Pos.MAX_OFFSET);
	}
	
	public void readGlobal(FastReader in) {
		WIDTH = in.nextInt();
		HEIGHT = in.nextInt();
		
		Pos.init(WIDTH, HEIGHT);
		center = Pos.from(WIDTH/2, HEIGHT/2);
	}

	public void readOptionalState(FastReader in) {
		String line = in.nextLine();
		String[] inputs = line.split(" ");
		
		State.turn = Integer.parseInt(inputs[0]) -1 ; // turn minus one beacuse the read will add 1 !
		if (inputs.length > 1) {
			State.STALL = Integer.parseInt(inputs[1]);
		}
	}

	public void saveOptionalState() {
		Logger.info(Player.DEBUG_OUPUT,"Optional State : ");
		Logger.info(Player.DEBUG_OUPUT,String.format("^ %d %d", State.turn, State.STALL));
	}

	public void read(FastReader in) {
		Action.resetCache();

		State.turn++;
		
		myRecyclerCount =  0;
		myUnitCount = 0;
		oppUnitCount = 0;
		myCellCount = 0;
		oppCellCount = 0;
		
		myMatter = in.nextInt();
		oppMatter = in.nextInt();
		Player.start = System.currentTimeMillis();
		
		saveGlobalState();
		
		boolean thisturnSTALL = true;
		for (Pos p : Pos.allMapPositions) {
			hasChangedSinceLast[p.o] = 0;
		}
		
		for (int y = 0; y < HEIGHT; y++) {
			//if (Player.DEBUG_OUPUT) System.err.println("Y : "+y);
			if (Player.DEBUG_OUPUT) System.err.print("^");
			for (int x = 0; x < WIDTH; x++) {
				Pos p = Pos.from(x,y);
				attack[p.o] = 0;
				
				int scrapAmount = in.nextInt();
				
				int owner = in.nextInt(); // 1 = me, 0 = foe, -1 = neutral
				int units = in.nextInt();
				int recycler = in.nextInt();
				
				if (s[p.o] != scrapAmount) {
					for (Pos n : p.meAndNeighbors4dirs) {
						State.hasChangedSinceLast[n.o] = 1;
					}
				}
				
				if (Player.predictedNextState.o[p.o] != owner || Player.predictedNextState.rec[p.o] != recycler) {
					if (isStall() ) {
						System.err.println("UNSTALLING because of "+p);
					}
					thisturnSTALL = false;
				}
				
				this.s[p.o] = scrapAmount;
				this.u[p.o] = units;
				this.mu[p.o] = units;
				this.ou[p.o] = units; // lecture seule, c'est pir savoir si je peux poser des reycler à tout moment
				this.lm[p.o] = 0; // at first, no defender needed
				this.o[p.o] = owner;
				this.oo[p.o] = owner;
				this.rec[p.o] = recycler;
				if (recycler != 0 && owner == O.ME) myRecyclerCount++;
				
				if (turn == 0 && scrapAmount > 0) totalCellCount++;
				
				switch (owner) {
				case 1 : 
					myUnitCount+=units; 
					if (recycler == 0) myCellCount++;
					break;
				case 0 : 
					oppUnitCount+=units;
					if (recycler == 0) oppCellCount++;
					break;
				}
				
				
				int canBuild = in.nextInt();
				int canSpawn = in.nextInt();
				int inRangeOfRecycler = in.nextInt();

				
				// if (Player.DEBUG_OUPUT) System.err.println(String.format("^%d %d %d %d %d %d %d", scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler));
				if (Player.DEBUG_OUPUT) {
					int pack = 0;
					pack<<=5;	pack+= scrapAmount;
					pack<<=2; pack+= (owner + 1);
					pack<<= 5; pack+=(units);
					pack<<= 1; pack+=(recycler != 0 ? 1 : 0);
					pack<<= 1; pack+=(canBuild != 0 ? 1 : 0);
					pack<<= 1; pack+=(canSpawn != 0 ? 1 : 0);
					pack<<= 1; pack+=(inRangeOfRecycler != 0 ? 1 : 0);
					System.err.print(String.format("%5.5s ", pack));
				}
			}
			if (Player.DEBUG_OUPUT) System.err.println();
		}
		
		if (!thisturnSTALL) STALL = 0; else STALL++;
		
		
		//debugRecyclersInformations();
		
		calculateLockedMap();
	}

	private void saveGlobalState() {
		if (Player.DEBUG_OUPUT) {
			// print global every line
			Logger.info("Global ");
			Logger.info("^"+WIDTH+" "+HEIGHT);
			saveOptionalState();
			Logger.info(String.format("^%d %d", myMatter, oppMatter));
		}
	}

	public void debugPackedState() {
		saveGlobalState();
		
		System.err.println();
		for (int y=0;y<Pos.HEIGHT;y++) {
			System.err.print("^");
			for (int x=0;x<Pos.WIDTH;x++) {
				Pos p = Pos.from(x,y);
				int pack = 0;
				pack<<=5;	pack+= s[p.o];
				pack<<=2; pack+= (o[p.o] + 1);
				pack<<= 5; pack+=(u[p.o]);
				pack<<= 1; pack+=(rec[p.o] != 0 ? 1 : 0);
				pack<<= 1; pack+=0;
				pack<<= 1; pack+=0;
				pack<<= 1; pack+=0;
				System.err.print(String.format("%5.5s ", pack));
			}
			System.err.println();
		}
	}
	
	private void calculateLockedMap() {
		for (Pos pos : Pos.allMapPositions) {
			lm[pos.o] = 0;
			if (o[pos.o] == O.NEUTRAL) continue;
			if (!canMove(pos)) continue;
			
			int oppositeOwner = o[pos.o] == O.ME ? O.OPP : O.ME;
			int cell = 0;
			for (Pos n : pos.neighbors4dirs) {
				if (o[n.o] == oppositeOwner && mu[n.o] > 0) {
					cell++;
					lm[pos.o]+=mu[n.o];
				}
			}
			if (cell > 1) {
				lm[pos.o] = Math.min(lm[pos.o], u[pos.o]);
			} else {
				lm[pos.o] = 0;
			}
		}
	}

	public static String unpack(int packed) {
		int inRangeOfRecycler = packed & 0b1; packed>>= 1;
		int canSpawn = packed & 0b1; packed>>= 1;
		int canBuild= packed & 0b1; packed>>= 1;
		int recycler = packed & 0b1; packed>>= 1;
		int units = packed & 0b11111; packed>>= 5;
		int owner = (packed & 0b11) - 1; packed>>= 2;
		int scrapAmount = packed & 0b11111; packed>>= 5;

		return String.format("%s %s %s %s %s %s %s", scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler);
	}
	
	public static String unpackInversed(int packed) {
		int inRangeOfRecycler = packed & 0b1; packed>>= 1;
		int canSpawn = packed & 0b1; packed>>= 1;
		int canBuild= packed & 0b1; packed>>= 1;
		int recycler = packed & 0b1; packed>>= 1;
		int units = packed & 0b11111; packed>>= 5;
		int owner = (packed & 0b11) - 1; packed>>= 2;
		int scrapAmount = packed & 0b11111; packed>>= 5;

		if( owner == O.ME) owner = O.OPP;
		else if (owner == O.OPP) owner = O.ME;
		
		return String.format("%s %s %s %s %s %s %s", scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler);
	}
	
	public boolean canBuild(Pos d) {
		return oo[d.o] == O.ME 
				&& rec[d.o] == 0 
				&& u[d.o] == 0 
				&& ou[d.o] == 0 
				&& s[d.o] > 0;
	}

	public boolean canSpawn(Pos d) {
		return oo[d.o] == O.ME 
				&& rec[d.o] == 0 
				&& s[d.o] > 0;
	}

	public boolean canMove(Pos d) {
		return rec[d.o] == 0 
				&& s[d.o] > 0;
	}

	public void readPacked(FastReader in) {
		int myMatter = in.nextInt();
		int oppMatter = in.nextInt();
		
		String unpacked = ""+myMatter+" "+oppMatter+" ";
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				int packed = in.nextInt();
				String unpack = unpack(packed);
				unpacked += unpack+" ";
			}
		}
		read(FastReader.fromString(unpacked));
	}

	public void readPackedInversed(FastReader in) {
		System.out.println("***** Read inverse ! *****");
		int oppMatter = in.nextInt();
		int myMatter = in.nextInt();
		
		String unpacked = ""+myMatter+" "+oppMatter+" ";
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				int packed = in.nextInt();
				String unpack = unpackInversed(packed);
				unpacked += unpack+" ";
			}
		}
		read(FastReader.fromString(unpacked));
	}

	public double recyclerFullValue(Pos p) {
		int value = s[p.o];
		for (Pos n : p.neighbors4dirs) {
			value += s[n.o];
		}
		return value;
	}

	public void apply(Action action) {
		if (action.type == Action.WAIT) {
		} else if (action.type == Action.BUILD) {
			myMatter-= 1 * O.COST;

			
			
			rec[action.to().o] = 1;
			
		} else if (action.type == Action.SPAWN) {
			myMatter-= action.amount * O.COST;
			
			
			u[action.to().o] += action.amount;

		} else if (action.type == Action.MOVE) {
			u[action.from().o] -= action.amount;
			mu[action.from().o] -= action.amount;
			
			if (o[action.from().o] == o[action.to().o]) {
				//same owner, we add
				u[action.to().o] += action.amount;
			} else if (o[action.to().o] == O.NEUTRAL) {
				if (countRedAround(action.to()) == 0) {
					u[action.to().o] += action.amount;
					o[action.to().o] = O.ME;
				}
			}
			
		} else {
			throw new RuntimeException("Unknown action:"+action);
		}
	}

	public int countRedAround(Pos unit) {
		int count = 0;
		for (Pos n : unit.neighbors4dirs) {
			if (this.o[n.o] == O.OPP) count +=this.u[n.o]; 
		}
		return count;
	}
	
	
	public int redAttackCount(Pos unit) {
		int count = 0;
		for (Pos n : unit.neighbors4dirs) {
			if (this.o[n.o] == O.OPP && this.u[n.o] > 0) count ++; 
		}
		return count;
	}

	public int countBlueAround(Pos unit) {
		int count = 0;
		for (Pos n : unit.neighbors4dirs) {
			if (this.o[n.o] == O.ME) count +=this.u[n.o]; 
		}
		return count;
	}

	public boolean isMine(Pos p) {
		return o[p.o] == O.ME;
	}
	public boolean isNeutral(Pos p) {
		return o[p.o] == O.NEUTRAL;
	}
	public boolean isOpp(Pos p) {
		return o[p.o] == O.OPP;
	}

	public boolean hasUnits(Pos p) {
		return u[p.o] > 0;
	}

	public static boolean isStall() {
		return STALL > 6;
	}

	public static void resetStall() {
		STALL = 0;
	}



}
