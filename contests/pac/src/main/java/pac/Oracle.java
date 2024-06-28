package pac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pac.agents.Pacman;
import pac.agents.Pellet;
import pac.map.Pos;

public class Oracle {
  List<Pos> lastTurnPellets = new ArrayList<>();
  HashMap<Integer, Set<Pos>> potentialPositions = new HashMap<>();
  public int cooldown[] = new int[10];
  public int speedTurnsLeft[] = new int[10];

  private Pos predictedPosWhenCharging[] = new Pos[10]; // quand il recharge, il doit rester à la même place. si on constate qu'il n'est plus la,il n'a pas rechargé 
  
  int lastOppScore = 0;
  
  private State state;
  
  public Oracle() {
    for (int i=0;i<10;i++) {
      potentialPositions.put(i, new HashSet<>());
      cooldown[i] = 0;
      speedTurnsLeft[i] = 0;
      predictedPosWhenCharging[i] = Pos.INVALID; // on ne sait pas où
    }
  }
  
  public Set<Pos> potentialPosOf(int index) {
    return potentialPositions.get(index);
  }
  
  public void update(State state) {
    this.state = state;
    
    resetCooldownWhenMovingPacmen();
    
    
    for (int i=5;i<5+state.maxPacmen;i++) {
      if (state.pacmen[i].isDead()) continue;

      updatePacman(state, state.pacmen[i], potentialPositions.get(i), i);
      if (state.pacmen[i].pos == Pos.INVALID && potentialPositions.get(i).size() == 1) {
        state.pacmen[i].pos = potentialPositions.get(i).iterator().next(); 
        if (Player.debugOracle()) {
          System.err.println("Can't see "+i+ " but only 1 pos is possible ! Updating pacman pos with " + state.pacmen[i].pos);
        }
      }
    }
    updateFromSuperPelletsDisapearance(state);
    updateLastStateSuperPellets();
    
    if (Player.BR_REMOVE_PELLETS_FROM_HIDDEN_POSITIONS) {
    	removePelletsFromEnnemyPositions();
    }
    
    if (Player.debugOracle()) {
      debugSpeedPatterns(state);
    }

    updateCooldowns();
    
    
    if (Player.DEBUG_ORACLE_POS || Player.debugOracle()) {
      for (int i=5;i<5+state.maxPacmen;i++) {
        if (state.pacmen[i].isDead()) continue;

        debugPotentialPosition(i);
      }
    }
    
    //updatePelletsValue(state);
  }

  private void removePelletsFromEnnemyPositions() {
    for (int o=5;o<5+state.maxPacmen;o++) {
      if (state.pacmen[o].isDead()) continue;
      Set<Pos> positions = potentialPositions.get(o);
      if (positions.size() == 1) {
      	state.pellets[positions.iterator().next().offset].value = 0.0;
      }
    }  	
	}

	/*
   * if we thought the pac was recharging but he moved, 
   * then, obviously he hadn't
   */
  private void resetCooldownWhenMovingPacmen() {
    for (int o=5;o<5+state.maxPacmen;o++) {
      if (state.pacmen[o].isDead()) continue;


      
      if (predictedPosWhenCharging[o] != Pos.INVALID) {
        if (Player.debugOracle()) {
          System.err.println("Testing reset cooldown of "+o);
          System.err.println(" predicted pos is "+predictedPosWhenCharging[o]);
          System.err.println(" current pos is "+state.pacmen[o].pos);
        }

        if (state.pacmen[o].pos != Pos.INVALID) {
          // do nothing, we have updated because seeing him
          predictedPosWhenCharging[o] = Pos.INVALID;
          continue;
        }
        
        // check if he should STILL be in our view range (we may have move and not him, so complicated! )
        boolean ko = false;
        for (int i=0;i<5 && !ko;i++) {
          Pacman myPac = state.pacmen[i];
          if (myPac.pos == Pos.INVALID) continue;

          for (int d=0;d<4 && !ko;d++) {
            Pos current = myPac.pos.neighbors[d];
            while (current != Pos.INVALID && current != myPac.pos) {
              if (current == predictedPosWhenCharging[o]) {
                ko = true;
              }
              current = current.neighbors[d];
            }
          }
        }
        
        predictedPosWhenCharging[o] = Pos.INVALID;
        if (ko) {
          // we should have saw him, but didn't. it means he moved and so can't have recharged !
          if (Player.debugOracle()) {
            System.err.println("we should have saw him ("+o+"), but didn't. it means he moved and so can't have recharged !");
          }
          cooldown[o] = 1; // hack to avoid considering he will charge this turn
          speedTurnsLeft[o] = 0;
        } else {
          if (Player.debugOracle()) {
            System.err.println("we moved out of range of his ("+o+")last position, so can't see it anymore !");
          }
        }
      }
    }
  }

