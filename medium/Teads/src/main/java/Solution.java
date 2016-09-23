import java.util.*;
import java.io.*;
import java.math.*;

class Solution {
  
 static List<EdgeRunner> runners = new ArrayList<>();
 
  static class Node {
    final int index;
    public int freeRunners = 0;
    
    EdgeRunner bestRunner = null;
    
    public Node(int index) {
      this.index = index;
    }
    void addEdge(Edge edge) {
      freeRunners++;
      freeEdges.add(edge);
    }
    Set<Edge> freeEdges = new HashSet<>();
  }
  static class EdgeRunner {
    boolean active = true;
    Node currentNode;
    int length; // length to this edge
    public void advance() {
      if (currentNode.freeEdges.size() > 1) {
        // wait
      } else if (currentNode.freeEdges.size() == 1) {
        Node nextNode;
        Edge edge = currentNode.freeEdges.iterator().next();
        nextNode = edge.node1 == currentNode ? edge.node2 : edge.node1;
        length++;
        nextNode.freeEdges.remove(edge);
        if (nextNode.freeEdges.size() == 0) {
          if (nextNode.bestRunner != null) {
            nextNode.bestRunner.desactivate();
          }
          this.desactivate();
        }
        if (nextNode.bestRunner != null ) {
          nextNode.bestRunner.desactivate();
        }
        nextNode.bestRunner = this;
        currentNode = nextNode;
      }
    }
    private void desactivate() {
      active = false;
    }
  }
  
  static class Edge {
    final Node node1;
    final Node node2;
    
    Edge(Node node1, Node node2) {
      this.node1 = node1;
      this.node2 = node2;
      node1.addEdge(this);
      node2.addEdge(this);
    }
  }
    static Node personsNodes[];
    
    public static void play(int n, Node[] nodes) {
      
    }
    public static void main(String args[]) {
      long time1 = System.currentTimeMillis();  
      Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of adjacency relations
        personsNodes = new Node[2*n];
        for (int i = 0; i < n; i++) {
          int xi = in.nextInt(); // the ID of a person which is adjacent to yi
          int yi = in.nextInt(); // the ID of a person which is adjacent to xi
          Node n1 = personsNodes[xi];
          Node n2 = personsNodes[yi];
          if (n1 == null) {
            n1 = new Node(xi);
            personsNodes[xi] = n1;
          }
          if (n2 == null) {
            n2 = new Node(yi);
            personsNodes[yi] = n2;
          }
          new Edge(n1, n2);
        }
        // 1st find the exit nodes
        for (int i=0;i<2*n;i++) {
          Node node = personsNodes[i];
          if (node != null) {
            if (node.freeEdges.size() == 1) {
              EdgeRunner runner = new EdgeRunner();
              runner.currentNode = node;
              runner.length = 0;
              runners.add(runner);
            }
          }
        }

        boolean stillOneActive = false;
        EdgeRunner lastOne = null;
        do {
          stillOneActive = false;
          for (EdgeRunner runner : runners) {
            if (runner.active) {
              lastOne = runner;
              runner.advance();
              if (runner.active) {
                stillOneActive = true;
              }
            }
          }
        } while( stillOneActive);
        
        // The minimal amount of steps required to completely propagate the advertisement
        System.out.println(lastOne.length);
        long time2 = System.currentTimeMillis();

        System.err.println(""+(time2-time1)+" ms");
    }
}