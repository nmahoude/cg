package lcm.sim;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Card;
import lcm.fixtures.StateFixture;

/**
 * end to end games to check simulation
 * 
 * @author nmahoude
 *
 */
public class SimulationEndToEndTest extends BattleMode {

  @Test
  public void first() throws Exception {
    // 1st turn
    
    
    String input = "30 1 25 25" + NL + "30 0 25 25" + NL + "5 5" + NL +
        "0 1  0 2 0 0 0 63     0 0 0" + NL +
        "0 3  0 0 4 3 3 4      0 -1 0" + NL +
        "0 5  0 0 2 1 4 4      0 0 0" + NL +
        "0 7  0 0 2 3 2 4      0 0 0" + NL +
        "0 9  0 0 2 0 4 36     0 0 0" + NL +
        "";
    
    state.read(new Scanner(input));
    State current = state;
    List<Action> actions = new ArrayList<>();

    actions.clear();
    actions.add(Action.pass());
    current = sim.simulate(current, actions);
    
    // him
    actions.clear();
    actions.add(Action.pass());
    current = sim.simulate(current, actions);
    
    current = checkState(current, 
        "30 2 24 25"+NL+"30 1 24 25"+NL+"6 6"+NL+
        "0 1  0 2 0 0 0 63     0 0 0"+NL+
        "0 3  0 0 4 3 3 4      0 -1 0"+NL+
        "0 5  0 0 2 1 4 4      0 0 0"+NL+
        "0 7  0 0 2 3 2 4      0 0 0"+NL+
        "0 9  0 0 2 0 4 36     0 0 0"+NL+
        "0 11 0 0 6 5 7 0      0 0 1"+NL+
        "");
    
    // me
    actions.clear();
    actions.add(Action.summon(state.card(9)));
    current = sim.simulate(current, actions);
    
    // him
//    actions.clear();
//    actions.add(Action.summon(CardFixture.addHand(12, state)));
//    current = sim.simulate(current, actions);
//    
//    current = checkState(current, 
//        "30 3 23 25"+NL+"30 2 23 25"+NL+"6 8"+NL+
//        "0 1  0 2 0 0 0 63     0 0 0"+NL+
//        "0 3  0 0 4 3 3 4      0 -1 0"+NL+
//        "0 5  0 0 2 1 4 4      0 0 0"+NL+
//        "0 7  0 0 2 3 2 4      0 0 0"+NL+
//        "0 11 0 0 6 5 7 0      0 0 1"+NL+
//        "0 13 0 0 4 4 3 0      0 0 1"+NL+
//        "0 9  1 0 2 0 4 36     0 0 0"+NL+
//        "0 12 -1 0 2 2 2 32     0 0 0"+NL
//        );
        
    
  }

  private State checkState(State current, String input) {
    State c = StateFixture.createBattleState();
    c.read(new Scanner(input));
    
    assertThat(current.me.health, is(c.me.health));
    assertThat(current.opp.health, is(c.opp.health));
    
    assertThat(current.cardsFE + 1 , is(c.cardsFE)); // one draw card
    for (int i=0;i<current.cardsFE;i++) {
      Card c1 = current.cards[i];
      Card c2 = c.card(c1.id); // may not be in same order
      
      assertThat(c1.id, is(c2.id));
      assertThat(c1.attack, is(c2.attack));
      assertThat(c1.defense, is(c2.defense));
      assertThat(c1.abilities, is(c2.abilities));
    }
    return c;
  }

