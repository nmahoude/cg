package codeOfTheRings;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PlayerAStar {
  private static final int BEAM_K = 600;
  static char currentLetters[] = new char[30];

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    String magicPhrase = in.nextLine();
    new PlayerAStar().play(magicPhrase);
    
    
  }

  public void play(String magicPhrase) {
    Node.goal = magicPhrase.toCharArray();

    Node best = null;
    int bestLength = Integer.MAX_VALUE;
    
    PriorityQueue<Node> toExplore = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.totalEstimatedCost(), n2.totalEstimatedCost()));
    
    Node first = new Node();
    first.index = -1;
    toExplore.add(first);

    while(!toExplore.isEmpty()) {
      Node current = toExplore.poll();

      if (current.totalEstimatedCost() > bestLength) {
        continue; // too long sorry
      }
      
      if (current.index == Node.goal.length-1) {
        // the end, keep the best
        if (current.lengthFrom < bestLength) {
          bestLength = current.lengthFrom;
          best = current;
        }
        continue; // the end of the magic phrase
      }
      
      for (int i=0;i<30;i++) {
        Node next = new Node();
        next.copyFromParent(current);
        
        int c;
        if (Node.goal[next.index] == ' ') {
          c = 0;
        } else {
          c = Node.goal[next.index] - 'A' +1;
        }
        
        next.doCharAt(c, i);
        toExplore.add(next);
      }
    }
    
    System.err.println("Best is "+best+" with length "+bestLength);
    List<Node> inOrder = new ArrayList<>();
    Node current = best;
    while (current != null) {
      inOrder.add(0, current);
      current = current.parent;
    }
    
    
    String output="";
    for (int i=1;i<inOrder.size();i++) {
      output+=Node.outputFromTo(inOrder.get(i-1), inOrder.get(i));
    }
    
    System.out.println(output);

    
  }

}
