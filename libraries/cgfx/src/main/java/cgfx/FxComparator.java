package cgfx;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import cgfx.components.ActionsList;
import cgfx.components.CGFactory;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FxComparator extends Application implements Observer {
  private final CGFactory factory;

  
	GameReader gameReader = new GameReader();
	GameWrapper wrapperLeft;
	GameWrapper wrapperRight;
	GameViewer viewerLeft;
	GameViewer viewerRight;
	EvaluationWrapper eWrapperLeft;
	EvaluationWrapper eWrapperRight;
	ActionsList actionListLeft;
	ActionsList actionListRight;
	
	XYChart.Series<String, Number> seriesLeft;
  XYChart.Series<String, Number> seriesRight;

	TextInputDialog td = new TextInputDialog();
	
	int turn = 1;

  public FxComparator(CGFactory factory) {
    this.factory = factory;
    wrapperLeft = factory.createGameWrapper();
    wrapperRight = factory.createGameWrapper();
    viewerLeft = factory.createGameViewer(wrapperLeft);
    viewerRight = factory.createGameViewer(wrapperRight);
    eWrapperLeft = factory.createEvaluationWrapper();
    eWrapperRight = factory.createEvaluationWrapper();

    actionListLeft = new ActionsList(wrapperLeft);
    actionListRight = new ActionsList(wrapperRight);
    
    final CategoryAxis xAxis = new CategoryAxis();
	    final NumberAxis yAxis = new NumberAxis();
	        
	    xAxis.setLabel("Actions");
	    yAxis.setLabel("score");
	    seriesLeft = new XYChart.Series();
	    seriesRight= new XYChart.Series();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		updateBarCharts();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		gameReader.readReplayFromFile("referee.json");
		wrapperLeft.readGlobal(gameReader.getInput(0));
		wrapperLeft.think();
		wrapperRight.copyFrom(wrapperLeft);
		wrapperRight.think();
		
		setupScene(primaryStage);
		
		wrapperLeft.addObserver(this);
		wrapperRight.addObserver(this);
		
		actionListLeft.refreshActions();
		actionListRight.refreshActions();

		td.setHeaderText("Turn to go ...");
	}

	private void updateBarCharts() {
		eWrapperLeft.update(wrapperLeft);
		eWrapperRight.update(wrapperRight);
		
		for (int i=0;i<eWrapperLeft.names().size();i++) {
		  seriesRight.getData().get(i).setYValue(eWrapperRight.value(i));
		  seriesLeft.getData().get(i).setYValue(eWrapperLeft.value(i));
		}
	}
	
	@SuppressWarnings("restriction")
	private void setupScene(Stage primaryStage) {
		primaryStage.setTitle("CG - Action comparator");
		
		final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    
    xAxis.setLabel("axe");
    yAxis.setLabel("value");
    BarChart chart = new BarChart<String,Number>(xAxis,yAxis);
    seriesLeft.setName("left");
    seriesRight.setName("right");
    
    for (String name : eWrapperLeft.names()) {
      seriesRight.getData().add(new XYChart.Data(name,0));
      seriesLeft.getData().add(new XYChart.Data(name,0));
    }
    
    chart.getData().add(seriesLeft);
    chart.getData().add(seriesRight);
		
		HBox toolbar = new HBox();
		
		Pane leftBox = new HBox(new VBox(viewerLeft), actionListLeft/*, left.getChart()*/);
		Pane rightBox = new HBox(new VBox(viewerRight/*, right.getChart()*/), actionListRight);
		
		HBox hbox = new HBox(leftBox, rightBox);
		VBox vbox = new VBox(toolbar, hbox, chart);
		Scene scene = new Scene(vbox, 1560, 1024);

		Button next = new Button("Next");
		next.setOnAction(value -> {
			turn++;
			wrapperLeft.readTurn(gameReader.getInput(turn));
			wrapperLeft.think();
	
			wrapperRight.copyFrom(wrapperLeft);
			wrapperRight.think();
	
			actionListLeft.refreshActions();
			actionListRight.refreshActions();
		});
		
		Button goTo = new Button("GoTo");
		goTo.setOnAction(value -> {
			td.setContentText("current ="+(turn+1));
			Optional<String> result = td.showAndWait();
			if (result.isPresent()) {
				int turnToGo = Integer.parseInt(result.get());
				System.err.println("Go to turn ... "+turnToGo);
				
				
				wrapperLeft.readGlobal(gameReader.getInput(0));
				wrapperRight.readGlobal(gameReader.getInput(0));
				turn = 0;
				for (int i=0;i<turnToGo;i++) {
					turn++;
					wrapperLeft.readTurn(gameReader.getInput(turn));
					wrapperRight.readTurn(gameReader.getInput(turn));
				}
				wrapperLeft.think();
				wrapperRight.think();

				actionListLeft.refreshActions();
				actionListRight.refreshActions();
			}
		});

		Button paste = new Button("Paste Game");
		paste.setOnAction(value -> {
			turn = 0;
			String pasteString = Clipboard.getSystemClipboard().getString();
			
			gameReader.readReplayFromString(pasteString);
			wrapperLeft.readGlobal(gameReader.getInput(0));
			wrapperRight.copyFrom(wrapperLeft);
			
		});
		
		toolbar.getChildren().add(next);
		toolbar.getChildren().add(goTo);
		toolbar.getChildren().add(paste);
		
		
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
