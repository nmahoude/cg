package ag;

public class AGSolution {
  public static FastRand rand = new FastRand(42);
  
  public int keys[] = new int[8];
  public double energy;
  public double points;


  public void copyFrom(AGSolution model) {
    for (int i=0;i<8;i++) {
      keys[i] = model.keys[i];
    }
    energy = model.energy;
    points = model.points;
  }
  
  public void copyFromLastBestSolution(AGSolution lastBestSolution) {
    for (int i=0;i<7;i++) {
      keys[i] = lastBestSolution.keys[i+1]; //don't forget to shift 1 pair of blocks
    }
    keys[7] = getRandomKey();
    energy = 0;
    points = 0;
  }

  
  @Override
  public String toString() {
    String keyStr= "";
    for (int i=0;i<8;i++) {
      keyStr+="["+keyToColumn(keys[i])+","+keyToRotation(keys[i])+"] ";
    }
    return keyStr;
  }
  
  public static final int keyToColumn(int key) {
    return key >>> 2;
  }

  public static final int keyToRotation(int key) {
    return key & 0b11;
  }

  public static void crossover(AGSolution child1, AGSolution child2, AGSolution parent1, AGSolution parent2, int invChanceToMutate) {
    child1.resetSolution();
    child2.resetSolution();
    
    int crossover = rand.fastRandInt(8);
    for (int i=0;i<crossover;i++) {
      child1.keys[i] = parent1.keys[i];
      child2.keys[i] = parent1.keys[i];
    }
    for (int i=crossover;i<8;i++) {
      child1.keys[i] = parent2.keys[i];
      child2.keys[i] = parent1.keys[i];
    }

    
    mutateChild(child1, invChanceToMutate);
    mutateChild(child2, invChanceToMutate);
  }

  public static void mutateChild(AGSolution child, int invChanceToMutate) {
    if (rand.fastRandInt(invChanceToMutate) == 0) {
      int mutateGene = rand.fastRandInt(8);
      child.keys[mutateGene] = getRandomKey();
    }
  }

  public static void mutate(AGSolution child, AGSolution parent) {
    child.resetSolution();
    int chancesOverToMutate = 4; // 1/chances
    for (int i=0;i<8;i++) {
      if (rand.fastRandInt((8-i)*chancesOverToMutate) == 0) {
        child.keys[i] = getRandomKey();
      } else {
        child.keys[i] = parent.keys[i];
      }
    }
  }

  public void resetSolution() {
    points = 0;
    energy = Double.NEGATIVE_INFINITY;
  }

  public static int getRandomKey() {
    int key = rand.fastRandInt(22);
    if (key >= 2) { key+=1; }
    if (key >= 22) { key+=1; }
    return key;
  }

  public void randomize() {
    energy = Double.NEGATIVE_INFINITY;
    points = 0;

    keys[0] = getRandomKey();
    keys[1] = getRandomKey();
    keys[2] = getRandomKey();
    keys[3] = getRandomKey();
    keys[4] = getRandomKey();
    keys[5] = getRandomKey();
    keys[6] = getRandomKey();
    keys[7] = getRandomKey();
  }


}
