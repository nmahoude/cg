
/* bManuel (24 + mover */
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;


class Pos {

	public static int WIDTH = 24;

	public static int HEIGHT = 12;

	public static final int MAX_OFFSET = WIDTH * HEIGHT;

	private static Pos[] positions = new Pos[MAX_OFFSET];

	public static Pos VOID = new Pos(-1, -1);

	public static List<Pos> allMapPositions = new ArrayList<>();

	public List<Pos> neighbors4dirs = new ArrayList<>();

	public List<Pos> meAndNeighbors4dirs = new ArrayList<>();

	static void init(int W, int H) {
		WIDTH = W;
		HEIGHT = H;
		//MAX_OFFSET = W * H;
		allMapPositions.clear();
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				Pos pos = new Pos(x, y);
				positions[x + WIDTH * y] = pos;
				allMapPositions.add(pos);
			}
		}
		initCardinalNeighbors();
	//init8Neighbors();
	}

	public final int x;

	public final int y;

	public final int offset;

	private Pos(int x, int y) {
		this.x = x;
		this.y = y;
		this.offset = x + WIDTH * y;
	}

	/* Cardinal neighbors are 4 in the cardinal direction */
	private static void initCardinalNeighbors() {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				Pos p = positions[x + WIDTH * y];
				p.neighbors4dirs.clear();
				p.meAndNeighbors4dirs.clear();
				if (x > 0)
					p.neighbors4dirs.add(Pos.from(x - 1, y));
				if (y > 0)
					p.neighbors4dirs.add(Pos.from(x, y - 1));
				if (x < WIDTH - 1)
					p.neighbors4dirs.add(Pos.from(x + 1, y));
				if (y < HEIGHT - 1)
					p.neighbors4dirs.add(Pos.from(x, y + 1));
				p.meAndNeighbors4dirs.add(p);
				for (Pos pos : p.neighbors4dirs) {
					p.meAndNeighbors4dirs.add(pos);
				}
			}
		}
	}

	/* */
	/**/
	public static Pos secureFrom(int x, int y) {
		if (x < 0 || x >= WIDTH)
			return VOID;
		if (y < 0 || y >= HEIGHT)
			return VOID;
		return from(x, y);
	}

	public static Pos from(int x, int y) {
		return positions[x + WIDTH * y];
	}

	public static Pos from(int offset) {
		return positions[offset];
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	public int manhattan(Pos current) {
		return Math.abs(current.x - x) + Math.abs(current.y - y);
	}
}

class Action {

	private static Action[] cache = new Action[1000];

	static {
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new Action();
		}
	}

	private static int cacheFE = 0;

	public static final int WAIT = 0;

	public static final int MOVE = 1;

	public static final int BUILD = 2;

	public static final int SPAWN = 3;

	int type;

	int amount;

	Pos from;

	Pos to;

	public Pos realTarget;

	private String origin;

	static void resetCache() {
		cacheFE = 0;
	}

	@Override
	public String toString() {
		switch(type) {
			case MOVE:
				return "MOVE " + amount + " " + from.x + " " + from.y + " " + to.x + " " + to.y + ";";
			case WAIT:
				return "WAIT;";
			case BUILD:
				return "BUILD " + to.x + " " + to.y + ";";
			case SPAWN:
				return "SPAWN " + amount + " " + to.x + " " + to.y + ";";
			default:
				return "Unknown type " + type;
		}
	}

	public void debug() {
		if (type == MOVE)
			System.out.println(toString() + " (real=" + realTarget + ") [" + origin + "]");
		else
			System.out.println(toString() + "[" + origin + "]");
	}

	public static Action move(int amount, Pos from, Pos to, String origin) {
		return move(amount, from, to, to, origin);
	}

	public static Action move(int amount, Pos from, Pos to, Pos realTarget, String origin) {
		Action a = cache[cacheFE++];
		a.type = MOVE;
		a.amount = amount;
		a.from = from;
		a.to = to;
		a.realTarget = realTarget;
		a.origin = origin;
		return a;
	}

	public static Action spawn(int amount, Pos pos, String origin) {
		return spawn(amount, pos, pos, origin);
	}
	public static Action spawn(int amount, Pos pos, Pos target, String origin) {
		Action a = cache[cacheFE++];
		a.type = SPAWN;
		a.amount = amount;
		a.to = pos;
		a.origin = origin;
		return a;
	}

	public static Action build(Pos pos, String origin) {
		Action a = cache[cacheFE++];
		a.amount = 1;
		a.type = BUILD;
		a.to = pos;
		a.origin = origin;
		return a;
	}

	public Pos to() {
		return to;
	}

	public Pos from() {
		return from;
	}

	public static Action copyOf(Action action) {
		Action a = new Action();
		a.type = action.type;
		a.amount = action.amount;
		a.from = action.from;
		a.to = action.to;
		a.realTarget = action.realTarget;
		a.origin = action.origin;
		return a;
	}

	public int type() {
		return type;
	}

	public int amount() {
		return amount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, from, realTarget, to, type);
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

}

class O {

	public static final int UNIT_COST = 10;

	public static final int ME = 1;

	public static final int OPP = 0;

	public static final int NEUTRAL = -1;
}

class Logger {

	public static boolean hasWarning;

	public static boolean hasError;

	public static void reset() {
		hasWarning = false;
		hasError = false;
	}

	public static void info(Object str) {
		info(true, str);
	}

	public static void warning(Object str) {
		warning(true, str);
	}

	public static void error(Object str) {
		error(true, str);
	}

	public static void info(boolean enabled, Object str) {
		if (!enabled)
			return;
		System.err.println(str);
	}

	public static void warning(boolean enabled, Object str) {
		if (!enabled)
			return;
		System.err.println(str);
		hasWarning = true;
	}

	public static void error(boolean enabled, Object str) {
		if (!enabled)
			return;
		System.err.println(str);
		hasError = true;
	}
}

class FastReader {

	private static final int BUFFER_SIZE = 1 << 16;

	private DataInputStream din;

	private byte[] buffer;

	private int bufferPointer, bytesRead;

	public FastReader() {
		this(System.in);
	}

	public FastReader(InputStream in) {
		din = new DataInputStream(System.in);
		buffer = new byte[BUFFER_SIZE];
		bufferPointer = bytesRead = 0;
	}

	public FastReader(byte inputs[]) {
		buffer = new byte[inputs.length];
		System.arraycopy(inputs, 0, buffer, 0, inputs.length);
		bufferPointer = 0;
		bytesRead = inputs.length;
	}

	public static FastReader fromString(String input) {
		return new FastReader(input.getBytes());
	}

	public static FastReader fromFile(String filename) throws IOException {
		FastReader reader = new FastReader();
		reader.din = new DataInputStream(new FileInputStream(filename));
		reader.buffer = new byte[BUFFER_SIZE];
		reader.bufferPointer = reader.bytesRead = 0;
		return reader;
	}

	public String readLine() {
		// line length
		byte[] buf = new byte[64];
		int cnt = 0, c;
		while ((c = read()) != -1) {
			if (c == '\n')
				break;
			buf[cnt++] = (byte) c;
		}
		return new String(buf, 0, cnt);
	}

	public int nextInt() {
		int ret = 0;
		byte c = read();
		while (c <= ' ') c = read();
		boolean neg = (c == '-');
		if (neg)
			c = read();
		do {
			ret = ret * 10 + c - '0';
		} while ((c = read()) >= '0' && c <= '9');
		if (neg)
			return -ret;
		return ret;
	}

	public long nextLong() {
		long ret = 0;
		byte c = read();
		while (c <= ' ') c = read();
		boolean neg = (c == '-');
		if (neg)
			c = read();
		do {
			ret = ret * 10 + c - '0';
		} while ((c = read()) >= '0' && c <= '9');
		if (neg)
			return -ret;
		return ret;
	}

	public double nextDouble() {
		double ret = 0, div = 1;
		byte c = read();
		while (c <= ' ') c = read();
		boolean neg = (c == '-');
		if (neg)
			c = read();
		do {
			ret = ret * 10 + c - '0';
		} while ((c = read()) >= '0' && c <= '9');
		if (c == '.') {
			while ((c = read()) >= '0' && c <= '9') {
				ret += (c - '0') / (div *= 10);
			}
		}
		if (neg)
			return -ret;
		return ret;
	}

