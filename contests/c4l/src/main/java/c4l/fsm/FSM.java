package c4l.fsm;

import c4l.GameState;
import c4l.entities.Robot;

public class FSM {
   
  final FSMCenter center = new FSMCenter(this);
  final FSMDiagnosis diag = new FSMDiagnosis(this);
  final FSMMolecule mole = new FSMMolecule(this);
  final FSMSample sample = new FSMSample(this);
  final FSMLaboratory lab = new FSMLaboratory(this);
  
  
  private FSMNode lastState = center;
  private FSMNode currentState = center;
  GameState state;
  Robot me;
  
  private String output;
  

  public void think(GameState state, Robot me) {
    output = "GOTO "+currentState.module();
    this.state = state;
    this.me = me;
  
    if (me.eta > 0 ) {
      // TODO do some thinking here, to prepare next actions ?
      // currentState.preThink();
      
      System.out.println("Still moving to "+currentState.module()+" ...");
      return;
    } 

    currentState.think();
    
    System.out.println(output);
  }
  
  void moveToState(FSMNode newState) {
    lastState = currentState;
    currentState = newState;
    output = "GOTO "+newState.module();
  }
}
