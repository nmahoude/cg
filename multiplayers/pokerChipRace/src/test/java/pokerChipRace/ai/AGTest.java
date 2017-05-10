package pokerChipRace.ai;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import pokerChipRace.simulate.Simulation;

public class AGTest {
  GameState state;
  Simulation simulation;
  
  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation();
    simulation.setGameState(state);
  }
  
  @Test
  public void perfTest() throws Exception {
    readEntity(0,0,490.242431640625, 271.53594970703125, 0.0, 0.0,21.0);
    readEntity(1,1,621.3764038085938, 340.9454345703125, 0.0, 0.0,21.0);
   
    AG ag = new AG();
    long start = System.currentTimeMillis();
    ag.getSolutionRandom(state, start+20);
    
  }
  
  @Test
  public void timeoutTest() throws Exception {

    
    readEntity(8,2,455.1235046386719, 96.34878540039062, -6.901064872741699, 10.422347068786621,23.754297256469727);
    readEntity(9,2,493.3630676269531, 474.5994873046875, 1.1242090463638306, 14.241412162780762,22.220111846923828);
    readEntity(10,2,345.88079833984375, 95.40994262695312, 0.0, 0.0,23.0);
    readEntity(11,2,563.6305541992188, 99.97278594970703, 7.886931896209717, 11.911252975463867,22.220111846923828);
    readEntity(0,0,104.40815734863281, 181.14071655273438, -14.267930030822754, -24.753843307495117,21.46666717529297);
    readEntity(1,0,132.09652709960938, 72.64500427246094, -25.035388946533203, -13.767931938171387,21.46666717529297);
    readEntity(2,0,482.9543762207031, 262.2353515625, -1.2514829635620117, -11.979368209838867,23.754297256469727);
    readEntity(3,0,713.4016723632812, 336.08160400390625, -37.39424133300781, -5.9219841957092285,22.220111846923828);
    readEntity(4,1,213.18992614746094, 363.6053466796875, -1.082543969154358, -1.4360530376434326,22.94883155822754);
    readEntity(5,1,244.23162841796875, 221.4355010986328, -21.166719436645508, -19.191055297851562,21.46666717529297);
    readEntity(6,1,68.583984375, 293.87689208984375, -6.493415832519531, -27.82377052307129,21.46666717529297);
    readEntity(7,1,357.06207275390625, 203.4893035888672, -12.298047065734863, -7.275781154632568,22.220111846923828);
    readEntity(12,-1,296.3113098144531, 434.249267578125, -0.4311569929122925, 0.3237760066986084,14.019767761230469);
    readEntity(13,-1,650.8404541015625, 24.522785186767578, -1.6699169874191284, 0.027312999591231346,20.849367141723633);
    readEntity(14,-1,452.8052062988281, 408.05108642578125, 12.827564239501953, 15.632423400878906,23.71942710876465);
    readEntity(15,-1,239.3698272705078, 329.1707763671875, -3.283742904663086, 7.432618141174316,11.860701560974121);
    readEntity(16,-1,619.00439453125, 335.37139892578125, 37.25712585449219, 20.209362030029297,18.773540496826172);
    readEntity(17,-1,400.1342468261719, 458.83758544921875, -1.545814037322998, 0.6949329972267151,11.618141174316406);
    readEntity(18,-1,437.4541931152344, 274.469970703125, 101.40597534179688, -172.3856964111328,5.93857479095459);
    readEntity(25,-1,172.93223571777344, 277.1220703125, 45.453914642333984, -194.76637268066406,5.93857479095459);
    readEntity(27,-1,664.8976440429688, 431.6606750488281, -157.61253356933594, 82.90776824951172,5.93857479095459);
    readEntity(28,-1,329.5624694824219, 456.8318176269531, 101.19429016113281, -172.5095672607422,5.93857479095459);
    readEntity(30,-1,333.6446838378906, 183.48416137695312, 162.73001098632812, 89.4915542602539,5.737207889556885);
    readEntity(32,-1,120.85943603515625, 500.6525573730469, 42.20720291137695, -180.8544921875,5.737207889556885);
    readEntity(34,-1,414.63494873046875, 375.9336853027344, 137.5836639404297, 124.74185943603516,5.737207889556885);

    Player.rand.setSeed(9163063626091541570L,-7753284015946618596L);
    AG ag = new AG();
    long start = System.currentTimeMillis();
    ag.getSolutionRandom(state, start+20);
    long end = System.currentTimeMillis();
    System.err.println("time : "+(end-start));
  }
  
  @Test
  public void entity0shouldNotSendDropletAgainstIncomming() throws Exception {
    readEntity(0,0,371.1510314941406, 421.6116027832031, 0.0, -26.23961639404297,24.0);
    readEntity(1,-1,410.09722900390625, 438.6258850097656, -181.78208923339844, -83.39827728271484,6.196774005889893);
    state.backup();
    
    // wait
    AGSolution sol = new AGSolution(1);
    for (int i=0;i<sol.angles.length;i++) {
      sol.angles[i] = -1;
    }
    AG ag = new AG();
    ag.sim = simulation;
    ag.state = state;
    
    ag.play(sol);
    System.err.println(sol.energy);
    sol.clear();
    
    state.restore();
    sol.angles[0] = 6.20;
    ag.play(sol);
    System.err.println(sol.energy);
  }
  
  @Test
  public void comparaisonWithLeader() throws Exception {
    state.myId = 1;
    readEntity(1,1,317.28558349609375, 96.56682586669922, 0.0, 0.0,45.0);
    readEntity(0,0,659.2265014648438, 73.94156646728516, 0.0, 0.0,45.0);
    readEntity(2,-1,769.0978393554688, 384.8266906738281, 5.477825164794922, 0.6422320008277893,21.663904190063477);
    readEntity(3,-1,202.45875549316406, 226.9713592529297, -4.247443199157715, 0.8041210174560547,16.018810272216797);
    readEntity(4,-1,580.3755493164062, 357.3370361328125, 0.411855012178421, 3.9367640018463135,20.330307006835938);
    readEntity(5,-1,475.2607727050781, 417.98992919921875, -3.445904016494751, 2.7382450103759766,14.48482894897461);
    readEntity(6,-1,177.6339111328125, 127.03191375732422, 3.037209987640381, 0.6223940253257751,21.998483657836914);
    readEntity(7,-1,230.7200164794922, 323.6396484375, -0.4635710120201111, 0.5751579999923706,20.22197914123535);
    readEntity(8,-1,458.7026672363281, 34.97816467285156, -4.526316165924072, 2.476516008377075,22.028120040893555);
    readEntity(9,-1,424.6960144042969, 337.5643615722656, -0.5164859890937805, 6.6508002281188965,23.209829330444336);
    readEntity(10,-1,728.3934936523438, 454.1436462402344, 6.74180793762207, 1.7221859693527222,23.77242088317871);
    readEntity(11,-1,373.0942077636719, 270.80877685546875, -2.0161049365997314, 4.169328212738037,17.556819915771484);
    state.backup();
    
    AGSolution sol = new AGSolution(1);
    for (int i=0;i<sol.angles.length;i++) {
      sol.angles[i] = -1;
    }
    sol.angles[0] = 3.0;
    
    AG ag = new AG();
    ag.sim = simulation;
    ag.state = state;
    
    AGSolution sol2 = ag.getSolutionRandom(state, System.currentTimeMillis()+140);
    
    //ag.play(sol);
    sol2.debug();
  }

  
  @Test
  public void sortAGSolutions() throws Exception {
    AGSolution sol1 = new AGSolution(1);
    AGSolution sol2 = new AGSolution(1);
    AGSolution sol3 = new AGSolution(1);
    
    sol1.energy = 1.0;
    sol2.energy = 3.0;
    sol3.energy = 2.0;
    
    AGSolution population[] = new AGSolution[] { sol1, sol2, sol3};
    
    AG.sortPopulation(population);
    
    assertThat(population[0], is (sol2));
    assertThat(population[1], is (sol3));
    assertThat(population[2], is (sol1));
    
  }
  
  private void readEntity(int id, int owner, double x, double y, double vx, double vy, double radius) {
    Entity entity = state.getInitialChip(id);
    entity.update(owner, x,y, radius, vx, vy);
    if (owner == 0) {
      state.myChips.add(entity);
    }
  }
}
