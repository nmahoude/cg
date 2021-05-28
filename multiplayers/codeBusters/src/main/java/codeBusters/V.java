package codeBusters;

public class V {
	final double vx, vy;
	
	public V(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
	}
	
	public static V dir(P origin, P target) {
		double vx = target.x - origin.x;
		double vy = target.y - origin.y;
		
		return new V(vx, vy);
	}
	
	
	public static V dirNorm(P origin, P target) {
		double vx = target.x - origin.x;
		double vy = target.y - origin.y;
		
		return new V(vx, vy).normalize();
	}
	
	public V inv() {
		return new V(-vx, -vy);
	}
	public V normalize() {
		double vx = this.vx;
		double vy = this.vy;
		double dist = Math.sqrt(vx*vx+vy*vy);
		if (dist == 0) dist = 1;
		vx /= dist;
		vy /= dist;

		return new V(vx, vy);
	}



	public V mult(int d) {
		return new V (vx * d, vy * d);
	}
	
	@Override
	public String toString() {
		return String.format("V(%f,%f)", vx, vy);
	}
}
