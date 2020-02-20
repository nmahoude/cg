package c4l.fsm;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeOptimizerNode;
import c4l.sample.SampleOptimizerNode;

public class FSM {
   
  final FSMCenter center;
  final FSMDiagnosis diag;
  final FSMMolecule mole;
  final FSMSample sample;
  final FSMLaboratory lab;
  
  
  private FSMNode currentState;
  GameState state;
  Robot me;
  MoleculeOptimizerNode moleculeRoot;
  SampleOptimizerNode sampleRoot;
  
  private String output;
  
  public FSM(GameState state, Robot me) {
    this.state = state;
    this.me = me;
    
    center = new FSMCenter(this);
    diag = new FSMDiagnosis(this);
    mole = new FSMMolecule(this);
    sample = new FSMSample(this);
    lab = new FSMLaboratory(this);
    
    currentState = center;
  }

  void init() {
    moleculeRoot = null;
    sampleRoot = null;
  }

  public void think() {
    System.err.println("Tour : "+state.ply);
    //TODO find a good combination if turns are soon ended 200
    // ==> add a level of urge in the algorithm : when end og game, don't try to build to much plies strategy
    
    init();
    
    output = "GOTO "+currentState.module()+" Don't know what to do :(";
  
    if (me.eta > 0 ) {
      // TODO do some thinking here, to prepare next actions ?
      // currentState.preThink();
      explainYourself("Still moving ( eta="+me.eta+" ) to "+currentState.module()+" ...");
      System.out.println("So long ....");
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
    if (moleculeRoot == null) {
      moleculeRoot = buildMoleculeChoiceOptimized();
    }
    return moleculeRoot.getBestChild();
  }
  
  SampleOptimizerNode getBestSamplesAtDiag() {
    if (sampleRoot == null) {
      sampleRoot = buildSampleChoiceOptimized();
    }
    return sampleRoot.bestChild;
  }

  private SampleOptimizerNode buildSampleChoiceOptimized() {
    System.err.println("use the optimizer Solo");
    SampleOptimizerNode root = new SampleOptimizerNode();
    root.start(state, me);
    return root;
  }

  private MoleculeOptimizerNode buildMoleculeChoiceOptimized() {
    System.err.println("use the optimizer Luke");
    MoleculeOptimizerNode root = new MoleculeOptimizerNode();
    root.start(state.ply, state.availables, state.scienceProjects, me);
    return root;
  }

  public boolean isAt(Module diagnosis) {
    return me.eta == 0 && currentState.module() == diagnosis;
  }
}
