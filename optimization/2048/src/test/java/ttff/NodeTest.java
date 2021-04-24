package ttff;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NodeTest {

	private Node parent;
	private Node child;

	@BeforeEach
	public void setup() {
		parent = new Node();
		child = new Node();
	}
	
	@Test
	public void shouldCalculateNextSeed() throws Exception {
		parent.seed = 6835255L;
		
		assertThat(parent.nextSeed()).isEqualTo(8610627L);
	}
	
	@Test
	public void shouldNotDecal_when_full() throws Exception {
		parent.bits = 0b1000_0100_0010_0001L;
		child.left(parent);
		assertThat(child.bits).isEqualTo(0b1000_0100_0010_0001L);
	}

	@Test
	public void shouldDecalOneLineFullLeftToLEft_when_left() throws Exception {
		parent.bits = 0b0000_0100_0010_0001L;
		child.left(parent);
		assertThat(tbs(child.bits)).isEqualTo(tbs(0b0100_0010_0001_0000L));
	}

	private String tbs(long result) {
		return Long.toBinaryString(result);
	}
	
	@Test
	public void shouldDecalFullLeftToLEft_when_left() throws Exception {
		parent.bits = 0b1000_0000_0100_0000___0000_0000_0000_0110L;
		child.left(parent);
		assertThat(child.bits).isEqualTo(0b1000_0100_0000_0000___0110_0000_0000_0000L);
	}
	
	@Test
	public void shouldMerge_when_left() throws Exception {
		parent.bits = 0b0100_0000_0100_0000L;
		child.left(parent);
		assertThat(tbs(child.bits)).isEqualTo(tbs(0b0101_0000_0000_0000L));
		assertThat(child.score).isEqualTo(pow(2, 5));
	}
	
	@Test
	public void shouldMerge_when_right() throws Exception {
		parent.bits = 0b0100_0000_0100_0000L;
		child.right(parent);
		assertThat(tbs(child.bits)).isEqualTo(tbs(0b0000_0000_0000_0101L));
		assertThat(child.score).isEqualTo(pow(2, 5));
	}

	
	@Nested
	class transpose {
		@Test
		void transpose1() throws Exception {
			parent.bits = 
					 (0b1111_1110_1100_1000L << 48)
					+(0b0000_0000_0000_0000L << 32)
					+(0b0000_0000_0000_0000L << 16)
					+(0b1000_0100_0010_0001L << 0)
					;

			long expected = 
				 (0b1111_0000_0000_1000L << 48)
				+(0b1110_0000_0000_0100L << 32)
				+(0b1100_0000_0000_0010L << 16)
				+(0b1000_0000_0000_0001L << 0)
				;
			
			
			long result = parent.transpose(parent.bits);
			
			assertThat(result).isEqualTo(expected);
			
		}
	}
	
	@Test
	public void shouldShiftUp_when_up() throws Exception {
		parent.bits = (0b0000_0000_0000_0000L << 48)
								+(0b0100_0000_0000_0000L << 32)
								+(0b0010_0000_0000_0000L << 16)
								+(0b0001_0000_0000_0000L << 0)
								;
		
		long expected = 
				 (0b0100_0000_0000_0000L << 48)
				+(0b0010_0000_0000_0000L << 32)
				+(0b0001_0000_0000_0000L << 16)
				+(0b0000_0000_0000_0000L << 0)
				;
		
		
		child.up(parent);
		
		assertThat(child.bits).isEqualTo(expected);
	}

	@Test
	public void shouldMergeUp_when_up() throws Exception {
		parent.bits = (0b0000_0000_0000_0000L << 48)
								+(0b0100_0000_0000_0000L << 32)
								+(0b0100_0000_0000_0000L << 16)
								+(0b0001_0000_0000_0000L << 0)
								;
		
		long expected = 
				 (0b0101_0000_0000_0000L << 48)
				+(0b0001_0000_0000_0000L << 32)
				+(0b0000_0000_0000_0000L << 16)
				+(0b0000_0000_0000_0000L << 0)
				;
		
		
		child.up(parent);
		
		assertThat(child.bits).isEqualTo(expected);
		assertThat(child.score).isEqualTo(pow(2,5));
	}

	
	@Test
	void tableDown() throws Exception {
		int value = 0b1000_0100_0000_0000;
		
		
		assertThat(Node.table_down[value]).isEqualTo(
				 (0b0000_0000_0000_0000 << 48)
				+(0b0000_0000_0000_0000 << 32)
				+(0b0000_0000_0000_1000 << 16)
				+(0b0000_0000_0000_0100 << 0)
				
				);
	}
	
	@Test
	void tableRight() throws Exception {
		int value = 0b1000_0100_0000_0000;
		
		
		assertThat(Node.table_right[value]).isEqualTo(
				 (0b0000_0000_0000_0000 << 48)
				+(0b0000_0000_0000_0000 << 32)
				+(0b0000_0000_0000_0000 << 16)
				+(0b0000_0000_1000_0100 << 0)
				
				);
	}
	
	
	@Test
	public void shouldShiftDown_when_down() throws Exception {
		parent.bits = (0b1000_0000_0000_0000L << 48)
								+(0b0100_0000_0000_0000L << 32)
								+(0b0000_0000_0000_0000L << 16)
								+(0b0000_0000_0000_0000L << 0)
								;
		
		long expected = 
				 (0b0000_0000_0000_0000L << 48)
				+(0b0000_0000_0000_0000L << 32)
				+(0b1000_0000_0000_0000L << 16)
				+(0b0100_0000_0000_0000L << 0)
				;
		
		
		child.down(parent);
		
		assertThat(child.bits).isEqualTo(expected);
	}

	@Test
	public void shouldMergeDown_when_down() throws Exception {
		parent.bits = (0b0000_0000_0000_0000L << 48)
								+(0b0100_0000_0000_0000L << 32)
								+(0b0100_0000_0000_0000L << 16)
								+(0b0001_0000_0000_0000L << 0)
								;
		
		long expected = 
				 (0b0000_0000_0000_0000L << 48)
				+(0b0000_0000_0000_0000L << 32)
				+(0b0101_0000_0000_0000L << 16)
				+(0b0001_0000_0000_0000L << 0)
				;
		
		
		child.down(parent);
		
		assertThat(child.bits).isEqualTo(expected);
		assertThat(child.score).isEqualTo(pow(2,5));
	}

	@Test
	public void shouldDebug() throws Exception {
		parent.bits = (0b0001_0010_0011_0100L << 48)
								+(0b0010_1000_0000_0000L << 32)
								+(0b0011_0000_1100_0000L << 16)
								+(0b0100_0000_0000_1111L << 0)
								;
		
		parent.debug();
		
	}

	private int pow(int i, int j) {
		return (int)Math.pow(i, j);
	}

	@Test
	public void shouldDoubleMerge_when_left() throws Exception {
		parent.bits = 0b0100_0100_0100_0100L;
		child.left(parent);
		assertThat(tbs(child.bits)).isEqualTo(tbs(0b0101_0101_0000_0000L));
	}
	
}
