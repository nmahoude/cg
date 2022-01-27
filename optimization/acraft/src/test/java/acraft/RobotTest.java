package acraft;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class RobotTest {

	@Test
	void calculateScore() throws Exception {
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
		
		
		State state = new State();
		state.read(new FastReader(input.getBytes()));
		state.addRobot(new Robot(Pos.get(3, 4, Pos.RIGHT)));
		
		int score = state.calculateScore();
		assertThat(score).isEqualTo(12);
	}
	
	@Test
	void turn() throws Exception {
		String input = ""
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###...........L####\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "0\r\n"
				+ "";
		
		
		State state = new State();
		state.read(new FastReader(input.getBytes()));
		state.addRobot(new Robot(Pos.get(3, 4, Pos.RIGHT)));
		
		int score = state.calculateScore();
		assertThat(score).isEqualTo(23);
	}
	
	@Test
	void dontLoop() throws Exception {
		String input = ""
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###R..........L####\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "###################\r\n"
				+ "0\r\n"
				+ "";
		
		
		State state = new State();
		state.read(new FastReader(input.getBytes()));
		state.addRobot(new Robot(Pos.get(3, 4, Pos.RIGHT)));
		
		int score = state.calculateScore();
		assertThat(score).isEqualTo(23);
	}
}
