package c4l.sample;

import java.util.ArrayList;
import java.util.List;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

/**
 * Brute force optimizer (should pass)
 * @author nmahoude
 *
 */
public class SampleOptimizer {
  GameState state;
  Robot me;
  public List<Sample> samples = new ArrayList<>();
  double xpGainWeights[] = new double[GameState.MOLECULE_TYPE];
  
  SampleInfo best = new SampleInfo();
  
  public SampleInfo optimize(GameState state, Robot me) {
    this.state = state;
    this.me = me;
    
    samples.clear();
    for (Sample sample : state.availableSamples) {
      samples.add(sample);
    }
    for (Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) continue;
      samples.add(sample);
    }
  
    updateXPGainWeights();
    
    
    for (Sample sample1 : samples) {
      for (Sample sample2 : samples) {

        for (Sample sample3 : samples) {
          SampleInfo info = new SampleInfo();

          info.samples.add(sample1);
          if (sample1 != sample2) {
            info.samples.add(sample2);
          }
          if (sample3 != sample1 && sample3 != sample2) {
            info.samples.add(sample3);
          }
          
          calculateScore(info);
          
          if (info.score > best.score) {
            best = info;
          }
        }
      }
    }
    return best;
  }

  private void updateXPGainWeights() {
    int total = 0;
    int perMolecule[] = new int[GameState.MOLECULE_TYPE];
    for (ScienceProject scienceProject : state.scienceProjects) {
      if (scienceProject.doneBy != -1) continue;
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        total += scienceProject.expertiseNeeded[i];
        perMolecule[i] += scienceProject.expertiseNeeded[i];
      }
    }
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (total > 0) {
        xpGainWeights[i] = 1.0 * perMolecule[i] / total;
      } else {
        xpGainWeights[i] = 0;
      }
    }
  }

  private int totalStorage;
  int storage[] = new int[GameState.MOLECULE_TYPE];
  int pickedMolecules[] = new int[GameState.MOLECULE_TYPE];
  int expertise[] = new int[GameState.MOLECULE_TYPE];
  int availables[] = new int[GameState.MOLECULE_TYPE];
  
  private SampleInfo calculateScore(SampleInfo info) {
    init();
    info.points = 0;
    int totalMoleculePicked = 0;
    
    double xpBonus = 0.0;
    for (Sample sample : info.samples) {
      for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
        int needed = sample.costs[j];
        if ( needed > availables[j]+storage[j]+expertise[j]) {
          info.score = Double.NEGATIVE_INFINITY;
          return info; // can't do it
        } else {
          if (expertise[j] >= needed) {
            needed = 0;
          } else {
            needed-= expertise[j];
          }
          if (storage[j] >= needed ) {
            storage[j] -= needed;
            needed = 0;
          } else {
            needed -= storage[j];
            storage[j] = 0;
          }
          if (availables[j] < needed) {
            info.score = Double.NEGATIVE_INFINITY;
            return info; // can't do it
          } else {
            totalMoleculePicked+=needed;
            pickedMolecules[j]+=needed;
            availables[j]-=needed;
            needed = 0;
            if (totalMoleculePicked + totalStorage > 10) {
              info.score = Double.NEGATIVE_INFINITY;
              return info; // can't do it
            }
          }
        }
      }
      
      xpBonus += 1.0*xpGainWeights[sample.expertise.index];
      expertise[sample.expertise.index]++;
      info.points+=sample.health;
      // bonus to fill our XP
    }
    
    info.points += getExpertisePoints();
    
    info.score = 0.0 + xpBonus;
    int turns = 0;
    if (totalMoleculePicked ==0) {
      turns = 
             Module.distance(me.target, Module.DIAGNOSIS)
           + Module.distance(Module.DIAGNOSIS, Module.LABORATORY)
           + info.samples.size();
    } else {
      turns = 
              Module.distance(me.target, Module.DIAGNOSIS)
            + Module.distance(Module.DIAGNOSIS, Module.MOLECULES)
            + totalMoleculePicked
            + Module.distance(Module.MOLECULES, Module.LABORATORY)
            + info.samples.size()
          ;
    }
    if (state.ply + turns > 200) {
      //TODO handle a risk here ? if state.ply+turns == 200 we can theorically get all, but it's not sure if opp blocks us
      info.score = Double.NEGATIVE_INFINITY;
      return info; // can't do it
    }
    
    // count the number of swap
    for (Sample removed : me.carriedSamples) {
      if (info.samples.indexOf(removed) == -1) {
        turns++;// action to remove the sample
      }
    }
    for (Sample added : info.samples) {
      if (me.carriedSamples.indexOf(added) == -1) {
        turns++;
      }
    }
    
    info.score += 1.0*info.points / turns;
    
    return info;
  }
  private int getExpertisePoints() {
    int xpPoints = 0;
    for (ScienceProject sp : state.scienceProjects) {
      if (sp.doneBy != -1) continue;
      boolean good = true;
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (expertise[i]<sp.expertiseNeeded[i]) {
          good = false;
          break;
        }
      }
      if (good)  {
        xpPoints+=50;
      }
    }
    return xpPoints;
  }
  private void init() {
    totalStorage = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      storage[i] = me.storage[i];
      totalStorage+=storage[i];
      expertise[i] = me.expertise[i];
      pickedMolecules[i] = 0;
      availables[i] = state.availables[i];
    }
  }
}