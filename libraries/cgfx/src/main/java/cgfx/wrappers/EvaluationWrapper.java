package cgfx.wrappers;

import java.util.List;

public interface EvaluationWrapper {

	int count();

	List<String> names();

	double value(int index);

	void update(GameWrapper gameWrapper);

}