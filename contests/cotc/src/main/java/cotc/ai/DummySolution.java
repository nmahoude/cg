package cotc.ai;

import java.util.HashMap;
import java.util.Map;

import cotc.GameState;
import cotc.ai.ag.AGAction;
import cotc.entities.Action;
import cotc.entities.Ship;

public class DummySolution implements AISolution {
  GameState state;
  
  public DummySolution(GameState state) {
    this.state = state;
  }
  @Override
  public String[] output() {
    String[] output = new String[state.shipCount];
    for (int i=0;i<output.length;i++) {
      output[i] = "WAIT";
    }
    return output;
  }
  @Override
  public Map<Ship, AGAction[]> getActions() {
    Map<Ship, AGAction[]> actions = new HashMap<>();
    for (Ship ship : state.teams.get(0).shipsAlive) {
      AGAction[] agActions = new AGAction[1];
      agActions[0] = new AGAction(Action.WAIT, null);
      actions.put(ship, agActions);
    }
    return actions;
  }

  @Override
  public void setEnergy(int i) {
  }
  @Override
  public void resetEnergy() {
  }
  @Override
  public void updateEnergyTurn1(GameState state) {
  }
  @Override
  public void updateEnergy(GameState state) {
  }
}
