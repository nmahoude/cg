
import java.util.PriorityQueue;
import java.util.Scanner;

class Player {

	private static final boolean DEBUG = true;
	private static int nbFloors;
	private static int width;
	private static int nbRounds;
	private static int exitFloor;
	private static int exitPos;
	private static int nbTotalClones;
	private static int nbAdditionalElevators;

	static enum Direction {
		RIGHT, LEFT, NONE
	}

	static enum Action {
		ELEVATOR, BLOCK, WAIT
	}

	static int floors[][];
	static int realFloors[][];
	static int blocksPerLine[];

	static Node bestSoFar[][][];
	private static boolean finishedAStar;

	static class AStarNode {
		public int round;
		public int cameFrom;
	}
	
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		nbFloors = in.nextInt();
		width = in.nextInt();
		nbRounds = in.nextInt();
		exitFloor = in.nextInt();
		exitPos = in.nextInt();
		nbTotalClones = in.nextInt();
		nbAdditionalElevators = in.nextInt();

		
		
		AStarNode aStar[] = new AStarNode[nbFloors*width*nbTotalClones*nbAdditionalElevators*2];
		System.err.println("Length is "+aStar.length);
		for (int i=0;i<aStar.length;i++) {
			aStar[i] = new AStarNode();
		}
		
		
		floors = new int[nbFloors][width];
		realFloors = new int[nbFloors][width];
		blocksPerLine = new int[nbFloors];
		bestSoFar = new Node[nbFloors][width][2];

		System.err.println("nbClones = " + nbTotalClones);
		System.err.println("NbRounds = " + nbRounds);
		System.err.println("NbElevator = " + nbAdditionalElevators);

