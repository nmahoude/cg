/*
Proudly built by org.ndx.codingame.simpleclass.Assembler on 2019-05-26T21:43:25.369+02:00[Europe/Paris]
@see https://github.com/Riduidel/codingame/tree/master/tooling/codingame-simpleclass-maven-plugin
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Dir {

    UP(0, 0, -1), DOWN(1, 0, 1), LEFT(2, -1, 0), RIGHT(3, 1, 0);

    private static List<Dir> dirs = Stream.of(UP, DOWN, LEFT, RIGHT).collect(Collectors.toList());

    public final int dx;

    public final int dy;

    public final int decal;

    Dir(int decal, int dx, int dy) {
        this.decal = decal;
        this.dx = dx;
        this.dy = dy;
    }

    public static List<Dir> randomValues() {
        Collections.shuffle(dirs);
        return dirs;
    }
}

class Pos {

    public static final Pos INVALID = new Pos(-1, -1);

    static Pos positions[] = new Pos[12 * 12];

    public final int x;

    public final int y;

    public final int index;

    static {
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                positions[x + y * 12] = new Pos(x, y);
            }
        }
    }

    private Pos(int x, int y) {
        this.x = x;
        this.y = y;
        this.index = x + 12 * y;
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", x, y);
    }

    public static Pos get(int x, int y) {
        if (x < 0 || x >= 12 || y < 0 || y >= 12) {
            return INVALID;
        }
        return positions[x + y * 12];
    }

    public Pos move(Dir dir) {
        return get(this.x + dir.dx, this.y + dir.dy);
    }

    public boolean isValid() {
        return x != -1;
    }

    public int manhattan(Pos next) {
        // TODO CACHE ?
        return Math.abs(this.x - next.x) + Math.abs(this.y - next.y);
    }
}

class Agent {

    public Pos HQ;

    public int gold;

    public int income;

    public int unitsCount;

    Agent(Pos HQ) {
        this.HQ = HQ;
    }

    public void read(Scanner in) {
        unitsCount = 0;
        gold = in.nextInt();
        income = in.nextInt();
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println(String.format("%d %d", gold, income));
        }
    }
}

enum UnitType {

    SOLDIER_1(1, 2), SOLDIER_2(2, 3), SOLDIER_3(3, 3), HQ(-1, 1), MINE(-1, 1), TOWER(-1, 3);

    public final int neededLevelToKill;

    public final int level;

    private UnitType(int level, int neededLevelToKill) {
        this.level = level;
        this.neededLevelToKill = neededLevelToKill;
    }

    public static UnitType getFromLevel(int lvl) {
        switch(lvl) {
            case 1:
                return SOLDIER_1;
            case 2:
                return SOLDIER_2;
            case 3:
                return SOLDIER_3;
            default:
                throw new RuntimeException("Unknown solder lvl : " + lvl);
        }
    }

    public static UnitType getFromBuilding(int value) {
        switch(value) {
            case 0:
                return HQ;
            case 1:
                return MINE;
            case 2:
                return TOWER;
            default:
                throw new RuntimeException("Unknown building type : " + value);
        }
    }
}

class Unit {

    public static final int TOWER_COST = 15;

    public static final int SOLDIER_1_COST = 10;

    public int owner;

    public Pos pos;

    public int id;

    public UnitType type;

    // action already done this turn
    public boolean done;

    public int degreeOfFreedom;

    public boolean dead;

    @Override
    public String toString() {
        return "Unit (" + id + ") at " + pos;
    }

    public void readSoldier(Scanner in) {
        owner = in.nextInt();
        id = in.nextInt();
        int level = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        pos = Pos.get(x, y);
        type = UnitType.getFromLevel(level);
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println(String.format("%d %d %d %d %d", owner, id, level, x, y));
        }
    }

    public void readBuilding(Scanner in) {
        owner = in.nextInt();
        id = -1;
        int bt = in.nextInt();
        type = UnitType.getFromBuilding(bt);
        int x = in.nextInt();
        int y = in.nextInt();
        pos = Pos.get(x, y);
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println(String.format("%d %d %d %d", owner, bt, x, y));
        }
    }

    public boolean canKill(Unit other) {
        return other.canBeKilledBy(this);
    }

    public boolean isStatic() {
        return type == UnitType.HQ || type == UnitType.MINE || type == UnitType.TOWER;
    }

    public boolean canBeKilledBy(Unit unit) {
        return type.neededLevelToKill <= unit.type.level;
    }

    public boolean canWalkOn(Cell cell) {
        // TODO maybe check it is not void ?
        if (this.type == UnitType.SOLDIER_3)
            return true;
        if (cell.unit == null)
            return true;
        if (cell.unit.canBeKilledBy(this))
            return true;
        for (Cell neighbor : cell.neighbors) {
            if (neighbor.unit != null && neighbor.unit.type == UnitType.TOWER && neighbor.isActive() == true && neighbor.unit.owner != this.owner) {
                return false;
            }
        }
        return false;
    }
}

class Cell {

    public static final Cell VOID = new Cell(Pos.INVALID);

    public final Pos pos;

    public final Cell neighbors[] = new Cell[4];

    public Unit unit;

    private int statut;

    // is the cell cut
    public boolean cut;

    public int threat = 0;

    public int cutValue = 0;

    public Cell(Pos pos) {
        this.pos = pos;
        if (pos == Pos.INVALID) {
            statut = Board.VOID;
        }
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public int getStatut() {
        return statut;
    }

    public boolean isActive() {
        return statut == Board.P0_ACTIVE || statut == Board.P1_ACTIVE;
    }

    public void initCell(Cell[] cells) {
        for (Dir dir : Dir.values()) {
            Pos n = pos.move(dir);
            if (!n.isValid() || cells[n.index] == Cell.VOID) {
                neighbors[dir.decal] = Cell.VOID;
            } else {
                neighbors[dir.decal] = cells[n.index];
            }
        }
    }

    public boolean isProtectedByTower() {
        for (Cell neighbor : neighbors) {
            if (neighbor != Cell.VOID && neighbor.isActive() && neighbor.unit != null && neighbor.unit.type == UnitType.TOWER && neighbor.unit.owner == neighbor.owner()) {
                return true;
            }
        }
        return false;
    }

    private int owner() {
        if (statut == Board.P0_ACTIVE)
            return 0;
        if (statut == Board.P1_ACTIVE)
            return 1;
        return -1;
    }

    public int calculateThreatScore() {
        threat = 0;
        for (Cell c : neighbors) {
            if (c == VOID)
                continue;
            if (c.getStatut() == Board.EMPTY) {
                threat += 1;
            }
            //      }
            if (c.getStatut() == Board.P1_ACTIVE) {
                threat += 10;
                if (c.unit != null) {
                    threat += 100;
                }
            }
        }
        return threat;
    }

    @Override
    public String toString() {
        return "" + pos;
    }
}

class Board {

    public static final int VOID = -1;

    public static final int EMPTY = 0;

    public static final int P0_ACTIVE = 1;

    public static final int P0_INACTIVE = 2;

    public static final int P1_ACTIVE = 3;

    public static final int P1_INACTIVE = 4;

    public boolean cellsInited = false;

    public Cell cells[] = new Cell[12 * 12];

    {
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                Pos pos = Pos.get(x, y);
                cells[pos.index] = new Cell(pos);
            }
        }
    }

    public void read(Scanner in) {
        for (int y = 0; y < 12; y++) {
            String line = in.next();
            if (OldPlayer.DEBUG_INPUT) {
                System.err.println(line);
            }
            for (int x = 0; x < 12; x++) {
                Pos pos = Pos.get(x, y);
                Cell cell = cells[pos.index];
                cell.cut = false;
                cell.unit = null;
                switch(line.charAt(x)) {
                    case '#':
                        cells[pos.index] = Cell.VOID;
                        break;
                    case '.':
                        cells[pos.index].setStatut(EMPTY);
                        break;
                    case 'O':
                        cells[pos.index].setStatut(P0_ACTIVE);
                        break;
                    case 'o':
                        cells[pos.index].setStatut(P0_INACTIVE);
                        break;
                    case 'X':
                        cells[pos.index].setStatut(P1_ACTIVE);
                        break;
                    case 'x':
                        cells[pos.index].setStatut(P1_INACTIVE);
                        break;
                }
            }
        }
        initCellsGraphIfNeeds();
    }

    private void initCellsGraphIfNeeds() {
        if (!cellsInited) {
            cellsInited = true;
            for (int y = 0; y < 12; y++) {
                for (int x = 0; x < 12; x++) {
                    cells[x + 12 * y].initCell(cells);
                }
            }
        }
    }

    private void debugCells() {
        System.err.println("CELLS : ");
        for (int i = 0; i < 144; i++) {
            System.err.println(cells[i]);
        }
    }

    /**
   * return the frontier of the active map from the pos (out frontier)
   */
    /**
   * return the frontier of the active map from the pos (out frontier)
   */
    public List<Pos> getFrontierOut(Pos init, int ownCell) {
        return getFrontier(init, ownCell, false);
    }

    /**
   * return the frontier of the active map from the pos (IN frontier)
   */
    /**
   * return the frontier of the active map from the pos (IN frontier)
   */
    public List<Pos> getFrontierIn(Pos init, int ownCell) {
        return getFrontier(init, ownCell, true);
    }

    private List<Pos> getFrontier(Pos init, int ownCell, boolean in) {
        List<Pos> frontier = new ArrayList<>();
        List<Pos> toVisit = new ArrayList<>();
        List<Pos> visited = new ArrayList<>();
        toVisit.add(init);
        while (!toVisit.isEmpty()) {
            Pos current = toVisit.remove(0);
            visited.add(current);
            for (Dir dir : Dir.values()) {
                Pos next = current.move(dir);
                if (!next.isValid())
                    continue;
                if (toVisit.contains(next))
                    continue;
                if (visited.contains(next))
                    continue;
                Cell cell = cells[next.index];
                if (cell.getStatut() == ownCell) {
                    toVisit.add(next);
                } else if (cell.getStatut() != VOID) {
                    if (in) {
                        // add the current cell as it has a non P0_ACTIVE neighbor
                        if (!frontier.contains(current)) {
                            frontier.add(current);
                        }
                    } else {
                        // add the next cell as it IS a non P0_ACTIVE cell
                        if (!frontier.contains(next)) {
                            frontier.add(next);
                        }
                    }
                }
            }
        }
        return frontier;
    }

    public int getCellValue(Pos pos) {
        return cells[pos.index].getStatut();
    }

    public void add(Unit unit) {
        cells[unit.pos.index].unit = unit;
    }
}

