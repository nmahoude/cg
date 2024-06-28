package ooc.trailmapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.Direction;
import ooc.OOCMap;
import ooc.P;

public class PositionDetectorLight2Test {
	PositionDetectorLight2 sut;
	
	@BeforeEach
	public void setup() {
		OOCMap map = new OOCMap();
		sut = new PositionDetectorLight2(map);
	}
	
	
	@Test
	void whenMoveEastThenPositionIsTranslated() throws Exception {
		placeAt(P.get(7, 7));
		
		sut.move(Direction.EAST);

		assertThat(sut.hasPositions(P.get(8, 7))).isTrue();
		assertThat(sut.hasPositions(P.get(7, 7))).isFalse();
	}
	
	@Test
	void whenMoveEastAtBorderThenPositionDisappear() throws Exception {
		placeAt(P.get(14, 7));
		
		sut.move(Direction.EAST);

		assertThat(sut.hasPositions(P.get(14, 7))).isFalse();
		assertThat(sut.hasPositions(P.get(0, 8))).isFalse();
	}
	
	@Test
	void whenMoveWestAtBorderThenPositionDisappear() throws Exception {
		placeAt(P.get(0, 7));
		
		sut.move(Direction.WEST);

		assertThat(sut.hasPositions(P.get(0, 7))).isFalse();
		assertThat(sut.hasPositions(P.get(14, 6))).isFalse();
	}

	@Test
	void whenMoveNorthThenPositionTranslated() throws Exception {
		placeAt(P.get(7, 7));
		
		sut.move(Direction.NORTH);

		assertThat(sut.hasPositions(P.get(7, 6))).isTrue();
	}

	private void placeAt(P pos) {
		sut.addPositions(pos);
	}
}
