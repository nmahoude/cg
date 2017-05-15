package c4l.fsm;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
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
    //TODO find a good combination if turns are soon ended 200
    // no need to continue making samples if we win the game ...
    
    // TODO block the enemy if score too high
    
    // TODO think about expertise
    init();
    
    output = "GOTO "+currentState.module()+" Don't know what to do :(";
  
    if (me.eta > 0 ) {
      // TODO do some thinking here, to prepare next actions ?
      // currentState.preThink();
      explainYourself("Still moving ( eta="+me.eta+" ) to "+currentState.module()+" ...");
      System.out.println("Thinking about life ...");
      return;
    } 
    System.err.println("I'm at module "+currentState.module());
    currentState.think();
    System.out.println(output);
  }
  
  private void explainYourself(String explanation) {
    System.err.println("EXP ----> "+explanation);
  }

  public void connect(int action, String explanation) {
    explainYourself(explanation);
    output = "CONNECT "+action;
  }

  public void connect(MoleculeType type, String explanation) {
    explainYourself(explanation);
    output = "CONNECT "+type.toString();
  }

  public void goTo(Module module, String explanation) {
    explainYourself(explanation);
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
  
  MoleculeComboInfo getBestComboForSamples() {
    if (root == null) {
      buildMoleculeChoiceOptimized();
    }
    return root.getBestChild().combo;
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
    root.start();
  }

  public boolean isAt(Module diagnosis) {
    return me.eta == 0 && currentState.module() == diagnosis;
  }
}
