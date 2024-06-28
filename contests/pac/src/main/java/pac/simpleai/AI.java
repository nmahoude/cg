package pac.simpleai;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.agents.Pellet;
import pac.ai.AGPelletOptimizer;
import pac.map.PathResolver;
import pac.map.Pos;
import pac.sim.Action;

public class AI {
  private static AGPelletOptimizer dpo = new AGPelletOptimizer();

  private State state;

  Pos predictatedPos[] = new Pos[5];

  public AI(State state) {
    this.state = state;
    for (int i = 0; i < 5; i++) {
      predictatedPos[i] = Pos.INVALID;
      state.pacmen[i].order.doWait();
    }
  }
  
  public void think() {
    
    for (int i=0;i<5;i++) {
      if (state.pacmen[i].pos == Pos.INVALID) continue;
      if (collisionConflict(i)) {
        System.err.println("Detecting hidden collision for #"+state.pacmen[i].index+" @"+state.pacmen[i].pos+" waited = "+predictatedPos[i]);
      }
    }
    
    for (int i = 0; i < 5; i++) {
      Pacman pacman = state.pacmen[i];
      if (pacman.isDead() || pacman.pos == Pos.INVALID) continue;
      pacman.updateMind(state);
    }

    
    // Find best combination of paths 
    Pos moves[][] = dpo.optimize(state);

    // use best paths
    for (int i = 0; i < 5; i++) {
      if (state.pacmen[i].pos == Pos.INVALID) continue;
      
      chooseMoves(moves, i);
    }

    for (int i=0;i<5;i++) {
      Pacman pacman = state.pacmen[i];
      if (pacman.pos == Pos.INVALID) continue;
      ActionAI chosen = pacman.chooseOrder();
      predictatedPos[i] = chosen.target[1];
    }
  }

  private boolean collisionConflict(int i) {
    return Player.turn != 1 && predictatedPos[i] != state.pacmen[i].pos;
  }

  private void chooseMoves(Pos[][] moves, int index) {
    Pacman pacman = state.pacmen[index];

    if (pacman.speedTurnsLeft == 0) {
      moves[index][2] = moves[index][1];
    }
    
    if (pacman.cooldown == 0) {
      if (collisionConflict(index)) {
        Action switchNemesis = Action.switchNemesis(pacman.type);
        pacman.find(switchNemesis).score = Double.POSITIVE_INFINITY;
        return;
      }
      
      // check for speed !
      if (moves[index][2] == pacman.pos) {
        System.err.println("SPEED + next move would be a A/R - forbid SPEED"); // Don't speed and move ! ne pas speed si la prochaine pastille ne nous permet pas de faire un double !
        moves[index][2] = moves[index][1]; 
      } else {
        if (pacman.find(Action.SPEED).score < 0 ) {
          System.err.println("SPEED is forbidden");
        } else if (Player.turn == 199) {
          System.err.println("Don't SPEED on last turn, we may grab one pellet");
        } else {
          System.err.println("SPEED is the best to DO - everytime ? "); // TODO C'est vraiment la meilleur option de SPEED le max du temps ?
          pacman.find(Action.SPEED).score +=500;
        }
      }
    } 

    Pos bestBigPellet = null;
    int bestDist = Integer.MAX_VALUE;
    for (Pellet bigPellet : pacman.myBigPellets) {
      Pos pellet = bigPellet.pos;
      if (bigPellet.value == 0) continue;
      int closest = state.getClosestFromPelletAt(pellet);
      if (closest == index) {
        if (pacman.pos.distance(pellet) < bestDist) {
          bestDist = pacman.pos.distance(pellet);
          bestBigPellet = pellet;
        }
      }
    }
    
    if (bestBigPellet != null &&  bestDist >= PathResolver.DEPTH) {
//      System.err.println("Big pellets @ "+bestBigPellet+" too far, doing manual research");
      
      Pos[] shortestRouteTo = pacman.getShortestRouteTo(bestBigPellet);
      if (pacman.speedTurnsLeft > 0) {
        pacman.find(Action.MOVE, shortestRouteTo[0], shortestRouteTo[1]).score +=500;
      } else {
        pacman.find(Action.MOVE, shortestRouteTo[0], shortestRouteTo[0]).score +=500;
      }
    }

    // reset target if collision
    if (pacman.speedTurnsLeft > 0) {
      if (moves[index][2] == pacman.pos) {
        moves[index][2] = moves[index][1];
      }      
    }
    for (int o=0;o<index;o++) {
      if (state.pacmen[o].pos == Pos.INVALID) continue;
      
      boolean collision;

      // 1st collision, try to only revert 1 move
      collision = ColliderResolver.hasCollidedFull(moves[index], moves[o]);
      if (collision) {
        moves[index][1] = moves[index][1];
      }
      // 2nd collision, revert 2 moves
      collision = ColliderResolver.hasCollidedFull(moves[index], moves[o]);
      if (collision) {
        moves[index][1] = moves[index][0];
        moves[index][2] = moves[index][0];
      }
    }

    ActionAI actionAI = pacman.find(Action.MOVE, moves[index][1], moves[index][2]);
    actionAI.score += 1;
  }
}
