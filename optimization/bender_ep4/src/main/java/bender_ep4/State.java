package bender_ep4;

import fast.read.FastReader;

public class State {
	static int width;
	static int height;
	static Pos start;
	static Pos target;
	
	static int switchCount;
	static Pos[] switches = new Pos[10];
	static Pos[] blocks = new Pos[10];
	static int grid[] = new int[21*21];
	
	
	int switchState = 0; // bit representation of switch state

	public void read(FastReader in) {
		State.width = in.nextInt();
		State.height = in.nextInt();
		
		for (int y = 0; y < height; y++) {
			String line = in.nextLine();
			for (int x=0;x<width;x++) {
				Pos pos = Pos.from(x, y);
				grid[pos.offset] = (line.charAt(x) == '#' ? 1 : 0);
			}
		}
		State.start = Pos.from(in.nextInt(), in.nextInt());
		State.target = Pos.from(in.nextInt(), in.nextInt());
		
		State.switchCount = in.nextInt();
		for (int i = 0; i < switchCount; i++) {
			State.switches[i] = Pos.from(in.nextInt(), in.nextInt());
			State.blocks[i] = Pos.from(in.nextInt(), in.nextInt());
			int initialState = in.nextInt(); // 1 if blocking, 0 otherwise
			
			if (initialState == 1) {
				switchState |= (1 << i);
			}
		}
	}

}