class State {

    public Agent me = new Agent(null);

    public Agent opp = new Agent(null);

    public Board board = new Board();

    public List<Pos> mineSpots = new ArrayList<>();

    public List<Unit> units = new ArrayList<>();

    public int turn;

    public int numberOfMines;

    public void readInit(Scanner in) {
        int numberMineSpots = in.nextInt();
        if (OldPlayer.DEBUG_INPUT) {
            System.err.print(numberMineSpots + " ");
        }
        for (int i = 0; i < numberMineSpots; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            mineSpots.add(Pos.get(x, y));
            if (OldPlayer.DEBUG_INPUT) {
                System.err.print("" + x + " " + y + " ");
            }
        }
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println();
        }
    }

    public void readTurn(Scanner in) {
        turn++;
        me.read(in);
        opp.read(in);
        board.read(in);
        units.clear();
        int buildingCount = in.nextInt();
        numberOfMines = 0;
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println(buildingCount);
        }
        for (int i = 0; i < buildingCount; i++) {
            Unit building = new Unit();
            building.readBuilding(in);
            units.add(building);
            board.add(building);
            if (building.type == UnitType.HQ) {
                if (building.owner == 0) {
                    me.HQ = building.pos;
                } else {
                    opp.HQ = building.pos;
                }
            } else if (building.type == UnitType.MINE) {
                numberOfMines++;
            }
        }
        int unitCount = in.nextInt();
        if (OldPlayer.DEBUG_INPUT) {
            System.err.println(unitCount);
        }
        for (int i = 0; i < unitCount; i++) {
            Unit unit = new Unit();
            unit.readSoldier(in);
            units.add(unit);
            board.add(unit);
            if (unit.owner == 0) {
                me.unitsCount++;
            } else {
                opp.unitsCount++;
            }
        }
    }

    public int unitsCountOf(int i) {
        return i == 0 ? me.unitsCount : opp.unitsCount;
    }

    public Unit getUnitAtPos(Pos next) {
        return board.cells[next.index].unit;
    }

    public Unit getAnyActiveTowerNearPos(Pos next) {
        Cell cell = board.cells[next.index];
        for (int i = 0; i < 4; i++) {
            Cell neighbor = cell.neighbors[i];
            if (neighbor.unit != null && neighbor.getStatut() == Board.P1_ACTIVE && neighbor.unit.type == UnitType.TOWER) {
                return neighbor.unit;
            }
        }
        return null;
    }

    public void move(Unit unit, Pos pos) {
        // remove unit from current cell
        if (unit.pos != null) {
            board.cells[unit.pos.index].unit = null;
        }
        // move unit
        unit.pos = pos;
        Unit other = getUnitAtPos(pos);
        if (other != null) {
            other.dead = true;
        }
        // put unit in cell
        Cell cell = board.cells[pos.index];
        if (cell.getStatut() != Board.P0_ACTIVE) {
            this.me.income += 1;
            cell.setStatut(Board.P0_ACTIVE);
        }
        cell.unit = unit;
    // TODO recalc active/inactive/dead units ?
    }

    public Cell getCell(Unit unit) {
        return board.cells[unit.pos.index];
    }

    public Cell getCell(Pos pos) {
        return board.cells[pos.index];
    }

    public int getCostToConquerCell(Cell cell) {
        if (cell.isActive() && cell.isProtectedByTower())
            return 30;
        if (cell.unit == null)
            return 10;
        Unit unit = cell.unit;
        switch(unit.type) {
            case HQ:
                return 10;
            case MINE:
                return 10;
            case SOLDIER_1:
                return 20;
            case SOLDIER_2:
                return 30;
            case SOLDIER_3:
                return 30;
            case TOWER:
                return 10;
        }
        throw new RuntimeException("QW2 : unknown case for cost");
    }

    public UnitType getUnitToConquer(Cell cell) {
        if (cell.isActive() && cell.isProtectedByTower())
            return UnitType.SOLDIER_3;
        if (cell.unit == null)
            return UnitType.SOLDIER_1;
        Unit unit = cell.unit;
        switch(unit.type) {
            case HQ:
                return UnitType.SOLDIER_1;
            case MINE:
                return UnitType.SOLDIER_1;
            case SOLDIER_1:
                return UnitType.SOLDIER_2;
            case SOLDIER_2:
                return UnitType.SOLDIER_3;
            case SOLDIER_3:
                return UnitType.SOLDIER_3;
            case TOWER:
                return UnitType.SOLDIER_1;
        }
        throw new RuntimeException("QW2 : unknown case for cost");
    }

    public int getNumberOfMines() {
        return numberOfMines;
    }
}

