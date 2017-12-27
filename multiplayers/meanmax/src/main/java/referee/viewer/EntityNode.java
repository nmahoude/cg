package referee.viewer;

import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import meanmax.Game;
import meanmax.entities.Entity;
import meanmax.entities.Tanker;
import meanmax.entities.Wreck;

public class EntityNode extends Group {
  Entity entity;
  private Circle oldPos;
  public Circle pos;
  private Text text;
  private Line line;

  public EntityNode() {
    pos = new Circle(0, 0, 400.0);
    this.getChildren().add(pos);

    oldPos = new Circle(0, 0, 400.0);
    this.getChildren().add(oldPos);
    
    line = new Line(0.0, 0.0, 100, 0);
    this.getChildren().add(line);
    
    text = new Text(0, 0, "");
    text.setFill(Color.BLUE);
    text.setScaleX(1.0 / 0.05);
    text.setScaleY(1.0 / 0.05);
    this.getChildren().add(text);
    

  }

  private String getType() {
    String types[] = {"re", "de", "do", "ta", "wr", "tar", "gr", "oil"};
    return types[entity.type];
  }

  public void update(Entity e) {
    this.entity = e;
    setVisible(true);

    oldPos.setCenterX(this.getTranslateX() - entity.position.x);
    oldPos.setCenterY(this.getTranslateY() - entity.position.y);
    
    oldPos.setStroke(pos.getStroke());
    oldPos.setFill(new Color(0.5, 0.5, 0.5, 0.5));
    oldPos.setBlendMode(BlendMode.LIGHTEN);
    oldPos.setRadius(pos.getRadius());
    oldPos.toBack();
    
    
    line.setEndX(entity.speed.vx);
    line.setEndY(entity.speed.vy);
    line.setFill(Color.GREEN);
    
    String description = ""+e.unitId + "/" + getType();
    if (e.type == Game.WRECK) {
      pos.setFill(Color.LIGHTBLUE);
      pos.setStroke(Color.LIGHTBLUE);
      this.toBack();
      description = "" + ((Wreck)e).water;
    } else if (e.type == Game.TANKER) {
      pos.setFill(Color.LIGHTGREY);
      pos.setStroke(Color.LIGHTGREY);
      description = "" + ((Tanker)e).water;
    } else if (e.type >= Game.SKILL_EFFECT_TAR ) {
      pos.setFill(Color.TRANSPARENT);
      pos.setStroke(Color.BROWN);
      this.toBack();
    } else if (e.type >= Game.SKILL_EFFECT_OIL ) {
      pos.setFill(Color.BLUE);
      pos.setStroke(Color.BLUE);
      this.toBack();
    } else if (e.unitId < 3) {
      pos.setFill(Color.web("ffaa00"));
      pos.setStroke(Color.web("ffaa00"));
    } else if (e.unitId < 6) {
      pos.setFill(Color.web("1166ff"));
      pos.setStroke(Color.web("1166ff"));
    } else if (e.unitId < 9) {
      pos.setFill(Color.web("ff4400"));
      pos.setStroke(Color.web("ff4400"));
    }
    
    this.setTranslateX(entity.position.x);
    this.setTranslateY(entity.position.y);
    
    text.setText(description);
    pos.setRadius(entity.radius);
  }

  public void hide() {
    setVisible(false);
  }

}
