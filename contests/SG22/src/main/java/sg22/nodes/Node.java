package sg22.nodes;

import java.util.ArrayList;
import java.util.List;

import fast.array.FastArray;
import sg22.Application;
import sg22.Cards;
import sg22.GamePhase;
import sg22.Hand;
import sg22.Simulator;
import sg22.State;
import sg22.Actions.Action;

public class Node {
  private static final Simulator simulator = new Simulator();
  
  public Node parent;
  public State s = new State();
  public Action action = new Action();
  
  public FastArray<Node> children = new FastArray<>(Node.class, 80);
  
  
  void copyFrom(Node model) {
    this.s.copyFrom(model.s);
  }

  public void compute() {
    children.clear();
    if (this.s.phase == GamePhase.END) return;
    
    
    if (s.phase == GamePhase.MOVE) {
      for (int j=1;j<=8;j++) {
        System.err.println("Move @ "+j);
        int currentLoc = s.agents[0].location;
        if (currentLoc != -1 && j == 8) continue; // pas de 8eme coup si on est pas sur le poste 8!
        
        int decal = currentLoc + j;
        int newLoc = decal % 8;
        
        Node node = NodeCache.getWithParent(this);
        node.action.moveTo(newLoc);
        simulator.simulate(node);
        
        children.add(node);

        
        // other card to get ?
        if (s.me.permanentDailyRoutineCards > 0) {
          for (int k=1;k<=s.me.permanentDailyRoutineCards;k++) {
            Node cardBefore = NodeCache.getWithParent(this);
            cardBefore.action.moveToAndGet(newLoc, (8 + newLoc-k) % 8);
            simulator.simulate(cardBefore);
            children.add(cardBefore);
  
  
            
            Node cardAfter= NodeCache.getWithParent(this);
            cardAfter.action.moveToAndGet(newLoc, (newLoc+k) % 8);
            simulator.simulate(cardAfter);
            children.add(cardAfter);
          }
          
        }

      }
      
    } else if (s.phase == GamePhase.THROW) {
      int start = 0;
      if (this.action.isThrow() && this.action.target != -1) {
        start = this.action.target; // if one card already thrown, don't rethrow cards lesser than the one above
      }
      boolean hasOnethrow = false;
      for (int i=0;i<=Cards.BONUS;i++) {
        if (s.hand.getHandCount(i)<=0) continue;
        hasOnethrow = true;

        if (i< start) {
          continue;
        }
        
        Node node = NodeCache.getWithParent(this);
        node.action.throwCard(i);
        simulator.simulate(node);
        
        children.add(node);
        
      }
      if (!hasOnethrow) {
        Node node = NodeCache.getWithParent(this);
        node.action.throwCard(-1);
        simulator.simulate(node);
        
        children.add(node);
      }
      
      
      
    } else if (s.phase == GamePhase.GIVE) {
      boolean foundone = false;
      for (int i=0;i<=8;i++) {
        if (s.hand.getHandCount(i)<=0) continue;

        Node node = NodeCache.getWithParent(this);
        node.action.give(i);
        simulator.simulate(node);
        children.add(node);

        foundone = true;
      }
      if (!foundone) {
        // add a fake give to get our 2 debt cards :/
        Node node = NodeCache.getWithParent(this);
        node.action.give(-1);
        simulator.simulate(node);
        children.add(node);
        
      }
    } else if (s.phase == GamePhase.PLAY) {
      
      for (int i=0;i<8;i++) {
        if (s.hand.getHandCount(i)<=0) continue;
        
        if (i == Cards.TASK_PRIORIZATION) {
          
          for (int discard=0;discard<=8;discard++) {
            if (s.hand.getHandCount(discard) <= 0) continue;
            if (discard == Cards.TASK_PRIORIZATION && s.hand.getHandCount(discard) <= 1) continue; // need one to play the card & one to discard
            
            for (int get=0;get<=8;get++) {
              if (discard == get) continue;
              
              if (s.cardsOnDesks.count[get] <= 0) continue;
              
              Node node = NodeCache.getWithParent(this);
              node.action.taskPriorization(discard, get);
              simulator.simulate(node);
              children.add(node);

            }
          }
          
        } else if (i == Cards.CI) {
          for (int index=0;index<=8;index++) {
            if (this.s.hand.getHandCount(index) == 0) continue;
            if (index == Cards.CI && s.hand.getHandCount(index) <= 1) continue; // need one to play the card & one to use
            
            Node ci = NodeCache.getWithParent(this);
            ci.action.ci(index);
            simulator.simulate(ci);
            children.add(ci);
          }
          
        } else {
          if (i == Cards.TRAINING) {
            // if (this.action.isPlay() && !this.action.isTraining() ) continue; // on ne peut jouer le TRAINING qu'au premier tour ou apres un training
            
            Node node = NodeCache.getWithParent(this);
            node.action.training();
            simulator.simulate(node);
            children.add(node);

          } else if (i == Cards.CODING) {
            // if (this.action.isPlay() && !this.action.isTraining() && !this.action.isCoding() ) continue; // on ne peut jouer le CODING qu'au premier tour ou apres un training ou apres coding

            Node node = NodeCache.getWithParent(this);
            node.action.coding();
            simulator.simulate(node);
            children.add(node);

          } else if (i == Cards.DAILY_ROUTINE) {
            Node node = NodeCache.getWithParent(this);
            node.action.daily();
            simulator.simulate(node);
            children.add(node);

          } else if (i == Cards.ARCHITECTURE_STUDY) {
            Node node = NodeCache.getWithParent(this);
            node.action.archi();
            simulator.simulate(node);
            children.add(node);


          } else if (i == Cards.CODE_REVIEW) {
            Node node = NodeCache.getWithParent(this);
            node.action.codeReview();
            simulator.simulate(node);
            children.add(node);


          } else if (i == Cards.REFACTORING) {
            Node node = NodeCache.getWithParent(this);
            node.action.refactoring();
            simulator.simulate(node);
            children.add(node);

          }
        }

      }
      
      // extra actions for play : 
      // WAIT  : will end the plays without playing
      Node node = NodeCache.getWithParent(this);
      node.action.doWait();
      simulator.simulate(node);
      children.add(node);

      
    } else if (s.phase == GamePhase.RELEASE) {
      // RELEASES
      for (Application a : State.applications) {
        int debt;
        if ((debt = a.canFinish(s.hand)) != -1) {
          if (s.agents[0].score < 4 || debt == 0) {
            Node release = NodeCache.getWithParent(this);
            release.action.release(a);
            simulator.simulate(release);
            children.add(release);

          }
        }
      }
      
      // extra actions for play : 
      // WAIT  : will end the plays without releasing
      Node node = NodeCache.getWithParent(this);
      node.action.doWait();
      simulator.simulate(node);
      children.add(node);

    }
    
  }



