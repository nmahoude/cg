package gitc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

public class GameStateTest {

  @Test
  @Ignore
  public void readBackupLine() throws Exception {
    Scanner in = new Scanner("String to backup");
    
    GameState state = new GameState();
    state.read(in);
    
    assertThat(state.inputBackup, is("String to backup"));
  }
}
