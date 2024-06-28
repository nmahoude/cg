package pac.map;

import pac.State;
import pac.agents.Pellet;

public class Path {
	public double score;

	public Pos positions[];
  public double scores[];
  
  public Path(int depth) {
    positions = new Pos[depth];
    scores = new double[depth];
  }

  @Override
  public String toString() {
  	String output = ""+score+" =>";
    for (int depth=0;depth<PathResolver.DEPTH;depth++) {
      output+=" "+positions[depth];
      output+="*"+scores[depth]+"*";
    }
    return output;
  }

  public void debug(State state) {
    System.err.println("Score "+this.score);

    int depth = 1;
    for (Pos pos : this.positions) {
      depth++;
      Pellet pellet = state.pellets[pos.offset];
      System.err.print(" "+pos);
      if (pellet == null) continue;
      System.err.print("*"+pellet.value+"*");
    }
    System.err.println();
    
  }
}
