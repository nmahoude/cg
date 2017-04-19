package cotc.ai;

import java.util.Map;

import cotc.GameState;
import cotc.ai.ag.AGAction;
import cotc.ai.ag.ShipActions;
import cotc.entities.Ship;

public interface AISolution {

  String[] output();

  void setEnergy(int i);
  void resetEnergy();

  void updateEnergyTurn(int turn, GameState state);
  void updateEnergyEnd(GameState state);

  Map<Ship, AGAction[]> getActions();
  ShipActions[] getActionsNew();
}
