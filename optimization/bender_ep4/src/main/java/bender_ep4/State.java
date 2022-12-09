package bender_ep4;

import fast.read.FastReader;

public class State {
	static int width;
	static int height;
	static Pos start;
	static Pos target;
	
	static int switchCount;
	static Pos[] switches = new Pos[12];
	static Pos[] fields = new Pos[12];
	int grid[] = new int[21*21];
	
	
	int switchState = 0; // bit representation of switch state

	public void read(FastReader in) {
		State.width = in.nextInt();
		State.height = in.nextInt();
		
		for (int y = 0; y < height; y++) {
			String line = in.nextLine();
			for (int x=0;x<width;x++) {
				Pos pos = Pos.from(x, y);
				char c = line.charAt(x);
				if (c == '#') grid[pos.offset] = 1;
				if (c == '+') grid[pos.offset] = 2;
				if (c == '.') grid[pos.offset] = 0;
			}				
		}
		State.start = Pos.from(in.nextInt(), in.nextInt());
		State.target = Pos.from(in.nextInt(), in.nextInt());
		
		State.switchCount = in.nextInt();
		for (int i = 0; i < switchCount; i++) {
			State.switches[i] = Pos.from(in.nextInt(), in.nextInt());
			State.fields[i] = Pos.from(in.nextInt(), in.nextInt());
			int initialState = in.nextInt(); // 1 if blocking, 0 otherwise
			
			grid[State.switches[i].offset] = 10 + i;
			if (initialState == 1) {
				grid[State.fields[i].offset] = 30 + i;
				switchState |= (1 << i);
			}
		}
		System.err.println("Switch state is "+Integer.toBinaryString(switchState)+" ( "+switchState+")");
	}

	public void debug() {
		System.err.println("State grid:  ");
		for (int y = 0; y < height; y++) {
			for (int x=0;x<width;x++) {
				System.err.print(grid[Pos.from(x, y).offset] == 0 ? ' ' : 'x');
			}
			System.err.println();
		}
	}

}
