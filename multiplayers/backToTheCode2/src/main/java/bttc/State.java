package bttc;

import java.util.ArrayList;
import java.util.List;

public class State {

  public static final int NEUTRAL = -1;
  public static final int TREATED = -2;

  public static final Direction around[] = { Direction.UpLeft, Direction.Up, Direction.UpRight, Direction.Right, Direction.DownRight, Direction.Down, Direction.DownLeft,
      Direction.Left };
  public static final Direction cardinalDirections[] = { Direction.Up, Direction.Right, Direction.Down, Direction.Left };
  
  public static StateFactory stateFactory = new StateFactory();

  int cells[] = new int[35 * 20];
  Agent players[] = new Agent[4];

  State moveAwayPattern;
  int moveAwayStep;
  State previousGrid;
  static int countExpansions;

  State() {
    for (int i = 0; i < 4; i++) {
      players[i] = new Agent();
    }
  }

  public static boolean A_IS_B_OR_C(Object a, Object b, Object c) {
    return ((a) == (b) || (a) == (c));
  }

  public void copyFrom(State model) {
    System.arraycopy(model.cells, 0, cells, 0, 35*20);
    for (int i=0;i<Player.nbPlayers;i++) {
      players[i].copyFrom(model.players[i]);
    }
    this.moveAwayPattern = model.moveAwayPattern;
    this.moveAwayStep = model.moveAwayStep;
    this.previousGrid = model.previousGrid;
  }

  
  int cellAt(Position pos) {
    return cells[pos.x + 35 * pos.y];
  }

  int score(int p) {
    return players[p].score;
  }

  static int score[] = new int[4];
  void computeScore() {
    for (int i = 0; i < 4; i++) {
      score[i] = 0;
    }
    for (int y = 0; y < 20; y++) {
      for (int x = 0; x < 35; x++) {
        if (cells[x + 35 * y] >= 0) {
          score[cells[x + 35 * y]]++;
        }
      }
    }
    for (int i = 0; i < 4; i++) {
      players[i].score = score[i];
    }
  }

  /* stops as soon as possible */
  boolean attemptFillQuick(Position pos, int p) {
    boolean res = true;
    if (cellAt(pos) == NEUTRAL) {
      cells[pos.x + 35 * pos.y] = TREATED;

      for (Direction dir : cardinalDirections) {
        Position newPos = pos.move(dir);
        if (newPos != Position.WALL) {
          if (!attemptFillQuick(newPos, p)) {
            res = false;
            break;
          }
        } else {
          res = false;
          break;
        }
      }
    } else {
      if (cellAt(pos) == TREATED) {
      } else if (p == NEUTRAL) {
        /* No player's cell encountered yet */
        p = cellAt(pos);
      } else {
        res = p == cellAt(pos);
      }
    }
    return res;
  }

  void fill(Position pos, int p) {
    if (cellAt(pos) == NEUTRAL) {
      cells[pos.x + 35 * pos.y] = p;
      players[p].score++;
      for (Direction dir : cardinalDirections) {
        Position newPos = pos.move(dir);
        if (newPos != Position.WALL) {
          fill(newPos, p);
        }
      }
    }
  }

  int cellAround(Position pos_, Direction dir) {
    Position pos = pos_.move(dir);
    if (pos == Position.WALL) {
      return 9;
    }
    return cellAt(pos);
  }

  int fictiveOwnerAround(Position pos, Direction dir) {
    Position _pos = pos.move(dir);
    if (_pos != Position.WALL) {
      return cellAt(_pos);
    } else {
      return 9;
    }
  }

  static State tempGrid = new State();
  void tryFill(Position pos, int p) {
    tempGrid.copyFrom(this);
    boolean canFill = tempGrid.attemptFillQuick(pos, p);
    if (canFill) {
      fill(pos, p);
    }
  }

  Position closestNeutral(Position pos) {
    Position res = pos;
    int dist = 1;
    int maxDist = Math.max(
        Math.max(pos.distance(Position.get(0, 0)), pos.distance(Position.get(34, 0))),
        Math.max(pos.distance(Position.get(0, 19)), pos.distance(Position.get(34, 19))));
    while (dist <= maxDist) {
      int x;
      int y;
      // top left
      x = Math.max(0, pos.x - dist);
      y = pos.y - (dist - pos.x + x);
      while (y >= 0 && x <= pos.x) {
        if (cells[x + 35 * y] == NEUTRAL) {
          res = Position.get(x, y);
          return res;
        }
        x++;
        y--;
      }
      // bottom left
      x = Math.max(0, pos.x - dist + 1);
      y = pos.y + (dist - pos.x + x);
      while (y < 20 && x <= pos.x) {
        if (cells[x + 35 * y] == NEUTRAL) {
          res = Position.get(x, y);
          return res;
        }
        x++;
        y++;
      }
      // top right
      x = Math.min(34, pos.x + dist);
      y = pos.y - (pos.x + dist - x);
      while (y >= 0 && x > pos.x) {
        if (cells[x + 35 * y] == NEUTRAL) {
          res = Position.get(x, y);
          return res;
        }
        x--;
        y--;
      }
      // bottom right
      x = Math.min(34, pos.x + dist - 1);
      y = pos.y + (pos.x + dist - x);
      while (y < 20 && x > pos.x) {
        if (cells[+35 * y] == NEUTRAL) {
          res = Position.get(x, y);
          return res;
        }
        x--;
        y++;
      }
      dist++;
    }
    return res;
  }

