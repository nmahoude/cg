/* Manual*/
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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

class Vec {

  public int vx;

  public int vy;

  public Vec(int dx, int dy) {
    this.vx = dx;
    this.vy = dy;
  }

  public Vec() {
    this.vx = 0;
    this.vy = 0;
  }

  public void copyFrom(Vec model) {
    this.vx = model.vx;
    this.vy = model.vy;
  }

  public void inverse() {
    vx = -vx;
    vy = -vy;
  }

  public void target(Pos origin, Pos target) {
    int dist = target.dist(origin);
    this.vx = (State.MOB_MOVE * (target.x - origin.x) / dist);
    this.vy = (State.MOB_MOVE * (target.y - origin.y) / dist);
  }

  public void set(int dx, int dy) {
    vx = dx;
    vy = dy;
  }

  @Override
  public int hashCode() {
    return Objects.hash(vx, vy);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vec other = (Vec) obj;
    return Double.doubleToLongBits(vx) == Double.doubleToLongBits(other.vx) && Double.doubleToLongBits(vy) == Double.doubleToLongBits(other.vy);
  }

  @Override
  public String toString() {
    if (Player.inversed) {
      return "" + (-vx) + "," + (-vy);
    } else {
      return "" + vx + "," + vy;
    }
  }

  public void alignTo(Pos from, Pos target, int speedMax) {
    double dx = target.x - from.x;
    double dy = target.y - from.y;
    double dist = (int) Math.sqrt(dx * dx + dy * dy);
    dx = dx * speedMax / dist;
    dy = dy * speedMax / dist;
    this.set((int) dx, (int) dy);
  }
}

class Unit {

  public int id;

  public final Pos pos = new Pos();

  public final Vec speed = new Vec();

  public int shieldLife;

  public int isControlled;

  public int health;

  public boolean nearBase;

  public int threatFor;

  // for Simulator calculations
  public double dx, dy;

  public boolean isPushed;

  public int controlTargetCount;

  public final Pos controlTarget = new Pos();

  public int controlNextTargetCount;

  public final Pos controlNextTarget = new Pos();

  @Override
  public String toString() {
    return "u:" + id + " @" + pos;
  }

  public void read(FastReader in) {
    pos.x = in.nextInt();
    pos.y = in.nextInt();
    shieldLife = in.nextInt();
    isControlled = in.nextInt();
    health = in.nextInt();
    speed.vx = in.nextInt();
    speed.vy = in.nextInt();
    nearBase = in.nextInt() == 1;
    threatFor = in.nextInt();
    dx = dy = 0;
    if (Player.inversed) {
      pos.inverse();
      speed.inverse();
    }
    debugInput();
  }

  public void update(State state) {
    pos.add(speed);
    if (!nearBase) {
      int dist2ToMyBase = pos.dist2(State.myBase);
      int dist2ToOppBase = pos.dist2(State.oppBase);
      if (dist2ToMyBase < State.BASE_TARGET_KILL2) {
        state.health[0]--;
        this.health = 0;
      } else if (dist2ToOppBase < State.BASE_TARGET_KILL2) {
        state.health[1]--;
        this.health = 0;
      } else if (dist2ToMyBase < State.BASE_TARGET_DIST2) {
        speed.target(pos, State.myBase);
      } else if (dist2ToOppBase < State.BASE_TARGET_DIST2) {
        speed.target(pos, State.oppBase);
      }
    }
  }

  public void copyFrom(Unit model) {
    id = model.id;
    pos.x = model.pos.x;
    pos.y = model.pos.y;
    dx = dy = 0;
    shieldLife = model.shieldLife;
    isControlled = model.isControlled;
    health = model.health;
    speed.copyFrom(model.speed);
    nearBase = model.nearBase;
    threatFor = model.threatFor;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public final boolean isInRange(Pos pos, int range) {
    return this.pos.dist2(pos) <= range * range;
  }

  public boolean isInRange(Unit unit, int range) {
    return this.pos.dist2(unit.pos) < range * range;
  }

  public boolean isInFog() {
    return shieldLife == -1000;
  }

  public boolean hasShield() {
    return shieldLife > 0;
  }

  public boolean isInHitRange(Hero hero) {
    return this.pos.isInRange(hero.pos, State.MONSTER_TARGET_KILL);
  }

  public boolean isInRangeOfMyBase() {
    return this.isInRange(State.myBase, State.BASE_TARGET_DIST);
  }

  public boolean isInRangeOfOppBase() {
    return this.isInRange(State.oppBase, State.BASE_TARGET_DIST);
  }

  public void debugInput() {
    int dpx = pos.x;
    int dpy = pos.y;
    int dvx = (int) speed.vx;
    int dvy = (int) speed.vy;
    if (Player.inversed) {
      dpx = State.WIDTH - pos.x;
      dpy = State.HEIGHT - pos.y;
      dvx = -dvx;
      dvy = -dvy;
    }
    if (State.DEBUG_INPUTS)
      System.err.println("^" + id + " 0 " + dpx + " " + dpy + " " + shieldLife + " " + isControlled + " " + health + " " + dvx + " " + dvy + " " + (nearBase ? "1" : "0") + " " + threatFor);
  }

  public boolean isInRange(Hero hero, int range) {
    return isInRange(hero.pos, range);
  }

  public boolean isInViewRange(Hero hero) {
    return isInRange(hero.pos, State.HERO_VIEW_RADIUS);
  }
}

class Hero {

  public int id;

  public int owner;

  public final Pos pos = new Pos();

  public int shieldLife;

  // values for simulator
  private boolean inFog;

  public double dx, dy;

  public boolean isControlled;

  public Hero() {
  }

  @Override
  public String toString() {
    return "H(" + id + "," + owner + ") @" + pos;
  }

  public void copyFrom(Hero model) {
    this.id = model.id;
    this.owner = model.owner;
    this.pos.copyFrom(model.pos);
    this.shieldLife = model.shieldLife;
    this.inFog = model.inFog;
    this.isControlled = model.isControlled;
  }

  public void read(FastReader in) {
    pos.x = in.nextInt();
    pos.y = in.nextInt();
    inFog = false;
    shieldLife = in.nextInt();
    
    int isControlled = in.nextInt();
    this.isControlled = isControlled != 0;
    int health = in.nextInt();
    int vx = in.nextInt();
    int vy = in.nextInt();
    int nearBase = in.nextInt();
    int threatFor = in.nextInt();
    if (State.DEBUG_INPUTS)
      System.err.println("^" + id + " " + (owner == 0 ? 1 : 2) + " " + pos.x + " " + pos.y + " " + shieldLife + " " + isControlled + " " + health + " " + vx + " " + vy + " " + nearBase + " " + threatFor);
    if (Player.inversed) {
      pos.x = State.WIDTH - pos.x;
      pos.y = State.HEIGHT - pos.y;
    }
  }

  public boolean isInRange(Unit unit, int range) {
    return this.pos.dist2(unit.pos) <= range * range;
  }

  public boolean isInFog() {
    return inFog;
  }

  public void setInFog() {
    this.inFog = true;
    this.pos.copyFrom(Pos.VOID);
  }

  public boolean isInRange(Pos opos, int range) {
    return this.pos.isInRange(opos, range);
  }

  public void debug() {
    if (isInFog()) {
      System.err.println("" + id + " ... in Fog");
    } else {
      System.err.println("" + id + " " + pos.output() + " " + shieldLife);
    }
  }

  public void readEncoded(String string) {
    id = (int) string.charAt(0);
    pos.x = (int) string.charAt(1);
    pos.y = (int) string.charAt(2);
    shieldLife = (int) string.charAt(3);
  }

  public void debugEncoded() {
    if (isInFog()) {
      System.err.println("" + id + " ... in Fog");
    } else {
      System.err.println("" + (char) id + "" + pos.outputEncoded() + (char) shieldLife);
    }
  }

  public boolean hasShield() {
    return shieldLife > 0;
  }

  public boolean isInRange(Hero hero, int range) {
    return isInRange(hero.pos, range);
  }
}

class FogResolver {

  public boolean seenUnits[] = new boolean[500];

  private Unit units[] = new Unit[500];

  {
    for (int i = 0; i < 500; i++) units[i] = new Unit();
  }

  private Map<Integer, Unit> fogUnitsCandidites = new HashMap<>();

  public List<Unit> activeUnitsInFog = new ArrayList<>();

  public void startOfturn(State state) {
    fogUnitsCandidites.clear();
    for (Unit unit : state.units) {
      // System.err.println("Adding in fog candidates : "+unit);
      units[unit.id].copyFrom(unit);
      fogUnitsCandidites.put(units[unit.id].id, units[unit.id]);
      if (!seenUnits[unit.id]) {
        // first time we see the unit, check if it is near a border, if saw add the symmetry
        if (unit.pos.x < 500 || unit.pos.x > State.WIDTH - 500 || unit.pos.y < 500 || unit.pos.y > State.HEIGHT - 500) {
          int id = unit.pos.y > State.HEIGHT / 2 ^ !Player.inversed ? unit.id + 1 : unit.id - 1;
          System.err.println("Unit" + unit + "Near border, Adding unit " + id + " via symmetry ! ");
          if (seenUnits[id]) {
            System.err.println("   already saw " + id);
          } else {
            Unit symmetry = units[id];
            symmetry.copyFrom(unit);
            symmetry.id = id;
            symmetry.pos.inverse();
            symmetry.speed.inverse();
            fogUnitsCandidites.put(symmetry.id, symmetry);
            seenUnits[id] = true;
            System.err.println("Put symmetry in fog : " + symmetry);
          }
        }
      }
      seenUnits[unit.id] = true;
    }
  }

  public void endOfRead(State state) {
    for (Unit unit : state.units) {
      if (fogUnitsCandidites.containsKey(unit.id)) {
        // System.err.println("Still see "+unit+" so remove from fog candidates");
        fogUnitsCandidites.remove(unit.id);
      }
    }
    activeUnitsInFog.clear();
    for (Unit unit : fogUnitsCandidites.values()) {
      // System.err.println("checking fog unit "+unit);
      playTurnInFog(unit);
      if (!unit.pos.insideOfMap()) {
        //System.err.println("  out of map - discarding");
        continue;
      }
      if (shouldSee(state, unit.pos)) {
        //System.err.println("  should see it, but no - discarding");
        continue;
      }
      if (unit.pos.isInRange(State.oppBase, State.BASE_KILL_RADIUS)) {
        continue;
      }
      //System.err.println("  keeping, still in fog");
      activeUnitsInFog.add(unit);
    }
  }

  private void playTurnInFog(Unit unit) {
    unit.pos.add(unit.speed);
    if (unit.pos.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
      unit.speed.alignTo(unit.pos, State.oppBase, State.MOB_MOVE);
    }
    if (unit.shieldLife > 0)
      unit.shieldLife--;
  }

  private boolean shouldSee(State state, Pos pos) {
    for (Hero hero : state.myHeroes) {
      if (hero.isInRange(pos, State.HERO_VIEW_RADIUS)) {
        return true;
      }
    }
    return false;
  }

  public void reinsertFoMobs(State state) {
    for (Unit unit : activeUnitsInFog) {
      state.units.add(unit);
    }
  }

  public void debug() {
    if (State.DEBUG_INPUTS) {
      System.err.println("****************************");
      System.err.println("UNITS IN FOG - debug 'input'");
      System.err.println("****************************");
      System.err.println("^" + activeUnitsInFog.size());
      for (Unit unit : activeUnitsInFog) {
        unit.debugInput();
      }
    }
  }
}

class Future {

  private static final int FUTURE_MAX = 25;

