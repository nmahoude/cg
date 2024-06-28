package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PointsTest {
  Scan myOldScan = new Scan();
  Scan oppOldScan = new Scan();
  Scan myNewScan = new Scan();
  
  @BeforeAll
  static void megaSetup() {
    State.fColor = new int[] { -1, -1, -1, -1, 0, 1, 0, 1, 0, 1, 2, 3, 2, 3, 2, 3, 4, 4, 4, 4, 4, 4};
    State.fType = new int[] { -1, -1, -1, -1, 0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 3, 3, 3, 3, 3 , 3};
  }
  
  @BeforeEach
  void setup() {
    myOldScan = new Scan();
    oppOldScan = new Scan();
    myNewScan = new Scan();
  }
  
  @Test
  void noPoints() throws Exception {
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    assertThat(points).isZero();
  }
  
  @Test
  void firstWithFish() throws Exception {
    myNewScan.scan(4);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(2);
  }
  
  @Test
  void firstWithType1() throws Exception {
    myNewScan.scan(13);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(4);
  }

  @Test
  void firstWithType2() throws Exception {
    myNewScan.scan(8);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(6);
  }
  
  @Test
  void secondWithFish() throws Exception {
    oppOldScan.scan(4);
    myNewScan.scan(4);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(1);
  }
  
  @Test
  void firstWithRowCup() throws Exception {

    myNewScan.scan(10);
    myNewScan.scan(12);
    myNewScan.scan(14);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(18);
    
  }
  
  @Test
  void firstWithColCup() throws Exception {

    myNewScan.scan(12);
    myNewScan.scan(7);
    myNewScan.scan(6);
    myNewScan.scan(13);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(24);
  }
  
  @Test
  void secondWithColCup() throws Exception {
    oppOldScan.scan(12);
    oppOldScan.scan(7);
    oppOldScan.scan(6);
    oppOldScan.scan(13);
    
    
    myNewScan.scan(12);
    myNewScan.scan(7);
    myNewScan.scan(6);
    myNewScan.scan(13);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(12);
  }
  
  @Test
  void lastFishToGetFirstColCup() throws Exception {

    myOldScan.scan(12);
    myOldScan.scan(7);
    myOldScan.scan(6);
    myOldScan.updateFirsts(new Scan());
    
    myNewScan.scan(13);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(4+8);
  }

  @Test
  void completeLastFishToGetFirstColCupWithAllFishes() throws Exception {

    myOldScan.scan(12);
    myOldScan.scan(7);
    myOldScan.scan(6);
    myOldScan.updateFirsts(new Scan());
    
    myNewScan.scan(12);
    myNewScan.scan(7);
    myNewScan.scan(6);
    myNewScan.scan(13);
    
    int points = Points.deltaPoints(myNewScan, myOldScan, oppOldScan);
    
    assertThat(points).isEqualTo(4+8);
  }
}
