package tools;

import java.util.Locale;
import java.util.Scanner;

import meanmax.Game;

public class GameSetup {

  public static void setup() {
    read(0,0,1,0.5,400.0,1995.0,2674.0,269.0,138.0,-1,-1);
    read(1,1,1,1.5,400.0,423.0,3909.0,216.0,-41.0,-1,-1);
    read(2,2,1,1.0,400.0,-571.0,2975.0,-279.0,725.0,-1,-1);
    read(3,0,2,0.5,400.0,618.0,1818.0,85.0,-376.0,-1,-1);
    read(4,1,2,1.5,400.0,1564.0,1829.0,-62.0,126.0,-1,-1);
    read(5,2,2,1.0,400.0,1162.0,2920.0,3.0,-34.0,-1,-1);
    read(6,0,0,0.5,400.0,-2118.0,3792.0,-570.0,-693.0,-1,-1);
    read(7,1,0,1.5,400.0,-2396.0,109.0,-94.0,161.0,-1,-1);
    read(8,2,0,1.0,400.0,1937.0,3483.0,0.0,200.0,-1,-1);
    read(142,3,-1,5.5,700.0,-5067.0,2641.0,-120.0,62.0,6,6);
    read(156,3,-1,3.0,700.0,3097.0,1191.0,-234.0,-91.0,1,6);
    read(159,3,-1,3.0,700.0,-1200.0,-3540.0,81.0,238.0,1,6);
    read(164,3,-1,3.0,650.0,-2346.0,4833.0,84.0,-119.0,1,5);
    read(165,3,-1,3.0,850.0,5427.0,-1260.0,-247.0,57.0,1,9);
    read(168,3,-1,3.0,850.0,-6224.0,-6291.0,281.0,284.0,1,9);
    read(154,4,-1,-1.0,750.0,728.0,2338.0,0.0,0.0,2,-1);
    read(155,4,-1,-1.0,700.0,-2725.0,-593.0,0.0,0.0,6,-1);
    read(163,4,-1,-1.0,600.0,2795.0,-1807.0,0.0,0.0,1,-1);
    read(167,4,-1,-1.0,750.0,-3394.0,-461.0,0.0,0.0,1,-1);
    Game.backup();
  }
  
  static void read(int unitId, int unitType, int playerId, double mass, double radius, double x, double y, double vx, double vy, int extra, int extra2) {
    Scanner in = new Scanner(""+unitId+" "+unitType+" "+playerId+" "+mass+" "+(int)radius+" "+(int)x+" "+(int)y+" "+(int)vx+" "+(int)vy+" "+extra+" "+extra2);
    in.useLocale(Locale.ENGLISH);
    Game.readOneUnit(in);
    Game.backup(); // we backup the number of entities each turn ...
  }
}