  State[] states = new State[FUTURE_MAX];

  public Future() {
    for (int i = 0; i < FUTURE_MAX; i++) {
      states[i] = new State();
    }
  }

  public void calculate(State originalState) {
    states[0].copyFrom(originalState);
    for (int i = 1; i < FUTURE_MAX; i++) {
      states[i].copyFrom(states[i - 1]);
      for (Unit unit : states[i].units) {
        if (unit.isDead())
          continue;
        unit.pos.add(unit.speed);
        if (!unit.pos.insideOfMap()) {
          // TODO check if in base boundary
          unit.health = 0;
        }
      }
    }
  }

  public State get(int depth) {
    if (depth >= FUTURE_MAX) {
      System.err.println("WARNING asking for future @ " + depth + " can only give @ " + (FUTURE_MAX - 1));
      return states[FUTURE_MAX - 1];
    } else {
      return states[depth];
    }
  }
}

class UnitPool {

  public static final int MAX = 10_000;

  private static Unit[] units = new Unit[MAX];

  private static int unitsFE = 0;

  static {
    for (int i = 0; i < MAX; i++) {
      units[i] = new Unit();
    }
  }

  public static void reset() {
    unitsFE = 0;
  }

  public static Unit get() {
    return units[unitsFE++];
  }
}

class MobInterceptor {

  public static int CHECK_STEP = 50;

  public static boolean DEBUG_STEPS = false;

  public static final int MAX_STEPS_TO_INTERCEPT = 10;

  final Pos initialTarget = new Pos();

  int initialSteps;

  final Pos realTarget = new Pos();

  int realSteps;

  public int mobsCount;

  // intercept evaluation result
  public int interceptSteps = 0;

  public final Pos interceptPosition = new Pos();

  final Pos nextTurnTargetPos = new Pos();

  public Pos intercept(State state, Hero hero, Unit target) {
    initialTarget.copyFrom(target.pos);
    initialSteps = (Math.max(0, (hero.pos.dist(target.pos)) / State.HERO_MAX_MOVE));
    System.err.println("*** DEBUG MobInterceptor *** ");
    System.err.println("NAIVE : " + hero.id + " @ " + hero.pos + " need " + initialSteps + " steps to reach " + target.id + " @ " + target.pos);
    if (initialSteps <= 1) {
      // ici : on est sur de le taper au prochain coup, mais on peut optimiser pour le tapper, rester dans la hitbox  ET tapper un autre!
      nextTurnTargetPos.copyFrom(target.pos);
      // next turn pos to glue on target as a best solution
      nextTurnTargetPos.add(target.speed);
      System.err.println("Can optimize interceptor by hitting another mob ? ");
      Pos bestPos = bestPosFor(state, hero.pos, target.pos, nextTurnTargetPos);
      realSteps = 1;
      realTarget.copyFrom(bestPos);
      return bestPos;
    } else {
      System.err.println("Need more than one steps, so let's intercept on correct trajectory");
      stepsAndPosToIntercept(state, hero, target);
      realSteps = interceptSteps;
      if (interceptSteps == 200) {
        System.err.println("Can't reach it !");
        realSteps = 200;
        realTarget.copyFrom(Pos.VOID);
        return realTarget;
      }
      realTarget.copyFrom(interceptPosition);
      // TODO check if we can find a near pos where i hit a monster too, keeping the number of steps 
      System.err.println("targeting " + realTarget + " in " + interceptSteps);
      System.err.println("TODO : can we find a better pos for the route to hit some monster ? ");
      return interceptPosition.stepFrom(hero.pos, State.HERO_MAX_MOVE);
    }
  }

  final Pos bestPosToIntercept = new Pos();

  final Pos mobPos = new Pos();

  public void stepsAndPosToIntercept(State state, Hero hero, Unit unit) {
    int bestSteps = Integer.MAX_VALUE;
    int bestStepAt = 0;
    mobPos.copyFrom(unit.pos);
    for (int i = 0; i < MAX_STEPS_TO_INTERCEPT; i++) {
      if (!mobPos.insideOfMap()) {
        bestPosToIntercept.copyFrom(Pos.VOID);
        bestStepAt = 200;
        break;
      }
      int stepToNewPos = Math.max(0, (int) (0.5 + (1.0 * hero.pos.dist(mobPos) - State.MONSTER_TARGET_KILL) / State.HERO_MAX_MOVE));
      int diff = Math.abs(stepToNewPos - i);
      if (diff < bestSteps) {
        bestSteps = diff;
        bestStepAt = i;
        bestPosToIntercept.copyFrom(mobPos);
      } else if (diff > bestSteps) {
        break;
      }
      mobPos.add(unit.speed);
    }
    interceptSteps = bestStepAt;
    interceptPosition.copyFrom(bestPosToIntercept);
  }

  final Pos myNextPos = new Pos();

  final Pos bestPos = new Pos();

  public Pos bestPosFor(State state, Pos startPos, Pos currentTarget, Pos currentTargetNextPos) {
    int bestMobsCount = 0;
    bestPos.copyFrom(Pos.VOID);
    for (int dy = -800; dy <= 800; dy += CHECK_STEP) {
      for (int dx = -800; dx <= 800; dx += CHECK_STEP) {
        if (dx * dx + dy * dy > 800 * 800) {
          if (DEBUG_STEPS)
            System.err.print(" ");
          continue;
        }
        myNextPos.copyFrom(startPos);
        myNextPos.addAndSnap(dx, dy);
        // we need to hit the chosen mob ! we will move but not him before the combat resolution
        if (currentTarget != null && myNextPos.dist2(currentTarget) > State.MONSTER_TARGET_KILL2) {
          if (DEBUG_STEPS)
            System.err.print("X");
          continue;
        }
        // calculer le nombre de mobs qu'on peut hit sur ce tour (les mobs n'auront pas bouger avant la résolution du combat)
        int mobsCount = 0;
        for (Unit unit : state.units) {
          if (unit.isDead())
            continue;
          if (myNextPos.dist2(unit.pos) < State.MONSTER_TARGET_KILL2)
            mobsCount++;
        }
        if (DEBUG_STEPS)
          System.err.print(mobsCount > 9 ? "+" : mobsCount);
        if (mobsCount > bestMobsCount) {
          bestMobsCount = mobsCount;
          bestPos.copyFrom(myNextPos);
        } else if (mobsCount == bestMobsCount) {
          // meme nombre de mob, mais on est plus pret de la prochaine position de notre target, donc on le choisi
          if (currentTargetNextPos != null && myNextPos.dist2(currentTargetNextPos) < bestPos.dist2(currentTargetNextPos)) {
            bestPos.copyFrom(myNextPos);
          }
        }
      }
      if (DEBUG_STEPS)
        System.err.println();
    }
    if (bestMobsCount == 1) {
      System.err.println("targeting only target @" + currentTargetNextPos + " by going to closest pos : " + bestPos);
    } else {
      System.err.println("targeting multiple targets " + bestMobsCount + " by going to closest pos : " + bestPos);
    }
    mobsCount = bestMobsCount;
    return bestPos;
  }
}

interface MicroAI {

  public Action think(State state, Hero hero);
}

class AttackNearest implements MicroAI {

  static final MobInterceptor mobInterceptor = new MobInterceptor();

  public Action think(State state, Hero hero) {
    double bestScore = Double.NEGATIVE_INFINITY;
    Unit best = null;
    for (Unit unit : state.units) {
      if (unit.isDead())
        continue;
      mobInterceptor.stepsAndPosToIntercept(state, hero, unit);
      Pos interceptPos = mobInterceptor.interceptPosition;
      if (interceptPos.equals(Pos.VOID))
        continue;
      // dont follow (and so hit) if near opp base  
      if (interceptPos.isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000))
        continue;
      // defending base is not my job 
      if (interceptPos.isInRange(State.myBase, State.BASE_TARGET_DIST + 2000))
        continue;
      State future = State.future.get(mobInterceptor.interceptSteps);
      int mobsCount = future.getUnitCountInRange(interceptPos, State.MONSTER_TARGET_KILL);
      // Player.draw.drawLine(Color.BLACK, hero.pos, interceptPos);
      // draw.drawText(Color.BLACK, interceptPos, ""+mobsCount);
      // low steps is better, then mobsCount is better
      double score = 10 * (100 - mobInterceptor.interceptSteps) + mobsCount;
      // System.err.println("  score of unit "+unit+" is "+score + "( "+mobInterceptor.interceptSteps+" / future : "+mobsCount+")");
      if (score > bestScore) {
        bestScore = score;
        best = unit;
      }
    }
    if (best != null) {
      System.err.println("attacking " + best);
      Pos target = mobInterceptor.intercept(state, hero, best);
      System.err.println("Optimizing ... to " + target);
      return Action.doMove(target);
    } else {
      return Action.WAIT;
    }
  }
}

/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
class ControlMobToOppBase implements MicroAI {

  public static final ControlMobToOppBase i = new ControlMobToOppBase();

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  private static final int MIN_RANGE_FROM_BASE = 1000;

  private static final int MAX_RANGE_FROM_BASE = 4000;

  private static final int MINIMUM_HEALTH_TO_CONTROL = 20;

  private static final Pos[] TargetDirection = new Pos[] { new Pos(17630, 5000), new Pos(13630, 9000), State.oppBase };

  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10)
      return Action.WAIT;
    for (Unit unit : state.units) {
      if (unit.isDead())
        continue;
      if (!unit.isInRange(hero, State.CONTROL_RANGE))
        continue;
      // pas assez de vie
      if (unit.health < MINIMUM_HEALTH_TO_CONTROL)
        continue;
      // shield
      if (unit.hasShield())
        continue;
      // trop pret de la base
      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + MIN_RANGE_FROM_BASE))
        continue;
      // trop loin de la base
      if (!unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + MAX_RANGE_FROM_BASE))
        continue;
      // pas besoin si elle va déjà dans la direction
      if (unit.nearBase || unit.threatFor == 2)
        continue;
      //TODO check current dir & mobs in quadrant instead of random
      //Pos target = TargetDirection[random.nextInt(3)];
      Pos target = State.oppBase;
      return Action.doControl(unit.id, target);
    }
    return Action.WAIT;
  }
}

/**
 * no target, but should hit the maximum of bots
 * @author nmahoude
 *
 */
/**
 * no target, but should hit the maximum of bots
 * @author nmahoude
 *
 */
class HitMaxBots implements MicroAI {

  public static final HitMaxBots i = new HitMaxBots();

  private static final MobInterceptor mobInterceptor = new MobInterceptor();

  @Override
  public Action think(State state, Hero hero) {
    System.err.println("Move for max mobs !");
    Pos intercept = mobInterceptor.bestPosFor(state, hero.pos, null, null);
    if (intercept.equals(Pos.VOID))
      return Action.WAIT;
    System.err.println("Found " + mobInterceptor.mobsCount + " @ " + intercept);
    return Action.doMove(intercept);
  }
}

class InitPatrol implements MicroAI {

  static Pos[] positions = new Pos[] { new Pos(4252, 8491), new Pos(8374, 457), new Pos(5966, 7344) };

  public Action think(State state, Hero hero) {
    int id = hero.id >= 3 ? hero.id - 3 : hero.id;
    for (Unit unit : state.units) {
      if (unit.isInViewRange(hero)) {
        return Action.WAIT;
      }
    }
    return Action.doMove(positions[id]);
  }
}

/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
/**
 * Shield buffy mobs going into opp base
 * @author nmahoude
 *
 */
class ShieldAttack implements MicroAI {

