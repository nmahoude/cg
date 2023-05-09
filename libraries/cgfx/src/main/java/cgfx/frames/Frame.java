package cgfx.frames;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents Data from a game turn
 * /!\ can be either player !
 * 
 * @author nmahoude
 *
 */
public class Frame {
  int agentId = -1;
  String stderr = "";
  String stdout = "";
  
  
  public String cleanStderr() {
    return Stream.of(stderr.split("\n"))
        .map(String::trim)
        .filter(s -> s.length() != 0 && s.charAt(0) == '^')
        .map(s -> s.replace("^", " ").concat("")) // remove ^
        .collect(Collectors.joining());
  }
  
  public int agentId() {
    return agentId;
  }
  
  public String stderr() {
    return stderr;
  }
  
  public String stdout() {
    return stdout;
  }
  
  
  public static Frame fromInput(String input) {
    Frame f = new Frame();
    f.agentId = 0;
    f.stdout = "-?-";
    f.stderr = input;
    return f;
  }
}