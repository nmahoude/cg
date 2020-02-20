package c4l.fsm;

import c4l.entities.Module;

public class FSMCenter extends FSMNode {

  FSMCenter(FSM fsm) {
    super(fsm);
  }

  @Override
  public void think() {
    // FIRST ACTION OF THE PLAYER
    // WE CANT GO BACK IN THIS STATE AFTERWARD
    // TODO better tactics here ?
    fsm.goTo(Module.SAMPLES, "first action");
  }

  @Override
  public Module module() {
    return Module.START_POS;
  }
}
