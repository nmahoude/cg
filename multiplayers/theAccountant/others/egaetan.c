import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Shoot enemies before they collect all the incriminating data! The closer you
 * are to an enemy, the more damage you do but don't get too close or you'll get
 * killed.
 * 
 * 
 * 
 * 
 **/
class Player {

    private static final int SEED = 2_002;

	private static final boolean DEBUG = false; 
    
    private static final int NB_PER_GENERATION = 200;
    private static final int NB_ELECTED_PER_GENERATION = 10;
    private static final int NB_MOVES = 256;
    private static final int ENEMY_SIZE = 4;
    private static final int WOLF_X = 0;
    private static final int WOLF_Y = 1;
    private static final int WOLF_SHOOT = 2;
    private static final int WOLF_DEAD = 3;
    private static final int DATA_TAG = 4;
    private static final int _2_000 = 2_000 * 2_000;

    
    private final static int[] deathRate={-9999999, -6999999, -4899999, -3429999, -2400999, -1680699, -1176489, -823542, -576480, -403536, -282475, -197732, -138412, -96889, -67822, -47475, -33232, -23263, -16284, -11398, -7979, -5585, -3909, -2736, -1915, -1341, -938, -657, -459, -321, -225, -157, -110, -77, -54, -37, -26, -18, -12, -9, -6, -4, -3, -2, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    
    public final static void main(String args[]) {
        new Player().start(System.in, System.out);
    }
    

        private static final int RandomCacheSize = 100_000;
        int index = 0;
        final int[] cached = new int[RandomCacheSize];

        private void initRandom(long seed) {
            Random r = new Random(seed);
            for (int i = RandomCacheSize; --i > 0;) {
                int n = r.nextInt();
                cached[i] = n > 0 ? n : -n;
            }
        }

        final boolean nextBoolean() {
            return nextInt() % 2 == 0;
        }

        private final int nextInt() {
            index = (index + 1) % RandomCacheSize;
            return cached[index];
        }

        final int nextInt(int vMax) {
            return nextInt() % vMax;
        }

        
        final int eval(int[] moves, int initEnemyCount, int initDataCount, int[] current, int lastMove, int enemy_tag, int[] enemy_stole_data, int enemiesTotalLifeAtStart) {
        	final int totalDatas = DATA_TAG + (initDataCount << 1) + initEnemyCount * ENEMY_SIZE;
            int[] current2 = new int[totalDatas];
        	for (int turn = 0; turn < NB_MOVES; turn++) {
        		associateEnnemyToData(initDataCount, enemy_tag, initEnemyCount, current);
    			
        		System.arraycopy(current, 0, current2, 0, totalDatas);
    			moveEnnemy(enemy_tag, initEnemyCount, current, enemy_stole_data);
    			
                if (turn >= lastMove) {
                	moves[turn << 1] = -1;
                    moves[(turn << 1) + 1] = -1;
                    lastMove++;
                }
                
				int wolf_x = current[0];
		        int wolf_y = current[1];
		        int mx = moves[turn << 1];
				int my = moves[(turn << 1) + 1];
				
				if (mx != -1) {
					moveWolf(current2, mx, my);

					for (int e = 0; e < initEnemyCount; e++) {
						if (current2[enemy_tag + e * ENEMY_SIZE + 2] <= 0) {
							continue;
						}

						int enemy_x = current2[enemy_tag + e * ENEMY_SIZE];
						int enemy_y = current2[enemy_tag + e * ENEMY_SIZE + 1];

						int distance = (enemy_x - current2[0]) * (enemy_x - current2[0]) + (enemy_y - current2[1]) * (enemy_y - current2[1]);

						if (distance <= _2_000) {
							moves[turn << 1] = -1;
							moves[(turn << 1) + 1] = -1;
					        lastMove = turn;
					        //System.err.println("Start to shoot");
							break;
						}
					}
				}
				checkGameOver(enemy_tag, initEnemyCount, current);
				if (current[WOLF_DEAD] != 0) {
                	//System.err.println("+DEAD");
                	return -1;
                }
				
				mx = moves[turn << 1];
				my = moves[(turn << 1) + 1];
				
				// move wolf
				if (mx != -1) {
					moveWolf(current, mx, my);
					//System.err.println("Move");
				}
				
				// check gameover
				int closest = checkGameOver(enemy_tag, initEnemyCount, current);
				if (current[WOLF_DEAD] != 0) {
					//System.err.println("++DEAD");
					return -1;
				}
				
				// shoot
				if (mx == -1) {
				    int on = wolfShoot(enemy_tag, current, my, closest);
				    moves[(turn << 1) + 1] = on;
				}
				
				// remove stolen datas
				removeStolenDatas(enemy_tag, initEnemyCount, current, enemy_stole_data);

                int nbKilled = 0;
                for (int i = 0; i < initEnemyCount; i++) {
                    nbKilled += current[enemy_tag + i * ENEMY_SIZE + 2] > 0 ? 0 : 1;
                }
                int remainingData = 0;
                for (int i = 0; i < initDataCount; i++) {
                    if (current[DATA_TAG + (i << 1)] != -1) remainingData++;
                }

				if (remainingData == 0 && nbKilled == 0) {
                    return -1;
                }
                if (current[WOLF_DEAD] != 0) {
                	return -1;
                }

                if (nbKilled == initEnemyCount) {
                    return (remainingData * 100 + nbKilled * 10 
                    		+ (remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT])*3 )) 
                            ;
                }
                if (remainingData == 0) {
                    return  nbKilled * 10; 
                }

                if (turn == NB_MOVES) {
                	return 0;
                }
            }
        	return -1;
        }
        final int evalMoves(int[] moves, int initEnemyCount, int initDataCount, int[] current, int lastMove, int enemy_tag, int[] enemy_stole_data, int enemiesTotalLifeAtStart) {
        	for (int turn = 0; turn < NB_MOVES; turn++) {
        		associateEnnemyToData(initDataCount, enemy_tag, initEnemyCount, current);
        		
        		moveEnnemy(enemy_tag, initEnemyCount, current, enemy_stole_data);
        		
        		if (turn >= lastMove) {
        			moves[turn << 1] = -1;
        			moves[(turn << 1) + 1] = -1;
        			lastMove++;
        			System.err.println("Shoot");
        		}
        		
        		int mx = moves[turn << 1];
        		int my = moves[(turn << 1) + 1];
        		
        		// move wolf
        		if (mx != -1) {
        			moveWolf(current, mx, my);
        			System.err.println("Move");
        		}
        		
        		// check gameover
        		int closest = checkGameOver(enemy_tag, initEnemyCount, current);
        		if (current[WOLF_DEAD] != 0) {
        			return -1;
        		}
        		
        		// shoot
        		if (mx == -1) {
        			wolfShoot(enemy_tag, current, my, closest);
        		}
        		
        		// remove stolen datas
        		removeStolenDatas(enemy_tag, initEnemyCount, current, enemy_stole_data);
        		
        		int nbKilled = 0;
        		for (int i = 0; i < initEnemyCount; i++) {
        			nbKilled += current[enemy_tag + i * ENEMY_SIZE + 2] > 0 ? 0 : 1;
        		}
        		int remainingData = 0;
        		for (int i = 0; i < initDataCount; i++) {
        			if (current[DATA_TAG + (i << 1)] != -1) remainingData++;
        		}
        		
        		if (remainingData == 0 && nbKilled == 0) {
        			return -1;
        		}
        		if (current[WOLF_DEAD] != 0) {
        			System.err.println("DEAD");
        			return -1;
        		}
        		
        		if (nbKilled == initEnemyCount) {
        			return (remainingData * 100 + nbKilled * 10 
        					+ (remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT])*3 )) 
        					;
        		}
        		if (remainingData == 0) {
        			return  nbKilled * 10; 
        		}
        		
        		if (turn == NB_MOVES) {
        			return 0;
        		}
        	}
        	return -1;
        }


    final void start(InputStream input, PrintStream output) {
    	long startTime = System.nanoTime();
        Scanner in = new Scanner(input);

        int[] datas = new int[DATA_TAG + 100 * 2 + 100 * ENEMY_SIZE];
        Arrays.fill(datas, -1);
        datas[WOLF_DEAD] = 0;
        datas[WOLF_SHOOT] = 0;


        int x = in.nextInt();
        int y = in.nextInt();

        System.err.println(x + " " + y);
        datas[WOLF_X] = x;
        datas[WOLF_Y] = y;
        int initDataCount = in.nextInt();

        if (DEBUG) System.err.println(initDataCount);
        for (int i = 0; i < initDataCount; i++) {
            int dataId = in.nextInt();
            int dataX = in.nextInt();
            int dataY = in.nextInt();
            datas[DATA_TAG + (dataId << 1)] = dataX;
            datas[DATA_TAG + (dataId << 1) + 1] = dataY;
            if (DEBUG) {
                System.err.println(dataId + " " + dataX + " " + dataY);
            }
        }
        int enemy_tag = DATA_TAG + (initDataCount << 1);
        int initEnemyCount = in.nextInt();

        int enemiesTotalLifeAtStart = 0;
        if (DEBUG) {
            System.err.println(initEnemyCount);
        }

        for (int i = 0; i < initEnemyCount; i++) {
            int enemyId = in.nextInt();
            int enemyX = in.nextInt();
            int enemyY = in.nextInt();
            int enemyLife = in.nextInt();
            int dc = enemy_tag + enemyId * ENEMY_SIZE;
            datas[dc] = enemyX;
            datas[dc + 1] = enemyY;
            datas[dc + 2] = enemyLife;
            enemiesTotalLifeAtStart += enemyLife;
            if (DEBUG) {
                System.err.println(enemyId + " " + enemyX + " " + enemyY + " " + enemyLife);
            }
        }
        

        boolean firstTurn = true;

        int eval = 0;
        int nbSims = 0;
        int[] enemy_stole_data = new int[initEnemyCount];
        int[] enemy_stole_data_origine = new int[initEnemyCount];
        Arrays.fill(enemy_stole_data_origine, -1);

        initRandom(SEED);
        int[] moves = new int[NB_MOVES * 2];
        int turn = 0;
        final int totalDatas = DATA_TAG + (initDataCount << 1) + initEnemyCount * ENEMY_SIZE;
        int[] current = new int[totalDatas];

        int nbKilled = 0;
        int remainingData = 0;

        int[] scoreEvals = new int[NB_ELECTED_PER_GENERATION];
        int[] nbMovesPerEval = new int[NB_ELECTED_PER_GENERATION];
        int minEvalScore = Integer.MIN_VALUE;
        int maxEvalScoreIndex = 0;
        Arrays.fill(scoreEvals, Integer.MIN_VALUE);
        int[] movesEvals = new int[NB_ELECTED_PER_GENERATION * NB_MOVES * 2];
        int[] scoreEvalsSaved = new int[NB_ELECTED_PER_GENERATION];
        int[] nbMovesPerEvalSaved = new int[NB_ELECTED_PER_GENERATION];
        int[] movesEvalsSaved = new int[NB_ELECTED_PER_GENERATION * NB_MOVES * 2];
        Arrays.fill(movesEvalsSaved, -1);
        int[] bestMove = new int[NB_MOVES * 2];
        int bestMoveScore = Integer.MIN_VALUE;
        
        associateEnnemyToData(initDataCount, enemy_tag, initEnemyCount, datas);
        
        //  XXX
        if (initDataCount < 3)
        {
        	int bx = 0;
        	int by = 0;
        	for (int i = 0; i < initDataCount; i++) {
        		bx += datas[DATA_TAG + (i << 1) + 1];
        		by += datas[DATA_TAG + (i << 1) + 1];
        	}
        	bx = bx / initDataCount;
        	by = by / initDataCount;

        	int delta_x = bx - datas[WOLF_X];
        	int delta_y = by - datas[WOLF_Y];
        	double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

        	int nbMoveToGo = (int) (distance / 1000);
        	Arrays.fill(moves, -1);
        	for (int j = 0; j < nbMoveToGo - 1; j++) {
        		moves[j<<1] = bx;
        		moves[(j<<1)+1] = by;
        		
        	}
        	System.arraycopy(datas, 0, current, 0, totalDatas);

        	int e = eval(moves, initEnemyCount, initDataCount, current, nbMoveToGo -1, enemy_tag, enemy_stole_data, enemiesTotalLifeAtStart);
        	System.err.println("Save the world :" + e);
        	if (e > 0) {
        		bestMoveScore = e;
        		bestMove[0] =bx;
        		bestMove[1] =by;
        		System.arraycopy(moves, 0, bestMove, 2, NB_MOVES * 2 - 2);
        	}
        }
		
        prefill(datas, initDataCount, enemy_tag, initEnemyCount, nbMovesPerEval, movesEvals);
        long timeLimit;
        
        // game loop
        while (true) {
            minEvalScore = Integer.MIN_VALUE;
            maxEvalScoreIndex = 0;
            nbSims = 0;
            // XXX Timelimite
            if (!firstTurn) {
            	startTime = System.nanoTime();
            	timeLimit = 98 * 1_000_000 + startTime; // 96
                read(in, datas, enemy_tag);
            }
            else {
                firstTurn = false;
                timeLimit = 990 * 1_000_000 + startTime; // 970
            }

            int bx = -1;
            int by = -1;

            for (int i = 0; i< NB_ELECTED_PER_GENERATION; i++) {
                nbMovesPerEval[i]= nbMovesPerEvalSaved[i] - 1;
                System.arraycopy(movesEvalsSaved,i * NB_MOVES * 2 + 2, movesEvals, i * NB_MOVES * 2, NB_MOVES * 2 - 2);
            }
            System.arraycopy(bestMove, 2, bestMove, 0, NB_MOVES * 2 - 2);
            
            int currentGeneration = 0;
            
            
			associateEnnemyToData(initDataCount, enemy_tag, initEnemyCount, datas);
			
			moveEnnemy(enemy_tag, initEnemyCount, datas, enemy_stole_data_origine);
			
            
            while (System.nanoTime() < timeLimit) {
                currentGeneration ++;
                
                System.arraycopy(movesEvalsSaved, 0, movesEvals, 0, NB_ELECTED_PER_GENERATION * NB_MOVES * 2);
                System.arraycopy(nbMovesPerEvalSaved, 0, nbMovesPerEval, 0, NB_ELECTED_PER_GENERATION);
                Arrays.fill(scoreEvalsSaved, Integer.MIN_VALUE);
                minEvalScore = Integer.MIN_VALUE;
                
                for (int inGen = 0; inGen < NB_PER_GENERATION && System.nanoTime() < timeLimit; inGen ++ ) {

                    nbSims++;
                    System.arraycopy(datas, 0, current, 0, totalDatas);
                    System.arraycopy(enemy_stole_data_origine, 0, enemy_stole_data, 0, initEnemyCount);
                    
                    eval = 0;

                    // XXX Mutation

                    int selected = nextInt(NB_ELECTED_PER_GENERATION);
                    int lastMove = nbMovesPerEval[selected];

                    if (nextInt(5)<1) {
                        cross(moves, nbMovesPerEval, movesEvals, selected);
                    }
                    else {
                        System.arraycopy(movesEvals, selected * NB_MOVES * 2, moves, 0, NB_MOVES * 2);
                    }
                        

                    if (lastMove > 0) {
                        lastMove = mutate(initDataCount, initEnemyCount, moves, current, currentGeneration, lastMove);
                    }

                    for (turn = 0; turn < NB_MOVES; turn++) {

                        if (turn >= lastMove) {
                            generateMove(initEnemyCount, moves, turn, initDataCount, current);
                            lastMove++;
                        }
						int mx = moves[turn << 1];
						int my = moves[(turn << 1) + 1];
						
						// move wolf
						if (mx != -1) {
							moveWolf(current, mx, my);
						}
						
						// check gameover
						int closest = checkGameOver(enemy_tag, initEnemyCount, current);
						
						// shoot
						if (mx == -1) {
						    wolfShoot(enemy_tag, current, my, closest);
						}
						
						// remove stolen datas
						removeStolenDatas(enemy_tag, initEnemyCount, current, enemy_stole_data);

                        // TODO eval
                        nbKilled = 0;
                        for (int i = 0; i < initEnemyCount; i++) {
                            nbKilled += current[enemy_tag + i * ENEMY_SIZE + 2] > 0 ? 0 : 1;
                        }
                        remainingData = 0;
                        for (int i = 0; i < initDataCount; i++) {
                            if (current[DATA_TAG + (i << 1)] != -1) remainingData++;
                        }

                        if (remainingData == 0 && nbKilled == 0) {
                            eval = deathRate[turn + 1];
                            break;
                        }
                        if (current[WOLF_DEAD] != 0) {
                            eval =  remainingData * 100 + nbKilled * 10;
                            break;
                        }

                        if (nbKilled == initEnemyCount) {
                        	eval = 10_000 * (remainingData * 100 + nbKilled * 10 + (remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT])*3 )) 
    						+100 - turn
    						;
                            int scoreExact = (remainingData * 100 + nbKilled * 10 + (remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT])*3 )) 
                                    ;
                            if (scoreExact > bestMoveScore) {
                                bestMoveScore = scoreExact;
                                System.arraycopy(moves, 0, bestMove, 0, NB_MOVES * 2);
                            }
                            break;
                        }
                        if (remainingData == 0) {
                        	eval = 1_000 * (remainingData * 100 + nbKilled * 10 + (remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT])*3 )) 
                        			+100 - turn
                        			;
                            int scoreExact = nbKilled * 10; 
                            if (scoreExact > bestMoveScore) {
                                bestMoveScore = scoreExact;
                                System.arraycopy(moves, 0, bestMove, 0, NB_MOVES * 2);
                            }
                            break;
                        }

                        if (turn == NB_MOVES) {
                            eval += remainingData * 100 + nbKilled * 10 + remainingData * Math.max(0, enemiesTotalLifeAtStart - 3 * current[WOLF_SHOOT]);
                            break;
                        }
                        else {
                			associateEnnemyToData(initDataCount, enemy_tag, initEnemyCount, current);
                			
                			moveEnnemy(enemy_tag, initEnemyCount, current, enemy_stole_data);
                			
                        }

                    }

                    if (eval > minEvalScore) {
                        int indexToInsert = 0;
                        int nextMin = eval;
                        for (int i = 0; i < NB_ELECTED_PER_GENERATION; i++) {
                            if (scoreEvalsSaved[i] == minEvalScore) {
                                indexToInsert = i;
                            }
                            else if (scoreEvalsSaved[i] < nextMin) {
                                nextMin = scoreEvalsSaved[i];
                            }
                        }
                        minEvalScore = nextMin;
                        scoreEvalsSaved[indexToInsert] = eval;
                        nbMovesPerEvalSaved[indexToInsert] = turn;
                        System.arraycopy(moves, 0, movesEvalsSaved, indexToInsert * NB_MOVES * 2, NB_MOVES * 2);
                        if (eval > scoreEvalsSaved[maxEvalScoreIndex]) {
                            maxEvalScoreIndex = indexToInsert;
                        }
                    }
                }
            }

            bx = bestMove[0];
            by = bestMove[1];
			
			// move wolf
			if (bx != -1) {
				moveWolf(datas, bx, by);
			}
			
			// check gameover
			int closest = checkGameOver(enemy_tag, initEnemyCount, datas);
			
			// shoot
			int realShoot = closest;
			if (bx == -1) {
			    realShoot = wolfShoot(enemy_tag, datas, by, closest);
			}
			
			// remove killed ennemies
			
			// remove stolen datas
			removeStolenDatas(enemy_tag, initEnemyCount, datas, enemy_stole_data_origine);
            

            if (datas[WOLF_DEAD] > 0) {
            	System.err.println("OOOPS");
            }
            
            output.println((bx == -1 ? ("SHOOT " + realShoot + " " + nbSims) : ("MOVE " + bx + " " + by + " " + nbSims)) + "/" + currentGeneration);
        }
    }

	private void cross(int[] moves, int[] nbMovesPerEval, int[] movesEvals, int selected) {
		int selected2 = nextInt(NB_ELECTED_PER_GENERATION);
		if (selected == selected2) {
		    System.arraycopy(movesEvals,selected* NB_MOVES * 2, moves, 0, NB_MOVES * 2);
		}
		else {
		    System.arraycopy(movesEvals, selected* NB_MOVES * 2, moves, 0, NB_MOVES * 2);
		    int min = Math.min(nbMovesPerEval[selected], nbMovesPerEval[selected2]);
		    if (min>0) {
		        int joinPOint = nextInt(min);
		        System.arraycopy(movesEvals, selected2 * NB_MOVES * 2 + joinPOint*2, moves, 0, (NB_MOVES - joinPOint)* 2);
		    }
		}
	}

	private int mutate(int initDataCount, int initEnemyCount, int[] moves, int[] current, int currentGeneration,
			int lastMove) {
		for (int i = 0; i < lastMove -1; i++) {
		    int r = nextInt(70 + 2*currentGeneration);
		    if (r < 10) { // change current
		        generateMove(initEnemyCount, moves, i, initDataCount, current);
		    }
		    else if (r < 20) { // duplicate current
		        System.arraycopy(moves, i*2, moves, i*2 + 2, (NB_MOVES -i -1)* 2);
		        lastMove++;
		    }
		    else if (r < 30) { // suppress current
		        System.arraycopy(moves, i*2 + 2, moves, i*2, (NB_MOVES -i  -1)* 2);
		        lastMove --;
		    }
		    else if (r < 40) { // reverse
		        int tx = moves[i * 2];
		        int ty = moves[i * 2 + 1];
		        moves[i*2] = moves[i*2 + 2];
		        moves[i*2 + 1] = moves[i*2 + 2 + 1];
		        moves[i*2 + 2] = tx;
		        moves[i*2 + 2 + 1] = ty;
		    }
		    else if (r < 50) { // insert
		        System.arraycopy(moves, i*2, moves, i*2 + 2, (NB_MOVES -i   -1)* 2);
		        generateMove(initEnemyCount, moves, i, initDataCount, current);
		        lastMove++;
		    }
		    else if (r < 60) {
		    	if (moves[i*2] > -1) { // tweak move
		    		int change = 1 + 1_000 / currentGeneration;
		    		moves[i*2] += nextInt(change)-change/2;
		    		moves[i*2 + 1] += nextInt(change)-change/2;

		    		if (moves[i*2]<0) moves[i*2] = 0;
		    		if (moves[i*2]>15_999) moves[i*2] = 15_999;
		    		if (moves[i*2 + 1]<0) moves[i*2 + 1] = 0;
		    		if (moves[i*2 + 1]>8_999) moves[i*2 + 1] = 8_999;
		    	}
		    	else {
		    		moves[i*2 + 1] = nextInt(initEnemyCount);
		    	}
		    }
		}
		return lastMove;
	}

    private void prefill(int[] datas, int initDataCount, int enemy_tag, int initEnemyCount, int[] nbMovesPerEval,
    		int[] movesEvals) {
    	int initMove = 0;
    	
    	if (initDataCount < 5)
    	{
    		int bx = 0;
    		int by = 0;
    		for (int i = 0; i < initDataCount; i++) {
    			bx += datas[DATA_TAG + (i << 1) + 1];
    			by += datas[DATA_TAG + (i << 1) + 1];
    		}
    		bx = bx / initDataCount;
    		by = by / initDataCount;

    		int delta_x = bx - datas[WOLF_X];
    	    int delta_y = by - datas[WOLF_Y];
    		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

    		nbMovesPerEval[initMove] = (int) (distance / 1000);
    		
    		movesEvals[initMove * NB_MOVES * 2 + 0] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 1] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 2] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 3] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 4] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 5] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 6] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 7] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 8] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 9] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 10] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 11] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 12] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 13] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 14] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 15] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 16] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 17] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 18] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 19] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 20] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 21] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 22] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 23] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 24] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 25] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 26] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 27] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 28] = bx;
    		movesEvals[initMove * NB_MOVES * 2 + 29] = by;
    		movesEvals[initMove * NB_MOVES * 2 + 30] = -1;
    		movesEvals[initMove * NB_MOVES * 2 + 31] = -1;
    		movesEvals[initMove * NB_MOVES * 2 + 32] = -1;
    		movesEvals[initMove * NB_MOVES * 2 + 33] = -1;
    		movesEvals[initMove * NB_MOVES * 2 + 34] = -1;
    		movesEvals[initMove * NB_MOVES * 2 + 35] = -1;
    		initMove ++;
    	}

    	if (true) {
    		for (int i = 0; i < initDataCount && initMove < NB_ELECTED_PER_GENERATION; i ++) {

    			int mx = datas[DATA_TAG + (initMove << 1)];
    			int my = datas[DATA_TAG + (initMove << 1) + 1];
    			
    			int delta_x = mx - datas[WOLF_X];
        	    int delta_y = my - datas[WOLF_Y];
        		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

        		nbMovesPerEval[initMove] = (int) (distance / 1000);
    			
    			movesEvals[initMove * NB_MOVES * 2 + 0] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 1] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 2] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 3] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 4] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 5] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 6] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 7] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 8] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 9] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 10] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 11] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 12] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 13] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 14] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 15] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 16] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 17] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 18] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 19] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 20] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 21] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 22] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 23] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 24] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 25] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 26] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 27] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 28] = mx;
    			movesEvals[initMove * NB_MOVES * 2 + 29] = my;
    			movesEvals[initMove * NB_MOVES * 2 + 30] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 31] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 32] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 33] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 34] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 35] = -1;
    			initMove ++;
    		}
    	}
    	if (true) { //initEnemyCount < 100
    		for (int i = 0; i < initEnemyCount && initMove < NB_ELECTED_PER_GENERATION; i ++) {
    			nbMovesPerEval[initMove] = 7;
    			int dc = enemy_tag + i * ENEMY_SIZE;
    			movesEvals[initMove * NB_MOVES * 2 + 0] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 1] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 2] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 3] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 4] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 5] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 6] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 7] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 8] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 9] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 10] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 11] = datas[dc +1];
    			movesEvals[initMove * NB_MOVES * 2 + 12] = datas[dc];
    			movesEvals[initMove * NB_MOVES * 2 + 13] = datas[dc +1];
    			initMove ++;
    		}
    		for (int i = 0; i < initEnemyCount && initMove < NB_ELECTED_PER_GENERATION; i ++) {
    			if (initMove >= NB_ELECTED_PER_GENERATION) {
    				break;
    			}
    			nbMovesPerEval[initMove] = 4;
    			movesEvals[initMove * NB_MOVES * 2 + 0] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 1] = i;
    			movesEvals[initMove * NB_MOVES * 2 + 2] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 3] = i;
    			movesEvals[initMove * NB_MOVES * 2 + 4] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 5] = i;
    			movesEvals[initMove * NB_MOVES * 2 + 6] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 7] = i;
    			movesEvals[initMove * NB_MOVES * 2 + 8] = -1;
    			movesEvals[initMove * NB_MOVES * 2 + 9] = i;
    			initMove ++;
    		}
    	}
    }

    public final int closestEnemy(int enemy_tag, int initEnemyCount, int[] current) {
        int by = -1;

        int minDist = Integer.MAX_VALUE;
        int wolf_x = current[0];
        int wolf_y = current[1];
        for (int e = 0; e < initEnemyCount; e++) {
            if (current[enemy_tag + e * ENEMY_SIZE + 2] <= 0) {
                continue;
            }

            int enemy_x = current[enemy_tag + e * ENEMY_SIZE];
            int enemy_y = current[enemy_tag + e * ENEMY_SIZE + 1];

            int distance = (enemy_x - wolf_x) * (enemy_x - wolf_x) + (enemy_y - wolf_y) * (enemy_y - wolf_y);

            if (distance < minDist) {
                minDist = distance;
                by = e;
                break;
            }
        }
        return by;
    }

    public final void read(Scanner in, int[] datas, int enemy_tag) {
        int wolfx = in.nextInt();
        int wolfy = in.nextInt();

        datas[WOLF_X] = wolfx;
        datas[WOLF_Y] = wolfy;

        int dataCount = in.nextInt();
        if (DEBUG) System.err.println(dataCount);

        for (int i = 0; i < dataCount; i++) {
            int dataId = in.nextInt();
            int dataX = in.nextInt();
            int dataY = in.nextInt();
            datas[DATA_TAG + (dataId << 1)] = dataX;
            datas[DATA_TAG + (dataId << 1) + 1] = dataY;
            if (DEBUG) System.err.println(dataId + " " + dataX + " " + dataY);
        }

        int enemyCount = in.nextInt();
        if (DEBUG) System.err.println(enemyCount);

        for (int i = 0; i < enemyCount; i++) {
            int enemyId = in.nextInt();
            int enemyX = in.nextInt();
            int enemyY = in.nextInt();
            int enemyLife = in.nextInt();
            int dc = enemy_tag + enemyId * ENEMY_SIZE;
            datas[dc] = enemyX;
            datas[dc + 1] = enemyY;
            datas[dc + 2] = enemyLife;
            if (DEBUG) System.err.println(enemyId + " " + enemyX + " " + enemyY + " " + enemyLife);
        }
    }

    public final void dump(int[] datas, int totalDatas) {
        String tes = "";
        for (int i = 0; i < totalDatas; i++) {
            tes += datas[i] + " ";
        }
        System.err.println(tes);
    }

    private void generateMove(int enemyCount, int[] moves, int position, int dataCount, int[] current) {
        boolean shouldShoot = nextInt(9) > 0;
        if (shouldShoot) {
            moves[position << 1] = -1;
            moves[(position << 1) + 1] = nextInt(enemyCount);
        }
        else {
        	boolean shouldRandomMove = nextInt(2)<1;
            if (shouldRandomMove) {
                moves[position << 1] = nextInt(16_000);
                moves[(position << 1) + 1] = nextInt(9_000);
            }
            else {
                int[] mapping = new int[dataCount];
                int nbDatas = 0;
                for (int d = 0; d < dataCount; d++) {
                    int data_x = current[DATA_TAG + (d << 1)];
                    if (data_x < 0) {
                        continue;
                    }
                    mapping[nbDatas] = d;
                    nbDatas ++;
                }
                int cible = nextInt(nbDatas);
                moves[position << 1] = current[DATA_TAG + (mapping[cible] << 1)];
                moves[(position << 1) + 1] = current[DATA_TAG + (mapping[cible] << 1) + 1];
                
            }
        }
    }

    public final void moveWolf(int[] current, int wx, int wy) {

        int delta_x = wx - current[WOLF_X];
        int delta_y = wy - current[WOLF_Y];
        double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

        if (distance > 1000) {
            double move_x = (1000. * (delta_x) / distance);
            double move_y = (1000. * (delta_y) / distance);

            current[WOLF_X] += move_x;
            current[WOLF_Y] += move_y;
        }
        else {
            current[WOLF_X] = wx;
            current[WOLF_Y] = wy;
        }
        if (current[WOLF_X]<0) current[WOLF_X] = 0;
        else if (current[WOLF_X]>15_999) current[WOLF_X] = 15_999;
        if (current[WOLF_Y]<0) current[WOLF_Y] = 0;
        else if (current[WOLF_Y]>8_999) current[WOLF_Y] = 8_999;
    }

    public final void removeStolenDatas(int enemy_tag, int enemyCount, int[] current, int[] enemy_stole_data) {
        for (int e = 0; e < enemyCount; e++) {
            if (enemy_stole_data[e] != -1 && current[enemy_tag + e * ENEMY_SIZE + 2] > 0) {
                current[DATA_TAG + (enemy_stole_data[e] << 1)] = -1;
                current[DATA_TAG + (enemy_stole_data[e] << 1) + 1] = -1;
            }
            enemy_stole_data[e] = -1;
        }
    }

    public final int wolfShoot(int enemey_tag, int[] current, int e, int closest) {
        int wolf_x = current[WOLF_X];
        int wolf_y = current[WOLF_Y];
        int cible = e >= 0 ? e : closest;
        current[WOLF_SHOOT]++;

        if (current[enemey_tag + cible * ENEMY_SIZE + 2] <= 0) {
            cible = closest;
        }
        int enemy_x = current[enemey_tag + cible * ENEMY_SIZE];
        int enemy_y = current[enemey_tag + cible * ENEMY_SIZE + 1];

        int dx = enemy_x - wolf_x;
        int dy = enemy_y - wolf_y;
        int amount = degats(dx * dx + dy * dy);

        current[enemey_tag + cible * ENEMY_SIZE + 2] = (int) Math.round((double) (current[enemey_tag + cible * ENEMY_SIZE + 2] - amount));
        return cible;
    }

    public final int checkGameOver(int enemey_tag, int enemyCount, int[] current) {
        int wolf_x = current[0];
        int wolf_y = current[1];
        int minDist = Integer.MAX_VALUE;
        int closest = 0;
        for (int e = 0; e < enemyCount; e++) {
            if (current[enemey_tag + e * ENEMY_SIZE + 2] <= 0) {
                continue;
            }

            int enemy_x = current[enemey_tag + e * ENEMY_SIZE];
            int enemy_y = current[enemey_tag + e * ENEMY_SIZE + 1];

            int distance = (enemy_x - wolf_x) * (enemy_x - wolf_x) + (enemy_y - wolf_y) * (enemy_y - wolf_y);

            if (minDist > distance) {
                closest = e;
                minDist = distance;
            }
            if (distance <= _2_000) {
                current[WOLF_DEAD] = 1;
                break;
            }
        }
        return closest;
    }

    public final void moveEnnemy(int enemey_tag, int enemyCount, int[] current, int[] enemy_stole_data) {
        for (int e = 0; e < enemyCount; e++) {
            int enemy_x = current[enemey_tag + e * ENEMY_SIZE];
            int enemy_y = current[enemey_tag + e * ENEMY_SIZE + 1];

            if (current[enemey_tag + e * ENEMY_SIZE + 2] <= 0) {
                continue;
            }
            int data_x = current[DATA_TAG + (current[enemey_tag + e * ENEMY_SIZE + 3] << 1)];
            int data_y = current[DATA_TAG + 1 + (current[enemey_tag + e * ENEMY_SIZE + 3] << 1)];

            int delta_x = data_x - enemy_x;
            int delta_y = data_y - enemy_y;
            double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);

            if (distance > 500) {
                double move_x = (500. * (delta_x) / distance);
                double move_y = (500. * (delta_y) / distance);

                current[enemey_tag + e * ENEMY_SIZE] += move_x;
                current[enemey_tag + e * ENEMY_SIZE + 1] += move_y;
                if (current[enemey_tag + e * ENEMY_SIZE] == data_x && current[enemey_tag + e * ENEMY_SIZE + 1] == data_y) {
                    enemy_stole_data[e] = current[enemey_tag + e * ENEMY_SIZE + 3];
                }
            }
            else {
                current[enemey_tag + e * ENEMY_SIZE] = data_x;
                current[enemey_tag + e * ENEMY_SIZE + 1] = data_y;
                enemy_stole_data[e] = current[enemey_tag + e * ENEMY_SIZE + 3];
            }
        }
    }



    
    public final void associateEnnemyToData(int dataCount, int enemey_tag, int enemyCount, int[] current) {
        int minDist;
        for (int e = 0; e < enemyCount; e++) {
            int enemy_data = current[enemey_tag + e * ENEMY_SIZE + 3];
            if (enemy_data != -1 && current[DATA_TAG + (enemy_data << 1)] >= 0) {
                continue;
            }
            minDist = Integer.MAX_VALUE;
            int enemy_x = current[enemey_tag + e * ENEMY_SIZE];
            int enemy_y = current[enemey_tag + e * ENEMY_SIZE + 1];
            for (int d = 0; d < dataCount; d++) {
                int data_x = current[DATA_TAG + (d << 1)];
                if (data_x < 0) {
                    continue;
                }

                int data_y = current[DATA_TAG + 1 + (d << 1)];

                int deltaX = enemy_x - data_x;
                int deltaY = enemy_y - data_y;
                int distance = deltaX * deltaX + deltaY * deltaY;
                if (distance < minDist) {
                    minDist = distance;
                    current[enemey_tag + e * ENEMY_SIZE + 3] = d;
                    if (distance == 0)
                    	break;
                }
            }
        }
    }

    public final int degats(int dist) {
        int degat = 0;
        if (dist < 4082822) degat = 14;
        else if (dist < 4641589) degat = 13;
        else if (dist < 5333599) degat = 12;
        else if (dist < 6206802) degat = 11;
        else if (dist < 7333490) degat = 10;
        else if (dist < 8827109) degat = 9;
        else if (dist < 10874633) degat = 8;
        else if (dist < 13803666) degat = 7;
        else if (dist < 18235271) degat = 6;
        else if (dist < 25477834) degat = 5;
        else if (dist < 38732003) degat = 4;
        else if (dist < 67860441) degat = 3;
        else if (dist < 158988090) degat = 2;
        else degat = 1;
        return degat;

    }
}