	private void fillBuffer() {
		try {
			bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
			if (bytesRead == -1)
				buffer[0] = -1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte read() {
		if (bufferPointer == bytesRead)
			fillBuffer();
		return buffer[bufferPointer++];
	}

	public void close() {
		if (din == null)
			return;
		try {
			din.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String nextString() {
		return next();
	}

	public String next() {
		byte c;
		StringBuilder sBuf = new StringBuilder(64);
		do {
			c = read();
		} while (c <= ' ');
		do {
			if (c == '\n' || c == ' ')
				break;
			sBuf.append((char) c);
		} while ((c = read()) != -1);
		return sBuf.toString();
	}

	public String nextLine() {
		byte c;
		StringBuilder sBuf = new StringBuilder(64);
		do {
			c = read();
		} while (c <= ' ');
		do {
			if (c == '\n')
				break;
			sBuf.append((char) c);
		} while ((c = read()) != -1);
		return sBuf.toString();
	}

	public void nextLinePass() {
		byte c;
		do {
			c = read();
		} while (c <= ' ');
		do {
			if (c == '\n')
				break;
		} while ((c = read()) != -1);
	}

	public byte nextByte() {
		return nextBytes()[0];
	}

	public byte[] nextBytes() {
		// max line length
		byte[] buf = new byte[64];
		int cnt = 0, c;
		while ((c = read()) != -1) {
			if (c == '\n' || c == ' ')
				break;
			buf[cnt++] = (byte) c;
		}
		return buf;
	}

	public char[] nextChars() {
		// max line length
		char[] buf = new char[64];
		int cnt = 0, c;
		while ((c = read()) != -1) {
			if (c == '\n' || c == ' ') {
				buf[cnt++] = '\n';
				break;
			} else {
				buf[cnt++] = (char) c;
			}
		}
		buf[cnt++] = '\n';
		return buf;
	}
}

class State {

	public static int turn = -1;

	public static int WIDTH;

	public static int HEIGHT;

	public static Pos center;

	public int myUnitCount = 0;

	public int oppUnitCount = 0;

	public int myRecyclerCount = 0;

	public int myCellCount = 0;

	public int oppCellCount = 0;

	public int myMatter;

	public int oppMatter;

	public int[] scraps = new int[Pos.MAX_OFFSET];

	public int[] owner = new int[Pos.MAX_OFFSET];

	public int[] units = new int[Pos.MAX_OFFSET];

	public int[] movableUnits = new int[Pos.MAX_OFFSET];

	public int[] originalUnits = new int[Pos.MAX_OFFSET];

	public int[] recycler = new int[Pos.MAX_OFFSET];

	public State() {
	}

	public void copyFrom(State model) {
		this.myUnitCount = model.myUnitCount;
		this.oppUnitCount = model.oppUnitCount;
		this.myMatter = model.myMatter;
		this.oppMatter = model.oppMatter;
		this.myRecyclerCount = model.myRecyclerCount;
		this.myCellCount = model.myCellCount;
		this.oppCellCount = model.oppCellCount;
		System.arraycopy(model.scraps, 0, this.scraps, 0, Pos.MAX_OFFSET);
		System.arraycopy(model.owner, 0, this.owner, 0, Pos.MAX_OFFSET);
		System.arraycopy(model.units, 0, this.units, 0, Pos.MAX_OFFSET);
		System.arraycopy(model.movableUnits, 0, this.movableUnits, 0, Pos.MAX_OFFSET);
		System.arraycopy(model.originalUnits, 0, this.originalUnits, 0, Pos.MAX_OFFSET);
		System.arraycopy(model.recycler, 0, this.recycler, 0, Pos.MAX_OFFSET);
	}

	public void readGlobal(FastReader in) {
		WIDTH = in.nextInt();
		HEIGHT = in.nextInt();
		Pos.init(WIDTH, HEIGHT);
		center = Pos.from(WIDTH / 2, HEIGHT / 2);
	}

	public void readOptionalState(FastReader in) {
		// turn minus one beacuse the read will add 1 !
		State.turn = in.nextInt() - 1;
	}

	public void saveOptionalState() {
		Logger.info(Player.DEBUG_OUPUT, "Optional State : ");
		Logger.info(Player.DEBUG_OUPUT, String.format("^ %d", State.turn));
	}

	public void read(FastReader in) {
		Action.resetCache();
		State.turn++;
		myRecyclerCount = 0;
		myUnitCount = 0;
		oppUnitCount = 0;
		myCellCount = 0;
		oppCellCount = 0;
		myMatter = in.nextInt();
		oppMatter = in.nextInt();
		Player.start = System.currentTimeMillis();
		if (Player.DEBUG_OUPUT) {
			// print global every line
			Logger.info("Global ");
			Logger.info("^" + WIDTH + " " + HEIGHT);
			saveOptionalState();
			Logger.info(String.format("^%d %d", myMatter, oppMatter));
		}
		for (int y = 0; y < HEIGHT; y++) {
			//if (Player.DEBUG_OUPUT) System.err.println("Y : "+y);
			if (Player.DEBUG_OUPUT)
				System.err.print("^");
			for (int x = 0; x < WIDTH; x++) {
				Pos p = Pos.from(x, y);
				int scrapAmount = in.nextInt();
				// 1 = me, 0 = foe, -1 = neutral
				int owner = in.nextInt();
				int units = in.nextInt();
				int recycler = in.nextInt();
				this.scraps[p.offset] = scrapAmount;
				this.units[p.offset] = units;
				this.movableUnits[p.offset] = units;
				// lecture seule, c'est pir savoir si je peux poser des reycler à tout moment
				this.originalUnits[p.offset] = units;
				this.owner[p.offset] = owner;
				this.recycler[p.offset] = recycler;
				if (recycler != 0 && owner == O.ME)
					myRecyclerCount++;
				switch(owner) {
					case 1:
						myUnitCount += units;
						if (recycler == 0)
							myCellCount++;
						break;
					case 0:
						oppUnitCount += units;
						if (recycler == 0)
							oppCellCount++;
						break;
				}
				int canBuild = in.nextInt();
				int canSpawn = in.nextInt();
				int inRangeOfRecycler = in.nextInt();
				// if (Player.DEBUG_OUPUT) System.err.println(String.format("^%d %d %d %d %d %d %d", scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler));
				if (Player.DEBUG_OUPUT) {
					int pack = 0;
					pack <<= 5;
					pack += scrapAmount;
					pack <<= 2;
					pack += (owner + 1);
					pack <<= 5;
					pack += (units);
					pack <<= 1;
					pack += (recycler != 0 ? 1 : 0);
					pack <<= 1;
					pack += (canBuild != 0 ? 1 : 0);
					pack <<= 1;
					pack += (canSpawn != 0 ? 1 : 0);
					pack <<= 1;
					pack += (inRangeOfRecycler != 0 ? 1 : 0);
					System.err.print(String.format("%5.5s ", pack));
				}
			}
			if (Player.DEBUG_OUPUT)
				System.err.println();
		}
	}

	public static String unpack(int packed) {
		int inRangeOfRecycler = packed & 0b1;
		packed >>= 1;
		int canSpawn = packed & 0b1;
		packed >>= 1;
		int canBuild = packed & 0b1;
		packed >>= 1;
		int recycler = packed & 0b1;
		packed >>= 1;
		int units = packed & 0b11111;
		packed >>= 5;
		int owner = (packed & 0b11) - 1;
		packed >>= 2;
		int scrapAmount = packed & 0b11111;
		packed >>= 5;
		return String.format("%s %s %s %s %s %s %s", scrapAmount, owner, units, recycler, canBuild, canSpawn, inRangeOfRecycler);
	}

	public boolean canBuild(Pos d) {
		return myMatter >= O.UNIT_COST && owner[d.offset] == O.ME && recycler[d.offset] == 0 && units[d.offset] == 0 && scraps[d.offset] > 0;
	}

	public boolean canSpawn(Pos d) {
		return owner[d.offset] == O.ME && recycler[d.offset] == 0 && scraps[d.offset] > 0;
	}

	public boolean canMove(Pos d) {
		return recycler[d.offset] == 0 && scraps[d.offset] > 0;
	}

	public void readPacked(FastReader in) {
		int myMatter = in.nextInt();
		int oppMatter = in.nextInt();
		String unpacked = "" + myMatter + " " + oppMatter + " ";
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				int packed = in.nextInt();
				String unpack = unpack(packed);
				unpacked += unpack + " ";
			}
		}
		read(FastReader.fromString(unpacked));
	}

	public double recyclerFullValue(Pos p) {
		int value = scraps[p.offset];
		for (Pos n : p.neighbors4dirs) {
			value += scraps[n.offset];
		}
		return value;
	}

	public void apply(Action action) {
		if (action.type == Action.WAIT) {
		} else if (action.type == Action.BUILD) {
			myMatter -= 1 * O.UNIT_COST;
			recycler[action.to().offset] = 1;
		} else if (action.type == Action.SPAWN) {
			myMatter -= action.amount * O.UNIT_COST;
			units[action.to().offset] += action.amount;
		} else if (action.type == Action.MOVE) {
			units[action.from().offset] -= action.amount;
			movableUnits[action.from().offset] -= action.amount;
			if (owner[action.from().offset] == owner[action.to().offset]) {
				//same owner, we add
				units[action.to().offset] += action.amount;
			} else if (owner[action.to().offset] == O.NEUTRAL) {
			// TODO check around to be sure ?
			//units[action.to().offset] += action.amount;
			//owner[action.to().offset] = O.ME;
			}
		} else {
			throw new RuntimeException("Unknown action:" + action);
		}
	}

	public int countRedAround(Pos unit) {
		int count = 0;
		for (Pos n : unit.neighbors4dirs) {
			if (this.owner[n.offset] == O.OPP)
				count += this.units[n.offset];
		}
		return count;
	}

	public boolean isMine(Pos p) {
		return owner[p.offset] == O.ME;
	}

	public boolean isNeutral(Pos p) {
		return owner[p.offset] == O.NEUTRAL;
	}

	public boolean isOpp(Pos p) {
		return owner[p.offset] == O.OPP;
	}

	public boolean hasUnits(Pos p) {
		return units[p.offset] > 0;
	}
}

/**
 * All the position representing an Ilot
 *
 */
/**
 * All the position representing an Ilot
 *
 */
class Ilot {

	public static final int DISPUTED = 4;

	public Pos pos[] = new Pos[Pos.MAX_OFFSET];

	public int posFE;

	public int ruler = O.NEUTRAL;

	public int myTroupsCount;

	public int oppTroupsCount;

	public int nbRecyclers;

	public boolean isFullCoverByMe;

	public Ilot() {
		isFullCoverByMe = true;
	}

	void build(State state, Pos start, int[] visited, int currentIndex) {
		pos[posFE++] = start;
		visited[start.offset] = currentIndex;
		if (state.owner[start.offset] != O.ME) {
			isFullCoverByMe = false;
		}
		if (ruler == O.NEUTRAL) {
			ruler = state.owner[start.offset];
		} else if (ruler == O.ME) {
		} else if (ruler == O.OPP) {
		}
		if (state.owner[start.offset] == O.ME) {
			myTroupsCount += state.units[start.offset];
		} else {
			oppTroupsCount += state.units[start.offset];
		}
		if (state.owner[start.offset] != O.NEUTRAL && state.owner[start.offset] != ruler) {
			ruler = DISPUTED;
		}
		for (Pos n : start.neighbors4dirs) {
			if (visited[n.offset] != currentIndex) {
				if (state.recycler[n.offset] > 0) {
					pos[posFE++] = n;
					visited[n.offset] = currentIndex;
					nbRecyclers++;
				} else if (state.canMove(n)) {
					build(state, n, visited, currentIndex);
				}
			}
		}
	}

	public void debug() {
		for (int i = 0; i < posFE; i++) {
			System.err.print(pos[i] + " ");
		}
		System.err.println();
		System.err.println("	=> ruler index " + ruler);
	}

	public static List<Ilot> build(State state, List<Pos> input) {
		int startIndex = 1;
		// TODO do not instantiate here, but need to remember last index and can overflow !
		int[] visited = new int[Pos.MAX_OFFSET];
		List<Ilot> ilots = new ArrayList<>();
		for (Pos p : input) {
			if (visited[p.offset] >= startIndex)
				continue;
			if (!state.canMove(p))
				continue;
			Ilot ilot = new Ilot();
			ilot.build(state, p, visited, startIndex);
			ilots.add(ilot);
		}
		return ilots;
	}

	public static List<Ilot> build(State state) {
		return build(state, Pos.allMapPositions);
	}

	@Override
	public String toString() {
		String output = "Ruler:" + ruler + ", cells=" + posFE + "(real : " + (posFE - nbRecyclers) + ") => ";
		for (int i = 0; i < posFE; i++) {
			output += pos[i] + ",";
		}
		return output;
	}

	public boolean isFullCoverByMe() {
		return isFullCoverByMe;
	}

	public boolean isDisputed() {
		return ruler == DISPUTED;
	}

	public Set<Pos> oppReachablePositions(State state) {
		// 1. Get all his cells in the ilot
		Set<Pos> allOppReachablePositions = new HashSet<>();
		Deque<Pos> current = new ArrayDeque<>();
		// TODO le precalculer dans l'ilot, ca semble déjà fait, mais pas conservé
		for (int i = 0; i < this.posFE; i++) {
			Pos opp = this.pos[i];
			if (state.owner[opp.offset] != O.OPP)
				continue;
			if (!state.canMove(opp))
				continue;
			allOppReachablePositions.add(opp);
			current.add(opp);
		}
		// 2. from all the positions,
		while (!current.isEmpty()) {
			Pos p = current.poll();
			for (Pos n : p.neighbors4dirs) {
				if (state.owner[n.offset] == O.ME || !state.canMove(n))
					continue;
				if (allOppReachablePositions.contains(n))
					continue;
				allOppReachablePositions.add(n);
				current.offer(n);
			}
		}
		return allOppReachablePositions;
	}

	public int size() {
		return posFE;
	}

	public static int myPotentialCells(List<Ilot> originalIlots) {
		int count = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.ME)
				count += ilot.size();
			if (ilot.isDisputed())
				count += ilot.size();
		}
		return count;
	}

	public static int oppPotentialCells(List<Ilot> originalIlots) {
		int count = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.OPP)
				count += ilot.size();
			if (ilot.isDisputed())
				count += ilot.size();
		}
		return count;
	}
}

class TTSlice {

	public State state = new State();

	// List<Ilot> ilots = new ArrayList<>();
	public void copyFrom(State original) {
		this.state.copyFrom(original);
	}

	public void copyFrom(TTSlice model) {
		this.state.copyFrom(model.state);
	}
}

class Sim {

	public static void oneTurn(State state) {
		int[] minedByMe = new int[Pos.MAX_OFFSET];
		int[] minedByOpp = new int[Pos.MAX_OFFSET];
		for (Pos current : Pos.allMapPositions) {
			if (state.recycler[current.offset] == 0)
				continue;
			if (state.owner[current.offset] == O.ME) {
				for (Pos n : current.meAndNeighbors4dirs) {
					minedByMe[n.offset]++;
				}
			} else {
				for (Pos n : current.meAndNeighbors4dirs) {
					minedByOpp[n.offset]++;
				}
			}
		}
		// recycle & clean recyclers
		for (Pos current : Pos.allMapPositions) {
			if (state.scraps[current.offset] > 0) {
				if (minedByMe[current.offset] > 0)
					state.myMatter++;
				if (minedByOpp[current.offset] > 0)
					state.oppMatter++;
				if (minedByMe[current.offset] > 0 || minedByOpp[current.offset] > 0)
					state.scraps[current.offset]--;
			}
			if (state.scraps[current.offset] == 0) {
				state.recycler[current.offset] = 0;
				state.units[current.offset] = 0;
				state.movableUnits[current.offset] = 0;
			}
		}
	}

	public static void tenTurn(State state) {
		for (int i = 0; i < 10; i++) {
			oneTurn(state);
		}
	}
}

/**
 * 
 * @author nmahoude
 *
 */
/**
 * 
 * @author nmahoude
 *
 */
class TimeTraveler {

	public static final int MAX_DEPTH = 11;

	private TTSlice slices[] = new TTSlice[MAX_DEPTH];

	public Map<Integer, List<Pos>> positions = new HashMap<>();

	private State original;

	public int myMatterBonus[] = new int[MAX_DEPTH];

	public int myTotalMatterBonus = 0;

	public int oppMatterBonus[] = new int[MAX_DEPTH];

	public int oppTotalMatterBonus = 0;

	public TimeTraveler() {
		for (int i = 0; i < MAX_DEPTH; i++) {
			slices[i] = new TTSlice();
			positions.put(i, new ArrayList<>());
		}
	}

