package marslander3;

public class Simulation {

    static final double G = 3.711;

    static double cos[] = new double[360];

    static double sin[] = new double[360];

    static {
        for (int i = -180; i < 180; i++) {
            cos[180 + i] = Math.cos(i * Math.PI / 180.);
            sin[180 + i] = Math.sin(i * Math.PI / 180.);
        }
    }

    private Mars mars;

    private MarsLander lander;

    public int result;

    public Simulation(Mars mars, MarsLander lander) {
        this.mars = mars;
        this.lander = lander;
    }

    /*
   * return false if lander is destroyed
   */
    public void update(int values[]) {
        if (lander.angle + values[0] < -90 || lander.angle + values[0] > 90)
            values[0] = 0;
        if (lander.thrust + values[1] < 0 || lander.thrust + values[1] > 4)
            values[1] = 0;
        // NOTE : plus de verification sur le fuel vu qu'on atjs une solution avec du fuel
        int angle = lander.angle + values[0];
        int thrust = lander.thrust + values[1];
        //    double oldX = lander.x;
        //    double oldY = lander.y;
        lander.fuel -= thrust;
        int correctedAngle = 180 + angle;
        double sinAngleThrust = sin[correctedAngle] * thrust;
        double cosAngleThrust = cos[correctedAngle] * thrust;
        lander.x = lander.x + lander.vx - 0.5 * sinAngleThrust;
        lander.y = lander.y + lander.vy + 0.5 * (cosAngleThrust - G);
        lander.vx = lander.vx - 1. * sinAngleThrust;
        lander.vy = lander.vy + 1. * (cosAngleThrust - G);
        lander.angle = angle;
        lander.thrust = thrust;
        result = checkAgainstMars(lander.getXAsInt(), lander.getYAsInt());
    //    if (result == -1.0) {
    //      lander.x = oldX;
    //      lander.y = oldY;
    //    }
    }

    /**
   * 0 : still in the air
   * 1 : landing safe
   * -1 : crash
   */
    /**
   * 0 : still in the air
   * 1 : landing safe
   * -1 : crash
   */
    private int checkAgainstMars(int x, int y) {
        if (x < 0)
            return -1;
        if (x > 6999)
            return -1;
        if (y - mars.dist[x] < 0) {
            if (mars.distanceToLandingZone(x, y) > 0) {
                return -1;
            } else if (lander.angle == 0 && Math.abs(lander.vx) < 20 && Math.abs(lander.vy) < 40) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    public void reset() {
        result = 0;
    }
}