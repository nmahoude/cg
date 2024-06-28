package pac.ai;

import pac.Player;
import pac.State;
import pac.agents.Pellet;
import pac.map.Path;
import pac.map.PathResolver;
import pac.map.Pos;

public class MCPelletOptimizer {
  private static final int MAX_ITERATIONS = 2000;
  PathValueCalculator pathValueCalculator = new PathValueCalculator();
  
  
  Path[] paths = new Path[5]; // work

  public Pos[][] optimize(State state) {
    
    Pos[][] best = new Pos[5][2];

    
    pathValueCalculator.init(state, getMaximumDepthAcceptable());
    pathValueCalculator.sort();
    
    Path[] bests = findBestCombination(state);
    
    // TODO what ifpacman 0 is dead ?
    if (bests[0] == null) {
    	bests[0] = pathValueCalculator.bestPaths[0][0];
    	bests[1] = pathValueCalculator.bestPaths[1][0];
    	bests[2] = pathValueCalculator.bestPaths[2][0];
    	bests[3] = pathValueCalculator.bestPaths[3][0];
    	bests[4] = pathValueCalculator.bestPaths[4][0];
    }
    
    for (int i=0;i<5;i++) {
      if (state.pacmen[i].pos == Pos.INVALID ) continue;
      
      Path bestPath = bests[i];
      //System.err.println("Pacman #"+i+" best path "+bestPath);
      
      best[i][0] = bestPath.positions[1];
      best[i][1] = bestPath.positions[2];
    }

    return best;
  }

  private Path[] findBestCombination(State state) {
    if (Player.debugDFSOptimizer()) {
      Player.map.debugMap("value of pellets", pos -> {
        Pellet pellet = state.pellets[pos.offset];
        if (pellet == null) { return " ";}
        else if (pellet.value < 1) { return "?";}
        else return "O";
      });
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos != Pos.INVALID) {
          System.err.println("#"+i+" best paths ");
          for (int t=0;t<Math.min(5, pathValueCalculator.bestPathsFE[i]);t++) {
            System.err.println(pathValueCalculator.bestPaths[i][t]);
          }
        }
      }
    }
    
    
    Path[] bests = new Path[5];
    findBestCombinationOfPaths(state, bests);

    if (Player.turn == 0) {
      System.err.println("Best path is ");
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos != Pos.INVALID) {
          System.err.println(bests[i]);
        }
      }
    }
    return bests;
  }

  private void findBestCombinationOfPaths(State state, Path[] bests) {
    double bestScore = Double.NEGATIVE_INFINITY;
    
    
    for (int iteration = 0; iteration < MAX_ITERATIONS;iteration++) {
      
      Path[] paths = findRandomPaths(state);

      double score = PathScorer.scorePaths(state, paths, getMaximumDepthAcceptable());
      if (score > bestScore) {
        bestScore = score;
        for (int i=0;i<5;i++) {
          bests[i] = paths[i];
        }
      }
    }
  }

	private int getMaximumDepthAcceptable() {
		return Math.min(200-Player.turn, PathResolver.DEPTH);
	}

  private Path[] findRandomPaths(State state) {
    for (int i=0;i<5;i++) {
      if (state.pacmen[i].pos == Pos.INVALID ) continue;
      
      paths[i] = pathValueCalculator.bestPaths[i][Player.random.nextInt(pathValueCalculator.bestPathsFE[i])];
    }
    return paths;
  }

}
