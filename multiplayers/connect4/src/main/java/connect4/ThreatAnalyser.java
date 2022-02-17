package connect4;

public class ThreatAnalyser {
	public int[] myThreats = new int[5];
	public int[] oppThreats = new int[5];
	
	public void analyse(long mine, long opp) {
		long myThreatMask[] = new long[4];
		long oppThreatMask[] = new long[4];
		
		
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
			long myThreat = myThreatMask[i] & ~mine;
			long oppThreat = oppThreatMask[i] & ~opp;
			
			myThreats[i] = Long.bitCount(myThreat);
			oppThreats[i] = Long.bitCount(oppThreat);
		}
		
		
	}
}
