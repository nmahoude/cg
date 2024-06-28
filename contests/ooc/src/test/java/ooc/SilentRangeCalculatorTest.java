package ooc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ooc.orders.Charge;
import ooc.orders.Order;
import ooc.orders.Orders;

public class SilentRangeCalculatorTest {

	
	private SilentRangeCalculator silentRangeCalculator;
	private BitSet trail;
	private P currentPos;
	
	@BeforeEach
	public void setup() {
		trail = new BitSet();
		silentRangeCalculator = new SilentRangeCalculator();
	}
	
	@Test
	void allRangeMaxWhenNoOrders() throws Exception {
		int[] ranges = silentRangeCalculator.run(P.get(7, 7), trail);
		
		assertThat(ranges).isEqualTo(new int[] { 4, 4, 4, 4});
	}
	
	@Test
	void northRangeIs0WhenLastMoveSouth() throws Exception {
		P pos = startPosition(7,7).south().current();
		
		int[] ranges = silentRangeCalculator.run(pos, trail);
		
		assertThat(ranges).isEqualTo(new int[] { 0, 4, 4, 4});
	}
	
	@Test
	void northAndWestRangeIs0WhenLastMovesN_E_S() throws Exception {
		P pos = startPosition(7,7).north().east().south().current();

		int[] ranges = silentRangeCalculator.run(pos, trail);
		
		assertThat(ranges).isEqualTo(new int[] { 0, 4, 4, 0});
	}

	@Test
	void onlySouthWhenLastMovesN_E_E_S_W() throws Exception {
		P pos = startPosition(7,7).north().east().east().south().west().current();
		
		int[] ranges = silentRangeCalculator.run(pos, trail);
		
		assertThat(ranges).isEqualTo(new int[] { 0, 0, 4, 0});
	}

	@Test
	void noRangeWhenLastMovesN_E_E_S_S_W_N() throws Exception {
		P pos = startPosition(7,7).north().east().east().south().south().west().north().current();
		int[] ranges = silentRangeCalculator.run(pos, trail);

		
		assertThat(ranges).isEqualTo(new int[] { 0, 0, 0, 0});
	}

	private P current() {
		return currentPos;
	}

	private SilentRangeCalculatorTest north() {
		currentPos = currentPos.neighbors[0];
		trail.set(currentPos.o);
		return this;
	}
	private SilentRangeCalculatorTest east() {
		currentPos = currentPos.neighbors[1];
		trail.set(currentPos.o);
		return this;
	}
	private SilentRangeCalculatorTest south() {
		currentPos = currentPos.neighbors[2];
		trail.set(currentPos.o);
		return this;
	}
	private SilentRangeCalculatorTest west() {
		currentPos = currentPos.neighbors[3];
		trail.set(currentPos.o);
		return this;
	}

	private SilentRangeCalculatorTest startPosition(int x, int y) {
		currentPos = P.get(x, y);
		trail.set(currentPos.o);
		return this;
	}

	private void setVisitedPos(int x, int y) {
		trail.set(P.get(x, y).o);
	}


}
