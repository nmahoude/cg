package acraft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class AIDFSTest {

	private State state;

	@Test
	void debug() throws Exception {
		String input = ""
				+ "###################\r\n"
				+ "#...#...#...#...###\r\n"
				+ "#...#...#...#...###\r\n"
				+ "#...#...#...#...###\r\n"
				+ "###################\r\n"
				+ "#...#...#...#...###\r\n"
				+ "#...#...#...#...###\r\n"
				+ "#...#...#...#...###\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "0\r\n"
				+ "";
		
		
		state = new State();
		state.read(new FastReader(input.getBytes()));
		
		addRobot(1,1,'R');
		addRobot(6,1,'D');
		addRobot(11,1,'L');
		addRobot(14,2,'D');
		addRobot(14,6,'U');
		addRobot(1,7,'R');
		addRobot(6,7,'U');
		addRobot(11,7,'L');
		
		AIDFS ai = new AIDFS();
		ai.think(state);
		
		
		
	}

	private void addRobot(int x, int y, char dir) {
		state.addRobot(new Robot(Pos.get(x, y, dir)));
	}
}
