package ooc.ai.search.scorers;

import java.util.HashMap;
import java.util.Map;

import ooc.FreeCellsDetector2;
import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.ai.search.Node;

public class LastChanceScorer extends Scorer {
  private Map<P, Integer> memoFreeCellsInDir = new HashMap<>();
  
  private double score;
  private double moveScore;
  private double myLifeScore;
  private double mineFieldScore;
  private double minimalDamageScore;
  private double potentialDamageScore;
  private double furtiviteScore;
  private double silenceScore;

  private double freeCellsScore;

  private int potentialMine_2damages;

  private int potentialMine_1damages;

  private double potential1DamageScore;

  private double potential2DamageScore;

  private State state;

  private P currentPos;

  private Oracle oracle;

  private Node node;

  public LastChanceScorer(P center) {
  }

  @Override
  public void reset() {
    memoFreeCellsInDir.clear();
  }

  @Override
  public double calculate(State initState, Node node, Oracle oracle) {
    this.node = node;
    this.oracle = oracle;
    this.state = node.state;
    this.currentPos = node.state.myPos;
    score = 0.0;
    
    int furtivite = state.fastDetector.count();
    
    myLifeScore = 1.0 * state.myLife;
    
    
    Integer cache = memoFreeCellsInDir.get(currentPos);
    int cellAvailable;
    if (cache == null) {
      cellAvailable = new FreeCellsDetector2().countFreeCellInDir(state, state.myPos);
      memoFreeCellsInDir.put(currentPos, cellAvailable);
    } else {
      cellAvailable = cache.intValue();
    }
    freeCellsScore = 0.01 * cellAvailable;
    
    
    // ne pas s'approcher des minefields
    double mineField = oracle.oppMapper.minesHeatMap2[currentPos.o]; // 0 -> 1
    mineFieldScore = -0.001 * mineField;
    
    potentialMine_2damages = oracle.oppMapper.allMines.get(currentPos.o) ? 1: 0;
    potentialMine_1damages = 0;
    for (P pos : Player.map.squaredNeighbors.get(currentPos)) {
      if (oracle.oppMapper.allMines.get(pos.o)) {
        potentialMine_1damages = 1;
        break;
      }
    }
    
    minimalDamageScore = 10.0 * state.torpedoHitStat.minimalDamage;
    potentialDamageScore = 0.01 * state.torpedoHitStat.potentialDamage;
    
    
    silenceScore = node.hasSilence() ? -1 : 0; // malus to do a silence  (TODO need to counteract by bonus ..) 
    if (furtivite > 10) {
      silenceScore = node.hasSilence() ? -1000 : 0;
    }
    
    furtiviteScore = 0.0;
    furtiviteScore += dangerCoeff() * 0.5 * furtivite;
    
    if (node.hasTorpedo()) {
      furtiviteScore +=  0.01 * furtivite;
    }

    moveScore = 0; // int 1 -> 4
    for (P pos : Player.map.squaredNeighbors.get(currentPos)) {
      if (state.isVisitedCells(pos)) {
        moveScore += 0.00001 * 1; 
      }
    }

    if (furtivite <= 30) {
      potential1DamageScore = -0.1 * ((30 - furtivite) / 30.0) * potentialMine_1damages;
      potential2DamageScore = -0.2 * ((30 - furtivite) / 30.0) * potentialMine_2damages;
    } else {
      potential1DamageScore = 0;
      potential2DamageScore = 0;
    }

    
    score += myLifeScore;       // keep your hp
    score += minimalDamageScore; // garanteed damages : lower opp hp
    score += potentialDamageScore; // somewhat less garanteed : lower opp hp
    score += moveScore;         // move wisely
    score += mineFieldScore;      // don't go in mineField
    score += silenceScore; // don't do silence if not needed 
    score += furtiviteScore; // furtivité : 0 -> 225
    score += freeCellsScore;
    score += potential2DamageScore;
    score += potential1DamageScore;
    return score;
  }

  private double dangerCoeff() {
    if (state.fastDetector.count() > 5) return 0.0;
    
    if (oracle.closestOppPos() > 5 
        && (potentialMine_1damages == 0 && potentialMine_2damages == 0)) {
      return 0.0;
    }
    return 1.0;
  }

  public String debug() {
    return "score="+score
          //+", ls:"+myLifeScore
          //+", mds="+minimalDamageScore
          //+", pds="+potentialDamageScore
          +", ms="+moveScore
          +", fc="+freeCellsScore
          //+", mis="+mineFieldScore
          +", ss="+silenceScore
          +", fs="+furtiviteScore
          //+", p2d="+potential2DamageScore
          //+", p1d="+potential1DamageScore
          ;
  }
}