  private static final int MINIMUM_HEALTH_TO_SHIELD = 12;

  @Override
  public Action think(State state, Hero hero) {
    // no mana
    if (state.mana[0] < 10)
      return Action.WAIT;
    Unit best = null;
    int bestScore = Integer.MIN_VALUE;
    for (Unit monster : state.units) {
      if (monster.isDead())
        continue;
      // on ne met pas de shield sur les morts et les shieldés
      if (monster.hasShield())
        continue;
      // on ne le fait que sur les mobs avec pas mal de health
      if (monster.health < MINIMUM_HEALTH_TO_SHIELD)
        continue;
      // il faut qu'on puisse caster ...
      if (!monster.isInRange(hero.pos, State.SHIELD_RANGE))
        continue;
      Unit nextTurn = State.future.get(1).findUnitById(monster.id);
      // doit entrer dans la base au prochain tour où déjà y etre
      if (!nextTurn.pos.isInRange(State.oppBase, State.BASE_TARGET_DIST))
        continue;
      if (monster.health > bestScore) {
        bestScore = monster.health;
        best = monster;
      }
    }
    if (best != null) {
      System.err.println("ATT SHIELD on " + best);
      return Action.doShield(best.id);
    }
    return Action.WAIT;
  }
}

/**
 * check if there is an excellent opportunity to take regardless of everything
 * 
 * 1. wind unit into opp base when we are so close it will be disastrous
 * 
 * 
 * @author nmahoude
 *
 */
/**
 * check if there is an excellent opportunity to take regardless of everything
 * 
 * 1. wind unit into opp base when we are so close it will be disastrous
 * 
 * 
 * @author nmahoude
 *
 */
class TakeOpportunity implements MicroAI {

  public static final TakeOpportunity i = new TakeOpportunity();

  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10)
      return Action.WAIT;
    for (Unit unit : state.units) {
      if (unit.hasShield())
        continue;
      if (!unit.isInRange(hero, State.WIND_PUSH_DISTANCE))
        continue;
      if (unit.isInRange(State.oppBase, 2200)) {
        // direct hit !
        return Action.doWind(State.oppBase);
      }
      if (unit.isInRange(State.oppBase, 3500)) {
        // TODO not the center ?
        return Action.doWind(State.oppBase);
      }
    }
    return Action.WAIT;
  }
}

class UseWindTowardsOppBase implements MicroAI {

  public static final UseWindTowardsOppBase i = new UseWindTowardsOppBase();

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  private static final int MIN_DISTANCE_TO_BASE_TO_WIND = State.BASE_TARGET_DIST + 1500;

  private static final int MIN_MOB_HEALTH_TO_WIND = 10;

  private static final Pos[] TargetDirection = new Pos[] { new Pos(17630, 5000), new Pos(13630, 9000), State.oppBase };

  @Override
  public Action think(State state, Hero hero) {
    if (state.mana[0] < 10) {
      return Action.WAIT;
    }
    int monsterCount = 0;
    for (Unit monster : state.units) {
      if (monster.isDead())
        continue;
      if (monster.health < MIN_MOB_HEALTH_TO_WIND)
        continue;
      if (monster.hasShield())
        continue;
      if (monster.isInRange(hero.pos, State.WIND_RANGE) && monster.isInRange(State.oppBase, MIN_DISTANCE_TO_BASE_TO_WIND)) {
        monsterCount++;
      }
    }
    if (monsterCount > 0) {
      Pos pos = state.getBestQuadrantToThrow();
      return Action.doWind(pos);
    }
    return Action.WAIT;
  }
}

class AttackerV2 {

  private static final ShieldAttack SHIELD_ATTACK = new ShieldAttack();

  private static final HitMaxBots HIT_MAX_BOTS = new HitMaxBots();

  private static final InitPatrol INIT_PATROL = new InitPatrol();

  private static final AttackNearest ATTACK_NEAREST = new AttackNearest();

  private static final Pos CENTER = new Pos(17630 / 2, 9000 / 2);

  private static final MobInterceptor mobInterceptor = new MobInterceptor();

  private static ThreadLocalRandom random = ThreadLocalRandom.current();

  public static final int NEED_MANA_THRESHOLD = 60;

  public static final int NO_NEED_MANA_THRESHOLD = 100;

  public static final int MIND_EARLYGAME = 0;

  public static final int MIND_NEED_MANA = 1;

  public static final int MIND_AGGRO = 2;

  private State state;

  private Hero hero;

  int mind = MIND_EARLYGAME;

  public void outputForDebug() {
    if (State.DEBUG_INPUTS) {
      System.err.println("^turn " + Player.turn);
      System.err.println("^mind " + mind);
    }
  }

  public void read(FastReader in) {
    in.nextString();
    Player.turn = in.nextInt();
    in.nextString();
    mind = in.nextInt();
  }

  public void updateTurn(State state) {
    state = state;
  }

  int sawCenterAt;

  public Action attack(State state, Hero hero) {
    this.state = state;
    this.hero = hero;
    System.err.println("*************************");
    System.err.println("*  ATTACKER V2     *");
    System.err.println("*************************");
    outputForDebug();
    final int ATTACK_TURN = 100;
    // histeresis for mana
    if (mind == MIND_AGGRO || mind == MIND_NEED_MANA) {
      if (state.mana[0] < NEED_MANA_THRESHOLD)
        mind = MIND_NEED_MANA;
      if (state.mana[0] > NO_NEED_MANA_THRESHOLD)
        mind = MIND_AGGRO;
    } else {
      if (Player.turn > ATTACK_TURN || state.mana[0] > 200) {
        mind = MIND_AGGRO;
      } else {
        mind = MIND_EARLYGAME;
      }
    }
    Action action;
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT)
      return action;
    if (mind == MIND_NEED_MANA) {
      if ((action = shouldGetBackSomeMana()) != Action.WAIT)
        return action;
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT)
        return action;
      return patrol();
    }
    if (mind == MIND_AGGRO) {
      if ((action = SHIELD_ATTACK.think(state, hero)) != Action.WAIT)
        return action;
      if ((action = UseWindTowardsOppBase.i.think(state, hero)) != Action.WAIT)
        return action;
      if ((action = ControlMobToOppBase.i.think(state, hero)) != Action.WAIT)
        return action;
      if ((action = sendOppsFurther()) != Action.WAIT)
        return action;
      if ((action = attackOppBase()) != Action.WAIT)
        return action;
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT)
        return action;
      return patrol();
    } else if (mind == MIND_EARLYGAME) {
      if (Player.turn < 10) {
        if ((action = INIT_PATROL.think(state, hero)) != Action.WAIT)
          return action;
      }
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT)
        return action;
      return patrol();
    } else {
      System.out.println("Unknwon mind " + mind);
      return Action.WAIT;
    }
  }

  private Action shouldGetBackSomeMana() {
    if (mind != MIND_NEED_MANA)
      return Action.WAIT;
    System.err.println("In need of mana");
    Action action;
    if ((action = HIT_MAX_BOTS.think(state, hero)) != Action.WAIT)
      return action;
    if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT)
      return action;
    return patrol();
  }

  private Action sendOppsFurther() {
    if (state.mana[0] < 10)
      return Action.WAIT;
    if (!hero.isInRange(State.oppBase, State.BASE_TARGET_DIST + 2000))
      return Action.WAIT;
    boolean unitInOppBaseRadius = false;
    for (Unit unit : state.units) {
      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
        unitInOppBaseRadius = true;
        break;
      }
    }
    // do send him back if there is no mobs in his base
    if (!unitInOppBaseRadius)
      return Action.WAIT;
    // TODO au contraire ???
    // 1st wind
    int windCount = 0;
    for (Hero opp : state.oppHeroes) {
      if (opp.hasShield())
        continue;
      if (opp.isInRange(hero, State.WIND_RANGE)) {
        windCount++;
      }
    }
    if (windCount > 0) {
      // opposé du centre TODO better to do ?
      return Action.doWind(hero.pos.x - State.oppBase.x, hero.pos.y - State.oppBase.y);
    }
    // then try to control
    for (Hero opp : state.oppHeroes) {
      if (opp.hasShield())
        continue;
      if (opp.isInRange(hero, State.CONTROL_RANGE)) {
        // TODO better angle ?
        return Action.doControl(opp.id, State.myBase);
      }
    }
    return Action.WAIT;
  }

  private Action attackOppBase() {
    if (hero.pos.isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000))
      return Action.WAIT;
    // just out of range
    return Action.doMove(State.oppBase);
  }

  static Pos patrols[] = new Pos[] { new Pos(7500, 7000), new Pos(12000, 2000), new Pos(13000, 4975) };

  private Action patrol() {
    if (hero.isInRange(CENTER, 200)) {
      sawCenterAt = Player.turn;
    }
    if (sawCenterAt < Player.turn - 15) {
      System.err.println("Moving to center");
      return Action.doMove(CENTER);
    } else {
      System.err.println("Patrolling ");
      return Action.doMove(patrols[random.nextInt(patrols.length)]);
    }
  }
}

class State {

  public static boolean DEBUG_INPUTS = true;

  public static final int WIDTH = 17630;

  public static final int HEIGHT = 9000;

  public static final int HEROES_COUNT = 3;

  public static final int HERO_MAX_MOVE = 800;

  public static final int MOB_MOVE = 400;

  public static final int BASE_TARGET_DIST = 5000;

  public static final int BASE_TARGET_DIST2 = BASE_TARGET_DIST * BASE_TARGET_DIST;

  public static final int BASE_KILL_RADIUS = 300;

  public static final int BASE_TARGET_KILL2 = BASE_KILL_RADIUS * BASE_KILL_RADIUS;

  private static final int BASE_VIEW = BASE_TARGET_DIST + 1000;

  public static final int MONSTER_TARGET_KILL = 800;

  public static final int MONSTER_TARGET_KILL2 = 800 * 800;

  private static final int SHIELD_LIFE = 12;

  public static final int HERO_VIEW_RADIUS = 2200;

  public static final int HERO_VIEW_RADIUS2 = HERO_VIEW_RADIUS * HERO_VIEW_RADIUS;

  public static final int WIND_RANGE = 1280;

  public static final int WIND_RANGE2 = WIND_RANGE * WIND_RANGE;

  public static final int WIND_PUSH_DISTANCE = 2200;

  public static final int SHIELD_RANGE = 2200;

  public static final int CONTROL_RANGE = 2200;

  public static final int CONTROL_RANGE2 = CONTROL_RANGE * CONTROL_RANGE;

  public static final Pos myBase = new Pos(0, 0);

  public static final Pos oppBase = new Pos(WIDTH, HEIGHT);

  // to keep where is attacker
  public static final Hero oppAttacker = new Hero();

  public static Future future = new Future();

  private static FogResolver fogResolver = new FogResolver();

  public Hero[] allHeroes = new Hero[6];

  public Hero[] myHeroes = new Hero[3];

  public Hero[] oppHeroes = new Hero[3];

  public List<Unit> units = new ArrayList<>();

  public int[] health = new int[2];

  public int[] mana = new int[2];

  public State() {
    for (int i = 0; i < 6; i++) {
      allHeroes[i] = new Hero();
    }
    setHeroesAndUnits();
    myBase.x = 0;
    myBase.y = 0;
    oppBase.x = WIDTH;
    oppBase.y = HEIGHT;
  }

  public void readGlobal(FastReader in) {
    int baseX = in.nextInt();
    int baseY = in.nextInt();
    Player.inversed = baseX != 0;
    setHeroesAndUnits();
    int heroesPerPlayer = in.nextInt();
    if (State.DEBUG_INPUTS)
      System.err.println("^" + baseX + " " + baseY + " " + heroesPerPlayer);
  }

