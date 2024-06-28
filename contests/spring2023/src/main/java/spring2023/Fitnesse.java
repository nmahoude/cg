package spring2023;

public class Fitnesse {

  public static double Scores(State state) {
    return state.myScore - state.oppScore;
  }

  public static double myAntsScore(State state) {
    return state.totalMyAnts;
  }
}
