package meanmax.ai.ag;

import meanmax.Game;
import meanmax.Player;
import meanmax.ai.eval.EvalV2;
import meanmax.entities.Destroyer;
import meanmax.entities.SkillEffect;
import meanmax.entities.Wreck;
import meanmax.simulation.Action;
import meanmax.simulation.Simulation;
import trigo.Position;

public class AGSolution implements Comparable<AGSolution> {
  public static final int DEPTH = 3;
  public static final int ACTIONS = 9;
  private static final int ACTIONS_FOR_PLAYER0 = 3;
  private static final int INV_MUTATION_RATE = 100;
  double patience[] = new double[] { 1.0, 0.9, 0.8, 0.7 };

  private static Simulation simulation = new Simulation();

  public Action actions[][] = new Action[DEPTH][ACTIONS];
  public double energy = 0.0;
  public String messages[] = new String[3];
  private EvalV2 eval;
  
  public AGSolution(EvalV2 eval) {
    this.eval = eval;
    for (int j = 0; j < DEPTH; j++) {
      for (int i = 0; i < ACTIONS; i++) {
        actions[j][i] = new Action();
      }
    }
  }
  
  public void evaluate() {
    energy = 0.0;
    for (int j=0;j<AGSolution.DEPTH;j++) {
      crashTestDummies(j);
      simulation.simulate(actions[j]);
      energy += patience[AGSolution.DEPTH-1] * eval.eval();
    }
    Game.restore();
  }

  public void reevaluate() {
    energy = 0.0;
    for (int j=0;j<AGSolution.DEPTH;j++) {
      simulation.simulate(actions[j]);
      energy += patience[AGSolution.DEPTH-1] * eval.eval();
    }
  }

  public void random() {
    energy = 0.0;
    for (int j = 0; j < DEPTH; j++) {
        randomOneDepth(j);
    }

    // SKILLS
    sendOil(actions[0/*depth*/][Game.DOOF], 0);
    sendGrenade(actions[0/*depth*/][Game.DESTROYER], Game.players[0]);

    //sendTar(actions[0][Game.REAPER], Game.players[0]);
  }

  private void sendTar(Action action, int playerId) {
    if (Game.players[0].score < 30)
        return;
    if (Game.random.nextDouble() > 0.5)
        return;
    for (int i = 0; i < Game.wrecks_FE; i++) {
        Wreck wreck = Game.wrecks[i];
        if (wreck.dead)
            continue;
        if (wreck.water < 3)
            continue;
        if (wreck.isInRange(Game.players[playerId].reaper, wreck.radius) && !Game.players[playerId].reaper.isInDoofSkill() && !Game.players[0].reaper.isInTarSkill()) {
            action.thrust = -1;
            action.target.x = wreck.position.x;
            action.target.y = wreck.position.y;
            return;
        }
    }
  }

  private void randomOneDepth(int depth) {
    for (int i = 0; i < ACTIONS_FOR_PLAYER0; i++) {
      Action action = actions[depth][i];
      Position.random(action.target, Game.MAP_RADIUS);
      
      double thrustRand = Game.random.nextDouble();
      if (thrustRand < 0.5) {
          // full thrust
          action.thrust = 300;
      } else if (thrustRand < 0.9) {
          action.thrust = (int) (300.0 * Game.random.nextDouble());
      } else {
          // full break
          action.thrust = 0;
      }
    }
  }
  
  private void sendOil(Action action, int playerId) {
    if (Game.players[playerId].rage < 30) return;
    
    // TODO random in all wrecks, not the first one ...
    for (int i=0;i<Game.wrecks_FE;i++) {
      Wreck wreck = Game.wrecks[i];
      if (wreck.dead) continue;
      if (wreck.distance2(Game.players[playerId].doof) > 2000*2000) continue;
      if (Game.random.nextDouble() > 0.3) continue;
      double d1 = Game.players[(playerId + 0) % 3].reaper.distance2(wreck);
      double d2 = Game.players[(playerId + 1) % 3].reaper.distance2(wreck);
      double d3 = Game.players[(playerId + 2) % 3].reaper.distance2(wreck);
      if (d2 < wreck.radius * wreck.radius || d3 < wreck.radius*wreck.radius) {
        if (d1 > 4 * wreck.radius * wreck.radius) {
          boolean alreadAffected = false;
          for (int j=0;j<Game.skillEffects_FE;j++) {
            SkillEffect se = Game.skillEffects[j];
            if (se.type == Game.SKILL_EFFECT_OIL && wreck.isInRange(se, 1000)) {
              alreadAffected = true;
              break;
            }
          }
          if (!alreadAffected) {
            action.thrust = -1;
            action.target.x = wreck.position.x;
            action.target.y = wreck.position.y;
            
            return;
          }
        }
      }
    }
  }

