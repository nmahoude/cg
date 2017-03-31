package oldTests;

import java.util.Arrays;
import java.util.Scanner;

import gitc.GameState;
import gitc.Player;
import gitc.ag.AGSolution;
import gitc.ag.AGSolutionComparator;
import gitc.simulation.Simulation;
import gitc.simulation.actions.MoveAction;

public class CaseBackFactoryDoesntSendToFront {

  /**
   * Pourquoi la factory 7 n'envoie-t-elle pas de troop ???
   * 
   * @param args
   */
  public static void main(String[] args) {
    GameState.TDD_OUPUT = false;

    GameState state = new GameState();
    String setup = "13 78\n";
    setup += "0 1 3\n";
    setup += "0 2 3\n";
    setup += "0 3 2\n";
    setup += "0 4 2\n";
    setup += "0 5 2\n";
    setup += "0 6 2\n";
    setup += "0 7 7\n";
    setup += "0 8 7\n";
    setup += "0 9 6\n";
    setup += "0 10 6\n";
    setup += "0 11 4\n";
    setup += "0 12 4\n";
    setup += "1 2 8\n";
    setup += "1 3 2\n";
    setup += "1 4 6\n";
    setup += "1 5 1\n";
    setup += "1 6 6\n";
    setup += "1 7 2\n";
    setup += "1 8 11\n";
    setup += "1 9 2\n";
    setup += "1 10 10\n";
    setup += "1 11 1\n";
    setup += "1 12 9\n";
    setup += "2 3 6\n";
    setup += "2 4 2\n";
    setup += "2 5 6\n";
    setup += "2 6 1\n";
    setup += "2 7 11\n";
    setup += "2 8 2\n";
    setup += "2 9 10\n";
    setup += "2 10 2\n";
    setup += "2 11 9\n";
    setup += "2 12 1\n";
    setup += "3 4 5\n";
    setup += "3 5 3\n";
    setup += "3 6 4\n";
    setup += "3 7 5\n";
    setup += "3 8 9\n";
    setup += "3 9 3\n";
    setup += "3 10 9\n";
    setup += "3 11 4\n";
    setup += "3 12 6\n";
    setup += "4 5 4\n";
    setup += "4 6 3\n";
    setup += "4 7 9\n";
    setup += "4 8 5\n";
    setup += "4 9 9\n";
    setup += "4 10 3\n";
    setup += "4 11 6\n";
    setup += "4 12 4\n";
    setup += "5 6 5\n";
    setup += "5 7 4\n";
    setup += "5 8 10\n";
    setup += "5 9 4\n";
    setup += "5 10 8\n";
    setup += "5 11 1\n";
    setup += "5 12 8\n";
    setup += "6 7 10\n";
    setup += "6 8 4\n";
    setup += "6 9 8\n";
    setup += "6 10 4\n";
    setup += "6 11 8\n";
    setup += "6 12 1\n";
    setup += "7 8 15\n";
    setup += "7 9 3\n";
    setup += "7 10 13\n";
    setup += "7 11 2\n";
    setup += "7 12 12\n";
    setup += "8 9 13\n";
    setup += "8 10 3\n";
    setup += "8 11 12\n";
    setup += "8 12 2\n";
    setup += "9 10 13\n";
    setup += "9 11 5\n";
    setup += "9 12 10\n";
    setup += "10 11 10\n";
    setup += "10 12 5\n";
    setup += "11 12 10\n";
    Scanner in = new Scanner(setup);
    state.readSetup(in);

    String source = "";
    source += "28\n";
    source += "0 FACTORY 1 0 0 0 0\n";
    source += "1 FACTORY 1 27 3 0 0\n";
    source += "2 FACTORY 1 361 3 0 0\n";
    source += "3 FACTORY 1 17 3 0 0\n";
    source += "4 FACTORY 1 20 3 0 0\n";
    source += "5 FACTORY 1 28 3 0 0\n";
    source += "6 FACTORY 1 3 3 0 0\n";
    source += "7 FACTORY 1 121 3 0 0\n";
    source += "8 FACTORY -1 81 3 0 0\n";
    source += "9 FACTORY 1 0 0 0 0\n";
    source += "10 FACTORY 1 82 0 0 0\n";
    source += "11 FACTORY 1 0 0 0 0\n";
    source += "12 FACTORY 1 179 0 0 0\n";
    source += "0 TROOP 1 1 2 4 1\n";
    source += "1 TROOP 1 3 2 2 1\n";
    source += "2 TROOP 1 1 2 8 4\n";
    source += "3 TROOP 1 5 10 1 4\n";
    source += "4 TROOP 1 3 2 3 3\n";
    source += "5 TROOP 1 4 12 2 1\n";
    source += "6 TROOP 1 6 10 3 1\n";
    source += "7 TROOP 1 3 2 1 4\n";
    source += "8 TROOP 1 5 2 3 4\n";
    source += "9 TROOP 1 6 10 2 2\n";
    source += "10 TROOP 1 5 10 2 7\n";
    source += "11 TROOP 1 1 2 5 8\n";
    source += "12 TROOP 1 3 2 6 6\n";
    source += "13 TROOP 1 6 2 1 1\n";
    source += "14 TROOP 1 6 12 2 1\n";
    in = new Scanner(source);
    state.read(in);

    Player.MILLISECONDS_THINK_TIME = 40;
    Player.gameState = state;
    Player.simulation = new Simulation(state);

    AGSolution solution1 = new AGSolution("WAIT");
    Player.simulation.simulate(solution1);

    AGSolution solution2 = new AGSolution("MOVE");
    solution2.players.get(0).addAction(new MoveAction(GameState.factories[7], GameState.factories[2], 60), 0);
    Player.simulation.simulate(solution2);

    System.out.println("WAIT : " + solution1.energy);
    System.out.println(solution1.message);

    System.out.println("MOVE : " + solution2.energy);
    System.out.println(solution2.message);

    AGSolution best = AGSolutionComparator.compare(Arrays.asList(solution1, solution2));
    System.out.println("Best is " + best.name);
  }
}
