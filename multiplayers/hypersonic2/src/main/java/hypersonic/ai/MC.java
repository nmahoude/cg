package hypersonic.ai;

import java.util.Arrays;

import hypersonic.Cache;
import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.simulation.MoveGenerator;
import hypersonic.simulation.Simulation;

public class MC {
  private static final int TIME_LIMIT = 50;
  private static final int DROP_BOMB_TIME_LIMIT = 25;
  private static final int DEPTH = 20;

  public State state = new State();
  public String message = "";
  Simulation simulator = new Simulation(state);
  MoveGenerator gen = new MoveGenerator(state);
  
  public static Move[] bestMoves = new Move[DEPTH];
  Move[] allowedMoves = new Move[10];
  int movesFE;

  private double bestScore;
  
  public void think(State model) {
    this.state.copyFrom(model);

    Move allMoves[] = new Move[DEPTH];
    bestScore = Double.NEGATIVE_INFINITY;

    // TODO timeout because P.get(-1, -1) ! Need to investigate
//    if (bestMoves[0] != null) {
//      startWithLastBestMove(model);
//    }

    
    
    int simu = 0;
    boolean dropEnnemyBombs = true;
    boolean survivableSituation = false;
    while (true) {
      simu++;
      if ((simu & 0b11111) == 0 ) {
        long duration = System.currentTimeMillis() - Player.startTime;
        if (duration > TIME_LIMIT) {
          break;
        } else if (duration > DROP_BOMB_TIME_LIMIT) {
          if (!survivableSituation) {
            // we did'nt find any safe routes until now, 
            // so stop dropping ennemy bombs to try to find a safe route now
            dropEnnemyBombs = false;
          }
        }
      }
      
      double score = 0;
      this.state.copyFrom(model);
      for (int t=0;t<DEPTH;t++) {
        this.state.players[0].points = 0;
        this.state.players[1].points = 0;
        this.state.players[2].points = 0;
        this.state.players[3].points = 0;
        
        
        if (t <= DEPTH - Bomb.DEFAULT_TIMER - 1 ) {
          movesFE = gen.getPossibleMoves(allowedMoves);
        } else {
          movesFE = gen.getPossibleMovesWithoutBombs(allowedMoves);
        }

        Move move = allowedMoves[Player.rand.nextInt(movesFE)];
        dropEnnemiesBombIfNeeded(dropEnnemyBombs, t);
        allMoves[t] = move;
        simulator.simulate(move);
        score += Score.score(state, t, move);

        if (this.state.players[Player.myId].isDead) {
          break;
        }
      }
   
      if (score > bestScore) {
        bestScore = score;
        Move tmp[] = bestMoves;
        bestMoves = allMoves;
        allMoves = tmp;
        survivableSituation |= !this.state.players[Player.myId].isDead;
        if(Player.DEBUG_AI) {
          System.err.println("New best score : "+bestScore);
          System.err.println("best move : "+Arrays.asList(bestMoves));
          System.err.println("Status pos = "+this.state.players[Player.myId].position);
          System.err.println("Status dead = "+this.state.players[Player.myId].isDead);
          System.err.println("Drop bombs ? "+dropEnnemyBombs);
        }
      }
    }
    
    message = ""+simu + " / "+(System.currentTimeMillis()-Player.startTime)+ " db:"+dropEnnemyBombs;
    
    Optimizer.optimizeBombs(bestMoves, bestScore, DEPTH, model, dropEnnemyBombs);

    
    if (Player.DEBUG_AI) {
      System.err.println("Simulations : " + simu);
      System.err.println("Still drop bombs? : "+dropEnnemyBombs);
    }
    System.err.println("Best (db:"+dropEnnemyBombs+"): "+Arrays.asList(bestMoves)+ " => "+bestScore);
    
  }

  private void dropEnnemiesBombIfNeeded(boolean dropEnnemyBombs, int t) {
    if (dropEnnemyBombs && t == 0) {
      // for all players different than me and who can, drop a bomb at first one
      for (int i=0;i<4;i++) {
        if (i == Player.myId) continue;
        Bomberman b = state.players[i];
        if (b.isDead || b.bombsLeft == 0) continue;
        state.addBomb(Cache.popBomb(i, b.position, 8, b.currentRange, true));
      }
    }
  }

  private void startWithLastBestMove(State model) {
    //System.err.println("Restaring for old best !");
    //System.err.println(Arrays.toString(bestMoves));
    for (int i=0;i<DEPTH-1;i++) {
      bestMoves[i] = bestMoves[i+1];
    }
    bestMoves[DEPTH-1] = Move.STAY;
    //System.err.println(Arrays.toString(bestMoves));
    
    this.state.copyFrom(model);
    
    double score = 0;
    for (int t=0;t<DEPTH;t++) {
      if (bestMoves[t] == null) break;
      simulator.simulate(bestMoves[t]);
      if (this.state.players[Player.myId].isDead) {
        score = -1_000_000 + t; // die the latest
        break;
      } else {
        score += Score.score(state, t, bestMoves[t]);
      }
    }
    bestScore = score;
  }

  public void ouput(State currentState) {
    final Move move = bestMoves[0];
    outputMove(currentState.players[Player.myId], move, message);
  }
  
  private void outputMove(final Bomberman me, final Move move, String message) {
    int newX = me.position.x + move.dx;
    int newY = me.position.y + move.dy;
    if (move.dropBomb) {
      System.out.println("BOMB "+newX+" "+newY+ " "+message);
    } else {
      System.out.println("MOVE "+newX+" "+newY+ " "+message);
    }
  }

  public void reset() {
  }

}
