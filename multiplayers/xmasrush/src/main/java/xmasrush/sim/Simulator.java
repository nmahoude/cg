package xmasrush.sim;

import xmasrush.Cell;
import xmasrush.State;
import xmasrush.ai.push.PushAction;

public class Simulator {
  Cell temp = new Cell(-1, -1);

  
  public void apply(State state, PushAction action, PushAction action2) {

    switch(action.dir ) {
    case UP:
      temp.copyCellContent(state.getCellAt(action.offset, 0));
      for (int i=0;i<6;i++) {
        state.getCellAt(action.offset, i).copyCellContent(state.getCellAt(action.offset, i+1));
      }
      state.getCellAt(action.offset, 6).copyCellContent(state.agents[0].playerCell);
      state.agents[0].playerCell.copyCellContent(temp);

      break;
    case RIGHT:
      temp.copyCellContent(state.getCellAt(6, action.offset));
      for (int i=6;i>0;i--) {
        state.getCellAt(i, action.offset).copyCellContent(state.getCellAt(i-1, action.offset));
      }
      state.getCellAt(0, action.offset).copyCellContent(state.agents[0].playerCell);
      state.agents[0].playerCell.copyCellContent(temp);

      break;
    case DOWN:
      temp.copyCellContent(state.getCellAt(action.offset, 6));
      for (int i=6;i>0;i--) {
        state.getCellAt(action.offset, i).copyCellContent(state.getCellAt(action.offset, i-1));
      }
      state.getCellAt(action.offset, 0).copyCellContent(state.agents[0].playerCell);
      state.agents[0].playerCell.copyCellContent(temp);
      
      break;
    case LEFT:
      temp.copyCellContent(state.getCellAt(0, action.offset));
      for (int i=0;i<6;i++) {
        state.getCellAt(i, action.offset).copyCellContent(state.getCellAt(i+1, action.offset));
      }
      state.getCellAt(6, action.offset).copyCellContent(state.agents[0].playerCell);
      state.agents[0].playerCell.copyCellContent(temp);
      
      break;
    default:
      break;
    }
  
    state.agents[0].moveIfNecessary(action);
    state.agents[1].moveIfNecessary(action);
  
  }
  
  public void unapply(State state, PushAction action, PushAction action2) {
    apply(state, action.counter, null);
  }
}