	public void init(State original) {
		this.original = original;
		// initialize all slices of time
		slices[0].copyFrom(original);
		myTotalMatterBonus = 0;
		oppTotalMatterBonus = 0;
		myMatterBonus[0] = 0;
		oppMatterBonus[0] = 0;
		for (int i = 1; i < MAX_DEPTH; i++) {
			slices[i].copyFrom(slices[i - 1]);
			Sim.oneTurn(slices[i].state);
			int delta;
			delta = slices[i].state.myMatter - slices[i - 1].state.myMatter;
			myMatterBonus[i] = delta;
			myTotalMatterBonus += delta;
			delta = slices[i].state.oppMatter - slices[i - 1].state.oppMatter;
			oppMatterBonus[i] = delta;
			oppTotalMatterBonus += delta;
		}
	}

	private static int[] matrix = new int[Pos.MAX_OFFSET * MAX_DEPTH];

	private static int MATRIX_INDEX;

	public void bfsInTime(Pos arriveAtT0) {
		Player.BFS_IN_TIME++;
		MATRIX_INDEX++;
		for (int i = 0; i < MAX_DEPTH; i++) {
			positions.get(i).clear();
		}
		positions.get(0).add(arriveAtT0);
		for (int time = 1; time < MAX_DEPTH; time++) {
			List<Pos> nextPositionsAtSlice = positions.get(time);
			for (Pos p : positions.get(time - 1)) {
				if (!slices[time].state.canMove(p))
					continue;
				for (Pos n : p.neighbors4dirs) {
					// already got there at the same time
					if (matrix[n.offset + Pos.MAX_OFFSET * time] == MATRIX_INDEX + 1)
						continue;
					// won't go back another time
					matrix[n.offset + Pos.MAX_OFFSET * time] = MATRIX_INDEX + 1;
					nextPositionsAtSlice.add(n);
				}
			}
			positions.get(time).addAll(nextPositionsAtSlice);
		}
	}

	public List<Pos> firstNotOwnerCells(Pos arriveAtT0) {
		for (int i = 0; i < MAX_DEPTH; i++) {
			positions.get(i).clear();
		}
		// 3D matrix 
		int[][] matrix = new int[Pos.MAX_OFFSET][MAX_DEPTH];
		List<Pos> positionsAtSlice = new ArrayList<>();
		if (slices[0].state.canMove(arriveAtT0)) {
			positionsAtSlice.add(arriveAtT0);
			positions.get(0).addAll(positionsAtSlice);
		}
		for (int time = 1; time < MAX_DEPTH; time++) {
			List<Pos> nextPositionsAtSlice = new ArrayList<>();
			for (Pos p : positionsAtSlice) {
				for (Pos n : p.meAndNeighbors4dirs) {
					if (!slices[time].state.canMove(n))
						continue;
					// already got there at the same time
					if (matrix[n.offset][time] != 0)
						continue;
					// won't go back another time
					matrix[n.offset][time] = 1;
					nextPositionsAtSlice.add(n);
				}
			}
			positionsAtSlice.clear();
			positionsAtSlice.addAll(nextPositionsAtSlice);
			positions.get(time).addAll(nextPositionsAtSlice);
		}
		return null;
	}

	public Set<Pos> forbidenCells() {
		Set<Pos> forbidenCells = new HashSet<>();
		// find all potential forbiden cells (the one that will disappear)
		Set<Pos> toCheck = new HashSet<>();
		for (Pos current : Pos.allMapPositions) {
			if (original.recycler[current.offset] == 0)
				continue;
			for (Pos n : current.neighbors4dirs) {
				if (original.canMove(n)) {
					toCheck.add(n);
				}
			}
		}
		for (Pos current : toCheck) {
			if (!original.canMove(current))
				continue;
			this.bfsInTime(current);
			if (this.positions.get(TimeTraveler.MAX_DEPTH - 1).isEmpty()) {
				if (Player.DEBUG_TIMETRAVEL)
					System.err.println(current + " => dead cell");
				forbidenCells.add(current);
			}
		}
		return forbidenCells;
	}

	public TTSlice sliceAt(int time) {
		if (time >= MAX_DEPTH)
			return slices[MAX_DEPTH - 1];
		return slices[time];
	}
}

class BFS {

	public int distances[] = new int[Pos.MAX_OFFSET];

	public List<Pos> allMyUnits = new ArrayList<>();

	public void calculate(State state) {
		List<Pos> init = new ArrayList<>();
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				if (state.owner[p.offset] == O.ME && state.units[p.offset] > 0) {
					init.add(Pos.from(x, y));
				}
			}
		}
		calculate(state, init, null);
	}

	public Pos reconstructPath2(State state, TimeTraveler tt, Pos from, Pos to, List<Pos>  visitedCells, List<Pos> frontier) {
		List<Pos> init = new ArrayList<>();
		init.add(from);
		calculate(state, init, tt);
		int distance = distances[to.offset];
		if (distance == Integer.MAX_VALUE) {
			Logger.error("No path from " + from + " " + to);
			return null;
		}
		bestPos = null;
		bestScore = Double.NEGATIVE_INFINITY;
		reconstructFrom(state, to, from, distance - 1, 0, visitedCells, frontier);
		return bestPos;
	}

	Pos bestPos;

	double bestScore;

	private void reconstructFrom(State state, Pos current, Pos target, int distToFind, double score, List<Pos> visitedCells, List<Pos> frontier) {
		for (Pos n : current.neighbors4dirs) {
			if (distances[n.offset] != distToFind)
				continue;
			if (n == target) {
				if (score > bestScore) {
					bestScore = score;
					bestPos = current;
				}
				return;
			}
			double localScore = 0.0;
			localScore -= visitedCells.contains(n) ? 2 : 0;
			localScore += state.owner[n.offset] == O.NEUTRAL ? 1 : 0;
			localScore += (frontier.contains(n) && state.units[n.offset] == 0) ? 0.1 : 0;
			reconstructFrom(state, n, target, distToFind - 1, score + localScore, visitedCells,frontier);
		}
	}

	public Pos reconstructPath(State state, TimeTraveler tt, Pos from, Pos to) {
		List<Pos> init = new ArrayList<>();
		init.add(from);
		calculate(state, init, tt);
		int distance = distances[to.offset];
		if (distance == Integer.MAX_VALUE) {
			Logger.error("No path from " + from + " " + to);
			return null;
		}
		Pos current = to;
		int currentDist = distance - 1;
		Pos firstStep = null;
		while (current != from) {
			if (Player.DEBUG_PATH)
				System.err.print(current + " <= ");
			Pos next = null;
			for (Pos n : current.neighbors4dirs) {
				if (distances[n.offset] == currentDist) {
					// TODO choix à faire si plusieurs path ? => renvoyer tous les chemins ... TOUS ?? Refaire la recherche à l'envers (to, from) pour descendre dans l'autre sens, mais attention au TT qui doit aller dans l'autre sens aussi !
					next = n;
					break;
				}
			}
			if (currentDist == 0) {
				firstStep = current;
			}
			current = next;
			currentDist--;
		}
		if (Player.DEBUG_PATH)
			System.err.println();
		return firstStep;
	}

	public void calculate(State state, Pos from, TimeTraveler tt) {
		List<Pos> init = new ArrayList<>();
		init.add(from);
		calculate(state, init, Collections.emptyList(), tt);
	}

	public void calculate(State state, List<Pos> init, TimeTraveler tt) {
		calculate(state, init, Collections.emptyList(), tt);
	}

	public void calculate(State state, List<Pos> init, List<Pos> delayed, TimeTraveler tt) {
		allMyUnits.clear();
		resetDistances();
		// add init positions
		allMyUnits.addAll(init);
		for (Pos pos : init) {
			distances[pos.offset] = 0;
		}
		for (Pos pos : delayed) {
			distances[pos.offset] = 1;
		}
		List<Pos> current = new ArrayList<>();
		current.addAll(init);
		current.addAll(delayed);
		List<Pos> next = new ArrayList<>();
		while (!current.isEmpty()) {
			next.clear();
			for (Pos p : current) {
				int currentDist = distances[p.offset];
				for (Pos n : p.neighbors4dirs) {
					if (tt != null) {
						if (tt.sliceAt(currentDist + 1).state.canMove(n) && distances[n.offset] > currentDist + 1) {
							distances[n.offset] = currentDist + 1;
							next.add(n);
						}
					} else {
						if (state.canMove(n) && distances[n.offset] > currentDist + 1) {
							distances[n.offset] = currentDist + 1;
							next.add(n);
						}
					}
				}
			}
			List<Pos> swp = current;
			current = next;
			next = swp;
		}
	//printGrid();
	}

	private void resetDistances() {
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				distances[p.offset] = Integer.MAX_VALUE;
			}
		}
	}

	private void printGrid() {
		System.err.println("BFS Grid");
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				if (distances[p.offset] == Integer.MAX_VALUE) {
					System.err.print("XXX ");
				} else {
					System.err.print(String.format("%3d ", distances[p.offset]));
				}
			}
			System.err.println();
		}
	}

	public Pos findClosest(Predicate<Pos> filter) {
		int minDist = Integer.MAX_VALUE;
		Pos best = null;
		for (Pos p : Pos.allMapPositions) {
			if (!filter.test(p))
				continue;
			if (distances[p.offset] < minDist) {
				minDist = distances[p.offset];
				best = p;
			}
		}
		return best;
	}
}

class Territory {

	int blueDistances[] = new int[Pos.MAX_OFFSET];

	int redDistances[] = new int[Pos.MAX_OFFSET];

	List<Pos> blueRobots = new ArrayList<>();

	List<Pos> redRobots = new ArrayList<>();

	List<Pos> bluePos = new ArrayList<>();

	List<Pos> redPos = new ArrayList<>();

	public List<Pos> disputed = new ArrayList<>();

	public List<Pos> blueTerritory = new ArrayList<>();

	public List<Pos> redTerritory = new ArrayList<>();

	public List<Pos> baston = new ArrayList<>();

	public double[] blueDangers = new double[Pos.MAX_OFFSET];

	public double[] redDangers = new double[Pos.MAX_OFFSET];

	public List<Pos> frontier = new ArrayList<>();

	private TimeTraveler tt;

	public void calculateTerritories(Ilot ilot, TimeTraveler tt) {
		calculateTerritoriesA(ilot.pos, ilot.posFE, tt);
		calculateDangerNew(ilot, tt);
		// oops need to recalculate them ! WHY ?
		calculateTerritoriesA(ilot.pos, ilot.posFE, tt);
	}

	public void calculateTerritoriesA(Pos[] positions, int posFE, TimeTraveler tt) {
		this.tt = tt;
		State init = tt.sliceAt(0).state;
		init(positions, posFE, init);
		// calculate distances & save them
		BFS bfs = new BFS();
		bfs.calculate(null, blueRobots, bluePos, tt);
		System.arraycopy(bfs.distances, 0, blueDistances, 0, Pos.MAX_OFFSET);
		bfs.calculate(null, redRobots, redPos, tt);
		System.arraycopy(bfs.distances, 0, redDistances, 0, Pos.MAX_OFFSET);
		disputed.clear();
		blueTerritory.clear();
		redTerritory.clear();
		baston.clear();
		for (int i = 0; i < posFE; i++) {
			Pos pos = positions[i];
			if (!init.canMove(pos))
				continue;
			if (blueDistances[pos.offset] < redDistances[pos.offset]) {
				if (!blueTerritory.contains(pos))
					blueTerritory.add(pos);
			} else if (blueDistances[pos.offset] > redDistances[pos.offset]) {
				if (!redTerritory.contains(pos))
					redTerritory.add(pos);
			} else {
				if (!disputed.contains(pos))
					disputed.add(pos);
			}
		}
		frontier.clear();
		for (Pos p : disputed) {
			boolean blue = init.owner[p.offset] == O.ME;
			boolean red = init.owner[p.offset] == O.OPP;
			for (Pos n : p.neighbors4dirs) {
				if (redTerritory.contains(n))
					red = true;
				if (blueTerritory.contains(n))
					blue = true;
			}
			if ((red && blue) || (red && !blue)) {
				if (!frontier.contains(p))
					frontier.add(p);
			}
		}
		for (Pos p : blueTerritory) {
			for (Pos n : p.neighbors4dirs) {
				if (redTerritory.contains(n)) {
					if (!frontier.contains(p))
						frontier.add(p);
				}
			}
		}
		baston.clear();
		for (Pos p : frontier) {
			if (disputed.contains(p)) {
				if (!baston.contains(p))
					baston.add(p);
			} else if (init.owner[p.offset] == O.ME) {
				// check if red unit next
				for (Pos n : p.neighbors4dirs) {
					if (init.owner[n.offset] == O.OPP && init.units[n.offset] > 0) {
						if (!baston.contains(p))
							baston.add(p);
					}
				}
			}
		}
	}

