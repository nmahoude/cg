package ooc.ai.start;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.OOCMap;
import ooc.P;

public class PatternStartPositionAITest {

	private PatternStartPositionAI sut;

	@BeforeEach
	public void setup() {
		sut = new PatternStartPositionAI();
	}
	
	@Test
	void emptyMapHas144emptySquareOfLength3() throws Exception {
		OOCMap map = new OOCMap();
		
		int count = sut.countSquareOfLength(map, 3).size();
		
		assertThat(count).isEqualTo(169);
	}
	
	@Test
	void emptyMapHas121emptySquareOfLength5() throws Exception {
		OOCMap map = new OOCMap();
		
		int count = sut.countSquareOfLength(map, 5).size();
		
		assertThat(count).isEqualTo(121);
	}

	@Test
	void emptyMapHas15emptyRectangleOfHeight15() throws Exception {
		OOCMap map = new OOCMap();
		
		int count = sut.countRectanglesOfLength(map, 15, 1).size();
		
		assertThat(count).isEqualTo(15);
	}

	@Test
	void anIsleInMiddleBlocks9SquaresOfLength3() throws Exception {
		OOCMap map = new OOCMap();
		map.cells.set(P.get(7, 7).o);
		
		
		int count = sut.countSquareOfLength(map, 3).size();
		
		assertThat(count).isEqualTo(169-9);
	}
	
	
}
