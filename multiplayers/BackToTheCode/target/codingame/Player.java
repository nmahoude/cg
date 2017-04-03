/*
Proudly built by org.ndx.codingame.simpleclass.Assembler on 2017-04-03T21:25:18.088+02:00[Europe/Paris]
@see https://github.com/Riduidel/codingame/tree/master/tooling/codingame-simpleclass-maven-plugin
*/
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

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

    public static final int EMPTY = 0;

    public static final int WALL = -1;

    public static final int PLAYER1 = 1;

    public static final int PLAYER2 = 2;

    public static final int PLAYER3 = 3;

    public static final int PLAYER4 = 4;

    int rot[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    public int cells[][] = new int[35][20];

    public int free = 0;

    public int scores[] = new int[4];

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
                int player = value - '0';
                scores[player] += 1;
                cells[x][rowIndex] = player + 1;
            }
        }
    }

    public P findClosestFreeCell(P startingPoint, List<P> pointsToCheck, List<P> pointsChecked) {
        while (!pointsToCheck.isEmpty()) {
            P currentP = pointsToCheck.remove(0);
            pointsChecked.add(currentP);
            if (getCell(currentP) == EMPTY && !startingPoint.equals(currentP)) {
                return currentP;
            } else {
                int decal = new Random().nextInt(4);
                for (int i = 0; i < 4; i++) {
                    int index = (i + decal) % 4;
                    P p = new P(currentP.x + rot[index][0], currentP.y + rot[index][1]);
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

    public void debugInfos() {
        for (int i = 0; i < 4; i++) {
            System.err.println("P" + i + " -> " + scores[i]);
        }
        System.err.println("Free: " + free);
    }
}

class Player {

    static Board board = new Board();

    private static int gameRound;

    private static int backInTimeLeft;

    static int lastDir[] = { 0, 0 };

    static int totalDist = 0;

    static int initDirs[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    static int dir[][] = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

    static int lengths[] = { 0, 0, 0, 0 };

    static int dirIndex = 0;

    private static int totalBackInTime;

    private static int totalBackInTime_backup;

    static int targetX = new Random().nextInt(35), targetY = new Random().nextInt(20);

    static boolean isX = true;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int opponentCount = in.nextInt();
        totalBackInTime_backup = 0;
        while (true) {
            gameRound = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            backInTimeLeft = in.nextInt();
            totalBackInTime = backInTimeLeft;
            for (int i = 0; i < opponentCount; i++) {
                int opponentX = in.nextInt();
                int opponentY = in.nextInt();
                int opponentBackInTimeLeft = in.nextInt();
                totalBackInTime += opponentBackInTimeLeft;
            }
            board.reinit();
            for (int i = 0; i < 20; i++) {
                board.addRow(i, in.next());
            }
            board.debugInfos();
            // wait until last turn to trigger back in time !
            if (board.free == 0 && backInTimeLeft != 0) {
                boolean doReverse = false;
                for (int i = 1; i < 4; i++) {
                    if (board.scores[0] < board.scores[i]) {
                        doReverse = true;
                    }
                }
                if (doReverse) {
                    System.out.println("BACK 25");
                    continue;
                }
            }
            P currentPosition = new P(x, y);
            //      if (targetX == -1 || (currentPosition.x == targetX && currentPosition.y == targetY)) {
            //        boolean good = false;
            //        int tries = 0;
            //        while (!good && tries < 10) {
            //          tries++;
            //          boolean goX = !isX;
            //          isX = !isX;
            //          if (goX) {
            //            targetX = new Random().nextInt(35);
            //            if (targetX != x) {
            //              int dir = Math.abs(targetX-x) / (targetX-x);
            //              if (board.cells[x+dir][y] == 0 || board.cells[x+dir][y] == Board.PLAYER1) {
            //                good =true;
            //              }
            //            }
            //          } else {
            //            targetY = new Random().nextInt(20);
            //            if (targetY != y) {
            //              int dir = Math.abs(targetY-y) / (targetY-y);
            //              if (board.cells[x][y+dir] == 0 || board.cells[x][y+dir] == Board.PLAYER1) {
            //                good =true;
            //              }
            //            }
            //          }
            //          
            //        }
            //        
            //      }
            //      P nextPosition = new P(targetX, targetY);
            P nextPosition = findNextPosition(currentPosition);
            System.out.println("" + nextPosition.x + " " + nextPosition.y);
        }
    }

    private static P findNextPositionRandomRect(P currentPosition) {
        P nextPosition;
        if (totalBackInTime != totalBackInTime_backup) {
            System.err.println("Detecting back in time, redistributing...");
            System.err.println("Position is " + currentPosition.x + "," + currentPosition.y);
            totalBackInTime_backup = totalBackInTime;
            for (int i = 0; i < 4; i++) {
                lengths[i] = 0;
            }
            System.out.println("" + 0 + " " + 0);
            return null;
        }
        if (lengths[dirIndex] == 0) {
            dirIndex = (dirIndex + 1) % 4;
            if (lengths[dirIndex] == 0) {
                findRectangle(currentPosition.x, currentPosition.y);
            }
        }
        System.err.println("Next rectangle " + lengths[0] + "/" + lengths[1] + "/" + lengths[2] + "/" + lengths[3] + " dir=" + dirIndex);
        nextPosition = new P(currentPosition.x + dir[dirIndex][0], currentPosition.y + dir[dirIndex][1]);
        lengths[dirIndex]--;
        return nextPosition;
    }

    private static void findRectangle(int x, int y) {
        dirIndex = new Random().nextInt(4);
        // lengths
        int maxLength = 10;
        lengths[0] = Math.min(35 - x - 1, 2 + new Random().nextInt(maxLength));
        lengths[1] = Math.min(20 - y - 1, 2 + new Random().nextInt(maxLength));
        lengths[2] = Math.min(x, 2 + new Random().nextInt(maxLength));
        lengths[3] = Math.min(y, 2 + new Random().nextInt(maxLength));
    }

    private static P findNextPosition(P position) {
        if (board.free == 0) {
            return new P(17, 10);
        }
        System.err.println("Dist : " + totalDist);
        if (totalDist < 10 && (lastDir[0] != 0 || lastDir[1] != 0)) {
            totalDist++;
            int checkX = position.x + lastDir[0];
            int checkY = position.y + lastDir[1];
            if (checkX >= 0 && checkX < 35 && checkY >= 0 && checkY < 20) {
                if (board.cells[checkX][checkY] == 0) {
                    return new P(checkX, checkY);
                }
            }
        }
        totalDist = 0;
        List<P> pointsToCheck = new ArrayList<>();
        pointsToCheck.add(position);
        int iter = 0;
        P p = new P(0, 0);
        while (iter < 3) {
            iter++;
            p = board.findClosestFreeCell(position, pointsToCheck, new ArrayList<>());
            int[] newDir = new int[2];
            newDir[0] = mapTo101(p.x - position.x);
            newDir[1] = mapTo101(p.y - position.y);
            if (newDir[0] != -lastDir[0] || newDir[1] != -lastDir[1]) {
                lastDir[0] = newDir[0];
                lastDir[1] = newDir[1];
                iter = 10_000;
            }
        }
        return p;
    }

    private static int mapTo101(int i) {
        if (i < 0)
            return -1;
        if (i > 0)
            return 1;
        return 0;
    }
}
