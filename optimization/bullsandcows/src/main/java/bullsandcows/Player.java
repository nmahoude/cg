package bullsandcows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Player {
	static final Random random = new Random(2);
	
	static final int BULL = 2;
  static final int NOT_HERE = 1;
  static final int UNKNWOWN = 0;
  static final int NOTUSED = -1;
  
	static int positionsPerDigits[][] = new int[10][10];
	static int numberLength;
  
	static int UCT[][][] = new int[10][10][3]; // pour chaque nombre, pour chaque position, (bulls, cows, total)
	
	static int bulls[] = new int[10];
	static {
		for (int i=0;i<bulls.length;i++) {
			bulls[i] = -1;
		}
	}
	
	static List<Proposition> allProposition = new ArrayList<>();
	
	public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    numberLength = in.nextInt();

    positionsPerDigits[0][0] = NOTUSED; // 0 cannot be used on position 0
    
    // game loop
    int proposition[] = new int[10];
    
    while (true) {
        int bulls = in.nextInt();
        int cows = in.nextInt();

        if (bulls != -1) {
        	checkLastProposition(proposition, bulls, cows);
        	allProposition.add(new Proposition(proposition, bulls, cows));
        }
        
        debugPositionPerDigits();
        
        
        random(proposition);
        //uct(proposition);
    }
	}

	private static void debugPositionPerDigits() {
		for (int digits=0;digits<10;digits++) {
			for (int index=0;index<numberLength;index++) {
				if (positionsPerDigits[digits][index] != NOTUSED) {
					System.err.print(" "+digits+" ");
				} else {
					System.err.print(" _ ");
				}
			}
			System.err.println();
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
	  			score = 4.0;
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
		boolean searchAgain;
		do  {
			searchAgain = false;
		  int disponible[] = new int[10];
		  
		  for (int index=0;index<numberLength;index++) {
		  	if (bulls[index] != -1) {
		  		proposition[index] = bulls[index];
		  		disponible[bulls[index]] = -1;
		  	} else {
			  	// check we can find a free number ...
			  	free = false;
			  	for (int j=0;j<10;j++) {
			  		if (disponible[j] == -1) continue;
			  		if (positionsPerDigits[j][index] == NOTUSED) continue;
			  		free = true;
			  		break;
			  	}
			  	if (!free) {
			  		searchAgain = true;
			  		break;
			  	}
			  	int rand;
			  	do {
			  		rand = random.nextInt(10);
			  	}	while (disponible[rand] == -1 || positionsPerDigits[rand][index] == NOTUSED);
			  	
			  	proposition[index] = rand;
			  	disponible[rand] = -1; // not usable anymore
		  	}
		  }
		  
		  if (hasAlreadyBeenDone(proposition)) {
		  	searchAgain = true;
		  }
		  
		  
		} while( searchAgain);
		

		long guess = guess(proposition, numberLength);
		System.out.println(guess); // number with numberLength count of digits
	}

	private static boolean hasAlreadyBeenDone(int[] proposition) {
		for (Proposition p : allProposition) {
			if (p.isSameAs(proposition)) {
				return true;
			}
		}
		return false;
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
		
		// check for unicity in column
		boolean found;
		do {
			found = false;
			for (int index=0;index<numberLength;index++) {
				if (Player.bulls[index] != -1) continue;
				
				int solutions = 0;
				int lastDigit = -1;
				for (int d=0;d<10;d++) {
					if (positionsPerDigits[d][index] != NOTUSED) {
						solutions++;
						lastDigit = d;
					}
				}
				if (solutions == 1) {
					System.err.println("Found a single digit in column "+index);
					setBull(index, lastDigit);
					found = true;
				}
			}
		} while (found);
		
		// check row unicity (only when 10 digits) 
		if (numberLength == 10) {
			do {
				found = false;

				for (int d=0;d<10;d++) {
					if (isAlreadyBull(d)) continue;
					
					int solutions = 0;
					int lastCol = -1;
					for (int index=0;index<numberLength;index++) {
						if (positionsPerDigits[d][index] != NOTUSED) {
							solutions++;
							lastCol = index;
						}
					}
					if (solutions == 1) {
						System.err.println("Found a single digit in row "+d);
						setBull(lastCol, d);
						found = true;
					}
				}
				
			} while (found);
			
		}
		
		
		// now check with past propositions
		int canBeBull[] = new int[10];
		int totalStllCanBeBulls = numberLength;
		if (bulls > 0) {
			for (int index=0;index<numberLength;index++) {
				for (Proposition prop : allProposition) {
					if (prop.digits[index] == proposition[index] && prop.bulls == 0) {
						// can't be this one !
						canBeBull[index] = -1;
						totalStllCanBeBulls--;
						break;
					}
				}
			}
			System.err.println("Proposition history is "+allProposition.size()+" found "+totalStllCanBeBulls+" that can be bull out of "+bulls);
			if (totalStllCanBeBulls == bulls) {
				System.err.println("Setting bulls ! ");
				for (int index=0;index<numberLength;index++) {
					if (canBeBull[index] == 0) {
						setBull(index, proposition[index]);
					}
				}
			}
		}
		
	}

	private static boolean isAlreadyBull(int d) {
		for (int i=0;i<numberLength;i++) {
			if (bulls[i] == d) return true;
		}
		return false;
	}

	// set the digit as bull on index
	private static void setBull(int index, int digit) {
		System.err.println("Found a bull ! digit "+digit +" @index "+index);
		bulls[index] = digit;

		for (int i=0;i<10;i++) {
			if (i == index) continue;
			positionsPerDigits[digit][i] = NOTUSED;
		}
		
		for (int i=0;i<10;i++) {
			if (i == digit) continue;
			positionsPerDigits[i][index] = NOTUSED;
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
