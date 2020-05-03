package calm.astar;

import calm.Item;
import calm.ItemMask;
import calm.P;
import calm.State;

public class AStarNode implements Comparable<AStarNode>{
  private static final int MAX_CHILDS = 20;
  private static final int MAX_ITEMS = 15;

  private static final int DROP_PENALTY = 15;

  AStarNode parent;
  
  Action action;
  int actionPtr;
  
  Item myHands = new Item(P.INVALID); // pos + item
  P hisPos = P.INVALID; // TODO static if we don't move it ?
  Item oven = new Item(P.INVALID);
  int ovenTimer = 0;
  Item onTables = new Item(P.INVALID);
  
  Item items[] = new Item[MAX_ITEMS]; // only the items on tables
  int itemsFE = 0;
  
  
  AStarNode[] childs = new AStarNode[MAX_CHILDS];
  int childsFE = 0;
  
  int turns; // turns for this action
  int totalTurns; // how many turns to here from start
  int estimatedRemainingTurns; // estimated remaining turns to the end goal (desert)
  

  public AStarNode() {
  }

	public void init(State state) {
		myHands.mask = state.me.hands.mask;
		myHands.pos = state.me.pos;
		hisPos = state.him.pos;
		oven.mask = state.ovenContents.mask;
		ovenTimer = state.ovenTimer;
		
		// TODO init items
	}


	public int getTotalTurns() {
		return totalTurns+estimatedRemainingTurns;
	}
	
	public void find(Item goal) {
		initChilds();
		fillChildActions();
		for (int i= 0;i<childsFE;i++) {
			childs[i].applyAction(this, goal);
		}
	}

	
  private void initChilds() {
  	for (int i=0;i<MAX_CHILDS;i++) {
  		childs[i] = new AStarNode();
  	}
	}

	private void fillChildActions() {
    childsFE =0;

    if (oven.hasDough() || oven.hasRawTart()) {
      childs[childsFE++].action = Action.WAIT;
    }

    if (!myHands.isEmpty()) {
      childs[childsFE++].action = Action.DROP;
    }
    
    for (int i=0;i<itemsFE;i++) {
      if (items[i] == null) continue;
      childs[childsFE].action = Action.GRAB;
      childs[childsFE].actionPtr = i;
      childsFE++;
    }
    if (!myHands.hasPlate() 
        && !myHands.hasDough() 
        && !myHands.hasChoppedDough() 
        && !myHands.hasStrawberries()
        && !myHands.hasRawTart()) {
      childs[childsFE++].action = Action.USE_DISH;
    }
    if (myHands.hasStrawberries()) {
      childs[childsFE++].action = Action.USE_CHOPPER;
    }
    if (myHands.hasDough()) {
      childs[childsFE++].action = Action.USE_CHOPPER;
      if (oven.mask == 0) childs[childsFE++].action = Action.USE_OVEN_IN;
    }
    if (myHands.hasRawTart() && oven.mask == 0) {
      childs[childsFE++].action = Action.USE_OVEN_IN; 
    }
    if (myHands.isEmpty() 
        || (myHands.hasPlate() && !myHands.hasBlueBerries())
        || (myHands.hasChoppedDough())) {
      childs[childsFE++].action = Action.USE_BB;
    }
    if (myHands.isEmpty() || (myHands.hasPlate() && !myHands.hasIceCream())) {
      childs[childsFE++].action = Action.USE_IC;
    }
    if (myHands.isEmpty()) {
      childs[childsFE++].action = Action.USE_SW;
      childs[childsFE++].action = Action.USE_DOUGH;
    }
    if ((myHands.isEmpty() || myHands.hasPlate()) 
        && (oven.mask == ItemMask.CROISSANT || oven.mask == ItemMask.BLUEBERRIES_TART)) {
      childs[childsFE++].action = Action.USE_OVEN_OUT;
    }
  }
  
  /**
   * All actions will/should be valid
   */
  private void applyAction(AStarNode parent, Item goal) {
    initFrom(parent);
    
    switch(action) {
    case DROP:
      // TODO where to put ?, add items
      Item item = new Item(P.INVALID);
      item.mask = myHands.mask;
      items[itemsFE++] = item;
      
      myHands.reset(0);
      
      turns= DROP_PENALTY + 1;
      break;
    case GRAB:
      Item grabbed = items[actionPtr];
      myHands.mask = grabbed.mask;
      items[actionPtr] = null;
      
      turns = 3;// TODO how many turns to grab the item ?
      break;
    case USE_BB:
      if (myHands.hasChoppedDough()) {
        myHands.mask = ItemMask.RAW_TART;
      } else {
        myHands.mask |= ItemMask.BLUEBERRIES;
      }
      turns=2; // TODO how many turns ?
      break;
    case USE_BELL:
      myHands.mask = 0;
      turns=2;
      // TODO finish !
      break;
    case USE_CHOPPER:
      if (myHands.hasStrawberries()) {
        myHands.reset(ItemMask.CHOPPED_STRAWBERRIES);
      } else {
        myHands.reset(ItemMask.CHOPPED_DOUGH);
      }
      turns=2; // TODO how many turns ?
      break;
    case USE_DISH:
      myHands.mask|=ItemMask.DISH;
      turns=2; // TODO how many turns ?
      break;
    case USE_DOUGH:
      myHands.mask = ItemMask.DOUGH;
      turns=2; // TODO how many turns ?
      break;
    case USE_IC:
      myHands.mask |= ItemMask.ICE_CREAM;
      turns=2; // TODO how many turns ?
      break;
    case USE_OVEN_IN:
      oven.mask = myHands.mask;
      ovenTimer = 10;
      myHands.mask = 0;
      turns=2; // TODO how many turns ?
      break;
    case USE_OVEN_OUT:
      myHands.mask |= oven.mask;
      oven.mask = 0;
      ovenTimer = 0;
      
      turns=2; // TODO how many turns ?
      // TODO if (turns > timer) burnt :(
      break;
    case USE_SW:
      myHands.mask = ItemMask.STRAWBERRIES;
      turns=2; // TODO how many turns ?
      break;
    case WAIT:
      // TODO only to wait oven, and then turns = waitTime for oven
      turns=1;
      ovenTimer = 1;
      break;
    default:
      throw new RuntimeException("unknwon action "+action);
    }
    
    // update the oven
    if (oven.mask != 0) {
      ovenTimer -=parent.turns;
      if (ovenTimer <= 0) {
        if (oven.hasCroissant() || oven.hasBlueBerriesTart()) {
          ovenTimer = 0;
          oven.mask = 0; // burnt;
        } else {
          ovenTimer +=10;
          if (oven.hasDough()) {
            oven.mask = ItemMask.CROISSANT;
          } else {
            oven.mask = ItemMask.BLUEBERRIES_TART;
          }
        }
      }
    }
    
    totalTurns+=turns;
    estimateRemainingTurns(goal);
  }

