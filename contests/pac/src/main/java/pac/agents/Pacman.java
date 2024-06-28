package pac.agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pac.Player;
import pac.State;
import pac.map.Pos;
import pac.minimax.MMNode;
import pac.minimax.Minimax;
import pac.sim.Action;
import pac.simpleai.ActionAI;
import pac.simpleai.Order;

public class Pacman {
  public final int index;
  
  public Pos pos = Pos.INVALID;
  public PacmanType type;
  public int cooldown;
  public int speedTurnsLeft;
  
  public Order order = new Order();
  public Mind mind = Mind.HUNGRY;
  public int mindTurn;
  
  public int afraidOf; // only one pacman ...

  ActionAI actions[] = new ActionAI[20];
  int actionsFE = 0;
  public static List<Pellet> myBigPellets = new ArrayList<>();
  Set<Pos> visited = new HashSet<>(10);
  
  
  public Pacman(int index) {
    this.index = index;
    for (int i=0;i<20;i++) {
      actions[i] = new ActionAI();
    }
    actionsFE = 0;
  }
  
  public void initTurn() {
    pos = Pos.INVALID;
    cooldown = Math.max(0, cooldown-1);
    speedTurnsLeft = Math.max(0, speedTurnsLeft-1);
    
    mindTurn = Math.max(0, mindTurn-1);
  }

  
  public void generateMoves() {
    actionsFE = 0;
    for (int i=0;i<20;i++) {
      actions[i].score = 0;
    }
    
    actions[actionsFE].action = Action.MOVE;
    actions[actionsFE].target[0] = this.pos;
    actions[actionsFE].target[1] = this.pos;
    actionsFE++;
    

    visited.clear();
    for (int d=0;d<4;d++) {
      Pos next = pos.neighbors[d];
      if (Player.map.isWall(next)) continue;
      actions[actionsFE].action = Action.MOVE;
      actions[actionsFE].target[0] = next;
      actions[actionsFE].target[1] = next;
      actionsFE++;
      visited.add(next);
      
      if (speedTurnsLeft > 0) {
        // 2 moves
        for (int d2=0;d2<4;d2++) {
          Pos next2 = next.neighbors[d2];
          if (Player.map.isWall(next2)) continue;
          if (visited.contains(next2)) continue;
          visited.add(next2);
          actions[actionsFE].action = Action.MOVE;
          actions[actionsFE].target[0] = next;
          actions[actionsFE].target[1] = next2;
          actionsFE++;
        }
      }
    }

    if (cooldown == 0) {
      createStandingAction(Action.SWITCH_PAPER);
      createStandingAction(Action.SWITCH_SCISSOR);
      createStandingAction(Action.SWITCH_ROCK);
      createStandingAction(Action.SPEED);
    }
  }
  
  private void createStandingAction(Action action) {
    actions[actionsFE].action = action;
    actions[actionsFE].target[0] = this.pos;
    actions[actionsFE].target[1] = this.pos;
    actionsFE++;
  }

  @Override
  public String toString() {
    return "Pacman ("+index+")"+type+" @"+pos+" cd="+cooldown+" spdTurns="+speedTurnsLeft+" => "+mind;
  }

  public void update(Pos p, PacmanType type, int speedTurnsLeft, int cooldown) {
    if (type == PacmanType.DEAD) {
      this.pos = Pos.INVALID;
    } else {
      this.pos = p;
    }
    this.type = type;
    this.speedTurnsLeft = speedTurnsLeft;
    this.cooldown = cooldown;
  }
  
  public String output() {
    if (pos == Pos.INVALID) return "";
    return order.toOuput(this)+" "+myMindOutput();
  }

  private String myMindOutput() {
    switch( mind) {
    case FRIGHTEN: return "惧";
    case HUNGRY: return "●";
    case HUNTING: return "🞜";
    default: return "";
    }
  }

  public void debugScores() {
    System.err.println("Score for pacman #"+index);
    for (int i=0;i<actionsFE;i++) {
      System.err.println("   "+actions[i]);
    }
  }
  
