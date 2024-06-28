package spring2022._cgfx;

import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.State;
import spring2022._cgfx.components.S22GameViewer;
import spring2022._cgfx.components.S22GameWrapper;
import spring2022.ai.AttackerV2;

/**
 * Debug the heuristic (of attacker ?)
 * @author nmahoude
 *
 */
public class S22SimpleAIDebug extends Application {
  S22GameWrapper wrapper = new S22GameWrapper();
  S22GameViewer viewer = new S22GameViewer(15, wrapper);

  S22GameWrapper wrapper2 = new S22GameWrapper();
  S22GameViewer viewer2 = new S22GameViewer(50, wrapper2);

  
  private State state;
  private AttackerV2 attacker = new AttackerV2();
  private Hero hero;
  public static void main(String[] args) throws FileNotFoundException {
    
    Application.launch(args);

  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {

    readGame();

    VBox viewer2Group = new VBox ();
    viewer2Group.getChildren().add(viewer2);
    
    Button think = new Button("Think");
    Text output = new Text();
    
    HBox view = new HBox(think, viewer) ;//, viewer2Group);
    VBox vbox = new VBox(view, output);
    


    
    think.setOnAction(value -> {
      updateView();

      Action action = attacker.think(state, state.myHeroes[2]);
      ActionDrawer.drawAction(viewer, hero, action, state.findPosById(action.targetEntity));
      output.setText(action.toString());
    });
    think.fire();
    

    
    
    Scene scene = new Scene(vbox, 1920, 1080);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Spring22 - SimpleAI Debugger");
    primaryStage.show();
  }


  private void updateView() {
    
    viewer.update(null, null);
    viewer2.update(null, null);
    
  }


  private void readGame() {
    this.state = wrapper.player.state;
    this.hero = state.myHeroes[2];
    
    
    boolean inverse = false;
    wrapper.readFromInput(inverse, """
^3 74 2 142
^9
^0 1 4647 998 0 0 -1 -1 -1 -1 -1
^1 1 1614 2898 0 0 -1 -1 -1 -1 -1
^2 1 11974 7458 0 0 -1 -1 -1 -1 -1
^5 2 5288 1044 0 0 -1 -1 -1 -1 -1
^51 0 3213 3212 0 0 5 -282 -282 1 1
^57 0 2493 3201 0 0 8 -245 -315 1 1
^61 0 2671 3025 0 0 16 -264 -299 1 1
^66 0 6367 1257 0 0 17 -306 257 0 0
^67 0 11263 7743 0 0 17 306 -257 0 0
****************************
UNITS IN FOG - debug 'input'
****************************
^2
^54 0 5501 5901 0 0 4 -60 395 0 0
^59 0 16402 8614 0 0 12 381 119 1 2
turn 85
*************************
*  ATTACKER V2     *
*************************
^turn 85
^mind 2
        """);

    //Player.draw = viewer;
    Player.start = System.currentTimeMillis() + 100;
  }

}
