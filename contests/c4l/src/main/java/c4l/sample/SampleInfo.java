package c4l.sample;

import java.util.ArrayList;
import java.util.List;

import c4l.entities.Sample;

public class SampleInfo {
  public int points = 0;
  public double score = Double.NEGATIVE_INFINITY;
  public List<Sample> samples = new ArrayList<>();
  
  @Override
  public String toString() {
    String output = "points = "+points+", score = "+score+ " samples:[";
    for (Sample sample : samples) {
      output+=""+sample.id+",";
    }
    output+="]";
    return output;
  }
}
