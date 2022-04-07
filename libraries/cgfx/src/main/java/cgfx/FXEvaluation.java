package cgfx;

import cgfx.components.CGFactory;
import cgfx.components.EvalChart;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Graph of how the evaluation change over turns
 * @author TH5893
 *
 */
public class FXEvaluation extends Application {
  private final CGFactory factory;

	GameReader gameReader = new GameReader();
	GameWrapper wrapper;
	EvaluationWrapper eWrapper;
	GameViewer right;

  public FXEvaluation(CGFactory factory) {
    this.factory = factory;
    wrapper = factory.createGameWrapper();
    eWrapper = factory.createEvaluationWrapper();
    right = factory.createGameViewer(wrapper);

  }

	int turn = 1;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		gameReader.readReplayFromFile("referee.json");

		wrapper.readGlobal(gameReader.getInput(0));
		
		setupScene(primaryStage);
	}


	@SuppressWarnings("restriction")
	private void setupScene(Stage primaryStage) {
		primaryStage.setTitle("CG - Evaluation ");
		
		EvalChart chart = new EvalChart(eWrapper);
		
		int index = 0;
		while (index < gameReader.inputs.size()-1) {
			chart.update(index, wrapper);
			index++;
			wrapper.readTurn(gameReader.getInput(index));
		}		

		Scene scene = new Scene(chart);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
