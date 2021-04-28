package org.nmx.codingame.graphviewer.sample;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.nmx.codingame.graphviewer.GameNode;
import org.nmx.codingame.graphviewer.GlobalData;

public class SampleNode extends GameNode {
	private double score;
	private String t1 = UUID.randomUUID().toString();
	
	
	public SampleNode() {
		this.score = new Random().nextDouble(); 
	}
	
	@Override
	public List<String> getTipDisplay() {
		return Arrays.asList("TODO ...", "id = "+t1, ""+"Score="+score);
	}

	@Override
	public double score() {
		return score;
	}
	
	@Override
	public int getRadius(GlobalData data) {
	  if (this.score > data.maxScore() / 2) {
	    return 16;
	  } else {
	    return 8;
	  }
	}
	
	@Override
	public int getBoundingbox(GlobalData data) {
	  return 16 * 2;
	}
	
}
