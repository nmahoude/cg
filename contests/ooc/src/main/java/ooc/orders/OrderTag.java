package ooc.orders;

public enum OrderTag {
	MOVE   (0b1), 
  SURFACE(0b10),
  SILENCE(0b100),
  TORPEDO(0b1000),
  MINE   (0b10000),
  SONAR  (0b100000), 
  TRIGGER(0b1000000);
	
	public final int mask;
	
	OrderTag(int mask) {
		this.mask = mask;
	}
}
