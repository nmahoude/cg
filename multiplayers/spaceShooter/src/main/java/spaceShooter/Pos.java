package spaceShooter;

public class Pos {
	double x;
	double y;
	public void set(double positionX, double positionY) {
		this.x = positionX;
		this.y = positionY;
	}
	public void set(Pos pos) {
		this.x = pos.x;
		this.y = pos.y;
	}
	public void add(Vec vec) {
		this.x += vec.vx;
		this.y += vec.vy;
	}
	
	public boolean inRadius(Pos pos, double radius) {
		double dist = (this.x-pos.x)*(this.x-pos.x) + (this.y-pos.y)*(this.y-pos.y);
		
		return dist <= radius*radius;
	}
}
