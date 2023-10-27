package codeOfTheRings;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
  private static final int BEAM_K = 250;
  static char currentLetters[] = new char[30];
  static int remainingLetters[] = new int[27];

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    String magicPhrase = in.nextLine();
    new Player().play(magicPhrase);
  }

  public void play(String magicPhrase) {
    Node.goal = magicPhrase.toCharArray();

    
    
    Node best = null;
    int bestLength = Integer.MAX_VALUE;
    
    Node first = new Node();
    List<Node> parents = new ArrayList<>();
    List<Node> childs = new ArrayList<>();
    parents.add(first);
    
    for (int i=0;i<27;i++) {
      remainingLetters[i] = 0;
    }
    for (int i=0;i<Node.goal.length;i++) {
      remainingLetters[letterIndex(Node.goal[i])]++; 
    }
    
    for (int p=0;p<magicPhrase.length();p++) {
      int c = letterIndex(Node.goal[p]);
      remainingLetters[c]--;
      
      
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
      childs.sort((c1, c2) -> Integer.compare(c2.score, c1.score));
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

  static int letterIndex(char letter) {
    int c;
    if (letter == ' ') {
      c = 0;
    } else {
      c = letter - 'A' +1;
    }
    return c;
  }

}
