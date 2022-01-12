package xmashrush2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StateTest {

	
	@Test
	void pushUpWhenNoOneOnColumn_makesTheColumnShiftAcordingly() throws Exception {
		State sut = new State();
		setupCells(sut, 
				"0123456"+
				"2123456"+
				"5123456"+
				"6123456"+
				"5123456"+
				"3123456"+
				"1123456"+
				""
				);
		sut.agents[0].cell = 4;
		sut.agents[0].item = 0;
		
		
		sut.apply(PushAction.actions(0, Direction.UP), null);
		
		assertThat(column(sut, 0)).isEqualTo("2565314");
		assertThat(sut.agents[0].cell).isEqualTo(0);
	}

	@Test
	void pushDownWhenNoOneOnColumn_makesTheColumnShiftAcordingly() throws Exception {
		State sut = new State();
		setupCells(sut, 
				"0123456"+
				"2123456"+
				"5123456"+
				"6123456"+
				"5123456"+
				"3123456"+
				"1123456"+
				""
				);
		sut.agents[0].cell = 4;
		sut.agents[0].item = 0;
		
		
		sut.apply(PushAction.actions(0, Direction.DOWN), null);
		
		assertThat(column(sut, 0)).isEqualTo("4025653");
		assertThat(sut.agents[0].cell).isEqualTo(1);
	}
	
	@Test
	void pushRightWhenNoOneOnColumn_makesTheRowsShiftAcordingly() throws Exception {
		State sut = new State();
		setupCells(sut, 
				"0123456"+
				"2123456"+
				"5123456"+
				"6123456"+
				"5123456"+
				"3123456"+
				"1123456"+
				""
				);
		sut.agents[0].cell = 4;
		sut.agents[0].item = 0;
		
		
		sut.apply(PushAction.actions(0, Direction.RIGHT), null);
		
		assertThat(row(sut, 0)).isEqualTo("4012345");
		assertThat(sut.agents[0].cell).isEqualTo(6);
	}
	
	@Test
	void pushLeftWhenNoOneOnColumn_makesTheRowsShiftAcordingly() throws Exception {
		State sut = new State();
		setupCells(sut, 
				"0123456"+
				"2123456"+
				"5123456"+
				"6123456"+
				"5123456"+
				"3123456"+
				"1123456"+
				""
				);
		sut.agents[0].cell = 4;
		sut.agents[0].item = 0;
		
		
		sut.apply(PushAction.actions(0, Direction.LEFT), null);
		
		assertThat(row(sut, 0)).isEqualTo("1234564");
		assertThat(sut.agents[0].cell).isEqualTo(0);
	}
	
	
	@Test
	void pushLeft_agentMoveWhenHisCellIsRemovedFromGrid() throws Exception {
		State sut = blankState();
		sut.agents[0].pos = Pos.from(0,0);
		
		sut.apply(PushAction.actions(0, Direction.LEFT), null);
		
		assertThat(sut.agents[0].pos).isEqualTo(Pos.from(6,0));
	}

	@Test
	void pushRight_agentMoveWhenPushOccurs() throws Exception {
		State sut = blankState();
		sut.agents[0].pos = Pos.from(3,0);
		
		sut.apply(PushAction.actions(0, Direction.RIGHT), null);
		
		assertThat(sut.agents[0].pos).isEqualTo(Pos.from(4,0));
	}

	@Test
	void pushRight_agentMoveWhenHisCellIsRemovedFromGrid() throws Exception {
		State sut = blankState();
		sut.agents[0].pos = Pos.from(6,0);
		
		sut.apply(PushAction.actions(0, Direction.RIGHT), null);
		
		assertThat(sut.agents[0].pos).isEqualTo(Pos.from(0,0));
	}

	@Test
	void pushUp_agentMoveWhenHisCellIsRemovedFromGrid() throws Exception {
		State sut = blankState();
		sut.agents[0].pos = Pos.from(4,0);
		
		sut.apply(PushAction.actions(4, Direction.UP), null);
		
		assertThat(sut.agents[0].pos).isEqualTo(Pos.from(4,6));
	}

	@Test
	void pushDown_agentMoveWhenHisCellIsRemovedFromGrid() throws Exception {
		State sut = blankState();
		sut.agents[0].pos = Pos.from(4,6);
		
		sut.apply(PushAction.actions(4, Direction.DOWN), null);
		
		assertThat(sut.agents[0].pos).isEqualTo(Pos.from(4,0));
	}

	
	@Test
	void pushOnOneQuestItemsIncreaseScore() throws Exception {
		State sut = blankState();
		Agent agent = sut.agents[0];
		agent.pos = Pos.from(0,0);
		agent.addQuest(1); // the item agent needs
		agent.item = 1; // the item on our cell
		agent.score = 10;
		
		sut.apply(PushAction.actions(0, Direction.LEFT), null);
		
		assertThat(agent.pos).isEqualTo(Pos.from(6,0)); // wrapped
		assertThat(agent.score).isEqualTo(11); // get score increase
	}
	
	
	private State blankState() {
		State sut = new State();
		setupCells(sut, 
				"0000000"+
				"0000000"+
				"0000000"+
				"0000000"+
				"0000000"+
				"0000000"+
				"0000000"+
				""
				);
		sut.agents[0].cell = 0;
		sut.agents[0].item = 0;
		return sut;
	}
	
	private void setupCells(State state, String content) {
		int index = 0;
		for (int y=0;y<7;y++) {
			for (int x=0;x<7;x++) {
				state.cells[Pos.from(x,y).offset] = Integer.parseInt(content.substring(index, index+1));
				index++;
			}
		}
	}

	private String column(State state, int col) {
		String content = "";
		for (int y=0;y<7;y++) {
			content+=state.cells[Pos.from(col, y).offset];
		}
		return content;
	}

	private String row(State state, int row) {
		String content = "";
		for (int x=0;x<7;x++) {
			content+=state.cells[Pos.from(x, row).offset];
		}
		return content;
	}
}
