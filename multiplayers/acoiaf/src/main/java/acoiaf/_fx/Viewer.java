package acoiaf._fx;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import acoiaf.Action;
import cgfx.ViewRestorer;
import cgfx.frames.Frame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Viewer extends Application {
		AOICGameWrapper game = new AOICGameWrapper();
		AOICGameViewer viewer;
		AOICGameOptionPane options = new AOICGameOptionPane();
		ListView<String> actionsList;
		private ObservableList<String> actionsItems = FXCollections.observableArrayList ();
		
		public static void main(String[] args) throws FileNotFoundException {
			Application.launch(args);
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
      ViewRestorer.applyOn(primaryStage);

      
			readGameInfo();

			viewer = new AOICGameViewer(game, options);
					
			actionsList = new ListView<String>();
			actionsList.setItems(actionsItems);
			

			Button calculateAI = new Button("Calculate AI");
			calculateAI.setOnAction(e -> {
				calculateAI.setDisable(true);
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(() -> {
					try {
					  game.think();
						
						viewer.setActions(game.actions);
						viewer.update(null, null);
			
						Platform.runLater(() -> {
							actionsItems.clear();
							game.actions.stream().map(Action::debugString).forEach(actionsItems::add);
						});
						
						
					} finally {
						calculateAI.setDisable(false);
					}
				});
				executor.shutdown();
			});
			
			HBox buttons = new HBox();
			buttons.getChildren().add(calculateAI);
			
			HBox view = new HBox(viewer, new VBox(options, actionsList));
			VBox vbox = new VBox(buttons, view);

			Scene scene = new Scene(vbox, 1800, 900);

			primaryStage.setScene(scene);
			primaryStage.setTitle("AOIC - Debugger");
			primaryStage.show();

			
			primaryStage.xProperty().addListener(__ -> System.out.println(primaryStage.getX()));
			
			viewer.update(null, null);
			
		}

		private void readGameInfo() {
		  Frame f = Frame.fromInput("""
		  
GLOBAL
^6 1 0 5 1 2 5 9 6 6 10 10 11 
INTERNAL
^0
TURN
^ 21 1 20 2
^O......#####
^.........###
^.........###
^.........###
^..........##
^...........#
^#...........
^##..........
^###.........
^###.........
^###........X
^#####......X
^2
^ 0 0 0 0
^ 1 0 11 11
^ 0

	^ \s
	""");

		  // game.readInit(f0); // not needed here
			game.readFromFrame(f);
			game.state.debugPackedState();
		}
}
