package acraft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class AIDFSTest {

	private State state;

	@Test
	void debug() throws Exception {
		String input = ""
				+ "RRRRRRRRR#RRRRRRRRD\r\n"
				+ "U........U........D\r\n"
				+ "U........U........D\r\n"
				+ "U........U........D\r\n"
				+ "#LLL..............D\r\n"
				+ "U..............RRR#\r\n"
				+ "U........D........D\r\n"
				+ "U........D........D\r\n"
				+ "U........D........D\r\n"
				+ "ULLLLLLLL#LLLLLLLLL\r\n"
				+ "0\r\n"
				+ "";
		
		
		state = new State();
		state.read(new FastReader(input.getBytes()));
		
		addRobot(1,1,'R');
		//addRobot(17,0,'D');
		//addRobot(1,8,'U');
		//addRobot(17,8,'L');
		
		AIDFS ai = new AIDFS();
		ai.think(state);
		
		
		
	}

	private void addRobot(int x, int y, char dir) {
		state.addRobot(new Robot(Pos.get(x, y, dir)));
	}
}