  void moveMe(Direction dir) {
    if (Player.strategy == Strategy.AssumeNoAgression) {
      for (int p = 1; p < Player.nbPlayers; p++) {
        // autoMovePlayer(p);
      }
    }
    movePlayer(0, dir);
  }

  // TODO !
  // void autoMovePlayer(int p) {
  // if (players[p].direction != Direction.None) {
  // Direction newDir[] = { players[p].direction, // Straight
  // (Direction) ((players[p].direction + (players[p].lastTurn ==
  // Direction.Left ? Direction.Left : Direction.Right)), // Last Turn
  // (Direction) ((players[p].direction + (players[p].lastTurn ==
  // Direction.Left ? Direction.Right : Direction.Left)), // opposite turn
  // (Direction) ((players[p].direction + 2) % 4) }; // reverse
  // int d;
  // for (d = 0; d < 4; d++) {
  // if (cellAround(players[p].pos, newDir[d]) == NEUTRAL) {
  // movePlayer(p, newDir[d]);
  // players[p].direction = newDir[d];
  // if (d == 2) {
  // players[p].lastTurn = players[p].lastTurn == Direction.Left ?
  // Direction.Right : Direction.Left;
  // }
  // break;
  // }
  // }
  // if (d >= 4) {
  // movePlayer(p, closestNeutral(players[p].pos));
  // }
  // }
  // }

  void movePlayer(int p, Direction dir) {
    players[p].pos = players[p].pos.move(dir);
    checkAndFill(p);
  }

  void movePlayer(int p, Position target) {
    players[p].pos = players[p].pos.stepTowards(target);
    checkAndFill(p);
  }

  void checkAndFill(int p) {
    Position pos = players[p].pos;
    if (cellAt(pos) == NEUTRAL) {
      cells[pos.x + 35 * pos.y] = p;
      players[p].score++;
      for (int i = 1; i < 8; i = i + 2) {
        if (fictiveOwnerAround(pos, around[i]) == p
            && fictiveOwnerAround(pos, around[(i + 2) % 8]) == p
            && fictiveOwnerAround(pos, around[(i + 1) % 8]) != p) {
          if (fictiveOwnerAround(pos, around[(i + 1) % 8]) == NEUTRAL) {
            tryFill(pos.add(around[(i + 1) % 8]), p);
          }
          if (fictiveOwnerAround(pos, around[(i + 1) % 8]) != p
              && A_IS_B_OR_C(fictiveOwnerAround(pos, around[(i + 3) % 8]), NEUTRAL, p)
              && fictiveOwnerAround(pos, around[(i + 4) % 8]) == NEUTRAL
              && A_IS_B_OR_C(fictiveOwnerAround(pos, around[(i + 5) % 8]), NEUTRAL, p)
              && fictiveOwnerAround(pos, around[(i + 6) % 8]) == NEUTRAL
              && A_IS_B_OR_C(fictiveOwnerAround(pos, around[(i + 7) % 8]), NEUTRAL, p)) {
            tryFill(pos.add(around[(i + 4) % 8]), p);
          }
        }
      }
      for (int i = 1; i < 8; i = i + 2) {
        if (fictiveOwnerAround(pos, around[i]) == p
            && fictiveOwnerAround(pos, around[(i + 4) % 8]) == p) {
          /*
           * .o. 
           * .O. line 
           * .o.
           */
          if (A_IS_B_OR_C(fictiveOwnerAround(pos, around[(i + 1) % 8]), NEUTRAL,
              p) &&
              fictiveOwnerAround(pos, around[(i + 2) % 8]) == NEUTRAL &&
              A_IS_B_OR_C(fictiveOwnerAround(pos, around[(i + 3) % 8]), NEUTRAL, p)) {
            if (!(fictiveOwnerAround(pos, around[(i + 5) % 8]) == p
                && fictiveOwnerAround(pos, around[(i + 6) % 8]) == p
                && fictiveOwnerAround(pos, around[(i + 7) % 8]) == p)) {
              tryFill(pos.add(around[(i + 2) % 8]), p);
            }
          }
        }
      }
    }
  }

