package fall2020;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class OptimTest {

	@Test
	public void inv() throws Exception {
		int inv[] = new int[4];
		int needed[] = new int[4];
		
		Random random = new Random();
		
		
		int decal0 = 0b10000;
		int decal1 = 0b10000_00000000;
		int decal2 = 0b10000_00000000_00000000;
		int decal3 = 0b10000_00000000_00000000_00000000;

		int NEG_CHECK = 0b10001111000011110000111100001111;
		
		System.out.println(""+decal0);
		System.out.println(""+decal1);
		System.out.println(""+decal2);
		System.out.println(""+decal3);
		
		
		int fullInv = 0;
		int fullNeeded = 0;

		for (int t=0;t<10_000;t++) {
			for (int i=0;i<4;i++) {
				inv[i] = random.nextInt(7);
				needed[i] = random.nextInt(7);
			}
			fullInv = inv[0]*decal0 + inv[1]*decal1 + inv[2]*decal2 + inv[3]*decal3;
			fullNeeded = needed[0]*decal0 + needed[1]*decal1 + needed[2]*decal2 + needed[3]*decal3;

			
			boolean result1 = inv[0]>=needed[0] && inv[1]>=needed[1] && inv[2]>=needed[2] && inv[3]>=needed[3];
			boolean result2 = ((fullInv - fullNeeded) & NEG_CHECK) == 0;

			if (result1 != result2) {
				
				System.out.println(String.format("FULLINV  = %10d ==> %32s",fullInv,Integer.toBinaryString(fullInv)));
				System.out.println(String.format("FULLNEED = %10d ==> %32s",fullNeeded,Integer.toBinaryString(fullNeeded)));
				System.out.println(String.format("I-N      = %10d ==> %32s",fullInv-fullNeeded,Integer.toBinaryString(fullInv-fullNeeded)));
				
				System.out.println(String.format("%32s", Integer.toBinaryString(fullInv-fullNeeded)));
				System.out.println(String.format("%32s", Integer.toBinaryString(NEG_CHECK)));
				
				System.out.println("DIFF ! ");
			}
		}
		
		
		int iterations = 1_000_000_000;
		long start1 = System.currentTimeMillis();
		for (int i=0;i<iterations;i++) {
			boolean result1 = inv[0]>=needed[0] && inv[1]>=needed[1] && inv[2]>=needed[2] && inv[3]>=needed[3];
		}
		System.out.println("T1 = "+(System.currentTimeMillis()-start1));
		
		long start2 = System.currentTimeMillis();
		for (int i=0;i<iterations;i++) {
			boolean result2 = ((fullInv - fullNeeded) & NEG_CHECK) != 0;
		}
		System.out.println("T2 = "+(System.currentTimeMillis()-start2));
		
	}
	
	
}