interface AI {

    public void think();
}

class Simulation {

    State state;

    List<String> actionsRecord = new ArrayList<>();

    public Simulation(State state) {
        this.state = state;
        actionsRecord.clear();
    }

    public boolean block(Unit unit) {
        // only block the unit 
        unit.done = true;
        return true;
    }

    public boolean moveUnit(Unit unit, Pos newPos) {
        if (unit.done) {
            System.err.println("ERROR - trying to move again unit " + unit);
            return false;
        }
        if (unit.pos.manhattan(newPos) != 1)
            return false;
        if (unit.canWalkOn(state.getCell(newPos)))
            // realize action
            state.move(unit, newPos);
        unit.done = true;
        // record action
        actionsRecord.add(String.format("MOVE %d %d %d;", unit.id, newPos.x, newPos.y));
        return true;
    }

    public boolean trainUnit(UnitType type, Pos pos) {
        System.err.println("TRAIN " + type + " @ " + pos);
        if (type == UnitType.SOLDIER_1 && state.me.gold < 10)
            return false;
        if (type == UnitType.SOLDIER_2 && state.me.gold < 20)
            return false;
        if (type == UnitType.SOLDIER_3 && state.me.gold < 30)
            return false;
        if (!pos.isValid())
            return false;
        // TODO check if our type can kill the 'other' one ...
        Unit unit = new Unit();
        unit.type = type;
        state.move(unit, pos);
        state.me.gold -= unitValue(type);
        unit.done = true;
        actionsRecord.add(String.format("TRAIN %d %d %d;", type.level, pos.x, pos.y));
        return true;
    }

