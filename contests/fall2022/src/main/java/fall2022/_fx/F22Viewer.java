package fall2022._fx;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fall2022.Action;
import fall2022.Player;
import fall2022.Pos;
import fall2022._fx.modules.F22GameOptionPane;
import fall2022._fx.modules.F22GameViewer;
import fall2022._fx.modules.F22GameWrapper;
import fall2022.ai.Search;
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

public class F22Viewer extends Application {
	F22GameWrapper wrapper = new F22GameWrapper();
	F22GameViewer viewer;
	F22GameViewer viewerOld;
	F22GameOptionPane options = new F22GameOptionPane();
	ListView<String> actionsList;
	private ObservableList<String> actionsItems = FXCollections.observableArrayList ();
	
	public static void main(String[] args) throws FileNotFoundException {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		readGameInfo();

		viewer = new F22GameViewer(wrapper);
		viewerOld = new F22GameViewer(wrapper);
				
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
^17 8
Optional State :
^ 8 0
^25 15
^ 0 12288 16384 18432 0 12288 8192 8192 8192 10241 0 20480 16384 16384 12288 16384 0
^ 0 0 0 17414 17414 13318 19462 9223 19463 19465 14849 8192 18432 12288 20480 16384 12288
^18432 0 9222 21510 19462 9222 13319 9225 17415 10785 18960 16384 16384 18432 18432 0 8192
^16384 0 0 19462 17414 13318 13318 13319 17426 18961 20992 0 8192 0 16384 12288 16384
^16384 12288 16384 0 9222 0 21510 21522 14849 14857 10753 4608 16896 18944 0 0 16384
^ 8192 0 18432 18432 17414 17414 19490 12816 20992 10753 8704 0 10752 20992 8704 0 18432
^12288 16384 20480 12288 18432 9234 16912 21008 20992 12800 18944 4608 16896 16896 0 0 0
^ 0 16384 12288 16384 16384 20480 0 12288 8192 8192 8192 12288 0 18432 16384 12288 0

^ \s
""";

		Search.MAX_SEARCH_TIME = 200_000;
	
		String cleanInput = Stream.of(input.split("\n"))
															.map(String::trim)
															.filter(s -> s.length() != 0 && s.charAt(0) == '^')
															.map(s -> s.replace("^", " ").concat("\n")) // remove ^
															.collect(Collectors.joining());

		System.err.println("Cleaned string is " + cleanInput);
		wrapper.readFromInput(cleanInput);
		
		//wrapper.state.recycler[Pos.from(10,4).offset] = 1;
//		wrapper.state.myMatter = 20;
		//wrapper.state.owner[Pos.from(6,1).offset] = O.OPP;
		
		//resetUnitsOn(Pos.from(7,6));
//		putUnitOn(2, Pos.from(16,8), O.OPP);
//		putUnitOn(0, Pos.from(14,6), O.ME);
		
		wrapper.state.debugPackedState();
	}

	private void putUnitOn(int count, Pos from, int owner) {
		wrapper.state.o[from.o] = owner;
		wrapper.state.u[from.o] = count;
		wrapper.state.mu[from.o] = 0;
		wrapper.state.ou[from.o] = count;
		
	}

	private void resetUnitsOn(Pos from) {
		wrapper.state.u[from.o] = 0;
		wrapper.state.mu[from.o] = 0;
		wrapper.state.ou[from.o] = 0;
	}

}
