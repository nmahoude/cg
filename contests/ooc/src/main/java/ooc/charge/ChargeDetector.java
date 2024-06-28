package ooc.charge;

import ooc.Cooldown;
import ooc.orders.Order;
import ooc.orders.OrderVisitor;

public class ChargeDetector implements OrderVisitor {

  int totalPoints;
  
  public int delaiToTorpedo = Cooldown.MAX_TORPEDO_COOLDOWN;
  int delaiToMine = Cooldown.MAX_MINE_COOLDOWN;
  int delaiToSilence = Cooldown.MAX_SILENCE_COOLDOWN;
  int delaiToSonar = Cooldown.MAX_SONAR_COOLDOWN;
  
  public void setupTurn() {
  }

  public void teardownTurn() {
  }

  @Override
  public void usedMove(Order order) {
    totalPoints++;

    delaiToTorpedo = Math.max(0, delaiToTorpedo-1);
    delaiToSonar = Math.max(0, delaiToSonar-1);
    delaiToSilence = Math.max(0, delaiToSilence-1);
    delaiToMine = Math.max(0, delaiToMine-1);
  }

  @Override
  public void usedSurface(Order order) {
  }

  @Override
  public void usedTorpedo(Order order) {
    delaiToTorpedo = Cooldown.MAX_TORPEDO_COOLDOWN-1;// -1 because MOVE -> USE
  }

  @Override
  public void usedSonar(Order order) {
    delaiToSonar= Cooldown.MAX_SONAR_COOLDOWN-1;// -1 because MOVE -> USE
  }

  @Override
  public void usedSilence(Order order) {
    delaiToSilence= Cooldown.MAX_SILENCE_COOLDOWN-1;// -1 because MOVE -> USE
  }

  @Override
  public void usedMine(Order order) {
    delaiToMine = Cooldown.MAX_MINE_COOLDOWN-1; // -1 because MOVE -> USE
  }

  @Override
  public void usedTrigger(Order order) {
  }
  
  public boolean canTorpedoNextTurn() {
  	return delaiToTorpedo == 0; 
  }
  
  
}
