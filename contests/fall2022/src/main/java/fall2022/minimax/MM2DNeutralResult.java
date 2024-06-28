package fall2022.minimax;

import java.util.Objects;

public class MM2DNeutralResult {
	public int attack;
	public int free;
	public boolean won = false;
	
	public MM2DNeutralResult(int a, int f, boolean won) {
		this.attack = a;
		this.free = f;
		this.won = won;
	}
	
	@Override
	public String toString() {
		return String.format("( k=%d, a=%d, won=%b) ", attack, free, won);
	}
	
	public static MM2DNeutralResult of(int k, int a, boolean won) {
		return new MM2DNeutralResult(k,a, won);
	}
	@Override
	public int hashCode() {
		return Objects.hash(free, attack);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MM2DNeutralResult other = (MM2DNeutralResult) obj;
		return free == other.free && attack == other.attack && won == other.won;
	}

	public static MM2DNeutralResult attack(int a) {
		return new MM2DNeutralResult(a, 0, false);
	}

	public MM2DNeutralResult free(int a) {
		return new MM2DNeutralResult(this.attack, a, this.won);
	}
	
	public MM2DNeutralResult withWon() {
		return new MM2DNeutralResult(this.attack, this.free, true);
	}

	public void setWon() {
		this.won = true;
	}
	
}