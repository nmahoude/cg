package csb.entities;

public class Team {
  public int timeout;
  private int b_timeout;
  
  public void backup() {
    b_timeout = timeout;
  }
  public void restore() {
    timeout = b_timeout;
  }
  public void copyFrom(Team model) {
    this.timeout = model.timeout;
    this.b_timeout = model.b_timeout;
  }
}