  private void sendGrenade(Action action, Player player) {
    if (player.rage < 60) return;
    if (Game.random.nextDouble() < 0.8) return; // 80% to not throw
    
    action.thrust = -1;
    double angleRand = Game.random.nextDouble();
    double length = 2000.0 * Game.random.nextDouble();
    action.target.x = player.destroyer.position.x + length * Math.cos(2 * Math.PI * angleRand);
    action.target.y = player.destroyer.position.y + length * Math.sin(2 * Math.PI * angleRand);
    
    return;
  }

  
  private void useSkill(int l) {
    if (Game.players[0].rage >= Player.rages[l]) {
      double rand = Game.random.nextDouble();
      if (rand > 0.8) {
        Game.players[0].rage -= Player.rages[l];
        actions[0][l].thrust = -1; // 100% grenade
        targetSkill(actions[0][l].target, Game.entities[l].position);
      }
    }
  }

  /**
   * dummies for ennemies
   */
  public void crashTestDummies(int turn) {
    for (int i=1;i<3;i++) {
      Game.dummy.dummyReaper(actions[turn][3*i],     Game.players[i]);
      Game.dummy.dummyDestroyer(actions[turn][3*i+1], Game.players[i]);
      Game.dummy.dummyDoof(actions[turn][3*i+2],    Game.players[i]);
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
    child1.energy = 0.0;
    child2.energy = 0.0;
    
    for (int l=0;l<3;l++) {
      // skill versus move
      if (parent1.actions[0][l].thrust * parent2.actions[0][l].thrust < 0 ) {
        for (int i=0;i<DEPTH;i++) {
          child1.actions[i][l].target.x = parent1.actions[i][l].target.x;
          child1.actions[i][l].target.y = parent1.actions[i][l].target.y;
          child1.actions[i][l].thrust   = parent1.actions[i][l].thrust;
          child2.actions[i][l].target.x = parent2.actions[i][l].target.x;
          child2.actions[i][l].target.y = parent2.actions[i][l].target.y;
          child2.actions[i][l].thrust   = parent2.actions[i][l].thrust;
        }
        forceMutate(child1, l);
        forceMutate(child2, l);
      } else {
        for (int i=0;i<DEPTH;i++) {
          double beta = Game.random.nextDouble();
          getAcceptableTarget(child1.actions[i][l].target, beta, parent1.actions[i][l].target, parent2.actions[i][l].target);
          child1.actions[i][l].thrust = getAcceptableThrust(beta, parent1.actions[i][l].thrust, parent2.actions[i][l].thrust);
    
          getAcceptableTarget(child2.actions[i][l].target, 1.0-beta, parent1.actions[i][l].target, parent2.actions[i][l].target);
          child2.actions[i][l].thrust = getAcceptableThrust(1.0-beta, parent1.actions[i][l].thrust, parent2.actions[i][l].thrust);
        }
        child1.mutate(l);
        child2.mutate(l);
      }
    }
  }

  // cross over actions, not action by action
  public static void crossOverAndMutateAlternative(AGSolution child1, AGSolution child2, AGSolution parent1, AGSolution parent2) {
    AGSolution p1, p2;
    for (int l=0;l<3;l++) {
      for (int i=0;i<DEPTH;i++) {
        double alpha = Game.random.nextDouble();
        p1 = alpha < 0.5 ? parent1 : parent2;
        p2 = alpha < 0.5 ? parent2 : parent1;
        child1.actions[i][l].target.x = p1.actions[i][l].target.x;
        child1.actions[i][l].target.y = p1.actions[i][l].target.y;
        child1.actions[i][l].thrust = p1.actions[i][l].thrust;

        child2.actions[i][l].target.x = p2.actions[i][l].target.x;
        child2.actions[i][l].target.y = p2.actions[i][l].target.y;
        child2.actions[i][l].thrust = p2.actions[i][l].thrust;
      }
      child1.mutate(l);
      child2.mutate(l);
    }  
  }
  
  private static int getAcceptableThrust(double d, int thrust1, int thrust2) {
    return (int)(thrust1 + d * (thrust2 - thrust1));
  }

  private static Position getAcceptableTarget(Position target, double beta, Position pos1, Position pos2) {
    target.x = pos1.x + beta * (pos2.x - pos1.x);
    target.y = pos1.y + beta * (pos2.y - pos1.y);
    return target;
  }

  public void mutate(int l) {
    if (Game.random.nextInt(INV_MUTATION_RATE) == 0) {
      forceMutate(this, l);
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
    energy = 0.0;
    for (int depth=0;depth<DEPTH-1;depth++) {
      actions[depth][0].copyFrom(model.actions[depth+1][0]);
      actions[depth][1].copyFrom(model.actions[depth+1][1]);
      actions[depth][2].copyFrom(model.actions[depth+1][2]);
    }
    randomOneDepth(DEPTH-1);
  }

  public void debug() {
    System.err.println("best solution : ");
    for (int j=0;j<AGSolution.DEPTH;j++) {
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

}
