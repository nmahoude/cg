package csb;

public class Team {
  public int timeout;
  private int b_timeout;
  
  public void backup() {
    b_timeout = timeout;
  }
  public void restore() {
    timeout = b_timeout;
  }
}
