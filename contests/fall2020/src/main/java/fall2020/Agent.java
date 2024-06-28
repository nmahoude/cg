package fall2020;

import fall2020.fast.FastReader;
import fall2020.optimizer.OInv;
import fall2020.optimizer.OSpell;
import fall2020.optimizer.OptiGraph;

public class Agent {
  public int brewedRecipe; // number of brewed potions /*\ statefull
  
  public OInv inv;
  
  public Spell spells[] = new Spell[50];
  public int spellsFE = 0;
  
  public long castedSpells;
  public long knownSpells;
  
  
  public int score;
  
  public Agent() {
  	for (int i=0;i<spells.length;i++) {
  		spells[i] = new Spell();
  	}
	}
  
  
  public void readSpell(FastReader in, int actionId, byte actionType) {
  	
    Spell spell = spells[spellsFE++];
    spell.gameId = actionId;
    spell.type = actionType;
		spell.read(in);

		knownSpells |= spell.spell.mask; // j'ai ce sort
		
		if(!spell.castable) {
		  castedSpells |= spell.spell.mask; 
		}
  }
  
  
  public void read(FastReader in) {
    int a = in.nextInt();
    int b = in.nextInt();
    int c = in.nextInt();
    int d = in.nextInt();

    inv = OptiGraph.getInventory(a, b, c, d);
    
    int newScore = in.nextInt();
    if (newScore != score) { brewedRecipe++; }
    score = newScore;
    
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("\"%d %d %d %d %d\"+EOF+", inv.inv[0], inv.inv[1], inv.inv[2], inv.inv[3], score));
    }
  }

  public boolean canBrew(Recipe recipe) {
    return inv.canBrew(recipe.recipe);
  }

  public boolean canCast(OSpell spell) {
    if ((castedSpells & spell.mask) != 0) return false; // exhausted
    return inv.canCast(spell);
  }

  public int getInv(int index) {
    return inv.inv[index];
  }


  public String findSpellId(OSpell spell) {
    for (int i=0;i<spellsFE;i++) {
      if (spells[i].spell == spell) return ""+spells[i].gameId;
    }
    return "-1";
  }


  public boolean hasSpell(OSpell spell) {
    return (spell.mask & knownSpells) != 0;
  }

  public boolean hasInvToCast(OSpell spell) {
    return inv.canCast(spell);
  }


  public boolean isExhausted(OSpell spell) {
    return (spell.mask & castedSpells) != 0;
  }


  public boolean isLastRecipe() {
    return brewedRecipe == 5;
  }
}
