package lcm;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import lcm.fixtures.StateFixture;

public class AgentTest {

  private State state;
  private Agent agent;

  @Before
  public void setup() {
    state = StateFixture.createBattleState();
    agent = new Agent(state, 0);
  }
  
  @Test
  public void runes_removeOnePoint_noRunes() throws Exception {
    agent.modifyHealth(-1);

    assertThat(agent.health, is(29));
    assertThat(agent.rune, is(5));
    assertThat(agent.nextTurnDraw, is(1));
  }
  
  @Test
  public void runes_RemoveFirstRune() throws Exception {
    agent.modifyHealth(-6);
    
    assertThat(agent.health, is(24));
    assertThat(agent.rune, is(4));
    assertThat(agent.nextTurnDraw, is(2));
  }

  @Test
  public void runes_dontRemove2TimesTheFirstRune() throws Exception {
    agent.modifyHealth(-6);
    agent.modifyHealth(+6);
    agent.modifyHealth(-6);
    
    assertThat(agent.health, is(24));
    assertThat(agent.rune, is(4));
    assertThat(agent.nextTurnDraw, is(2));
  }

  @Test
  public void runes_Remove2RunesInOneShot() throws Exception {
    agent.modifyHealth(-11);
    
    assertThat(agent.health, is(19));
    assertThat(agent.rune, is(3));
    assertThat(agent.nextTurnDraw, is(3));
  }

  @Test
  public void runes_Remove2RunesIn2Shot() throws Exception {
    agent.modifyHealth(-6);
    agent.modifyHealth(-5);
    
    assertThat(agent.health, is(19));
    assertThat(agent.rune, is(3));
    assertThat(agent.nextTurnDraw, is(3));
  }
  
}
