package ooc.trailmapper;

import ooc.P;

public class TNCache {

	private static final int MAX_TRAILNODES = 100_000;
	static TrailNode nodes[] = new TrailNode[MAX_TRAILNODES];
	static int currentIndex = MAX_TRAILNODES-1;
	
	static {
		for (int i=0;i<MAX_TRAILNODES;i++) {
			nodes[i] = new TrailNode(P.I);
		}
	}
	
	public static TrailNode pop() {
		TrailNode node = nodes[currentIndex--];
		node.reset();
		return node;
	}
	
	public static void push(TrailNode node) {
		nodes[++currentIndex] = node;
	}
	
	public static void restitute(TrailNode toRestitute[], int size) {
		for (int i=0;i<size;i++) {
			nodes[++currentIndex] = toRestitute[i];
		}
	}
}
