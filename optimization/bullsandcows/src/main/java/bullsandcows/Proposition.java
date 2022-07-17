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
	
	
}
