package planner.actionplanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import calmBronze.Table;
import planner.PlannerPlayer;
import planner.actions.Action;
import planner.actions.DropItem;
import planner.actions.UseDishWasher;
import planner.actions.UseEquipmentIngredient;
import planner.actions.UseItemOnTable;
import planner.actions.UseKnife;
import planner.actions.UseOven;
import planner.actions.Wait;
import planner.state.Item;
import planner.state.State;

public class Planner {
  private static final Wait WAIT = new Wait(5);
  private static final Action DROP = new DropItem();
  List<Action> staticActions = new ArrayList<>();
  
  public Planner() {
    staticActions.add(new UseEquipmentIngredient(State.equipmentBlueberries, Item.BLUEBERRIES));
    staticActions.add(new UseEquipmentIngredient(State.equipmentStrawberries, Item.STRAWBERRIES));
    staticActions.add(new UseEquipmentIngredient(State.equipmentIceCream, Item.ICE_CREAM));
    staticActions.add(new UseEquipmentIngredient(State.equipmentDough, Item.DOUGH));
    staticActions.add(new UseKnife());
    staticActions.add(new UseOven());
    staticActions.add(new UseDishWasher());

  }
  
  public List<Node> findPlan(State state, State goal) {
    ArrayList<Node> list = new ArrayList<>();
    ArrayList<Action> allActions = new ArrayList<>();
    if (goal.isCompatible(state)) {
      System.err.println("On est déjà compatible ! ");
      return list;
    }
    
    Node root = new Node();
    root.state = state;
    root.parent = null;
    root.totalCost = 0;
    
    PriorityQueue<Node> openList= new PriorityQueue<>(new Comparator<Node>() {
      @Override
      public int compare(Node o1, Node o2) {
        return Integer.compare(o1.totalCost, o2.totalCost);
      }
    });
    
    int bestCost = 210;
    Node best = null;
    
    openList.add(root);
    int simulation = 0;
    while (!openList.isEmpty()) {
      Node current= openList.poll();
      if (current.turn > bestCost) {
        break; // the best in the openList is worst than our solution, skip all
      }
      simulation++;
      if (PlannerPlayer.DEBUG_PLANNER) {
        System.err.println("Opening : " +current.toString());
      }
      
      allActions.clear();
      allActions.addAll(staticActions);
      if (current.state.ovenContents != 0) {
        allActions.add(WAIT);
      }
      if (current.state.agent1.items != 0) {
        allActions.add(DROP);
      }
      for (Entry<Table, Integer> tableEntry : current.state.tables.entrySet()) {
        allActions.add(new UseItemOnTable(tableEntry.getKey(), tableEntry.getValue()));
      }
      for (Action action : allActions) {
        if (action.prerequisites(current.state)) {
          Node child = new Node();
          child.parent = current;
          child.state.copyFrom(current.state);
          child.action = action;
          action.applyEffect(child.state);
          child.turn = child.state.turn;
          child.totalCost = child.turn + estimateTurnToGoal(child.state, goal); // TODO real cost here
          if (child.state.isCompatible(goal) && child.totalCost < bestCost) {
            bestCost = child.turn + 0;
            best = child;
            System.err.println("Found a compatible actions in "+ child.turn +"!  " + child.toString());
          } else {
            if (child.turn < bestCost) {
              openList.add(child);
            }
          }
        }
      }
    }

    System.err.println("Simulations :" + simulation);
    if (best == null) {
      return list;
    } else {
      System.err.println("Result : " + best.toString());
      Node c = best;
      while (c.action != null) {
        list.add(0, c);
        c=c.parent;
      }
      
      return list;
    }
  }

  private int estimateTurnToGoal(State state, State goal) {
    int remaining = goal.agent1.items & ~state.agent1.items;
    
    int total = 0;
    if ((remaining & Item.BLUEBERRIES_TART) != 0) {
      int costForBlueBerriesTart;
      if (worldHas(state, Item.BLUEBERRIES_TART) || state.ovenContents == Item.BLUEBERRIES_TART) {
        costForBlueBerriesTart = 3; // le temps d'aller la chercher
      } else if (state.ovenContents == Item.RAW_TART) {
        costForBlueBerriesTart = (state.ovenTimer-state.turn) +3;
      } else  if (worldHas(state, Item.RAW_TART)) {
        costForBlueBerriesTart = 10+4;
      } else if (worldHas(state, Item.CHOPPED_DOUGH)) {
        costForBlueBerriesTart = 10+5;
      } else {
        costForBlueBerriesTart = 10+6;
      }
      total+= costForBlueBerriesTart;
      remaining &= ~Item.BLUEBERRIES_TART; // remove from needed
    }
    
    if ((remaining & Item.CROISSANT) != 0) {
      int costForCroissant;
      if (worldHas(state, Item.CROISSANT) || state.ovenContents == Item.CROISSANT) {
        costForCroissant = 3; //le temps d'aller la chercher
      } else if (state.ovenContents == Item.DOUGH) {
        costForCroissant = (state.ovenTimer - state.turn) +3;
      } else {
        costForCroissant = 3+10;
      }
      total+=costForCroissant;
      remaining &= ~Item.CROISSANT; // remove from needed
    }
    
    total += Integer.bitCount(remaining) * 3;
    return total;
  }

  private boolean worldHas(State state, int item) {
    for (Integer i : state.tables.values()) {
      if (i == item) return true;
    }
    return false;
  }

}
