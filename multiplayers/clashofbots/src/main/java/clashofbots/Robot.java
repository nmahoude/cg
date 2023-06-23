package clashofbots;

/**
 * the robot vision
 */
public class Robot {
  static int CENTER_X = 2, CENTER_Y = 2;
  static int deltas[][] = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
  static String directions[] = { "RIGHT", "LEFT", "DOWN", "UP" };
  int grid[][] = new int[5][5];
  public String command = "SELFDESTRUCTION";

  public void think(State state) {

    if (shouldSelfDestruct()) {
      command = "SELFDESTRUCTION";
      return;
    }

    if (flee()) {
      return;
    }

    if (attack()) {
      return;
    }

//    if (moveToAttack()) {
//      return;
//    }

    int ennemyCount = countAround(false, CENTER_Y, CENTER_X);
    if (ennemyCount > 1) {
      command = "GUARD";
      return;
    }

    if (defendCell()) {
      return;
    }

    command = "GUARD DONT KNOW WHAT TO DO";
    return;
  }

  private boolean defendCell() {
    if (countAround(false, CENTER_Y, CENTER_X) != 0) return false;
    
    // fire in a cell that we want to protect or just to hit some moving target
    // (hopefully)
    for (int d = 0; d < 4; d++) {

      int x = CENTER_X + deltas[d][0];
      int y = CENTER_Y + deltas[d][1];
      if (grid[x][y] != 0) continue;

      int ennemy = countAround(false, x, y);
      if (ennemy > 0) {
        command = "ATTACK " + directions[d];
        return true;
      }
    }

    return false;
  }

  private boolean moveToAttack() {
    // no ennemy around, but can double the firpower of a friend robot
    if (countAround(false, CENTER_Y, CENTER_X) > 0)
      return false;

    for (int d = 0; d < 4; d++) {

      int x = CENTER_X + deltas[d][0];
      int y = CENTER_Y + deltas[d][1];
      if (grid[x][y] != 0)
        continue;

      if (countAround(false, x, y) == 1) {
        command = "MOVE " + directions[d];
        return true;
      }
    }

    return false;
  }

  private boolean attack() {
    if (countAround(false, CENTER_X, CENTER_Y) == 0)
      return false;

    // check the best ennemy robot to attack (kill, multi target, single target)
    int bestD = -1;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int d = 0; d < 4; d++) {

      int x = CENTER_X + deltas[d][0];
      int y = CENTER_Y + deltas[d][1];
      if (grid[x][y] > 0 || grid[x][y] == 0)
        continue;

      int health = -grid[x][y];

      double score = 0;
      score += (10 - health);
      if (health <= 2)
        score += 100;

      score += 2 * (countAround(true, x, y) - 1);

      if (score > bestScore) {
        bestScore = score;
        bestD = d;
      }
    }

    if (bestD != -1) {
      command = "ATTACK " + directions[bestD];
      return true;
    }

    return false;
  }

  private boolean flee() {
    if (countAround(false, CENTER_X, CENTER_Y) <= 1)
      return false;

    for (int d = 0; d < 4; d++) {

      int x = CENTER_X + deltas[d][0];
      int y = CENTER_Y + deltas[d][1];
      if (grid[x][y] != 0)
        continue;

      int enemyCount = countAround(false, x, y);
      int myCount = countAround(true, x, y) - 1;

      if (myCount < enemyCount)
        continue;

      command = "MOVE " + directions[d];
      return true;
    }

    return false; // can't flee
  }

  private int countAround(boolean myRobots, int centerX, int centerY) {
    int count = 0;
    for (int d = 0; d < 4; d++) {

      int x = centerX + deltas[d][0];
      int y = centerY + deltas[d][1];

      if ((myRobots && grid[x][y] > 0) || (!myRobots && grid[x][y] < 0)) {
        count++;
      }
    }
    return count;
  }

  private boolean shouldSelfDestruct() {

    double score = -grid[CENTER_X][CENTER_Y];

    for (int y = -1; y < 1; y++) {
      for (int x = -1; x < 1; x++) {
        if (x == 0 && y == 0)
          continue;

        int value = grid[CENTER_X + x][CENTER_Y + y];
        if (value < 0) {
          value = -value;
          if (value <= 4) {
            // TODO prendre guard en compte ?
            score += 1000;
          } else {
            score += 1;
          }
        } else if (value > 0) {
          if (value <= 4) {
            // TODO prendre guard en compte ?
            score -= 1000;
          } else {
            score -= 1;
          }
        }
      }
    }

//    if (grid[CENTER_X][CENTER_Y] <= 3 && score > 500) {
//      return true;
//    } else {
//      return false;
//    }

    return false;
  }

}
