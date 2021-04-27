package optimizer;

import com.codingame.gameengine.runner.simulate.GameResult;

@FunctionalInterface
public interface GameResolver {

	GameResult resolveGame(long seed, String cmd1, String cmd2);
}
