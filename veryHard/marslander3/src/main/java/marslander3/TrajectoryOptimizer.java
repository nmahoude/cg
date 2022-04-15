package marslander3;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryOptimizer {

    public int currentSegment;

    List<Double> xPoints = new ArrayList<>();

    List<Double> yPoints = new ArrayList<>();

    public void calculate(Mars mars, MarsLander lander) {
        currentSegment = mars.findCurrentSegment(lander.x);
        xPoints.clear();
        yPoints.clear();
        double currentTrajX = lander.x;
        double currentTrajY = lander.y;
        xPoints.add(currentTrajX);
        yPoints.add(currentTrajY);
        double nextTrajX = mars.pointsX[currentSegment + 1], nextTrajY = mars.pointsY[currentSegment + 1];
        double nextAngle = (nextTrajY - currentTrajY) / (nextTrajX - currentTrajX);
        for (int i = currentSegment + 2; i <= mars.flatIndexStart; i++) {
            double potTrajX = mars.pointsX[i], potTrajY = mars.pointsY[i];
            double potAngle = (potTrajY - currentTrajY) / (potTrajX - currentTrajX);
            if (potAngle < nextAngle) {
                System.err.println("Break in trajectory ...");
                xPoints.add(nextTrajX);
                yPoints.add(nextTrajY);
                currentTrajX = nextTrajX;
                currentTrajY = nextTrajY;
                nextTrajX = potTrajX;
                nextTrajY = potTrajY;
                nextAngle = (nextTrajY - currentTrajY) / (nextTrajX - currentTrajX);
            } else {
                nextTrajX = potTrajX;
                nextTrajY = potTrajY;
                nextAngle = potAngle;
            }
        }
        xPoints.add(nextTrajX);
        yPoints.add(nextTrajY);
        for (int i = 0; i < xPoints.size(); i++) {
            System.err.println(" To " + xPoints.get(i) + " , " + yPoints.get(i));
        }
    }

    /**
   *  get the Y distance between the lander and the best Trajectory 
   */
    /**
   *  get the Y distance between the lander and the best Trajectory 
   */
    public double getYDistance(MarsLander lander) {
        for (int i = 0; i < xPoints.size() - 1; i++) {
            if (lander.x >= xPoints.get(i) && lander.y < xPoints.get(i + 1)) {
                double dist = yPoints.get(i) + 1.0 * (yPoints.get(i + 1) - yPoints.get(i)) * (lander.x - xPoints.get(i)) / (xPoints.get(i + 1) - xPoints.get(i));
                return Math.abs(lander.y - dist);
            }
        }
        return 0;
    }
}