	/*
	 * danger si on donne trop de cases à nous en laissant la case
	 * 
	 * malus si elles sont vide
	 */
	private void calculateDangerNew(Ilot ilot, TimeTraveler tt) {
		State state = tt.sliceAt(0).state;
		for (Pos p : Pos.allMapPositions) {
			blueDangers[p.offset] = 0.0;
			redDangers[p.offset] = 0.0;
		}
		for (Pos current : Pos.allMapPositions) {
			double blueDanger = 0;
			double redDanger = 0;
			int blueUnitsAround = 0;
			int redUnitsAround = 0;
			for (Pos n : current.neighbors4dirs) {
				if (!state.canMove(n))
					continue;
				if (state.owner[n.offset] == O.ME)
					blueUnitsAround += state.units[n.offset];
				if (state.owner[n.offset] == O.OPP)
					redUnitsAround += state.units[n.offset];
				if (blueTerritory.contains(n) || disputed.contains(n)) {
					blueDanger += 1.0;
					if (state.isNeutral(n))
						blueDanger += 150.0;
					else if (state.owner[n.offset] == O.ME && state.units[n.offset] == 0)
						blueDanger += 5.0;
				}
				if (redTerritory.contains(n) || disputed.contains(n)) {
					redDanger += 1.0;
					if (state.isNeutral(n))
						redDanger += 15.0;
					else if (state.owner[n.offset] == O.OPP && state.units[n.offset] == 0)
						redDanger += 5.0;
				}
			}
			double bonus = state.owner[current.offset] == O.NEUTRAL ? 1.0 : 0.0;
			this.blueDangers[current.offset] = bonus + blueDanger + 1.0 * Math.max(0, redUnitsAround - blueUnitsAround);
			this.redDangers[current.offset] = bonus + redDanger + 1.0 * Math.max(0, blueUnitsAround - redUnitsAround);
		}
	}

	private void calculateDanger(Ilot ilot, TimeTraveler tt) {
		List<Pos> originalFrontier = new ArrayList<>();
		originalFrontier.addAll(frontier);
		int originalMT = this.blueTerritory.size();
		int originalOT = this.redTerritory.size();
		int originalNT = this.disputed.size();
		for (Pos p : Pos.allMapPositions) {
			blueDangers[p.offset] = 0.0;
			redDangers[p.offset] = 0.0;
		}
		for (Pos p : originalFrontier) {
			if (tt.sliceAt(0).state.owner[p.offset] == O.OPP)
				continue;
			// let's say he won this cell
			int originalOwner = tt.sliceAt(0).state.owner[p.offset];
			int originalUnits = tt.sliceAt(0).state.units[p.offset];
			tt.sliceAt(0).state.owner[p.offset] = O.OPP;
			tt.sliceAt(0).state.units[p.offset] = 1;
			calculateTerritoriesA(ilot.pos, ilot.posFE, tt);
			int myT = this.blueTerritory.size();
			int oT = this.redTerritory.size();
			int nt = this.disputed.size();
			boolean hasRedAround = originalOwner == O.OPP && originalUnits > 0;
			boolean hasBlueAround = originalOwner == O.ME && originalUnits > 0;
			for (Pos n : p.neighbors4dirs) {
				if (tt.sliceAt(0).state.units[n.offset] > 0) {
					if (tt.sliceAt(0).state.owner[n.offset] == O.ME)
						hasBlueAround = true;
					if (tt.sliceAt(0).state.owner[n.offset] == O.OPP)
						hasRedAround = true;
				}
			}
			if (hasRedAround)
				blueDangers[p.offset] = 20.0 * (oT - originalOT) + (nt - originalNT);
			if (hasBlueAround)
				redDangers[p.offset] = 20.0 * (myT - originalMT) + (nt - originalNT);
			tt.sliceAt(0).state.owner[p.offset] = originalOwner;
			tt.sliceAt(0).state.units[p.offset] = originalUnits;
		}
	}

	private void init(Pos[] positions, int posFE, State init) {
		bluePos.clear();
		redPos.clear();
		blueRobots.clear();
		redRobots.clear();
		for (int i = 0; i < posFE; i++) {
			Pos pos = positions[i];
			blueDistances[pos.offset] = Integer.MAX_VALUE;
			redDistances[pos.offset] = Integer.MAX_VALUE;
			if (!init.canMove(pos))
				continue;
			int unitsCount = init.units[pos.offset];
			if (init.owner[pos.offset] == O.ME) {
				if (unitsCount > 0) {
					if (!blueRobots.contains(pos))
						blueRobots.add(pos);
				} else {
					if (!bluePos.contains(pos))
						bluePos.add(pos);
				}
			}
			if (init.owner[pos.offset] == O.OPP) {
				if (unitsCount > 0) {
					if (!redRobots.contains(pos))
						redRobots.add(pos);
				} else {
					if (!redPos.contains(pos))
						redPos.add(pos);
				}
			}
		}
	}

	public void debugBaston() {
		if (baston.isEmpty())
			return;
		Logger.warning("BASTON ! ");
		for (Pos b : baston) {
			System.err.println(b);
		}
	}
}

class DiffusionMap {

	int visited[] = new int[Pos.MAX_OFFSET];

	public double grid[] = new double[Pos.MAX_OFFSET];

	static int visitedIndex = 0;

	private State state;

	private int targetOwner;

	private double diffusionCoef;

	public DiffusionMap(int targetOwner, double diffusionCoef) {
		this.targetOwner = targetOwner;
		this.diffusionCoef = diffusionCoef;
	}

	public void calculate(State state) {
		this.state = state;
		for (Pos p : Pos.allMapPositions) {
			grid[p.offset] = initValue(p);
		}
		for (Pos p : Pos.allMapPositions) {
			if (isHearth(state, p)) {
				grid[p.offset] = hearthValue(p);
				diffuseFrom(p, visitedIndex++);
			}
		}
	// printGrid();
	}

	private double initValue(Pos p) {
		if (state.owner[p.offset] == targetOwner) {
			return 80;
		} else {
			return 0;
		}
	}

	private double hearthValue(Pos p) {
		// * state.units[p.offset];
		return 100;
	}

	private void diffuseFrom(Pos init, int indexToCheck) {
		List<Pos> current = Arrays.asList(init);
		while (!current.isEmpty()) {
			List<Pos> next = new ArrayList<>();
			for (Pos p : current) {
				for (Pos n : p.neighbors4dirs) {
					if (visited[n.offset] == indexToCheck)
						continue;
					visited[n.offset] = indexToCheck;
					if (state.canMove(n)) {
						grid[n.offset] = Math.max(grid[n.offset], diffuse(grid[p.offset]));
						next.add(n);
					}
				}
			}
			current = next;
		}
	}

	private double diffuse(double d) {
		return d * diffusionCoef;
	}

	private boolean isHearth(State state, Pos p) {
		return state.owner[p.offset] == targetOwner && state.units[p.offset] > 0;
	}

	public void debug() {
		System.err.println("DiffusionMap Grid");
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				if (grid[p.offset] == Integer.MAX_VALUE) {
					System.err.print("XXXXXXX ");
				} else {
					System.err.print(String.format("%5.2f ", grid[p.offset]));
				}
			}
			System.err.println();
		}
	}
}

class Navigator {

	BFS bfs = new BFS();

	public Pos navigate(State work, Pos from, Pos to, TimeTraveler tt, List<Pos> frontier) {
		bfs.calculate(work, from, tt);
		return navigate(work, from, to, tt, Collections.emptyList(), frontier);
	}

	public Pos navigate(State work, Pos from, Pos to, TimeTraveler tt, List<Pos> visitedCells, List<Pos> frontier) {
		bfs.calculate(work, from, tt);
		return bfs.reconstructPath2(work, tt, from, to, visitedCells, frontier);
	}
}

class Spreader {
	static Navigator navigator = new Navigator();
	
	private TimeTraveler tt;
	
	public List<Action> think(State originalWork, List<Action> alreadyDoneActions, Ilot ilot, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();

		// need to copy work because we do some weird thing with movable units to block them
		State work = new State();
		work.copyFrom(originalWork);
		
		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);

		
		actions.addAll(moves(work, alreadyDoneActions, ilot,tt, territory));
		actions.addAll(spawn(work, ilot, territory));
		
		
		
		// apply action to originalWork
		for (Action a : actions) {
			originalWork.apply(a);
		}
		return actions;
	}
	
	
	
	
	private Collection<? extends Action> spawn(State work, Ilot ilot, Territory territory) {
		List<Action> actions = new ArrayList<>();

		boolean hasSpawn = true;
		while (hasSpawn && work.myMatter >= O.UNIT_COST) {
			hasSpawn = false;
			
			int bestDist = Integer.MAX_VALUE;
			Pos bestPos = null;
			
			for (int i=0;i<ilot.posFE;i++) {
				Pos unit = ilot.pos[i];
				if (!work.canSpawn(unit)) continue;
			
				bfses[unit.offset].calculate(work, unit, tt);
				
				int dist = distanceToFrontier(unit, territory.frontier);
				if (dist < bestDist) {
					bestDist = dist;
					bestPos = unit;
				}
			}

			if (bestPos != null) {
				Action spawn = Action.spawn(work.myMatter / O.UNIT_COST, bestPos, Spreader.class.getSimpleName());
				actions.add(spawn);
				work.apply(spawn);
				hasSpawn = true;
			}
		}
		return actions;
	}


	static BFS bfses[] = new BFS[Pos.MAX_OFFSET];
	static {
		for (int i=0;i<bfses.length;i++) {
			bfses[i] = new BFS();
		}
	}

	public List<Action> moves(State work, List<Action> alreadyDoneActions, Ilot ilot, TimeTraveler tt, Territory territory) {
		List<Action> actions = new ArrayList<>();

		// build frontier
		List<Pos> frontier = new ArrayList<>();
		for (Pos p : territory.frontier) {
			if (work.recycler[p.offset] != 0) continue;
			if (work.owner[p.offset] == O.OPP) continue; // an attack, not a spread
			frontier.add(p);
			bfses[p.offset].calculate(work, p, tt);
		}
		if (frontier.isEmpty()) {
			return Collections.emptyList();
		}
		
		
		
		List<Pos> myCells = new ArrayList<>();
		List<Pos> myUnits = new ArrayList<>();
		for (int i=0;i<ilot.posFE;i++) {
			Pos unit = ilot.pos[i];
			if (work.owner[unit.offset] != O.ME) continue;
			
			myCells.add(unit);
			if (work.movableUnits[unit.offset] > 0 ) myUnits.add(unit);
		}		
		
		
		List<Pos> visitedCells = new ArrayList<>();

		int servedFrontier[] = new int[Pos.MAX_OFFSET];
		boolean hasMove = true;
		while (hasMove) {
			hasMove = false;

			// TODO danger of cell to attract ?
			Collections.sort(frontier, (f1, f2) -> Double.compare(
					distanceToUnitsOrSpawn(work, f1, myUnits, myCells, work.myMatter >= O.UNIT_COST) + (servedFrontier[f1.offset] * 1000), 
					distanceToUnitsOrSpawn(work, f2, myUnits, myCells, work.myMatter >= O.UNIT_COST) + (servedFrontier[f2.offset] * 1000)));

			Pos f = frontier.get(0);
			servedFrontier[f.offset]++;
			
			Pos closestPos = closestPos(work, f, myUnits, myCells, work.myMatter >= O.UNIT_COST);
			if (closestPos == null) continue;
			
			if (work.movableUnits[closestPos.offset] > 0) {
				Pos nextPos = navigator.navigate(work, closestPos, f, tt, visitedCells, territory.frontier);
				if (nextPos != null) {
					visitedCells.add(nextPos);
					if (closestPos == nextPos) {
						work.movableUnits[closestPos.offset]--;
						hasMove = true;
					} else {
						Action move = Action.move(1, closestPos, nextPos, f, Spreader.class.getSimpleName());
						actions.add(move);
						work.apply(move);
						hasMove = true;
					}
				}
			} else if (work.myMatter >= O.UNIT_COST) {
				Action spawn = Action.spawn(1, closestPos, f, Spreader.class.getSimpleName());
				actions.add(spawn);
				work.apply(spawn);
				hasMove = true;
			}
		}
		return actions;
	}


	
	private Pos closestPos(State work, Pos cell, List<Pos> myUnits, List<Pos> myCells, boolean canSpawn) {

		BFS bfs = bfses[cell.offset];
		
		double best = Double.POSITIVE_INFINITY;
		Pos bestPos = null;
		
		for (Pos p : myUnits) {
			if (work.movableUnits[p.offset] == 0) continue;
			
			double dist = bfs.distances[p.offset];
			if (dist < best) {
				best = dist;
				bestPos = p;
			}
		}

		if (canSpawn) {
			for (Pos p : myCells) {
				double dist = bfs.distances[p.offset];
				if (dist ==0 ) {
					dist += 1.1;
				} else {
					dist += 1.1;
				}
				
				if (dist < best) {
					best = dist;
					bestPos = p;
				}
			}
		}
		
		return bestPos;
	}




	private double distanceToUnitsOrSpawn(State work, Pos cell, List<Pos> myUnits, List<Pos> myCells, boolean canSpawn) {
		BFS bfs = bfses[cell.offset];
		
		double best = Double.POSITIVE_INFINITY;
		Pos bestPos = null;
		
		for (Pos p : myUnits) {
			if (work.movableUnits[p.offset] == 0) continue;
			
			double dist = bfs.distances[p.offset];
			if (dist < best) {
				best = dist;
				bestPos = p;
			}
		}

		if (canSpawn) {
			for (Pos p : myCells) {
				double dist = bfs.distances[p.offset];
				if (dist ==0 ) {
					dist += 1.1;
				} else {
					dist += 1.1;
				}
				
				if (dist < best) {
					best = dist;
					bestPos = p;
				}
			}
		}
		
		return best;
	}




	private int distanceToFrontier(Pos u1, List<Pos> frontier) {
		BFS bfs = bfses[u1.offset];
		
		int min = Integer.MAX_VALUE;
		
		for (Pos f : frontier) {
			min = Math.min(min, bfs.distances[f.offset]);
		}
		return min;
	}




	public List<Action> think(State state, List<Action> aDactions, List<Ilot> ilots, Set<Pos> forbidenCells,
	    TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		
		for (Ilot ilot : ilots) {
			
			if (ilot.ruler == O.ME) {
				if (ilot.isFullCoverByMe()) {
					// do nothing!
				} else {
					actions.addAll(spreadQuickly(state, ilot, tt));
				}
			} else if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
				// nothing to do anymore :(
			} else  {
				actions.addAll(think(state, actions, ilot, tt));
			}
		}
		
		
		return actions;
	}
	static BFS bfs = new BFS();
	public static List<Action> spreadQuickly(State work, Ilot ilot, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();

		Territory t = new Territory();
		t.calculateTerritories(ilot, tt);
		
		List<Pos> chosen = new ArrayList<>();
		for (int i=0;i<ilot.posFE;i++) {
			Pos unit = ilot.pos[i];
			if (work.owner[unit.offset] != O.ME || work.movableUnits[unit.offset] == 0) continue;
			if (ilot.ruler != O.ME && t.frontier.contains(unit)) continue;
			
			bfs.calculate(work, unit, tt);
			
			int closestDist = Integer.MAX_VALUE;
			Pos closestCell = null;
			for (int j=0;j<ilot.posFE;j++) {
				Pos target = ilot.pos[j];
				int dist;
				if (work.owner[target.offset] == O.NEUTRAL && !chosen.contains(target)) {
					dist = bfs.distances[target.offset];
				} else if (work.owner[target.offset] == O.OPP && work.canMove(target)) {
					dist = bfs.distances[target.offset] -1000;
				} else {
					dist = Integer.MAX_VALUE;
				}
				
				if (dist < closestDist) {
					closestDist = dist;
					closestCell = target;
				}
			}
			if ( closestCell != null) {
				// TODO optimize spread, forbidden cells is not working 
				// chosen.add(closestCell);
				Pos target = navigator.navigate(work, unit, closestCell, tt, Collections.emptyList());
				if (target != null) {
					Action move = Action.move(work.movableUnits[unit.offset], unit, target, closestCell, Spreader.class.getSimpleName());
					actions.add(move);
					work.apply(move);
				}
			}
		}

		
		return actions;
	}

}


