package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CodingameView extends Application {
  
  public static boolean wait = true;
  static final Color DISABLE_PERCENTILE = Color.color(0.4, 0.4, 0.4);
  
  private ToolBar toolbar;

  List<GfxNode> gfxNodes = new ArrayList<>();
  private Group gameView;

  private static GameNode rootGameNode;

  public GlobalData globalData = new GlobalData();

  protected boolean controlIsDown = false;

  protected double stageScale = 0.1;
  
  
  public CodingameView() {
	}
  
	@Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Codingame javafx viewer");

    Group root = new Group();
    primaryStage.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        controlIsDown  = event.isControlDown();
      }
      
    });
    primaryStage.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
      @Override
      public void handle(ScrollEvent event) {
        if (controlIsDown) {
          if (event.getDeltaY() < 0) {
            if (stageScale > 0.05 * 2) {
              stageScale -=0.05;
            }
          } else {
              stageScale +=0.05;
          }
          gameView.setScaleX(stageScale);
          gameView.setScaleY(stageScale);
        }
      }
    });
    
    
    Scene scene = new Scene(root, 1024, 768);
    primaryStage.setScene(scene);

    gameView = new Group();
    Group draggableGameView = makeDraggable(gameView);
    gameView.setLayoutX(1);
    gameView.setLayoutY(1);
    
    root.getChildren().add(draggableGameView);

    toolbar = new ToolBar();
    root.getChildren().add(toolbar);

    rootNode = new GfxNode(this, null, rootGameNode);
    rootNode.redispose();
    GfxNode.redraw(rootNode, globalData);

    rootNode.setTranslateX(500);
    gameView.getChildren().add(rootNode);

    
    Button nextBtn = createNextBtn();
    toolbar.getItems().add(nextBtn);
    Button replieBtn = createReplieBtn();
    toolbar.getItems().add(replieBtn);
    
    // score percentile slider
    Slider slider = new Slider();
    slider.setMin(0);
    slider.setMax(1000);
    slider.setValue(0);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(5);
    slider.setMinorTickCount(1);
    slider.setBlockIncrement(1);
    slider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
        globalData.percentile = 1.0 * new_val.doubleValue() / 1000;
        updateNodes();
      }
    });
    toolbar.getItems().add(slider);
    
    root.getChildren().add(makeDraggableInfoPanel());
    
    selectedNode = new Circle();
    selectedNode.setVisible(false); 
    
    root.getChildren().add(selectedNode);
    
    primaryStage.show();
  }

	private Button createReplieBtn() {
		Button nextBtn = new Button("Replié (thrshld)");
    nextBtn.setOnAction(event -> {
      replieWithThresoldAction();
    });
		return nextBtn;
	}

	/**
	 * Replie all nodes with all childs under the currentThreshold
	 */
	private void replieWithThresoldAction() {
		rootNode.dfs(gfx -> { if (gfx != rootNode) {if (gfx.node().score() > globalData.percentile * globalData.maxScore) gfx.deplierNoeudEtParent(false); else gfx.replierNoeud(false); }} );
		rootNode.redispose();
		GfxNode.redraw(rootNode, globalData);
	}

	private Button createNextBtn() {
		Button nextBtn = new Button("Next");
    nextBtn.setOnAction(event -> {
      wait = false;
    });
		return nextBtn;
	}

  private void updateNodes() {
    rootNode.redispose();
    GfxNode.redraw(rootNode, globalData);
  }

  static class InfoPanel {
    Text text1 = new Text("");
    Text text2 = new Text("");
    Text text3 = new Text("");
    Text text4 = new Text("");
    Text text5 = new Text("");
    public void clear() {
      text1.setText("");
      text2.setText("");
      text3.setText("");
      text4.setText("");
      text5.setText("");
    }
    
    public void fill(List<String> values) {
      clear();
      if (values.size() > 0) text1.setText(values.get(0));
      if (values.size() > 1) text2.setText(values.get(1));
      if (values.size() > 2) text3.setText(values.get(2));
      if (values.size() > 3) text4.setText(values.get(3));
      if (values.size() > 4) text5.setText(values.get(4));
    }
    
  }
  private Node makeDraggableInfoPanel() {
    final VBox vbox = new VBox(20.0);
    vbox.setLayoutX(000);
    vbox.setLayoutY(600);
    vbox.setBackground(new Background(new BackgroundFill(Color.color(0.9, 0.9, 0.7), null, null)));
    infoPanel = new InfoPanel();
    vbox.getChildren().add(infoPanel.text1);
    vbox.getChildren().add(infoPanel.text2);
    vbox.getChildren().add(infoPanel.text3);
    vbox.getChildren().add(infoPanel.text4);
    vbox.getChildren().add(infoPanel.text5);
    return makeDraggable(vbox);
  }
  
  Paint colorFor(double score) {
    
    double reductedScore = globalData.reducted(score);
    return Color.color(reductedScore, 0, 1.0-reductedScore);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

  public static void execute(GameNode rootGameNode) {
    CodingameView.rootGameNode = rootGameNode;
    Application.launch();
  }
  
  public static void waitNext() {
    
    // wait for javafx push ?
    while (CodingameView.wait) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    CodingameView.wait = true;
  }
  
  private final BooleanProperty dragModeActiveProperty = new SimpleBooleanProperty(this, "dragModeActive", true);


  public boolean hideNodeUnderThreshold = false;
  InfoPanel infoPanel;
	Circle selectedNode;

	
  GfxNode rootNode;
  
  private static final class DragContext {
    public double mouseAnchorX;
    public double mouseAnchorY;
    public double initialTranslateX;
    public double initialTranslateY;
  }
  private Group makeDraggable(final Node node) {
    final DragContext dragContext = new DragContext();
    final Group wrapGroup = new Group(node);

    wrapGroup.addEventFilter(
            MouseEvent.ANY,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent mouseEvent) {
                    if (dragModeActiveProperty.get()) {
                        // disable mouse events for all children
                        //mouseEvent.consume();
                    }
                }
            });

    wrapGroup.addEventFilter(
            MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent mouseEvent) {
                    if (dragModeActiveProperty.get()) {
                        // remember initial mouse cursor coordinates
                        // and node position
                        dragContext.mouseAnchorX = mouseEvent.getX();
                        dragContext.mouseAnchorY = mouseEvent.getY();
                        dragContext.initialTranslateX =
                                node.getTranslateX();
                        dragContext.initialTranslateY =
                                node.getTranslateY();
                    }
                }
            });

    wrapGroup.addEventFilter(
            MouseEvent.MOUSE_DRAGGED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent mouseEvent) {
                    if (dragModeActiveProperty.get()) {
                        // shift node from its initial position by delta
                        // calculated from mouse cursor movement
                        node.setTranslateX(
                                dragContext.initialTranslateX
                                    + mouseEvent.getX()
                                    - dragContext.mouseAnchorX);
                        node.setTranslateY(
                                dragContext.initialTranslateY
                                    + mouseEvent.getY()
                                    - dragContext.mouseAnchorY);
                    }
                }
            });
            
    return wrapGroup;
  }


}
