package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ScoreMinimaxTest {

  
  @Test
  void perdu() throws Exception {
    State state = readState();

    List<Drone> needToGoUp = new ScoreMinimax().think(state);
    assertThat(needToGoUp).containsExactlyInAnyOrder(state.myDrones[0], state.myDrones[1]);
  }


  @Test
  @Disabled("other way to win")
  void canBeatHim() throws Exception {
    State state = readState();
    
    state.myDrones[0].copyFrom(state.oppDrones[0]);
    state.myDrones[1].copyFrom(state.oppDrones[1]);
    
    state.myDrones[0].pos.set(7671, 3300);
    List<Drone> needToGoUp = new ScoreMinimax().think(state);
    
    assertThat(needToGoUp).containsExactlyInAnyOrder(state.myDrones[1]);
  }

  
  @Test
  void debug() throws Exception {
    State state = StateTest.fromString("""
*** INIT ***
^ 18 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 3418 3419 3420 3421 
*** OPTIONAL ***
^ 10 0
^ 3048 5355 0 18 2144 
^ 8841 5251 0 24 1168 
^ 7204 5674 0 6 1152 
^ 3795 5820 0 23 2112 
^ 7
^ 6 2561 6314 -181 357
^ 11 3215 5000 170 -362
^ 7 7369 6478 80 392
^ 10 6747 4756 -178 -358
^ 18 2888 4756 139 522
^ 19 8360 4866 422 337
^ 5 675 4764 -161 -118
^ 179867483989156490 123572673330610896 281479685474817 281479696227529 703690985616186404 703690985616181171 281479583373915 4622650511 513984277191987754 513983620061989864 703697329282878440 703697329282880042 1217678599506234344 1217678599506234344 281479583370056 281479590584488 1217670791255687878 1217672758350715940 
^ 0 0
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 0 2944 5945 0 13
^ 2 7287 6268 0 1
^ 2
^ 1 8567 5784 0 19
^ 3 3914 6408 0 18
--- Current Scans ---
^ 12 0 5 0 11 0 6 2 10 2 7 1 4 1 10 1 7 3 6 3 11 3 12 3 13 
--- Fishes --- 
^ 5
^ 6 2380 6671 -245 316
^ 11 3385 4638 128 -379
^ 7 7449 6870 104 386
^ 18 3027 5278 -67 536
^ 19 8782 5203 -187 506
--- Blips --- 
^ 34
^ 305 206 107 108 209 10 11 112 113 114 115 116 117 18 19 220 121 2305 2206 2107 2108 2209 2310 2311 2212 2213 2214 2215 2216 2217 2318 2019 2220 2121 
*** END ***   
        """);
    
    List<Drone> needToGoUp = new ScoreMinimax().think(state);
  }
  
  
  
  State readState() {
    return StateTest.fromString("""
*** INIT ***
^ 14 4 105 1006 1107 2008 2109 210 311 1212 1313 2214 2315 3416 3417 
*** OPTIONAL ***
^ 17 1
^ 1750 5100 0 5 1091696 
^ 8219 8907 0 1 17536 
^ 7500 5100 0 5 546192 
^ 1465 9275 0 0 41056 
^ 9
^ 15 1217 9999 -130 378
^ 16 1198 8656 214 496
^ 7 8713 7342 122 -157
^ 14 8499 9999 99 387
^ 17 7566 7243 -19 -269
^ 6 3238 7192 184 -77
^ 13 2963 5240 397 45
^ 5 2389 3796 -200 0
^ 10 8343 3701 -199 11
^ 450926988837128945 505534903832807765 225186428553528761 56296718102438086 348762610625942108 396060229303535265 513132739632373787 656964584387249130 698643681620003681 461905530941082515 56578450940960958 281479926973633 281479838958766 304279093140790149 
^ 0 0
*** TURN ***
^ 0 0
Already scan : 
^0
---
^0
^ 2
^ 1 7671 8663 0 2
^ 3 2035 9090 0 1
^ 2
^ 0 1750 4500 0 6
^ 2 7500 4500 0 6
--- Current Scans ---
^ 19 1 10 1 7 1 14 3 5 3 13 3 6 3 15 0 11 0 6 0 15 0 13 0 4 0 5 2 4 2 12 2 7 2 8 2 14 2 10 
--- Fishes --- 
^ 1
^ 16 1412 9152 537 -53
--- Blips --- 
^ 28
^ 1304 1305 1306 1007 1308 1309 1010 1311 1312 1313 1114 1215 1216 1317 3004 3005 3006 3007 3008 3009 3010 3311 3012 3013 3114 3215 3216 3017 
*** END ***
        """);
  }
}