class RecyclerFirstTurn {

	public List<Action> think(State work, List<Ilot> originals) {
		if (work.myMatter < O.UNIT_COST)
			return Collections.emptyList();
		List<Action> toBuild = new ArrayList<>();
		Pos center = getCenter(work);
		if (center == Pos.from(1, 1) || center == Pos.from(State.WIDTH - 2, State.HEIGHT - 2) || center == Pos.from(1, State.HEIGHT - 2) || center == Pos.from(State.WIDTH - 2, 1)) {
			// dont build on corners, never
			return toBuild;
		}
		Action build = Action.build(center, this.getClass().getSimpleName());
		// vérifier qu'on ne fait pas d'ilot !
		State temp = new State();
		temp.copyFrom(work);
		temp.apply(build);
		// heureusement qu'on a le temps de calculer tout ca au 1er tour...
		TimeTraveler tt = new TimeTraveler();
		tt.init(temp);
		List<Pos> fc = new ArrayList<>();
		fc.addAll(tt.forbidenCells());
		boolean cannotBuild = false;
		for (Pos n : center.neighbors4dirs) {
			if (fc.contains(n)) {
				cannotBuild = true;
				break;
			}
		}
		if (cannotBuild)
			return Collections.emptyList();
		// now check if we create ilot in time
		State at10 = tt.sliceAt(10).state;
		List<Ilot> ilots = Ilot.build(at10);
		if (ilots.size() != originals.size()) {
			return Collections.emptyList();
		}
		// ok, no forbidden cells
		toBuild.add(build);
		work.apply(build);
		return toBuild;
	}

	private Pos getCenter(State state) {
		for (Pos p : Pos.allMapPositions) {
			if (state.canBuild(p))
				return p;
		}
		return null;
	}
}

class RecyclerMatterUpgrader {

	public List<Action> think(State work, List<Ilot> originalIlots, TimeTraveler tt) {
		if (work.myMatter < O.UNIT_COST)
			return Collections.emptyList();
		List<Action> toBuild = new ArrayList<>();
		if (State.turn < 0) {
			toBuild.addAll(lowRecyclers(work, originalIlots));
		}
		int myUnitCount = 0;
		int oppUnitCount = 0;
		boolean hasDisputed = false;
		for (Ilot ilot : originalIlots) {
			if (ilot.isDisputed()) {
				hasDisputed = true;
				myUnitCount += ilot.myTroupsCount;
				oppUnitCount += ilot.oppTroupsCount;
			}
		}
		int myTotalUnitInTime = myUnitCount + tt.myTotalMatterBonus / O.UNIT_COST;
		int oppTotalUnitInTime = oppUnitCount + tt.oppTotalMatterBonus / O.UNIT_COST;
		if (!hasDisputed || myTotalUnitInTime >= oppTotalUnitInTime) {
			return toBuild;
		}
		// hmm he will have more units than me !
		Logger.warning(Player.DEBUG_LOGIC, "I << on units in time : " + myTotalUnitInTime + " vs " + oppTotalUnitInTime);
		toBuild.addAll(buildSomeMatter(work));
		return toBuild;
	}

	private List<Action> buildSomeMatter(State work) {
		List<Action> toBuild = new ArrayList<>();
		State temp = new State();
		temp.copyFrom(work);
		for (int turn = 0; turn < 10; turn++) Sim.oneTurn(temp);
		int myOriginalMatter = temp.myMatter;
		List<Ilot> originalIlots = Ilot.build(temp);
		int targetIlotCount = originalIlots.size();
		int myOriginalPotentialCells = Ilot.myPotentialCells(originalIlots);
		int oppOriginalPotentialCells = Ilot.oppPotentialCells(originalIlots);
		int myOriginalUnitCount = 0;
		for (Ilot ilot : originalIlots) {
			myOriginalUnitCount += ilot.myTroupsCount;
		}
		double bestScore = Double.NEGATIVE_INFINITY;
		Pos bestPos = null;
		// TODO build more than one recycler ? 
		for (Pos c : Pos.allMapPositions) {
			if (work.originalUnits[c.offset] != 0 || work.owner[c.offset] != O.ME || work.recycler[c.offset] != 0)
				continue;
			temp.copyFrom(work);
			temp.recycler[c.offset] = 1;
			temp.owner[c.offset] = O.ME;
			for (int turn = 0; turn < 10; turn++) Sim.oneTurn(temp);
			List<Ilot> ilots = Ilot.build(temp);
			double score = 0.0;
			int myUnitAround = 0;
			for (Pos n : c.neighbors4dirs) {
				if (work.owner[n.offset] == O.ME)
					myUnitAround += work.units[n.offset];
			}
			if (myUnitAround > 0) {
				// try not to block units, the dirty way
				score -= 10_000 * myUnitAround;
			}
			int myPotentialCells = Ilot.myPotentialCells(ilots);
			int oppPotentialCells = Ilot.oppPotentialCells(ilots);
			if (myPotentialCells < myOriginalPotentialCells - 5) {
				// not worth it
				score = Double.NEGATIVE_INFINITY;
			}
			score -= 10 * (myOriginalPotentialCells - myPotentialCells);
			// how many scraps recycled after 10 turns
			score += (temp.myMatter - myOriginalMatter);
			if (temp.myMatter - myOriginalMatter <= 10) {
				// not worth it
				score = Double.NEGATIVE_INFINITY;
			}
			if (score > bestScore) {
				bestScore = score;
				bestPos = c;
			}
		}
		if (bestPos != null && work.myMatter >= O.UNIT_COST) {
			Action action = Action.build(bestPos, this.getClass().getSimpleName());
			toBuild.add(action);
			work.apply(action);
		}
		return toBuild;
	}

	private List<Action> lowRecyclers(State work, List<Ilot> originalIlot) {
		if (work.myMatter < O.UNIT_COST)
			return Collections.emptyList();
		List<Action> toBuild = new ArrayList<>();
		// pour toutes mes cases libres, poser un recycler si les 4 voisins sont en dessous en scraps
		for (Pos p : Pos.allMapPositions) {
			if (work.myMatter < O.UNIT_COST)
				break;
			if (work.originalUnits[p.offset] != 0 || work.owner[p.offset] != O.ME)
				continue;
			// TODO check for ilot creation ?
			boolean lower = true;
			int canHarvest = work.scraps[p.offset];
			for (Pos n : p.neighbors4dirs) {
				// already empty
				if (work.scraps[n.offset] == 0)
					continue;
				// already a recycler
				if (work.recycler[n.offset] == 1)
					continue;
				if (work.scraps[n.offset] <= work.scraps[p.offset]) {
					lower = false;
					canHarvest += Math.min(work.scraps[n.offset], work.scraps[p.offset]);
				}
			}
			if (lower && canHarvest > 10) {
				// build here
				Action action = Action.build(p, this.getClass().getSimpleName());
				toBuild.add(action);
				work.apply(action);
			}
		}
		return toBuild;
	}

	private boolean needMoreCells(List<Ilot> originalIlots) {
		int myCells = 0;
		int hisTheoricalCells = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.ME) {
				myCells += ilot.posFE;
			} else if (ilot.ruler == O.NEUTRAL) {
			} else {
				hisTheoricalCells += ilot.posFE;
			}
		}
		return hisTheoricalCells >= myCells;
	}

	private boolean hasDisputedIlot(List<Ilot> originalIlots) {
		boolean disputed = false;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == Ilot.DISPUTED) {
				disputed = true;
				break;
			}
		}
		return disputed;
	}
}

/**
 * Build recyclers to defend ourselves
 *
 */
/**
 * Build recyclers to defend ourselves
 *
 */
class RecyclersDefense {

	public List<Action> think(State work, List<Ilot> originalIlots) {
		List<Action> toBuild = new ArrayList<>();
		Pos bestRecyclerPos = createDefensiveIlotsHeuristic(work);
		if (work.myMatter >= O.UNIT_COST && bestRecyclerPos != null) {
			Action build = Action.build(bestRecyclerPos, this.getClass().getSimpleName());
			toBuild.add(build);
			work.apply(build);
		}
		return toBuild;
	}

	private Pos createDefensiveIlotsHeuristic(State state) {
		// simulate recycler to find a situation with an owned ilot
		State work = new State();
		// the score to beat !
		work.copyFrom(state);
		int bestMine = scoreIlots(state, work);
		Pos bestRecycler = null;
		for (Pos c : Pos.allMapPositions) {
			if (state.units[c.offset] != 0 || state.owner[c.offset] != O.ME || state.recycler[c.offset] != 0)
				continue;
			// check if under attack !
			boolean danger = false;
			for (Pos n : c.neighbors4dirs) {
				if (state.owner[n.offset] == O.OPP && state.units[n.offset] > 0) {
					danger = true;
					break;
				}
			}
			if (!danger)
				continue;
			work.copyFrom(state);
			work.recycler[c.offset] = 1;
			work.owner[c.offset] = O.ME;
			int base = state.owner[c.offset] == O.ME ? 1 : 0;
			for (Pos p : c.neighbors4dirs) {
				base += state.owner[p.offset] == O.ME ? 1 : 0;
			}
			int score = scoreIlots(state, work) - base;
			if (score > bestMine) {
				bestMine = score;
				bestRecycler = c;
			}
		}
		return bestRecycler;
	}

