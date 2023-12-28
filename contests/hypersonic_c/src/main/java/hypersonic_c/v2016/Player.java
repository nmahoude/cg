package hypersonic_c.v2016;

/*
Proudly built by org.ndx.codingame.simpleclass.Assembler on 2016-12-13T13:58:08.789+01:00[GMT+01:00]
@see https://github.com/Riduidel/codingame/tree/master/tooling/codingame-simpleclass-maven-plugin
*/
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

enum EntityType {

    PLAYER, BOMB, ITEM
}

class P {

    // maximum board
    static P[][] ps = new P[20][20];

    static {
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                ps[x][y] = new P(x, y);
            }
        }
    }

    public static P get(final int x, final int y) {
        return ps[x][y];
    }

    public final int x;

    public final int y;

    public P(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public int squareDistance(final P p) {
        return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
    }

    public int manhattanDistance(final P p) {
        return Math.abs(x - p.x) + Math.abs(y - p.y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final P other = (P) obj;
        return x == other.x && y == other.y;
    }
}

class Entity {

    public Board board;

    public int owner;

    public final EntityType entityType;

    public P position;

    public Entity(Board board, int owner, EntityType type, P position) {
        this.board = board;
        this.owner = owner;
        this.entityType = type;
        this.position = position;
    }
}

class Cache<T> {

    Deque<T> available = new ArrayDeque<>();

    public void retrocede(final T t) {
        available.push(t);
    }

    public void push(final T t) {
        available.push(t);
    }

    public final boolean isEmpty() {
        return available.isEmpty();
    }

    public T pop() {
        return available.pop();
    }

    public int size() {
        return available.size();
    }
}

class Bomb extends Entity {

    public static int DEFAULT_TIMER = 8;

    public static Cache<Bomb> cache = new Cache<>();

    static {
        for (int i = 0; i < 10000; i++) {
            cache.push(new Bomb(null, i, null, i, i));
        }
    }

    public int timer;

    public int range;

    private Bomb(final Board board, final int owner, final P position, final int timer, final int range) {
        super(board, owner, EntityType.BOMB, position);
        this.timer = timer;
        this.range = range;
    }

    public void explode() {
        board.explode(this);
    }

    public final void update() {
        timer--;
        if (timer == 0) {
            explode();
        }
    }

    public Bomb duplicate(final Board board) {
        Bomb b;
        if (cache.isEmpty()) {
            b = new Bomb(board, owner, position, timer, range);
        } else {
            b = cache.pop();
            b.board = board;
            b.owner = owner;
            b.position = position;
            b.timer = timer;
            b.range = range;
        }
        return b;
    }

    public static Bomb create(final Board board, final int owner, final P position, final int timer, final int range) {
        Bomb b;
        if (cache.isEmpty()) {
            b = new Bomb(board, owner, position, timer, range);
        } else {
            b = cache.pop();
            b.board = board;
            b.owner = owner;
            b.position = position;
            b.timer = timer;
            b.range = range;
        }
        return b;
    }
}

class Bomberman extends Entity {

    public static Cache<Bomberman> cache = new Cache<>();

    static {
        for (int i = 0; i < 10000; i++) {
            cache.push(new Bomberman(null, i, null, i, i));
        }
    }

    public int bombsLeft;

    public int currentRange;

    public boolean isDead = false;

    public double points = 0;

    public int bombCount = 0;

    public Bomberman(final Board board, final int owner, final P position, final int bombsLeft, final int currentRange) {
        super(board, owner, EntityType.PLAYER, position);
        this.bombsLeft = bombsLeft;
        this.currentRange = currentRange;
    }

    public void move(final P p) {
        if (board.canWalkOn(p)) {
            board.walkOn(this, p);
        }
    }

    public Bomberman duplicate(final Board board) {
        Bomberman b;
        if (cache.isEmpty()) {
            b = new Bomberman(board, owner, position, bombsLeft, currentRange);
        } else {
            b = cache.pop();
            b.board = board;
            b.owner = owner;
            b.position = position;
            b.bombsLeft = bombsLeft;
            b.currentRange = currentRange;
        }
        b.points = points;
        b.isDead = isDead;
        b.bombCount = bombCount;
        return b;
    }

    @Override
    public String toString() {
        return "Bomberman(" + owner + "): pos=" + position + " bLeft:" + bombsLeft + " cRange:" + currentRange + " isDead:" + isDead;
    }
}

class Item extends Entity {

    public static Cache<Item> cache = new Cache<>();

    static {
        for (int i = 0; i < 10000; i++) {
            cache.push(new Item(null, i, null, i, i));
        }
    }

    public int type;

    private Item(Board board, int owner, P position, int type, int ignored) {
        super(board, owner, EntityType.ITEM, position);
        this.type = type;
    }

    public Item duplicate(Board board) {
        Item i;
        if (cache.isEmpty()) {
            i = new Item(board, owner, position, type, 0);
        } else {
            i = cache.pop();
            i.board = board;
            i.owner = owner;
            i.position = position;
            i.type = type;
        }
        return i;
    }

    public static Item create(Board board, int owner, P position, int type, int unused) {
        Item i;
        if (cache.isEmpty()) {
            i = new Item(board, owner, position, type, 0);
        } else {
            i = cache.pop();
            i.board = board;
            i.owner = owner;
            i.position = position;
            i.type = type;
        }
        return i;
    }
}

class Board {

    public static Deque<Board> cache = new ArrayDeque<>();

    static {
        for (int i = 0; i < 10000; i++) {
            cache.push(new Board());
        }
    }

    public static void retrocede(final Board board) {
        for (final Bomb b : board.bombs) {
            Bomb.cache.retrocede(b);
        }
        for (final Bomberman b : board.players) {
            Bomberman.cache.retrocede(b);
        }
        for (final Item i : board.items) {
            Item.cache.retrocede(i);
        }
        cache.push(board);
    }

    public static final char EMPTY = '.';

    public static final char WALL = 'X';

    public static final char BOX = '0';

    public static final char BOX_1 = '1';

    public static final char BOX_2 = '2';

    public static final char ITEM_1 = 'l';

    public static final char ITEM_2 = 'k';

    public static final char BOMB = 'b';

    int rot[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    public int cells[];

    long totalBoxX = 0;

    long totalBoxY = 0;

    List<Bomb> bombs = new ArrayList<>();

    List<Item> items = new ArrayList<>();

    public List<Bomberman> players = new ArrayList<>();

    public Bomberman me;

    int destructedBox;

    int boxCount;

    public Board() {
        cells = new int[13 * 11];
    }

    private void clean() {
        bombs.clear();
        items.clear();
        players.clear();
        me = null;
        destructedBox = 0;
        boxCount = 0;
        totalBoxX = 0;
        totalBoxY = 0;
    // cells will be copied later
    }

    public Board duplicate() {
        Board board;
        if (cache.isEmpty()) {
            board = new Board();
        } else {
            board = cache.pop();
        }
        board.clean();
        board.boxCount = boxCount;
        board.totalBoxX = totalBoxX;
        board.totalBoxY = totalBoxY;
        for (final Bomb b : this.bombs) {
            board.bombs.add(b.duplicate(board));
        }
        for (final Item i : this.items) {
            board.items.add(i.duplicate(board));
        }
        for (final Bomberman b : this.players) {
            final Bomberman copy = b.duplicate(board);
            if (b == this.me) {
                board.me = copy;
            }
            board.players.add(copy);
        }
        System.arraycopy(cells, 0, board.cells, 0, 13 * 11);
        return board;
    }

    public void init() {
        bombs.clear();
        players.clear();
        items.clear();
        destructedBox = 0;
        boxCount = 0;
        totalBoxX = 0;
        totalBoxY = 0;
    }

    public void init(final int y, final String row) {
        for (int x = 0; x < 13; x++) {
            final char value = row.charAt(x);
            cells[x + 13 * y] = value;
            if (value == BOX || value == BOX_1 || value == BOX_2) {
                boxCount++;
                totalBoxX += x;
                totalBoxY += y;
            }
        }
    }

    public void simulate() {
        // simulate one turn
        final ListIterator<Bomb> ite = new ArrayList<>(bombs).listIterator();
        while (ite.hasNext()) {
            final Bomb b = ite.next();
            b.timer -= 1;
            if (b.timer == 0) {
                b.explode();
            }
        }
    }

    public void addBomb(final Bomb bomb) {
        bombs.add(bomb);
        cells[bomb.position.x + 13 * bomb.position.y] = BOMB;
    }

    public void explode(final Bomb bomb) {
        final List<Bomb> bombsToExplode = new ArrayList<>();
        final List<Bomb> explodedBombs = new ArrayList<>();
        bombsToExplode.add(bomb);
        while (!bombsToExplode.isEmpty()) {
            final Bomb b = bombsToExplode.remove(0);
            bombs.remove(b);
            explodedBombs.add(b);
            final Bomberman orginalBomberman = getBombermanWithId(b.owner);
            if (orginalBomberman != null) {
                orginalBomberman.bombsLeft += 1;
            }
            final int range = b.range;
            final P p = b.position;
            for (int r = 0; r < 4; r++) {
                final int dx = rot[r][0];
                final int dy = rot[r][1];
                for (int d = 1; d < range; d++) {
                    final int x = p.x + d * dx;
                    final int y = p.y + d * dy;
                    final boolean shouldStopExplosion = checkExplosion(bombsToExplode, orginalBomberman, x, y);
                    if (shouldStopExplosion) {
                        break;
                    }
                }
            }
        }
        for (final Bomb removeBombs : explodedBombs) {
            checkExplosion(bombsToExplode, null, removeBombs.position.x, removeBombs.position.y);
        }
    }

    private boolean checkExplosion(final List<Bomb> bombsToExplode, final Bomberman orginalBomberman, final int x, final int y) {
        if (isOnBoard(x, y)) {
            final int value = cells[x + 13 * y];
            if (value == WALL) {
                // stop explosion
                return true;
            }
            for (final Bomberman bomberman : players) {
                if (bomberman.position.equals(P.get(x, y))) {
                    bomberman.isDead = true;
                }
            }
            switch(value) {
                case BOX:
                    updatePoints(orginalBomberman, 1, x, y);
                    cells[x + 13 * y] = EMPTY;
                    return true;
                case BOX_1:
                    updatePoints(orginalBomberman, 1, x, y);
                    cells[x + 13 * y] = ITEM_1;
                    items.add(Item.create(this, 0, P.get(x, y), 1, 0));
                    return true;
                case BOX_2:
                    updatePoints(orginalBomberman, 1, x, y);
                    cells[x + 13 * y] = ITEM_2;
                    items.add(Item.create(this, 0, P.get(x, y), 2, 0));
                    return true;
                case ITEM_1:
                case ITEM_2:
                    cells[x + 13 * y] = EMPTY;
                    // stop explosion
                    return true;
                case BOMB:
                    final Bomb bombAt = getBombAt(x, y);
                    if (bombAt != null) {
                        bombsToExplode.add(bombAt);
                    }
                    // stop explosion
                    return true;
            }
        } else {
            // not on board
            return true;
        }
        return false;
    }

    private void updatePoints(final Bomberman orginalBomberman, final double points, int x, int y) {
        destructedBox++;
        boxCount--;
        totalBoxX -= x;
        totalBoxY -= y;
        if (orginalBomberman != null) {
            orginalBomberman.points += points;
        }
    }

    private Bomberman getBombermanWithId(final int owner) {
        for (final Bomberman b : players) {
            if (b.owner == owner) {
                return b;
            }
        }
        return null;
    }

    private Bomb getBombAt(final int x, final int y) {
        for (final Bomb b : bombs) {
            if (b.position.x == x && b.position.y == y) {
                return b;
            }
        }
        return null;
    }

    final boolean isOnBoard(final int x, final int y) {
        if (x < 0 || x > 12) {
            return false;
        }
        if (y < 0 || y > 10) {
            return false;
        }
        return true;
    }

    public void addPlayer(final Bomberman player) {
        players.add(player);
    }

    public void addItem(final Item item) {
        items.add(item);
        if (item.type == 1) {
            cells[item.position.x + 13 * item.position.y] = ITEM_1;
        } else {
            cells[item.position.x + 13 * item.position.y] = ITEM_2;
        }
    }

    public boolean canMoveTo(final int x, final int y) {
        if (!isOnBoard(x, y)) {
            return false;
        }
        final int value = cells[x + 13 * y];
        return value == EMPTY || value == ITEM_1 || value == ITEM_2;
    }

    public void walkOn(final Bomberman player, final P p) {
        final int value = cells[p.x + 13 * p.y];
        if (value == ITEM_1) {
            player.currentRange += 1;
        } else if (value == ITEM_2) {
            player.bombsLeft += 1;
        }
        player.position = p;
        cells[p.x + 13 * p.y] = EMPTY;
    }

    public boolean canWalkOn(final P p) {
        final int value = cells[p.x + 13 * p.y];
        return value != Board.WALL && value != Board.BOX && value != Board.BOX_1 && value != Board.BOX_2 && value != Board.BOMB;
    }

    public String getDebugString() {
        String output = "";
        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 13; x++) {
                output += (char) (cells[y * 13 + x]);
            }
            output += "\n";
        }
        return output;
    }
}

