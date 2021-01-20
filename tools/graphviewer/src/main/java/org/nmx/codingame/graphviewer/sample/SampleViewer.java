package org.nmx.codingame.graphviewer.sample;

import org.nmx.codingame.graphviewer.CodingameView;

public class SampleViewer {

	public static void main(String[] args) {
		SampleNode root = new SampleNode();
		for (int i=0;i<3;i++) {
			SampleNode child = new SampleNode();
			root.addChild(child);
		}
		
		
		CodingameView.execute(root);
	}
}
