package c4l.fsm;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeOptimizerNode;

public class FSM {
   
  private final FSMCenter center = new FSMCenter(this);
  private final FSMDiagnosis diag = new FSMDiagnosis(this);
  private final FSMMolecule mole = new FSMMolecule(this);
  private final FSMSample sample = new FSMSample(this);
  private final FSMLaboratory lab = new FSMLaboratory(this);
  
  
  private FSMNode lastState = center;
  private FSMNode currentState = center;
  GameState state;
  Robot me;
  MoleculeOptimizerNode root;

  
  private String output;
  
  public FSM(GameState state, Robot me) {
    this.state = state;
    this.me = me;
    
    // TODO WTF ?
    center.me = me ; center.state = state;
    diag.me = me; diag.state = state;
    mole.me = me; mole.state = state;
    sample.me = me; sample.state = state;
    lab.me =me; lab.state = state;
  }

  void init() {
    root = null;
  }

  public void think() {
    init();
    
    output = "GOTO "+currentState.module()+" Don't know what to do :(";
  
    if (me.eta > 0 ) {
      // TODO do some thinking here, to prepare next actions ?
      // currentState.preThink();
      System.out.println("Still moving ( eta="+me.eta+" ) to "+currentState.module()+" ...");
      return;
    } 
    System.err.println("I'm at module "+currentState.module());
    currentState.think();
    System.out.println(output);
  }
  
  public void connect(String action) {
    output = "CONNECT "+action;
  }

  public void connect(int action) {
    output = "CONNECT "+action;
  }

  public void goTo(Module module) {
    lastState = currentState;
    switch (module) {
      case DIAGNOSIS:
        currentState = diag;
        break;
      case LABORATORY:
        currentState = lab;
        break;
      case MOLECULES:
        currentState = mole;
        break;
      case SAMPLES:
        currentState = sample;
        break;
      default:
        System.out.println("UNKNOWN STATE");
        break;
    }
    output = "GOTO "+currentState.module();
  }
  
  MoleculeType getBestMoleculeForSamples() {
    MoleculeType type = null;
    if (me.target == Module.MOLECULES) {
      if (root == null) {
        buildMoleculeChoiceOptimized();
      }
      MoleculeOptimizerNode best = root.getBestChild();
      if (best ==null) {
        type = null;
      } else {
        type = best.pickedMolecule;
      }
    } else {
      // TODO Optimized the chosen molecule
      System.err.println("Requesting type "+type+" alone TO OPTIMIZE");
      int i=0;
      while (i<me.carriedSamples.size() && type == null) {
        type = me.getMissingMoleculeForSample(state, me.carriedSamples.get(i));
        i++;
      }
    }
    
    return type;
  }

  private void buildMoleculeChoiceOptimized() {
    System.err.println("use the optimizer Luke");
    root = new MoleculeOptimizerNode();
    int index = 0;
    for (Sample sample : me.carriedSamples) {
      root.createSample(index++, sample.costs, sample.health);
    }
    root.createStorage(me.storage);
    root.createExpertise(me.expertise);
    root.createAvailable(state.availables);
    root.freeStorage = Math.min(10, root.freeStorage ); // TODO Timeout at 10
    root.start();
    System.err.println("Free storage : "+root.freeStorage);
    System.err.println("Best score : "+root.score);
  }
}
