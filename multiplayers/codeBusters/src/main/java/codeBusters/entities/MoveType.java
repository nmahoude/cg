package codeBusters.entities;

import codeBusters.P;

public enum MoveType {
	WAIT {
		void output(P target, int targetId) {
			System.out.println("RELEASE WAIT");
		}
	},
	MOVE {
		void output(P target, int targetId) {
			System.out.println("MOVE"+target);
		}
	}, 
	BUST {
		void output(P target, int targetId) {
			System.out.println("BUST "+targetId);
		}
	}, 
	RELEASE {
		void output(P target, int targetId) {
			System.out.println("RELEASE");
		}
	}, 
	STUN {
		void output(P target, int targetId) {
			System.out.println("STUN "+targetId);
		}
	}, 
	RADAR {
		void output(P target, int targetId) {
			System.out.println("RADAR");
		}
	}, 
	EJECT {
		void output(P target, int targetId) {
			System.out.println("EJECT"+target);
		}
	};

	abstract void output(P target, int targetId);
}