  int nextSteps(State[] nextSteps) {
    int nextStepsFE = 0;
    nextStepsFE = 0;
    countExpansions++;

    if ((countExpansions & 0b11111) == 0) {
      if (System.currentTimeMillis() - Player.roundStart > Player.TIMEOUT) {
        Player.timeout = true;
      }
    }

    for (Direction dir : cardinalDirections) {
      State nextGrid = null;
      Position pos = players[0].pos.move(dir);
      if (pos != Position.WALL) {
        if (cellAt(pos) == NEUTRAL) {
          nextGrid = stateFactory.getNewGrid();
          nextGrid.copyFrom(this);
          nextGrid.previousGrid = this;
          nextGrid.moveMe(dir);
          // Acceleration
          for (int i = 1; i < Player.stepSize; i++) {
            if (nextGrid.cellAround(nextGrid.players[0].pos, dir) == NEUTRAL) {
              nextGrid.moveMe(dir);
            } else {
              break;
            }
          }
          nextSteps[nextStepsFE++] = nextGrid;
        }
      }
    }
    if (nextStepsFE == 0 /* dead end*/) {
      // There is no Neutral cell around, go away from here
      Position pos;
      if (moveAwayPattern == null) {
        pos = players[0].pos;
        moveAwayPattern = stateFactory.getNewGrid();
        for (int y = 0; y < 20; y++) {
          for (int x = 0; x < 35; x++) {
            moveAwayPattern.cells[x + 35 * y] = pos.distance(Position.get(x, y));
          }
        }
        moveAwayPattern.cells[pos.x + 35 * pos.y] = TREATED;
        moveAwayStep = 0;
      }
      moveAwayStep++;
      for (Direction dir : cardinalDirections) {
        pos = players[0].pos.move(dir);
        if (pos != Position.WALL) {
          if (moveAwayPattern.cellAt(pos) == moveAwayStep) {
            moveAwayPattern.cells[pos.x + 35 * pos.y] = TREATED;
            State nextGrid = stateFactory.getNewGrid();
            nextGrid.copyFrom(this);
            nextGrid.previousGrid = this;
            nextGrid.moveMe(dir);
            nextSteps[nextStepsFE++] = nextGrid;
          }
        }
      }
    } else {
      moveAwayPattern = null;
    }
    return nextStepsFE;
  }

  State(char trace[]) {
    int linesRead = 0;
    int charIdx = 0;
    while (linesRead < 20) {
      char currentChar = trace[charIdx];
      if (currentChar == '|' || currentChar == '>') {
        charIdx++;
        for (int x = 0; x < 35; x++) {
          currentChar = trace[charIdx];
          switch (currentChar) {
          case '.':
          case ' ':
          case '+':
          case '-':
          case '=':
          case '$':
          case '?':
            cells[x + 35 * linesRead] = NEUTRAL;
            break;
          case 'O':
          case '4':
            players[0].pos = Position.get(x, linesRead);
          case 'o':
          case '0':
            cells[x + 35 * linesRead] = 0;
            break;
          case 'X':
          case '5':
            players[1].pos = Position.get(x, linesRead);
          case 'x':
          case '1':
            cells[x + 35 * linesRead] = 1;
            break;
          case 'V':
          case '6':
            players[2].pos = Position.get(x, linesRead);
          case 'v':
          case '2':
            cells[x + 35 * linesRead] = 2;
            break;
          case 'I':
          case '7':
            players[3].pos = Position.get(x, linesRead);
          case 'i':
          case '3':
            cells[x + 35 * linesRead] = 3;
            break;
          }
          charIdx++;
        }
        charIdx++;
        linesRead++;
      }
      charIdx++;
    }

  }

  void print(State pathEnd) {
    char output[][] = new char[20][35];
    char ownerChars[] = { '0', '1', '2', '3' };
    char playerChars[] = { '4', '5', '6', '7' };
    for (int y = 0; y < 20; y++) {
      for (int x = 0; x < 35; x++) {
        int owner = cells[x + 35 * y];
        char c = owner == NEUTRAL ? '=' : '$';
        if (owner >= 0) {
          c = ownerChars[owner];
        }
        output[y][x] = c;
      }
    }
    while (pathEnd != null) {
      Position pos = pathEnd.players[0].pos;
      output[pos.y][pos.x] = '+';
      pathEnd = pathEnd.previousGrid;
    }
    for (int p = 0; p < Player.nbPlayers; p++) {
      if (players[p].pos.isValid()) {
        output[players[p].pos.y][players[p].pos.x] = playerChars[p];
      }
    }
  }

  void printPath(int p) {
    char output[][] = new char[20][35];
    Position pos = null;
    Position topLeft = Position.get(34, 19);
    Position bottomRight = Position.get(0, 0);
    // TODO memset(output, ' ', 20 * 35 * sizeof(char));
    State grid = this;
    while (grid != null) {
      pos = grid.players[p].pos;
      output[pos.y][pos.x] = '+';
      topLeft = Position.topLeft(topLeft, pos);
      bottomRight = Position.bottomRight(bottomRight, pos);
      grid = grid.previousGrid;
    }
    output[pos.y][pos.x] = 'O';

  }

  int pathLen() {
    State grid = this;
    int res = 0;
    while (grid.previousGrid != null) {
      res += grid.players[0].pos.distance(grid.previousGrid.players[0].pos);
      grid = grid.previousGrid;
    }
    return res;
  }

  Position pathAt(int i) {
    List<Position> path = new ArrayList<>();
    State grid = this;
    while (grid != null) {
      path.add(0, grid.players[0].pos);
      grid = grid.previousGrid;
    }
    if (Player.DEBUG_AI) {
      System.err.println("Path : " + path);
    }
    return path.get(i);
  }
}