	private int scoreIlots(State original, State work) {
		for (int turn = 0; turn < 10; turn++) Sim.oneTurn(work);
		List<Ilot> ilots = Ilot.build(work);
		int mine = 0;
		for (Ilot i : ilots) {
			// count the number of cells in each ilot belonging to me
			if (i.ruler == O.ME)
				mine += (i.posFE - i.nbRecyclers);
		}
		return mine;
	}
}

class RecyclerBuilder {

	List<Action> toBuild = new ArrayList<>();

	private List<Ilot> originalIlots;

	public List<Action> think(State work, List<Ilot> originalIlots, TimeTraveler tt) {
		this.originalIlots = originalIlots;
		toBuild.clear();
		toBuild.addAll(recyclerDefense(work));
		if (State.turn == 0) {
			toBuild.addAll(new RecyclerFirstTurn().think(work, originalIlots));
		} else {
			toBuild.addAll(new RecyclersDefense().think(work, originalIlots));
			toBuild.addAll(new RecyclerMatterUpgrader().think(work, originalIlots, tt));
		}
		return toBuild;
	}

	private List<Action> recyclerDefense(State work) {
		if (work.myMatter < O.UNIT_COST)
			return Collections.emptyList();
		List<Action> toDefend = new ArrayList<>();
		for (Ilot ilot : originalIlots) {
			// pas la peine de perdre du temps
			if (!ilot.isDisputed())
				continue;
			for (int i = 0; i < ilot.posFE; i++) {
				Pos p = ilot.pos[i];
				// already one
				if (work.recycler[p.offset] != 0)
					continue;
				if (work.owner[p.offset] == O.ME && work.units[p.offset] == 0) {
					boolean danger = false;
					for (Pos n : p.neighbors4dirs) {
						if (work.owner[n.offset] == O.OPP && work.units[n.offset] > 0) {
							danger = true;
						}
					}
					// build a recycler !
					if (work.myMatter >= O.UNIT_COST && work.originalUnits[p.offset] == 0) {
						if (danger) {
							Action build = Action.build(p, this.getClass().getSimpleName());
							toDefend.add(build);
							work.apply(build);
						}
					}
				}
			}
		}
		return toDefend;
	}
}

class UnitSpawner {

	public List<Action> think(State work, List<Ilot> ilots, Set<Pos> forbidenCells, DiffusionMap myUnitsMap, DiffusionMap ennemyUnitsMap, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		// Spawn some units
		double bestSpawnScore = Double.NEGATIVE_INFINITY;
		Pos bestSpawnPos = null;
		for (Ilot ilot : ilots) {
			if (ilot.ruler != Ilot.DISPUTED)
				continue;
			// try to protect cells in danger
			Territory t = new Territory();
			t.calculateTerritories(ilot, tt);
			for (Pos p : t.baston) {
				if (forbidenCells.contains(p))
					continue;
				if (work.owner[p.offset] == O.ME && work.canSpawn(p)) {
					int count = 0;
					for (Pos n : p.neighbors4dirs) {
						if (work.owner[n.offset] == O.OPP) {
							count += work.units[n.offset];
						}
					}
					int amount = Math.min(work.myMatter / O.UNIT_COST, count);
					if (amount > 0) {
						Action spawn = Action.spawn(amount, p, this.getClass().getSimpleName());
						actions.add(spawn);
						work.apply(spawn);
					}
				}
			}
			for (Pos current : t.frontier) {
				// don't spawn on disapearing cells
				if (forbidenCells.contains(current))
					continue;
				if (work.myMatter >= O.UNIT_COST) {
					double score = 0.0;
					// big coeff
					score += 1000.0 * ennemyUnitsMap.grid[current.offset];
					score -= 1000.0 * myUnitsMap.grid[current.offset];
					double positionalScore = 0.0;
					for (Pos d : current.neighbors4dirs) {
						if (!work.canMove(d))
							continue;
						if (work.owner[d.offset] == O.OPP)
							positionalScore += 0.1;
						if (work.owner[d.offset] == O.NEUTRAL)
							positionalScore += 0.5;
					}
					score += positionalScore;
					if (score > bestSpawnScore) {
						bestSpawnScore = score;
						bestSpawnPos = current;
					}
				}
			}
		}
		if (bestSpawnPos != null) {
			BFS bfs = new BFS();
			bfs.calculate(work, bestSpawnPos, tt);
			Pos reinforcementTarget = bfs.findClosest( c -> work.canMove(c) && work.owner[c.offset] == O.ME);
			if (reinforcementTarget != null) {
				int maxSpawn = work.myMatter / O.UNIT_COST;
				int amount = maxSpawn;
				if (amount > 0) {
					Action spawn = Action.spawn(amount, reinforcementTarget, this.getClass().getSimpleName());
					actions.add(spawn);
					work.apply(spawn);
				}
			}
		} else {
			actions.addAll(spawnFillers(work, ilots));
		}
		return actions;
	}

	private List<Action> spawnFillers(State state, List<Ilot> ilots) {
		List<Action> actions = new ArrayList<>();
		// check for owned ilot but not completly that have 0 units
		for (Ilot ilot : ilots) {
			if (ilot.ruler != O.ME)
				continue;
			if (ilot.isFullCoverByMe)
				continue;
			// TODO pouvoir spawn quand meme pour accelerer la couverture si on est en fin de partie ?
			if (ilot.myTroupsCount > 0)
				continue;
			for (int i = 0; i < ilot.posFE; i++) {
				Pos current = ilot.pos[i];
				if (state.myMatter < O.UNIT_COST || state.owner[current.offset] != O.ME || state.recycler[current.offset] != 0)
					continue;
				for (Pos n : current.neighbors4dirs) {
					if (state.owner[n.offset] == O.NEUTRAL) {
						actions.add(Action.spawn(1, current, this.getClass().getSimpleName()));
						state.myMatter -= O.UNIT_COST;
						break;
					}
				}
			}
		}
		return actions;
	}
}

class WinGameHeuristic {

	/**
	 * Find if there is conditions when we win the game !
	 * 
	 * 1. poser un recycler qui bloque tout et qui me laisse plus de cellule potentielle que lui
	 * @param territory 
	 * @param tt 
	 * 
	 * @return
	 */
	/**
	 * Find if there is conditions when we win the game !
	 * 
	 * 1. poser un recycler qui bloque tout et qui me laisse plus de cellule potentielle que lui
	 * @param territory 
	 * @param tt 
	 * 
	 * @return
	 */
	public List<Action> think(State work, List<Ilot> ilots, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		List<Action> moreIlotsThanHim = moreIlotsThanHim(work, ilots);
		actions.addAll(moreIlotsThanHim);
		List<Action> frontierOfRecyclers = frontierOfRecyclers(work, ilots, tt);
		actions.addAll(frontierOfRecyclers);
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME && ilot.isFullCoverByMe()) {
			// do nothing!
			}
			if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
			// nothing to do anymore :(
			} else {
				Territory territory = new Territory();
				territory.calculateTerritories(ilot, tt);
				List<Action> winByRecyclers = winByRecyclers(work, territory, tt);
				if (!winByRecyclers.isEmpty()) {
					actions.addAll(winByRecyclers);
				}
			}
		}
		return actions;
	}

	/* if we can build recycler on the frontier and i win the game, do it ! 
	 */
	private List<Action> frontierOfRecyclers(State work, List<Ilot> ilots, TimeTraveler tt) {
		boolean canFrontierize = true;
		Set<Pos> toBuild = new HashSet<>();
		for (Ilot ilot : ilots) {
			if (!ilot.isDisputed())
				continue;
			Territory t = new Territory();
			t.calculateTerritories(ilot, tt);
			int count = 0;
			for (Pos f : t.frontier) {
				if (work.canBuild(f)) {
					toBuild.add(f);
					count++;
				}
			}
			if (count != t.frontier.size()) {
				canFrontierize = false;
			}
		}
		if (canFrontierize && toBuild.size() * O.UNIT_COST <= work.myMatter) {
			State temp = new State();
			temp.copyFrom(work);
			for (Pos b : toBuild) {
				temp.recycler[b.offset] = 1;
			}
			for (int i = 0; i < 10; i++) Sim.oneTurn(temp);
			List<Ilot> newIlots = Ilot.build(temp);
			if (Ilot.myPotentialCells(newIlots) > Ilot.oppPotentialCells(newIlots)) {
				Logger.info(Player.DEBUG_LOGIC, "I can close the frontier with " + toBuild);
				List<Action> actions = new ArrayList<>();
				for (Pos b : toBuild) {
					Action build = Action.build(b, this.getClass().getSimpleName());
					work.apply(build);
					actions.add(build);
				}
				return actions;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Force Spawn an unit
	 *	
	 * @param work
	 * @param ilots
	 * @return
	 */
	/**
	 * Force Spawn an unit
	 *	
	 * @param work
	 * @param ilots
	 * @return
	 */
	private List<Action> moreIlotsThanHim(State work, List<Ilot> ilots) {
		if (work.myMatter < O.UNIT_COST)
			return Collections.emptyList();
		int mySureCells = 0;
		int oppPotentialCells = 0;
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME)
				mySureCells += ilot.size();
			if (ilot.ruler == O.OPP || ilot.ruler == Ilot.DISPUTED)
				oppPotentialCells += (ilot.size() - ilot.nbRecyclers);
		}
		Logger.info(Player.DEBUG_LOGIC, "Cells : " + mySureCells + " vs " + oppPotentialCells);
		if (mySureCells > oppPotentialCells) {
			// force spawn of an unit on my empyty ilots
			for (Ilot ilot : ilots) {
				if (ilot.ruler == O.ME && ilot.myTroupsCount == 0) {
					Logger.info(Player.DEBUG_LOGIC, "Checking ilot : " + ilot);
					for (int i = 0; i < ilot.posFE; i++) {
						Pos p = ilot.pos[i];
						if (work.canSpawn(p)) {
							Logger.info(Player.DEBUG_LOGIC, "Can spawn on " + p);
							// spawn everything to prevents mistakes with recyclers :)
							Action spawn = Action.spawn(work.myMatter / O.UNIT_COST, p, this.getClass().getSimpleName());
							work.apply(spawn);
							return Arrays.asList(spawn);
						}
					}
				}
			}
		}
		return Collections.emptyList();
	}

	Pos[] positions = new Pos[Pos.MAX_OFFSET];

	int posFE = 0;

	private List<Action> winByRecyclers(State original, Territory territoryO, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		posFE = 0;
		for (Pos p : Pos.allMapPositions) {
			// TODO lol, ce sont toujours les memes
			positions[posFE++] = p;
		}
		Set<Pos> visited = new HashSet<>();
		for (Pos f : territoryO.frontier) {
			for (Pos p : f.meAndNeighbors4dirs) {
				if (visited.contains(p))
					continue;
				visited.add(p);
				if (!original.canBuild(p))
					continue;
				State state = new State();
				state.copyFrom(original);
				Action build = Action.build(p, this.getClass().getSimpleName());
				state.apply(build);
				Sim.tenTurn(state);
				List<Ilot> ilots = Ilot.build(state);
				int mines = 0;
				int opps = 0;
				for (Ilot ilot : ilots) {
					if (ilot.ruler == O.ME)
						mines += ilot.size();
					if (ilot.ruler == O.OPP || ilot.isDisputed())
						opps += ilot.size();
				}
				// compter combien je kill de cells
				int deletedCells = 0;
				for (Pos n : p.neighbors4dirs) {
					if (original.owner[n.offset] == O.ME && original.scraps[n.offset] <= original.scraps[p.offset])
						deletedCells++;
				}
				if (mines - deletedCells > opps) /* nombre de case que je pourrais perdre :/ */
				{
					System.err.println("Winning !");
					actions.add(build);
					return actions;
				}
			}
		}
		return actions;
	}
}

class MM2DEntry {

	int blocked;

	int movable;

	int reinforcement;

	public int total() {
		return blocked + movable + reinforcement;
	}

	public static MM2DEntry blocked(int b) {
		MM2DEntry mm2dEntry = new MM2DEntry();
		mm2dEntry.blocked = b;
		return mm2dEntry;
	}

	public MM2DEntry movable(int m) {
		this.movable = m;
		return this;
	}

	public MM2DEntry reinforcement(int r) {
		this.reinforcement = r;
		return this;
	}
}

class MM2DNeutralResult {

	public int attack;

	public int free;

	public boolean won = false;

	public MM2DNeutralResult(int a, int f, boolean won) {
		this.attack = a;
		this.free = f;
		this.won = won;
	}

	@Override
	public String toString() {
		return String.format("( k=%d, a=%d, won=%b) ", attack, free, won);
	}

	public static MM2DNeutralResult of(int k, int a, boolean won) {
		return new MM2DNeutralResult(k, a, won);
	}

