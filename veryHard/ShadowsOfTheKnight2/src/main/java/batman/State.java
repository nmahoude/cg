package batman;

public class State {
	public int maxHeight;
	int min;
	int max;
	int oldPos;
	int currentPos;

	
	
	
	public State(int h, int initPos) {
		min = 0;
		max = h-1;
		
		maxHeight = h-1;
		
		currentPos = initPos;
		oldPos = initPos;
	}

	public void apply(BombDir bombDir) {
		int center = (currentPos + oldPos) % 2 == 1 ? 0 : 1;
		int delta2 = Math.abs((int)Math.floor(((currentPos +1 - oldPos) / 2.0)));
		if (delta2 == 0) {
			delta2 = 1;
		}
		
		if (bombDir == BombDir.UNKNOWN) {
		} else if (bombDir == BombDir.WARMER) {
			if (currentPos > oldPos) {
				min = Math.max(min, oldPos + delta2 + center);
				max = max;
			} else {
				max = Math.min(max, oldPos - delta2 - center);
				min = min;
			}
		} else if (bombDir == BombDir.COLDER) {
			if (currentPos > oldPos) {
				max = Math.min(max, currentPos - delta2 - center);
				min = min;
			} else {
				min = Math.max(min, oldPos - delta2 + center);
				max = max;
			}
		} else if (bombDir == BombDir.SAME) {
			min = max = (currentPos + oldPos + 1 ) / 2;
		}
	}
	
	
	public int decide(BombDir bombDir) {
		int newPos;
		if (min == max) {
			newPos = min;
		} else {
			System.err.println(String.format("maxHeight = %d, max = %d, currentPos = %d, min = %d", maxHeight, max, currentPos, min));
			
			newPos = Math.max(0, Math.min(maxHeight, max - (currentPos - min)));
			if (currentPos == newPos) {
				newPos+=1;
			}
		}
		
		
		
		oldPos = currentPos;
		currentPos = newPos;
				
		return newPos;
	}

	public void debug() {
		System.err.println(String.format("Min = %d, Max = %d", min, max));
	}
}
