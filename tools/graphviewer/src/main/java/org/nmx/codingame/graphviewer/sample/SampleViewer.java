package org.nmx.codingame.graphviewer.sample;

import org.nmx.codingame.graphviewer.CodingameView;
import org.nmx.codingame.graphviewer.GameNode;

public class SampleViewer {

	public static void main(String[] args) throws Exception {
		SampleNode root = new SampleNode();
		addSubNodes(root, 5, 4);

		CodingameView.execute(root);
	}

	private static void addSubNodes(GameNode root, int count, int depth) {
		if (depth == 0) return;
		
		for (int i=0;i<count;i++) {
			SampleNode child = new SampleNode();
			root.addChild(child);
				addSubNodes(child, count, depth-1);
		}
	}
}