  private void initFrom(AStarNode parent) {
  	this.parent = parent;
    this.myHands.copyFrom(parent.myHands);
    this.oven.copyFrom(parent.oven);
    this.ovenTimer = parent.ovenTimer;
    
    this.hisPos = parent.hisPos;
    this.totalTurns = parent.totalTurns;
    this.turns = 0;
    this.childsFE = 0;
    this.itemsFE = 0;
    for (int i=0;i<parent.itemsFE;i++) {
//      if (parent.items[i] == null) continue;
      this.items[this.itemsFE++] = parent.items[i];
    }
  }
  
  
  private void estimateRemainingTurns(Item goal) {
    estimatedRemainingTurns = 0;
    
    
    if (goal.mask == myHands.mask) {
    	estimatedRemainingTurns = 0; 
    	return;
    }

    // all impossible combinations
    if (myHands.hasPlate() && !goal.hasPlate()) {
      estimatedRemainingTurns+= 1000;
    }
    if (myHands.hasChoppedStrawberries() && !goal.hasChoppedStrawberries()) {
    	estimatedRemainingTurns+= 1000;
    }
    if (myHands.hasIceCream() && !goal.hasIceCream()) {
    	estimatedRemainingTurns+= 1000;
    }
    if (myHands.hasBlueBerries() && !goal.hasBlueBerries()) {
    	estimatedRemainingTurns+= 1000;
    }
    if (myHands.hasBlueBerriesTart() && !goal.hasBlueBerriesTart()) {
    	estimatedRemainingTurns+= 1000;
    }
    if (myHands.hasCroissant() && !goal.hasCroissant()) {
    	estimatedRemainingTurns+= 1000;
    }
    
    
    
    
    if (!myHands.hasPlate() && goal.hasPlate()) {
      estimatedRemainingTurns+= 3;// TODO distances
    }
    if (goal.hasBlueBerriesTart() && !myHands.hasBlueBerriesTart()) {
      estimatedRemainingTurns+= 25; // TODO take present ingredient in the hands or tables into account
    }
    if (goal.hasCroissant() && !myHands.hasCroissant()) {
      estimatedRemainingTurns+= 10; // TODO take present ingredient in the hands or tables into account
    }
    if (goal.hasChoppedStrawberries() && !myHands.hasChoppedStrawberries()) {
    	if (myHands.hasStrawberries()) {
    		estimatedRemainingTurns+= 3; // TODO distances
    	} else {
    		estimatedRemainingTurns+= 5; // TODO distances
    	}
    }
    if (goal.hasBlueBerries() && !myHands.hasBlueBerries()) {
      estimatedRemainingTurns+=3;// TODO distances
    }
    if (goal.hasIceCream() && !myHands.hasIceCream()) {
      estimatedRemainingTurns+=3;// TODO distances
    }
  }


  public void calculateChilds(Item goal) {
    childsFE = 0;
    if (goal.mask == myHands.mask) {
      estimatedRemainingTurns = 0;
      return;
    }
  }

	public boolean isFinished() {
		return estimatedRemainingTurns == 0;
	}

	@Override
	public int compareTo(AStarNode o) {
		return Integer.compare(getTotalTurns(), o.getTotalTurns());
	}
	
	public String debug() {
		AStarNode current = this;
		String output = "";
		while (current.parent != null) {
			output = ""+current.action+" -> "+output;
			current = current.parent;
		}
		return ""+totalTurns+"/"+estimatedRemainingTurns+" => "+output;
	}
	
	@Override
	public String toString() {
		return debug();
	}

	public int getCurrentTurns() {
		return totalTurns;
	}

  public AStarNode first() {
    AStarNode current = this;
    while (current.parent != null && current.parent.parent != null) {
      current = current.parent;
    }
    return current;
  }
  
  public Action getAction() {
    return action;
  }
  public int getActionPtr() {
    return actionPtr;
  }
}
