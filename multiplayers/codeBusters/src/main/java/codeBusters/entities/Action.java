package codeBusters.entities;

import codeBusters.P;

public class Action {
	private static final Action WAIT = new Action();
	static {
		WAIT.type = MoveType.WAIT;
	};
	private static final Action RELEASE = new Action();
	static {
		RELEASE.type = MoveType.RELEASE;	
	};
	
	public MoveType type;
	P target;
	public Ghost ghost;
	public Buster buster;
	
	
	public void output() {
		type.output(target, ghost != null ? ghost.id : buster != null ? buster.id : -1);
	}

	public static Action doWait() {
		return WAIT;
	}	

	public static Action release() {
		return RELEASE;
	}	

	public static Action move(P target) {
		Action action = new Action();
		action.type = MoveType.MOVE;
		action.target = target;
		return action;
	}

	public static Action stun(Buster target) {
		Action action = new Action();
		action.type = MoveType.STUN;
		action.buster = target;
		return action;
	}

	public static Action bust(Ghost ghost) {
		Action action = new Action();
		action.type = MoveType.BUST;
		action.ghost = ghost;
		return action;
	}

	public static Action eject(P target) {
		Action action = new Action();
		action.type = MoveType.EJECT;
		action.target = target;
		return action;
	}

	
}
