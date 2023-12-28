package cgfx;

import java.util.List;

import cgfx.components.CGFactory;
import cgfx.components.EvalChart;
import cgfx.components.EvalNode;
import cgfx.components.GameViewer;
import cgfx.frames.GameReader;
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

	static class FXEvalNode implements EvalNode {

    public static List<String> names() {
      return List.of("global");
    }

    @Override
    public double value(int index) {
      return 5;
    }
	  
	}

	@SuppressWarnings("restriction")
	private void setupScene(Stage primaryStage) {
		primaryStage.setTitle("CG - Evaluation ");
		
		EvalChart chart = new EvalChart(FXEvalNode.names());
		
		wrapper.readGlobal(gameReader.getCleanInput(0));
		for (int index=0;index<gameReader.getMaxTurn();index++) {
		chart.addNode(new FXEvalNode());
			wrapper.readTurn(gameReader.getInput(index));
			index++;
		}		

		Scene scene = new Scene(chart);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
