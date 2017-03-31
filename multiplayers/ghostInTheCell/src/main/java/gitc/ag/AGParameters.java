package gitc.ag;

public class AGParameters {
  /** pool size */
  public int POOL_SIZE = 100;
  
  /** depth of simulation */
  public int DEPTH = 10;

  /** number of tentative to mutate the best from a past generation (note : not the previous turn) */
  public int MUTATE_BEST_ITERATION_SIZE = 10;

  /* number of representants from best of previous turn (+random lastMove) */;
  public int BEST_RANDOM_SIZE = 10;

  /* 1 / % of chance to mutation. (example : 20 => 5%) */
  public int MUTATION_RATE = 20;

  /* number of iteration to find a better member of pool*/
  public int FIND_INDEX_ITERATION = 2; 

}
