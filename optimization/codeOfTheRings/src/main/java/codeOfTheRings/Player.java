package codeOfTheRings;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Player {
  private static final int BEAM_K = 600;
  static char currentLetters[] = new char[30];

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    String magicPhrase = in.nextLine();
    new Player().play(magicPhrase);
    
    
  }

  public void play(String magicPhrase) {
    Node.goal = magicPhrase.toCharArray();

    Node best = null;
    int bestLength = Integer.MAX_VALUE;
    
    PriorityQueue<Node> toExplore = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.totalEstimatedCost(), n2.totalEstimatedCost()));
    
    Node first = new Node();
    List<Node> parents = new ArrayList<>();
    List<Node> childs = new ArrayList<>();
    parents.add(first);
    for (int p=0;p<magicPhrase.length();p++) {
      int c;
      if (Node.goal[p] == ' ') {
        c = 0;
      } else {
        c = Node.goal[p] - 'A' +1;
      }
      // System.err.println("Goal "+Node.goal[p]+" is target index "+c);
      
      for (Node parent : parents) {
        for (int i=0;i<30;i++) {
          Node next = new Node();
          next.copyFromParent(parent);
          
          
          next.doCharAt(c, i);
          childs.add(next);
          
          if (p == magicPhrase.length()-1 && next.lengthFrom < bestLength) {
            bestLength = next.lengthFrom;
            best = next;
          }
        }
      }
      childs.sort((c1, c2) -> Integer.compare(c1.lengthFrom, c2.lengthFrom));
      parents.clear();
      for (int i=0;i<Math.min(childs.size(), BEAM_K);i++) {
        parents.add(childs.get(i));
      }
      childs.clear();
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
