package org.nmx.codingame.graphviewer;

public class GlobalData {
  double minScore;
  double maxScore;
  double percentile;
  
  public GlobalData() {
    maxScore = Double.NEGATIVE_INFINITY;
    minScore = Double.POSITIVE_INFINITY;
    percentile = 0.1;
  }

  public double reducted(double score) {
    return (score-minScore) / (maxScore- minScore);
  }

  public double scoreThreshold() {
    return this.percentile * this.maxScore;
  }

  public void addValue(double score) {
    if (score < this.minScore) { this.minScore = score; }
    if (score > this.maxScore) { this.maxScore = score; }
    
  }

  public double maxScore() {
    return maxScore;
  }
  
  public double minScore() {
    return minScore;
  }
}
