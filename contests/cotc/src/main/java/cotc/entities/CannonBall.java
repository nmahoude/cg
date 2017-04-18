package cotc.entities;

public class CannonBall extends Entity {
  public final int ownerEntityId;
  public final int srcX;
  public final int srcY;
  public final int initialRemainingTurns;
  public int remainingTurns;
  
  private int b_remainingTurns;

  public CannonBall(int entityId, int x, int y, Ship sender, int remaining) {
    super(EntityType.CANNONBALL, entityId, x, y);
    srcX = sender.position.x;
    srcY = sender.position.y;
    ownerEntityId = sender.id;
    remainingTurns = remaining;
    initialRemainingTurns = remaining;
  }

  public CannonBall(int entityId, int x, int y, int senderId, int srcX, int srcY, int remaining) {
    super(EntityType.CANNONBALL, entityId, x, y);
    this.srcX = srcX;
    this.srcY = srcY;
    ownerEntityId = senderId;
    remainingTurns = remaining;
    initialRemainingTurns = remaining;
  }
  
  public void update(int x, int y, int arg1, int arg2, int arg3, int arg4) {
    remainingTurns = arg2;
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(ownerEntityId, remainingTurns, 0, 0);
}
  @Override
  public void backup() {
    super.backup();
    b_remainingTurns = remainingTurns;
  }

  @Override
  public void restore() {
    super.restore();
    remainingTurns = b_remainingTurns;
  }
}