    public void buildUnit(UnitType type, Pos pos) {
        Unit unit = new Unit();
        unit.type = type;
        state.me.gold -= unitValue(type);
        state.move(unit, pos);
        unit.done = true;
        if (type == UnitType.TOWER) {
            actionsRecord.add(String.format("BUILD TOWER %d %d;", pos.x, pos.y));
        } else if (type == UnitType.MINE) {
            // TODO in state !!!!
            state.numberOfMines++;
            actionsRecord.add(String.format("BUILD MINE %d %d;", pos.x, pos.y));
        }
    }

    private int unitValue(UnitType type) {
        if (type == UnitType.SOLDIER_1)
            return 10;
        if (type == UnitType.SOLDIER_2)
            return 20;
        if (type == UnitType.SOLDIER_3)
            return 30;
        if (type == UnitType.TOWER)
            return 15;
        if (type == UnitType.MINE)
            return 20 + state.getNumberOfMines();
        throw new RuntimeException("unknwon unit " + type);
    }

    public String output() {
        StringBuffer sb = new StringBuffer();
        if (actionsRecord.isEmpty()) {
            return "WAIT";
        } else {
            for (String action : actionsRecord) {
                sb.append(action.toString());
            }
            return sb.toString();
        }
    }

    public void moveToHQ(Unit unit, Pos hQ) {
        // TODO FIX this method to use path finding
        actionsRecord.add(String.format("MOVE %d %d %d;", unit.id, hQ.x, hQ.y));
        unit.done = true;
    }
}

