package ooc.trailmapper;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import ooc.Direction;
import ooc.OOCMap;
import ooc.P;
import ooc.Player;
import ooc.SonarResult;
import ooc.orders.Order;
import ooc.orders.OrderVisitor;

public class TrailMapper implements OrderVisitor {
	private static final double gauss[] = new double[] { 1.0, 0.7};
	
	private static final int LAYER_SIZE = 5_000;
	static TrailNode tmp[], nextLayer[] = new TrailNode[LAYER_SIZE];
	static int tmpFE=0,nextLayerFE = 0;

	private OOCMap map;
	public TrailNode currentLayer[] = new TrailNode[LAYER_SIZE];
	public int currentLayerFE = 0;
	
  public BitSet allMines = new BitSet(OOCMap.S2);
	BitSet allTrigers = new BitSet(OOCMap.S2);
	public Set<P> potentialPositions = new HashSet<>();
	
	/* 
	 * first 20 positions that put mine at offset 
	 */
	public P trailCountByMineAtPos[] = new P[20*OOCMap.S2]; 
  public int trailCountByMineAtPosFE[] = new int[OOCMap.S2];
	
	public double heatMap[] = new double[OOCMap.S2];
	public double minesHeatMap2[] = new double[OOCMap.S2];
  
	public int realityCountByPos[] = new int[OOCMap.S2];
	public int realityCountTotal;

	public double posPotentiality[] = new double[OOCMap.S2];
	public P bestRealityPos;
	public double bestPot;
	
  boolean debug = false;


	
	public TrailMapper(OOCMap map) {
		this.map = map;
		initAllNodes();
	}
	
	
	public void copyFrom(TrailMapper model) {
		this.map = model.map;

		this.potentialPositions.clear();
		for (P pos : model.potentialPositions) {
			this.potentialPositions.add(pos);
		}

		this.debug = model.debug;
		this.currentLayerFE = 0;
		for (int i=0;i<model.currentLayerFE;i++) {
			TrailNode nodeModel = model.currentLayer[i];
			TrailNode node = TNCache.pop();
			node.copyFrom(nodeModel);
			this.currentLayer[this.currentLayerFE++] = node;
		}
	}
	
	private void initAllNodes() {
	  currentLayerFE = 0;
		for (int i=0;i<OOCMap.S2;i++) {
			P pos = P.getFromOffset(i);
			if (map.isIsland(pos)) continue;

			TrailNode node = TNCache.pop();
			node.currentPos = pos;
			currentLayer[currentLayerFE++] = node;
		}
		calculatePotentialPositions();
	}

	public int getCurrentLayerSize() {
	  return currentLayerFE;
	}

