package blockthespreadingfire;

public class Node {
	Pos pos;
	int dist;
	public static Node from(int x, int y, int dist) {
		Node node = new Node();
		node.pos = Pos.from(x, y);
		node.dist = dist;
		return node;
	}
}
