package cvz.simulation;

import java.util.List;

import cvz.GameState;
import cvz.entities.Human;
import cvz.entities.Zombie;
import trigonometry.Point;

public class Simulation {
  private static final int MAX_SIMULATION_STEP = 10;
  
  private static final int ZOMBIE_MOVE_RADIUS = 400;
  private static final long SQUARE_DIST_ASH_TO_KILL_ZOMBIE = 2_000 * 2_000;
  private static final int SQUARE_ZOMBIE_KILL_HUMAN = 400*400;
  
  private static int fibonaci[] = { 1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765};
  
  GameState state;
  
  /* simulation variables*/
  int turn = 0;
  private int deadZombies[];
  private int deadHumans[];
  private int score[];
  
  public void simulate(GameState state, AGSolution scorer, List<Action> actions) {
    this.state = state;
    init();
    
    for (Action action : actions) {
      simulateOneTurn(action);
    }
    scorer.calculateEnergy(this);
    this.state.restore();
  }
  
  private void init() {
    turn = 0;
    deadZombies = new int[MAX_SIMULATION_STEP];
    deadHumans = new int[MAX_SIMULATION_STEP];
    score = new int[MAX_SIMULATION_STEP];
  }

  public void simulateOneTurn(Action action) {
    ZombiesMove();
    AshMove(action);
    ZombiesKill();
    HumansKill();
  }
  
  private void score() {
  }

  private void ZombiesMove() {
    for (Zombie z : state.zombies) {
      Point old = z.p;
      if (z.dead) continue;
      
      Human target = z.findTarget(state.ash, state.humans);
      int dist = (int)target.p.squareDistance(z.p);
      if (dist < ZOMBIE_MOVE_RADIUS*ZOMBIE_MOVE_RADIUS) {
        z.p = target.p;
      } else {
        z.p = z.p.add(target.p.sub(z.p).normalize().dot(ZOMBIE_MOVE_RADIUS));
      }
    }    
  }

  private void AshMove(Action action) {
    state.ash.move(action.p);
  }

  private void ZombiesKill() {
    for (Zombie z : state.zombies) {
      if (z.dead) continue;
      
      if (state.ash.p.squareDistance(z.p) <= SQUARE_DIST_ASH_TO_KILL_ZOMBIE) {
        z.dead = true;
        addDeadZombie();
      }
    }
  }

  private void addDeadZombie() {
    deadZombies[turn]++;
  }

  private void HumansKill() {
    for (Zombie z : state.zombies) {
      if (z.dead) continue;
      
      for (Human h : state.humans) {
        if (h.dead) continue;
        if (z.p.squareDistance(h.p) < SQUARE_ZOMBIE_KILL_HUMAN) {
          h.dead = true;
          addDeadHuman();
        }
      }
    }    
  }

  private void addDeadHuman() {
    deadHumans[turn]++;
  }
}
