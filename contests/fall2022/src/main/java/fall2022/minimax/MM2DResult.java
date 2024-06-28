package fall2022.minimax;

import java.util.Objects;

public class MM2DResult {
	public int keep;
	public int free;
	public int reinforcement;
	public boolean won = false;
	
	public MM2DResult(int k, int f, int r, boolean won) {
		this.keep = k;
		this.free = f;
		this.reinforcement = r;
		this.won = won;
	}
	
	@Override
	public String toString() {
		return String.format("( k=%d, a=%d, r=%d, won=%b) ", keep, free, reinforcement, won);
	}
	
	public static MM2DResult of(int k, int a, int r, boolean won) {
		return new MM2DResult(k,a,r, won);
	}
	@Override
	public int hashCode() {
		return Objects.hash(free, keep, reinforcement);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MM2DResult other = (MM2DResult) obj;
		return free == other.free && keep == other.keep && reinforcement == other.reinforcement && won == other.won;
	}

	public static MM2DResult keep(int k) {
		return new MM2DResult(k, 0, 0, false);
	}

	public MM2DResult free(int a) {
		return new MM2DResult(this.keep, a, this.reinforcement, this.won);
	}
	
	public MM2DResult reinforce(int r) {
		return new MM2DResult(this.keep, this.free, r, this.won);
	}
	public MM2DResult withWon() {
		return new MM2DResult(this.keep, this.free, this.reinforcement, true);
	}

	public void setWon() {
		this.won = true;
	}
	
}