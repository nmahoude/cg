package fall2023._fx.modules;

import cgfx.Board;
import cgfx.BoardDistPicker;
import cgfx.Inset;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import fall2023.Action;
import fall2023.Collision;
import fall2023.Drone;
import fall2023.Fish;
import fall2023.GridMaster;
import fall2023.Rectangle;
import fall2023.Scan;
import fall2023.State;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class F23GameViewer extends GameViewer {
  private final static double boardScale = 0.07;

  
  private final F23GameWrapper wrapper;
  private final State state;
  private final F23GameOptionPane options;

  private static final Color BLIP_TRIANGULATION_COLOR = new Color(0.0, 0.0, 1.0, 0.1);
  private static final Color BLIP_OLD_TRIANGULATION_COLOR = new Color(0.5, 0.0, 0.0, 0.2);

  static final int fishOrder[] = { 4, 6, 8, 5, 7, 9, 10, 12, 14, 11, 13, 15};

  Board myScore;
  Board oppScore;
  
  Board board;

  Color fishColor[] = new Color[] { Color.PINK, Color.GOLD, Color.LIGHTGREEN, Color.DODGERBLUE, Color.DARKRED };
  
  int blipOverlayId = -1;
  private boolean isBlipPinned;

  private Action[] actions;

  public F23GameViewer(F23GameWrapper wrapper, F23GameOptionPane options) {
    this.wrapper = wrapper;
    this.options = options;
    this.state = wrapper.state;
    
    var gMyScore = new Group();
    var gOppScore = new Group();
    var gBoard = new Group();

    myScore = new Board(gMyScore, 128+8, 160+8+200, 1.0);

    myScore.getCanvas().setOnMouseMoved(mv -> {
      updateDrawBlip(mv);
    });
    
    oppScore = new Board(gOppScore, 128+8, 160+8+200, 1.0);
    
    board = new Board(gBoard, 10_000, 10_000, boardScale);
    BoardDistPicker bdp = new BoardDistPicker(board, () -> update(null, null));
    VBox vbox = new VBox(gMyScore, gOppScore);
    
    HBox hbox = new HBox(vbox, gBoard);
    
    VBox fullBox = new VBox(hbox);
    this.getChildren().add(fullBox);

    
    options.register(this);
  }

  
  @Override
  protected void updateView() {
    board.clear();
    myScore.clear();
    oppScore.clear();
    
    board.drawRect(Color.BLACK, Pos.from(0, 0), Length.of(10_000, 10_000));
    
    if (state.myDrones[0] == null) return; // not initialized, should remain empty
     
    drawScores();
    drawLevels();
    drawDrones();
    drawFishes();
    
    if (options.isSelected(F23GameOptionPane.DRAW_ALL_TRIANGULATION)) {
      drawAllTriangulations();
    }
    drawTrajectory();
    
    drawBlipOverlayById(blipOverlayId);
    //drawGridOverlay(blipOverlayId);

    drawActions();
    
    drawPaths();
    
  }


  
  private void drawPaths() {
    if (wrapper.path.isEmpty()) return;
    
    for (int i=1; i<wrapper.path.size();i++) {
      board.drawLine(Color.BLACK, pos(wrapper.path.get(i-1)), pos(wrapper.path.get(i)));
    }
  }


  private void drawTrajectory() {
    if (!options.isSelected(F23GameOptionPane.DRAW_TRAJECTORY_DRONE_POS)) return;
    
    for (int i=0;i<2;i++) {
      drawOneTrajectory(state.myDrones[i].id, myColor());
    }
    for (int i=0;i<2;i++) {
      drawOneTrajectory(state.oppDrones[i].id, oppColor());
    }
  }


  private void drawOneTrajectory(int d, Color color) {
    if (wrapper.allPositions[d][0].x == -1) return;
    for (int t=1;t<State.turn;t++) {
      board.drawLine(color, pos(wrapper.allPositions[d][t]), pos(wrapper.allPositions[d][t+1]));
    }
  }


  private void updateDrawBlip(MouseEvent mv) {
    
    if (! mv.isControlDown() && isBlipPinned) return;
    
    int x = (int)(mv.getX() / 32);
    int y = (int)(mv.getY() / 32);
    if (x > 2) {
      blipOverlayId = -1;
    } else if (y > 7) {
      blipOverlayId = -1;
    } else if ((y > 3 && y <5)) {
      blipOverlayId = -1;
    } else {
      int id;
      if (y > 5) {
        y = y - 2;
        id = 4 + x + 3 * y;
      } else {
        if (x + 3 * y < 12) {
          id = fishOrder[x + 3 * y];
        } else {
          id = -1;
        }
      }
      
      if (mv.isControlDown()) {
        isBlipPinned = true;
      }
      if (blipOverlayId == id) {
        return; // no change
      }
      //System.out.println("Display blip overlay of "+x+" "+y+" => id ="+id);
      if (true || blipOverlayId != -1 && state.fishPresent[id]) {
        blipOverlayId = id;
      } else {
        blipOverlayId = -1;
      }
    }

    if (blipOverlayId == -1) {
      // unpin
      isBlipPinned = false;
    }
    update(null, null);
  }

  private void drawGridOverlay(int id) {
    if (id == -1) return;
    if (state.myDrones[0] == null) return; // not initialized

    
    GridMaster grid = new GridMaster();
    Rectangle triangulation = new Rectangle();
    triangulation.intersect((Rectangle)state.getZoneTriangulation(id));

    for (int y=0;y<1000;y++) {
      for (int x=0;x<1000;x++) {
        if (triangulation.contains(x*10, y*10)) {
          grid.set(x,y);
        }
      }
    }
    
    for (int y=0;y<1000;y++) {
      for (int x=0;x<1000;x++) {
        if (grid.isSet(x, y)) {
          board.fillRect(BLIP_TRIANGULATION_COLOR, BLIP_TRIANGULATION_COLOR, Pos.from(x*10, y*10), Length.of(10, 10), Inset.NO);
        }
      }
    }
  }
  

  
  private void drawBlipOverlay() {
    drawBlipOverlayById(blipOverlayId);
  }
  
  private void drawBlipOverlayById(int id) {

    if (id == -1) return;
    if (state.myDrones[0] == null) return; // not initialized
    if (!state.fishPresent[id]) return;
    
    if (options.isSelected(F23GameOptionPane.DRAW_OLD_TRIANGULATION)) {
      drawTriangulation((Rectangle)state.previousState.getZoneTriangulation(id), BLIP_OLD_TRIANGULATION_COLOR);
    }
    
    if (options.isSelected(F23GameOptionPane.DRAW_BEST_TRIANGULATION)) {
      drawTriangulation((Rectangle)state.getBestTriangulation(id), BLIP_TRIANGULATION_COLOR);
    } else {
      drawTriangulation((Rectangle)state.getZoneTriangulation(id), BLIP_TRIANGULATION_COLOR);
    }
  }


  private void drawTriangulation(Rectangle triangulation, Color color) {
    if (triangulation.width <= 1 || triangulation.height <= 1) {
      board.fillCircle(color, Pos.from(triangulation.x, triangulation.y), 150);
    } else {
      board.fillRect(color, color, Pos.from(triangulation.x, triangulation.y), Length.of(triangulation.width, triangulation.height));
    }
  }

  private void drawAllTriangulations() {
    for (int i=4;i<4+state.creatureCount;i++) {
      if (!state.fishPresent[i]) continue;
      drawBlipOverlayById(i);
    }
  }



  private void drawActions() {
    if (actions == null || state.myDrones[0] == null) return;
    
    for (int i=0;i<4;i++) {
      Action a = actions[i];
      Drone drone = state.dronesById[i];
      
      board.drawLine(Color.GREEN, 3.0, pos(drone.pos), Pos.from(drone.pos.x + a.dx, drone.pos.y + a.dy));
      
    }
    
  }


  private void drawScores() {
    drawScores(myScore, state.myScans, state.myDrones, state.myScore);
    drawScores(oppScore, state.oppScans, state.oppDrones, state.oppScore);
    
    // draw bad fishes
    
    myScore.setFontSize(16);
    int x = 0;
    int y = 0;
    for (int i=State.creatureStart;i<State.creatureStart + State.creatureCount;i++) {
      if (State.fType[i] != 3) continue;
      
      Pos pos = Pos.from(32*x+16+4, 190 + 32*y+16+4);
      myScore.fillCircle(Color.DARKRED, pos, 16);
      myScore.drawText(Color.WHITE, pos.add(-8, 0), ""+i);
      
      x++;
      if (x == 3) { x= 0; y++;}
    }
  }


  private void drawScores(Board scoreView, Scan alreadyScans, Drone[] drones, int score) {
    scoreView.drawRect(Color.BLACK, Pos.from(0, 0), Length.of(128+4, 160+4));
    
    if (drones[0] == null) return;
    
    int x = 0;
    int y = 0;
    
    // prepare what is still possible to get as col & row
    Scan fakeForCups = new Scan();
    fakeForCups.append(alreadyScans);
    for (Drone d: drones) {
      fakeForCups.append(d.currentScans);
    }
    
    for (int i=4;i<16;i++) {
      if (state.fishPresent[i]) fakeForCups.scan(i);
    }
    
    // row cups
    for (int row=0;row<4;row++) {
      Pos pos = Pos.from(4+32*3, 32*row+4).add(4, 3);
      Color color = Color.GREY;
      
      if (alreadyScans.hasRowCup(row)) {
        color = fishColor[row];
      }
      scoreView.fillRect(color, color, pos, Length.of(26, 26));

      if (!alreadyScans.hasRowCup(row) && !fakeForCups.hasRowCup(row)) {
        scoreView.drawLine(Color.RED, 2.0, pos.add(0, 0), pos.add(26, 26) );
        scoreView.drawLine(Color.RED, 2.0, pos.add(26, 0), pos.add(0, 26) );
      }
      
      if (alreadyScans.hasRowCupFirst(row)) {
        scoreView.fillRect(Color.GOLDENROD, Color.GOLDENROD, pos.add(16, 0), Length.of(12, 12));
      }
      
    }
    
    // col cups
    for (int col=0;col<3;col++) {
      Pos pos = Pos.from(4+32*col, 32*4+4).add(4, 3);
      Color color = Color.GREY;
      
      if (alreadyScans.hasColCup(col)) {
        color = Color.DARKVIOLET;
      }
      scoreView.fillRect(color, color, pos, Length.of(26, 26));

      if (!alreadyScans.hasColCup(col) && !fakeForCups.hasColCup(col)) {
        scoreView.drawLine(Color.RED, 2.0, pos.add(0, 0), pos.add(26, 26) );
        scoreView.drawLine(Color.RED, 2.0, pos.add(26, 0), pos.add(0, 26) );
      }

      if (alreadyScans.hasColCupFirst(col)) {
        scoreView.fillRect(Color.GOLDENROD, Color.GOLDENROD, pos.add(16, 0), Length.of(12, 12));
      }
    }
    
    
    
    for (int o=0;o<12;o++) {
      int id = fishOrder[o];
      
      Pos pos = Pos.from(32*x+16+4, 32*y+16+4);
      if (alreadyScans.contains(id)) {
        scoreView.fillCircle(fishColor[State.fColor[id]], pos, 16);
        if (alreadyScans.isFirst(id)) {
          scoreView.fillRect(Color.GOLDENROD, Color.GOLDENROD, pos.add(4, -16), Length.of(12, 12));
        }
      } else {
        if (drones[0].hasScan(id) || drones[1].hasScan(id)) {
          scoreView.fillCircle(fishColor[State.fColor[id]], pos, 8);
        } else {
          scoreView.fillCircle(Color.GREY, pos, 16);
          if (!state.fishPresent[id]) {
//            System.err.println("Fish not present : "+id);
            scoreView.drawLine(Color.RED, 2.0, pos.add(-16, -16), pos.add(16 , 16));
            scoreView.drawLine(Color.RED, 2.0, pos.add(16, -16), pos.add(-16, 16));
          }
        }
        
        if (drones[0].hasScan(id)) {
          scoreView.setFontSize(8);
          scoreView.drawText(Color.BLACK, pos.add(-16, 8), "0");
        }
        
        if (drones[1].hasScan(id)) {
          scoreView.setFontSize(8);
          scoreView.drawText(Color.BLACK, pos.add(-8, 8), "1");
        }
      }
      scoreView.setFontSize(16);

      if (blipOverlayId == id) {
        scoreView.drawText(Color.BLACK, pos.add(-8, 0), ""+id+"*");
      } else {
        scoreView.drawText(Color.BLACK, pos.add(-8, 0), ""+id);
      }
      
      x++;
      if (x == 3) {
        x = 0;
        y++;
      }
    }
    
    scoreView.setFontSize(32);
    scoreView.drawText(Color.ORANGE, Pos.from(98, 160), ""+score);
    
  }


  private void drawFishes() {
    
    if (options.isSelected(F23GameOptionPane.DRAW_FICTIVE_POS)) {
      for (int l=4;l<16+State.uglyCount;l++) {
        if (true || state.getFishById(l) == null) {
          drawFictiveFish(l, fishColor[State.fColor[l]], false);
        }
      }
    }
    
    if (options.isSelected(F23GameOptionPane.DRAW_REAL_POS)) {
      for (Fish f : wrapper.fishes) {
        drawOnFish(f, fishColor[State.fColor[f.id]], false);
      }
    }
    
    for (Fish f : state.fishes) {
      drawOnFish(f, fishColor[State.fColor[f.id]], true);
    }
    
  }


  private void drawFictiveFish(int fishId, Color color, boolean filled) {
    fall2023.Pos center = state.getBestTriangulation(fishId).center();
    board.drawRect(color, Pos.from(center.x-100+4*fishId, center.y-100), Length.of(200, 200));
  }


  private void drawOnFish(Fish f, Color color, boolean filled) {
    Pos pos = Pos.from(f.pos.x, f.pos.y);

    if (filled) {
      board.fillCircle(color, pos, 100);
    } else {
      board.drawCircle(color, pos, 100);
    }
    board.drawLine(Color.GREEN, pos, pos.add(f.speed.vx, f.speed.vy));
    
    board.setFontSize(12);
    board.drawText(Color.BLACK, pos.add(-30, 12), ""+f.id);
    
    
    if (State.fType[f.id] != Fish.UGLY) {
      for (int i=0;i<4;i++) {
        boolean inRange = false;
        
        // boolean inScans = state.drones[i].hasScan(f.id); // Doesn't work, decalage dans les inputs/outputs/results :(
        if (isBigRange(state.dronesById[i])) {
          inRange = f.pos.dist2(state.dronesById[i].pos) < 2000 * 2000;
        } else {
          inRange = f.pos.dist2(state.dronesById[i].pos) < 800 * 800;
        }
        if (inRange && state.dronesById[i].hasScan(f.id) && !state.previousState.dronesById[i].hasScan(f.id)) board.drawLine(Color.BLACK, pos(f.pos), pos(state.dronesById[i].pos));
      }
    } else {
      // draw eat circle
      board.drawCircle(Color.RED, pos, 300);
    }
  }


  private Pos pos(fall2023.Pos p) {
    return Pos.from(p.x, p.y);
  }


  private void drawLevels() {
    board.drawLine(Color.LIGHTGREY, 1.0, Pos.from(0, 2500), Pos.from(10_000, 2500));
    board.drawLine(Color.LIGHTGREY, 1.0, Pos.from(0, 5000), Pos.from(10_000, 5000));
    board.drawLine(Color.LIGHTGREY, 1.0, Pos.from(0, 7500), Pos.from(10_000, 7500));
  }


  private void drawDrones() {
    for (int i=0;i<2;i++) {
      Drone currentDrone = state.myDrones[i];
      if (state.previousState != null) {
        Drone lastDrone = state.previousState.dronesById[currentDrone.id];
        if (lastDrone.pos.x != 0 && options.isSelected(F23GameOptionPane.DRAW_LAST_DRONE_POS))  {
          board.drawCircle(Color.GREY, Pos.from(lastDrone.pos.x, lastDrone.pos.y), Collision.DRONE_HIT_RANGE);
          board.drawLine(Color.GREY, Pos.from(lastDrone.pos.x, lastDrone.pos.y), Pos.from(currentDrone.pos.x, currentDrone.pos.y));
        }
      }
      drawDrone(currentDrone, true, myColor());
    }
    
    for (int i=0;i<2;i++) {
      Drone currentDrone = state.oppDrones[i];
      if (state.previousState != null) {
        Drone lastDrone = state.previousState.dronesById[currentDrone.id];
        
        if (lastDrone.pos.x != 0 && options.isSelected(F23GameOptionPane.DRAW_LAST_DRONE_POS))  {
          board.drawCircle(Color.GREY, Pos.from(lastDrone.pos.x, lastDrone.pos.y), Collision.DRONE_HIT_RANGE);
          board.drawLine(Color.GREY, Pos.from(lastDrone.pos.x, lastDrone.pos.y), Pos.from(currentDrone.pos.x, currentDrone.pos.y));
        }
      }
      
      drawDrone(currentDrone, false, oppColor());
    }
  }


  private Color oppColor() {
    return State.agentId == 0 ? Color.VIOLET : Color.ORANGE;
  }


  private Color myColor() {
    return State.agentId == 0 ? Color.ORANGE : Color.VIOLET;
  }


  private void drawDrone(Drone d, boolean myDrone, Color color) {
    if (d.emergency) {
      board.fillCircle(Color.RED, Pos.from(d.pos.x, d.pos.y), Collision.DRONE_HIT_RANGE+50);
    }
 
    board.fillCircle(color, Pos.from(d.pos.x, d.pos.y), Collision.DRONE_HIT_RANGE);
    board.setFontSize(16);
    board.drawText(Color.BLACK, Pos.from(d.pos.x, d.pos.y).add(-64, 8), "["+d.id+"]");
    board.setFontSize(12);
    board.drawText(Color.BLACK, Pos.from(d.pos.x, d.pos.y).add(-64, 180), "b("+d.battery+")");

    
    // do not show ranges
    if (myDrone && !options.isSelected(F23GameOptionPane.DRAW_MY_RANGES))
      return;
    if (!myDrone && !options.isSelected(F23GameOptionPane.DRAW_OPP_RANGES))
      return;

    if (isBigRange(d) || options.isSelected(F23GameOptionPane.DRAW_MY_2000_RANGES)) {
      board.drawCircle(color, Pos.from(d.pos.x, d.pos.y), 2000); // see fish (big light)
      board.drawCircle(Color.GREEN, Pos.from(d.pos.x, d.pos.y), 2000+300); // see big fish (big light)
    } 
    if (!isBigRange(d)) {
      board.drawCircle(color, Pos.from(d.pos.x, d.pos.y), 800); // see fish (normal light)
      board.drawCircle(Color.GREEN, Pos.from(d.pos.x, d.pos.y), 800+300); // see big fish (normal light)
    }
    
 
    board.drawCircle(Color.RED, Pos.from(d.pos.x, d.pos.y), 1400); // engine noise
  }


  private boolean isBigRange(Drone d) {
    return Math.abs(state.previousState.dronesById[d.id].battery - state.dronesById[d.id].battery) == 5;
  }


  @Override
  public void setOptionsPane(GameOptionPane options) {
  }


  public void setAction(Action[] actions) {
    this.actions = actions;
  }
}
