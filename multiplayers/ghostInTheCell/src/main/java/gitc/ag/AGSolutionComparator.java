package gitc.ag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gitc.simulation.actions.Action;

public class AGSolutionComparator {

  public static AGSolution compare(AGSolution... solutions) {
    List<AGSolution> solutionsList = new ArrayList<>();
    for (AGSolution sol : solutions) {
      solutionsList.add(sol);
    }
    return compare(solutionsList);
  }
  
  public static AGSolution compare(List<AGSolution> solutions) {
    List<AGSolution> sortedSolutions = solutions.stream()
      .sorted((s1, s2) -> {return Double.compare(s2.energy, s1.energy);})
      .collect(Collectors.toList());
    
    String name                   = "name:  ";
    String energy                 = "nrj:   ";
    String separator              = "------ ";
    String unitScore              = "U:     ";
    String productionScore        = "prod:  ";
    String influenceScore         = "inf:   ";
    String bombRemainingScore     = "bomb:  ";
    String factoryCountScore      = "fac:   ";
    String positioningScore       = "pos:   ";
    String troopsInTransitScore   = "tInT:  ";
    String troopsConvergenceScore = "tConv: ";
    String distanceBetweenFactoryScore = "dist:  ";
    String frontBackScore              = "F/B:   ";
    
    for (AGSolution solution : sortedSolutions) {
      name+=String.format("%1$10s", solution.name);
      energy+=String.format("%1$10f", solution.energy);
      separator+="----------";
      unitScore+=String.format("%1$10.4f", solution.unitScore);
      productionScore += String.format("%1$10.4f", solution.productionScore);
      influenceScore += String.format("%1$10.4f", solution.influenceScore);
      bombRemainingScore += String.format("%1$10.4f", solution.bombRemainingScore);
      factoryCountScore += String.format("%1$10.4f", solution.factoryCountScore);
      positioningScore += String.format("%1$10.4f", solution.positioningScore);
      troopsInTransitScore += String.format("%1$10.4f", solution.troopsInTransitScore);
      troopsConvergenceScore += String.format("%1$10.4f", solution.troopsConvergenceScore);
      distanceBetweenFactoryScore+= String.format("%1$10.4f", solution.distanceBetweenFactoryScore);
      frontBackScore+=String.format("%1$10.4f", solution.frontBackScore);
    }
    System.err.println(name);
    System.err.println(energy);
    System.err.println(separator);
    System.err.println(unitScore);
    System.err.println(productionScore);
    System.err.println(influenceScore);
    System.err.println(bombRemainingScore);
    System.err.println(factoryCountScore);
    System.err.println(positioningScore);
    System.err.println(troopsInTransitScore);
    System.err.println(troopsConvergenceScore);
    System.err.println(distanceBetweenFactoryScore);
    System.err.println(frontBackScore);
    
    int i = 0;
    for (AGSolution solution : sortedSolutions) {
      System.err.println("");
      System.err.println("Solution "+solution.name);
      System.err.println("------------");
      for (Action action : solution.players.get(0).turnActions[0].actions) {
        System.err.println(action);
      }
    }
    
    return sortedSolutions.get(0);
  }
}
