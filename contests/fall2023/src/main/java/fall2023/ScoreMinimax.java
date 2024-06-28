package fall2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScoreMinimax {
  int estimatedTurnsToUp[] = new int[4];
  Scan meFirstAfter = new Scan();
  Scan oppSecondAfter = new Scan();
  Scan meSecond = new Scan();
  Scan oppFirst= new Scan();
  private List<Drone> dronesSortedToSuface;
  private State state;

  public boolean willWinIfGoUp = false;
  public boolean willWinAnyWay = false;
  public boolean willLoseIfNotUp = false;
  public boolean willLoseWithThisState = false;
  
  public List<Drone> think(State state) {
    willWinIfGoUp = false;
    willWinAnyWay = false;
    willLoseIfNotUp = false;
    willLoseWithThisState = false;
    
    
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println("DEBUG pseudo MINIMAX ");
    this.state = state;
    for (int i=0;i<4;i++) {
      estimatedTurnsToUp[i] = state.dronesById[i].estimateTurnsToSurface();
    }
    dronesSortedToSuface = new ArrayList<>(Arrays.asList(state.dronesById));
    dronesSortedToSuface.sort((d1, d2) -> Integer.compare(estimatedTurnsToUp[d1.id], estimatedTurnsToUp[d2.id]));

    if (Player.DEBUG_SCORE_MINIMAX)  {
      for (Drone d: dronesSortedToSuface) {
        System.err.println("Order to surface : "+d+" => "+estimatedTurnsToUp[d.id]);
      }
    }
    
    boolean oppDescending = true;
    for (Drone opp: state.oppDrones) {
      if (state.previousState.dronesById[opp.id].pos.y > opp.pos.y) oppDescending = false;
    }
    
    List<Drone> goUps = new ArrayList<>();
    
    
    // will we win anyway ???
    if (State.agentId == 0) {
      doCombination(0b1010);
      if (meSecond.score() > oppFirst.score()) {
        System.err.println("     GOOOD ! we will win anyway");
        willWinAnyWay = true;
        return Collections.emptyList();
      }
      
//      doCombination(0b0101);
//      if (oppDescending && meSecond.score() > oppFirst.score()) {
//        System.err.println("     GOOOD ! we will win anyway");
//        willWinIfGoUp = true;
//        return Collections.emptyList();
//      }
      
    } else {
      doCombination(0b0101);
      if (meSecond.score() > oppFirst.score()) {
        System.err.println("     GOOOD ! we will win anyway");
        willWinAnyWay = true;
        return Collections.emptyList();
      }
      
      
//      doCombination(0b1010);
//      if (oppDescending && meSecond.score() > oppFirst.score()) {
//        System.err.println("     GOOOD ! we will win anyway");
//        willWinIfGoUp = true;
//        return Collections.emptyList();
//      }
    }
    
    
    doCombination(0b1111);
    if (meFirstAfter.score() < oppSecondAfter.score()) {
      if (Player.DEBUG_SCORE_MINIMAX) System.err.println("     ALready DEAD ? il faut faire qqchose ");
      goUps.add(state.myDrones[0]);
      goUps.add(state.myDrones[1]);
      willLoseWithThisState = true;
      willLoseIfNotUp = true;
    } else if (meSecond.score() > oppFirst.score()) {
      if (Player.DEBUG_SCORE_MINIMAX) System.err.println("     GOOD ! il faut remonter ");
      goUps.add(state.myDrones[0]);
      goUps.add(state.myDrones[1]);
      willWinIfGoUp = true;
    } else {
      // check with only one Drone if we lose!
      if (State.agentId == 0) {
        doCombination(0b1110);
        if (meFirstAfter.score() < oppSecondAfter.score()) {
          goUps.add(state.myDrones[0]);
          willLoseIfNotUp = true;
        }

        doCombination(0b1011);
        if (meFirstAfter.score() < oppSecondAfter.score()) {
          goUps.add(state.myDrones[1]);
          willLoseIfNotUp = true;
        }
        
      } else {
        doCombination(0b1101);
        if (meFirstAfter.score() < oppSecondAfter.score()) {
          goUps.add(state.myDrones[0]);
          willLoseIfNotUp = true;
        }

        doCombination(0b0111);
        if (meFirstAfter.score() < oppSecondAfter.score()) {
          goUps.add(state.myDrones[1]);
          willLoseIfNotUp = true;
        }

      }
    }
    
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println("basic goUps" + goUps);
    // now check if we can wait
    List<Drone> definitiveGoUps = new ArrayList<>();
    for (Drone myGoUp : goUps) {
      boolean canWait = true;
      for (Drone opp : state.oppDrones) {
        if (estimatedTurnsToUp[myGoUp.id] +2 > estimatedTurnsToUp[opp.id]) canWait = false;
      }
      if (!canWait) {
        definitiveGoUps.add(myGoUp);
      }
    }
    
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println("Definitive goup : "+definitiveGoUps);
    return definitiveGoUps;
  }


  private void doCombination(int droneMask) {
    Scan myWork = new Scan(state.myScans);
    Scan oppWork = new Scan(state.oppScans);

    
    for (int i=0;i<4;i++) {
      int estimated = state.dronesById[i].estimateTurnsToSurface();
      if (((1 << i) & droneMask) == 0) estimated+=2;
      estimatedTurnsToUp[i] = estimated;
    }
    dronesSortedToSuface = new ArrayList<>(Arrays.asList(state.dronesById));
    dronesSortedToSuface.sort((d1, d2) -> Integer.compare(estimatedTurnsToUp[d1.id], estimatedTurnsToUp[d2.id]));
    
    int currentTime = 0;
    for (Drone drone : dronesSortedToSuface) {
      int estimated = estimatedTurnsToUp[drone.id];
      
      if (estimated == currentTime) {
        
      } else {
        myWork.updateFirsts(oppWork);
        oppWork.updateFirsts(myWork);
        currentTime = estimated;
      }
      
      if (state.isMine(drone)) {
         myWork.append(drone.currentScans);
      } else {
        oppWork.append(drone.currentScans);
      }
    }
    myWork.updateFirsts(oppWork);
    oppWork.updateFirsts(myWork);
    int myScoreUp = myWork.score();
    int oppScoreUp = oppWork.score();
    
    // force all fishes now (mine, then opp, to see my best score)
    
    meFirstAfter.copyFrom(myWork);
    oppSecondAfter.copyFrom(oppWork);
    
    meFirstAfter.fill(state, state.myDrones);
    meFirstAfter.updateFirsts(oppSecondAfter);
    
    oppSecondAfter.fill(state, state.oppDrones);
    oppSecondAfter.updateFirsts(meFirstAfter);

    
    meSecond.copyFrom(myWork);
    oppFirst.copyFrom(oppWork);

    oppFirst.fill(state, state.oppDrones);
    oppFirst.updateFirsts(meSecond);
    meSecond.fill(state, state.myDrones);
    meSecond.updateFirsts(oppFirst);

    
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println(""+droneMask);
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println(" current => "+myScoreUp+" / "+ oppScoreUp);
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println(" final (me first) =" + meFirstAfter.score()+" / "+oppSecondAfter.score());
    if (Player.DEBUG_SCORE_MINIMAX) System.err.println(" final (opp first) =" + meSecond.score()+" / "+oppFirst.score());
  }
}
