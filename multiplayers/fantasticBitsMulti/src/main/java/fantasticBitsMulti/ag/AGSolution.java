package fantasticBitsMulti.ag;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.units.Unit;
import random.FastRand;

public class AGSolution {
  private static FastRand rand = new FastRand(42);

  public double energy;
  public Action[] actions0 = new Action[AG.DEPTH];
  public Action[] actions1 = new Action[AG.DEPTH];
  {
    for (int i=0;i<AG.DEPTH;i++) {
      actions0[i] = new Action();
      actions1[i] = new Action();
    }
  }
  
  
  public int spellTurn1;
  public Unit spellTarget1;
  public int spell1;

  public int spellTurn2;
  public Unit spellTarget2;
  public int spell2;


  public void randomize() {
    for (int i=0;i<AG.DEPTH;++i) {
      actions0[i].type = Action.TYPE_MOVE;
      actions0[i].angle = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
      
      actions1[i].type = Action.TYPE_MOVE;
      actions1[i].angle = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    }
    spellTurn1 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
    spell1 = 2+Player.rand.fastRandInt(2);
    spellTarget1 = Player.state.spellTargets[spell1][Player.rand.fastRandInt(Player.state.spellTargetsFE[spell1])];
    
    spellTurn2 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
    spell2 = 2+Player.rand.fastRandInt(2);
    spellTarget2 = Player.state.spellTargets[spell2][Player.rand.fastRandInt(Player.state.spellTargetsFE[spell2])];
  }

  public void copy(AGSolution solution) {
    for (int i = 0; i < AG.DEPTH; ++i) {
      actions0[i].copy(solution.actions0[i]);
      actions1[i].copy(solution.actions1[i]);
    }

    spellTurn1 = solution.spellTurn1;
    spell1 = solution.spell1;
    spellTarget1 = solution.spellTarget1;
    spellTurn2 = solution.spellTurn2;
    spell2 = solution.spell2;
    spellTarget2 = solution.spellTarget2;

    this.energy = solution.energy;
  }

  public void mutate() {
    int r = rand.fastRandInt(4);

    if (r == 0) {
      // Change a moves1
      actions0[Player.rand.fastRandInt(AG.DEPTH)].angle = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    } else if (r == 1) {
      // Change a moves2
      actions1[Player.rand.fastRandInt(AG.DEPTH)].angle = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    } else if (r == 2) {
      // Change spell1
      spellTurn1 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
      spell1 = Player.rand.fastRandInt(4);
      spellTarget1 = Player.state.spellTargets[spell1][Player.rand.fastRandInt(Player.state.spellTargetsFE[spell1])];
    } else {
      // Change spell2
      spellTurn2 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
      spell2 = Player.rand.fastRandInt(4);
      spellTarget2 = Player.state.spellTargets[spell2][Player.rand.fastRandInt(Player.state.spellTargetsFE[spell2])];
    }
  }

  public AGSolution mergeInto(AGSolution child, AGSolution solution) {
    for (int i = 0; i < AG.DEPTH; ++i) {
      if (Player.rand.fastRandInt(2) != 0) {
        child.actions0[i].copy(solution.actions0[i]);
      } else {
        child.actions0[i].copy(actions0[i]);
      }
      if (Player.rand.fastRandInt(2) != 0) {
        child.actions1[i].copy(solution.actions1[i]);
      } else {
        child.actions1[i].copy(actions1[i]);
      }
    }

    if (Player.rand.fastRandInt(2) != 0) {
      child.spellTurn1 = solution.spellTurn1;
      child.spellTarget1 = solution.spellTarget1;
      child.spell1 = solution.spell1;
    } else {
      child.spellTurn1 = spellTurn1;
      child.spellTarget1 = spellTarget1;
      child.spell1 = spell1;
    }

    if (Player.rand.fastRandInt(2) != 0) {
      child.spellTurn2 = solution.spellTurn2;
      child.spellTarget2 = solution.spellTarget2;
      child.spell2 = solution.spell2;
    } else {
      child.spellTurn2 = spellTurn2;
      child.spellTarget2 = spellTarget2;
      child.spell2 = spell2;
    }
    return child;
  }

  void makeNewSolutionFromLastBest(AGSolution best) {
    for (int j = 1; j < AG.DEPTH; ++j) {
      actions0[j - 1].copy(best.actions0[j]);
      actions1[j - 1].copy(best.actions1[j]);

      spellTurn1 = best.spellTurn1;
      spell1 = best.spell1;
      spellTarget1 = best.spellTarget1;
      spellTurn2 = best.spellTurn2;
      spell2 = best.spell2;
      spellTarget2 = best.spellTarget2;
      if (spellTurn1 == 0) {
        spellTurn1 = AG.SPELL_DEPTH - 1;
      } else {
        spellTurn1 -= 1;
      }

      if (spellTurn2 == 0) {
        spellTurn2 = AG.SPELL_DEPTH - 1;
      } else {
        spellTurn2 -= 1;
      }

      if (spellTarget1.dead) {
        spellTurn1 = AG.SPELL_DEPTH - 1;
        spellTarget1 = Player.state.spellTargets[spell1][rand.fastRandInt(Player.state.spellTargetsFE[spell1])];
      }

      if (spellTarget2.dead) {
        spellTurn2 = AG.SPELL_DEPTH - 1;
        spellTarget2 = Player.state.spellTargets[spell2][rand.fastRandInt(Player.state.spellTargetsFE[spell2])];
      }
    }
  }

  public void randomizeLastMove() {
    actions0[AG.DEPTH - 1].type = Action.TYPE_MOVE;
    actions0[AG.DEPTH - 1].angle = rand.fastRandInt(Player.ANGLES_LENGTH);

    actions1[AG.DEPTH - 1].type = Action.TYPE_MOVE;
    actions1[AG.DEPTH - 1].angle = rand.fastRandInt(Player.ANGLES_LENGTH);
  }

}
