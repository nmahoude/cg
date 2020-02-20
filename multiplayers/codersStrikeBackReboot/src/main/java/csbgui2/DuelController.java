package csbgui2;

import csb.ai.AG;
import csb.ai.AGParameters;
import csb.ai.AGSolution;
import csb.ai.RunnerScorer;
import csb.ai.Simulation;
import csb.ai.ZeroScorer;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.PhysicsEngine;
import trigonometry.Point;
import trigonometry.Vector;

/**
 * pod1 objective is to prevent pod0 to enter the cp
 * 
 * objective : test physics engine
 * @author nmahoude
 *
 */
public class DuelController extends Controller {
  PhysicsEngine engine = new PhysicsEngine();
  Pod pod0 = new Pod(0);
  Pod pod1 = new Pod(1);
  Pod pod2 = new Pod(2);
  Pod pod3 = new Pod(3);
  CheckPoint cp1 = new CheckPoint(10, 8000, 4500);
  CheckPoint cp2 = new CheckPoint(11, 16000, 4500);
  CheckPoint cp3 = new CheckPoint(12, 16000, 9000);
  
  public DuelController() {
    pod0.position = new Point(0,4500);
    pod0.direction = new Vector(1,0);
    
    pod1.position = new Point(8000,4500);
    pod1.direction = new Vector(-1,0);

    pod2.position = new Point(0,0);
    pod3.position = new Point(0,0);
    
    pods = new Pod[] { pod0, pod1, pod2, pod3};
    checkPoints = new CheckPoint[] {cp1, cp2, cp3};
    
    engine.pods = pods;
    engine.checkPoints = checkPoints;
    
    pod0.backup();
    pod1.backup();
    pod2.backup();
    pod3.backup();
  }

  @Override
  public void init() {
  }

  @Override
  public void update() {
    // calculate pod1 output based on AGSimulation
    AGParameters parameters = new AGParameters();
    Simulation simulation = new Simulation(engine);
    AG ag = new AG(simulation, parameters);
    simulation.scorer1 = new ZeroScorer();
    simulation.scorer2 = new RunnerScorer();
    AGSolution best = ag.evolution(System.nanoTime() + 150_000);
    
    pod0.restore();
    pod1.restore();
    pod2.restore();
    pod3.restore();

    pod0.apply(new Point(8000,4500), 50);
    pod1.apply(pod1.direction.rotate(best.actions1[0].angle), best.actions1[0].thrust);
    

    engine.simulate();
    
    pod0.backup();
    pod1.backup();
    pod2.backup();
    pod3.backup();
  }

}
