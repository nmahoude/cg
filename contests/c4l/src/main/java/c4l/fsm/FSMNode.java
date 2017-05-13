package c4l.fsm;

import c4l.entities.Module;

public abstract class FSMNode {

  FSM fsm;
  
  FSMNode(FSM fsm) {
    this.fsm = fsm;
  }

  public abstract void think();

  public void prethink() {
  }

  public abstract Module module();
}
