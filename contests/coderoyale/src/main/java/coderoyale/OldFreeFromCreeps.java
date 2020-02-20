package coderoyale;

import java.util.List;
import java.util.stream.Collectors;

import coderoyale.sites.Site;
import coderoyale.sites.Tower;
import coderoyale.units.Unit;
import trigonometry.Point;
import trigonometry.Vector;

public class OldFreeFromCreeps {

  public void freeFromCreeps() {
//    List<Unit> nearCreeps = him.creeps.stream()
//        .filter(u -> me.pos.dist(u.pos) < 200)
//        .collect(Collectors.toList());
//  if (nearCreeps.isEmpty()) return; // no enemy near
//  
//  Vector vec = new Vector(0,0);
//  // TODO verifier le rayon des tours et leur eloignement !
//  for (Tower tower : him.towers) {
//    if (!tower.protects(me.pos, 60)) continue; // no fear from his outside towers
//    Vector invDirection = new Point(me.pos.x, me.pos.y).sub(new Point(tower.attachedTo.pos.x, tower.attachedTo.pos.y));
//    double length = invDirection.length();
//    invDirection = invDirection.normalize().dot(1000.0 / length );
//    vec = vec.add(invDirection);
//  }
//  for (Tower tower : me.towers) {
//    if (tower.protects(me.pos, -60)) continue; // no help from inside towers
//    Vector invDirection = new Point(me.pos.x, me.pos.y).sub(new Point(tower.attachedTo.pos.x, tower.attachedTo.pos.y));
//    double length = invDirection.length();
//    invDirection = invDirection.normalize().dot(-1000 / length);
//    vec = vec.add(invDirection);
//  }
//  for (Unit creep : nearCreeps) {
//    Vector invDirection = new Point(me.pos.x, me.pos.y).sub(new Point(creep.pos.x, creep.pos.y));
//    double length = invDirection.length();
//    invDirection = invDirection.normalize().dot(100 / length);
//    vec = vec.add(invDirection);
//  }
//  
//  vec = vec.normalize();
//  Pos newPos = new Pos((int)(me.pos.x + 60.0 * vec.vx), (int)(me.pos.y + 60.0 * vec.vy));
//  System.err.println("Fleeing from creeps with direction " + vec);
//  System.err.println("current pos : " + me.pos + " desired pos : "+newPos);
//  me.moveTo(newPos);
  }
  
  public static boolean isFrontLine(Site site) {
//    List<Site> hisSites = getSiteByClosestDistance(site).stream()
//        .filter(s -> { return s != site; })
//        .filter(s -> { return s.structure.owner == 1;})
//        .collect(Collectors.toList());
//    for (Site s : hisSites) {
//      System.err.println("his : "+s);
//    }
//    List<Site> mySites = getSiteByClosestDistance(site).stream()
//        .filter(s -> { return s != site; })
//        .filter(s -> { return s.structure.owner == 0;})
//        .collect(Collectors.toList());
//    for (Site s : mySites) {
//      System.err.println("my : "+s);
//    }
//
//    double lengthToHis = Double.POSITIVE_INFINITY;
//    double lengthOfMines = Double.POSITIVE_INFINITY;
//    if (!hisSites.isEmpty()) {
//      lengthToHis = hisSites.get(0).pos.dist(site.pos);
//    }
//    if (!mySites.isEmpty()) {
//      for (Site mySite : mySites) {
//        for (Site hisSite : hisSites) {
//          double l = mySite.pos.dist(hisSite.pos);
//          if (l < lengthOfMines) {
//            lengthOfMines = l;
//          }
//        }
//      }
//    }
//    if (lengthToHis < lengthOfMines) {
//      return true; // closer from his sites
//    } else {
//      return false; // One of my site is closer
//    }
    return true;
  }
 
  private static void buildAvantPoste(List<Site> closestAvailableSites) {
//    // check if we can build an 'avant poste' for barracks
//    if (closestAvailableSites.isEmpty()) return;
//    Site site = closestAvailableSites.get(0);
//    
//    long betterBarracksCount = me.knightBarracks.stream()
//        .filter(barrack -> barrack.attachedTo.pos.dist(him.pos) < site.pos.dist(him.pos))
//        .count();
//    if (betterBarracksCount > 0) {
//      System.err.println("don't build advance post, better barrack exists");
//      return;
//    }
//    long towerProtected = me.towers.stream()
//          .filter(tower -> tower.protects(site.pos, 0))
//          .count();
//    if (towerProtected == 0) {
//      System.err.println("Don't build to far from tower");
//      return;
//    }
//    System.err.println("Build un poste avancé");
//    me.moveTo(site).then(site::buildKnightBarrack).end();
  }


}
