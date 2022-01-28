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
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###............####\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "0\r\n"
				+ "";
		
		
		state = new State();
		state.read(new FastReader(input.getBytes()));
		
		addRobot(3,4,'R');
		
		
		AIDFS ai = new AIDFS();
		ai.think(state);
		
		
		
	}

	private void addRobot(int x, int y, char dir) {
		state.addRobot(new Robot(Pos.get(x, y, dir)));
	}
}