		int nbElevators = in.nextInt(); // number of elevators
		for (int i = 0; i < nbElevators; i++) {
			int elevatorFloor = in.nextInt(); // floor on which this elevator is found
			int elevatorPos = in.nextInt(); // position of the elevator on its floor
			floors[elevatorFloor][elevatorPos] = 'E';
			realFloors[elevatorFloor][elevatorPos] = 'E';
		}

		
		// game loop
		int turn = 0;
		while (true) {
			turn++;

			int cloneFloor = in.nextInt(); // floor of the leading clone
			int clonePos = in.nextInt(); // position of the leading clone on its floor
			String direction = in.next(); // direction of the leading clone: LEFT or RIGHT

			if (cloneFloor == -1) {
				System.out.println("WAIT");
				continue; // WAIT for next clone
			}

			System.err.println("Current floor=" + cloneFloor + " pos=" + clonePos + " direction=" + direction);

			if (!finishedAStar) {
				start = System.currentTimeMillis();
				if (turn == 1) {
					Node node = new Node();
					node.rounds = 0;
					node.direction = Direction.valueOf(direction);
					node.pos = clonePos;
					node.floor = cloneFloor;
					node.cloneLeft = nbTotalClones;
					node.additionalElevators = nbAdditionalElevators;
					nodes.add(node);
				}
				
				Node result = findAStar();
				
				if (result == null) {
					System.err.println("Pas de soluton trouvée :(");
				} else {
					System.err.println("Une solution apparemment");
					
					Node current = result;
					while (current != null) {
						if (current.action == Action.ELEVATOR) {
							System.err.println("Setting elevator @ "+current);
							floors[current.floor-1][current.pos] = 'E';
						}
						if (current.action == Action.BLOCK) {
							System.err.println("Setting block @ "+current);
							floors[current.floor][current.pos] = 'B';
						}
						
						current = current.parent;
					}
				}
			}

			if (!finishedAStar) {
				System.out.println("WAIT");
			} else {
				System.err.println("Current is "+cloneFloor+"/"+clonePos);
				System.err.println("Floors = "+ floors[cloneFloor][clonePos]);
				if (floors[cloneFloor][clonePos] == 'E' && realFloors[cloneFloor][clonePos] == 0) {
					realFloors[cloneFloor][clonePos] = 'E';
					System.out.println("ELEVATOR");
				} else if (floors[cloneFloor][clonePos] == 'B' && realFloors[cloneFloor][clonePos] == 0) {
					realFloors[cloneFloor][clonePos] = 'B';
					System.out.println("BLOCK");
				} else {
					System.out.println("WAIT");
				}
			}
		}
	}


	static PriorityQueue<Node> nodes = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.estimatedTotalRounds(), n2.estimatedTotalRounds()));
	private static long start;
	public static Node findAStar() {
		System.err.println("Astar turn");

		while (!nodes.isEmpty()) {
			if (System.currentTimeMillis() - start > 90) {
				return null;
			}
			
			
			Node node = nodes.poll();

			if (node.rounds > nbRounds || node.floor > exitFloor) {
				continue;
			}
			
			if (node.isFinal()) {
				finishedAStar = true;
				return node;
			}
			
			
			Node best = bestSoFar[node.floor][node.pos][node.direction == Direction.RIGHT ? 0 : 1];
			if (best == node) {
				
			} else {
				if (best != null) {
					if ( best.rounds <= node.rounds 
						&& best.cloneLeft >= node.cloneLeft
						&& best.additionalElevators >= node.additionalElevators
						) {
						continue; // won't do better
					}
					
					if (best.rounds >= node.rounds 
							&& best.cloneLeft <= node.cloneLeft
							&& best.additionalElevators <= node.additionalElevators
							) {
						bestSoFar[node.floor][node.pos][node.direction == Direction.RIGHT ? 0 : 1] = node;
					}
				} else {
					bestSoFar[node.floor][node.pos][node.direction == Direction.RIGHT ? 0 : 1] = node;
				}
			}
				
			if (DEBUG && node.action != Action.WAIT && node.floor >= 10) System.err.println("Node "+node);
			
			if (floors[node.floor][node.pos] == 'E') {
				nodes.add(node.up());
			} else {
				if (node.action != Action.BLOCK && node.additionalElevators > 0) {
					nodes.add(node.elevator());
					
				}
				if (node.cloneLeft > 0) {
					nodes.add(node.block());
				}
				
				Node next = node.next();
				if (next != null) {
					nodes.add(next);
				}
			}			
		}

		System.err.println("No exit");
		finishedAStar = true;
		return null;
	}
		
	
	private static boolean isFinished(Node currentNode) {
		if (currentNode.floor != exitFloor)
			return false;

		System.err.println("Get on last floor !");
		int distanceToExit = Math.abs(currentNode.pos - exitPos) + currentNode.rounds;
		if (distanceToExit > nbRounds)
			return false;

		if (currentNode.direction == Direction.RIGHT) {
			if (currentNode.pos <= exitPos)
				return true;
		} else if (currentNode.direction == Direction.LEFT) {
			if (currentNode.pos >= exitPos)
				return true;
		}
		return false;
	}

	public static class Node {
		Node parent;
		public int additionalElevators;
		public int cloneLeft;
		int floor;
		int pos;
		int rounds;
		Direction direction;
		private Player.Action action;
		
		public boolean isFinal() {
			return pos == exitPos && floor == exitFloor;
		}
		

		public int currentRounds() {
			return rounds;
		}
		
		public int estimatedTotalRounds() {
			return rounds + remaingingRounds();
		}

		public Player.Node next() {
			Node node = new Node();
			node.action = Action.WAIT;
			node.parent = this;
			node.cloneLeft = this.cloneLeft;
			node.floor = this.floor;
			node.direction = this.direction;
			node.rounds = this.rounds + 1;
			node.additionalElevators = this.additionalElevators;

			if (direction == Direction.RIGHT && pos < width-1) {
				node.pos = this.pos + 1;
			} else if (direction == Direction.LEFT && pos > 0) {
				node.pos = this.pos - 1;
			} else {
				return null;
			}
			return node;
		}

		public int remaingingRounds() {
			if (floor > exitFloor) return Integer.MAX_VALUE;
			return Math.abs(floor-exitFloor) + Math.abs(pos-exitPos);
		}

		public Node elevator() {
			return elevator(pos);
		}		
		
			public Node elevator(int pos) {
			Node node = new Node();
			node.action = Action.ELEVATOR;
			node.parent = this;
			node.cloneLeft = this.cloneLeft - 1;
			node.floor = this.floor + 1;
			node.pos = pos;
			node.direction = this.direction;
			node.rounds = this.rounds + 3  + Math.abs(this.pos - pos);
			node.additionalElevators = this.additionalElevators - 1;
			return node;
		}

		public Player.Node up() {
			return up(pos);
		}
		
		public Player.Node up(int pos) {
			Node node = new Node();
			node.action = Action.WAIT;
			node.parent = this;
			node.cloneLeft = this.cloneLeft;
			node.floor = this.floor + 1;
			node.pos = pos;
			node.direction = this.direction;
			node.additionalElevators = this.additionalElevators;
			node.rounds = this.rounds + 1 + Math.abs(this.pos - pos);
			return node;
		}

		public Node block() {
			return block(pos);
		}
		
		
		public Node block(int pos) {
			Node node = new Node();
			node.action = Action.BLOCK;
			node.parent = this;
			node.cloneLeft = this.cloneLeft - 1;
			node.floor = this.floor;
			node.pos = pos;
			node.additionalElevators = this.additionalElevators;
			node.direction = this.direction == Direction.RIGHT ? Direction.LEFT : Direction.RIGHT;
			node.rounds = this.rounds + 3  + Math.abs(this.pos - pos);
			return node;
		}

		@Override
		public String toString() {
			return "A="+action+", Rnd=" + rounds + " F=" + floor + " / P=" + pos + " D="+direction+" => (" + cloneLeft + " / " + additionalElevators+")";
		}
	}

}
