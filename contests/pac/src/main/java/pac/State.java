package pac;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import pac.agents.Pacman;
import pac.agents.PacmanType;
import pac.agents.Pellet;
import pac.map.Pos;

public class State {
  public static final int SUPERPELLET_VALUE = 100;

  public static int MAX_THEORICAL_SCORE = 0;
  DeadEndOptimizer deadEndOptimizer = new DeadEndOptimizer(this);

  int myScore;
  int opponentScore;

  public Set<Pos> bigPellets = new HashSet<>();
  //public HashMap<Pos, Pellet> pellets = new HashMap<>();
  public Pellet pellets[] = new Pellet[Pos.SURFACE];
  public Set<Pos> visiblePellets = new HashSet<>();

  public Pacman pacmen[] = new Pacman[10];
  public int maxPacmen;

  public State() {
    for (int i=0;i<10;i++) {
      pacmen[i] = new Pacman(i % 5);
    }
  }

  public void init() {
    MAX_THEORICAL_SCORE = 0;
    for (int i=0;i<Pos.SURFACE;i++) {
      Pos p = Pos.getFromOffset(i);
      if (Player.map.isWall(p)) continue;
      
      
      pellets[i] = new Pellet(p, 1.0);
      MAX_THEORICAL_SCORE++;
    }
  }
  
  public void read(Scanner in) {
    // decrement pellets value
    for (int i=0;i<Pos.SURFACE;i++) {
      if (pellets[i] != null) {
        pellets[i].value *= 0.993;
      }
    }
    
    visiblePellets.clear();
    for (int i=0;i<10;i++) {
      pacmen[i].initTurn();
    }
    
    myScore = in.nextInt();
    opponentScore = in.nextInt();
    
    System.err.println("Turn "+Player.turn);
    System.err.println("Scores : "+myScore+" "+opponentScore);
    Player.start = System.currentTimeMillis();
    if (Player.DEBUG_TU) {
      System.err.println("\""+myScore+" "+opponentScore+" \"+");
    }
    
    
    int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
    if (Player.DEBUG_TU) {
      System.err.println("\""+visiblePacCount+" \"+");
    }
    for (int i = 0; i < visiblePacCount; i++) {
      int pacId = in.nextInt();
      boolean mine = in.nextInt() != 0;
      if (mine) {
        maxPacmen = Math.max(maxPacmen, pacId+1);
      }
      int x = in.nextInt();
      int y = in.nextInt();

      
      Pos p = Pos.get(x, y);
      int pacIndex;
      if (mine) {
        pellets[p.offset].value = 0;
        pacIndex = pacId;
      } else {
        pacIndex = 5+pacId;
      }
      
      String typeAsString = in.next();
      int speedTurnsLeft = 0; //in.nextInt();
      int cooldown = 1; //in.nextInt();
      if (Player.DEBUG_TU) {
        System.err.println("\""+pacId+" "+(mine?"1":"0")+" "+x+" "+y+" "+typeAsString+" "+speedTurnsLeft+" "+cooldown+" \"+");
      }
      
      pacmen[pacIndex].update(p, PacmanType.fromString(typeAsString), speedTurnsLeft, cooldown);
      if (!Player.DEBUG_TU && Player.DEBUG_PAC_INFO) {
        System.err.println(pacmen[pacIndex].toString());
      }
    }
    
    // remove all big pellets as they are visible from everywhere
    clearSuperPellets();

    // decrease unseen pellets value
    if (Player.BR_DECREMENT_PELLETS_VALUE) {
      for (int i=0;i<Pos.SURFACE;i++) {
        if (pellets[i] != null) pellets[i].value = Math.max(0, pellets[i].value - 0.005);
      }
    }
    
    // init all potentially visible pellets to 0
    deadEndOptimizer.clear();
    for (int i=0;i<5;i++) {
      Pos pacman = pacmen[i].pos;
      if (pacman == Pos.INVALID) continue;
      
      for (int d=0;d<4;d++) {
        Pos current = pacman;
        Pos last = pacman;
        do {
          pellets[current.offset].value = 0;
          last = current;
          current = current.neighbors[d];
          
        } while (current != Pos.INVALID && current != pacman /* check for circular map!*/);
        
        deadEndOptimizer.search(pacmen[i], last, d);
      }
    }
    
    int visiblePelletCount = in.nextInt(); // all pellets in sight
    if (Player.DEBUG_TU) {
      System.err.println("\""+visiblePelletCount+" \"+");
    }
    
    for (int i = 0; i < visiblePelletCount; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int value = in.nextInt(); // amount of points this pellet is worth
      if (Player.DEBUG_TU) {
        System.err.println("\""+x+" "+y+" "+value+" \"+");
      }
      
      Pos p = Pos.get(x, y);
      pellets[p.offset].value  = value;
      visiblePellets.add(p);
      
      if (value == 10) {
        pellets[p.offset].value = SUPERPELLET_VALUE;
//        if (forbiddenSuperPellets.contains(p)) {
//          pellets[p.offset].value = 0.5;
//        }

        bigPellets.add(p);
      }
    }

    // clear deadEnd where I don't see any pellets
    // rational is if someone has entered the deadend, he will clean it
    deadEndOptimizer.optimize();
    
    if (Player.turn == 1) {
      updateTotalNormalPellets();
      leverageStartSymetry();
    }
    
    if (Player.DEBUG_PELLETS) {
      showCurrentPelletsKnowledge();
    }
    
    if (Player.DEBUG_SEEING) {
      Player.map.debugMap("Pacman Ennemis", pos -> {
        for (int i=5;i<10;i++) {
          if (this.pacmen[i].isDead()) continue;

          if (this.pacmen[i].pos == pos) return ""+i;
        }
        return " ";
      });
    }
  }

