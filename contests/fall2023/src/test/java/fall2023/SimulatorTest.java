package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;

public class SimulatorTest {

  
  
  @Nested
  static class UglyMoves {
    State state = new State();
    Simulator sim = new Simulator();
    Action[] actions;
    
    
    @BeforeEach
    public void setup() {
      actions = new Action[] { new Action(), new Action(), new Action(), new Action() };
    }
    
    @Test
    void normalMove() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 18 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 3418 3419 3420 3421 
*** OPTIONAL ***
^ 0 0
^ 4968 4316 0 17 176
^ 6850 3702 0 26 1136
^ 5576 4316 0 17 240
^ 3390 3760 0 27 240

^ 0

^ 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000
^ 0 0  
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 4544 4740 0 18
^ 2 5152 4740 0 18
^ 2
^ 1 7213 4180 0 27
^ 3 2823 3563 0 28
--- Current Scans ---
^ 15 0 5 0 4 0 7 2 4 2 5 2 6 2 7 1 10 1 4 1 5 1 6 3 4 3 5 3 6 3 7 
--- Fishes --- 
^ 3
^ 4 5153 5000 2 400
^ 5 5159 5000 11 400
^ 20 3550 4774 121 -241
--- Blips --- 
^ 36
^ 104 105 106 107 108 209 10 311 212 113 114 115 216 117 218 119 220 121 1104 1105 1106 1207 1108 1209 1010 1311 1212 1113 1114 1215 1216 1117 1218 1119 1220 1121 
*** END ***          
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
    
      
      sim.applyJustMe(state, actions);
    
      
      assertThat(state.fishes.get(2).id).isEqualTo(20);
      assertThat(state.fishes.get(2).pos.x).isEqualTo(3671);
      assertThat(state.fishes.get(2).pos.y).isEqualTo(4533);
    }
    
    @Test
    void bumpSurLimit() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 18 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 3418 3419 3420 3421 
*** OPTIONAL ***
^ 0 0
^ 4968 4316 0 17 176
^ 6850 3702 0 26 1136
^ 5576 4316 0 17 240
^ 3390 3760 0 27 240
^ 0
 
^ 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 
^ 0 0 
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 4544 4740 0 18
^ 2 5152 4740 0 18
^ 2
^ 1 7213 4180 0 27
^ 3 2823 3563 0 28
--- Current Scans ---
^ 15 0 5 0 4 0 7 2 4 2 5 2 6 2 7 1 10 1 4 1 5 1 6 3 4 3 5 3 6 3 7 
--- Fishes --- 
^ 1
^ 20 4415 2637 252 -96
--- Blips --- 
^ 36
^ 104 105 106 107 108 209 10 311 212 113 114 115 216 117 218 119 220 121 1104 1105 1106 1207 1108 1209 1010 1311 1212 1113 1114 1215 1216 1117 1218 1119 1220 1121 
*** END ***          
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
    
      
      for (Drone d: state.dronesById) {
        d.pos.x = -1000;
        d.pos.y = -1000;
      }
      
      sim.apply(state, actions);
    
      
      assertThat(state.fishes.get(0).id).isEqualTo(20);
      assertThat(state.fishes.get(0).pos.x).isEqualTo(4667);
      assertThat(state.fishes.get(0).pos.y).isEqualTo(2541);
      assertThat(state.fishes.get(0).speed.vx).isEqualTo(252);
      assertThat(state.fishes.get(0).speed.vy).isEqualTo(96);
      
    }
    
    @Test
    void attirerLesUGLY() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 14 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 
*** OPTIONAL ***
^ 9 0
^ 1658 5279 0 23 6240 
^ 7964 5276 0 23 9360 
^ 7227 5046 0 22 1168 
^ 2766 5108 0 22 2144 
^ 0
^ 281479528257018 225464896822642555 281479691700492 281479691705859 703699352212479035 703691840314671738 4622654219 281479567509785 281479685079530 562117284162640955 703690375730829712 703711360941033082 1328867284069844602 281479666603111 
^ 0 0
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 1575 5873 0 24
^ 2 7175 5643 0 17
^ 2
^ 1 8146 5847 0 24
^ 3 2613 5688 0 23
--- Current Scans ---
^ 14 0 5 0 11 0 6 0 12 2 4 2 10 2 7 1 4 1 10 1 7 1 13 3 5 3 11 3 6 
--- Fishes --- 
^ 1
^ 17 5223 6026 530 -104
--- Blips --- 
^ 28
^ 4 5 106 107 108 209 10 11 212 113 114 115 116 117 2304 2305 2206 2107 2108 2209 2310 2311 2212 2113 2214 2215 2216 2217 
*** END ***        
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
    
      actions[2].dx = -83;
      actions[2].dy = 594;
      actions[2].lamp = true;
      
      sim.apply(state, actions);
      
      assertThat(state.dronesById[2].pos.x).isEqualTo(7092);
      assertThat(state.dronesById[2].pos.y).isEqualTo(6237);
      
      assertThat(state.fishes.get(0).id).isEqualTo(17);
      assertThat(state.fishes.get(0).pos.x).isEqualTo(5753);
      assertThat(state.fishes.get(0).pos.y).isEqualTo(5922);
      assertThat(state.fishes.get(0).speed.vx).isEqualTo(526);
      assertThat(state.fishes.get(0).speed.vy).isEqualTo(124);
      
    }
    
    
    @Test
    void anotherAttirerLesUGLY() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 16 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 3418 3419 