  public void setupTurn() {
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      oneReality.resetDamageTaken();
    }
  }

	public void applyOppDamage(P target) {
	  if (target == P.I) return;
	  
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      oneReality.damage(target);
    }
	}
	
	public P getClosestPotentialPosFrom(P from) {
		int bestDist = Integer.MAX_VALUE;
		P best = P.I;
		for (P p : potentialPositions) {
			int dist = Player.map.distances(from, p);
			if (dist < bestDist) {
				bestDist = dist;
				best = p;
			}
		}
		return best;
	}
	
	public void filterRealityBasedOnDamages(int damages) {
    nextLayerFE = 0;
    if (debug) System.err.println("Current number of reality "+currentLayerFE);
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      TrailNode newRealityNode = oneReality.checkDamageDealt(damages);
      if (newRealityNode != null) {
        nextLayer[nextLayerFE++] = newRealityNode;
        if (debug) System.err.println("Keeping position "+oneReality.currentPos +" with damage "+oneReality.getDamageTaken()+" == "+damages);
      } else {
      	TNCache.push(oneReality);
        if (debug) System.err.println("Filtering position "+oneReality.currentPos +" with damage "+oneReality.getDamageTaken()+" instead of "+damages);
      }
    }   
    swapLayers();
	}
	
  private void swapLayers() {
  	/*
  	 *  don't restitute node to cache, they have been reused already or pushed back
  	 */
  	tmp = currentLayer;
  	tmpFE = currentLayerFE;
  	currentLayer = nextLayer;
  	currentLayerFE = nextLayerFE;
		nextLayer = tmp;
		nextLayerFE = tmpFE;
	}


	@Override
  public void usedTorpedo(Order order) {
    handleTorpedo(order.pos);
  }

  @Override
  public void usedSonar(Order order) {
    // nothing to infer
  }

  @Override
  public void usedSilence(Order order) {
    handleSilentMove();
  }

  @Override
  public void usedMove(Order order) {
    handleMove(Direction.from(order.value));
  }

  @Override
  public void usedMine(Order order) {
    handleDropMine();
  }

  @Override
  public void usedSurface(Order order) {
    handleSurface(order.value);
  }

  @Override
  public void usedTrigger(Order order) {
    handleTriggerMine(order.pos);
  }

	private void handleTriggerMine(P pos) {
	  allTrigers.set(pos.o);

	  nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      TrailNode newRealityNode = oneReality.trigger(allTrigers, pos);
      if (newRealityNode != null) {
      	nextLayer[nextLayerFE++] = newRealityNode;
      } else {
        if (Player.D_TRIGGERS) System.err.println("Filtering trigger @"+pos+" not possible in reality");
      	TNCache.push(oneReality);
      }
    }   
    swapLayers();
  }

  private void handleDropMine() {
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      oneReality.dropMine();
    }    
  }

  public void handleSonarInSector(int sector) {
		nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
			TrailNode newRealityNode = oneReality.sonarInSector(sector);
			if (newRealityNode != null) {
				nextLayer[nextLayerFE++] = newRealityNode;
			} else {
      	TNCache.push(oneReality);
			}
		}		
		swapLayers();
	}
	
	public void handleSonarNOTInSector(int sector) {
		nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
			TrailNode newRealityNode = oneReality.sonarNOTInSector(sector);
			if (newRealityNode != null) {
				nextLayer[nextLayerFE++] = newRealityNode;
			} else {
      	TNCache.push(oneReality);
			}
		}		
		swapLayers();
	}
	
	private void handleSilentMove() {
		
		nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
			nextLayerFE = oneReality.silentMove(nextLayer, nextLayerFE); // opti perf, not clean
		}		
		swapLayers();
	}


	private void handleTorpedo(P target) {
		nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
			TrailNode newRealityNode = oneReality.fireTorpedo(target);
			if (newRealityNode != null) {
				nextLayer[nextLayerFE++] = newRealityNode;
			} else {
      	TNCache.push(oneReality);
			}
		}		
		swapLayers();
	}


	private void handleSurface(int sector) {
		nextLayerFE = 0;
		TrailNode nodes[] = new TrailNode[OOCMap.S2];
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
      
      TrailNode allreadyHandled = nodes[oneReality.currentPos.o];
			if (allreadyHandled != null) {
			  allreadyHandled.mines.or(oneReality.mines);
			  allreadyHandled.dropMineAt.or(oneReality.mines);
      	TNCache.push(oneReality);
				continue;
			}
			
			TrailNode newRealityNode = oneReality.surfaceInSector(sector);
			if (newRealityNode != null) {
			  nodes[newRealityNode.currentPos.o] = newRealityNode;
			  nextLayer[nextLayerFE++] = newRealityNode;
			} else {
      	TNCache.push(oneReality);
			}
		}
		swapLayers();
	}


	private void handleMove(Direction direction) {
		nextLayerFE = 0;
    for (int i=0;i<currentLayerFE;i++) {
      TrailNode oneReality = currentLayer[i];
			TrailNode newRealityNode = oneReality.move(direction.direction);
			if (newRealityNode != null) {
				nextLayer[nextLayerFE++] = newRealityNode;
			} else {
      	TNCache.push(oneReality);
			}
		}		
		swapLayers();
	}

	public void calculatePotentialPositions() {
		realityCountTotal = 0;
		double totalPotentiality = 0.0;
		
		for (int i=0;i<OOCMap.S2;i++) {
    	heatMap[i] = 0;
      minesHeatMap2[i] = 0.0;
    	realityCountByPos[i] = 0;
    	trailCountByMineAtPosFE[i] = 0;
    	posPotentiality[i] = 0.0;
    }

		allMines.clear();
	  potentialPositions.clear();
	  for (int i=0;i<currentLayerFE;i++) {
      TrailNode reality = currentLayer[i];
			P realityCurrentPos = reality.currentPos;
			
			allMines.or(reality.mines);
			totalPotentiality+= reality.potentiality;
			posPotentiality[reality.currentPos.o]+=reality.potentiality;
			
			if (realityCountByPos[reality.currentPos.o] == 0) {
				// only add if it is a new pos
				potentialPositions.add(realityCurrentPos);
			}
			realityCountByPos[reality.currentPos.o] += reality.aggregate;
			realityCountTotal += reality.aggregate;
			
			for (int m=0;m<OOCMap.S2;m++) {
			  if (reality.mines.get(m)) {
			    if (trailCountByMineAtPosFE[m] < 20) {
			      trailCountByMineAtPos[20 * m + trailCountByMineAtPosFE[m]] = reality.currentPos;
			    }
			    trailCountByMineAtPosFE[m]++;
			  }
			}
    }

    updateMinesMap2(allMines);

    bestRealityPos = null;
    bestPot = Double.NEGATIVE_INFINITY;
	  for (P realityCurrentPos : potentialPositions) {
	    posPotentiality[realityCurrentPos.o] /= totalPotentiality;
      if (posPotentiality[realityCurrentPos.o] > bestPot) {
	      bestPot = posPotentiality[realityCurrentPos.o];
	      bestRealityPos = realityCurrentPos;
	    }
	    
	    heatMap[realityCurrentPos.o]+= posPotentiality[realityCurrentPos.o] * realityCountByPos[realityCurrentPos.o] * gauss[0];
      for (P pos : realityCurrentPos.squaredNeighbors) {
      	heatMap[pos.o]+= posPotentiality[realityCurrentPos.o]* realityCountByPos[realityCurrentPos.o] * gauss[1];
      }
    }
	}

	public void debugPotentiality() {
	  for (int i=0;i<OOCMap.S2;i++) {
	    if (Player.turn > 5 && realityCountByPos[i] > 0) {
	      System.err.println("@"+P.getFromOffset(i)+" = "+((int)(100.0 * posPotentiality[i])));
	    }
	  }
	}
	
  private static double gauss7[] = new double[] {
      0.00000067,  0.00002292,  0.00019117,  0.00038771,  0.00019117,  0.00002292,  0.00000067,
      0.00002292,  0.00078633,  0.00655965,  0.01330373,  0.00655965,  0.00078633,  0.00002292,
      0.00019117,  0.00655965,  0.05472157,  0.11098164,  0.05472157,  0.00655965,  0.00019117,
      0.00038771,  0.01330373,  0.11098164,  0.22508352,  0.11098164,  0.01330373,  0.00038771,
      0.00019117,  0.00655965,  0.05472157,  0.11098164,  0.05472157,  0.00655965,  0.00019117,
      0.00002292,  0.00078633,  0.00655965,  0.01330373,  0.00655965,  0.00078633,  0.00002292,
      0.00000067,  0.00002292,  0.00019117,  0.00038771,  0.00019117,  0.00002292,  0.00000067
  };

  
  private void updateMinesMap2(BitSet allMines) {
    for (int i=0;i<OOCMap.S2;i++) {
      if (allMines.get(i)) {
        P p = P.getFromOffset(i);
        
        for (int dy=-3;dy<=3;dy++) {
          for (int dx=-3;dx<=3;dx++) {
            P c = P.get(p.x+dx, p.y+dy);
            if (Player.map.isIsland(c)) continue;
            minesHeatMap2[c.o] += gauss7[(dx+3) * 7 + (dy+3)];
          }
        }
      }
    }
  }
  public void debugMinesHeat2() {
    if (Player.D_MINES) {
      Player.map.debugMap("Potential Mines ", pos -> allMines.get(pos.o) ? "!" : " ");
    }
    Player.map.debugMap("MINES HEAT 2", p -> ""+((int)(10 * minesHeatMap2[p.o])));
  }


  public void debug() {
    map.debugMap("Potential positions (tot="+potentialPositions.size()+" nodes="+currentLayerFE+" / "+realityCountTotal+")", p -> potentialPositions.contains(p) ? "@" : " ");
  }

  public void teardownTurn() {
    this.calculatePotentialPositions();

    
    
    if (currentLayerFE > 1000 ) {
      // TODO nettoyer plus intelligement : les pos où il n'y a pas tant de réalité que ca, on peut les garder
      aggregatePositions();
    }
  }


	void aggregatePositions() {
	  System.err.println("AGGREGATE ...");
		BitSet mines[] = new BitSet[OOCMap.S2];
    BitSet dropMineAt[] = new BitSet[OOCMap.S2];
		for (int i=0;i<OOCMap.S2;i++) {
			mines[i] = new BitSet(OOCMap.S2);
			dropMineAt[i] = new BitSet(OOCMap.S2);
		}
		for (int i=0;i<currentLayerFE;i++) {
			TrailNode node = currentLayer[i];
			mines[node.currentPos.o].or(node.mines);
			dropMineAt[node.currentPos.o].or(node.dropMineAt);
		}
		
		clearCurrentLayer();
		
		for (P pos : potentialPositions) {
			TrailNode newReality = TNCache.pop();
			newReality.currentPos = pos;
			newReality.mines.or(mines[pos.o]);
      newReality.dropMineAt.or(dropMineAt[pos.o]);
			
			newReality.aggregate = realityCountByPos[pos.o];
		  currentLayer[currentLayerFE++] = newReality;
		}
	}

  public void handleSonarResult(int sector, SonarResult result) {
    if (result == SonarResult.ABSENT) {
      handleSonarNOTInSector(sector);
    } else {
      handleSonarInSector(sector);
    }
  }

  public void setDebug(boolean b) {
    this.debug = b;
  }


	public void clearCurrentLayer() {
		TNCache.restitute(currentLayer, currentLayerFE);
		currentLayerFE = 0;
	}


	public void updateStatisticalDamageAt(TorpedoHitStat stat, P target) {
		double totalDamage = 0.0;
		int potentialHitCount = 0;
		
		int first[] = new int[OOCMap.S2];
		int second[] = new int[OOCMap.S2];
		
		// handle potential direct hits
		if (realityCountByPos[target.o] > 0) {
		  potentialHitCount+=1;
		  first[target.o] = 2;
		  //stat.potentialHits.add(target);
		  totalDamage += 2 * posPotentiality[target.o] * realityCountByPos[target.o];
		}
		
		// handle potential neighbors hit
		for (P pos : target.squaredNeighbors) {
		  if (realityCountByPos[pos.o] > 0) {
		    potentialHitCount+=1;
	      first[pos.o] = 1;
	      //stat.potentialHits.add(pos);
		    totalDamage += 1 * posPotentiality[target.o] * realityCountByPos[pos.o];
		  }
		}

		if (stat.firstHit != null) {
		  target = stat.firstHit;
	    if (realityCountByPos[target.o] > 0) {
	      second[target.o] = 2;
	    }
	    
	    // handle potential neighbors hit
	    for (P pos : target.squaredNeighbors) {
	      if (realityCountByPos[pos.o] > 0) {
	        second[pos.o] = 1;
	      }
	    }
		} else {
		  stat.firstHit = target;
		}
		
		// min of the sum on the potential positions !
		int minimal = 4;
		for (P potential : potentialPositions) {
		  minimal = Math.min(minimal, first[potential.o]+ second[potential.o]);
		}
		stat.minimalDamage = minimal;
		
		stat.potentialDamage += totalDamage / realityCountTotal;
		stat.potentialCellHitCount += potentialHitCount;
		
		
	}
}
