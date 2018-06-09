package thales;

public class Team {
  public double depX;
  public double depY;

  public UFO ufos[] = new UFO[2];
  public Flag flag;

  public int score;
  

  public int _score;

  public Team() {
  }
  
  public void create(Team otherTeam) {
    flag = new Flag(this, otherTeam);
    ufos[0] = new UFO(this, otherTeam, 0);
    ufos[1] = new UFO(this, otherTeam, 1);
  }
  
  public void backup() {
    ufos[0].backup();
    ufos[1].backup();
    flag.backup();
    _score = score;
  }

  public void restore() {
    ufos[0].restore();
    ufos[1].restore();
    flag.restore();
    score = _score;
  }
}
