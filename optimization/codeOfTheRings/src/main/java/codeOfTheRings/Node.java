package codeOfTheRings;

public class Node {
  public static char[] goal;
  
  public int state[] = new int[30];
  public int position;
  public int lengthFrom;
  public int index;
  public Node parent;
  
  public int actionTargetChar;

  int score = 0;
  
  @Override
  public String toString() {
    return "P:"+position+" "+actionTargetChar+" lengthAfter = "+lengthFrom;
  }
  
  
  public void copyFromParent(Node model) {
    System.arraycopy(model.state, 0, state, 0, 30);
    this.position = model.position;
    this.lengthFrom = model.lengthFrom;
    this.parent = model;
    this.index = model.index+1;
    this.score = 0;
  }

  public void doCharAt(int targetChar, int newPosition) {
     int depCost;
     if (newPosition < position) {
       depCost = Math.min(position-newPosition, newPosition-position+30);
     } else {
       depCost = Math.min(newPosition-position, position-newPosition+30);
     }
     
     int depChange;
     if (state[newPosition] < targetChar) {
       depChange = Math.min(targetChar-state[newPosition], state[newPosition]-targetChar+27);
     } else {
       depChange = Math.min(state[newPosition]-targetChar, targetChar-state[newPosition]+27);
     }
     
     this.position = newPosition;
     this.actionTargetChar = targetChar;
     this.state[position] = targetChar;
     this.lengthFrom += depCost + depChange + 1;
     
     
     calculateScore();
  }
  
  
  private void calculateScore() {
    score = 0;
    
    score += -1 * 10 * lengthFrom;
    
    for (int i=index;i<goal.length;i++) {
      score += Player.remainingLetters[Player.letterIndex(Node.goal[i])];
    }
    
  }


  public static String outputFromTo(Node from, Node to) {
    String output ="";
    // first moves
    if (to.position < from.position) {
      if (from.position-to.position <  to.position-from.position+30) {
        output+=mult("<", from.position-to.position);
      } else {
        output+=mult(">", to.position-from.position+30);
      }
    } else {
      if (to.position-from.position <  from.position-to.position+30) {
        output+=mult(">", to.position-from.position);
      } else {
        output+=mult("<", from.position-to.position+30);
      }
    }

    // push
    if (from.state[to.position] < to.actionTargetChar) {
      if (to.actionTargetChar - from.state[to.position] < from.state[to.position] - to.actionTargetChar + 27) {
        output+=mult("+", to.actionTargetChar - from.state[to.position]);
      } else {
        output+=mult("-", from.state[to.position] - to.actionTargetChar + 27);
      }
    } else {
      if (from.state[to.position] - to.actionTargetChar< to.actionTargetChar - from.state[to.position] + 27) {
        output+=mult("-", from.state[to.position] - to.actionTargetChar);
      } else {
        output+=mult("+", to.actionTargetChar - from.state[to.position] + 27);
      }
    }

    output+=".";
    
    return output;
  }

  public int totalEstimatedCost() {
    return lengthFrom + 4 * (goal.length - index);
  }
  
  
  private static String mult(String action, int count) {
    if (count == 0) return "";
    
    String output = "";
    for (int i=0;i<count;i++) {
      output+=action;
    }
    return output;
  }


}
