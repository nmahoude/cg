package cotc.ai;

import java.util.HashMap;
import java.util.Map;

import cotc.GameState;
import cotc.ai.ag.AGAction;
import cotc.ai.ag.ShipActions;
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
    for (int s = 0; s < state.teams[0].shipsAlive.FE; s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];

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
  public void updateEnergyTurn(int turn, GameState state) {
  }
  @Override
  public void updateEnergyEnd(GameState state) {
  }
  @Override
  public ShipActions[] getActionsNew() {
    // TODO Auto-generated method stub
    return null;
  }
}
