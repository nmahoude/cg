package pac.minimax;

import java.util.ArrayList;
import java.util.List;

import pac.State;
import pac.agents.Pacman;
import pac.agents.PacmanType;
import pac.map.Pos;

public class MMNode {
  public static final int ACTION_WAIT = 0;
	public static final int ACTION_MOVE = 1;
  public static final int ACTION_SWITCH = 2;
  public static final int ACTION_SPEED = 3;
  public static final int ACTION_NO = 4;
  public static final int ACTION_MOVE2 = 5;

	
	private static int MAX = 500_000;
	static MMNode cache[] = new MMNode[MAX];
	public static int cacheFE = 0;
	
	static {
		for (int i=0;i<MAX;i++) {
			cache[i] = new MMNode();
		}
	}

	// data
	public Pos pos[][] = new Pos[2][3];
	public PacmanType type[] = new PacmanType[2];
	int speed[] = new int[2];
	int cooldown[] = new int[2];
	boolean maximizer;
	
	// structure
	List<MMNode> childs = new ArrayList<>(9);
  MMNode parent;
  public int action;
  public double score;
	
	public MMNode() {
	}
	
	static void resetCache() {
		cacheFE = 0;
	}
	
	private void copyFrom(MMNode model) {
		maximizer = !model.maximizer;
		
		for (int i=0;i<2;i++) {
			if (maximizer) {
				pos[i][0] = model.pos[i][2]; // acknowledge finished positions of last turn (both pacman)
				pos[i][1] = model.pos[i][2];
				pos[i][2] = model.pos[i][2];
			} else {
				pos[i][0] = model.pos[i][0]; // keep old positions to check collisions
				pos[i][1] = model.pos[i][1]; // keep old positions to check collisions
				pos[i][2] = model.pos[i][2]; // keep old positions to check collisions
			}
			type[i] = model.type[i];
			speed[i] = model.speed[i];
			cooldown[i] = model.cooldown[i];
		}
		childs.clear();
		parent = model;
	}

	public static MMNode pop() {
		MMNode node = cache[cacheFE++];
		node.childs.clear();
		return node;
	}

	public void initFrom(State state, Pacman me, Pacman opp) {
	  action = -1;
		pos[0][0] = me.pos;
    pos[0][1] = me.pos;
    pos[0][2] = me.pos;
		type[0] = me.type;
		speed[0] = me.speedTurnsLeft;
		cooldown[0] = me.cooldown;

		pos[1][0] = opp.pos;
    pos[1][1] = opp.pos;
    pos[1][2] = opp.pos;
    type[1] = opp.type;
    speed[1] = opp.speedTurnsLeft;
    cooldown[1] = opp.cooldown;
    
    maximizer = false; // root is not maximizer
	}

  public List<MMNode> getChild(boolean player0, int depth) {
    int index = player0 ? 0 : 1;

    // wait
    MMNode wait = pop();
    wait.copyFrom(this);
    wait.action = ACTION_WAIT;
    this.childs.add(wait);
    
    if (cooldown[index] == 0) {
      // switches
      MMNode swt;
      swt = switchTo(index, PacmanType.PAPER);   
      if (swt != null) this.childs.add(swt);
      swt = switchTo(index, PacmanType.ROCK);    
      if (swt != null) this.childs.add(swt);
      swt = switchTo(index, PacmanType.SCISSORS);
      if (swt != null) this.childs.add(swt);
    }

    if (cooldown[index] == 0) {
      // speed
      MMNode speed = pop();
      speed.copyFrom(this);
      speed.cooldown[index] = 10;
      speed.speed[index] = 5;
      speed.action = ACTION_SPEED;
      this.childs.add(speed);
    }
    
    for (int d=0;d<4;d++) {
      Pos nextPos = pos[index][2].neighbors[d]; // last pos of previous node
      if (nextPos == Pos.INVALID) continue;
      
      MMNode child;
      child = pop();
      child.copyFrom(this);
      // one move
      child.pos[index][1] = nextPos;
      child.pos[index][2] = nextPos;
      child.action = ACTION_MOVE;
      this.childs.add(child);
      
      if (speed[index] > 0) {
        // second layer of positions
        for (int d2=0;d2<4;d2++) {
          Pos nextPos2 = nextPos.neighbors[d2];
          if (nextPos2 == Pos.INVALID) continue;
          if (nextPos2 == pos[index][0]) continue; // retour arriere en speed pas possible
          
          child = pop();
          child.copyFrom(this);
          child.pos[index][1] = nextPos;
          child.pos[index][2] = nextPos2;
          child.action = ACTION_MOVE2;
          this.childs.add(child);
        }
      }
    }
    
    return this.childs;
  }

  private MMNode switchTo(int index, PacmanType newType) {
    if (newType == type[index]) return null;
    
    MMNode swt = pop();
    swt.copyFrom(this);
    swt.type[index] = newType;
    swt.cooldown[index] = 10;
    swt.action = ACTION_SWITCH;
    return swt;
  }
	
  @Override
  public String toString() {
    return toString(maximizer ? 0 : 1);
  }
  
  public String toString(int index) {
    String order = "";
    switch (action) {
    case ACTION_WAIT : order = "WAIT"; break;
    case ACTION_MOVE : order = "MOVE "+ pos[index][1]; break;
    case ACTION_MOVE2 : order = "MOVE "+ pos[index][1] + "+"+pos[index][2]; break;
    case ACTION_SWITCH : order = "SWITCH "+type[index]; break;
    case ACTION_SPEED : order = "SPEED"; break;
    case ACTION_NO : order = "()"; break;
    }
    return order;
  }

  public void debug(int depth, boolean showLog) {
    for (MMNode child : childs) {
      if (showLog) {
        System.err.println(String.format("%"+((depth+1)*4)+"s", "") +(depth %2 == 0 ? " ME " : " OPP")+" evalutating "+child.toString(depth % 2)+" score = "+child.score);
      }
      child.debug(depth+1, showLog);
    }
  }
  
  public String getBestChild(int depth, boolean maximizing) {
    if (maximizing) {
      MMNode best = null;
      for (MMNode child : childs) {
        if (best == null || child.score > best.score) {
          best = child;
        }
      }
      if (best != null) {
      	return best.toString(maximizing ? 0 : 1) + " => " + best.getBestChild(depth+1, !maximizing);
      } else {
      	return " END";
      }
    } else {
      MMNode best = null;
      for (MMNode child : childs) {
        if (best == null || child.score < best.score) {
          best = child;
        }
      }
      if (best != null) {
      	return best.toString(maximizing ? 0 : 1) + " => " + best.getBestChild(depth+1, !maximizing);
      } else {
      	return " END";
      }
    }
  }

  public List<MMNode> getChilds() {
    return childs;
  }
}