class CreateUnitAI implements AI {

    private Simulation sim;

    private State state;

    public CreateUnitAI(Simulation sim, State state) {
        this.sim = sim;
        this.state = state;
    }

    @Override
    public void think() {
        createUnits();
    }

    private void createUnits() {
        Set<Pos> availablePositionsSet = new HashSet<>();
        // trouver une position où deposer l'unité
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                Cell current = state.getCell(Pos.get(x, y));
                if (current.getStatut() == Board.P0_ACTIVE) {
                    addAllCellsForTrain(availablePositionsSet, x, y);
                    // check ifan inside cell is under threat
                    for (Cell neighbor : current.neighbors) {
                        if (neighbor.getStatut() == Board.P1_ACTIVE) {
                            addAllCellsForTrain(availablePositionsSet, x, y);
                        }
                    }
                }
            }
        }
        List<Cell> availableCells = availablePositionsSet.stream().map( p -> state.getCell(p)).collect(Collectors.toList());
        availableCells.forEach( c -> c.calculateThreatScore());
        availableCells.sort(( c1,  c2) -> {
            return Integer.compare(c2.threat, c1.threat);
        });
        if (OldPlayer.DEBUG_AI) {
            System.err.println("availableCells : ");
            for (Cell cell : availableCells) {
                System.err.print("[ " + cell.pos + ", (" + cell.threat + ") ],");
            }
            System.err.println();
        }
        if (availableCells.size() == 0) {
            System.err.println("ERROR, NO PLACE TO BUILD");
            return;
        }
        while (state.me.gold >= 10 && (state.me.income + 1 > state.unitsCountOf(0)) && !availableCells.isEmpty()) {
            Cell cell = availableCells.remove(0);
            if (cell.threat > 0 && state.getUnitAtPos(cell.pos) == null) {
                if (true && cell.getStatut() == Board.P0_ACTIVE && cell.pos.manhattan(state.opp.HQ) < 12 && /* second half */
                state.me.gold >= 15) {
                    sim.buildUnit(UnitType.TOWER, cell.pos);
                } else {
                    sim.trainUnit(UnitType.SOLDIER_1, /*state.getUnitToConquer( cell)*/
                    cell.pos);
                }
            }
        }
    }

    private void addAllCellsForTrain(Collection<Pos> availablePositions, int x, int y) {
        for (Dir dir : Dir.randomValues()) {
            Pos pos = Pos.get(x + dir.dx, y + dir.dy);
            if (pos != Pos.INVALID) {
                int cell = state.board.getCellValue(pos);
                if (cell == Board.EMPTY || cell == Board.P1_ACTIVE || cell == Board.P1_INACTIVE) {
                    availablePositions.add(pos);
                }
            }
        }
    }
}

