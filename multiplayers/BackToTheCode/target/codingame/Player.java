import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Player {

    static Board board = new Board();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        // Opponent count
        int opponentCount = in.nextInt();
        // game loop
        while (true) {
            int gameRound = in.nextInt();
            // Your x position
            int x = in.nextInt();
            // Your y position
            int y = in.nextInt();
            // Remaining back in time
            int backInTimeLeft = in.nextInt();
            for (int i = 0; i < opponentCount; i++) {
                // X position of the opponent
                int opponentX = in.nextInt();
                // Y position of the opponent
                int opponentY = in.nextInt();
                // Remaining back in time of the opponent
                int opponentBackInTimeLeft = in.nextInt();
            }
            board.reinit();
            for (int i = 0; i < 20; i++) {
                board.addRow(i, in.next());
            }
            P position = new P(x, y);
            List<P> pointsToCheck = new ArrayList<>();
            pointsToCheck.add(position);
            P p = board.findClosestFreeCell(pointsToCheck, new ArrayList<>());
            // action: "x y" to move or "BACK rounds" to go back in time
            System.out.println("" + p.x + " " + p.y);
        }
    }
}

class P {

    public P(int i, int j) {
        x = i;
        y = j;
    }

    public int x;

    public int y;

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        P other = (P) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}

class Board {

    private static final int EMPTY = 0;

    private static final int WALL = -1;

    public static final int PLAYER1 = 1;

    public static final int PLAYER2 = 2;

    public static final int PLAYER3 = 3;

    public static final int PLAYER4 = 4;

    int rot[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    int cells[][] = new int[35][20];

    int free = 0;

    int scores[] = new int[4 + 1];

    public void reinit() {
        free = 0;
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
        }
    }

    public void addRow(int rowIndex, String row) {
        for (int x = 0; x < 35; x++) {
            char value = row.charAt(x);
            if (value == '.') {
                free++;
                cells[x][rowIndex] = EMPTY;
            } else {
                int player = 1 + value - '0';
                scores[player] += 1;
                cells[x][rowIndex] = player;
            }
        }
    }

    public P findClosestFreeCell(List<P> pointsToCheck, List<P> pointsChecked) {
        while (!pointsToCheck.isEmpty()) {
            P currentP = pointsToCheck.remove(0);
            pointsChecked.add(currentP);
            if (getCell(currentP) == EMPTY) {
                return currentP;
            } else {
                for (int i = 0; i < 4; i++) {
                    P p = new P(currentP.x + rot[i][0], currentP.y + rot[i][1]);
                    if (!pointsChecked.contains(p) && !pointsToCheck.contains(p)) {
                        pointsToCheck.add(p);
                    }
                }
            }
        }
        return new P(0, 0);
    }

    private int getCell(P p) {
        return getCell(p.x, p.y);
    }

    private int getCell(int i, int j) {
        if (i < 0 || i > 35 - 1 || j < 0 || j > 20 - 1) {
            return WALL;
        }
        return cells[i][j];
    }
}
