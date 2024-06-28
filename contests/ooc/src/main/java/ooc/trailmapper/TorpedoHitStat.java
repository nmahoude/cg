package ooc.trailmapper;

import java.util.HashSet;
import java.util.Set;

import ooc.P;

public class TorpedoHitStat {
  Set<P> potentialHits = new HashSet<>();

  public double potentialDamage;
  public int potentialCellHitCount;
	public int minimalDamage;
  public P firstHit;

  public void copyFrom(TorpedoHitStat model) {
    this.potentialCellHitCount = model.potentialCellHitCount;
    this.potentialDamage = model.potentialDamage;
    this.minimalDamage = model.minimalDamage;
    potentialHits.clear();
    potentialHits.addAll(model.potentialHits);
    firstHit = model.firstHit;
  }

  public void reset() {
    this.potentialCellHitCount = 0;
    this.minimalDamage = 0;
    this.potentialDamage = 0.0;
    firstHit = null;
    potentialHits.clear();
  }

}
