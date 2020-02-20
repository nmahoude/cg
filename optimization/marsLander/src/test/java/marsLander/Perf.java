package marsLander;

import java.util.Scanner;

import marsLander.ai.AG;

public class Perf {

  public static void main(String[] args) {
    Mars mars = new Mars();
    //mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800"));
    //mars.readInput (new Scanner("10 0 100 1000 500 1500 100 3000 100 3500 500 3700 200 5000 1500 5800 300 6000 1000 6999 2000"));
    //mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800"));
    mars.readInput (new Scanner("20 0 1000 300 1500 350 1400 500 2000 800 1800 1000 2500 1200 2100 1500 2400 2000 1000 2200 500 2500 100 2900 800 3000 500 3200 1000 3500 2000 3800 800 4000 200 5000 200 5500 1500 6999 2800"));
    //mars.readInput (new Scanner("20 0 1000 300 1500 350 1400 500 2100 1500 2100 2000 200 2500 500 2900 300 3000 200 3200 1000 3500 500 3800 800 4000 200 4200 800 4800 600 5000 1200 5500 900 6000 500 6500 300 6999 500"));
    
    MarsLander lander = new MarsLander();
    //lander.readInput(new Scanner("2500 2700 0 0 550 0 0"));
    //lander.readInput(new Scanner("6500 2800 -100 0 600 90 0"));
    //lander.readInput(new Scanner("6500 2800 -90 0 750 90 0"));
    lander.readInput(new Scanner("500 2700 100 0 800 -90 0"));
    //lander.readInput(new Scanner("6500 2700 -50 0 1000 90 0"));

    AG ag = new AG(mars, lander);
    ag.randomizePopulation();

    for (int i=0;i<100_000;i++) {
      ag.oneIteration();
      ag.evolvePopulation();
    }
  }
}
