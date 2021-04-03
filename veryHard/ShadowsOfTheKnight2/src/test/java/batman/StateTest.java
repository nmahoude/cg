package batman;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StateTest {

	State state;

	@BeforeEach
	public void setup() {
		state = new State(200, 98);
		
		state.min = 0;
		state.max = 100 - 1;
	}

	@Nested
	class Apply {

		@Test
		public void firstRound_MoveToSymetricEndGreater() throws Exception {
			state.currentPos = state.oldPos = 1;
			int next = state.decide(BombDir.UNKNOWN);
			
			Assertions.assertThat(next).isEqualTo(98);
		}
		
		@Test
		public void firstRound_MoveToSymetricEnd() throws Exception {

			int next = state.decide(BombDir.UNKNOWN);

			Assertions.assertThat(next).isEqualTo(1);
		}

		@Test
		public void keepCorrectIntervalWhenWarmer() throws Exception {
			state.currentPos = 1;
			state.oldPos = 99;

			state.apply(BombDir.WARMER);

			Assertions.assertThat(state.min).isEqualTo(0);
			Assertions.assertThat(state.max).isEqualTo(49);
		}

		@Test
		public void cutIntervalIn2WhenBetweenMinAndMax() throws Exception {
			state.min = 50;
			state.max = 100;
			state.oldPos = 100;
			state.currentPos = 51;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isEqualTo(99);
		}
		
		
		@Test
		void goOppositeAtMaxIfBeforeMin() throws Exception {
			state.min = 0;
			state.max = 100;
			state.oldPos = 100;
			state.currentPos = -10;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isEqualTo(110);
		}

		@Test
		void goOppositeAtMaxIfBeforeMinCantExceedHeight() throws Exception {
			state.min = 0;
			state.max = 100;
			state.maxHeight = 100;
			state.oldPos = 100;
			state.currentPos = -10;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isEqualTo(100);
		}
		
		@Test
		void goOppositeAtMinIfAfterMax() throws Exception {
			state.min = 50;
			state.max = 60;
			state.oldPos = 100;
			state.currentPos = 65;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isEqualTo(45);
		}

		@Test
		void goOppositeAtMinIfAfterMaxCantGoUnder0() throws Exception {
			state.min = 0;
			state.max = 30;
			state.oldPos = 100;
			state.currentPos = 40;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isEqualTo(0);
		}
		
		@Test
		void whenInTheMiddleOfPossibleInterval() throws Exception {
			state.min = 2;
			state.max = 4;
			state.oldPos = 1;
			state.currentPos = 3;

			int next = state.decide(BombDir.WARMER);

			Assertions.assertThat(next).isNotEqualTo(3);
		}

		@Test
		void dontGoTooFar() throws Exception {
			state.maxHeight = 1000;
			state.min = 500;
			state.max = 999;
			state.oldPos = 498;
			state.currentPos = 999;

			int next = state.decide(BombDir.COLDER);

			Assertions.assertThat(next).isEqualTo(500);
			
		}

	}

	@Nested
	class Decide {
		@Test
		void decideWhenIntervalIsOneLong() throws Exception {
			state.min = 0;
			state.max = 1;
			state.oldPos = 0;
			state.currentPos = 1;

			state.apply(BombDir.WARMER);

			Assertions.assertThat(state.min).isEqualTo(1);
			Assertions.assertThat(state.max).isEqualTo(1);
			
		}
		
		
		@Test
		public void keepCorrectIntervalWhenWarmer2() throws Exception {
			state.min = 50;
			state.max = 100;
			state.oldPos = 100;
			state.currentPos = 50;

			state.apply(BombDir.WARMER);

			Assertions.assertThat(state.min).isEqualTo(50);
			Assertions.assertThat(state.max).isEqualTo(74);
		}

		@Test
		public void keepCorrectIntervalWhenWarmerAndWentUp() throws Exception {
			state.min = 0;
			state.max = 50;
			state.oldPos = 0;
			state.currentPos = 50;

			state.apply(BombDir.WARMER);

			Assertions.assertThat(state.min).isEqualTo(26);
			Assertions.assertThat(state.max).isEqualTo(50);
		}

		@Test
		public void colderRemoveZoneImIn() throws Exception {
			state.min = 0;
			state.max = 100;
			state.oldPos = 100;
			state.currentPos = 0;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(51);
			Assertions.assertThat(state.max).isEqualTo(100);
		}

		@Test
		public void colderRemoveZoneImInWhenWentUp() throws Exception {
			state.min = 0;
			state.max = 100;
			state.oldPos = 0;
			state.currentPos = 100;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(0);
			Assertions.assertThat(state.max).isEqualTo(49);
		}

		@Test
		void asCloseMeanBombIsInTheMiddle() throws Exception {
			state.min = 0;
			state.max = 99;
			state.oldPos = 0;
			state.currentPos = 99;

			state.apply(BombDir.SAME);

			Assertions.assertThat(state.min).isEqualTo(50);
			Assertions.assertThat(state.max).isEqualTo(50);
			
		}
		
		@Test
		void removeEnoughWhenColderUpper() throws Exception {
			state.min = 0;
			state.max = 5;
			state.oldPos = 0;
			state.currentPos = 5;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(0);
			Assertions.assertThat(state.max).isEqualTo(2);
			
		}

		@Test
		void removeEnoughWhenColderDown() throws Exception {
			state.min = 0;
			state.max = 49;
			state.oldPos = 29;
			state.currentPos = 20;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(25);
			Assertions.assertThat(state.max).isEqualTo(49);
			
		}

		
		@Test
		void middleIsREmovedWhenNotAsClose() throws Exception {
			state.min = 0;
			state.max = 14;
			state.oldPos = 6;
			state.currentPos = 8;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(0);
			Assertions.assertThat(state.max).isEqualTo(6);
			
		}
		
		@Test
		void colderWithBigDelta() throws Exception {
			state.min = 0;
			state.max = 3;
			state.oldPos = 0;
			state.currentPos = 3;

			state.apply(BombDir.COLDER);

			Assertions.assertThat(state.min).isEqualTo(0);
			Assertions.assertThat(state.max).isEqualTo(1);
			
		}

	}

	private Point point(int x, int y) {
		return new Point(x, y);
	}
}