  private void clearSuperPellets() {
    for (Pos p : bigPellets) {
      pellets[p.offset].value = 0;
    }
    bigPellets.clear();
  }

  private void showCurrentPelletsKnowledge() {
    Player.map.debugMap("potential Pellets", pos -> {
      Pellet pellet = pellets[pos.offset];
      if (pellet.value == 0) return " ";
      else if (pellet.value == 1) return ".";
      else if (pellet.value <  1) return ""+(char)(65+(int)(pellet.value * 26));
      else return "O";
    });
  }


  public int getClosestFromPelletAt(Pos p) {
    int bestIndex = 0;
    int bestDist = Integer.MAX_VALUE;
    
    for (int index=0;index<10;index++) {
      Pacman pacman = pacmen[index];
      if (pacman.pos == Pos.INVALID || pacmen[index].isDead()) continue;
      int distance = pacmen[index].pos.distance(p);
      if (distance < bestDist) {
        bestDist = distance;
        bestIndex = index;
      }
    }
    return bestIndex;
  }

  private void updateTotalNormalPellets() {
    MAX_THEORICAL_SCORE+=10*bigPellets.size() - 1* bigPellets.size()  // transform normal pellets to big ones
        - 2 * maxPacmen // remove where the pacmen are
        ; 
    System.err.println("Calculated max score = "+MAX_THEORICAL_SCORE);
  }

  private void leverageStartSymetry() {
    // update other positions
    for (int i=0;i<5;i++) {
      if (pacmen[i].pos== Pos.INVALID) continue;
      
      pacmen[5+i].pos =  Pos.get(Player.map.width - 1 - pacmen[i].pos.x , pacmen[i].pos.y);
      pacmen[5+i].type = pacmen[i].type;
      pacmen[5+i].speedTurnsLeft = pacmen[i].speedTurnsLeft; 
      pacmen[5+i].cooldown = pacmen[i].cooldown;
      
      pellets[pacmen[5+i].pos.offset].value = 0;
    }
    
    // remove pellets from best path to superpellets !
    Pos currentPos[] = new Pos[10];
    for (int i=5;i<10;i++) {
      currentPos[i] = this.pacmen[i].pos;
    }
    for (Pos pellet : bigPellets) {
      int index = getClosestFromPelletAt(pellet);
      if (index >= 5) {
        clearPathFromTo(currentPos[index], pellet);
        currentPos[index] = pellet;
      } else {
        Pacman.myBigPellets.add(pellets[pellet.offset]);
      }
    }
  }

  
  private void clearPathFromTo(Pos from, Pos pellet) {
    Pos current = from;
    
    pellets[pellet.offset].value = 0.1;
    while (current != pellet) {
      int bestDist = Integer.MAX_VALUE;
      Pos best = null;
      for (Pos neighbor : current.neighborsList) {
        if (neighbor == null) continue;
        if (neighbor.distance(pellet) < bestDist) {
          bestDist = neighbor.distance(pellet);
          best = neighbor;
        }
      }
      pellets[best.offset].value = 0.1;
      current = best;
    }
  }
  
}
