package bttc;

public class Agent {
  Position pos = Position.get(0, 0);
  int backInTimeLeft;
  Direction direction = Direction.None;
  Direction lastTurn = Direction.None; // Left or Right
  int score;
  public void copyFrom(Agent model) {
    this.pos = model.pos;
    this.backInTimeLeft = model.backInTimeLeft;
    this.direction = model.direction;
    this.lastTurn = model.lastTurn;
    this.score = model.score;
  }
}
