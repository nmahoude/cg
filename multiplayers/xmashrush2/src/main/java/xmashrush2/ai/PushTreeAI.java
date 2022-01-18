package xmashrush2.ai;

import java.util.ArrayList;
import java.util.List;

import xmashrush2.State;

/*
 * tree of single player pushes to reach items 
 */
public class PushTreeAI {

	private static final int MAX_NODES = 20_000;
	static PushTreeNode[] nodes = new PushTreeNode[MAX_NODES];
	private static int currentNodeFE = 0;
	static {
		for (int i=0;i<MAX_NODES;i++) {
			nodes[i] = new PushTreeNode();
		}
	}

	
	
	private final int depth;
	
	public PushTreeAI(int depth) {
		this.depth = depth;
	}
	
	
	
	public List<PushTreeNode> findSolution(State initialState) {
		resetCache();
		List<PushTreeNode> solutions = new ArrayList<>();
		
		PushTreeNode root = getFromCache();
		root.initFrom(initialState);

		List<PushTreeNode> childs1 = root.simulateAllPush();
		
		List<PushTreeNode> childs2 = new ArrayList<>();
		for (PushTreeNode child : childs1) {
			childs2.addAll(child.simulateAllPush());
		}
		
		
		
		return childs2;
	}



	static PushTreeNode getFromCache() {
		return nodes[currentNodeFE++];
	}

	public static void resetCache() {
		currentNodeFE = 0;
	}
}
