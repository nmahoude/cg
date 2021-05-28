package codeBusters.entities;

import java.util.HashMap;
import java.util.Map;

import codeBusters.P;
import codeBusters.Player;

public class Buster extends Entity{
  public int team;
  public int state;
  public int value;
  public Ghost carried;
  public int stunCooldown = 0;
  public int stunned = 0;
  public boolean hasRadar = true;
	
  public Action action = Action.doWait();
	public int notSeenAround;
	public static Map<Ghost, Buster> ejectedGhosts = new HashMap<>();

	public boolean isInOwnBase() {
		return (team == 0 && Player.TEAM0_BASE.dist2(this.position) < Player.BASE_RANGE_2)
				|| (team == 1 && Player.TEAM1_BASE.dist2(this.position) < Player.BASE_RANGE_2)
				;
	}

	public boolean isInRange2(P myBase, int range2) {
		return this.position.dist2(myBase) <= range2;
	}
	
	@Override
	public String toString() {
		return String.format("Buster[%s] @%s", id, position);
	}

	public boolean canSee(Buster e) {
		return this.isInRange2(e.position, Player.FOG_DISTANCE_2);
	}

	public boolean inStunRange(Buster e) {
		return this.isInRange2(e.position, Player.STUN_RANGE_2);
	}

	public boolean canStun(Buster b) {
		return isInRange2(b.position, Player.STUN_RANGE_2);
	}

	public boolean hasStun() {
		return stunCooldown == 0;
	}
	
	public boolean myTeam() {
		return team == Player.myTeamId;
	}

	public boolean hisTeam() {
		return !myTeam();
	}

	public int turnsToReach(Ghost ghost) {
		return (int)Math.ceil(Math.max(0, this.position.dist(ghost.position) - Player.STUN_RANGE ) / Player.MOVE_DISTANCE);
	}

}
