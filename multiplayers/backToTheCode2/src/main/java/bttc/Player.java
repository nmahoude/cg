package bttc;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Player {
  static boolean DEBUG_OUTPUT = false;
  static boolean DEBUG_AI = false;
  static final int TIMEOUT = 20;
  
  static Strategy strategy = Strategy.AssumeNoAgression;
  static int nbPlayers;
  static long roundStart;
  static boolean timeout;
  static int stepSize;

  static TimeLines timeLines = new TimeLines();
  private static int countAttemp;
  private static int countFill;
  private static int countMove;

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }
  
  public void play(Scanner in) {
    int opponentCount; // Opponent count
    opponentCount = in.nextInt();
    nbPlayers = opponentCount + 1;
    if (DEBUG_OUTPUT) {
      System.err.println(""+opponentCount);
    }
    timeLines.mainLine.round = -1;
    
    while (true) {
      readOneTurn(in);
    }
  }
  
  static State[] nextSteps = new State[4];
  static List<State> states = new LinkedList<>();
  public void readOneTurn(Scanner in) {
    // game loop
    {
      int gameRound;
      TimeLine currentTimeLine = timeLines.mainLine;
      State currentGrid;

      timeout = false;

      countAttemp = 0;
      countFill = 0;
      countMove = 0;

      gameRound = in.nextInt();
      gameRound--;
      if (DEBUG_OUTPUT) {
        System.err.println(gameRound);
      }
      roundStart = System.currentTimeMillis();

      /* detect back in time actions here, and backup current timeline */
      if (currentTimeLine.round != gameRound - 1) {
        int nbLostRounds = currentTimeLine.round - (gameRound - 1);
        System.err.println("BACK IN TIME " + nbLostRounds + " ROUNDS");
      }

      currentTimeLine.round = gameRound;
      currentGrid = currentTimeLine.currentState();
      for (int i = 0; i < nbPlayers; i++) {
        int x; // x position
        int y; // y position
        int backInTimeLeft; // Remaining back in time
        x = in.nextInt();
        y = in.nextInt();
        backInTimeLeft = in.nextInt();
        if (DEBUG_OUTPUT) {
          System.err.println(""+x+" "+y+" "+backInTimeLeft);
        }
        if (x == -1) {
          currentGrid.players[i].pos = Position.WALL;
        } else {
          currentGrid.players[i].pos = Position.get(x, y);
          currentGrid.players[i].backInTimeLeft = backInTimeLeft;
        }
      }
      for (int y = 0; y < 20; y++) {
        String line;
        line = in.next();
        if (DEBUG_OUTPUT) {
          System.err.println(line);
        }

        for (int rowIdx = 0; rowIdx < 35; rowIdx++) {
          switch (line.charAt(rowIdx)) {
          case '.':
            currentGrid.cells[rowIdx + 35 * y] = State.NEUTRAL;
            break;
          default:
            currentGrid.cells[rowIdx + 35 * y] = line.charAt(rowIdx) - '0';
            break;
          }
        }
      }

      currentGrid.computeScore();

      if (gameRound == 0) {
        /*
         * assume players are going to go towards the closest corner,
         * horizontally then vertically
         */
        for (int p = 0; p < nbPlayers; p++) {
          Position pos = currentGrid.players[p].pos;
          if (pos.x < 18) {
            if (pos.y < 10) {
              currentGrid.players[p].direction = Direction.Left;
              currentGrid.players[p].lastTurn = Direction.Right;
            } else {
              currentGrid.players[p].direction = Direction.Left;
              currentGrid.players[p].lastTurn = Direction.Left;
            }
          } else {
            if (pos.y < 10) {
              currentGrid.players[p].direction = Direction.Right;
              currentGrid.players[p].lastTurn = Direction.Left;
            } else {
              currentGrid.players[p].direction = Direction.Right;
              currentGrid.players[p].lastTurn = Direction.Right;
            }
          }
        }
      }

      if (gameRound > 0) {
        for (int p = 0; p < nbPlayers; p++) {
          Agent previousPlayer = currentTimeLine.states[currentTimeLine.round - 1].players[p];
          Agent currentPlayer = currentGrid.players[p];
          if (!currentPlayer.pos.isValid()) {
            currentPlayer.direction = Direction.None;
          } else {
            currentPlayer.direction = previousPlayer.pos.directionTo(currentPlayer.pos);
            currentPlayer.lastTurn = previousPlayer.lastTurn;
            if (nextCardinal(previousPlayer.direction) == currentPlayer.direction) {
              currentPlayer.lastTurn = Direction.Right;
            } else if (prevCardinal(previousPlayer.direction) == currentPlayer.direction) {
              currentPlayer.lastTurn = Direction.Left;
            }
          }
        }
      }

      currentGrid.moveAwayPattern = null;
      currentGrid.previousGrid = null;
      
      int currentScore = currentGrid.score(0);
      int bestScore = currentScore;
      State bestGrid = currentGrid;
      double bestScorePerStep = 0;
 
      State.stateFactory.reset();

      for (stepSize = 3; stepSize >= 1; stepSize--) {
        states.clear();
        
        State newGrid = State.stateFactory.getNewGrid();
        newGrid.copyFrom(currentGrid);
        newGrid.previousGrid = null;
        states.add(newGrid);
        State workingGrid;

        int currentLenCount = 1;
        int nextLenCount = 0;
        int currentLen = 1;
        State.countExpansions = 0;

        while (!states.isEmpty()) {
          
          workingGrid = states.remove(0);
          int nextStepsFE = workingGrid.nextSteps(nextSteps);
          for (int i=0;i<nextStepsFE;i++) {
            State nextState = nextSteps[i];
            // add to next steps to handle
            
            states.add(nextState);

            // check if this step is good
            int nextScore = nextState.score(0);
            if (nextScore > bestScore) {
              int nextPathLen = nextState.pathLen();

              double selectionCriteria;
              selectionCriteria = (1.0 * (nextScore - currentScore + 20)) / (nextPathLen + 20);
              if (selectionCriteria > bestScorePerStep) {
                bestScore = nextScore;
                bestGrid = nextState;
                bestScorePerStep = selectionCriteria;
              }
            }
          }

          nextLenCount += nextStepsFE;
          currentLenCount--;
          if (currentLenCount == 0) {
            currentLenCount = nextLenCount;
            nextLenCount = 0;
            currentLen++;
          }

          if (timeout) {
            if (DEBUG_AI) {
              System.err.println("TIMEOUT!!");
            }
            break;
          }
        }
        if (timeout) {
          break;
        }
      }

      currentGrid.print(bestGrid);

      Position nextPos;

      if (DEBUG_AI) {
        System.err.println("Reconstitute path");
        System.err.println("Best score is "+bestScore);
      }
      if (bestGrid.pathLen() > 0) {
        nextPos = bestGrid.pathAt(1);
      } else {
        if (DEBUG_AI) {
          System.err.println("No path, Go to the closest free cell");
        }
        nextPos = currentGrid.closestNeutral(currentGrid.players[0].pos);
      }

      System.out.println("" + nextPos.x + " " + nextPos.y);
    }
  }

  private static Direction nextCardinal(Direction direction) {
    switch (direction) {
    case Up:
      return Direction.Right;
    case Right:
      return Direction.Down;
    case Down:
      return Direction.Left;
    case Left:
      return Direction.Up;
    }
    return Direction.None;
  }

  private static Direction prevCardinal(Direction direction) {
    switch (direction) {
    case Up:
      return Direction.Left;
    case Right:
      return Direction.Up;
    case Down:
      return Direction.Right;
    case Left:
      return Direction.Down;
    }
    return Direction.None;
  }

}