class ExplorerAI implements AI {

    private Simulation sim;

    private State state;

    public ExplorerAI(Simulation sim, State state) {
        this.sim = sim;
        this.state = state;
    }

    @Override
    public void think() {
        List<Unit> toMove = new ArrayList<>();
        for (Unit unit : state.units) {
            if (unit.done || unit.owner == 1 || unit.dead || unit.isStatic())
                continue;
            updateDegreeOfFreedom(unit);
            toMove.add(unit);
        }
        state.units.sort(( u1,  u2) -> Integer.compare(u1.degreeOfFreedom, u2.degreeOfFreedom));
        for (Unit unit : toMove) {
            moveForExploration(unit);
        }
    }

    private void moveForExploration(Unit unit) {
        int bestScore = 0;
        Pos bestPos = unit.pos;
        Cell current = state.getCell(unit);
        for (Cell cell : current.neighbors) {
            if (cell == Cell.VOID)
                continue;
            if (!unit.canWalkOn(cell))
                continue;
            int score = 0;
            if (cell.getStatut() == Board.EMPTY) {
                score = 1;
            } else if (cell.getStatut() == Board.P1_INACTIVE) {
                score = 2;
            } else if (cell.getStatut() == Board.P1_ACTIVE) {
                score = 3;
            }
            if (cell.unit != null) {
                if (cell.unit.owner == 1 && unit.canKill(cell.unit)) {
                    score += 100;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestPos = cell.pos;
            }
        }
        if (bestScore > 0) {
            sim.moveUnit(unit, bestPos);
        } else {
        }
    }

    private void updateDegreeOfFreedom(Unit unit) {
        if (unit.isStatic()) {
            return;
        }
        int dof = 0;
        Cell current = state.getCell(unit);
        for (Cell neighbor : current.neighbors) {
            if (neighbor == Cell.VOID)
                continue;
            if (neighbor.getStatut() == Board.EMPTY || neighbor.getStatut() == Board.P1_INACTIVE) {
                dof++;
            } else if (neighbor.getStatut() == Board.P1_ACTIVE) {
                if (neighbor.unit == null || neighbor.unit.canBeKilledBy(unit)) {
                    dof++;
                }
            }
        }
        unit.degreeOfFreedom = dof;
    }
}

class LongCutAI implements AI {

    private Simulation sim;

    private State state;

    private Cell bestCutCell;

    private int bestCutReward;

    private int bestDir;

    public LongCutAI(Simulation sim, State state) {
        this.sim = sim;
        this.state = state;
    }

    @Override
    public void think() {
        int completeValue = findValueFromCut();
        int done[] = new int[144];
        bestCutCell = null;
        bestCutReward = 0;
        bestDir = 0;
        for (int i = 0; i < 144; i++) {
            Cell current = state.board.cells[i];
            if (current == Cell.VOID)
                continue;
            if (current.getStatut() != Board.P0_ACTIVE)
                continue;
            if (done[current.pos.index] != 0)
                continue;
            for (int dir = 0; dir < 4; dir++) {
                cutInDir(current, dir, completeValue);
            }
        }
        if (bestCutCell != null) {
            System.err.println("**** APPLYING CUT from " + bestCutCell);
            Cell next = bestCutCell;
            for (int decal = 0; true; decal++) {
                next = next.neighbors[bestDir];
                if (next == Cell.VOID || next.getStatut() != Board.P1_ACTIVE) {
                    break;
                }
                sim.trainUnit(state.getUnitToConquer(next), next.pos);
            }
        }
    }