  private void setHeroesAndUnits() {
    if (Player.inversed) {
      for (int i = 0; i < 3; i++) {
        myHeroes[i] = allHeroes[3 + i];
        myHeroes[i].owner = 0;
        oppHeroes[i] = allHeroes[i];
        oppHeroes[i].owner = 1;
      }
    } else {
      for (int i = 0; i < 3; i++) {
        myHeroes[i] = allHeroes[i];
        myHeroes[i].owner = 0;
        oppHeroes[i] = allHeroes[3 + i];
        oppHeroes[i].owner = 1;
      }
    }
  }

  public void read(FastReader in) {
    fogResolver.startOfturn(this);
    for (Hero hero : allHeroes) {
      hero.setInFog();
    }
    for (int i = 0; i < 2; i++) {
      int health = in.nextInt();
      int mana = in.nextInt();
      this.health[i] = health;
      this.mana[i] = mana;
    }
    if (State.DEBUG_INPUTS)
      System.err.println("^" + health[0] + " " + mana[0] + " " + health[1] + " " + mana[1]);
    // Amount of heros and monsters you can see
    int entityCount = in.nextInt();
    if (State.DEBUG_INPUTS)
      System.err.println("^" + entityCount);
    units.clear();
    for (int i = 0; i < entityCount; i++) {
      int id = in.nextInt();
      int type = in.nextInt();
      if (type == 1 || type == 2) {
        allHeroes[id].id = id;
        allHeroes[id].read(in);
      } else {
        // Attention le pool est reset entre chaque tour
        Unit unit = UnitPool.get();
        unit.id = id;
        unit.read(in);
        units.add(unit);
      }
    }
    updateOppAttacker();
    fogResolver.endOfRead(this);
    fogResolver.debug();
    fogResolver.reinsertFoMobs(this);
    updateAfterFog();
  }

  private void updateAfterFog() {
    future.calculate(this);
  }

  private void updateOppAttacker() {
    Hero turnOppAttacker = attackerOppHero();
    if (turnOppAttacker != null) {
      oppAttacker.copyFrom(turnOppAttacker);
    } else {
      oppAttacker.pos.copyFrom(Pos.VOID);
      oppAttacker.shieldLife = 1000;
    }
  }

  public void copyFrom(State model) {
    this.health[0] = model.health[0];
    this.health[1] = model.health[1];
    this.mana[0] = model.mana[0];
    this.mana[1] = model.mana[1];
    for (int i = 0; i < 6; i++) {
      this.allHeroes[i].copyFrom(model.allHeroes[i]);
    }
    // attach my & opp heroes correctly
    for (int i = 0; i < 3; i++) {
      if (Player.inversed) {
        myHeroes[i] = allHeroes[i + 3];
        oppHeroes[i] = allHeroes[i];
      } else {
        myHeroes[i] = allHeroes[i];
        oppHeroes[i] = allHeroes[i + 3];
      }
    }
    units.clear();
    for (Unit u : model.units) {
      Unit unit = UnitPool.get();
      unit.copyFrom(u);
      units.add(unit);
    }
  }

  public Unit getClosestUnit(Pos pos) {
    int bestDist = Integer.MAX_VALUE;
    Unit best = null;
    for (Unit unit : units) {
      if (unit.isDead())
        continue;
      int dist2 = pos.dist2(unit.pos);
      if (dist2 < bestDist) {
        bestDist = dist2;
        best = unit;
      }
    }
    return best;
  }

  /** 
   * return the best direction to throw a mob depending on occupation 
   */
  /** 
   * return the best direction to throw a mob depending on occupation 
   */
  final int[] qCount = new int[3];

  public Pos getBestQuadrantToThrow() {
    for (int i = 0; i < qCount.length; i++) {
      qCount[i] = 0;
    }
    // check quadrant
    for (Unit monster : units) {
      if (monster.isDead())
        continue;
      if (!monster.isInRange(oppBase, State.BASE_TARGET_DIST))
        continue;
      int q = getQuadrant(monster.pos);
      qCount[q]++;
    }
    int leastQuandrant = 1;
    for (int i = 0; i < 3; i++) {
      if (qCount[i] < qCount[leastQuandrant]) {
        leastQuandrant = i;
      }
    }
    System.err.println("DEBUG Quadrants : " + Arrays.toString(qCount));
    System.err.println("     least is " + leastQuandrant);
    if (leastQuandrant == 1) {
      return oppBase;
    } else if (leastQuandrant == 0) {
      return Pos.get(17630 - 1, 9000 - 1 - 3001);
    } else {
      return Pos.get(17630 - 1 - 3000, 9000 - 1);
    }
  }

  public static int getQuadrant(Pos pos) {
    double x = oppBase.x - pos.x;
    double y = oppBase.y - pos.y;
    // project on oppBase -> 
    
    int dist = (int) Math.sqrt(x * x + y * y);
    
    x = x / dist;
    if (x > Math.cos(Math.PI / 6)) {
      return 0;
    } else if (x > Math.cos(2 * Math.PI / 6)) {
      return 1;
    } else
      return 2;
  }

  /**
   * return opp hero that is in base view zone (considering he is attacking me)
   */
  /**
   * return opp hero that is in base view zone (considering he is attacking me)
   */
  private Hero attackerOppHero() {
    for (int i = 0; i < 3; i++) {
      if (myBase.isInRange(oppHeroes[i].pos, BASE_VIEW))
        return oppHeroes[i];
    }
    return null;
  }

  public Unit findUnitById(int id) {
    for (Unit unit : units) {
      if (unit.id == id)
        return unit;
    }
    return null;
  }

  public int getUnitCountInRange(Pos pos, int range) {
    int count = 0;
    for (Unit unit : units) {
      if (unit.isDead())
        continue;
      if (unit.isInRange(pos, range))
        count++;
    }
    return count;
  }

  /** for debugging purposes */
  /** for debugging purposes */
  public void readFog(FastReader in) {
    int entityInFog = in.nextInt();
    for (int i = 0; i < entityInFog; i++) {
      int id = in.nextInt();
      int type = in.nextInt();
      Unit unit = new Unit();
      unit.id = id;
      unit.read(in);
      units.add(unit);
    }
    updateAfterFog();
  }

  public Hero findHeroById(int id) {
    return allHeroes[id];
  }

  /**
   * do move and resolve combat for this action
   * @param action
   * @param action2
   */
  /**
   * do move and resolve combat for this action
   * @param action
   * @param action2
   */
  public void apply(int h, Action action) {
    if (action.type == Action.TYPE_MOVE) {
      myHeroes[h].pos.moveToward(action.target, HERO_MAX_MOVE);
    }
    for (Unit unit : units) {
      Hero hero = myHeroes[h];
      if (hero.isInRange(unit, MONSTER_TARGET_KILL)) {
        unit.health -= 2;
      }
    }
    units.removeIf( u -> u.isDead());
  }

  public Pos findPosById(int targetEntity) {
    for (Hero h : allHeroes) {
      if (h.id == targetEntity)
        return h.pos;
    }
    for (Unit h : units) {
      if (h.id == targetEntity)
        return h.pos;
    }
    return null;
  }

  public static State fromInput(String input, AttackerV2 attacker) {
    String lines[] = input.split("\n");
    String gameInput = "";
    String fogInput = "";
    String attackerInput = "";
    int i = 0;
    while (lines[i].trim().startsWith("^")) {
      gameInput += lines[i++] + "\r\n";
    }
    while (!lines[i++].contains("^")) ;
    i--;
    while (lines[i].trim().startsWith("^")) {
      fogInput += lines[i++] + "\r\n";
    }
    while (!lines[i++].contains("^")) ;
    i--;
    while (i < lines.length && lines[i].trim().startsWith("^")) {
      attackerInput += lines[i++] + "\r\n";
    }
    State state = new State();
    state.read(FastReader.fromString(gameInput.replace("^", "")));
    state.readFog(FastReader.fromString(fogInput.replace("^", "")));
    if (attacker != null)
      attacker.read(FastReader.fromString(attackerInput));
    return state;
  }
}

class Pos {

  public static final Pos VOID = new Pos(-10000, -10000);

  public static List<Pos> heroMoveRadius = new ArrayList<>();

  static {
    for (int dy = -800; dy <= 800; dy += 10) {
      for (int dx = -800; dx <= 800; dx += 10) {
        if (dx * dx + dy * dy > 800 * 800)
          continue;
        heroMoveRadius.add(new Pos(dx, dy));
      }
    }
  }

  public int x;

  public int y;

  public Pos() {
    this.x = 0;
    this.y = 0;
  }

  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Pos(Pos target) {
    this.x = target.x;
    this.y = target.y;
  }

  public final int dist2(Pos pos) {
    return (this.x - pos.x) * (this.x - pos.x) + (this.y - pos.y) * (this.y - pos.y);
  }

  public boolean isInRange(Pos pos, int range) {
    return dist2(pos) <= range * range;
  }

  public void copyFrom(Pos pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  public void moveToward(Pos target, int maxMove) {
    int dist2 = this.dist2(target);
    if (dist2 <= maxMove * maxMove) {
      this.copyFrom(target);
    } else {
      int dist = (int) (Math.sqrt(dist2));
      this.x += (target.x - this.x) * maxMove / dist;
      this.y += (target.y - this.y) * maxMove / dist;
    }
  }

  @Override
  public String toString() {
    if (Player.inversed) {
      return String.format("(%d, %d)", State.WIDTH - x, State.HEIGHT - y);
    } else {
      return String.format("(%d, %d)", x, y);
    }
  }

  public String output() {
    if (Player.inversed) {
      return "" + (State.WIDTH - x) + " " + (State.HEIGHT - y);
    } else {
      return "" + x + " " + y;
    }
  }

  public int dist(Pos pos) {
    return (int) Math.sqrt(dist2(pos));
  }

  public String outputEncoded() {
    if (Player.inversed) {
      return "" + (char) (State.WIDTH - x) + (char) (State.HEIGHT - y);
    } else {
      return "" + (char) x + "" + (char) y;
    }
  }

  public void addAndSnap(int dx, int dy) {
    this.add(dx, dy);
    if (this.x < 0)
      this.x = 0;
    if (this.x > State.WIDTH)
      this.x = State.WIDTH;
    if (this.y < 0)
      this.y = 0;
    if (this.y > State.HEIGHT)
      this.y = State.HEIGHT;
  }

  public void addAndSnap(double dx, double dy) {
    dx += this.x;
    dy += this.y;
    this.x = (int) Math.round(dx + 0.45);
    this.y = (int) Math.round(dy + 0.45);
    if (this.x < 0)
      this.x = 0;
    if (this.x > State.WIDTH)
      this.x = State.WIDTH;
    if (this.y < 0)
      this.y = 0;
    if (this.y > State.HEIGHT)
      this.y = State.HEIGHT;
  }

  public void decode(String string) {
    x = (int) (string.charAt(0));
    y = (int) (string.charAt(1));
  }

  public void add(int dx, int dy) {
    this.x += dx;
    this.y += dy;
  }

  public void copyFrom(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean insideOfMap() {
    if (this.x < 0)
      return false;
    if (this.x > State.WIDTH)
      return false;
    if (this.y < 0)
      return false;
    if (this.y > State.HEIGHT)
      return false;
    return true;
  }

  public static Pos get(int x, int y) {
    return new Pos(x, y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pos other = (Pos) obj;
    return x == other.x && y == other.y;
  }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void inverse() {
    x = State.WIDTH - x;
    y = State.HEIGHT - y;
  }

  public void add(Vec speed) {
    this.x += speed.vx;
    this.y += speed.vy;
  }

  public void addAndSnap(Vec speed) {
    addAndSnap(speed.vx, speed.vy);
  }

  private static final Pos stepFrom = new Pos();

  public Pos stepFrom(Pos pos, int range) {
    int dx = this.x - pos.x;
    int dy = this.y - pos.y;
    double dist = Math.sqrt(dx * dx + dy * dy);
    stepFrom.x = (int) (pos.x + range * dx / dist);
    stepFrom.y = (int) (pos.y + range * dy / dist);
    return stepFrom;
  }

  public void add(Pos target) {
    this.x += target.x;
    this.y += target.y;
  }

  public int fastDist(Pos pos) {
    double d = (pos.x - x) * (pos.x - x) + (pos.y - y) * (pos.y - y);
    double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(d) - (1l << 52)) >> 1) + (1l << 61));
    double better = (sqrt + d / sqrt) / 2.0;
    return (int)better;
  }

