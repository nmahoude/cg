package oldcvz;

import java.util.List;

public class Simulation {

  public static final int ASH_MOVE = 1000;
  public static final int ZOMBIE_MOVE = 400;
  
  private static final int MAX_SIMULATION_STEP = 200;

  private static final int ZOMBIE_MOVE_RADIUS = 400;

  private static final long SQUARE_DIST_ASH_TO_KILL_ZOMBIE = 2_000 * 2_000;

  private static final int SQUARE_ZOMBIE_KILL_HUMAN = 400 * 400;

  static double depth[] = new double[MAX_SIMULATION_STEP];
  static {
    depth[0] = 1.0;
    for (int i=1;i<MAX_SIMULATION_STEP;i++) {
      depth[i] = 0.99 * depth[i-1];
    }
  }
  
  State state;

  int turn = 0;

  int steps;
  public int score;
  public int scores[]= new int[MAX_SIMULATION_STEP];
  public Point nextPos = new Point(0, 0);

  private boolean debug;

  
  private Point finalAction = new Point(0,0);

  public void simulate(State state, AGSolution scorer, List<Point> actions) {
    this.state = state;
    this.steps = actions.size();
    init();
    for (Point action : actions) {
      simulateOneTurn(action);
      if (action == actions.get(0)) {
        nextPos.x = state.ash.p.x; 
        nextPos.y = state.ash.p.y; 
      }
    }
    // go to closer human
    Human human = scorer.closerHuman(state);
    Human ash = state.ash;

    if (human != null) {
      while (human.p.squareDistance(ash.p) > SQUARE_DIST_ASH_TO_KILL_ZOMBIE) {
        if (human.dead) break;
        
        double dist = human.p.distTo(ash.p);
        finalAction.x = ash.p.x + ASH_MOVE * (human.p.x - ash.p.x) / dist;
        finalAction.y = ash.p.y + ASH_MOVE * (human.p.y - ash.p.y) / dist;
        
        simulateOneTurn(finalAction);
      }
    } else {
    }
    
    scorer.calculateEnergy(this);
    
  }

  private void init() {
    turn = 0;
    score = 0;
    
    for (int i=0;i<MAX_SIMULATION_STEP;i++) {
      scores[i] = 0;
    }
    
  }

  public void simulateOneTurn(Point action) {
    ZombiesMove();
    AshMove(action);
    ZombiesKill();
    HumansKill();
    turn++;
  }

  private void ZombiesMove() {
    for (Zombie z : state.zombies) {
      Point old = z.p;
      if (z.dead)
        continue;
      Human target = z.findTarget(state.ash, state.humans);
      int dist = (int) target.p.squareDistance(z.p);
      if (dist < ZOMBIE_MOVE_RADIUS * ZOMBIE_MOVE_RADIUS) {
        z.p = target.p;
      } else {
        z.p = z.p.add(target.p.sub(z.p).normalize().dot(ZOMBIE_MOVE_RADIUS));
      }
    }
  }

  private void AshMove(Point action) {
    state.ash.move(action);
  }

  private void ZombiesKill() {
    int scoreThisTurn = 0;

    int deadZombieThisTurn = 0;
    for (Zombie z : state.zombies) {
      if (z.dead)
        continue;
      if (state.ash.p.squareDistance(z.p) <= SQUARE_DIST_ASH_TO_KILL_ZOMBIE) {
        if (debug) {
          System.err.println("Ash @"+state.ash.p+" kill "+z.id+" @"+z.p);
        }
        deadZombieThisTurn++;
        state.aliveZombies--;
        z.dead = true;
        z.deadThisTurn = true;
        scoreThisTurn += state.aliveHumans * state.aliveHumans * 10 * AGSolution.fibValues[deadZombieThisTurn];
      }
    }
    scores[turn] = scoreThisTurn;
    score += scoreThisTurn;
  }

  private void HumansKill() {
    for (Zombie z : state.zombies) {
      if (z.dead)
        continue;
      for (Human h : state.humans) {
        if (h.dead)
          continue;
        if (z.p.squareDistance(h.p) < SQUARE_ZOMBIE_KILL_HUMAN) {
          h.dead = true;
          if (debug) {
            System.err.println("Zombie "+z.id+" @"+z.p+" kill H "+h.id+" @"+h.p);
          }

          state.aliveHumans--;
        }
      }
    }
    if (state.aliveHumans == 0) {
      scores[turn] = -100_000;
    }
  }

  public void enableDebug() {
    debug = true;
  }
  public void resetDebug() {
    debug = false;
  }
}