  private void updateCooldowns() {
    for (int i=5;i<5+state.maxPacmen;i++) {
      if (state.pacmen[i].isDead()) continue;

      if (cooldown[i] == 0) {
        if (Player.debugOracle()) {
          System.err.println("His cooldown was 0, so now, It will speed");
        }
        // TODO prendre les RG en compte ici ! en fonction de la situation, recharger est pas la bonne maniere
        // think he will recharge
        cooldown[i] = 9;
        speedTurnsLeft[i] = 5;
        predictedPosWhenCharging[i] = state.pacmen[i].pos;
      } else {
        speedTurnsLeft[i] = Math.max(0, speedTurnsLeft[i]-1);
        cooldown[i] = Math.max(0,  cooldown[i]-1);
      }
    }
  }

  private void debugSpeedPatterns(State state) {
    System.err.println("Predicted Action");
    for (int i=5;i<5+state.maxPacmen;i++) {
      if (state.pacmen[i].isDead()) continue;

      System.err.print(" pacman "+(i-5)+" => ");
      if (cooldown[i] == 0) System.err.println("Recharging");
      else if (speedTurnsLeft[i] > 0) System.err.println("Full speed (cd="+cooldown[i]+",stl = "+speedTurnsLeft[i]+")");
      else System.err.println("on Cooldown (cd="+cooldown[i]+" / stl="+speedTurnsLeft[i]+")");
    }
  }

  public void debugPotentialPosition(int index) {
    if (state.pacmen[index].isDead()) {
      System.err.println("Pacman "+ (index-5)+ " is dead");
    }
    Player.map.debugMap("Potentiel positions of "+(index-5), pos -> potentialPositions.get(index).contains(pos) ? "0" : " ");
  }

  private void updatePelletsValue(State state) {
    if (Player.DEBUG_PELLET_DECAY) System.err.println("Update pellets value");
    if (state.opponentScore > lastOppScore) {
      Set<Pos> allPos = new HashSet<>();
      for (int i=5;i<5+state.maxPacmen;i++) {
        if (state.pacmen[i].isDead()) continue;

        allPos.addAll(potentialPositions.get(i));
      }
      double delta = 1.0 * (state.opponentScore - lastOppScore) / State.MAX_THEORICAL_SCORE;
      if (Player.DEBUG_PELLET_DECAY) System.err.println("Score has change, delta is "+delta);

      int count = 0, total =0;;
      for (Pos p : allPos) {
        Pellet pellet = state.pellets[p.offset];
        if (pellet != null) {
          total++;
          if (!state.visiblePellets.contains(p)) {
            pellet.value = Math.max(0.3, pellet.value - delta);
            count++;
          }
        }
      }
      if (Player.DEBUG_PELLET_DECAY) System.err.println("Update "+count+" pellets out of "+total);
    }
    lastOppScore = state.opponentScore;
  }


  private void updateLastStateSuperPellets() {
    lastTurnPellets.clear();
    for (Pos p : state.bigPellets) {
      lastTurnPellets.add(p);
    }
  }

  private void updatePacman(State state, Pacman pacman, Set<Pos> currentPositions, int index) {
    
    if (pacman.pos != Pos.INVALID) {
      // visible pacman
      currentPositions.clear();
      currentPositions.add(pacman.pos);
      if (Player.debugOracle()) { 
        System.err.println(" ********** ");
        System.err.println("I see "+(index-5)+", updating the speeding infos !");
        System.err.println(pacman.toString());
      }
      
      cooldown[index] = pacman.cooldown;
      speedTurnsLeft[index] = pacman.speedTurnsLeft;
      
      
      if (Player.debugOracle()) System.err.println("New speed/cooldown => "+speedTurnsLeft[index]+" / "+cooldown[index]);
    } else {
      // invisible pacman
      if (cooldown[index] == 0) {
        // on va considerer qu'il va SPEED, mais on est pas sur, dans le doute, on consider qu'il SPEED ET qu'il move
        expandCurrentPositions(currentPositions);
      } else {
        // move
        expandCurrentPositions(currentPositions);
        if (speedTurnsLeft[index] > 0) {
          expandCurrentPositions(currentPositions);
        }
      }

      removeCellThatISee(state, currentPositions);
    }
    
  }

