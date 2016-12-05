package fantasticBits.fb.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import fantasticBits.fb.Player;
import fantasticBits.fb.ai.Move;
import fantasticBits.fb.ai.Type;
import trigonometry.Point;
import trigonometry.Vector;

public class GameState {
  private boolean doUnitTests = false;
  public int rounds=0;
  public static final int MAX_X=16001;
  public static final int MAX_Y=7501;
  private static final Point CENTER = new Point( MAX_X / 2, MAX_Y / 2);
  
  Player players[] = new Player[2];
  public int myTeamId;
  
  
  public List<Entity> allEntities = new ArrayList<>();
  private List<Snaffle> snafflesCache;
  
  public GameState duplicate() {
    GameState newState = new GameState();
    newState.myTeamId = myTeamId;
    
    for (Entity e: allEntities) {
      Entity duplicate = e.duplicate();
      duplicate.estimateNextPosition();
      newState.allEntities.add(duplicate);
    }
    return newState;
  }

  private GameState nextGameState = null;
  public GameState getNextGameState() {
    if (nextGameState == null) {
      nextGameState = new GameState();
      for (Entity entity : allEntities) {
        nextGameState.allEntities.add(entity.duplicate());
      }
    }
    return nextGameState;
  }
  
  public void applyMove(Wizard wizard, Move move) {
    if (move.type == Type.MOVE) {
      Vector v = new Point(move.x, move.y).sub(wizard.position).normalize().dot(move.thrust); //.dot(1);
      wizard.speed = wizard.speed.add(v);
      wizard.simulate();
    } else if (move.type == Type.THROW) {
      Snaffle snaffle = (Snaffle)move.target;
      Vector v = new Point(move.x, move.y).sub(wizard.position).normalize().dot(move.thrust).dot(0.75); //.dot(1);
      snaffle.speed = snaffle.speed.add(v);
      snaffle.simulate();
    }
  }

  public void initRound() {
    nextGameState = null;
    snafflesCache = null;
    resetGrabbedSnaffles();
    
    updateMagicGauges();
  }

  private void updateMagicGauges() {
    players[0].gauge = Math.min(100, players[0].gauge+1);
    players[1].gauge = Math.min(100, players[1].gauge+1);
  }

  private void resetGrabbedSnaffles() {
    for (Entity e : allEntities) {
      if (isAWizard(e)) {
        Wizard w = (Wizard)e;
        w.setGrabbedSnaffle(null);
      }
    }
  }

  private boolean isAWizard(Entity e) {
    return e.type == EntityType.WIZARD || e.type == EntityType.OPPONENT_WIZARD;
  }

  public void tearDown() {
    // if needed, from Referee
  }

  public void readRoundValue(Scanner in) {
    rounds++;
    initRound();
    
    List<Entity> newEntities = new ArrayList<>();
    
    players[0].bludgerAvoidance = Math.max(0, players[0].bludgerAvoidance-1);

    // Data from CG
    if (doUnitTests) {
      System.err.println("setMagicGauge("+players[0].gauge+");");
    }
    int entities = in.nextInt();
    for (int i = 0; i < entities; i++) {
      int entityId = in.nextInt();
      EntityType entityType = EntityType.valueOf(in.next());
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int state = in.nextInt();
      if (inversed()) {
        // inverse all X if in inversed mode !
        x = MAX_X-x;
        vx = -vx;
      }
      if (doUnitTests ) {
        System.err.println("addEntity("+entityId+", "+entityType+", "+x+", "+y+", "+vx+", "+vy+", "+state+");");
      }
      switch (entityType) {
        case WIZARD:
          Wizard wizard = getWizard(entityId);
          wizard.update(x, y, vx, vy, state);
          allEntities.remove(wizard);
          newEntities.add(wizard);
          break;
        case OPPONENT_WIZARD:
          Wizard oppwizard = getOppWizard(entityId);
          oppwizard.update(x, y, vx, vy, state);
          allEntities.remove(oppwizard);
          newEntities.add(oppwizard);
          break;
        case SNAFFLE:
          Snaffle snaffle = getSnaffle(entityId);
          snaffle.update(x, y, vx, vy);
          checkGrabbedSnaffle(newEntities, snaffle);
          allEntities.remove(snaffle);
          newEntities.add(snaffle);
          break;
        case BLUDGER:
          Bludger bludger = getBludger(entityId);
          bludger.udpate(x, y, vx, vy);
          allEntities.remove(bludger);
          newEntities.add(bludger);
          break;
      }
    }
    updateScore(allEntities);
    System.err.println("SCORE  :"+players[0].score +" / "+players[1].score);

    allEntities = newEntities;
    updateBludgers();
  }

  private void updateScore(List<Entity> remainingEntities) {
    for (Entity entity : remainingEntities) {
      Snaffle snaffle = (Snaffle)entity;
      if (snaffle.position.x > MAX_X / 2) {
        players[0].score++;
      } else {
        players[1].score++;
      }
    }
  }