  public void copyFrom(State state) {
    s.copyFrom(state);
    action.root();
  }

  public void debug() {
    debug("");
  }

  private void debug(String decal) {
    for (int i=0;i<children.length;i++) {
      Node node = children.elements[i];
      System.err.println(decal+node);
      if (!node.children.isEmpty()) {
        //node.debug(decal+"\t");
      }
    }
  }
  
  static List<Node> listOfActions = new ArrayList<>();

  public List<Node> listOfActions() {
    listOfActions.clear();
    Node first = this;
    while (first.parent != null) {
      listOfActions.add(0, first);
      first = first.parent;
    }
    return listOfActions;
  }

  public void debugActionList() {
    listOfActions.clear();
    Node first = this;
    while (first.parent != null) {
      listOfActions.add(0, first);
      first = first.parent;
    }
  
    System.err.println("Suite : ");
    for (Node n : listOfActions) {
      System.err.print(n.action+"  => ");
    }
    System.err.println();
  }
  
  
  @Override
  public String toString() {
    if (action.isRelease()) {
      return action +" : P["+s.phase+"] debt = " + (this.s.hand.get(Hand.LOC_HAND, Hand.DEBT) - this.parent.s.hand.get(Hand.LOC_HAND, Hand.DEBT));
    } else {
      return action +" : P["+s.phase+"]";
    }
  }

  public static Node root(State state) {
    Node node = NodeCache.getWithParent(null);
    node.s.copyFrom(state);
    
    return node;
  }

  public static Node init() {
    return new Node();
  }
}