    private void cutInDir(Cell current, int dir, int completeValue) {
        List<Cell> cutted = new ArrayList<>();
        int cost = 0;
        Cell next = current;
        for (int decal = 0; true; decal++) {
            next = next.neighbors[dir];
            if (next == Cell.VOID || next.getStatut() != Board.P1_ACTIVE) {
                break;
            }
            next.cut = true;
            cost += state.getCostToConquerCell(next);
            cutted.add(next);
        }
        // no opp territory
        if (cutted.isEmpty())
            return;
        // calc the reward
        int reward = completeValue - findValueFromCut();
        if (reward > 0) {
            System.err.println("CUT AT " + current.pos + " in dir " + dir + " for " + cutted.size() + " cells");
            System.err.println("cutted are " + cutted);
            System.err.println(" cut value is " + reward + " for a cost of " + cost + "my gold : " + state.me.gold);
            if (reward > cost && state.me.gold >= cost) {
                System.err.println("COST IS BETTER THAN REWARD : " + cost + " vs reward " + reward);
                int cutReward = reward - cost;
                next.cutValue = cutReward;
                System.err.println("" + cutReward);
                if (cutReward > bestCutReward) {
                    System.err.println("NEw best cut is at " + current);
                    bestCutCell = current;
                    bestCutReward = cutReward;
                    bestDir = dir;
                }
            }
        }
        // uncut
        cutted.forEach( c -> c.cut = false);
    }

    private int findValueFromCut() {
        int visited[] = new int[144];
        List<Cell> toVisit = new ArrayList<>();
        toVisit.add(state.getCell(state.opp.HQ));
        int value = 0;
        while (!toVisit.isEmpty()) {
            Cell current = toVisit.remove(0);
            if (visited[current.pos.index] != 0)
                continue;
            visited[current.pos.index] = 1;
            value += 1;
            if (current.unit != null) {
                value += (current.unit.type == UnitType.SOLDIER_1) ? 10 : 0;
                value += (current.unit.type == UnitType.SOLDIER_2) ? 20 : 0;
                value += (current.unit.type == UnitType.SOLDIER_3) ? 30 : 0;
            }
            for (Cell neighbor : current.neighbors) {
                if (neighbor.getStatut() == Board.P1_ACTIVE && neighbor.cut == false && visited[neighbor.pos.index] == 0) {
                    toVisit.add(neighbor);
                }
            }
        }
        return value;
    }
}

/**
 * Check if we can go straigh to enemy HQ and so win the game
 * @author nmahoude
 *
 */
/**
 * Check if we can go straigh to enemy HQ and so win the game
 * @author nmahoude
 *
 */
class QuickWinAI {

    public boolean wannaPlay = false;

    private State state;

    private List<Dir> dirsToHQ;

    private Pos initPos;

    private Simulation sim;

    public QuickWinAI(Simulation sim, State state) {
        this.sim = sim;
        this.state = state;
    }

    public void think() {
        long start = System.currentTimeMillis();
        List<Pos> frontier = state.board.getFrontierIn(state.me.HQ, Board.P0_ACTIVE);
        if (OldPlayer.DEBUG_AI) {
            System.err.println("The out frontier : ");
            System.err.println(frontier);
        }
        for (Pos pos : frontier) {
            dirsToHQ = findQuickWinFromPos(pos);
            if (dirsToHQ != null) {
                initPos = pos;
                wannaPlay = true;
                break;
            }
        }
        long stop = System.currentTimeMillis();
        System.err.println("QuickWin algo in " + (stop - start) + " ms.");
        if (dirsToHQ != null) {
            System.err.println("Found a quickwin ...");
            System.err.println("From " + initPos + " -> " + dirsToHQ);
            Pos current = initPos;
            for (Dir dir : dirsToHQ) {
                current = current.move(dir);
                UnitType type = getLevelToConquerCell(current);
                sim.trainUnit(type, current);
            }
        }
        return;
    }

