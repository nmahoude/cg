package ooc.minimax;

import java.util.BitSet;

import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.trailmapper.TrailNode;

public class MMNode {
	private static int MAX = 2_000;
	static MMNode cache[] = new MMNode[MAX];
	public static int cacheFE = 0;
	
	static {
		for (int i=0;i<MAX;i++) {
			cache[i] = new MMNode();
		}
	}
	
	P pos[] = new P[2];
	BitSet trails[] = new BitSet[2];
	int torpedoDelai[] = new int[2];
	int life[] = new int[2];
	
	MMNode childs[] = new MMNode[4];
	int childsFE = 0;
	
	public int score;
  MMNode parent;
  int dir;
	
	public MMNode() {
		for (int i=0;i<2;i++) {
			pos[i] = P.I;
			trails[i] = new BitSet(225);
			torpedoDelai[i] = 0;
			life[i] = 6;
		}
	}
	
	static void resetCache() {
		cacheFE = 0;
	}
	
	/** mini simulation */
	MMNode move(int player, int dir, int depth) {
		MMNode child = cache[cacheFE++];
		child.copyFrom(this);
		child.parent = this;
		child.dir = dir;

		child.pos[player] = child.pos[player].neighbors[dir];
		child.trails[player].set(child.pos[player].o);
		child.torpedoDelai[player]--;
		
		if (child.torpedoDelai[player] <= 0) {
			int potDamage = Player.map.possibleDamageFromPosition(child.pos[player], child.pos[1-player]);
			child.life[1-player] -= potDamage;
			
			int dist = Player.map.blastDistance(child.pos[player],child.pos[1-player]);

			
			if (dist == 0) {
				child.life[player] -= 2;
			} else if (dist == 1) {
				child.life[player] -= 1;
			}
			child.torpedoDelai[player] = 2;
		}
		
		this.childs[childsFE++] = child;
		return child;
	}

	private void copyFrom(MMNode model) {
		for (int i=0;i<2;i++) {
			pos[i] = model.pos[i];
			trails[i].clear();
			trails[i].or(model.trails[i]);
			torpedoDelai[i] = model.torpedoDelai[i];
			life[i] = model.life[i];
		}
		childsFE = 0;
	}

	public static MMNode pop() {
		MMNode node = cache[cacheFE++];
		node.childsFE = 0;
		return node;
	}

	public void initFrom(State state) {
		pos[0] = state.myPos;
		trails[0] = state.visitedCells; // can do a pointeur copy, will not be modified
    trails[0].set(pos[0].o);
		torpedoDelai[0] = state.cooldowns.torpedoCooldown();
		life[0] = state.myLife;
		
		TrailNode oppInfo = Player.oracle.oppMapper.currentLayer[0];
		pos[1] = oppInfo.currentPos;
		trails[1] = oppInfo.trail;
		trails[1].set(pos[1].o);
		torpedoDelai[1] = Player.oracle.chargeDetector.delaiToTorpedo;
		life[1] = state.oppLife;
	}
	
}