  private void removeCellThatISee(State state, Set<Pos> currentPositions) {
    for (int i=0;i<5;i++) {
      Pacman myPac = state.pacmen[i];
      if (myPac.pos == Pos.INVALID) continue;

      currentPositions.remove(myPac.pos);
      for (int d=0;d<4;d++) {
        Pos current = myPac.pos.neighbors[d];
        while (current != Pos.INVALID && current != myPac.pos) {
          currentPositions.remove(current);
          current = current.neighbors[d];
        }
      }
    }
  }

  private void updateFromSuperPelletsDisapearance(State state) {
    if (Player.debugOracle()) {
      System.err.println("List of current superpellets");
      for (Pos superPellet : state.bigPellets) {
        System.err.print(""+superPellet+", ");
      }
      System.err.println();
      System.err.println("List of last turn superpellets");
      for (Pos superPellet : lastTurnPellets) {
        System.err.print(""+superPellet+", ");
      }
      System.err.println();
    }
    
    for (Pos superPellet : lastTurnPellets) {
      if (!state.bigPellets.contains(superPellet)) {
        if (Player.debugOracle()) System.err.println(" **** superpellets disapear at "+superPellet);
        // resolve who could eat the pellets since last turns
        boolean iateIt = false;
        for (int i=0;i<5;i++) {
          if (state.pacmen[i].pos == Pos.INVALID) continue;
          
          if (state.pacmen[i].pos.distance(superPellet) <= 1) {
            iateIt = true;
            break;
          }
        }
        if (!iateIt) {
          int bestI=-1; // no one
          for (int i=5;i<5+state.maxPacmen;i++) {
            if (state.pacmen[i].isDead()) continue;

            Set<Pos> potentialPos = potentialPositions.get(i);
            if (potentialPos.contains(superPellet)) {
              if (bestI == -1) {
                bestI = i;
              } else {
                if (Player.debugOracle()) System.err.println("     more than one : "+bestI +" and "+i);
                bestI = -2; // more than one
              }
            }
          }
          if (bestI>=0) {
            if (Player.debugOracle()) System.err.println("  Pacman "+bestI+" ate the superpellets last turn");
            Set<Pos> potentialPos = potentialPositions.get(bestI);
            potentialPos.clear();
            potentialPos.add(superPellet);
            for (Pos n : superPellet.neighbors) {
              if (n != Pos.INVALID) {
                potentialPos.add(n);
              }
            }
          } else {
            if (Player.debugOracle()) System.err.println("  More than one pacman or zero could ate it ("+bestI+")");
          }
        } else {
          if (Player.debugOracle()) System.err.println("  Seem like I ate it");
        }
      }
    }
  }


  private void expandCurrentPositions(Set<Pos> currentPositions) {
    Set<Pos> newPositions = new HashSet<>();
    for (Pos currentPosition : currentPositions) {
      for (Pos n : currentPosition.neighbors) {
        if (n == Pos.INVALID) continue;
        newPositions.add(n);
      }
    }
    currentPositions.addAll(newPositions);
  }

  public Pos getPotentialPosOf(int o) {
    Set<Pos> positions = potentialPositions.get(o);
    if (positions.size() == 1) {
      return positions.iterator().next();
    }
    return Pos.INVALID;
  }

  public void updateAfraidOffOnWorseCaseScenario(Pacman pacman, Pacman other) {
    other.cooldown = cooldown[other.index + 5];
    other.speedTurnsLeft = speedTurnsLeft[other.index+5];
    Set<Pos> allPositions = potentialPositions.get(other.index + 5);
    Pos worse = Pos.INVALID;
    for (Pos p : allPositions) {
      if (worse == Pos.INVALID || worse.distance(pacman.pos) > p.distance(pacman.pos)) {
        worse = p;
      }
    }
    other.pos = worse;
  }

}
