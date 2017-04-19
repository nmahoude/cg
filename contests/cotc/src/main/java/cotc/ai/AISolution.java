package cotc.ai;

import java.util.Map;

import cotc.GameState;
import cotc.ai.ag.AGAction;
import cotc.entities.Ship;

public interface AISolution {

  String[] output();

  void setEnergy(int i);
  void resetEnergy();

  void updateEnergyTurn1(GameState state);
  void updateEnergy(GameState state);

  Map<Ship, AGAction[]> getActions();

}