  public void add(double dx, double dy) {
    this.x += dx;
    this.y += dy;
  }
}

class AGInformation {

  boolean deadlyDanger = false;

  boolean controlAttacker = false;

  boolean[] canControl = new boolean[3];

  public int unitToControl;

  Hero oppAttacker = null;

  public void update(State state) {
    deadlyDanger = false;
    controlAttacker = false;
    for (int i = 0; i < 3; i++) canControl[i] = false;
    for (Unit unit : state.units) {
      if (unit.isDead())
        continue;
      if (unit.isInRange(State.myBase, 1500)) {
        System.err.println("DEADLY DANGER");
        deadlyDanger = true;
      }
    }
    oppAttacker = null;
    for (Hero hero : state.oppHeroes) {
      if (hero.pos.dist(State.myBase) < State.BASE_TARGET_DIST + 2000) {
        oppAttacker = hero;
      // TODO what if plusieurs attaquants ?
      }
    }
  }
}

class LightState {

  public Unit units[] = new Unit[25];

  public int unitsFE = 0;

  public Hero[] hero = new Hero[] { new Hero(), new Hero() };

  public Hero[] oppHeroes = new Hero[] { new Hero(), new Hero(), new Hero() };

  public int health;

  public int mana;

  public int wildMana;

  public int extraBonus;

  public LightState() {
    for (int i = 0; i < units.length; i++) {
      // private pool
      units[i] = new Unit();
    }
  }

  public void createFrom(State state) {
    health = state.health[0];
    mana = state.mana[0];
    wildMana = 0;
    hero[0].copyFrom(state.myHeroes[0]);
    hero[0].id = 0;
    hero[1].copyFrom(state.myHeroes[1]);
    hero[1].id = 1;
    for (int i = 0; i < 3; i++) {
      oppHeroes[i].copyFrom(state.oppHeroes[i]);
    }
    unitsFE = 0;
    for (Unit unit : state.units) {
      if (unit.isDead())
        continue;
      // don't get all units ...
      if (!unit.isInRange(State.myBase, 10_000))
        continue;
      this.units[this.unitsFE].copyFrom(unit);
      this.unitsFE++;
    }
  }

  public void copyFrom(LightState model) {
    health = model.health;
    mana = model.mana;
    wildMana = 0;
    hero[0].copyFrom(model.hero[0]);
    hero[1].copyFrom(model.hero[1]);
    for (int i = 0; i < 3; i++) {
      oppHeroes[i].copyFrom(model.oppHeroes[i]);
    }
    unitsFE = model.unitsFE;
    for (int i = 0; i < model.unitsFE; i++) {
      Unit unit = model.units[i];
      this.units[i].copyFrom(unit);
    }
  }

  public Unit getUnitById(int id) {
    for (int i = 0; i < unitsFE; i++) {
      if (units[i].id == id)
        return units[i];
    }
    return null;
  }

  public Pos findPosById(int targetEntity) {
    for (Hero h : hero) {
      if (h.id == targetEntity)
        return h.pos;
    }
    for (Hero h : oppHeroes) {
      if (h.id == targetEntity)
        return h.pos;
    }
    for (int i = 0; i < unitsFE; i++) {
      if (units[i].id == targetEntity)
        return units[i].pos;
    }
    return Pos.VOID;
  }

  public void kill(int index) {
    // swap units & decrement unitsFE
    Unit swap = units[index];
    units[index] = units[unitsFE - 1];
    units[unitsFE - 1] = swap;
    unitsFE--;
  }
}

class TriAction {

  public Action[] actions = new Action[3];

  public TriAction() {
    actions[0] = new Action();
    actions[1] = new Action();
    actions[2] = new Action();
  }

  public void copyFrom(TriAction model) {
    actions[0].copyFrom(model.actions[0]);
    actions[1].copyFrom(model.actions[1]);
    actions[2].copyFrom(model.actions[2]);
  }

  public void output() {
    for (int i = 0; i < 3; i++) {
      System.out.println(actions[i] + " #" + i);
    }
  }

  public void reset() {
    for (int i = 0; i < 3; i++) {
      actions[i].type = Action.TYPE_WAIT;
    }
  }
}

class AG {

  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  public static final int POPULATION_SIZE = 50;

  public static final int SURVIVOR_SIZE = 8;

  public static final int MAX_DEPTH = 12;
  public static int DEPTH = 12;

  public AGInformation information = new AGInformation();

  public final AGSolution[] solutions = new AGSolution[POPULATION_SIZE];

  {
    for (int i = 0; i < POPULATION_SIZE; i++) {
      solutions[i] = new AGSolution();
    }
  }

  final AGSolution bestSolution = new AGSolution();

  private double bestScore;

  private final LightState originalState = new LightState();

  private final TriAction oppHeroesAction = new TriAction();

  private final LightState state = new LightState();

  final TriAction bestTriaction = new TriAction();

  private int bestPlyaAt;

  private int currentPly;

  public TriAction think(State originalRealState) {
    DEPTH = Math.min(MAX_DEPTH, 1 + 220 - Player.turn);
    System.err.println("Starting AG @ " + (System.currentTimeMillis() - Player.start) + "for depth " + DEPTH);
    information.update(originalRealState);
    originalState.createFrom(originalRealState);
    oppHeroesAction.reset();
//    for (int i=0;i<3;i++) {
//      if (originalRealState.mana[1] >= 10 && originalState.oppHeroes[i].isInRange(State.myBase, State.BASE_TARGET_DIST+1000)) {
//        oppHeroesAction.actions[i].wind(State.myBase);
//      }
//    }
    
    
    
    
    resetAG();
    // TODO implements other pecalculated populations
    System.err.println("TODO : implements other pecalculated populations");
    // always first !
    initPopulationFromLastTurn(originalState);
    while (true) {
      currentPly++;
      if ((currentPly & 8 - 1) == 0 && System.currentTimeMillis() - Player.start > 40) {
        break;
      }
      doOnePly(originalState);
    }
    System.err.println("AG " + currentPly + " plies in " + (System.currentTimeMillis() - Player.start) + " ms");
    System.err.println("Best ply @ " + bestPlyaAt + " / " + currentPly + " with score : " + bestScore);
    bestTriaction.actions[0].updateFromAGValues(originalState.hero[0].pos, bestSolution.c[0][0].angle, bestSolution.c[0][0].speed);
    bestTriaction.actions[1].updateFromAGValues(originalState.hero[1].pos, bestSolution.c[0][1].angle, bestSolution.c[0][1].speed);
    return bestTriaction;
  }