  private void checkGrabbedSnaffle(List<Entity> entities, Snaffle snaffle) {
    snaffle.grabbedBy = null;
    for (Entity e : entities) {
      if (isAWizard(e)) {
        if (snaffle.position.squareDistance(e.position) < e.radius*e.radius) {
          Wizard w = (Wizard)e;
          w.setGrabbedSnaffle(snaffle);
          snaffle.grabbedBy = w;
          System.err.println("SNAFFLE "+snaffle.entityId+" GRABBED BY "+w.entityId);
        }
      }
    }
  }

  private void updateBludgers() {
    List<Wizard> allWizards = getAllWizards();
    for (Entity e:  allEntities) {
      if (e.type == EntityType.BLUDGER) {
        ((Bludger)e).chooseTarget(allWizards);
      }
    }
  }

  private Wizard getWizard(int entityId) {
    for (Entity e : allEntities) {
      if (e.entityId == entityId) return (Wizard)e;
    }
    Wizard w = new Wizard(entityId, EntityType.WIZARD, 0, 0, 0, 0, 0);
    allEntities.add(w);
    return w;
  }

  private Wizard getOppWizard(int entityId) {
    for (Entity e : allEntities) {
      if (e.entityId == entityId) return (Wizard)e;
    }
    Wizard w = new Wizard(entityId, EntityType.OPPONENT_WIZARD, 0, 0, 0, 0, 0);
    allEntities.add(w);
    return w;
  }


  private Bludger getBludger(int entityId) {
    for (Entity b : allEntities) {
      if (b.entityId == entityId) {
        return (Bludger)b;
      }
    }
    Bludger b = new Bludger(entityId, 0, 0, 0, 0);
    allEntities.add(b);
    return b;
  }

  private Snaffle getSnaffle(int entityId) {
    for (Entity e : allEntities) {
      if (e.entityId == entityId) return (Snaffle)e;
    }
    Snaffle s = new Snaffle(entityId, 0, 0, 0, 0);
    allEntities.add(s);
    return s;
  }

  public void readInitValues(Scanner scanner) {
    // create players
    players[0] = new Player();
    players[1] = new Player();
    // from CG

    myTeamId = scanner.nextInt();
  }

  public Player getSelfPlayer() {
    return players[0];
  }

  public Player getPlayer(int index) {
    return players[index];
  }

  public Point center() {
    return CENTER;
  }

  public List<Snaffle> getOrderedSnafflesOnPosition(final Point position) {
    List<Snaffle> snaffles = getSnaffles();
    snaffles.sort(new Comparator<Snaffle>() {
      @Override
      public int compare(Snaffle s1, Snaffle s2) {
        return Integer.compare((int)s1.position.squareDistance(position), (int)s2.position.squareDistance(position));
      }
    });
    return snaffles;
  }

  
  public void simulateOneTurn() {
    for (Entity e : allEntities) {
      e.simulate();
    }
  }

  public boolean inversed() {
    return myTeamId == 1;
  }

  public List<Snaffle> getSnaffles() {
    if (snafflesCache == null) {
      snafflesCache = getEntities(EntityType.SNAFFLE);
    }
    return snafflesCache;
  }

  /** don't do 2 loops (getWizard + getOppWizard) */
  public List<Wizard> getAllWizards() {
    List<Wizard> ents = new ArrayList<>();
    for (Entity e : allEntities) {
      if (e.type == EntityType.WIZARD || e.type == EntityType.OPPONENT_WIZARD) {
        ents.add((Wizard)e);
      }
    }
    return ents;
  }
  
  public List<Wizard> getWizards() {
    return getEntities(EntityType.WIZARD);
  }

  public List<Wizard> getOppWizards() {
    return getEntities(EntityType.OPPONENT_WIZARD);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Entity> List<T> getEntities(EntityType type) {
    List<T> ents = new ArrayList<>();
    for (Entity e : allEntities) {
      if (e.type == type) {
        ents.add((T)e);
      }
    }
    return ents;
  }

  public List<Bludger> getBludgers() {
    return getEntities(EntityType.BLUDGER);
  }
  
  public Point snaffleGravityCenter() {
    long sumX = 0, sumY=0;
    if (getSnaffles().size() == 0) {
      return Playfield.center;
    }
    for (Snaffle snaffle : getSnaffles()) {
      sumX+=snaffle.position.x;
      sumY+=snaffle.position.y;
    }
    return new Point(1.0*sumX/getSnaffles().size(), 1.0*sumY/getSnaffles().size());
  }

  public Entity getEntityById(int i) {
    for (Entity entity : allEntities) {
      if (entity.entityId == i) {
        return entity;
      }
    }
    return null;
  }
}