package meanmax.ai.ag2;

import meanmax.Game;
import meanmax.Player;
import meanmax.ai.eval.Eval;
import meanmax.entities.Destroyer;
import meanmax.entities.Doof;
import meanmax.entities.Entity;
import meanmax.entities.Reaper;
import meanmax.entities.Wreck;
import meanmax.simulation.Action;
import meanmax.simulation.Simulation;
import trigo.Position;

public class AGSolution implements Comparable<AGSolution> {
  public static final int DEPTH = 4;
  
  public static final int GENES_COUNT = 2;
  public static final int ACTIONS = 9;
  private static final int MUTATION_RATE = 20;
  double patience[] = new double[] { 1.0, 0.9, 0.8, 0.7, 0.6 };

  private static Simulation simulation = new Simulation();

  public double genes[][][] = new double[DEPTH][3][GENES_COUNT];
  public Action actions[][] = new Action[DEPTH][ACTIONS];
  
  
  public double energies[] = new double[DEPTH];
  public double energy = Double.NEGATIVE_INFINITY;
  public String messages[] = new String[3];
  private Eval eval;
  
  public AGSolution(Eval eval) {
    this.eval = eval;
    for (int j = 0; j < DEPTH; j++) {
      for (int i = 0; i < ACTIONS; i++) {
        actions[j][i] = new Action();
      }
    }
  }

  public boolean isBetterThan(AGSolution other) {
    return this.energy > other.energy;
  }
  
  public void evaluateRandom() {
    energy = 0.0;
    for (int depth=0;depth<AGSolution.DEPTH;depth++) {
      randomOneDepth(depth);
      actionForDummyPlayers(depth);
      simulation.simulate(actions[depth]);
      updateEnergy(depth);
    }
    Game.restore();
  }

  public void evaluate() {
    energy = 0.0;
    for (int depth=0;depth<AGSolution.DEPTH;depth++) {
      allGenesToActions(depth);
      actionForDummyPlayers(depth);
      simulation.simulate(actions[depth]);
      updateEnergy(depth);
    }
    Game.restore();
  }

  private void allGenesToActions(int depth) {
    for (int l=0;l<3;l++) {
      geneToAction(l, genes[depth][l], actions[depth][l]);
      geneToAction(l, genes[depth][l], actions[depth][l]);
    }
  }

  private void updateEnergy(int depth) {
    energies[depth] = eval.eval();
    energy += patience[AGSolution.DEPTH-1] * energies[depth];
  }
  
  public void reevaluate() {
    energy = 0.0;
    for (int depth=0;depth<AGSolution.DEPTH;depth++) {
      simulation.simulate(actions[depth]);
      updateEnergy(depth);
    }
  }

  private void randomOneDepth(int depth) {
    for (int l=0;l<3;l++) {
      genes[depth][l][0] = Game.random.nextDouble();
      genes[depth][l][1] = Game.random.nextDouble();
      geneToAction(l, genes[depth][l], actions[depth][l]);
    }
  }
  
  private void geneToAction(int looter, double genes[], Action action) {
    if (looter == Game.REAPER) {
      reaperGeneToAction(genes, action);
    } else if (looter == Game.DESTROYER) {
      destroyerGeneToAction(genes, action);
    } else {
      doofGeneToAction(genes, action);
    }
  }
  
  private void reaperGeneToAction(double[] genes, Action action) {
    Reaper reaper = Game.players[0].reaper;
    if (genes[0] < -1.0/*no tar*/) {
      double range = 2000.0 * genes[0] / 0.2;
      action.thrust = -1;
      randomPosition(reaper, action, range, genes[1]);
    } else if (genes[0] < 0.5) {
      Game.dummy.dummyReaper(action, Game.players[0]);
    } else {
      geneToMove(reaper, (genes[0] - 0.5) / ( 1.0 - 0.5), genes[1], action);
    }
  }

  private void destroyerGeneToAction(double[] genes, Action action) {
    Destroyer destroyer = Game.players[0].destroyer;
    if (genes[0] < 0.2) {
      double range = 2000.0 * genes[0] / 0.2;
      action.thrust = -1;
      randomPosition(destroyer, action, range, genes[1]);
    } else {
      geneToMove(destroyer, (genes[0] - 0.2) / ( 1.0 - 0.2), genes[1], action);
    }
  }

  private void doofGeneToAction(double[] genes, Action action) {
    Doof doof = Game.players[0].doof;
    if (genes[0] < 0.2) {
      double range = 2000.0 * genes[0] / 0.2;
      action.thrust = -1;
      randomPosition(doof, action, range, genes[1]);
    } else {
      geneToMove(doof, (genes[0] - 0.2) / ( 1.0 - 0.2), genes[1], action);
    }
  }

  private void geneToMove(Entity entity, double thrustRnd, double angleRnd, Action action) {
    int thrust;
    if (thrustRnd < 0.2) {
      thrust = 0;
    } else if (thrustRnd < 0.5) {
      thrust = (int)(300 * (thrustRnd-0.2) / (0.5-0.2));
    } else {
      thrust = 300;
    }
    randomPosition(entity, action, 2000, angleRnd);
    action.thrust = thrust;
  }
  
  private void randomPosition(Entity entity, Action action, double range, double angleRnd) {
    double angle = angleRnd * 2 * Math.PI;
    action.target.x = entity.position.x + range * Math.cos(angle);
    action.target.y = entity.position.y + range * Math.sin(angle);
  }
  