  public void updateMind(State state) {
    generateMoves();
    
    if (mindTurn == 0) {
      mind = Mind.HUNGRY;
      afraidOf = -1;
    }
    for (int i = 5; i < 5 + state.maxPacmen; i++) {
      Pacman other = state.pacmen[i];
      if (other.isDead())
        continue;
      if (other.pos == Pos.INVALID)
        continue;
      if (Player.DEBUG_PACMIND)
        System.err.println("comparing my " + this.index + " with opp " + other.index);
      if (other.pos.distance(this.pos) <= 4) {
        if (Player.DEBUG_PACMIND)
          System.err.println("   is near me > so we should be scary");
        this.mind = Mind.FRIGHTEN;
        this.mindTurn = 2;
        this.afraidOf = i;
      }
    }
    if (mind == Mind.FRIGHTEN) {
      if (state.pacmen[afraidOf].pos == Pos.INVALID) {
        Player.oracle.updateAfraidOffOnWorseCaseScenario(this, state.pacmen[afraidOf]);
      }
      
      doQuickResolution(state);
    } else {
      for (int i=0;i<actionsFE;i++) {
        switch(actions[i].action) {
        case MOVE:
          actions[i].score = 100;
          break;
        case SPEED:
          actions[i].score = 1000;
          break;
        case SWITCH_PAPER:
          actions[i].score = -10_000;
          break;
        case SWITCH_ROCK:
          actions[i].score = -10_000;
          break;
        case SWITCH_SCISSOR:
          actions[i].score = -10_000;
          break;
        case WAIT:
          actions[i].score = -10_000;
          break;
        default:
          break;
        }
      }
    }
  }
  private void doQuickResolution(State state) {
    long start = System.currentTimeMillis();
    MMNode root = Player.minimax.searchMinimizing(state, Minimax.PESSIMISTIC, this, state.pacmen[afraidOf]);
    
    if (Player.debugPacmanMinimaxResult()) {
      System.err.println("Doing fast resolution of afraid pacman "+ index+" against "+afraidOf);
      System.err.println("afraidOf is "+state.pacmen[afraidOf]);
      System.err.println("Time to minimax "+(System.currentTimeMillis() - start));
      System.err.println("Scores  :");
    }
    
    for (MMNode child : root.getChilds()) {
      if (Player.debugPacmanMinimaxResult() || (Player.turn == 4 && index == 2)) {
        System.err.println("    "+child.toString(0)+" = "+child.score);
      }

      // update scores
      if (child.action == MMNode.ACTION_SPEED) {
        find(Action.SPEED).score = normalize(child.score);
      } else if (child.action == MMNode.ACTION_MOVE || child.action == MMNode.ACTION_MOVE2) {
        find(Action.MOVE, child.pos[0][1], child.pos[0][2]).score = normalize(child.score);
      } else if (child.action == MMNode.ACTION_WAIT) {
        find(Action.MOVE, pos, pos).score = normalize(child.score) - 20; // malus to wait

      } else if (child.action == MMNode.ACTION_SWITCH) {
        if (child.type[0] == PacmanType.PAPER) {
          find(Action.SWITCH_PAPER).score = normalize(child.score);
        }
        if (child.type[0] == PacmanType.ROCK) { 
          find(Action.SWITCH_ROCK).score = normalize(child.score);
        }
        if (child.type[0] == PacmanType.SCISSORS) { 
          find(Action.SWITCH_SCISSOR).score = normalize(child.score);
        }
      }
    }
  }

  private double normalize(double score) {
    if (score > 6000) {
      return  score; // sure win
    } else if (score < -6000) {
      return score; // sure lose
    } else if (score > 300) {
      return 0.001;
    } else if (score < -300) {
      return -0.001;
    } else {
      return 0;
    }
  }

  public ActionAI find(Action action) {
    for (int i=0;i<actionsFE;i++) {
      if (actions[i].action == action) {
        return actions[i];
      }
    }
    return null;
  }

  public ActionAI find(Action action, Pos one, Pos two) {
    for (int i=0;i<actionsFE;i++) {
      if (actions[i].action == action && actions[i].target[0] == one && actions[i].target[1] == two) {
        return actions[i];
      }
    }
    System.err.println("Can't find "+action+" "+one +" "+two);
    debugScores();
    return null;
  }

  public boolean isDead() {
    return type == PacmanType.DEAD;
  }

  /* return the pos 2 cells away for the target */
  Pos[] shortestPath = new Pos[2];
  public Pos[] getShortestRouteTo(Pos target) {
    shortestPath[0] = pos.getClosestCellToDist(target);
    shortestPath[1] = shortestPath[0].getClosestCellToDist(target); 
    return shortestPath;
  }

  public ActionAI chooseOrder() {
    ActionAI best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<actionsFE;i++) {
      // System.err.println("action for pacman #"+index+" is "+actions[i]);

      if (actions[i].score > bestScore) {
        bestScore = actions[i].score;
        best = actions[i];
      }
    }
    
    best.toOrder(this.order);

    return best;
  }
  
}
