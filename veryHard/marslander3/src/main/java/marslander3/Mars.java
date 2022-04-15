package marslander3;

import java.util.Scanner;

public class Mars {

    public int[] pointsX;

    public int[] pointsY;

    int flatIndexStart = -1;

    int posxStart, posxEnd;

    // all distances by x !
    public double dist[] = new double[7000];

    public void readInput(Scanner in) {
        int N = in.nextInt();
        Player.inputLogger.debug(""+N);
        
        pointsX = new int[N];
        pointsY = new int[N];
        for (int i = 0; i < N; i++) {
            int landX = in.nextInt();
            int landY = in.nextInt();
            Player.inputLogger.debug(""+landX+" "+landY);
            pointsX[i] = landX;
            pointsY[i] = landY;
            if (i != 0) {
                if (pointsY[i - 1] == pointsY[i]) {
                    flatIndexStart = i - 1;
                    posxStart = pointsX[i - 1];
                    posxEnd = pointsX[i];
                }
            }
            if (i > 0) {
                int sx = pointsX[i - 1];
                int ex = pointsX[i];
                int sy = pointsY[i - 1];
                int ey = pointsY[i];
                for (int x = sx; x < ex; x++) {
                    dist[x] = sy + 1.0 * (ey - sy) * (x - sx) / (ex - sx);
                }
            }
        }
        if (Player.DEBUG_OUTPUT) {
            System.err.print("" + N + " ");
            for (int i = 0; i < N; i++) {
                System.err.print("" + pointsX[i] + " " + pointsY[i] + " ");
            }
            System.err.println();
        }
    }

    public double distanceToLandingZone(MarsLander lander) {
        return distanceToLandingZone(lander.getXAsInt(), lander.getYAsInt());
    }

    public double distanceToLand(MarsLander lander) {
        return dist[lander.getXAsInt()];
    }

    public double distanceToLandingZone(int x, int y) {
        if (x < posxStart) {
            return 1.0 * (posxStart - x) / (posxStart - 0);
        } else if (x > posxEnd) {
            return 1.0 * (x - posxEnd) / (7000 - posxEnd);
        } else {
            return 0.0;
        }
    }

    public int findCurrentSegment(double x) {
        for (int s = 0; s < this.pointsX.length; s++) {
            if (x >= this.pointsX[s] && x < this.pointsX[s + 1]) {
                return s;
            }
        }
        return -1;
    }
}