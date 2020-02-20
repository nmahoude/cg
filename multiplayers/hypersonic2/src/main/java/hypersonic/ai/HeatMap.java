package hypersonic.ai;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.State;
import hypersonic.simulation.Simulation;
import hypersonic.utils.P;

/**
 * For a current state, for each coordinates (x,y) get a score if the position is near boxes (manhattan)
 * @author nmahoude
 *
 */
public class HeatMap {
  public static double score[] = new double[Board.WIDTH * Board.HEIGHT];
  private static State heatMapState = new State();
  private static Simulation sim = new Simulation(heatMapState);

  
  public static void calculate(State state) {
    heatMapState.copyFrom(state);
    heatMapState.turn += 10; // make all bombs explode !
    sim.simulate(Move.STAY);
    clear();
    
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        if ((x & 0b1) == 0b1 && (y &0b1) == 0b1)  continue;
        
        int value = state.board.cells[P.get(x, y).offset];
        if (value == Board.BOX) {
          fillFrom(x,y, 1);
        } else if (value == Board.BOX_1) {
          fillFrom(x,y, 1.1);
        } else if (value == Board.BOX_2) {
          fillFrom(x,y,1.1);
        }
      }
    }
  }

  private static void clear() {
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        score[x+Board.WIDTH*y] = 0;
      }
    }
  }

  private static void fillFrom(int ox, int oy, double base) {
    for (int y=0;y<Board.HEIGHT;y++) {
      for (int x=0;x<Board.WIDTH;x++) {
        int dist = Math.abs(ox-x)+Math.abs(oy-y)+1;
        score[x+Board.WIDTH*y] += base / dist;
      }
    }
  }
}