  public void resetAG() {
    currentPly = 0;
    bestPlyaAt = 0;
    bestScore = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < POPULATION_SIZE; i++) {
      solutions[i].score = Double.NEGATIVE_INFINITY;
    }
  }

  public void initWindAllDirection(LightState originalState) {
    for (int i = SURVIVOR_SIZE; i < POPULATION_SIZE; i++) {
      solutions[i].random();
      if (random.nextDouble() > 0.5) {
        solutions[i].c[0][0].angle = random.nextInt(360);
        solutions[i].c[0][0].speed = -1;
      }
      if (random.nextDouble() > 0.5) {
        solutions[i].c[0][1].angle = random.nextInt(360);
        solutions[i].c[0][1].speed = -1;
      }
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }

  public void doOnePly(LightState originalState) {
    for (int i = SURVIVOR_SIZE; i < POPULATION_SIZE; i += 2) {
      AGSolution.merge(solutions[random.nextInt(SURVIVOR_SIZE)], solutions[random.nextInt(SURVIVOR_SIZE)], solutions[i], solutions[i + 1]);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
      state.copyFrom(originalState);
      solutions[i + 1].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }

  /**
   * SURVIVOR = same as last turn, decaled by one turn
   * remaing = informedRandom
   * @param originalState
   */
  /**
   * SURVIVOR = same as last turn, decaled by one turn
   * remaing = informedRandom
   * @param originalState
   */
  public void initPopulationFromLastTurn(LightState originalState) {
    for (int i = 0; i < SURVIVOR_SIZE; i++) {
      solutions[i].decalFromLastTurn();
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    for (int i = SURVIVOR_SIZE; i < POPULATION_SIZE; i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }

  public void initSpeedStraight(LightState originalState, int speed) {
    int steps = (int) Math.sqrt(POPULATION_SIZE - SURVIVOR_SIZE);
    int i = SURVIVOR_SIZE;
    for (int a0 = 0; a0 < steps; a0++) {
      solutions[i].dispatch(0, a0, steps, speed);
      for (int a1 = 0; a1 < steps; a1++) {
        solutions[i].dispatch(1, a1, steps, speed);
        state.copyFrom(originalState);
        solutions[i].applyOn(state, oppHeroesAction);
        i++;
      }
    }
    sortPopulationAndUpdateBest();
  }

  public void initSpeedStraightAndRandom(LightState originalState, int speed) {
    int steps = (int) Math.sqrt(POPULATION_SIZE - SURVIVOR_SIZE);
    int i = SURVIVOR_SIZE;
    for (int a0 = 0; a0 < steps; a0++) {
      for (int a1 = 0; a1 < steps; a1++) {
        solutions[i].dispatchOne(0, a0, steps, speed);
        solutions[i].dispatchOne(1, a1, steps, speed);
        state.copyFrom(originalState);
        solutions[i].applyOn(state, oppHeroesAction);
        i++;
      }
    }
    sortPopulationAndUpdateBest();
  }

  public void initRandomPopulation(LightState originalState) {
    for (int i = SURVIVOR_SIZE; i < POPULATION_SIZE; i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }

  public void initFullRandomPopulation(LightState originalState) {
    for (int i = 0; i < POPULATION_SIZE; i++) {
      solutions[i].informedRandom(information);
      state.copyFrom(originalState);
      solutions[i].applyOn(state, oppHeroesAction);
    }
    sortPopulationAndUpdateBest();
  }

  private void sortPopulationAndUpdateBest() {
    // should be enough (famous last words :) )
    for (int i = 0; i < POPULATION_SIZE; i++) {
      int max = i;
      double maxScore = solutions[i].score;
      for (int j = i + 1; j < POPULATION_SIZE; j++) {
        if (solutions[j].score > maxScore) {
          maxScore = solutions[j].score;
          max = j;
        }
      }
      if (max == i)
        continue;
      // swap
      AGSolution tmp = solutions[i];
      solutions[i] = solutions[max];
      solutions[max] = tmp;
    }
    if (solutions[0].score > bestScore) {
      bestScore = solutions[0].score;
      bestSolution.copyFrom(solutions[0]);
      bestPlyaAt = currentPly;
    }
  }
}

class AGEvaluator {

  private static final Pos[] PATROL = new Pos[] { Pos.get(6000, 2000), Pos.get(2757, 5500) };

  public double evaluate(LightState state) {
    double score = 0;
    score += 1_000_000 * state.health;
    // add something the simulator try to say (like a control )
    score += state.extraBonus;
    
    if (Player.turn < 40) {
      score += 200 * state.mana;
      score += 1000 * state.wildMana;
    } else {
      score += 1 * state.mana;
      score += 2 * state.wildMana;
    }    
    
    int mobsInBaseZoneCount = 0;
    int closestDist = Integer.MAX_VALUE;
    double relativeDists = 0;
    double healthValue = 0.0;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      int distToBase = unit.pos.fastDist(State.myBase);
      if (distToBase < State.BASE_TARGET_DIST) {
        mobsInBaseZoneCount++;
        closestDist = Math.min(closestDist, distToBase);
        int stepsToKillBase = Math.max(1, (distToBase - 300) / State.MOB_MOVE);
        int distToHero0 = unit.pos.fastDist(state.hero[0].pos);
        int distToHero1 = unit.pos.fastDist(state.hero[1].pos);
        
        relativeDists += Math.min(distToHero0, distToHero1) / stepsToKillBase;
        healthValue += Math.max(0, unit.health - 2 * stepsToKillBase);
      } else {
      }
    }
    score -= 2.0 * relativeDists;
    score -= 1.0 * healthValue;
    score -= 10 * mobsInBaseZoneCount;
    
    for (int i = 0; i < 3; i++) {
      Hero h = state.oppHeroes[i];
      if (h.isInFog()) continue;

      //int distToHero0 = h.pos.fastDist(state.hero[0].pos);
      //int distToHero1 = h.pos.fastDist(state.hero[1].pos);
      //score -= 0.1*Math.max(0,  State.CONTROL_RANGE - distToHero0);
      //score -= 0.1*Math.max(0,  State.CONTROL_RANGE - distToHero1 ); // avoid controls
      
      score -= 100.0 * Math.max(0, State.BASE_TARGET_DIST+3000 - h.pos.fastDist(State.myBase)); // pas plus loin que la base
      if (h.pos.fastDist(Player.DEFENSE) < 2000) {
        score -= 500.0 * Math.max(0, State.BASE_TARGET_DIST+3000 - h.pos.fastDist(State.myBase)); // pas plus loin que la base
      }
      
    }

    
    
    // aller voir le point en defense de temps en temps
    int bestDist = Integer.MAX_VALUE;
    for (int i = 0; i < 2; i++) {
      Hero h = state.hero[i];
      int distToDefense = h.pos.fastDist(Player.DEFENSE);
      if (distToDefense < bestDist) {
        bestDist = distToDefense;
      }
    }
    score -= 2 * bestDist * Player.lastSeenDefense;
    
    
    return score;
  }

  public double finalEval(LightState state) {
    Hero[] hero = state.hero;
    Hero[] oppHeroes = state.oppHeroes;
    double score = 0.0;
    int closestDist = Integer.MAX_VALUE;
    int mobsInBaseZoneCount = 0;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      int distToBase = unit.pos.dist(State.myBase);
      closestDist = Math.min(closestDist, distToBase);
      if (distToBase < State.BASE_TARGET_DIST) {
        mobsInBaseZoneCount++;
      }
    }
    if (mobsInBaseZoneCount > 0) {
      score -= 500 * mobsInBaseZoneCount;
      for (int i = 0; i < hero.length; i++) {
        Hero h = hero[i];
        score -= Math.max(0, h.pos.dist(State.myBase) - (closestDist));
      }
      int distToPatrol0 = Math.min(state.hero[0].pos.dist(PATROL[0]), state.hero[1].pos.dist(PATROL[0]));
      int distToPatrol1 = Math.min(state.hero[0].pos.dist(PATROL[1]), state.hero[1].pos.dist(PATROL[1]));
      score -= 0.001 * (distToPatrol0 + distToPatrol1);
    } else {
      // patrol ?
      int distToPatrol0 = Math.min(state.hero[0].pos.dist(PATROL[0]), state.hero[1].pos.dist(PATROL[0]));
      int distToPatrol1 = Math.min(state.hero[0].pos.dist(PATROL[1]), state.hero[1].pos.dist(PATROL[1]));
      score -= 0.001 * (distToPatrol0 + distToPatrol1);
    }
    for (int i = 0; i < oppHeroes.length; i++) {
      Hero h = oppHeroes[i];
      if (h.isInFog())
        continue;
      score += Math.max(0, h.pos.dist(State.myBase));
    }
    for (int i = 0; i < hero.length; i++) {
      Hero h = hero[i];
      score += -Math.max(0, h.pos.dist(State.myBase) - 8_000);
    }
    return score;
  }
}

class Chromosome {

  private static ThreadLocalRandom random = ThreadLocalRandom.current();

  public int angle;

  public int speed;

  public Chromosome() {
  }

  public Chromosome(int angle, int speed) {
    this.angle = angle;
    this.speed = speed;
  }

  public void copyFrom(Chromosome model) {
    this.angle = model.angle;
    this.speed = model.speed;
  }

  public void random() {
    angle = 5 * random.nextInt(360 / 5);
    speed = 25 * random.nextInt(32 + 1);
    if (random.nextDouble() > 0.95) {
      speed = -1;
      angle = -1;
    }
  // no control here
  }

  public void mutate(Chromosome model) {
    this.angle = model.angle;
    this.speed = model.speed;
    if (speed >= 0) {
      int deltaSpeed = 25 * (3 - random.nextInt(3 * 2));
      speed = Math.max(0, Math.min(800, speed + deltaSpeed));
    } else {
    // wind or control : don't change speed !
    }
    if (speed >= -10) {
      // move or wind
      int deltaAngle = 5 - random.nextInt(5 * 2);
      angle += deltaAngle;
      if (angle < 0)
        angle += 360;
      if (angle >= 360)
        angle -= 360;
    } else {
    // control : don't change angle (entityid)
    }
  }
}

class Simulator {

  public static final boolean ALLOW_WIND = true;

  public static final boolean ALLOW_CONTROL = true;

  public static void apply(LightState state, int d, TriAction actions) {
    apply(state, d, actions, null);
  }

  public static void apply(LightState state, int atDepth, TriAction myActions, TriAction oppActions) {
    Action[] actions = myActions.actions;
    resetAll(state);
    // do controls
    if (ALLOW_CONTROL) {
      for (int h = 0; h < 2; h++) {
        if (actions[h].type == Action.TYPE_CONTROL && state.mana >= 10) {
          state.mana -= 10;
          doControl(state, state.hero[h], actions[h].targetEntity, actions[h].target);
          // for the control, je ne sais pas comment le faire autrement ....
          state.extraBonus = 1_000_000;
        }
      }
    }
    // move heroes
    for (int h = 0; h < 2; h++) {
      if (actions[h].type == Action.TYPE_MOVE) {
        if (state.hero[h].dx == 0 && state.hero[h].dy == 0) {
          state.hero[h].pos.copyFrom(actions[h].target);
        }
      }
    }
    // damage
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      for (int h = 0; h < 2; h++) {
        if (unit.isInHitRange(state.hero[h])) {
          unit.health -= 2;
          state.mana += 2;
          if (!unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
            // MIAM
            state.wildMana += 2;
          }
        }
      }
      if (unit.health <= 0) {
        state.kill(u);
        // hack need to update the replaced unit
        u--;
      }
    }
    // do wind
    if (ALLOW_WIND) {
      for (int h = 0; h < 2; h++) {
        if (actions[h].type == Action.TYPE_WIND && state.mana >= 10) {
          state.mana -= 10;
          doWind(state, 0, state.hero[h].pos, actions[h].target);
        }
      }
      if (atDepth == 0 && oppActions != null) {
        for (int h = 0; h < 3; h++) {
          if (oppActions.actions[h].type == Action.TYPE_WIND) {
            doWind(state, 1, state.oppHeroes[h].pos, oppActions.actions[h].target);
          }
        }
      }
    }
    // move units
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      if (unit.isPushed) {
        // TODO Intersect with rect
        unit.pos.add(unit.dx, unit.dy);
      } else if (unit.controlTargetCount > 0) {
        double dx = unit.controlTarget.x / unit.controlTargetCount - unit.pos.x;
        double dy = unit.controlTarget.y / unit.controlTargetCount - unit.pos.y;
        double dist = (int) Math.sqrt(dx * dx + dy * dy);
        dx = dx * State.MOB_MOVE / dist;
        dy = dy * State.MOB_MOVE / dist;
        unit.pos.add(dx, dy);
        unit.speed.set((int) dx, (int) dy);
      } else {
        // normal move
        // TODO check if out of map
        unit.pos.add(unit.speed);
      }
      if (unit.isInRange(State.myBase, State.BASE_KILL_RADIUS)) {
        // do damage to base
        state.health--;
        unit.health = 0;
        state.kill(u);
        u--;
        continue;
      } else if (unit.isInRange(State.oppBase, State.BASE_KILL_RADIUS)) {
        unit.health = 0;
        state.kill(u);
        u--;
        continue;
      } else if (unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
        // reput in map
        unit.pos.addAndSnap(0, 0);
        unit.speed.alignTo(unit.pos, State.myBase, State.MOB_MOVE);
      } else if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
        // reput in map
        unit.pos.addAndSnap(0, 0);
        unit.speed.alignTo(unit.pos, State.oppBase, State.MOB_MOVE);
      } else {
        if (!unit.pos.insideOfMap()) {
          unit.health = 0;
          state.kill(u);
          u--;
          continue;
        }
      }
      // reset nextTarget !
      unit.controlTargetCount = unit.controlNextTargetCount;
      unit.controlTarget.copyFrom(unit.controlNextTarget);
      unit.controlNextTargetCount = 0;
      unit.controlNextTarget.set(0, 0);
    }
    // Compromis : on ne fait le push qu'a t = 0
    if (atDepth == 0) {
      // c'est tellemnt aléatoire après (déjà que la ....)
      for (int i = 0; i < 2; i++) {
        Hero h = state.hero[i];
        h.pos.addAndSnap(h.dx, h.dy);
      }
      Hero[] oppHeroes = state.oppHeroes;
      for (int i = 0; i < oppHeroes.length; i++) {
        Hero h = oppHeroes[i];
        h.pos.addAndSnap(h.dx, h.dy);
      }
    }
  }

  private static void resetAll(LightState state) {
    state.extraBonus = 0;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      unit.isPushed = false;
      unit.dx = 0;
      unit.dy = 0;
    }
    for (int i = 0; i < 2; i++) {
      Hero h = state.hero[i];
      h.dx = 0;
      h.dy = 0;
      h.isControlled = false;
    }
    for (int i = 0; i < 3; i++) {
      Hero h = state.oppHeroes[i];
      h.dx = 0;
      h.dy = 0;
      h.isControlled = false;
    }
  }

  private static void doControl(LightState state, Hero hero, int id, Pos target) {
    // on heroes
    Hero opp = null;
    for (Hero h : state.oppHeroes) {
      if (h.id != id)
        continue;
      if (!h.hasShield() && h.isInRange(hero, State.CONTROL_RANGE)) {
        opp = h;
      }
      // break in any case if id is found
      break;
    }
    if (opp != null) {
      double dx = target.x - opp.pos.x;
      double dy = target.y - opp.pos.y;
      double dist = (int) Math.sqrt(dx * dx + dy * dy);
      dx = dx * State.HERO_MAX_MOVE / dist;
      dy = dy * State.HERO_MAX_MOVE / dist;
      opp.pos.addAndSnap(dx, dy);
      return;
    }
    // on units
    Unit mob = null;
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      if (unit.id != id)
        continue;
      if (!unit.hasShield() && unit.isInRange(hero.pos, State.CONTROL_RANGE)) {
        mob = unit;
      }
      // break in any case if id is found
      break;
    }
    if (mob != null) {
      mob.controlNextTargetCount++;
      mob.controlNextTarget.add(target.x, target.y);
    }
  }

  private static void doWind(LightState state, int who, Pos pos, Pos target) {
    double dx = target.x - pos.x;
    double dy = target.y - pos.y;
    double dist = (int) Math.sqrt(dx * dx + dy * dy);
    dx = (int) (dx * State.WIND_PUSH_DISTANCE / dist);
    dy = (int) (dy * State.WIND_PUSH_DISTANCE / dist);
    for (int u = 0; u < state.unitsFE; u++) {
      Unit unit = state.units[u];
      if (unit.isInFog())
        continue;
      if (unit.hasShield())
        continue;
      if (unit.isInRange(pos, State.WIND_RANGE)) {
        unit.dx += dx;
        unit.dy += dy;
        unit.isPushed = true;
      }
    }
    if (who == 0) {
      // move him
      for (int i = 0; i < 3; i++) {
        if (state.oppHeroes[i].pos == Pos.VOID)
          continue;
        if (!state.oppHeroes[i].hasShield() && state.oppHeroes[i].isInRange(pos, State.WIND_RANGE)) {
          state.oppHeroes[i].dx += dx;
          state.oppHeroes[i].dy += dy;
        }
      }
    } else {
      // move me
      for (int i = 0; i < 2; i++) {
        if (!state.hero[i].hasShield() && state.hero[i].isInRange(pos, State.WIND_RANGE)) {
          state.hero[i].dx += dx;
          state.hero[i].dy += dy;
        }
      }
    }
  }
}

