package spring2021;

public class Action {
	public static final Action WAIT = new Action(0, -1, -1);
	public static final Action END_TURN = WAIT;
	public static final int COMPLETE = 1;
	public static final int GROW = 2;
	public static final int SEED = 3;
	
	private static Action grows[] = new Action[37];
	private static Action completes[] = new Action[37];
	private static Action seeds[] = new Action[37*37];
	
	static {
		for (int index=0;index<37;index++) {
			grows[index] = new Action(GROW, index, -1);
			completes[index] = new Action(COMPLETE, index, -1);
			for (int j=0;j<37;j++) {
				seeds[index*37+j] = new Action(SEED, index, j);
			}
		}
	}
	
	public int type;
	public int index0;
	public int index1;
	
	public Action(int action, int index0, int index1) {
		this.type = action;
		this.index0 = index0;
		this.index1 = index1;
	}

	@Override
	public String toString() {
		switch(type) {
			case 0: return "WAIT";
			case COMPLETE: return "COMPLETE "+index0;
			case GROW: return "GROW "+index0;
			case SEED: return "SEED "+index0+" "+index1;
			default: return "WAIT DEFAULT";
		}
	}

	public static Action doWait() {
		return WAIT;
	}

	public static Action grow(int i) {
		return grows[i];
	}

	public static Action complete(int i) {
		return completes[i];
	}

	public static Action seed(int i, int j) {
		return seeds[i*37+j];
	}

	public static Action fromString(String str) {
		if (str.startsWith("GROW")) {
			return grow(Integer.parseInt(str.substring(4).trim()));
		} else if (str.startsWith("COMPLETE")) {
			return complete(Integer.parseInt(str.substring(8).trim()));
		} else if (str.startsWith("WAIT")) {
			return WAIT;
		} else if (str.startsWith("SEED")) {
			String[] elts = str.split(" ");
			return seed(Integer.parseInt(elts[1].trim()), Integer.parseInt(elts[2].trim()));
		} else {
			throw new RuntimeException("Cant parse command |"+str+"|");
		}
	}
}