    List<Dir> findQuickWinFromPos(Pos pos) {
        int board[] = new int[12 * 12];
        ArrayList<Dir> previous = new ArrayList<>();
        List<Dir> possibleDir = findDirs(pos, board, 0, previous);
        return possibleDir;
    }

    private List<Dir> findDirs(Pos pos, int[] board, int previousCost, List<Dir> previous) {
        for (Dir dir : Dir.values()) {
            Pos next = pos.move(dir);
            int index = next.x + 12 * next.y;
            if (!next.isValid())
                continue;
            int cellValue = state.board.cells[index].getStatut();
            // can't go through
            if (cellValue == Board.VOID)
                continue;
            // our own cell
            if (cellValue == Board.P0_ACTIVE)
                continue;
            // check if there is a unit at position (to find the price)
            int currentCost = previousCost + getCostToConquerCell(next);
            if (state.me.gold < currentCost) {
                // path too expensive
                continue;
            }
            if (board[next.index] != 0 && board[next.index] < currentCost)
                // more expensive than a previous path
                continue;
            board[next.index] = currentCost;
            List<Dir> forNext = new ArrayList<>(previous);
            forNext.add(dir);
            if (next == state.opp.HQ) {
                // found it !
                return forNext;
            }
            List<Dir> result = findDirs(next, board, currentCost, forNext);
            if (result != null) {
                return result;
            }
        }
        // no solution
        return null;
    }

    UnitType getLevelToConquerCell(Pos next) {
        // lvl 3 to pass through tower defense
        Unit b = state.getAnyActiveTowerNearPos(next);
        if (b != null) {
            return UnitType.SOLDIER_3;
        }
        Unit unit = state.getUnitAtPos(next);
        if (unit != null) {
            if (unit.type.neededLevelToKill == 1)
                return UnitType.SOLDIER_1;
            else if (unit.type.neededLevelToKill == 2)
                return UnitType.SOLDIER_2;
            else if (unit.type.neededLevelToKill == 3)
                return UnitType.SOLDIER_3;
            else
                throw new RuntimeException("Unknown unit to kill " + unit);
        } else {
            // no unit
            return UnitType.SOLDIER_1;
        }
    }

    int getCostToConquerCell(Pos next) {
        UnitType lvl = getLevelToConquerCell(next);
        switch(lvl) {
            case SOLDIER_1:
                return 10;
            case SOLDIER_2:
                return 20;
            case SOLDIER_3:
                return 30;
            default:
                String output = "Unknown lvl " + lvl + " at pos " + next;
                System.out.println(output);
                throw new RuntimeException(output);
        }
    }
}

/**
 * All soldier who didn't move go to hq
 * @author nmahoude
 *
 */
/**
 * All soldier who didn't move go to hq
 * @author nmahoude
 *
 */
class RushToHQAI implements AI {

    private Simulation sim;

    private State state;

    public RushToHQAI(Simulation sim, State state) {
        this.sim = sim;
        this.state = state;
    }

    @Override
    public void think() {
        for (Unit unit : state.units) {
            if (unit.done || unit.owner == 1 || unit.dead || unit.isStatic())
                continue;
            sim.moveToHQ(unit, state.opp.HQ);
        }
    }
}

class OldPlayer {

    public static boolean DEBUG_INPUT = true;

    public static boolean DEBUG_AI = true;

    State state = new State();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        OldPlayer player = new OldPlayer();
        player.play(in);
    }

    public void play(Scanner in) {
        state.readInit(in);
        // game loop
        while (true) {
            state.readTurn(in);
            Simulation sim = new Simulation(state);
            QuickWinAI quickwin = new QuickWinAI(sim, state);
            quickwin.think();
            if (quickwin.wannaPlay) {
                System.out.println(sim.output());
                continue;
            }
            new LongCutAI(sim, state).think();
            new ExplorerAI(sim, state).think();
            //new DefenseAI(sim, state).think();
            new RushToHQAI(sim, state).think();
            new CreateUnitAI(sim, state).think();
            //new BuildMineAI(sim, state).think();
            System.out.println(sim.output());
        }
    }
}