class AGSolution {

  private static ThreadLocalRandom random = ThreadLocalRandom.current();

  public static AGEvaluator evaluator = new AGEvaluator();

  private TriAction actions = new TriAction();

  public static final double[] cos = new double[360];

  public static final double[] sin = new double[360];

  static {
    for (int a = 0; a < 360; a++) {
      cos[a] = Math.cos(Math.PI * a / 180);
      sin[a] = Math.sin(Math.PI * a / 180);
    }
  }

  public static final double[] depthFactor = new double[AG.DEPTH];

  static {
    for (int i = 0; i < AG.DEPTH; i++) {
      depthFactor[i] = Math.pow(0.9, i);
    }
  }

  // 0 = angle, 1 = speed
  public Chromosome[][] c = new Chromosome[AG.DEPTH][2];

  {
    for (int i = 0; i < AG.DEPTH; i++) {
      for (int agent = 0; agent < 2; agent++) {
        c[i][agent] = new Chromosome();
      }
    }
  }

  public double score;

  public void random() {
    for (int i = 0; i < AG.DEPTH; i++) {
      for (int agent = 0; agent < 2; agent++) {
        c[i][agent].random();
        if (i < 3 && random.nextDouble() > 0.95) {
          c[i][agent].speed = -1;
          c[i][agent].angle = -1;
        }
      }
    }
  }

  public void dispatch(int agent, int d, int max, int speed) {
    for (int i = 0; i < AG.DEPTH; i++) {
      c[i][agent].angle = 360 * d / max;
      c[i][agent].speed = speed;
    }
  }

  public void dispatchOne(int agent, int d, int max, int speed) {
    c[0][agent].angle = 360 * d / max;
    c[0][agent].speed = speed;
    for (int i = 1; i < AG.DEPTH; i++) {
      c[i][agent].random();
    }
  }

  public void informedRandom(AGInformation information) {
    double pushThreshold = 0.97;
    int pushDephtThreshold = 3;
    if (information.deadlyDanger) {
      pushThreshold = 0.7;
      pushDephtThreshold = 1;
    } else if (information.oppAttacker != null) {
      pushThreshold = 0.9;
      pushDephtThreshold = AG.DEPTH;
    }
    for (int i = 0; i < AG.DEPTH; i++) {
      for (int agent = 0; agent < 2; agent++) {
        c[i][agent].random();
        if (i < pushDephtThreshold && random.nextDouble() > pushThreshold) {
          c[i][agent].speed = -1;
          c[i][agent].angle = -1;
        }
      }
    }
  /*
  if (information.controlAttacker) {
    // Tant pis on choisi de maniere fixe
    if (information.canControl[0]) {
    c[0][0].speed = -666;
    c[0][0].angle = information.unitToControl;
    } else if (information.canControl[1]) {
    c[0][1].speed = -666;
    c[0][1].angle = information.unitToControl;
    }
  }
  */
  }

  public void applyOn(LightState state, TriAction oppActions) {
    score = 0.0;
    // 1st step with the opp actions
    actions.actions[0].updateFromAGValues(state.hero[0].pos, c[0][0].angle, c[0][0].speed);
    actions.actions[1].updateFromAGValues(state.hero[1].pos, c[0][1].angle, c[0][1].speed);
    Simulator.apply(state, 0, actions, oppActions);
    score += depthFactor[0] * evaluator.evaluate(state);
    // remaining steps without opp actions
    for (int d = 1; d < AG.DEPTH; d++) {
      actions.actions[0].updateFromAGValues(state.hero[0].pos, c[d][0].angle, c[d][0].speed);
      actions.actions[1].updateFromAGValues(state.hero[1].pos, c[d][1].angle, c[d][1].speed);
      Simulator.apply(state, d, actions);
      score += depthFactor[d] * evaluator.evaluate(state);
    }
    //score += evaluator.finalEval(state);
  }

  public void copyFrom(AGSolution model) {
    for (int i = 0; i < AG.DEPTH; i++) {
      c[i][0].copyFrom(model.c[i][0]);
      c[i][1].copyFrom(model.c[i][1]);
      score = model.score;
    }
  }

  public static void merge(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double choice = random.nextDouble();
    if (choice < 0.5) {
      crossOver(sol1, sol2, dest1, dest2);
    } else if (choice < 0.6) {
      softRandom(sol1, sol2, dest1, dest2);
    } else if (choice < 0.62) {
      fullRandom(dest1, dest2);
    } else {
      // last choice
      copyAndMutate(sol1, sol2, dest1, dest2);
    }
  }

