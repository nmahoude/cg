package dab;

import javax.print.attribute.standard.Sides;

import dab.fast.FastReader;

public class Player {

  public static void main(String args[]) {
      FastReader in = new FastReader(System.in);
      new Player().play(in);
  }

  private void play(FastReader in) {
    
    int boardSize = in.nextInt(); // The size of the board.
    State state = new State(boardSize);
    
    
    String playerId = in.nextString(); // The ID of the player. 'A'=first player, 'B'=second player.

    // game loop
    while (true) {
        int playerScore = in.nextInt(); // The player's score.
        int opponentScore = in.nextInt(); // The opponent's score.
        
        state.fillAll();
        
        
        int numBoxes = in.nextInt(); // The number of playable boxes.
        for (int i = 0; i < numBoxes; i++) {
            String boxName = in.nextString(); // The ID of the playable box.
            int x = boxName.charAt(0) - 'A';
            int y = boxName.charAt(1) - '1';
            System.err.println("Getting "+x+" "+y+" from "+boxName);
            Box box = state.box(x,y);
            
            // TODO optimize by reading next char directly?
            String sides = in.nextString(); // Playable sides of the box.
            if (sides.contains("T")) box.unsetEdge(Edge.TOP);
            if (sides.contains("R")) box.unsetEdge(Edge.RIGHT);
            if (sides.contains("B")) box.unsetEdge(Edge.BOTTOM);
            if (sides.contains("L")) box.unsetEdge(Edge.LEFT);
            
            System.err.println("box & sides : "+boxName+" "+sides);
        }

        for (Box b : state.boxes) {
          System.err.println(""+b.name+" "+b.setEdges);
        }
        
        double bestScore = Double.NEGATIVE_INFINITY;
        Edge bestEdge = null;
        for (Edge edge : state.allEdges) {
          if (edge.isSet()) continue;
          
          double score = 0;
          if (edge.boxes[1] == null) {
            int setEdges = edge.boxes[0].setEdges;
            if (setEdges == 3) {
              score = 100;
            } else if (setEdges == 2) {
              score = -100;
            } else {
              score = 0;
            }
          } else {
            int setEdges0 = edge.boxes[0].setEdges;
            int setEdges1 = edge.boxes[1].setEdges;
            
            if ((setEdges0 == 3 && setEdges1 == 2) || (setEdges0 == 2 && setEdges1 == 3)) {
              score = 10_000;
            } else if ((setEdges0 == 3 && setEdges1 == 3) || (setEdges0 == 3 & setEdges1 == 3)) {
              score = 5_000;
            } else if (setEdges0 == 3 || setEdges1 == 3) {
              score = 100;
            } else if (setEdges0 == 2 || setEdges1 == 2) {
              score = -100;
            } 
          }
           
          System.err.println(""+edge+" => "+score);
          if (score > bestScore) {
            bestEdge = edge;
            bestScore = score;
          }
        }

        Box chosenBox = bestEdge.boxes[0];
        
        
        System.err.println("Best edge : "+bestEdge);
        System.err.println("Best box : "+chosenBox);
        System.out.println(chosenBox.name+" "+letter(chosenBox, bestEdge));
    }
  }

  private String letter(Box box, Edge edge) {
    if (box.edges[Edge.TOP] == edge) return "T";
    if (box.edges[Edge.RIGHT] == edge) return "R";
    if (box.edges[Edge.BOTTOM] == edge) return "B";
    if (box.edges[Edge.LEFT] == edge) return "L";
    return null;
  }

  private String letter(int chosenEdge) {
    if (chosenEdge==Edge.TOP) return "T";
    if (chosenEdge==Edge.RIGHT) return "R";
    if (chosenEdge==Edge.BOTTOM) return "B";
    if (chosenEdge==Edge.LEFT) return "L";
    return "not found "+chosenEdge;
  }

  private int freeEdge(Box box) {
    if (!box.edges[Edge.TOP].isSet()) return Edge.TOP;
    if (!box.edges[Edge.RIGHT].isSet()) return Edge.RIGHT;
    if (!box.edges[Edge.BOTTOM].isSet()) return Edge.BOTTOM;
    if (!box.edges[Edge.LEFT].isSet()) return Edge.LEFT;
    return -1;
  }
}