	@Override
	public int hashCode() {
		return Objects.hash(free, attack);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MM2DNeutralResult other = (MM2DNeutralResult) obj;
		return free == other.free && attack == other.attack && won == other.won;
	}

	public static MM2DNeutralResult attack(int a) {
		return new MM2DNeutralResult(a, 0, false);
	}

	public MM2DNeutralResult free(int a) {
		return new MM2DNeutralResult(this.attack, a, this.won);
	}

	public MM2DNeutralResult withWon() {
		return new MM2DNeutralResult(this.attack, this.free, true);
	}

	public void setWon() {
		this.won = true;
	}
}

class MM2DResult {

	public int keep;

	public int free;

	public int reinforcement;

	public boolean won = false;

	public MM2DResult(int k, int f, int r, boolean won) {
		this.keep = k;
		this.free = f;
		this.reinforcement = r;
		this.won = won;
	}

	@Override
	public String toString() {
		return String.format("( k=%d, a=%d, r=%d, won=%b) ", keep, free, reinforcement, won);
	}

	public static MM2DResult of(int k, int a, int r, boolean won) {
		return new MM2DResult(k, a, r, won);
	}

	@Override
	public int hashCode() {
		return Objects.hash(free, keep, reinforcement);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MM2DResult other = (MM2DResult) obj;
		return free == other.free && keep == other.keep && reinforcement == other.reinforcement && won == other.won;
	}

	public static MM2DResult keep(int k) {
		return new MM2DResult(k, 0, 0, false);
	}

	public MM2DResult free(int a) {
		return new MM2DResult(this.keep, a, this.reinforcement, this.won);
	}

	public MM2DResult reinforce(int r) {
		return new MM2DResult(this.keep, this.free, r, this.won);
	}

	public MM2DResult withWon() {
		return new MM2DResult(this.keep, this.free, this.reinforcement, true);
	}

	public void setWon() {
		this.won = true;
	}
}

/** 
 * 
 * resolve a fight between 2 neighbors cells
 * 
 * @author nmahoude
 *
 */
/** 
 * 
 * resolve a fight between 2 neighbors cells
 * 
 * @author nmahoude
 *
 */
class MM2DFightDecider {

	public List<MM2DResult> resolve(MM2DEntry me, MM2DEntry opp) {
		List<MM2DResult> results = new ArrayList<>();
		int defense = me.total() - opp.movable;
		if (defense < 0) {
			// no way to defend
			return results;
		}
		int free;
		int maxWithNotWon = -1;
		for (free = 0; free <= me.movable; free++) {
			// check we still can defend
			if (me.total() - free < opp.movable)
				break;
			// sur de gagner !
			boolean won = free > opp.total();
			if (!won)
				maxWithNotWon = Math.max(maxWithNotWon, free);
			if (won) {
				results.add(new MM2DResult(me.movable - free, free, Math.max(0, opp.movable - (me.blocked + me.movable - free)), true));
			}
		}
		if (maxWithNotWon != -1) {
			results.add(new MM2DResult(me.movable - maxWithNotWon, maxWithNotWon, Math.max(0, opp.movable - (me.blocked + me.movable - maxWithNotWon)), false));
		}
		return results;
	}

	public List<MM2DNeutralResult> resolveNeutral(MM2DEntry me, MM2DEntry opp) {
		List<MM2DNeutralResult> results = new ArrayList<>();
		if (me.movable > opp.movable) {
			int neededToWin = opp.movable + 1;
			results.add(new MM2DNeutralResult(neededToWin, me.movable - neededToWin, true));
		} else if (me.movable == opp.movable) {
			results.add(new MM2DNeutralResult(me.movable, 0, false));
		} else if (me.movable < opp.movable) {
			results.add(new MM2DNeutralResult(0, me.movable, false));
		}
		return results;
	}

	/*
	 * Objective is to catch the opp cell without losing ours
	 */
	public List<MM2DResult> resolve(int[] me, int[] opp) {
		List<MM2DResult> results = new ArrayList<>();
		int defense = (me[0] + me[1]) - opp[0];
		if (defense < 0) {
			// no way to defend
			return results;
		}
		// here we can defend, now can we attack with 'i' units (only with m[0]) ?
		int maxWithNotWon = -1;
		for (int i = 0; i <= me[0]; i++) {
			if (opp[0] - (me[0] + me[1] - i) > 0)
				break;
			// sur de gagner !
			boolean won = i > opp[0] + opp[1];
			if (!won)
				maxWithNotWon = Math.max(maxWithNotWon, i);
			if (won) {
				MM2DResult result = new MM2DResult(me[0] - i, i, Math.max(0, opp[0] - (me[0] - i)), true);
				results.add(result);
			}
			// no more needed
			if (won)
				break;
		}
		if (maxWithNotWon != -1) {
			MM2DResult result = new MM2DResult(me[0] - maxWithNotWon, maxWithNotWon, Math.max(0, opp[0] - (me[0] - maxWithNotWon)), false);
			results.add(result);
		}
		return results;
	}

	public List<MM2DResult> resolve3D(int[] me, int[] opp) {
		List<MM2DResult> results = new ArrayList<>();
		int defense = (me[0] + me[1]) - opp[0];
		if (defense < 0) {
			// no way to defend
			return results;
		}
		// here we can defend, now can we attack (only with m[0] ?
		int maxWithNotWon = -1;
		for (int i = 0; i <= me[1]; i++) {
			if (opp[0] - (me[1] - i) > 0)
				break;
			boolean won = i > opp[0] + opp[1];
			if (!won)
				maxWithNotWon = Math.max(maxWithNotWon, i);
			if (won) {
				MM2DResult result = new MM2DResult(me[1] - i, 0, i, true);
				results.add(result);
			}
			// no more needed
			if (won)
				break;
		}
		if (maxWithNotWon != -1) {
			MM2DResult result = new MM2DResult(me[0] - maxWithNotWon, maxWithNotWon, Math.max(0, opp[0] - (me[0] - maxWithNotWon)), false);
			results.add(result);
		}
		return results;
	}
}

class InDefenseOfDanger {

	private TimeTraveler tt;

	private Navigator navigator = new Navigator();

	boolean needBoostInMatter = false;

	boolean defense;

	public List<Action> think(State work, List<Ilot> ilots, Set<Pos> forbidenCells, TimeTraveler tt, boolean defense, DiffusionMap myMap, DiffusionMap oppMap) {
		this.tt = tt;
		this.defense = defense;
		List<Action> moves = new ArrayList<>();
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME && ilot.isFullCoverByMe()) {
			// do nothing!
			}
			if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
			// nothing to do anymore :(
			} else {
				moves.addAll(defense(work, ilot, forbidenCells, myMap, oppMap));
			}
		}
		return moves;
	}

	private List<Action> defense(State work, Ilot ilot, Set<Pos> forbidenCells, DiffusionMap myMap, DiffusionMap oppMap) {
		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);
		this.needBoostInMatter = false;
		if (tt.myTotalMatterBonus < tt.oppTotalMatterBonus || ilot.myTroupsCount < ilot.oppTroupsCount) {
			needBoostInMatter = true;
		}
		List<Action> actions = new ArrayList<>();
		List<Pos> posToCheck = new ArrayList<>();
		for (Pos unit : territory.frontier) {
			posToCheck.add(unit);
		}
		// sort units per importance
		Collections.sort(posToCheck, ( u1,	u2) -> Double.compare(territory.blueDangers[u2.offset], territory.blueDangers[u1.offset]));
		for (Pos cell : posToCheck) {
			// rien à défendre, c'est un trou
			if (!tt.sliceAt(1).state.canMove(cell))
				continue;
			if (territory.blueDangers[cell.offset] > 0) {
				int maxOppUnits = 0;
				int myReinforcement = 0;
				Pos target = null;
				for (Pos n : cell.neighbors4dirs) {
					if (work.owner[n.offset] == O.OPP) {
						maxOppUnits += work.units[n.offset];
						// TODO ne pas choisir au hazard ...
						if (work.canMove(n))
							target = n;
					}
					if (work.owner[n.offset] == O.ME) {
						myReinforcement += work.movableUnits[n.offset];
					}
				}
				int myUnitsCount = work.movableUnits[cell.offset];
				maxOppUnits = Math.max(0, maxOppUnits - (work.units[cell.offset] - work.movableUnits[cell.offset]));
				// TODO take possible reinforcement by move in account
				int mySpawn = work.owner[cell.offset] == O.ME ? work.myMatter / O.UNIT_COST : 0;
				int oppSpawn = target != null && work.owner[target.offset] == O.OPP ? work.oppMatter / O.UNIT_COST : 0;
				List<MM2DResult> results;
				if (work.owner[cell.offset] == O.ME) {
					results = new MM2DFightDecider().resolve(new int[] { myUnitsCount, myReinforcement + mySpawn }, new int[] { maxOppUnits, oppSpawn });
				} else {
					if (work.owner[cell.offset] == O.NEUTRAL) {
						if (oppMap.grid[cell.offset] >= myMap.grid[cell.offset]) {
							// find the closest owned cell & ask for help (spawn)
							BFS bfs = new BFS();
							bfs.calculate(work, cell, tt);
							Pos reinforcementByMoveTarget = bfs.findClosest( c -> work.owner[c.offset] == O.ME && work.movableUnits[c.offset] > 0);
							Pos reinforcementBySpawn = work.myMatter >= O.UNIT_COST ? bfs.findClosest( c -> work.canMove(c) && work.owner[c.offset] == O.ME) : // can't spawn
							null;
							if (reinforcementByMoveTarget == null && reinforcementBySpawn == null) {
								results = new MM2DFightDecider().resolve3D(new int[] { 0, myReinforcement }, new int[] { maxOppUnits, 0 });
							} else if (reinforcementByMoveTarget != null && reinforcementBySpawn != null) {
								// the closest (minus 1 for spawn)
								if (bfs.distances[reinforcementByMoveTarget.offset] <= bfs.distances[reinforcementBySpawn.offset] + 1) {
									// move
									Pos nextPos = navigator.navigate(work, reinforcementByMoveTarget, cell, tt, territory.frontier);
									if (nextPos != null) {
										Action move = Action.move(1, reinforcementByMoveTarget, nextPos, cell, this.getClass().getSimpleName());
										work.apply(move);
										actions.add(move);
										results = Collections.emptyList();
									} else {
										results = new MM2DFightDecider().resolve3D(new int[] { 0, myReinforcement }, new int[] { maxOppUnits, 0 });
									}
								} else {
									// spawn
									Action spawn = Action.spawn(1, reinforcementBySpawn, this.getClass().getSimpleName());
									work.apply(spawn);
									actions.add(spawn);
									results = Collections.emptyList();
								}
							} else if (reinforcementByMoveTarget != null) {
								// move
								Pos nextPos = navigator.navigate(work, reinforcementByMoveTarget, cell, tt, territory.frontier);
								if (nextPos != null) {
									Action move = Action.move(1, reinforcementByMoveTarget, nextPos, cell, this.getClass().getSimpleName());
									work.apply(move);
									actions.add(move);
									results = Collections.emptyList();
								} else {
									results = new MM2DFightDecider().resolve3D(new int[] { 0, myReinforcement }, new int[] { maxOppUnits, 0 });
								}
							} else {
								// spawn
								Action spawn = Action.spawn(1, reinforcementBySpawn, this.getClass().getSimpleName());
								work.apply(spawn);
								actions.add(spawn);
								results = Collections.emptyList();
							}
						} else {
							results = new MM2DFightDecider().resolve3D(new int[] { 0, myReinforcement }, new int[] { maxOppUnits, 0 });
						}
					} else {
						results = new MM2DFightDecider().resolve3D(new int[] { 0, myReinforcement }, new int[] { maxOppUnits, 0 });
					}
				}
				if (Player.DEBUG_LOGIC) {
					Logger.info("MM2D Results : ");
					for (MM2DResult result : results) {
						Logger.info("	 MM2D Result : ");
					}
				}
				if (results.size() == 0) {
					if (work.units[cell.offset] == 0 && work.owner[cell.offset] == O.ME && work.myMatter >= O.UNIT_COST) {
						Action blocker = Action.build(cell, this.getClass().getSimpleName());
						work.apply(blocker);
						actions.add(blocker);
					}
				} else if (results.size() == 2) {
					// on peut attaquer et gagner !
					// TODO on peut attaquer avec plus si on veut, won ne donne que le minimum tant
					// qu'on conserve 'keep' et qu'on applique reinforcement !
					MM2DResult won = results.get(0);
					// apply defense
					// keep
					work.movableUnits[cell.offset] -= won.keep;
					// reinforcements
					if (won.reinforcement > 0) {
						spawnAndMove(work, territory, actions, cell, myReinforcement, won, tt);
					}
					if (won.free > 0) {
						Action move = Action.move(won.free, cell, target, this.getClass().getSimpleName());
						actions.add(move);
						work.apply(move);
					}
				} else {
					// on peut défendre, en attaquant
					// TODO on peut aussi garder des unités, pas obliger d'attaquer ...
					MM2DResult defense = results.get(0);
					if (false && !defense.won) {
						// on peut rien faire maintenant, mais peut etre qu'on peut spawn pour défendre
						// vu que c'est la frontière !
						// 1. try, spawn one on the closest blue cell, how to force it to come close ?
						Pos closest = null;
						int bestDist = Integer.MAX_VALUE;
						for (Pos p : Pos.allMapPositions) {
							if (!work.canSpawn(p))
								continue;
							int manhattan = p.manhattan(cell);
							if (manhattan < bestDist) {
								closest = p;
								bestDist = manhattan;
							}
						}
						if (closest != null && work.myMatter >= O.UNIT_COST) {
							Action spawn = Action.spawn(1, closest, this.getClass().getSimpleName());
							work.apply(spawn);
							actions.add(spawn);
						}
					// 2. TODO OR attract close enough units
					} else {
						// try defensive move
						if (this.defense && defense.free > 0) {
							defense.reinforcement = Math.max(0, defense.reinforcement - defense.free);
							defense.free = 0;
						}
						// keep
						work.movableUnits[cell.offset] -= defense.keep;
						if (defense.free == 0 && defense.keep == 0 && defense.reinforcement == 0 && work.owner[cell.offset] == O.ME && work.units[cell.offset] == 0 && work.myMatter > O.UNIT_COST) {
							// special case spawn!
							Action spawn = Action.spawn(1, cell, this.getClass().getSimpleName());
							actions.add(spawn);
							work.apply(spawn);
						}
						// reinforcements
						if (defense.reinforcement > 0) {
							spawnAndMove(work, territory, actions, cell, myReinforcement, defense, tt);
						}
						// attack max
						if (!this.defense && defense.free > 0 && target != null) {
							Action move = Action.move(defense.free, cell, target, this.getClass().getSimpleName());
							actions.add(move);
							work.apply(move);
						}
					}
				}
			} else {
			// TODO pourquoi ca aide pas ça ?
			// Check for defense protect
			// if (territory.frontier.contains(unit)) {
			// // oh ! I'm on the frontier, I can't move as I wish
			// // si y'a des red pret de moi, je dois voir si je dois défendre
			// int reds = work.countRedAround(unit);
			// if (reds > 0) {
			// // really need to protect now
			// MM2DFightDecider dd = new MM2DFightDecider();
			// int blockedUnits = work.units[unit.offset] - work.movableUnits[unit.offset];
			// List<MM2DResult> results = dd.resolve(new int[] {
			// work.movableUnits[unit.offset], blockedUnits + work.myMatter / O.UNIT_COST},
			// new int[] { reds, work.oppMatter / O.UNIT_COST});
			// if (results.size() > 0) {
			// MM2DResult defense = results.get(results.size()-1);
			// work.movableUnits[unit.offset] -= defense.keep;
			//
			// // spawn to protect
			// if (defense.reinforcement > blockedUnits) {
			// Action spawn = Action.spawn(defense.reinforcement - blockedUnits, unit);
			// work.apply(spawn);
			// actions.add(spawn);
			// }
			// }
			// }
			// }
			}
		}
		return actions;
	}

	private void spawnAndMove(State work, Territory territory, List<Action> actions, Pos unit, int myPotentialReinforcementWithMoves, MM2DResult result, TimeTraveler tt) {
		int notOnTheFrontierUnits = 0;
		for (Pos n : unit.neighbors4dirs) {
			if (!territory.frontier.contains(n) && work.owner[n.offset] == O.ME) {
				notOnTheFrontierUnits += work.movableUnits[n.offset];
			}
		}
		if (work.originalUnits[unit.offset] == 0 && work.owner[unit.offset] == O.ME && work.myMatter >= O.UNIT_COST) {
			// on peut envisager le recycler !
			if (result.reinforcement > 1) {
				// TODO on pourrait spawn quand meme ?
				Action recycler = Action.build(unit, this.getClass().getSimpleName());
				actions.add(recycler);
				work.apply(recycler);
				return;
			} else {
				// only 1, but it can be more intersting to spawn a recycler if we can't keep up
				if (needBoostInMatter) {
					// he produces more than me, block with recycler
					Action recycler = Action.build(unit, this.getClass().getSimpleName());
					actions.add(recycler);
					work.apply(recycler);
					return;
				}
			}
		}
		int needToMove = Math.min(myPotentialReinforcementWithMoves, result.reinforcement);
		int needToSpawn = result.reinforcement - needToMove;
		// roulette
		while (needToMove > 0) {
			for (Pos n : unit.neighbors4dirs) {
				if (needToMove == 0)
					break;
				if (work.isMine(n) && work.movableUnits[n.offset] > 0) {
					Action move = Action.move(1, n, unit, this.getClass().getSimpleName());
					actions.add(move);
					work.apply(move);
					needToMove--;
				}
			}
		}
		if (needToSpawn > 0) {
			if (needToSpawn > 0) {
				Action spawn = Action.spawn(needToSpawn, unit, this.getClass().getSimpleName());
				actions.add(spawn);
				work.apply(spawn);
			}
		}
	}

	/**
	 * Check if it is ok to build a recycler on pos unit
	 */
	/**
	 * Check if it is ok to build a recycler on pos unit
	 */
	private boolean isOkToBuildRecycler(State work, Pos unit, List<Pos> frontier) {
		// check if neutre & frontier à côté
		boolean blockTheRoad = false;
		for (Pos n : unit.neighbors4dirs) {
			if (work.owner[n.offset] == O.NEUTRAL && frontier.contains(n)) {
				// check if there is another blue not block around this neutral cell ...
				boolean anotherRoad = false;
				for (Pos n2 : n.neighbors4dirs) {
					// don't recheck the one we are proposing a reycler on
					if (n2 == unit)
						continue;
					if (work.owner[n2.offset] == O.ME && work.canMove(n2)) {
						anotherRoad = true;
						break;
					}
				}
				if (!anotherRoad) {
					blockTheRoad = true;
					break;
				}
			}
		}
		if (blockTheRoad) {
			return false;
		} else {
			return true;
		}
	}

	private int distanceToFrontier(Pos u1, List<Pos> frontier) {
		// TODO using manhatten for speed, real distances would be better ...
		int min = Integer.MAX_VALUE;
		for (Pos f : frontier) {
			min = Math.min(min, f.manhattan(u1));
		}
		return min;
	}
}