enum Move {

    UP(" ↑"), LEFT(" ←"), RIGHT(" →"), DOWN(" ↓"), STAY(" •"), UP_BOMB("☢↑"), LEFT_BOMB("☢←"), RIGHT_BOMB("☢→"), DOWN_BOMB("☢↓"), STAY_BOMB("☢•");

    String name;

    Move(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

class Simulation {

    public static final double DEAD_MALUS = -999_999;

    private static final int BOX_BONUS = 8;

    public Board board;

    private double myPreviousPoints;

    public final double getScoreHeuristic() {
        if (board.me.isDead) {
            return DEAD_MALUS;
        }
        if (board.boxCount > 0) {
            return 8 * (board.me.points - myPreviousPoints) + //- 0.1*distanceToBoxGravityCenter()
            0.01 * board.me.bombsLeft + 0.01 * Math.max(board.me.bombCount, 10) + 0.01 * Math.max(board.me.currentRange, 10);
        } else {
            return 13 - board.me.position.manhattanDistance(P.get(7, 5));
        }
    }

    private double distanceToBoxGravityCenter() {
        int x = (int) (board.totalBoxX / board.boxCount);
        int y = (int) (board.totalBoxY / board.boxCount);
        int dist = board.me.position.manhattanDistance(P.get(x, y));
        return dist;
    }

    public final boolean isFinished() {
        return false;
    }

    public List<Move> getPossibleMoves() {
        final List<Move> moves = new ArrayList<>();
        for (final Move move : Move.values()) {
            if (isMovePossible(move)) {
                moves.add(move);
            }
        }
        return moves;
    }

    private boolean horizontallyPossible(int x, int y) {
        return (!(y % 2 == 1 && x % 2 == 0));
    }

    private boolean verticallyPossible(int x, int y) {
        return (!(y % 2 == 0 && x % 2 == 1));
    }

    public final boolean isMovePossible(final Move move) {
        final Bomberman me = board.me;
        int x = me.position.x;
        int y = me.position.y;
        switch(move) {
            case DOWN:
                return verticallyPossible(x, y) && board.canMoveTo(x, y + 1);
            case DOWN_BOMB:
                return me.bombsLeft > 0 && verticallyPossible(x, y) && board.canMoveTo(x, y + 1);
            case LEFT:
                return horizontallyPossible(x, y) && board.canMoveTo(x - 1, y);
            case LEFT_BOMB:
                return me.bombsLeft > 0 && horizontallyPossible(x, y) && board.canMoveTo(x - 1, y);
            case RIGHT:
                return horizontallyPossible(x, y) && board.canMoveTo(x + 1, y);
            case RIGHT_BOMB:
                return me.bombsLeft > 0 && horizontallyPossible(x, y) && board.canMoveTo(x + 1, y);
            case STAY:
                return true;
            case STAY_BOMB:
                return me.bombsLeft > 0 && true;
            case UP:
                return verticallyPossible(x, y) && board.canMoveTo(x, y - 1);
            case UP_BOMB:
                return me.bombsLeft > 0 && verticallyPossible(x, y) && board.canMoveTo(x, y - 1);
            default:
                return false;
        }
    }

    public final void copyFrom(final Simulation simulation) {
        board = simulation.board.duplicate();
    }

    public final void simulate(final Move move) {
        myPreviousPoints = board.me.points;
        board.destructedBox = 0;
        final List<Bomb> bombs = new ArrayList<>(board.bombs);
        for (final Bomb bomb : bombs) {
            bomb.update();
        }
        simulateMove(move);
    }

    private void simulateMove(final Move move) {
        int newX = board.me.position.x;
        int newY = board.me.position.y;
        boolean dropBomb = false;
        switch(move) {
            case DOWN_BOMB:
                dropBomb = true;
            case DOWN:
                newY += 1;
                break;
            case LEFT_BOMB:
                dropBomb = true;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT_BOMB:
                dropBomb = true;
            case RIGHT:
                newX += 1;
                break;
            case STAY_BOMB:
                dropBomb = true;
            case STAY:
                break;
            case UP_BOMB:
                dropBomb = true;
            case UP:
                newY -= 1;
        }
        if (dropBomb) {
            board.addBomb(Bomb.create(board, board.me.owner, board.me.position, 8, board.me.currentRange));
            board.me.bombsLeft -= 1;
        }
        if (board.canWalkOn(P.get(newX, newY)) && (newX != board.me.position.x || newY != board.me.position.y)) {
            board.walkOn(board.me, P.get(newX, newY));
        }
    }
}

class FastRand {

    int g_seed;

    public FastRand(int seed) {
        g_seed = seed;
    }

    public void fast_srand(int seed) {
        //Seed the generator
        g_seed = seed;
    }

    public int fastrand() {
        //fastrand routine returns one integer, similar output value range as C lib.
        g_seed = (214013 * g_seed + 2531011);
        return (g_seed >> 16) & 0x7FFF;
    }

    public int fastRandInt(int maxSize) {
        return fastrand() % maxSize;
    }

    public int fastRandInt(int a, int b) {
        return (a + fastRandInt(b - a));
    }

    public double fastRandDouble() {
        return (fastrand()) / 0x7FFF;
    }

    public double fastRandDouble(double a, double b) {
        return a + ((fastrand()) / 0x7FFF) * (b - a);
    }
}

class AGSolution {

    private static FastRand random = new FastRand(43);

    public static int MOVE_COUNT = 12;

    static double[] pow;

    static {
        pow = new double[MOVE_COUNT];
        for (int i = 0; i < MOVE_COUNT; i++) {
            pow[i] = Math.pow(0.9, i);
        }
    }

    public Move[] keys = new Move[MOVE_COUNT];

    public double energy = 0;

    private int mutateNextGenAt = 0;

    public void reset() {
        energy = 0;
        mutateNextGenAt = 0;
    }

    public void play(Simulation simulation) {
        //    printMoves();
        energy = 0;
        for (int i = 0; i < MOVE_COUNT; i++) {
            if (i >= mutateNextGenAt) {
                List<Move> possibleMoves = simulation.getPossibleMoves();
                keys[i] = possibleMoves.get(random.fastRandInt(possibleMoves.size()));
            } else {
            // keep old move
            }
            simulation.simulate(keys[i]);
            if (simulation.board.me.isDead) {
                energy = -1000.0 * (MOVE_COUNT - i);
                // dead
                return;
            } else {
                energy += pow[i] * simulation.getScoreHeuristic();
            }
            if (i == 0 && keys[i].ordinal() > 3) {
                putPotentialAdverserialBombs(simulation);
            }
        }
    }

    private void putPotentialAdverserialBombs(Simulation simulation) {
        for (Bomberman b : simulation.board.players) {
            if (b == simulation.board.me)
                continue;
            if (!b.isDead && b.position.manhattanDistance(simulation.board.me.position) < 3 && b.bombsLeft > 0) {
                simulation.board.addBomb(Bomb.create(simulation.board, b.owner, b.position, 8, b.currentRange));
            }
        }
    }

    private void printMoves() {
        System.err.println("Moves :");
        for (int i = 0; i < MOVE_COUNT; i++) {
            System.err.print(keys[i]);
        }
        System.err.println("");
    }

    public void copyFrom(AGSolution agSolution) {
        for (int i = 0; i < MOVE_COUNT; i++) {
            this.keys[i] = agSolution.keys[i];
        }
    }

    public static void mutate(AGSolution child, AGSolution parent) {
        child.mutateNextGenAt = random.fastRandInt(MOVE_COUNT);
        child.energy = 0;
        for (int i = 0; i < MOVE_COUNT; i++) {
            child.keys[i] = parent.keys[i];
        }
    }
}

class AG {

    private static final int POPULATION_COUNT = 200;

    AGSolution bestSolution = new AGSolution();

    AGSolution populations[] = new AGSolution[POPULATION_COUNT];

    AGSolution populations2[] = new AGSolution[POPULATION_COUNT];

    {
        for (int i = 0; i < POPULATION_COUNT; i++) {
            populations[i] = new AGSolution();
            populations2[i] = new AGSolution();
        }
    }

    FastRand rand = new FastRand(213135453);

    private int generations;

    private double bestScore;

    private Board originalBoard;

    private int bestGen;

    public void simulate(long start, Board board, AGSolution lastBestSolution) {
        originalBoard = board;
        bestScore = Double.NEGATIVE_INFINITY;
        generations = 0;
        generationInitialPopulation();
        do {
            playPopulation();
            sortPopulationOnEnergy();
            newChampionFightAgainstElder();
            mutateAndCrossOverPopulation(bestSolution);
        } while (System.nanoTime() - start < 90_000_000);
        System.err.println("AG-generations : " + generations);
        System.err.println("AG-best from " + bestGen + " generation");
    }

    private void generationInitialPopulation() {
    }

    private void mutateAndCrossOverPopulation(AGSolution bestSolution2) {
        for (int i = 0; i < POPULATION_COUNT; i++) {
            int individu1 = rand.fastRandInt(POPULATION_COUNT);
            for (int p = 0; p < 3; p++) {
                int pop = rand.fastRandInt(POPULATION_COUNT);
                if (populations[pop].energy > populations[individu1].energy) {
                    individu1 = pop;
                }
            }
            AGSolution.mutate(populations2[i], populations[individu1]);
        }
        swapPopulations();
    }

    private void swapPopulations() {
        AGSolution[] temp = populations2;
        populations2 = populations;
        populations = temp;
    }

    private void randomPopulate(AGSolution[] populations3) {
        for (int i = 0; i < POPULATION_COUNT; i++) {
            populations[i].reset();
        }
    }

    private void playPopulation() {
        generations++;
        //System.err.println("New generation  : "+generations);
        Simulation simulation = new Simulation();
        for (int i = 0; i < POPULATION_COUNT; i++) {
            simulation.board = originalBoard.duplicate();
            populations[i].play(simulation);
        }
    }

    private void sortPopulationOnEnergy() {
        Arrays.sort(populations, new Comparator<AGSolution>() {

            @Override
            public int compare(AGSolution o1, AGSolution o2) {
                return Double.compare(o2.energy, o1.energy);
            }
        });
    }

    private void newChampionFightAgainstElder() {
        if (populations[0].energy > bestScore) {
            bestGen = generations;
            bestScore = populations[0].energy;
            bestSolution.copyFrom(populations[0]);
        }
    }

    public Move findNextBestMove() {
        return bestSolution.keys[0];
    }
}

class Node {

    public static Cache<Node> cache = new Cache<>();

    static {
        for (int i = 0; i < 10000; i++) {
            cache.push(new Node());
        }
    }

    // state
    int depth = 0;

    int count = 0;

    Simulation simulation = new Simulation();

    Map<Move, Node> childs = new HashMap<>();

    List<Move> moves;

    Move move;

    public double getBestScore() {
        if (childs.isEmpty()) {
            return getScore();
        } else {
            double bestScore = Integer.MIN_VALUE;
            for (final Node child : childs.values()) {
                final double score = child.getBestScore();
                if (bestScore < score) {
                    bestScore = score;
                }
            }
            return getScore() + bestScore;
        }
    }

    double getScore() {
        return simulation.getScoreHeuristic();
    }

    public void simulate(final int depth) {
        count++;
        this.depth = depth;
        if (depth == MonteCarlo.MAX_DEPTH) {
            return;
        }
        final Move nextMove = findPossibleRandomMove();
        //    System.err.println("depth = "+(remainingDepth));
        //    System.err.println("me : "+simulation.board.me);
        //    System.err.println("choosing move : "+move+ " from "+moves.toString());
        Node child = childs.get(nextMove);
        if (child == null) {
            child = createChild(nextMove);
        }
        if (child.simulation.board.me.isDead) {
            return;
        }
        child.simulate(depth + 1);
    }

    private void simulateWorstCaseScenario(final Simulation simulation) {
        for (final Bomberman bomberman : simulation.board.players) {
            if (bomberman != simulation.board.me && bomberman.bombsLeft > 0 && bomberman.position.manhattanDistance(simulation.board.me.position) < 3) {
                simulation.board.addBomb(Bomb.create(simulation.board, bomberman.owner, bomberman.position, Bomb.DEFAULT_TIMER, bomberman.currentRange));
            }
        }
    }

    private Node createChild(final Move nextMove) {
        Node child;
        if (cache.isEmpty()) {
            child = new Node();
        } else {
            child = cache.pop();
        }
        child.move = nextMove;
        child.simulation.copyFrom(this.simulation);
        if (depth == 0) {
            simulateWorstCaseScenario(child.simulation);
        }
        child.simulation.simulate(nextMove);
        childs.put(nextMove, child);
        return child;
    }

    private Move findPossibleRandomMove() {
        if (moves == null) {
            moves = simulation.getPossibleMoves();
        }
        final int choice = ThreadLocalRandom.current().nextInt(moves.size());
        final Move nextMove = moves.get(choice);
        return nextMove;
    }

    public void retrocedRoot() {
        for (final Node child : childs.values()) {
            child.retroced();
        }
    }

    public void retroced() {
        if (simulation.board != null) {
            Board.retrocede(simulation.board);
        }
        for (final Node child : childs.values()) {
            child.retroced();
        }
        clear();
        cache.retrocede(this);
    }

    public void clear() {
        childs.clear();
        moves = null;
        move = null;
        count = 0;
    }

    public void getNodeList(final List<Node> nodes) {
        if (childs.isEmpty()) {
            return;
        } else {
            double bestScore = Integer.MIN_VALUE;
            Node bestNode = null;
            for (final Node child : childs.values()) {
                final double score = child.getBestScore();
                if (bestScore < score) {
                    bestScore = score;
                    bestNode = child;
                }
            }
            nodes.add(bestNode);
            bestNode.getNodeList(nodes);
        }
    }
}

class MonteCarlo {

    private static final int SIMULATION_COUNT = 2_200;

    public static final int MAX_DEPTH = 10;

    public Node root = new Node();

    public void init() {
        root = new Node();
    }

    public void simulate(long duration, long startTime, final Simulation simulation) {
        root.clear();
        root.simulation = new Simulation();
        root.simulation.copyFrom(simulation);
        do {
            root.simulate(0);
        } while (System.nanoTime() - startTime < duration);
    }

    public Move simulateBeam(final Simulation simulation) {
        final Simulation sim = new Simulation();
        double bestScore = Integer.MAX_VALUE;
        Move bestMove = null;
        final List<Move> chain = new ArrayList<>(MAX_DEPTH);
        for (int i = SIMULATION_COUNT; i >= 0; i--) {
            sim.copyFrom(simulation);
            chain.clear();
            for (int depth = 0; depth < MAX_DEPTH; depth++) {
                final List<Move> moves = simulation.getPossibleMoves();
                final int choice = ThreadLocalRandom.current().nextInt(moves.size());
                final Move nextMove = moves.get(choice);
                chain.add(nextMove);
                sim.simulate(nextMove);
            }
            final double score = sim.getScoreHeuristic();
            if (score > bestScore) {
                bestScore = score;
                bestMove = chain.get(0);
            }
        }
        return bestMove;
    }

    public Move findNextBestMove() {
        double bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (final Entry<Move, Node> entry : root.childs.entrySet()) {
            final double score = entry.getValue().getBestScore();
            System.err.println("-----");
            System.err.println("[" + entry.getKey() + "] (" + entry.getValue().count + " sim) with score of " + entry.getValue().getScore() + ", total: " + score);
            debugBestMove(entry.getValue());
            if (score > bestScore) {
                bestScore = score;
                bestMove = entry.getKey();
            }
        }
        //System.err.println("Choosen move : "+bestMove);
        return bestMove;
    }

    // try to explain why certain choices
    private void debugBestMove(final Node n) {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(n);
        n.getNodeList(nodes);
        System.err.print("Path : ");
        int count = 0;
        for (final Node node : nodes) {
            System.err.print("" + node.move + "(" + /*+node.simulation.board.me.position+","*/
            node.getScore() + ")");
            count++;
            if (count >= 20) {
                count = 0;
                System.err.println("");
                break;
            }
        }
        System.err.println("");
    }

    void debugAllMoves(final Node n) {
        final List<Node> nodes = new ArrayList<>();
        nodes.add(n);
        n.getNodeList(nodes);
        System.out.print("Path : ");
        int count = 0;
        for (final Node node : nodes) {
            System.out.print("" + node.move + "(" + /*+node.simulation.board.me.position+","*/
            node.getScore() + ")");
            count++;
            if (count >= 20) {
                count = 0;
                System.out.println("");
                break;
            }
        }
        System.out.println("");
    }
}

class Player {

    Board board = new Board();

    private long startTime;

    private static Scanner in;

    private static int myId;

    void play() {
        final Simulation sim = new Simulation();
        sim.board = board;
        final MonteCarlo mc = new MonteCarlo();
        while (true) {
            getSimulationState();
            AG ag = new AG();
            ag.simulate(startTime, board, null);
            final Move move = ag.findNextBestMove();
            outputMove(board.me, move);
        }
    }

    private void outputMove(final Bomberman me, final Move move) {
        int newX = board.me.position.x;
        int newY = board.me.position.y;
        boolean dropBomb = false;
        switch(move) {
            case DOWN_BOMB:
                dropBomb = true;
            case DOWN:
                newY += 1;
                break;
            case LEFT_BOMB:
                dropBomb = true;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT_BOMB:
                dropBomb = true;
            case RIGHT:
                newX += 1;
                break;
            case STAY_BOMB:
                dropBomb = true;
            case STAY:
                break;
            case UP_BOMB:
                dropBomb = true;
            case UP:
                newY -= 1;
        }
        if (dropBomb) {
            System.out.println("BOMB " + newX + " " + newY);
        } else {
            System.out.println("MOVE " + newX + " " + newY);
        }
    }

    private void getSimulationState() {
        initBoard();
        initEntities();
    }

    private void initEntities() {
        final int bombsOnBoard[] = new int[4];
        final int entities = in.nextInt();
        startTime = System.nanoTime();
        for (int i = 0; i < entities; i++) {
            final int entityType = in.nextInt();
            final int owner = in.nextInt();
            final int x = in.nextInt();
            final int y = in.nextInt();
            final int param1 = in.nextInt();
            final int param2 = in.nextInt();
            if (entityType == 0) {
                final Bomberman player = new Bomberman(board, owner, P.get(x, y), param1, param2);
                board.addPlayer(player);
                if (player.owner == myId) {
                    board.me = player;
                }
            } else if (entityType == 1) {
                final Bomb bomb = Bomb.create(board, owner, P.get(x, y), param1, param2);
                board.addBomb(bomb);
                bombsOnBoard[owner] += 1;
            } else if (entityType == 2) {
                final Item item = Item.create(board, owner, P.get(x, y), param1, param2);
                board.addItem(item);
            }
        }
        // update bombsCount
        for (final Bomberman b : board.players) {
            b.bombCount = b.bombsLeft + bombsOnBoard[b.owner];
        }
    //System.err.println("ME == pos: "+board.me.position+" bLeft: "+board.me.bombsLeft+ "/"+board.me.bombCount+" - range:"+board.me.currentRange);
    }

    private void initBoard() {
        board.init();
        for (int y = 0; y < 11; y++) {
            final String row = in.next();
            board.init(y, row);
        }
    }

    public static void main(final String args[]) {
        in = new Scanner(System.in);
        final int width = in.nextInt();
        final int height = in.nextInt();
        myId = in.nextInt();
        final Player p = new Player();
        p.play();
    }
}

