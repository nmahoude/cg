package sg22;

public class Agent {
  public int location = -1;
  public int score;
  public int permanentDailyRoutineCards;
  public int permanentArchitectureStudyCards;
  
  
  public void copyFrom(Agent model) {
    this.location = model.location;
    this.score = model.score;
    this.permanentDailyRoutineCards = model.permanentDailyRoutineCards;
    this.permanentArchitectureStudyCards = model.permanentArchitectureStudyCards;
  }

}
