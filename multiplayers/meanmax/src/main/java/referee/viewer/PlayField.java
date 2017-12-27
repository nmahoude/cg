package referee.viewer;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import meanmax.Game;

public class PlayField extends Pane {
  public EntityNode[] entityNodes = new EntityNode[50];
  public Circle field;
  
  double orgSceneX, orgSceneY;
  double orgTranslateX, orgTranslateY;
  
  public PlayField() {
    setManaged(false);
    maxWidth(Game.MAP_RADIUS  * 2);
    maxHeight(Game.MAP_RADIUS * 2);
    minWidth(Game.MAP_RADIUS  * 2);
    minHeight(Game.MAP_RADIUS * 2);
    
    createEntities();
    update();
    
  }
  
  public void createEntities() {
    getChildren().clear();
    field = new Circle();
    field.setRadius(6000);
    field.setFill(Color.BLACK);
    field.setStroke(Color.BROWN);
    getChildren().add(field);

    for (int i=0;i<entityNodes.length;i++) {
      EntityNode cpr = new EntityNode();
      getChildren().add(cpr);
      entityNodes[i] = cpr;

      cpr.setOnMousePressed(circleOnMousePressedEventHandler);
      cpr.setOnMouseDragged(circleOnMouseDraggedEventHandler);
    }
  }
  
  public void update() {
    for (int i=0;i<Game.entities_FE;i++) {
      entityNodes[i].update(Game.entities[i]);
    }
    for (int i=Game.entities_FE;i<entityNodes.length;i++) {
      entityNodes[i].hide();
    }
    field.toBack();
  }
  
  EventHandler<MouseEvent> circleOnMousePressedEventHandler = 
      new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent t) {
          orgSceneX = t.getSceneX();
          orgSceneY = t.getSceneY();
          orgTranslateX = ((EntityNode)(t.getSource())).getTranslateX();
          orgTranslateY = ((EntityNode)(t.getSource())).getTranslateY();
      }
  };
   
  EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = 
      new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent t) {
          double offsetX = t.getSceneX() - orgSceneX;
          double offsetY = t.getSceneY() - orgSceneY;
          double newTranslateX = orgTranslateX + offsetX / 0.05d;
          double newTranslateY = orgTranslateY + offsetY / 0.05d;
          
          EntityNode entityNode = ((EntityNode)(t.getSource()));
          entityNode.setTranslateX(newTranslateX);
          entityNode.setTranslateY(newTranslateY);
          entityNode.entity.position.x = newTranslateX;
          entityNode.entity.position.y = newTranslateY;
          entityNode.entity.backup();
      }
  };
}
