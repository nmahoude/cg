package spring2022._cgfx.components;

import static spring2022._cgfx.Mapper.map;

import cgfx.Board;
import cgfx.CGFXColor;
import cgfx.Length;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.GameWrapper;
import javafx.scene.paint.Color;
import spring2022.Future;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ag.AG;
import spring2022.ag.LightState;
import spring2022.ai.DrawDebugger;

public class S22GameViewer extends GameViewer implements DrawDebugger {
  
  private S22GameWrapper wrapper;
  private Board board;

  private boolean drawQuadrants = true;
  
  public S22GameViewer(GameWrapper wrapper) {
    this(10, wrapper);
  }

  public S22GameViewer(int scale, GameWrapper wrapper) {
    board = new Board(this, 17630 , 9000, 1.0 / scale);
    
    this.wrapper = (S22GameWrapper)wrapper;
  }

  @Override
  protected void updateView() {
    draw(this.wrapper.player.state);
  }

  private void draw(State state) {
    board.clear();
    board.getGc().setLineWidth(1);
    
    board.drawRect(Color.BLACK, cgfx.Pos.from(0, 0), Length.of(17630,  9000));
    
    drawBases();
    
    if (wrapper.lightState == null ) {
      drawFullState(state);
    } else {
      drawLightState();
    }
  }

  private void drawFullState(State state) {
    for (Hero hero : state.myHeroes) {
      drawMyHero(hero);
    }
 
    for (Hero hero : state.oppHeroes) {
      drawOppHero(hero);
    }
 
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      drawUnit(unit, state.future);
    }
    
    drawMyPoints(state.health[0], state.mana[0]);
    
  }

  private void drawMyPoints(int h, int m) {

    board.drawText(Color.BLUE, cgfx.Pos.from(0, 9000 - 16), "Mana: "+m);
    for (int i=0;i<h;i++) {
      board.fillCircle(Color.BLUE, cgfx.Pos.from(48 + 16 * 10 * i, 9000 - 16*10 ), 40);
    }
  }

  private void drawLightState() {
    // user the lightstate !
    LightState state = wrapper.lightState;
    for (Hero hero : state.hero) {
      drawMyHero(hero);
    }
 
    for (Hero hero : state.oppHeroes) {
      drawOppHero(hero);
    }
 
    for (Unit unit : state.units) {
      drawUnit(unit, null);
    }
    
    drawMyPoints(state.health, state.mana);
  }

  private void drawUnit(Unit unit, Future future) {
    if (unit.isDead()) return;
    
    board.drawCircle(Color.PURPLE, map(unit.pos), 80);
    
    if (unit.hasShield()) {
      Pos pos = unit.pos;
      drawShield(pos);
    }
    
    
    if (future != null) {
      final Pos last = new Pos();
      last.copyFrom(unit.pos);
      
      for (int i=1;i<AG.DEPTH;i++) {
        State s = future.get(i);
        Unit u = s.findUnitById(unit.id);
        board.drawCircle(Color.GREY, map(u.pos) , 10);
        board.drawLine(Color.GRAY, map(last), map(u.pos));
        last.copyFrom(u.pos);
      }
    }
  }

  private void drawShield(Pos pos) {
    board.drawCircle(Color.PURPLE, map(pos), 100);
    board.drawCircle(Color.PURPLE, map(pos), 120);
  }

  private void drawOppHero(Hero hero) {
    if (hero.isInFog()) return;
    
    board.drawCircle(Color.RED, map(hero.pos), 20); // pos
    board.drawCircle(Color.LIGHTPINK, map(hero.pos), 800); // MOVE
    board.drawCircle(Color.LIGHTPINK, map(hero.pos), 2200); // VIEW
    
    if (hero.hasShield()) {
      drawShield(hero.pos);
    }

  }

  private void drawMyHero(Hero hero) {
    board.drawCircle(Color.BLUE, map(hero.pos), 20); // pos
    board.drawCircle(Color.LIGHTBLUE, map(hero.pos), 800); // MOVE
    board.drawCircle(Color.LIGHTBLUE, map(hero.pos), 2200); // VIEW
    board.drawText(Color.BLUE, map(hero.pos), ""+hero.id);

    if (hero.hasShield()) {
      drawShield(hero.pos);
    }

  }

  private void drawBases() {
    board.drawCircle(Color.BLUE, cgfx.Pos.from(0, 0), 300); // KILL ZONE
    board.drawCircle(Color.BLUE, cgfx.Pos.from(0, 0), 5000); // LIMIT OF AGGRO
    board.drawCircle(Color.LIGHTCORAL, cgfx.Pos.from(0, 0), 6000); // VIEW

    board.drawCircle(Color.RED, cgfx.Pos.from(State.WIDTH, State.HEIGHT), 300); // kill zone
    board.drawCircle(Color.RED, cgfx.Pos.from(State.WIDTH, State.HEIGHT), 5000); // LIMIT OF AGGRO
    board.drawCircle(Color.LIGHTCORAL, cgfx.Pos.from(State.WIDTH, State.HEIGHT), 6000); // VIEW

    if (drawQuadrants ) {
      for (int i=1;i<3;i++) {
        board.drawLine(Color.LIGHTBLUE, cgfx.Pos.from(0, 0), cgfx.Pos.from(0+ 5000 * Math.cos(Math.PI * i / 6), 0 + 5000 * Math.sin(Math.PI * i / 6)));
      }
      for (int i=1;i<3;i++) {
        board.drawLine(Color.LIGHTCORAL, cgfx.Pos.from(State.WIDTH, State.HEIGHT), cgfx.Pos.from(State.WIDTH - 5000 * Math.cos(Math.PI * i / 6), State.HEIGHT - 5000 * Math.sin(Math.PI * i / 6)));
      }
    }
  }

  @Override
  public void setOptionsPane(GameOptionPane options) {
  }
  
  @Override
  public void drawLine(CGFXColor color, double drawSize, Pos start, Pos end) {
    board.drawLine(map(color), drawSize, map(start), map(end));
  }

  @Override
  public void drawLine(CGFXColor color, Pos start, Pos end) {
    board.drawLine(map(color), 1 , map(start), map(end));
  }

  @Override
  public void drawCircle(CGFXColor color, Pos from, int radius) {
    board.drawCircle(map(color), map(from), radius);
  }

  @Override
  public void drawText(CGFXColor black, Pos pos, String string) {
    board.drawText(map(black), map(pos.x , pos.y ), string);
  }

  public Board board() {
    return board;
  }
}
