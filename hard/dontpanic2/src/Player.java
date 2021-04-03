
import java.util.PriorityQueue;
import java.util.Scanner;

class Player {

	private static int nbFloors;
	private static int width;
	private static int nbRounds;
	private static int exitFloor;
	private static int exitPos;
	private static int nbTotalClones;
	private static int nbAdditionalElevators;

	static enum Direction {
		RIGHT(0), LEFT(1), NONE(100);

		public final int value;

		Direction(int value) {
			this.value = value;
		}
	}

	static enum Action {
		ELEVATOR, BLOCK, WAIT
	}

	static int floors[][];
	static int realFloors[][];

	static class AStarNode {
		public final int floor;
		public final int pos;
		public final int clones;
		public final int elevator;
		public final Player.Direction dir;
		public final int remainingRounds;

		public int rounds = -1;
		public AStarNode cameFrom;
		public Action action;
		
		public AStarNode(int y, int x, int c, int e, Direction dir) {
			this.floor = y;
			this.pos = x;
			this.clones = c;
			this.elevator = e;
			this.dir = dir;
			
			remainingRounds = Math.abs(exitFloor - floor) + Math.abs(pos - exitPos);
		}

		public int estimatedTotalRounds() {
			return rounds + remainingRounds;
		}
		
		public boolean isExit() {
			return pos == exitPos && floor == exitFloor;
		}
		
		
		@Override
		public String toString() {
			return ""+floor+" / "+pos+" / "+dir+" -> A = "+action;
		}
	}
	
	static Player.AStarNode[][][][][] aStars;
	static Player.AStarNode[][][][][] aStarCameFrom;
	static AStarNode exit;
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		nbFloors = in.nextInt();
		width = in.nextInt();
		nbRounds = in.nextInt();
		exitFloor = in.nextInt();
		exitPos = in.nextInt();
		nbTotalClones = in.nextInt();
		nbAdditionalElevators = in.nextInt();

		
		
		aStars = new AStarNode[nbFloors][width][nbTotalClones+1][nbAdditionalElevators+1][2];
		aStarCameFrom = new AStarNode[nbFloors][width][nbTotalClones+1][nbAdditionalElevators+1][2];

		for (int y=0;y<nbFloors;y++) {
			for (int x=0;x<width;x++) {
				for (int c=0;c<nbTotalClones+1;c++) {
					for (int e=0;e<nbAdditionalElevators+1;e++) {
						for (int d=0;d<2;d++) {
							aStars[y][x][c][e][d] = new AStarNode(y, x, c, e, d==0 ? Direction.RIGHT : Direction.LEFT);
						}
					}
				}
			}
		}
		
		
		floors = new int[nbFloors][width];
		realFloors = new int[nbFloors][width];

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

			AStarNode result = null;
			if (turn == 1) {
				AStarNode start = aStars[cloneFloor][ clonePos][ nbTotalClones][ nbAdditionalElevators][ Direction.valueOf(direction).value];
				nodes.add(start);
				result = findAStar();
			}
			
			
			if (result == null) {
				System.err.println("Pas de soluton trouvée :(");
			} else {
				System.err.println("Une solution apparemment");
				
				AStarNode current = result;
				while (current != null) {
					System.err.println("Node : "+current);
					if (current.action == Action.ELEVATOR) {
						System.err.println("Setting elevator @ "+current);
						floors[current.floor-1][current.pos] = 'E';
					}
					if (current.action == Action.BLOCK) {
						System.err.println("Setting block @ "+current);
						floors[current.floor][current.pos] = 'B';
					}
					
					current = current.cameFrom;
				}
			}

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

	static PriorityQueue<AStarNode> nodes = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.estimatedTotalRounds(), n2.estimatedTotalRounds()));
	public static AStarNode findAStar() {
		System.err.println("Astar turn");

		while (!nodes.isEmpty()) {
			
			AStarNode node = nodes.poll();
			
			if (node.rounds > nbRounds || node.floor > exitFloor) {
				continue;
			}
			
			if (node.isExit()) {
				System.err.println("Found exit");
				return node;
			}
			
			if (floors[node.floor][node.pos] == 'E' ) {
				// pas le choix
				AStarNode next = aStars[node.floor+1][ node.pos][ node.clones][ node.elevator][ node.dir.value];
				replaceIfNeeded(node, next, 1, Action.WAIT);
				continue;
			}
			
			// wait
			if (node.dir == Direction.RIGHT) {
				if (node.pos < width-1) {
					AStarNode next = aStars[node.floor][ node.pos+1][ node.clones][ node.elevator][ node.dir.value];
					replaceIfNeeded(node, next, 1, Action.WAIT);
				}
			} else {
				if (node.pos > 0) {
					AStarNode next = aStars[node.floor][ node.pos-1][ node.clones][ node.elevator][ node.dir.value];

					replaceIfNeeded(node, next, 1, Action.WAIT);
				}
			}
			
			// Elevator
			if (node.elevator > 0 && node.floor < exitFloor) {
				AStarNode next = aStars[node.floor+1][ node.pos][ node.clones][ node.elevator-1][ node.dir.value];

				replaceIfNeeded(node, next, 3, Action.ELEVATOR);
			}
			
			// Block
			if (node.clones > 0) {
				AStarNode next = aStars[node.floor][ node.pos][ node.clones-1][ node.elevator][ node.dir == Direction.RIGHT ? Direction.LEFT.value: Direction.RIGHT.value];

				replaceIfNeeded(node, next, 3, Action.BLOCK);
			}
		}

		System.err.println("No exit");
		return null;
	}
		
	
	private static void replaceIfNeeded(AStarNode node, AStarNode next, int cost, Action action) {
		if (next.rounds == -1 || next.rounds > node.rounds+cost) {
			nodes.remove(next);
			next.rounds = node.rounds+cost;
			next.cameFrom = node;
			next.action = action;
			nodes.add(next);
		}				
	}
}
