package fantasticBitsMulti.ag;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.units.Unit;

public class AGSolution {
  public double energy;
  public int moves1[] = new int[Player.DEPTH];
  public int moves2[] = new int[Player.DEPTH];

  public int spellTurn1;
  public Unit spellTarget1;
  public int spell1;

  public int spellTurn2;
  public Unit spellTarget2;
  public int spell2;

  public void randomize() {
    for (int i=0;i<Player.DEPTH;++i) {
      moves1[i] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
      moves2[i] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    }
    spellTurn1 = Player.rand.fastRandInt(Player.SPELL_DEPTH);
    spell1 = Player.rand.fastRandInt(4);
    spellTarget1 = Player.spellTargets[spell1][Player.rand.fastRandInt(Player.spellTargetsFE[spell1])];
    spellTurn2 = Player.rand.fastRandInt(Player.SPELL_DEPTH);
    spell2 = Player.rand.fastRandInt(4);
    spellTarget2 = Player.spellTargets[spell2][Player.rand.fastRandInt(Player.spellTargetsFE[spell2])];
  }

  public void copy(AGSolution solution) {
    for (int i = 0; i < Player.DEPTH; ++i) {
      moves1[i] = solution.moves1[i];
      moves2[i] = solution.moves2[i];
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
    int r = Player.rand.fastRandInt(4);

    if (r == 0) {
      // Change a moves1
      moves1[Player.rand.fastRandInt(Player.DEPTH)] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    } else if (r == 1) {
      // Change a moves2
      moves2[Player.rand.fastRandInt(Player.DEPTH)] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
    } else if (r == 2) {
      // Change spell1
      spellTurn1 = Player.rand.fastRandInt(Player.SPELL_DEPTH);
      spell1 = Player.rand.fastRandInt(4);
      spellTarget1 = Player.spellTargets[spell1][Player.rand.fastRandInt(Player.spellTargetsFE[spell1])];
    } else {
      // Change spell2
      spellTurn2 = Player.rand.fastRandInt(Player.SPELL_DEPTH);
      spell2 = Player.rand.fastRandInt(4);
      spellTarget2 = Player.spellTargets[spell2][Player.rand.fastRandInt(Player.spellTargetsFE[spell2])];
      spellTarget2.speed();
    }
  }

  public AGSolution merge(AGSolution solution) {
    AGSolution child = new AGSolution();

    for (int i = 0; i < Player.DEPTH; ++i) {
      if (Player.rand.fastRandInt(2) != 0) {
        child.moves1[i] = solution.moves1[i];
        child.moves2[i] = solution.moves2[i];
      } else {
        child.moves1[i] = moves1[i];
        child.moves2[i] = moves2[i];
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

}
