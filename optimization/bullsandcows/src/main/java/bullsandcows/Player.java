package bullsandcows;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
	static final ThreadLocalRandom random = ThreadLocalRandom.current();
	static final int BULL = 2;
  static final int NOT_HERE = 1;
  static final int UNKNWOWN = 0;
  static final int NOTUSED = -1;
  
	static int positionsPerDigits[][] = new int[10][10];
	private static int numberLength;
  
	static int UCT[][][] = new int[10][10][3]; // pour chaque nombre, pour chaque position, (bulls, cows, total)
	
	
	
	static List<String> allPossibleCodes = new ArrayList<>();
	
	public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    numberLength = in.nextInt();

    System.err.println("All possibilities size = "+allPossibleCodes.size());
    
    positionsPerDigits[0][0] = NOTUSED; // 0 cannot be used on position 0
    
    // game loop
    int proposition[] = new int[10];
    
    while (true) {
        int bulls = in.nextInt();
        int cows = in.nextInt();

        if (bulls != -1) {
        	checkLastProposition(proposition, bulls, cows);
        }
        
        
        random(proposition);
        // TODO ? uct(proposition);
    }
	}

	private static void uct(int[] proposition) {
	  int disponible[] = new int[10];

	  for (int index=0;index<numberLength;index++) {
	  	// 1. pick the number disponible with maximum uct
	  	int bestDigit = -1;
	  	double bestScore = Double.NEGATIVE_INFINITY;
	  	for (int d=0;d<9;d++) {
	  		if (disponible[d] == -1) continue;
	  		if (positionsPerDigits[d][index] == NOTUSED) continue;

	  		double score;
	  		if (UCT[d][index][2] == 0) {
	  			score = 2.0;
	  		} else {
	  			score = 1.0 * (2 * UCT[d][index][0] + UCT[d][index][1]) / (UCT[d][index][2]);
	  		}
	  		if (score > bestScore) {
	  			bestScore = score;
	  			bestDigit = d;
	  		}
	  		
	  	}
	  	
	  	proposition[index] = bestDigit;
	  	disponible[bestDigit] = -1;
	  }		

	  long guess = guess(proposition, numberLength);
		System.out.println(guess); // number with numberLength count of digits
	
	}	
	
	
	private static void random(int[] proposition) {
		boolean free = false;
		do  {
		  int disponible[] = new int[10];
		  
		  for (int index=0;index<numberLength;index++) {
		  	// check we can find a free number ...
		  	free = false;
		  	for (int j=0;j<10;j++) {
		  		if (disponible[j] == -1) continue;
		  		if (positionsPerDigits[j][index] == NOTUSED) continue;
		  		free = true;
		  		break;
		  	}
		  	if (!free) {
		  		break;
		  	}
		  	int rand;
		  	do {
		  		rand = random.nextInt(10);
		  	}	while (disponible[rand] == -1 || positionsPerDigits[rand][index] == NOTUSED);
		  	
		  	proposition[index] = rand;
		  	disponible[rand] = -1; // not usable anymore
		  }
		} while( !free);
		

		long guess = guess(proposition, numberLength);
		System.out.println(guess); // number with numberLength count of digits
	}

	private static void checkLastProposition(int[] proposition, int bulls, int cows) {
		if (bulls == -1) return;
		
		
		for (int i=0;i<numberLength;i++) {
			int digit = proposition[i];
			UCT[digit][i][0] += bulls;
			UCT[digit][i][1] += cows;
			UCT[digit][i][2] += 1;
		}
		
		
		if (bulls == 0 && cows == 0) {
			// no digits in the solution !
			for (int i=0;i<numberLength;i++) {
				int notUsed = proposition[i];
				for (int j=0;j<10;j++) {
					positionsPerDigits[notUsed][j] = NOTUSED;
				}
			}
			return;
		}
		
		if (bulls == 0) {
			// no number can be used at the given position
			for (int i=0;i<numberLength;i++) {
				int notUsed = proposition[i];
				positionsPerDigits[notUsed][i] = NOTUSED;
			}
		}
		
		if (cows == 0) {
			// no number can be used in another position
			// but maybe where it is (if bulls > 0)
			for (int i=0;i<numberLength;i++) {
				int notUsed = proposition[i];
				for (int j=0;j<10;j++) {
					if (j == i) continue;
					positionsPerDigits[notUsed][j] = NOTUSED;
				}
			}
		}
		
		
		if (cows+bulls == numberLength) {
			// all digits are used, so no other !
			for (int other=0;other<10;other++) {
				if (inProposition(other, proposition)) continue;

				for (int j=0;j<10;j++) {
					positionsPerDigits[other][j] = NOTUSED;
				}
				
			}
		}
	}

	private static boolean inProposition(int other, int[] proposition) {
		for (int i=0;i<numberLength;i++) {
			if (other == proposition[i]) return true;
		}
		return false;
	}

	private static long guess(int[] proposition, int numberLength) {
		long value = 0L;
		for (int i=0;i<numberLength;i++) {
			value = 10*value + proposition[i];
		}
		return value;
	}
}