  private static void crossOverAndMutate(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    for (int i = 0; i < AG.DEPTH; i++) {
      if (random.nextDouble() > 0.9) {
        dest1.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > 0.9) {
        dest1.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest1.c[i][1].copyFrom(sol2.c[i][1]);
      }
      if (random.nextDouble() > 0.9) {
        dest2.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest2.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > 0.9) {
        dest2.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }

  private static void fullRandom(AGSolution dest1, AGSolution dest2) {
    dest1.random();
    dest2.random();
  }

  private static void crossOver(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    for (int i = 0; i < AG.DEPTH; i++) {
      dest1.c[i][0].copyFrom(sol1.c[i][0]);
      dest1.c[i][1].copyFrom(sol2.c[i][1]);
      dest2.c[i][0].copyFrom(sol2.c[i][0]);
      dest2.c[i][1].copyFrom(sol1.c[i][1]);
    }
  }

  private static void copyAndMutate(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double mutationThreshold = 0.90;
    for (int i = 0; i < AG.DEPTH; i++) {
      if (random.nextDouble() > mutationThreshold) {
        dest1.c[i][0].mutate(sol1.c[i][0]);
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest1.c[i][1].mutate(sol1.c[i][1]);
      } else {
        dest1.c[i][1].copyFrom(sol1.c[i][1]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest2.c[i][0].mutate(sol2.c[i][0]);
      } else {
        dest2.c[i][0].copyFrom(sol2.c[i][0]);
      }
      if (random.nextDouble() > mutationThreshold) {
        dest2.c[i][1].mutate(sol2.c[i][1]);
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }

  private static void softRandom(AGSolution sol1, AGSolution sol2, AGSolution dest1, AGSolution dest2) {
    double rerandomThreshold = 0.90;
    for (int i = 0; i < AG.DEPTH; i++) {
      if (random.nextDouble() > rerandomThreshold) {
        dest1.c[i][0].random();
      } else {
        dest1.c[i][0].copyFrom(sol1.c[i][0]);
      }
      if (random.nextDouble() > rerandomThreshold) {
        dest1.c[i][1].random();
      } else {
        dest1.c[i][1].copyFrom(sol1.c[i][1]);
      }
      if (random.nextDouble() > rerandomThreshold) {
        dest2.c[i][0].random();
      } else {
        dest2.c[i][0].copyFrom(sol2.c[i][0]);
      }
      if (random.nextDouble() > rerandomThreshold) {
        dest2.c[i][1].random();
      } else {
        dest2.c[i][1].copyFrom(sol2.c[i][1]);
      }
    }
  }

  public void decalFromLastTurn() {
    for (int i = 0; i < AG.DEPTH - 1; i++) {
      c[i][0].copyFrom(c[i + 1][0]);
      c[i][1].copyFrom(c[i + 1][1]);
    }
    c[AG.DEPTH - 1][0].random();
    c[AG.DEPTH - 1][1].random();
  }

  public void doWind(int id, int step, int maxStep) {
    c[0][id].speed = -1;
    c[0][id].angle = 360 * step / maxStep;
  }
}

class Action {

  public static final int TYPE_WAIT = 0;

  public static final int TYPE_MOVE = 1;

  public static final int TYPE_WIND = 2;

  public static final int TYPE_SHIELD = 3;

  public static final int TYPE_CONTROL = 4;

  public static final Action WAIT = new Action(TYPE_WAIT, Pos.VOID, -1);

  public int type;

  public final Pos target = new Pos();

  public int targetEntity;

  public Action() {
    type = TYPE_WAIT;
  }

  public Action(int type, Pos target, int id) {
    this.type = type;
    this.target.copyFrom(target);
    this.targetEntity = id;
  }

  public Action(int type, int x, int y, int id) {
    this.type = type;
    this.target.x = x;
    this.target.y = y;
    this.targetEntity = id;
  }

  @Override
  public String toString() {
    switch(type) {
      case TYPE_WAIT:
        return "WAIT";
      case TYPE_MOVE:
        return "MOVE " + target.output();
      case TYPE_WIND:
        return "SPELL WIND " + target.output();
      case TYPE_SHIELD:
        return "SPELL SHIELD " + targetEntity;
      case TYPE_CONTROL:
        return "SPELL CONTROL " + targetEntity + " " + target.output();
      default:
        return "NOT_IMPLEMENTED";
    }
  }

  public void moveTo(Pos target) {
    this.type = Action.TYPE_MOVE;
    this.target.copyFrom(target);
  }

  public void moveTo(int x, int y) {
    this.type = Action.TYPE_MOVE;
    this.target.x = x;
    this.target.y = y;
  }

  public void doWait() {
    this.type = TYPE_WAIT;
  }

  public void wind(Pos toward) {
    type = TYPE_WIND;
    target.copyFrom(toward);
  }

  public void control(int id, Pos toward) {
    type = TYPE_CONTROL;
    target.copyFrom(toward);
    targetEntity = id;
  }

  public void copyFrom(Action model) {
    this.type = model.type;
    this.target.copyFrom(model.target);
    this.targetEntity = model.targetEntity;
  }

  public void updateFromAGValues(Pos pos, int angle, int speed) {
    if (speed == 0) {
      this.copyFrom(WAIT);
    } else if (speed > 0) {
      this.type = TYPE_MOVE;
      this.target.x = Math.max(0, Math.min(State.WIDTH, pos.x + (int) (speed * AGSolution.cos[angle])));
      this.target.y = Math.max(0, Math.min(State.HEIGHT, pos.y + (int) (speed * AGSolution.sin[angle])));
    } else if (speed == -1) {
      this.type = TYPE_WIND;
      if (angle == -1) {
        this.target.x = State.oppBase.x;
        this.target.y = State.oppBase.y;
      } else {
        this.target.x = pos.x + (int) (1600 * AGSolution.cos[angle]);
        this.target.y = pos.y + (int) (1600 * AGSolution.sin[angle]);
      }
    } else if (speed <= -666) {
      this.type = TYPE_CONTROL;
      this.targetEntity = angle;
      this.target.copyFrom(State.oppBase);
    }
  }

  public static Action doMove(Pos pos) {
    return newAction(TYPE_MOVE, pos, -1);
  }

  public static Action doMove(int x, int y) {
    return newAction(TYPE_MOVE, new Pos(x, y), -1);
  }

  private static Action newAction(int type, Pos pos, int id) {
    Action action = ActionPool.get();
    action.type = type;
    action.target.copyFrom(pos);
    action.targetEntity = id;
    return action;
  }

  private static Action newAction(int type, int x, int y, int id) {
    Action action = ActionPool.get();
    action.type = type;
    action.target.x = x;
    action.target.y = y;
    action.targetEntity = id;
    return action;
  }

  public static Action doWind(Pos pos) {
    return newAction(TYPE_WIND, pos, -1);
  }

  public static Action doWind(int x, int y) {
    return newAction(TYPE_WIND, x, y, -1);
  }

  public static Action doShield(int id) {
    return newAction(TYPE_SHIELD, Pos.VOID, id);
  }

  public boolean isSpell() {
    return type == TYPE_WIND || type == TYPE_SHIELD || type == TYPE_CONTROL;
  }

  public static Action doControl(int id, Pos pos) {
    return newAction(TYPE_CONTROL, pos, id);
  }
}

class ActionPool {

  public static final int MAX = 10_000;

  private static Action[] pool = new Action[MAX];

  private static int poolFE = 0;

  static {
    for (int i = 0; i < MAX; i++) {
      pool[i] = new Action();
    }
  }

  public static void reset() {
    poolFE = 0;
  }

  public static Action get() {
    return pool[poolFE++];
  }
}

class DefenderSimpleAI {

  private static State state;

  private static Hero hero;

  static boolean enemyHasAttacker = false;

  public static void updateTurn(State state) {
    DefenderSimpleAI.state = state;
    detectEnemyAttacker();
  }

  public static Action defend(State state, Hero hero) {
    DefenderSimpleAI.state = state;
    DefenderSimpleAI.hero = hero;
    Action action;
    if ((action = shouldReturnToBase()) != Action.WAIT)
      return action;
    if ((action = shouldPanicWind()) != Action.WAIT)
      return action;
    if ((action = shouldRegularWind()) != Action.WAIT)
      return action;
    if ((action = attackClosestToBase()) != Action.WAIT)
      return action;
    if ((action = shouldPatrol()) != Action.WAIT)
      return action;
    return Action.WAIT;
  }

  private static Action shouldRegularWind() {
    if (state.mana[0] < 10)
      return Action.WAIT;
    int monsterInWindRange = 0;
    for (Unit monster : state.units) {
      if (monster.isDead())
        continue;
      int dist2Base = State.myBase.dist2(monster.pos);
      int dist2ToHero = hero.pos.dist2(monster.pos);
      if (dist2ToHero < State.WIND_RANGE2 && dist2Base < 5000 * 5000 && monster.shieldLife <= 0) {
        monsterInWindRange++;
      }
    }
    if (monsterInWindRange > 0) {
      // TODO do better angle ...
      return Action.doWind(State.oppBase);
    }
    return Action.WAIT;
  }

  private static void detectEnemyAttacker() {
    if (enemyHasAttacker) {
      return;
    }
    for (Hero hero : state.oppHeroes) {
      if (hero.isInRange(State.myBase, 8000)) {
        enemyHasAttacker = true;
      }
    }
  }

  private static Action shouldPatrol() {
    Action action = new Action();
    int decal = hero.id >= 3 ? (hero.id - 3) + 1 : hero.id + 1;
    int distX = (int) (6000 * Math.cos(decal * Math.PI * 25 / 180));
    int distY = (int) (6000 * Math.sin(decal * Math.PI * 25 / 180));
    action.moveTo(distX, distY);
    return action;
  }

  private static Action shouldReturnToBase() {
    if (hero.pos.dist(State.myBase) > 10_000) {
      return Action.doMove(State.myBase);
    }
    return Action.WAIT;
  }

  private static Action shouldPanicWind() {
    if (state.mana[0] < 10)
      return Action.WAIT;
    int monsterInWindRange = 0;
    for (Unit monster : state.units) {
      if (monster.isDead())
        continue;
      int dist2Base = State.myBase.dist2(monster.pos);
      int dist2ToHero = hero.pos.dist2(monster.pos);
      if (dist2ToHero < State.WIND_RANGE2 && dist2Base < 1000 * 1000 && monster.shieldLife <= 0) {
        monsterInWindRange++;
      }
    }
    if (monsterInWindRange > 0) {
      // TODO do better angle ...
      return Action.doWind(State.oppBase);
    }
    return Action.WAIT;
  }

  private static Action attackClosestToBase() {
    int bestDist = Integer.MAX_VALUE;
    Unit bestUnit = null;
    boolean isUnderThreat = hasOppHeroNearBase();
    for (Unit monster : state.units) {
      if (monster.isDead())
        continue;
      int dist2 = State.myBase.dist2(monster.pos);
      if (bestDist > dist2) {
        bestDist = dist2;
        bestUnit = monster;
      }
    }
    if (bestUnit != null) {
      if (!bestUnit.isInRange(State.myBase, State.BASE_TARGET_DIST + 2000) && bestUnit.health > 20) {
        System.err.println("Don't attack closest unit when too much health and out of base zone + 2000");
        return Action.WAIT;
      }
      // TODO find a better position ?
      return Action.doMove(bestUnit.pos);
    } else {
      return Action.WAIT;
    }
  }

  private static boolean hasOppHeroNearBase() {
    for (int h = 0; h < 3; h++) {
      if (state.oppHeroes[h].isInFog())
        continue;
      if (state.oppHeroes[h].isInRange(State.myBase, 6000)) {
        return true;
      }
    }
    return false;
  }
}

class Patroler {

  private static final AttackNearest ATTACK_NEAREST = new AttackNearest();

  private static final InitPatrol INIT_PATROL = new InitPatrol();

  private static State state;

  private static Hero hero;

  private static Pos[] targets = new Pos[] { new Pos(6500, 7000), new Pos(8000, 1750), new Pos(9000, 4500) };

  public static Action think(State state, Hero hero) {
    Patroler.state = state;
    Patroler.hero = hero;
    Action action;
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT)
      return action;
    if (Player.turn < 10) {
      if ((action = INIT_PATROL.think(state, hero)) != Action.WAIT)
        return action;
    }
    if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT)
      return action;
    if ((action = shouldReturnToBase()) != Action.WAIT)
      return action;
    if ((action = shouldPatrol()) != Action.WAIT)
      return action;
    return Action.WAIT;
  }

  private static Action shouldPatrol() {
    Action action = new Action();
    int offset = hero.id >= 3 ? (hero.id - 3) : hero.id;
    action.moveTo(targets[offset]);
    return action;
  }

  private static Action shouldReturnToBase() {
    return Action.WAIT;
  }
}

class SimpleAI {

  private static final boolean USE_DEFENSE_WHEN_WINNING = false;

  private State state = new State();

  private TriAction action = new TriAction();

  private AttackerV2 attacker = new AttackerV2();

  private DefenderSimpleAI defender = new DefenderSimpleAI();

  AG ag = new AG();

  public TriAction think(State originalState) {
    this.state.copyFrom(originalState);
    if (USE_DEFENSE_WHEN_WINNING && originalState.health[0] > originalState.health[1]) {
      Action defend = defender.defend(state, state.myHeroes[2]);
      action.actions[2].copyFrom(defend);
    } else {
      Action attack = attacker.attack(state, state.myHeroes[2]);
      action.actions[2].copyFrom(attack);
    }
    // let the defense use the mana in priority ... if (action.actions[2].isSpell()) state.mana[0]-=10;
    this.state.copyFrom(originalState);
    if (Player.turn <= 10) {
      // let the defense play without AG to explore (TODO in the evaluation !)
      action.actions[0].copyFrom(Patroler.think(state, state.myHeroes[0]));
      action.actions[1].copyFrom(Patroler.think(state, state.myHeroes[1]));
      // warmup AG
      TriAction best = ag.think(state);
    } else {
      // let the AG play
      TriAction best = ag.think(state);
      action.actions[0].copyFrom(best.actions[0]);
      action.actions[1].copyFrom(best.actions[1]);
    }
    
    // override if control is possible
    if (state.mana[0] >= 10) {
      for (int o=0;o<3;o++) {
        Hero opp = originalState.oppHeroes[o];
        if (!opp.isControlled && opp.pos.fastDist(Player.DEFENSE) < 2000) {
          System.err.println("Opp "+opp+" in DEFENSE");
          for (int h=0;h<3;h++) {
            Hero hero = originalState.myHeroes[h];
            if (hero.pos.fastDist(opp.pos) < State.WIND_RANGE) {
              System.err.println("Hero "+hero+" can defend !");
              action.actions[h].wind(new Pos(17630, 0));
              break;
            }
          }
        }
      }
      
    }
    
    
    
    return action;
  }

  private void debugControl() {
  // on opp heroes
  //  for (Hero h : state.oppHeroes) {
  //    if (h.isInRange(state.myHeroes[0], State.CONTROL_RANGE)) {
  //    action.actions[0].control(h.id, State.oppBase);
  //    break;
  //    }
  //  }
  // on mobs
  //  for (Unit unit : state.units) {
  //    if (Player.turn % 4 == 0 && unit.isInRange(state.myHeroes[0].pos, State.CONTROL_RANGE)) {
  //    action.actions[0].control(unit.id, State.oppBase);
  //    }
  //  }
  }
}

class Player {

  private static final int WARMUP_TIME = 600;

  public static boolean inversed = false;

  public static int turn = 0;

  public static long start;

  public State state = new State();

  public SimpleAI ai = new SimpleAI();

  public AG ag = new AG();

  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);
    new Player().play(in);
  }

  private void play(FastReader in) {
    readGlobal(in);
    while (true) {
      UnitPool.reset();
      ActionPool.reset();
      readTurn(in);
      think();
    }
  }

  private void think() {
    TriAction action = ai.think(state);
    action.output();
  }

  public void readGlobal(FastReader in) {
    state.readGlobal(in);
  }

  static final Pos DEFENSE = new Pos(6000,1000);
  public static int lastSeenDefense = 0;
  public void readTurn(FastReader in) {
    Player.turn++;
    state.read(in);
    
    lastSeenDefense++;
    for (int i=0;i<3;i++) {
      if (state.myHeroes[i].isInRange(DEFENSE, 2000)) {
        lastSeenDefense = 0;
      }
    }
    
    
    System.err.println("turn " + Integer.toString(turn));
    if (turn == 1) {
      Player.start = System.currentTimeMillis() + WARMUP_TIME;
    } else {
      Player.start = System.currentTimeMillis();
    }
  }
}
