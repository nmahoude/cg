package spring2021.s21.components;

import java.util.List;

import cgfx.components.EvalNode;
import spring2021.ai.EvaluationNext;

/** Wrapper from game evaluator to view */
public class S21EvaluationNode implements EvalNode {

	EvaluationNext evaluation = new EvaluationNext();
	private double total;
	
	public S21EvaluationNode(S21GameWrapper gameWrapper) {
    total = evaluation.evaluate(gameWrapper.player.state);
	}
	
	public static List<String> names() {
		return List.of("total", "sunScore", "score", "my sun score", "currentSeedDeltaRichnessSerie");
	}

	@Override
	public double value(int index) {
		switch(index) {
		case 0: return total;
		case 1: return evaluation.sunScore;
		case 2: return evaluation.theScore;
		case 3: return evaluation.mySunScore;
		case 4: return evaluation.currentSeedDeltaRichness;
		default:
			throw new RuntimeException("Index inconnu : "+index);
		}
	}
	
}
