package codeBusters;

public class CheckPoint {
	public final P position;
	int lastSeenTurn;
	
	public CheckPoint(P p) {
		position = p; 
		lastSeenTurn = -1000;
	}

}
