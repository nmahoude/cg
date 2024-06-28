package fall2020.optimizer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OptiGraphTest {

  
  @BeforeAll
  static void setup() {
    // force init of OptiGraph
    recipe(-2, 0, -2, 0);
  }
  
  @Test
  public void basicSpellIsKnown() throws Exception {
    OSpell spell = spell(2, 0, 0, 0);
    
    assertThat(spell).isNotNull();
  }
  
  @Test
	void allInventoriesExistsForTier0plus() throws Exception {
		for (OInv inv : OptiGraph.invs) {
			for (int i=1;i<=10-inv.allTierTotal;i++) {
				assertThat(OptiGraph.getInventory(inv.inv[0]+i, inv.inv[1], inv.inv[2], inv.inv[3])).isNotNull();
			}
		}
	}
  
  
  @Test
  public void canBrewSimpleRecipe() throws Exception {
    ORecipe recipe = recipe(-2, 0, -2, 0); 
    
    OInv source = inventory(4, 0, 3, 0); 
    
    OInv target1 = source.brew(recipe);
    
    assertThat(target1).isNotNull();
  }
  
  @Test
  void distanceFromStartToSimpleRecipe() throws Exception {
    ORecipe recipe = recipe(-2, -2, 0, -2);
    
    OInv source = inventory(3, 0, 0, 0);
    
    long spellsMask = 0b1111;
    
    
    int distance = source.recipeDistance[recipe.id];
    System.err.println("Distance is "+distance);
    int d2 = OptiGraph.findReadDistanceTo(source, spellsMask, recipe);
    System.err.println("Real distance (wihtout rest) " + d2);
  }
  
  
  @Test
  void debugSpellsScore() throws Exception {
    //OptiGraph.showSpellScores();
    OptiGraph.calculateSpellScore2();
  }
  
  @Test
  public void cantBrewTwoRecipesWhenNotEnoughIngredients() throws Exception {
    ORecipe recipe = recipe(-2, 0, -2, 0);
    
    OInv source = OptiGraph.getInventory(new int[] { 4, 0, 2, 2});
    
    OInv target1 = source.brew(recipe);
    OInv target2 = target1.brew(recipe);
    
    assertThat(target1).isNotNull();
    assertThat(target2).isNull();
  }

  private static OSpell spell(int i, int j, int k, int l) {
    return OptiGraph.getSpell(i,j,k,l);
  }

  private static ORecipe recipe(int i, int j, int k, int l) {
    return OptiGraph.getRecipe(i, j, k, l);
  }

  private OInv inventory(int i, int j, int k, int l) {
    return OptiGraph.getInventory(new int[] { i, j, k, l});
  }
}
