package org.nmx.codingame.graphviewer.sample;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.nmx.codingame.graphviewer.GameNode;

public class SampleNode extends GameNode {
	private double score;
	private String t1 = UUID.randomUUID().toString();
	
	
	public SampleNode() {
		this.score = new Random().nextDouble(); 
	}
	
	@Override
	public List<String> getTipDisplay() {
		return Arrays.asList("TODO ...", t1, ""+score);
	}

	@Override
	public double score() {
		return score;
	}
}
