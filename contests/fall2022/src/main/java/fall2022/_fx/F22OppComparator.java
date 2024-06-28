package fall2022._fx;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fall2022.Action;
import fall2022.Player;
import fall2022._fx.modules.F22GameOptionPane;
import fall2022._fx.modules.F22GameViewer;
import fall2022._fx.modules.F22GameWrapper;
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

public class F22OppComparator extends Application {
	F22GameWrapper wrapper = new F22GameWrapper();
	F22GameViewer viewer = new F22GameViewer(wrapper);
	F22GameViewer viewerOld = new F22GameViewer(wrapper);
	F22GameOptionPane options = new F22GameOptionPane();
	ListView<String> actionsList;
	private ObservableList<String> actionsItems = FXCollections.observableArrayList ();
	
	public static void main(String[] args) throws FileNotFoundException {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		readGameInfo();

		actionsList = new ListView<String>();
		actionsList.setItems(actionsItems);
		
		viewer.setOptionsPane(options);
		viewerOld.setOptionsPane(options);
		options.register(viewer);

		Button calculateAI = new Button("Calculate AI");
		calculateAI.setOnAction(e -> {
			calculateAI.setDisable(true);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(() -> {
				try {
					List<Action> actions = Player.ai.think(wrapper.state);

					System.err.println("New Actions ");
					actions.forEach(Action::debug);
					
					viewer.setActions(actions);
					viewer.update(null, null);
					
					Platform.runLater(() -> {
						actionsItems.clear();
						actions.stream().map(Action::debugString).forEach(actionsItems::add);
					});
					
					/*
					List<Action> actionsOld = new AIOld().think(wrapper.state);
					viewerOld.setActions(actionsOld);
					System.err.println("Old actions ");
					actionsOld.forEach(Action::debug);

					
					viewerOld.update(null, null);
					*/
					
					
				} finally {
					calculateAI.setDisable(false);
				}
			});
			executor.shutdown();
		});
		
		HBox view = new HBox(viewer, new VBox(options, actionsList));
//    HBox solutions = new HBox(listOfSolutions, listOfSteps);
		VBox vbox = new VBox(calculateAI, view);

		Scene scene = new Scene(vbox, 1800, 900);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Fall 22 - Debugger");
		primaryStage.show();

		viewer.update(null, null);
		viewerOld.update(null, null);
	}

	private void readGameInfo() {
	String input = """
Global
^16 8
Optional State :
^ 10 0
^30 24
^ 8192 3078 0 5129 7175 17414 21510 21510 8192 0 12288 18432 8192 8192 16384 0
^ 3078 21510 13318 7175 5126 0 0 13318 17426 20480 12800 8192 16384 8192 18432 8192
^ 0 3078 9222 0 0 9222 17415 13318 17426 16912 16896 12800 8192 8192 12288 8192
^ 8192 5126 9222 8192 8192 6145 15369 18433 0 12801 4096 16896 16896 16896 12288 0
^ 0 0 5126 17414 17414 13318 17443 0 14337 8713 0 8704 8704 8704 16896 8192
^ 8192 0 9222 9222 13318 17426 16944 16896 12800 12801 8704 8704 0 8704 2560 0
^ 8192 18432 9222 17414 9222 13318 21008 16896 12288 0 0 12800 6657 12800 20992 2560
^ 0 16384 8192 8192 18432 12288 0 8704 20992 20992 16896 6657 4617 0 2560 8192
 
   """;

		String cleanInput = Stream.of(input.split("\n"))
															.map(String::trim)
															.filter(s -> s.length() != 0 && s.charAt(0) == '^')
															.map(s -> s.replace("^", " ").concat("\n")) // remove ^
															.collect(Collectors.joining());

		System.err.println("Cleaned string is " + cleanInput);
		wrapper.readInversedFromInput(cleanInput);
		
		//wrapper.state.recycler[Pos.from(10,4).offset] = 1;
		//wrapper.state.myMatter = 100;
		//wrapper.state.owner[Pos.from(6,1).offset] = O.OPP;
//		wrapper.state.units[Pos.from(11,2).offset] = 1;
//		wrapper.state.movableUnits[Pos.from(11,2).offset] = 1;
	}

}
