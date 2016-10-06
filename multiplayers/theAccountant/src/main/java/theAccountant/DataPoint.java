package theAccountant;

import trigonometry.Point;

public class DataPoint {

  int id;
  int worth = 100;
  Point p;
  
  DataPoint(Point p) {
    this.p = p;
  }
  public DataPoint duplicate() {
    DataPoint dp = new DataPoint(p);
    dp.id = id;
    dp.worth = worth;
    return dp;
  }

}
