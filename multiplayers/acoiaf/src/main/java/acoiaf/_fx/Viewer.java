package acoiaf._fx;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import acoiaf.Action;
import acoiaf.Player;
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
		AOICGameWrapper wrapper = new AOICGameWrapper();
		AOICGameViewer viewer;
		AOICGameOptionPane options = new AOICGameOptionPane();
		ListView<String> actionsList;
		private ObservableList<String> actionsItems = FXCollections.observableArrayList ();
		
		public static void main(String[] args) throws FileNotFoundException {
			Application.launch(args);
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
			readGameInfo();

			viewer = new AOICGameViewer(wrapper);
					
			actionsList = new ListView<String>();
			actionsList.setItems(actionsItems);
			
			viewer.setOptionsPane(options);
			options.register(viewer);

			Button calculateAI = new Button("Calculate AI");
			calculateAI.setOnAction(e -> {
				calculateAI.setDisable(true);
				ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(() -> {
					try {
						long start = System.currentTimeMillis();
						List<Action> actions = Player.ai.think(wrapper.state);
						long end = System.currentTimeMillis();
						System.err.println("Think in "+(end-start)+"ms");
						System.err.println("New Actions ");
						actions.forEach(Action::debug);
						
						viewer.setActions(actions);
						viewer.update(null, null);
			
						Platform.runLater(() -> {
							actionsItems.clear();
							actions.stream().map(Action::debugString).forEach(actionsItems::add);
						});
						
						
					} finally {
						calculateAI.setDisable(false);
					}
				});
				executor.shutdown();
			});
			
			HBox view = new HBox(viewer, new VBox(options, actionsList));
			VBox vbox = new VBox(calculateAI, view);

			Scene scene = new Scene(vbox, 1800, 900);

			primaryStage.setScene(scene);
			primaryStage.setTitle("Fall 22 - Debugger");
			primaryStage.show();

			viewer.update(null, null);
		}

		private void readGameInfo() {
		String input = """
		  
GLOBAL
^12 1 0 5 0 6 0 2 1 3 2 5 3 6 8 8 9 9 10 5 11 6 11 10 11 
INTERNAL
^8
TURN
^ 28 20 8 26
^OOOOOOOOO..#
^OOOOOOOO....
^OOOOOOO.....
^.OO........#
^...#XXX##..#
^..###XX###..
^..###XX###..
^#..##XXX#...
^#...XXXXXX..
^....XXXXXXX.
^..XXXXXXXXXX
^#..........X
^2
^ 0 0 0 0
^ 1 0 11 11
^ 14
^0 1 1 8 0
^0 4 1 5 2
^0 6 1 7 1
^0 8 1 1 3
^0 10 1 2 3
^0 12 1 6 2
^1 2 1 4 9
^1 3 1 4 8
^1 5 1 3 10
^1 7 1 5 6
^1 9 1 6 4
^1 11 1 5 4
^1 13 1 4 4
^1 14 1 2 10

	^ \s
	""";

			String cleanInput = Stream.of(input.split("\n"))
																.map(String::trim)
																.filter(s -> s.length() != 0 && s.charAt(0) == '^')
																.map(s -> s.replace("^", " ").concat("")) // remove ^
																.collect(Collectors.joining());

			System.err.println("Cleaned string is " + cleanInput);
			wrapper.readFromInput(cleanInput);
			wrapper.state.debugPackedState();
		}
}