class AI1 {

	public static final double DIFFUSION_COEF = 0.9;

	private State state = new State();

	public DiffusionMap ennemyUnitsMap = new DiffusionMap(O.OPP, DIFFUSION_COEF);

	public DiffusionMap myUnitsMap = new DiffusionMap(O.ME, DIFFUSION_COEF);

	TimeTraveler tt = new TimeTraveler();

	Sim sim = new Sim();

	private List<Ilot> ilots;

	private Set<Pos> forbidenCells = new HashSet<>();

	public List<Action> think(State originalStateReadOnly) {
		this.state.copyFrom(originalStateReadOnly);
		List<Action> actions = new ArrayList<>();
		long start = System.currentTimeMillis();
		ilots = Ilot.build(state);
		tt.init(state);
		forbidenCells.clear();
		forbidenCells.addAll(tt.forbidenCells());
		ennemyUnitsMap.calculate(state);
		myUnitsMap.calculate(state);
		if (Player.DEBUG_INIT_ILOTS) {
			System.err.println("Precalculated (initial) ilots: ");
			for (Ilot ilot : ilots) {
				System.err.println(ilot);
			}
		}
		//		if (State.turn == 7) {
		//			Action debug = Action.move(1, Pos.from(8,4), Pos.from(9,4), "TEST PathFinding");
		//			state.apply(debug);
		//			actions.add(debug);
		//		}
		List<Action> winActions = new WinGameHeuristic().think(state, ilots, tt);
		actions.addAll(winActions);
		// D'abord en mode defense, ensuite en mode attaque
		actions.addAll(new InDefenseOfDanger().think(state, ilots, forbidenCells, tt, true, myUnitsMap, ennemyUnitsMap));
		actions.addAll(new InDefenseOfDanger().think(state, ilots, forbidenCells, tt, false, myUnitsMap, ennemyUnitsMap));
		Logger.info(Player.DEBUG_PERF, "Time after IDOD " + (System.currentTimeMillis() - start) + " ms");
		List<Action> recyclerToBuild = new RecyclerBuilder().think(state, ilots, tt);
		actions.addAll(recyclerToBuild);
		// recalculate ilots !
		if (!recyclerToBuild.isEmpty()) {
			ilots = Ilot.build(state);
		}
		if (Player.DEBUG_AFTER_RECYCLER_ILOTS) {
			System.err.println("Recalculated ilots (after recyclers): ");
			for (Ilot ilot : ilots) {
				System.err.println(ilot);
			}
		}
		Logger.info(Player.DEBUG_PERF, "Time after Recyclers " + (System.currentTimeMillis() - start) + " ms");
		// now we can reason on a "correct" map
		ennemyUnitsMap.calculate(state);
		myUnitsMap.calculate(state);
		tt.init(state);
		Logger.info(Player.DEBUG_PERF, "Time after Preparation " + (System.currentTimeMillis() - start) + " ms");
		forbidenCells.clear();
		forbidenCells.addAll(tt.forbidenCells());
		Logger.info(Player.DEBUG_PERF, "Time after Forbidden cells " + (System.currentTimeMillis() - start) + " ms");
		List<Action> spawnActions = new UnitSpawner().think(state, ilots, forbidenCells, myUnitsMap, ennemyUnitsMap, tt);
		actions.addAll(spawnActions);
		Logger.info(Player.DEBUG_PERF, "Time after Spawns " + (System.currentTimeMillis() - start) + " ms");
		List<Action> moves = new Spreader().think(state, actions, ilots, forbidenCells, tt);
		actions.addAll(moves);
		Logger.info(Player.DEBUG_PERF, "Time after Moves " + (System.currentTimeMillis() - start) + " ms");
		return actions;
	}
}

class Player {

	public static boolean DEBUG_OUPUT = true;

	public static boolean DEBUG_INIT_ILOTS = false;

	public static boolean DEBUG_AFTER_RECYCLER_ILOTS = false;

	public static boolean DEBUG_PATH = false;

	public static boolean DEBUG_MOVING = false;

	public static boolean DEBUG_TIMETRAVEL = false;

	public static boolean DEBUG_DANGER = false;

	public static boolean DEBUG_MINIMAX = false;

	public static boolean DEBUG_LOGIC = false;

	public static boolean DEBUG_PERF = false;

	public State state = new State();

	public static long start;

	public static int BFS_IN_TIME;

	public static String message;

	public static void main(String[] args) {
		FastReader in = new FastReader(System.in);
		new Player().play(in);
	}

	private void play(FastReader in) {
		state.readGlobal(in);
		while (true) {
			message = "";
			state.read(in);
			debugTerritories(state);
			BFS_IN_TIME = 0;
			think();
			long end = System.currentTimeMillis();
			System.err.println("Time : " + (end - start));
			System.err.println("BFS_IN_TIME " + BFS_IN_TIME);
		}
	}

	public static void debugTerritories(State state) {
		State work = new State();
		work.copyFrom(state);
		Sim.tenTurn(work);
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		List<Ilot> ilots = Ilot.build(work);
		int myTerritory = 0;
		int oppTerritory = 0;
		int disputedTerritory = 0;
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME) {
				myTerritory += ilot.size();
			} else if (ilot.ruler == O.OPP) {
				oppTerritory += ilot.size();
			} else {
				Territory territory = new Territory();
				territory.calculateTerritories(ilot, tt);
				myTerritory += territory.blueTerritory.size();
				oppTerritory += territory.redTerritory.size();
				disputedTerritory += territory.disputed.size();
			}
		}
		Player.message = "" + myTerritory + "/" + oppTerritory + "/" + disputedTerritory;
	}

	private void think() {
		if (State.turn == 0) {
			for (int i = 0; i < 5; i++) {
				// faire chauffer la JVM
				new AI1().think(state);
			}
		}
		List<Action> actions = new AI1().think(state);
		String command = "";
		for (Action action : actions) {
			command += action;
		}
		command += "MESSAGE ";
		if (Logger.hasError)
			command += "🤯 ";
		if (Logger.hasWarning)
			command += "⚠ ";
		command += (state.turn + 1) + "|" + message + " - " + (System.currentTimeMillis() - start) + "ms;";
		if ("".equals(command)) {
			System.out.println("WAIT");
		} else {
			System.out.println(command);
		}
		Logger.reset();
	}
}