  public void actionForDummyPlayers(int turn) {
    Action[] actionsAtDepth = actions[turn];
    for (int i=1;i<3;i++) {
      Player player = Game.players[i];
      Game.dummy.dummyReaper(actionsAtDepth[3*i],     player);
      Game.dummy.dummyDestroyer(actionsAtDepth[3*i+1], player);
      Game.dummy.dummyDoof(actionsAtDepth[3*i+2],    player);
    }
  }

  public void copyFrom(AGSolution sol) {
    this.energy = sol.energy;
    
    for (int j = 0; j < DEPTH; j++) {
      for (int i=0;i<3*3;i++) {
        actions[j][i].copyFrom(sol.actions[j][i]);
      }
    }
  }

  public void output() {
    Wreck wreck = null; //EvalV2.getBestWreck();
    Destroyer destroyer = Game.players[0].destroyer;
    
    Wreck wreck2 = null;
    boolean foundWreck2 = false;
    for(int i=0;i<Game.wrecks_FE;i++) {
      wreck2 = Game.wrecks[i];
      if (wreck2.dead) continue;
      if (destroyer.isInRange(wreck2, 1.5 * wreck2.radius))  {
        foundWreck2 = true;
        break;
      }
    }
    
    System.out.println(actions[0][0] + (wreck!=null ? " "+wreck.unitId : " R"));
    System.out.println(actions[0][1] + (foundWreck2 ? " "+wreck2.unitId : " D"));
    System.out.println(actions[0][2] + " D");
  }

  public static AGSolution createFake() {
    AGSolution fake = new AGSolution(null);
    fake.energy = Double.NEGATIVE_INFINITY;
    return fake;
  }

  public static void crossOverAndMutate(AGSolution child1, AGSolution child2, AGSolution parent1, AGSolution parent2) {
    child1.energy = Double.NEGATIVE_INFINITY;
    child2.energy = Double.NEGATIVE_INFINITY;
    
    for (int depth=0;depth<DEPTH;depth++) {
      for (int l=0;l<3;l++) {
        for (int g=0;g<GENES_COUNT;g++) {
          double beta = Game.random.nextDouble();
          child1.genes[depth][l][g] = beta * parent1.genes[depth][l][g] + (1.0-beta) * parent2.genes[depth][l][g];
          child2.genes[depth][l][g] = beta * parent2.genes[depth][l][g] + (1.0-beta) * parent1.genes[depth][l][g];
        }
      }
    }
    child1.mutate();
    child2.mutate();
  }

  public void mutate() {
    for (int depth=0;depth<DEPTH;depth++) {
      for (int l=0;l<3;l++) {
        for (int g=0;g<GENES_COUNT;g++) {
          if (Game.random.nextInt(100) < MUTATION_RATE) {
            genes[depth][l][g] = Game.random.nextDouble();
          }
        }
      }
    }
  }

  private static void forceMutate(AGSolution solution, int looterIndex) {
    for (int i = 0; i < DEPTH; i++) {
        solution.actions[i][looterIndex].target.x += (100.0 - 200.0 * Game.random.nextDouble());
        solution.actions[i][looterIndex].target.y += (100.0 - 200.0 * Game.random.nextDouble());
        // retarget outof range skills
        if (solution.actions[i][looterIndex].thrust < 0) {
          double l = Game.entities[looterIndex].position.dist(solution.actions[i][looterIndex].target);
          if (l > 2000) {
              double coef = (int) l / 2000.0;
              // bring back in [ 0 2000]
              double nl = l - coef * 2000.0;
              solution.actions[i][looterIndex].target.x *= (nl / l);
              solution.actions[i][looterIndex].target.y *= (nl / l);
          }
        }
    }
  }

  private static void targetSkill(Position target, Position position) {
    double angle = 2 * Math.PI * Game.random.nextDouble();
    double length = 2000.0 * Game.random.nextDouble();
    target.x = position.x + length * Math.cos( angle );
    target.y = position.y + length * Math.sin( angle );
  }

  @Override
  public int compareTo(AGSolution o) {
    return Double.compare(o.energy, this.energy); // best energy first !
  }

  public void shiftAndCopy(AGSolution model) {
    energy = Double.NEGATIVE_INFINITY;
    for (int depth=0;depth<DEPTH-1;depth++) {
      actions[depth][0].copyFrom(model.actions[depth+1][0]);
      actions[depth][1].copyFrom(model.actions[depth+1][1]);
      actions[depth][2].copyFrom(model.actions[depth+1][2]);
    }
    randomOneDepth(DEPTH-1);
  }

  public void debugOilPrediction() {
    for (int j=0;j<AGSolution.DEPTH;j++) {
      System.err.println("at depth "+j);
      if (actions[j][3+2].thrust == -1) {
        System.err.print("p2 will oil -> ");
        actions[j][3+2].debug();
      }
      if (actions[j][3*2+2].thrust == -1) {
        System.err.print("p3 will oil -> ");
        actions[j][3*2+2].debug();
      }
    }
  }
  
  public void debug() {
    System.err.println("best solution : ");
    for (int j=0;j<AGSolution.DEPTH;j++) {
      System.err.println("at depth "+j);
      simulation.simulate(actions[j]);
      for (int i=0;i<3;i++) {
        actions[j][i].debug();
      }
      eval.eval();
      eval.debug();
    }
    System.err.println("total score : "+Game.players[0].score);
    System.err.println("total rage : "+Game.players[0].rage);
    
    Game.restore();
  }

  public void resetEnergy() {
    energy = Double.NEGATIVE_INFINITY;    
  }

}
