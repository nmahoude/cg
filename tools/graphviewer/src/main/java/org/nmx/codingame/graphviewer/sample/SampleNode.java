package org.nmx.codingame.graphviewer.sample;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.nmx.codingame.graphviewer.GameNode;

public class SampleNode extends GameNode {
	private double score;

	public SampleNode() {
		this.score = new Random().nextDouble(); 
	}
	
	@Override
	public List<String> getTipDisplay() {
		return Arrays.asList("TODO ...", "TODO 2 ...");
	}

	@Override
	public double score() {
		return score;
	}

	public void addChild(SampleNode child) {
		children.add(child);
		child.parent = this;
	}

}
