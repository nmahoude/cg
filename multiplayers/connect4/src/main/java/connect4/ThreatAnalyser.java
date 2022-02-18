package connect4;

public class ThreatAnalyser {
	long myThreatMask[] = new long[5];
	long oppThreatMask[] = new long[5];
	public int[] myThreats = new int[5];
	public int[] oppThreats = new int[5];
	
	public void analyse(long mine, long opp) {
		for (int i=0;i<5;i++) {
			myThreatMask[i] = 0;
			oppThreatMask[i] = 0;
			
			myThreats[i] = 0;
			oppThreats[i] = 0;
		}
		
		for (int i=0;i<Connect4Checker.allConnect4MasksFE;i++) {
			long mask = Connect4Checker.allConnect4Masks[i];
			
			int myBits = Long.bitCount(mine & mask);
			int oppBits = Long.bitCount(opp & mask);
			
			
			if (oppBits == 0) {
				myThreatMask[myBits] |= mask;
			}
			
			if (myBits == 0) {
				oppThreatMask[oppBits] |= mask;
			}
		}
		
		for (int i=0;i<4;i++) {
			myThreatMask[i] = myThreatMask[i] & ~mine;
			oppThreatMask[i] = oppThreatMask[i] & ~opp;
			
			myThreats[i] = Long.bitCount(myThreatMask[i]);
			oppThreats[i] = Long.bitCount(oppThreatMask[i]);
		}
		
		
	}
}
