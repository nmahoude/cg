package optimizer;

import com.codingame.gameengine.runner.dto.GameResult;

@FunctionalInterface
public interface GameResolver {

	GameResult resolveGame(long seed, String cmd1, String cmd2);
}