  /*
Standard Error Stream:
"30 1 25 25"+NL+"30 0 25 25"+NL+"5 5"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 5  0 0 2 1 4 4      0 0 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 9  0 0 2 0 4 36     0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
AI think in 93 ms
Iterations : 117760
Standard Output Stream:
063
143
Standard Output Stream:
PASS 0
064
143
Standard Error Stream:
"30 2 24 25"+NL+"30 1 24 25"+NL+"6 6"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 5  0 0 2 1 4 4      0 0 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 9  0 0 2 0 4 36     0 0 0"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(9)));
AI think in 95 ms
Iterations : 52736
Standard Output Stream:
SUMMON 9 ;
065
143
066
143
Game Summary:
Player nmahoude performed action: SUMMON 9
Standard Output Stream:
SUMMON 12 140
067
143
068
143
Game Summary:
Player ClosetAI performed action: SUMMON 12
Standard Error Stream:
"30 3 23 25"+NL+"30 2 23 25"+NL+"6 8"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 5  0 0 2 1 4 4      0 0 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 9  1 0 2 0 4 36     0 0 0"+NL+
"0 12 -1 0 2 2 2 32     0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(5)));
AI think in 95 ms
Iterations : 32256
Standard Output Stream:
SUMMON 5 ;
069
143
070
143
Game Summary:
Player nmahoude performed action: SUMMON 5
Standard Output Stream:
SUMMON 4;ATTACK 12 5;ATTACK 4 5 430
071
143
072
143
Game Summary:
Player ClosetAI performed action: SUMMON 4
073
143
Game Summary:
Player ClosetAI performed action: ATTACK 12 5
074
143
Game Summary:
Player ClosetAI performed action: ATTACK 4 5
Standard Error Stream:
"30 4 22 25"+NL+"30 3 22 25"+NL+"6 9"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 15 0 0 4 4 4 1      4 0 0"+NL+
"0 9  1 0 2 0 4 36     0 0 0"+NL+
"0 12 -1 0 2 2 2 0      0 0 0"+NL+
"0 4  -1 0 3 2 2 2      0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(15)));
AI think in 95 ms
Iterations : 41984
Standard Output Stream:
SUMMON 15 ;
075
143
076
143
Game Summary:
Player nmahoude performed action: SUMMON 15
Standard Output Stream:
USE 18 9;ATTACK 12 9;ATTACK 4 9;SUMMON 8 785
077
143
078
143
Game Summary:
Player ClosetAI performed action: USE 18 9
079
143
Game Summary:
Player ClosetAI performed action: ATTACK 12 9
080
143
Game Summary:
Player ClosetAI performed action: ATTACK 4 9
081
143
Game Summary:
Player ClosetAI performed action: SUMMON 8
Standard Error Stream:
"34 5 21 25"+NL+"30 4 21 25"+NL+"5 10"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 17 0 0 4 9 1 0      0 0 0"+NL+
"0 15 1 0 4 4 4 1      4 0 0"+NL+
"0 12 -1 0 2 2 2 0      0 0 0"+NL+
"0 4  -1 0 3 2 2 2      0 0 0"+NL+
"0 8  -1 0 4 4 3 0      0 0 1"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(17)));
    actions.add(Action.attack(state.card(15),state.card(4)));
AI think in 95 ms
Iterations : 56832
Standard Output Stream:
SUMMON 17 ;ATTACK 15 4 ;
082
143
083
143
Game Summary:
Player nmahoude performed action: SUMMON 17
084
143
Game Summary:
Player nmahoude performed action: ATTACK 15 4
Standard Output Stream:
ATTACK 12 17;ATTACK 8 -1;SUMMON 16;SUMMON 20 1649
085
143
086
143
Game Summary:
Player ClosetAI performed action: ATTACK 12 17
087
143
Game Summary:
Player ClosetAI performed action: ATTACK 8 -1
088
143
Game Summary:
Player ClosetAI performed action: SUMMON 16
089
143
Game Summary:
Player ClosetAI performed action: SUMMON 20
Standard Error Stream:
"30 6 20 25"+NL+"28 5 19 25"+NL+"5 10"+NL+
"0 1  0 2 0 0 0 63     0 0 0"+NL+
"0 3  0 0 4 3 3 4      0 -1 0"+NL+
"0 7  0 0 2 3 2 4      0 0 0"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 19 0 0 7 7 7 0      1 -1 0"+NL+
"0 15 1 0 4 4 2 1      0 0 0"+NL+
"0 8  -1 0 4 4 3 0      0 0 0"+NL+
"0 16 -1 0 2 3 2 4      0 0 0"+NL+
"0 20 -1 0 2 2 2 32     0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.use(state.card(1),state.card(16)));
    actions.add(Action.attack(state.card(15),Card.opponent));
    actions.add(Action.summon(state.card(3)));
    actions.add(Action.summon(state.card(7)));
AI think in 97 ms
Iterations : 12800
Standard Output Stream:
USE 1 16 ;ATTACK 15 -1 ;SUMMON 3 ;SUMMON 7 ;
090
143
091
143
Game Summary:
Player nmahoude performed action: USE 1 16
092
143
Game Summary:
Player nmahoude performed action: ATTACK 15 -1
093
143
Game Summary:
Player nmahoude performed action: SUMMON 3
094
143
Game Summary:
Player nmahoude performed action: SUMMON 7
Standard Output Stream:
SUMMON 6;USE 24 15;ATTACK 6 3;ATTACK 20 7;ATTACK 16 -1;ATTACK 8 -1 2767
095
143
096
143
Game Summary:
Player ClosetAI performed action: SUMMON 6
097
143
Game Summary:
Player ClosetAI performed action: USE 24 15
098
143
Game Summary:
Player ClosetAI performed action: ATTACK 6 3
099
143
Game Summary:
Player ClosetAI performed action: ATTACK 20 7
100
143
Game Summary:
Player ClosetAI performed action: ATTACK 16 -1
101
143
Game Summary:
Player ClosetAI performed action: ATTACK 8 -1
Standard Error Stream:
"23 7 18 20"+NL+"23 6 17 20"+NL+"5 9"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 19 0 0 7 7 7 0      1 -1 0"+NL+
"0 21 0 0 2 1 4 4      0 0 0"+NL+
"0 23 0 0 4 7 4 0      0 0 0"+NL+
"0 8  -1 0 4 4 3 0      0 0 0"+NL+
"0 16 -1 0 2 3 2 0      0 0 0"+NL+
"0 20 -1 0 2 2 2 0      0 0 0"+NL+
"0 6  -1 0 5 4 1 2      0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(23)));
    actions.add(Action.summon(state.card(21)));
AI think in 96 ms
Iterations : 115200
Standard Output Stream:
SUMMON 23 ;SUMMON 21 ;
102
143
103
143
Game Summary:
Player nmahoude performed action: SUMMON 23
104
143
Game Summary:
Player nmahoude performed action: SUMMON 21
Standard Output Stream:
ATTACK 8 21;ATTACK 6 23;ATTACK 20 -1;ATTACK 16 -1;SUMMON 2;SUMMON 28 3520
105
143
106
143
Game Summary:
Player ClosetAI performed action: ATTACK 8 21
107
143
Game Summary:
Player ClosetAI performed action: ATTACK 6 23
108
143
Game Summary:
Player ClosetAI performed action: ATTACK 20 -1
109
143
Game Summary:
Player ClosetAI performed action: ATTACK 16 -1
110
143
Game Summary:
Player ClosetAI performed action: SUMMON 2
111
143
Game Summary:
Player ClosetAI performed action: SUMMON 28
Standard Error Stream:
"18 8 16 15"+NL+"23 7 16 20"+NL+"4 10"+NL+
"0 11 0 0 6 5 7 0      0 0 1"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 19 0 0 7 7 7 0      1 -1 0"+NL+
"0 25 0 2 0 -1 -1 0      0 0 0"+NL+
"0 27 0 0 2 0 5 4      0 0 0"+NL+
"0 8  -1 0 4 4 2 0      0 0 0"+NL+
"0 16 -1 0 2 3 2 0      0 0 0"+NL+
"0 20 -1 0 2 2 2 0      0 0 0"+NL+
"0 2  -1 0 3 2 3 12     0 0 0"+NL+
"0 28 -1 0 2 2 2 32     0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.use(state.card(25),state.card(2)));
    actions.add(Action.summon(state.card(11)));
    actions.add(Action.summon(state.card(27)));
AI think in 95 ms
Iterations : 39936
Standard Output Stream:
USE 25 2 ;SUMMON 11 ;SUMMON 27 ;
112
143
113
143
Game Summary:
Player nmahoude performed action: USE 25 2
114
143
Game Summary:
Player nmahoude performed action: SUMMON 11
115
143
Game Summary:
Player nmahoude performed action: SUMMON 27
Standard Output Stream:
ATTACK 2 27;ATTACK 20 27;ATTACK 28 27;ATTACK 16 11;ATTACK 8 11;SUMMON 22 3970
116
143
117
143
Game Summary:
Player ClosetAI performed action: ATTACK 2 27
118
143
Game Summary:
Player ClosetAI performed action: ATTACK 20 27
119
143
Game Summary:
Player ClosetAI performed action: ATTACK 28 27
120
143
Game Summary:
Player ClosetAI performed action: ATTACK 16 11
121
143
Game Summary:
Player ClosetAI performed action: ATTACK 8 11
122
143
Game Summary:
Player ClosetAI performed action: SUMMON 22
Standard Error Stream:
"18 9 14 15"+NL+"24 8 15 20"+NL+"4 8"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 19 0 0 7 7 7 0      1 -1 0"+NL+
"0 29 0 0 4 3 2 3      0 0 0"+NL+
"0 31 0 0 8 8 8 5      0 0 1"+NL+
"0 20 -1 0 2 2 2 0      0 0 0"+NL+
"0 2  -1 0 3 1 2 12     0 0 0"+NL+
"0 28 -1 0 2 2 2 32     0 0 0"+NL+
"0 22 -1 0 8 8 8 5      0 0 1"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(31)));
AI think in 95 ms
Iterations : 72192
Standard Output Stream:
SUMMON 31 ;
123
143
124
143
Game Summary:
Player nmahoude performed action: SUMMON 31
Standard Output Stream:
USE 34 2;ATTACK 2 31;ATTACK 20 -1;ATTACK 28 -1;ATTACK 22 -1;SUMMON 10;SUMMON 32 5716
125
143
126
143
Game Summary:
Player ClosetAI performed action: USE 34 2
127
143
Game Summary:
Player ClosetAI performed action: ATTACK 2 31
128
143
Game Summary:
Player ClosetAI performed action: ATTACK 20 -1
129
143
Game Summary:
Player ClosetAI performed action: ATTACK 28 -1
130
143
Game Summary:
Player ClosetAI performed action: ATTACK 22 -1
131
143
Game Summary:
Player ClosetAI performed action: SUMMON 10
132
143
Game Summary:
Player ClosetAI performed action: SUMMON 32
Standard Error Stream:
"6 10 10 5"+NL+"25 9 13 20"+NL+"3 13"+NL+
"0 13 0 0 4 4 3 0      0 0 1"+NL+
"0 19 0 0 7 7 7 0      1 -1 0"+NL+
"0 29 0 0 4 3 2 3      0 0 0"+NL+
"0 33 0 0 3 2 5 0      0 0 0"+NL+
"0 35 0 0 6 4 7 4      0 0 0"+NL+
"0 37 0 0 2 1 5 0      0 0 0"+NL+
"0 39 0 2 0 0 0 63     0 0 0"+NL+
"0 20 -1 0 2 2 2 0      0 0 0"+NL+
"0 2  -1 0 3 1 2 28     0 0 0"+NL+
"0 28 -1 0 2 2 2 32     0 0 0"+NL+
"0 22 -1 0 8 8 8 5      0 0 0"+NL+
"0 10 -1 0 3 2 3 12     0 0 0"+NL+
"0 32 -1 0 2 2 3 4      0 0 0"+NL+
"";
    state.read(new Scanner(input));
    List<Action> actions = new ArrayList<>();
    actions.add(Action.summon(state.card(29)));
    actions.add(Action.summon(state.card(35)));
    actions.add(Action.attack(state.card(29),state.card(10)));
    actions.add(Action.use(state.card(39),state.card(22)));
AI think in 103 ms
Iterations : 28160
Standard Output Stream:
SUMMON 29 ;SUMMON 35 ;ATTACK 29 10 ;USE 39 22 ;
133
143
134
143
Game Summary:
Player nmahoude performed action: SUMMON 29
135
143
Game Summary:
Player nmahoude performed action: SUMMON 35
136
143
Game Summary:
Player nmahoude performed action: ATTACK 29 10
137
143
Game Summary:
Player nmahoude performed action: USE 39 22
Standard Output Stream:
ATTACK 22 35;ATTACK 2 -1;ATTACK 20 -1;ATTACK 28 -1;ATTACK 32 -1;SUMMON 26 6312
138
143
139
143
Game Summary:
Player ClosetAI performed action: ATTACK 22 35
140
143
Game Summary:
Player ClosetAI performed action: ATTACK 2 -1
141
143
Game Summary:
Player ClosetAI performed action: ATTACK 20 -1
142
143
Game Summary:
Player ClosetAI performed action: ATTACK 28 -1
143
143
Game Summary:
   */
}
