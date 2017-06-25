package ww;

import java.util.Scanner;

import ww.sim.Simulation;

public class Player {
  static GameState state = new GameState();
  static Simulation simulation = new Simulation();
  static Node backupState = new Node();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    state.readInit(in);

    int round = 0;
    // game loop
    while (true) {
      round++;
      
      state.readRound(in);
      state.toTDD();
      if (round > 1) {
        assessOppPosition();
      }

      Node node = new Node();
      node.calculateChilds(state);
      
      System.out.println(node.bestAction.toPlayerOutput());

      state.restore();
      simulation.simulate(node.bestAction, state); // simulate the state after our action
      backupState.save(state);
    }
  }

  /*
    compare old state with new state and try to know new opp position
    if found with xxx% sure, modify current gamestate to counter *fog of war*
   */
  private static void assessOppPosition() {
    // TODO Auto-generated method stub
    
  }
}