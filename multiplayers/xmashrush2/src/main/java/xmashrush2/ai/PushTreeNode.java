package xmashrush2.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xmashrush2.BFS;
import xmashrush2.Pos;
import xmashrush2.PushAction;
import xmashrush2.State;

public class PushTreeNode {
	static BFS bfs = new BFS();

	State state = new State();
	List<Pos> potentialPos = new ArrayList<>();
	PushTreeNode parent;

	public PushAction actionFromParent;
	public List<Pos> reachableQuestItems = new ArrayList<>();
	
	@Override
	public String toString() {
		return actionFromParent.toString()+" reachableItems : "+reachableQuestItems+" pos : "+potentialPos;
	}
	
	public void initFrom(State state) {
		initFrom(state, 20);
	}
	
	public void initFrom(State state, int maxSteps) {
		this.state.copyFrom(state);
		this.actionFromParent = null;
		this.parent = null;
		
		this.potentialPos.clear();
		// find all reachable positions
		bfs.process(state, state.agents[0].pos, 20);
		for (int i=0;i<49;i++) {
			if (bfs.gScore[i]  > maxSteps) continue;
			potentialPos.add(Pos.from(i));
		}
	}

	public List<PushTreeNode> simulateAllPush() {
		List<PushTreeNode> childs = new ArrayList<>();
		
		for (PushAction pa : PushAction.actions) {
			PushTreeNode child = PushTreeAI.getFromCache();
			child.parent = this;
			child.actionFromParent = pa;
			child.state.copyFrom(this.state);
			child.state.apply(pa,  null);
			
			child.updatePotentialPos(this.potentialPos, pa);
			child.calculateNewPotentialPos();
			
			childs.add(child);
		}
		
		return childs;
	}

	private void calculateNewPotentialPos() {
		bfs.process(this.state, this.potentialPos);
		
		this.potentialPos.clear();
		this.reachableQuestItems.clear();
		for (int i=0;i<49;i++) {
			if (bfs.gScore[i]  == Integer.MAX_VALUE) continue;
			
			if (this.state.agents[0].needs(this.state.items[i])) {
				this.reachableQuestItems.add(Pos.from(i));
			}
			potentialPos.add(Pos.from(i));
		}
	}

	public List<Pos> findStartingPosFromParentToReach(Pos target, int maxSteps) {
		return findStartingPosFromParentToReach(Arrays.asList(target), maxSteps);
	}
	
	public List<Pos> findStartingPosFromParentToReach(List<Pos> targets, int maxSteps) {
		bfs.process(state, targets, maxSteps);
		List<Pos> reachablePosToTarget = new ArrayList<>();
		
		for (int i=0;i<49;i++) {
			if (bfs.gScore[i] == Integer.MAX_VALUE) continue;
			if (potentialPos.contains(Pos.from(i))) {
				reachablePosToTarget.add(Pos.from(i));
			}
		}
		System.err.println("Reachable pos to get to target : "+reachablePosToTarget);
		List<Pos> filteredParentPosToTarget = new ArrayList<>();
		for (Pos pos : reachablePosToTarget) {
			
			Pos positionBefore = pos.unapplyPushOnPos(this.actionFromParent);
			if (parent.potentialPos.contains(positionBefore)) {
				filteredParentPosToTarget.add(positionBefore);
			} else {
			}
		}
		System.err.println("before action "+this.actionFromParent+", I should be in "+filteredParentPosToTarget);
		
		return filteredParentPosToTarget;
	}
	
	private void updatePotentialPos(List<Pos> initialPositions, PushAction pa) {
		potentialPos.clear();
		for (Pos p : initialPositions) {
			potentialPos.add(p.applyPushOnPos(pa));
		}
	}

	public PushAction getInitialPushAction() {
		PushTreeNode current = this;
		while (current.parent.parent != null) {
			current = current.parent;
		}
		return current.actionFromParent;
	}
}