*** OPTIONAL ***
^ 0 0
^ 3272 2308 0 28 0
^ 6825 4123 0 19 27056
^ 7272 2308 0 28 0
^ 2848 4458 0 19 40560

^ 0

^ 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000
^ 0 0 
*** TURN ***
^ 16 0
Already scan : 
^4 10 5 4 11
---
^0
^ 2
^ 0 2848 2732 0 23
^ 2 6848 2732 0 23
^ 2
^ 1 6825 3523 0 20
^ 3 2848 3858 0 20
--- Current Scans ---
^ 15 1 4 1 5 1 11 1 13 1 7 1 8 1 14 3 4 3 5 3 10 3 11 3 12 3 6 3 9 3 15 
--- Fishes --- 
^ 1
^ 18 6179 3117 457 287
--- Blips --- 
^ 0
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
    
      actions[2].dx = 0;
      actions[2].dy = -600;
      actions[2].lamp = true;
      
      sim.applyJustMe(state, actions);
    
      
      assertThat(state.dronesById[2].pos.x).isEqualTo(6848);
      assertThat(state.dronesById[2].pos.y).isEqualTo(2132);
      
      assertThat(state.fishes.get(0).id).isEqualTo(18);
      assertThat(state.fishes.get(0).pos.x).isEqualTo(6636);
      assertThat(state.fishes.get(0).pos.y).isEqualTo(3404);
      assertThat(state.fishes.get(0).speed.vx).isEqualTo(89);
      assertThat(state.fishes.get(0).speed.vy).isEqualTo(-533);
      
    }
    
    @Test
    void collision() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 14 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 
*** OPTIONAL ***
^ 0 0
^ 7512 6012 0 15 5200
^ 8839 5391 0 25 1040
^ 1336 6012 0 15 10272
^ 1102 6094 0 25 26656

^ 0

^ 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391613440000 703730391613440000 703730391777280000 703730391777280000
^ 0 0 
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 7512 5412 0 16
^ 2 1760 6436 0 16
^ 2
^ 1 8798 5990 0 26
^ 3 1636 6368 0 26
--- Current Scans ---
^ 14 0 4 0 10 0 6 0 12 2 5 2 11 2 13 1 4 1 10 1 12 3 5 3 11 3 13 3 14 
--- Fishes --- 
^ 1
^ 17 7695 4775 -149 519
--- Blips --- 
^ 28
^ 304 305 206 207 108 209 310 311 112 213 214 115 316 17 1004 1305 1106 1107 1108 1209 1010 1011 1112 1213 1214 1115 1016 1017 
*** END ***
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
    
      actions[0].dx = 0;
      actions[0].dy = -600;
      actions[0].lamp = false;
      
      sim.applyJustMe(state, actions);
    
      
      assertThat(state.dronesById[0].pos.x).isEqualTo(7512);
      assertThat(state.dronesById[0].pos.y).isEqualTo(4812);
      assertThat(state.dronesById[0].emergency).isEqualTo(true);
      
    }    
    @Test
    void noCollision() throws Exception {
      FastReader in = FastReader.fromString(StateTest.cleanInput("""
*** INIT ***
^ 18 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 3418 3419 3420 3421 
*** OPTIONAL ***
^ 0 0
^ 7088 5588 0 20 32
^ 7774 6834 0 14 5728
^ 912 5588 0 20 2064
^ 1832 6031 0 8 10640

^ 0

^ 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 703730391613440000 703730391613440000 703730391777280000 703730391777280000 703730391941120000 703730391941120000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 2111105275166720000 
^ 0 0 
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 7512 6012 0 15
^ 2 488 6012 0 15
^ 2
^ 1 7670 6243 0 15
^ 3 1884 5433 0 9
--- Current Scans ---
^ 14 0 5 0 12 2 4 2 11 1 10 1 5 1 6 1 9 1 12 3 4 3 11 3 7 3 13 3 8 
--- Fishes --- 
^ 5
^ 12 6275 6989 -42 196
^ 16 1677 6210 -533 -89
^ 17 7873 6845 -173 -512
^ 20 1476 7011 -380 -384
^ 21 8256 7203 -286 -458
--- Blips --- 
^ 32
^ 304 5 208 109 10 311 212 313 214 115 216 117 218 119 220 121 1004 1005 1108 1109 1010 1011 1112 1013 1114 1115 1116 1117 1118 1119 1120 1121 
*** END ***
          """));
      State.readPackedInit(in);
      state.readOptional(in);
      state.readPacked(in);
      
      actions[0].dx = 0;
      actions[0].dy = -600;
      actions[0].lamp = false;
      
      sim.applyJustMe(state, actions);
      
      
      assertThat(state.dronesById[0].pos.x).isEqualTo(7512);
      assertThat(state.dronesById[0].pos.y).isEqualTo(5412);
      assertThat(state.dronesById[0].emergency).isEqualTo(false);
      
    }    
    
    
  }
}
