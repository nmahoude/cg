package bullsandcows;

import java.util.Arrays;

public class Proposition {
	int digits[];
	int bulls;
	int cows;
	
	public Proposition(int[] digits, int bulls, int cows) {
		super();
		this.digits = Arrays.copyOf(digits, 10);
		this.bulls = bulls;
		this.cows = cows;
	}

	public boolean isSameAs(int[] proposition) {
		for (int i=0;i<Player.numberLength;i++) {
			if (this.digits[i] != proposition[i]) return false;
		}
		return true;
	}

	/** 
	 * return true if the digit @ index is compatible with our proposition & result 
	 * @param mask 
	 * @param proposition
	 * @param i
	 * @return
	 */
	public boolean inlinedWith(int[] mask) {
		boolean result = true;
		for (int i=0;i<Player.numberLength;i++) {
			int indexInMask = indexOf(digits[i], mask);
			
			if (indexInMask == -1) continue;
			
			if (i == indexInMask && bulls == 0 ) {
				result = false;
				break;
			}
			if (i != indexInMask && cows == 0) {
				result = false;
				break;
			}
		}
		
		return result;
	}

	private int indexOf(int digit, int[] mask) {
		for (int i=0;i<Player.numberLength;i++) {
			if (mask[i] == digit) return i;
		}
		return -1;
	}
	
	
}
