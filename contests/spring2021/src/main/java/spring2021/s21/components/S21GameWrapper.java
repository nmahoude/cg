package spring2021.s21.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cgfx.wrappers.GameWrapper;
import spring2021.Action;
import spring2021.Player;
import spring2021.Simulator;
import spring2021.State;

public class S21GameWrapper extends GameWrapper {

	State baseState = new State();
	Player player = new Player();
	
	@Override
	protected void _copyFrom(GameWrapper gameWrapper) {
		S21GameWrapper s21GameWrapper = (S21GameWrapper)gameWrapper;
		this.baseState.copyFrom(s21GameWrapper.baseState);
		this.player.state.copyFrom(s21GameWrapper.player.state);
		
	}
	
	@Override
	protected void readGlobalInput(String in) {
		try(Scanner i = new Scanner(in)) {
		  player = new Player();
		  player.readGlobal(i);
		  baseState.copyFrom(player.state);
		}
	}

	@Override
	protected void readTurnInput(String in) {
    try(Scanner i = new Scanner(in)) {
  		player.readTurn(i);
  		baseState.copyFrom(player.state);
    }
	}

	@Override
	protected List<String> _calculateAIListOfActions() {
		List<String> actions = new ArrayList<>();
		actions.add(CURRENT);
		for (int d=1;d<Player.bestActionsFE+1;d++) {
			if (player.bestActions[d-1] != null) {
				actions.add(player.bestActions[d-1].toString());
			} else {
				System.out.println("Warning - breaking early because of null action");
				break;
			}
		}
		return actions;
	}

	@Override
	protected void _think() {
		player.think();
	}

	@Override
	protected void _applyAction(String action) {
		if (GameWrapper.CURRENT.equals(action)) return;
		
		Simulator.simulate(player.state, Action.fromString(action), 0);
	}

	@Override
	protected void _resetFromBase() {
		player.state.copyFrom(baseState);
	}
}
