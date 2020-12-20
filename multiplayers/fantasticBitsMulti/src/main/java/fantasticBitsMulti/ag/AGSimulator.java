package fantasticBitsMulti.ag;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.simulation.Scorer;
import fantasticBitsMulti.simulation.Simulation;
import fantasticBitsMulti.units.Snaffle;

public class AGSimulator {
  Simulation sim = new Simulation();
  Scorer scorer = new Scorer();

  Action action0 = new Action();
  Action action1 = new Action();
  Action action2 = new Action();
  Action action3 = new Action();
  
  public void simulate(AGSolution solution) {
    scorer.reset();

    for (int i = 0; i < AG.DEPTH; ++i) {
      if (solution.spellTurn1 == i) {
        action0.type = Action.TYPE_CAST;
        action0.spellId = solution.spell1;
        action0.target = solution.spellTarget1;
      } else {
        if (Player.state.wizards[0].snaffle != null) {
          action0.type = Action.TYPE_THROW;
          action0.thrust = 500;
        } else {
          action0.type = Action.TYPE_MOVE;
          action0.thrust = 150;
        }
        action0.cosAngle = Player.cosAngles[solution.moves1[i]];
        action0.sinAngle = Player.sinAngles[solution.moves1[i]];
      }
      
      if (solution.spellTurn2 == i) {
        action1.type = Action.TYPE_CAST;
        action1.spellId = solution.spell2;
        action1.target = solution.spellTarget2;
      } else {
        if (Player.state.wizards[1].snaffle != null) {
          action1.type = Action.TYPE_THROW;
          action1.thrust = 500;
        } else {
          action1.type = Action.TYPE_MOVE;
          action1.thrust = 150;
        }
        action1.cosAngle = Player.cosAngles[solution.moves2[i]];
        action1.sinAngle = Player.sinAngles[solution.moves2[i]];
      }
      
      Simulation.simulate(action0, action1, Action.WAIT, Action.WAIT);

      scorer.evalTurn(i);
    }
    scorer.finalEval();
    solution.energy = scorer.eval();
    Player.state.restoreState();
  }

  
  public static void dummies() {
    if (Player.state.wizards[2].snaffle != null) {
      Player.state.wizards[2].snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.state.wizards[2].position.distTo(Player.hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < Player.state.snafflesFE; ++i) {
        Snaffle snaffle = Player.state.snaffles[i];

        if (!snaffle.dead) {
          d = Player.state.wizards[2].position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        Player.state.wizards[2].thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }

    if (Player.state.wizards[3].snaffle != null) {
      Player.state.wizards[3].snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.state.wizards[3].position.squareDistance(Player.hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < Player.state.snafflesFE; ++i) {
        Snaffle snaffle = Player.state.snaffles[i];

        if (!snaffle.dead) {
          d = Player.state.wizards[3].position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        Player.state.wizards[3].thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }
  }

}
