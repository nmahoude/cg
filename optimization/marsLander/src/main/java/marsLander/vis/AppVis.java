package marsLander.vis;

import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import marsLander.Mars;
import marsLander.MarsLander;
import marsLander.ai.AG;
import marsLander.ai.AGSolution;
import marsLander.sim.Simulation;

public class AppVis extends Application  {
  public static final int factor = 10;

  private Mars mars;
  private MarsLander lander;
  AG ag;
  private GraphicsContext gc;

  private int turn = 0;

  public AppVis() {
    Mars mars = new Mars();
    //mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800"));
    //mars.readInput (new Scanner("10 0 100 1000 500 1500 100 3000 100 3500 500 3700 200 5000 1500 5800 300 6000 1000 6999 2000"));
    //mars.readInput (new Scanner("7 0 100 1000 500 1500 1500 3000 1000 4000 150 5500 150 6999 800"));
    //mars.readInput (new Scanner("20 0 1000 300 1500 350 1400 500 2000 800 1800 1000 2500 1200 2100 1500 2400 2000 1000 2200 500 2500 100 2900 800 3000 500 3200 1000 3500 2000 3800 800 4000 200 5000 200 5500 1500 6999 2800"));
    mars.readInput (new Scanner("20 0 1000 300 1500 350 1400 500 2100 1500 2100 2000 200 2500 500 2900 300 3000 200 3200 1000 3500 500 3800 800 4000 200 4200 800 4800 600 5000 1200 5500 900 6000 500 6500 300 6999 500"));
    
    MarsLander lander = new MarsLander();
    //lander.readInput(new Scanner("2500 2700 0 0 550 0 0"));
    //lander.readInput(new Scanner("6500 2800 -100 0 600 90 0"));
    //lander.readInput(new Scanner("6500 2800 -90 0 750 90 0"));
    //lander.readInput(new Scanner("500 2700 100 0 800 -90 0"));
    lander.readInput(new Scanner("6500 2700 -50 0 1000 90 0"));
    //lander.readInput(new Scanner("1994 2202 -61 -3 756 -35 4")); // end game of 5

    ag = new AG(mars, lander);
    ag.randomizePopulation();
    
    this.mars = mars;
    this.lander = lander;
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("MarsLander Visualisation");
    Group root = new Group();
    Canvas canvas = new Canvas(700, 500);
    gc = canvas.getGraphicsContext2D();
    gc.scale(1, -1);
    gc.translate(0, -500);
    drawMars(gc);
    
    Timeline timeline = new Timeline(new KeyFrame(
        Duration.millis(100),
        ae -> update()));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    
    root.getChildren().add(canvas);
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
    
  }

  private Object update() {
    gc.clearRect(0, 0, 700, 500);
    drawMars(gc);

    doOneIteration();
    
    return null;
  }
  
  private void doOneIteration() {
    turn ++;
    if (turn > 1) {
      ag.evolvePopulation();
    }
    ag.oneIteration();

    for (int i=0;i<AG.SIZE;i++) {
      AGSolution solution = ag.solutions[i];
      //if (Double.isInfinite(solution.score) && solution.score < 0) continue;

      Color p = getColorFromScore(solution);
      gc.setStroke(p);
      
      drawSolution(solution);    
    }

    if (Double.isInfinite(ag.solutions[0].score)) {
      gc.setStroke(Color.ORANGE);
      gc.setLineWidth(10);
      
    } else {
      gc.setStroke(Color.BLUEVIOLET);
    }
    drawSolution(ag.solutions[0]);

  }

  private void drawSolution(AGSolution solution) {
    MarsLander thisLander = new MarsLander();
    thisLander.copyFrom(lander);
    
    Simulation simulation = new Simulation(mars, thisLander);
    simulation.reset();
    
    double lastX =  thisLander.x;
    double lastY = thisLander.y;
    for (int s = 0;s<AGSolution.DEPTH;s++) {
      simulation.update(solution.values[s]);
      if (simulation.result != 0) break;
      gc.strokeLine(lastX / factor, lastY / factor, thisLander.x / factor, thisLander.y / factor);
      lastX = thisLander.x;
      lastY = thisLander.y;
    }
  }

  private Color getColorFromScore(AGSolution solution) {
    double score = solution.score;
    double red = 0;
    double green = 0;
    double blue = 0;
    if (Double.isInfinite(score)) {
      red = 0;
      green = 0;
      if (score > 0) {
        blue = 1.0;
      } else {
        red = 1.0;
      }
    } else {
      red = 0;
      green = Math.max(0, Math.min((score - 100.0) / 10.0, 1.0));
    }
    Color p = new Color(red, green, blue, 1.0);
    return p;
  }

  private void drawMars(GraphicsContext gc) {
    gc.setFill(Color.RED);
    gc.setStroke(Color.RED);
    gc.setLineWidth(2);
    
    
    for (int i=1;i<mars.pointsX.length;i++) {
      gc.strokeLine(mars.pointsX[i-1] / factor, mars.pointsY[i-1] / factor, mars.pointsX[i] / factor, mars.pointsY[i] / factor);
    }
  }

  
  public static void main(String[] args) {
    launch(args);
  }
}
