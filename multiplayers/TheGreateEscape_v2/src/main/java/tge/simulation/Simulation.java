package tge.simulation;

import tge.Cell;
import tge.Player;
import tge.WallOrientation;

public class Simulation {
  /* return true if the action was valid */
  public boolean play(Action action) {
    if (action.type == ActionType.WALL) {
      return dropWall(action);
    } else {
      return move(action);
    }
  }
  
  private boolean move(Action action) {
    Cell cell = Player.grid.get(action.agent.position);
    if (cell.walls[action.moveIndex] != 0 || cell.cells[action.moveIndex] == Cell.invalid) return false;
    action.agent.position = cell.cells[action.moveIndex].position;
    return true;
  }

  private boolean dropWall(Action action) {
    if (action.agent.wallLefts == 0) return false;

    Cell firstCell = Player.grid.get(action.position);
    if (action.orientation == WallOrientation.VERTICAL) {
      Cell secondCell = firstCell.cells[Cell.DOWN];
      if (secondCell == Cell.invalid || firstCell.walls[Cell.LEFT] != 0 || secondCell.walls[Cell.LEFT] != 0) return false;
      if (firstCell.walls[Cell.DOWN] != 0 && firstCell.walls[Cell.DOWN] == firstCell.cells[Cell.LEFT].walls[Cell.DOWN]) return false;
      firstCell.walls[Cell.LEFT] = Cell.wallIndex;
      secondCell.walls[Cell.LEFT] =Cell.wallIndex;
      firstCell.cells[Cell.LEFT].walls[Cell.RIGHT] = Cell.wallIndex;
      secondCell.cells[Cell.LEFT].walls[Cell.RIGHT] =Cell.wallIndex;
      Cell.wallIndex++;
    } else {
      Cell secondCell = firstCell.cells[Cell.RIGHT];
      if (secondCell == Cell.invalid || firstCell.walls[Cell.UP] !=0 || secondCell.walls[Cell.UP] != 0) return false;
      if (firstCell.walls[Cell.RIGHT] != 0 && firstCell.walls[Cell.RIGHT] == firstCell.cells[Cell.UP].walls[Cell.RIGHT]) return false;
      firstCell.walls[Cell.UP] = Cell.wallIndex;
      secondCell.walls[Cell.UP] = Cell.wallIndex;
      firstCell.cells[Cell.UP].walls[Cell.DOWN] = Cell.wallIndex;
      secondCell.cells[Cell.UP].walls[Cell.DOWN] = Cell.wallIndex;
      Cell.wallIndex++;
    }
    action.agent.wallLefts--;
    
    return true;
  }

  public void unplay(Action action) {
    if (action.type == ActionType.WALL) {
      action.agent.wallLefts++;

      // undrop
      Cell firstCell = Player.grid.get(action.position);
      if (action.orientation == WallOrientation.VERTICAL) {
        Cell secondCell = firstCell.cells[Cell.DOWN];
        firstCell.walls[Cell.LEFT] = 0;
        secondCell.walls[Cell.LEFT] = 0;
        firstCell.cells[Cell.LEFT].walls[Cell.RIGHT] = 0;
        secondCell.cells[Cell.LEFT].walls[Cell.RIGHT] = 0;
      } else {
        Cell secondCell = firstCell.cells[Cell.RIGHT];
        firstCell.walls[Cell.UP] = 0;
        secondCell.walls[Cell.UP] = 0;
        firstCell.cells[Cell.UP].walls[Cell.DOWN] = 0;
        secondCell.cells[Cell.UP].walls[Cell.DOWN] = 0;
      }
    } else {
      // unmove
      Cell cell = Player.grid.get(action.agent.position);
      action.agent.position = cell.cells[(action.moveIndex +2) % 4].position;
    }
  }
}
