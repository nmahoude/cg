package weightsOptimizer.optimizer;

public class Result {
  public final int wins;
  public final int losses;
  
  
  public Result(int wins, int losses) {
    this.wins = wins;
    this.losses = losses;
  }


  @Override
  public String toString() {
    return String.format("%d - %d", wins, losses);
  }


  public Result inv() {
    return new Result(losses, wins);
  }
}
