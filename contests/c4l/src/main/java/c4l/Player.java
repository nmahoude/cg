package c4l;

import java.util.Random;
import java.util.Scanner;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.minimax.MoleculeNode;
import cgutils.random.FastRandom;
import minimax.Minimax;

/**
 * Bring data on patient samples from the diagnosis machine to the laboratory
 * with enough molecules to produce medicine!
 **/
public class Player {
  public static final Random rand = new FastRandom(17);//System.currentTimeMillis());

  private static final int MAX_CARRIED = 10;
  private static final int MAX_CARRIED_SAMPLES = 3;
  private static GameState state = new GameState();
  private static Robot me = state.robots[0];
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    state.readScienceProjects(in);

    // game loop
    while (true) {
      state.initRound();
      for (int i = 0; i < 2; i++) {
        state.robots[i].read(in);
      }
      
      state.readAvailables(in);
      state.readSamples(in);
      
      orderSamplesPerScore();
      Sample firstUndiscoveredSample = getFirstUnDiscoveredSample();
      
      if (me.carriedSamples.isEmpty()) {
        getSomeSamples();
        continue;
      } 
      
      // can't complete any sample :(
      if ( firstUndiscoveredSample == null 
          && !haveACompleteSample()
          && getBestMoleculeForSamples() == null) {
        if (me.carriedSamples.size() < MAX_CARRIED_SAMPLES) {
          getSomeSamples();
          continue;
        } else {
          trashSomeSamples();
          continue;
        }
      }
      if (me.carriedSamples.size() < MAX_CARRIED_SAMPLES 
          && me.target == Module.SAMPLES) {
        getSomeSamples();
        continue;
      }
      
      System.err.println("here we have at least one sample");
      if (firstUndiscoveredSample != null) {
        System.err.println("Sample is not discovered, need to go to DIAG");
        if (me.target != Module.DIAGNOSIS) {
          System.err.println("not at DIAG, going");
          System.out.println("GOTO DIAGNOSIS");
          continue;
        }
        System.err.println("Diagnosis now");
        System.out.println("CONNECT "+firstUndiscoveredSample.id);
        continue;
      }
      
      if (!haveACompleteSample()) {
        System.err.println("On a pas les molecules");
        if (me.target != Module.MOLECULES) {
          System.err.println("on va sur MOLE");
          System.out.println("GOTO MOLECULES");
          continue;
        }
        // we are at MOLE, get some molecules
        MoleculeType type= getBestMoleculeForSamples();
        if (type != null) {
          System.err.println("Need molecule "+type);
          System.out.println("CONNECT "+type);
          continue;
        } else {
          // WAIT
          // TODO trash worst sample ???
          System.err.println("no more molecule, waiting ....");
          System.out.println("GOTO MOLECULES");
          continue;
          
        }
      }
      
      if (me.target != Module.LABORATORY) {
        System.err.println("On est pas sur LAB");
        System.out.println("GOTO LABORATORY");
        continue;
      }
      
      System.err.println("Ca y est, on peut fabriquer");
      System.out.println("CONNECT "+getCompleteSample().id);
    }
  }

  private static void orderSamplesPerScore() {
    me.carriedSamples.sort((sample1, sample2) -> {
      return Double.compare(
          sample2.score(me, state), 
          sample1.score(me, state));
    });
    me.carriedSamples.forEach(sample -> sample.debug());
  }

  private static void trashSomeSamples() {
    if (me.target != Module.DIAGNOSIS) {
      System.out.println("GOTO DIAGNOSIS");
      return;
    } else {
      System.out.println("CONNECT "+me.carriedSamples.get(2).id);
    }
  }

  private static MoleculeType getBestMoleculeForSamples() {
    if (me.getTotalCarried() == MAX_CARRIED) {
      System.err.println("FULL Of molecules :(");
      return null;
    }
    
    // Test a minimax to choose the best molecule
    doMinimax();
    
    // TODO Optimized the chosen molecule
    int i=0;
    MoleculeType type = null;
    while (i<me.carriedSamples.size() && type == null) {
      type = me.getMissingMoleculeForSample(state, me.carriedSamples.get(i));
      i++;
    }
    System.err.println("Requesting type "+type);
    return type;
  }

  private static void doMinimax() {
    Minimax minimax = new Minimax();
    
    int totalAvailables = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      totalAvailables+=state.availables[i];
    }
    int[] values = new int[6*GameState.MOLECULE_TYPE];
    for (int i=0;i<3;i++) {
      if (me.carriedSamples.size() > i) {
        Sample sample = me.carriedSamples.get(i);
        for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
          values[i*GameState.MOLECULE_TYPE+j] = Math.max(0, sample.costs[j] - me.storage[j] - me.expertise[j]);
        }
        
      } else {
        for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
          values[i*GameState.MOLECULE_TYPE+j] = 99;
        }
      }
    }
    // next 3 lines
    for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
      values[3*GameState.MOLECULE_TYPE+j]=0;
      values[4*GameState.MOLECULE_TYPE+j]=0;
      for (int i=0;i<3;i++) {
        /*SUM*/values[3*GameState.MOLECULE_TYPE+j]+= values[i*GameState.MOLECULE_TYPE+j]>0 ? 1 : 0;
        /*MAX*/values[4*GameState.MOLECULE_TYPE+j]= Math.max(values[4*GameState.MOLECULE_TYPE+j], values[i*GameState.MOLECULE_TYPE+j]);
      }
      /*DISPO*/values[5*GameState.MOLECULE_TYPE+j] = state.availables[j];
    }    
    
    MoleculeNode node = new MoleculeNode(10-me.getTotalCarried(), totalAvailables, values);
    minimax.alphaBetaPositionAware(node, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
  }

  private static boolean haveACompleteSample() {
    return getCompleteSample() != null;
  }

  public static Sample getCompleteSample() {
    for (Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) continue;
      if (me.hasMolecules(sample)) {
        return sample;
      }
    }
    return null;
  }
  private static Sample getFirstUnDiscoveredSample() {
    for(Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) {
        return sample;
      }
    }
    return null;
  }

  private static void getSomeSamples() {
    System.err.println("No samples");
    
    if (me.target == Module.DIAGNOSIS) {
      // check if there is an available sample that can fit our molecules
      Sample best = null;
      for (Sample sample : state.availableSamples) {
        if (me.hasMolecules(sample)) {
          if (best == null || best.health < sample.health) {
            best = sample;
          }
        }
      }
      if (best != null) {
        System.err.println("Found a perfect match");
        best.debug();
        if (me.target == Module.DIAGNOSIS) {
          System.out.println("CONNECT "+best.id);
        } else {
          System.out.println("GOTO DIAGNOSIS");
        }
        return;
      }

      System.err.println("No perfect match, check if there is enough total molecules for the sample ");
      best = null;
      double bestScore = Double.NEGATIVE_INFINITY;
      for (Sample sample : state.availableSamples) {
        if (me.isThereEnoughMoleculeForSample(state, sample)) {
          double score = sample.score(me, state);
          if (bestScore < score) {
            best = sample;
            bestScore = score;
          }
        }
      }
      if (best != null) {
        System.err.println("Found a plausible match");
        best.debug();
        if (me.target == Module.DIAGNOSIS) {
          System.out.println("CONNECT "+best.id);
        } else {
          System.out.println("GOTO DIAGNOSIS");
        }
        return;
      }
      System.err.println("Found no match in the DIAG samples, goto SAMPLES now");
    }

    // check to go back to SAMPLES
    if (me.target != Module.SAMPLES) {
      System.err.println("GOTO sample");
      System.out.println("GOTO SAMPLES");
      return;
    }
    System.err.println("we are at SAMPLES, get a sample");
    //TODO test with rank = 1 to levelup quickly ?
    if (me.totalExpertise < 4) {
      System.err.println("Not enough expertise, get a 2");
      System.out.println("CONNECT "+2);
    } else {
      System.err.println("rank 2 or 3, I don't care");
      System.out.println("CONNECT "+(2+rand.nextInt(1)));
    }
    return;
  }
}
