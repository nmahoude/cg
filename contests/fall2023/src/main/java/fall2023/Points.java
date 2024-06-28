package fall2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Points {

  public static int deltaPoints(Scan currentScans, Scan myGlobalScans, Scan oppGlobalScans) {
    Scan work = new Scan();
    work.copyFrom(myGlobalScans);
    
    int currentPoint = myGlobalScans.score();
    for (int i=4;i<16;i++) {
      if (currentScans.contains(i)) work.scan(i);
    }
    work.updateFirsts(oppGlobalScans);
    int nextPoints = work.score();
    
    return nextPoints - currentPoint;
  }

  
  
  /**
   * predict if we can win the game from the current position
   * if everybody go up now !
   * 
   * @param state
   * @return 
   */
  public static boolean willIWin(State state) {
    int estimatedTurnsToUp[] = new int[4];
    for (int i=0;i<4;i++) {
      estimatedTurnsToUp[i] = state.dronesById[i].estimateTurnsToSurface();
    }
    
    
    Scan myWork = new Scan();
    Scan oppWork = new Scan();
    myWork.copyFrom(state.myScans);
    oppWork.copyFrom(state.oppScans);

    List<Drone> dronesSortedToSuface = new ArrayList<>(Arrays.asList(state.dronesById));
    dronesSortedToSuface.sort((d1, d2) -> Integer.compare(estimatedTurnsToUp[d1.id], estimatedTurnsToUp[d2.id]));
    
    System.err.println("Drone order to surface : ");
    for (Drone drone : dronesSortedToSuface) {
      System.err.println(drone+" ==> "+estimatedTurnsToUp[drone.id]);
    }    
    
    int currentTime = 0;
    for (Drone drone : dronesSortedToSuface) {
      if (estimatedTurnsToUp[drone.id] == currentTime) {
        
      } else {
        myWork.updateFirsts(oppWork);
        oppWork.updateFirsts(myWork);
        currentTime = estimatedTurnsToUp[drone.id];
      }
      
      if (state.isMine(drone)) {
         myWork.append(drone.currentScans);
      } else {
        oppWork.append(drone.currentScans);
      }
    }
    myWork.updateFirsts(oppWork);
    oppWork.updateFirsts(myWork);

    // now fill the last fishes (opp before me to estimate the worst)
    for (int l = 4; l < 16; l++) {
      if (!state.fishPresent[l]) continue;
      if (!oppWork.contains(l)) oppWork.scan(l);
    }
    oppWork.updateFirsts(myWork);
    
    for (int l = 4; l < 16; l++) {
      if (!state.fishPresent[l]) continue;
      if (!myWork.contains(l)) myWork.scan(l);
    }
    myWork.updateFirsts(oppWork);

    System.err.println("My score  = "+myWork.score());
    System.err.println("Opp score  = "+oppWork.score());
    
    return myWork.score() > oppWork.score();
  }



  public static void allCombinations(State state) {
    Scan myWork = new Scan();
    Scan oppWork = new Scan();
    if (Player.DEBUG_EXPECTED_POINTS) {
      for (int i = 0; i < 15; i++) {
        myWork.copyFrom(state.myScans);
        oppWork.copyFrom(state.oppScans);
        if ((i & 0b1) != 0)
          myWork.append(state.myDrones[0].currentScans);
        if ((i & 0b10) != 0)
          myWork.append(state.myDrones[1].currentScans);
        if ((i & 0b100) != 0)
          oppWork.append(state.oppDrones[0].currentScans);
        if ((i & 0b1000) != 0)
          oppWork.append(state.oppDrones[1].currentScans);

        myWork.updateFirsts(oppWork);
        oppWork.updateFirsts(myWork);
        int myScore = myWork.score();
        int oppScore = oppWork.score();

        System.err.println("Expected for " + Integer.toBinaryString(i) + " : " + myScore + " / " + oppScore);
      }

    }
  }



  public static void someDebug(State state) {
    System.out.println("///////////////////////////////////////////");
    System.out.println("Some debug ! ");

    for (Drone d: state.dronesById) {
      System.out.println("Estimated turns to surface : "+d.estimateTurnsToSurface());
    }
    
    
    Scan myWork = new Scan();
    Scan oppWork = new Scan();
    myWork.copyFrom(state.myScans);
    oppWork.copyFrom(state.oppScans);
    
    for (Drone d : state.oppDrones) {
      oppWork.append(d.currentScans);
    }
    oppWork.updateFirsts(myWork);
    
    System.out.println("En remontant now, il aura "+oppWork.score());
    
    
    // let's say I get everything now
    for (Drone d : state.myDrones) {
      myWork.append(d.currentScans);
    }
    myWork.updateFirsts(oppWork);
    System.out.println("Mon score actuel serait alors "+myWork.score());
    
    myWork.fill(state, state.myDrones);
    myWork.updateFirsts(oppWork);
    
    System.out.println("Mon meilleur score serait alors "+myWork.score());

    // 
    System.out.println("   WHAT IF on remonte en meme temps");
    myWork.copyFrom(state.myScans);
    oppWork.copyFrom(state.oppScans);
    
    for (Drone d : state.myDrones) {
      myWork.append(d.currentScans);
    }
    for (Drone d : state.oppDrones) {
      oppWork.append(d.currentScans);
    }
    oppWork.updateFirsts(myWork);
    myWork.updateFirsts(oppWork);
    
    System.out.println("En remontant en meme temps, il aurait "+oppWork.score());
    System.out.println("En remontant en meme temps, j'aurais "+myWork.score());
    
    myWork.fill(state, state.myDrones);
    myWork.updateFirsts(oppWork);
    System.out.println("Mon meilleur score serait alors "+myWork.score());
    
    
    System.out.println("///////////////////////////////////////////");
  }
}
