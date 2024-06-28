package sg22;

public class Fitnesse {

  
  public double score(State state) {
    // state must be in END phase !
    
    double score = 0;
    
    if (state.agents[0].score == 5) {
      return Double.MAX_VALUE;
    }
    
    score= 1000 * state.agents[0].score;
    
    
    
    return score;
  }
}
