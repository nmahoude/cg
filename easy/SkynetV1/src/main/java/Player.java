import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
  static List<Node> nodes = new ArrayList<>();
  private static List<Link> links = new ArrayList<>();
  
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        
        // create nodes
        for (int i=0;i<N;i++) {
          nodes.add(new Node(i));
        }
        for (int i = 0; i < L; i++) {
            int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            int N2 = in.nextInt();
            Link link = new Link(nodes.get(N1), nodes.get(N2));
            links.add(link);
        }
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            nodes.get(EI).isGateway = true;
        }
        // print links
//        for (Link link : links) {
//          System.err.println("real Links : "+link.node1.index+"->"+link.node2.index);
//        }
        // game loop
        while (true) {
            int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Node agentNode = nodes.get(SI);
            // find the closest gateway should be sufficient
            Path path = agentNode.findShortestPathToGateway();
            Link theLinkToBreak = null;
            if (path != null && path.nodes.size() > 1) {
              Node nextNode = path.nodes.get(1);
              theLinkToBreak = agentNode.findLinkTo(nextNode);
              agentNode.links.remove(theLinkToBreak);
              nextNode.links.remove(theLinkToBreak);
            }
            
            if (theLinkToBreak != null) { 
              System.out.println(""+theLinkToBreak.node1.index+" "+theLinkToBreak.node2.index);
            } else {
              System.out.println("0 1");
            }
        }
    }
    
    static class Path {
      List<Node> nodes = new ArrayList<>();

      public boolean contains(Node potentialNode) {
        return nodes.contains(potentialNode);
      }
      void debug(String message) {
//        String debugString = "";
//        for (Node node :nodes) {
//          debugString+=""+node.index+"->";
//        }
//        
//        System.err.println(message+ " / Size:("+nodes.size() + ") "+debugString);
      }
    }
    
    static class Node {
      List<Link> links = new ArrayList<>();
      private int index;
      boolean isGateway = false;
      
      public Node(int index) {
        this.index = index;
      }

      public Link findLinkTo(Node nextNode) {
        for (Link link : this.links) {
          if ((link.node1 == this && link.node2 == nextNode)
            || (link.node2 == this && link.node1 == nextNode)) {
            return link;
          }
        }
//        System.err.println("Oops, can't find link between nodes");
        return null; //oops ?
      }

      public Path findShortestPathToGateway() {
//        System.err.println("Finding shortest path from "+this.index);
        Path path = new Path();
        path.nodes.add(this);
        Path chosenPath = findShortestPathToGateway(path);
        chosenPath.debug("chosen Path");
        return chosenPath;
      }

      public Path findShortestPathToGateway(Path path) {
        path.debug("Find shortest base (current:"+this.index+") ");
        int minDist = 10000;
        Path shortestPath = null;
        for (Link link : this.links) {
//          System.err.println("testing link : "+link.node1.index+","+link.node2.index);
          Node potentialNode = link.node1 != this ? link.node1 : link.node2;
          if (path.contains(potentialNode)) {
//            path.debug("dead end to "+potentialNode.index);
            continue;
          }
          Path newPath = new Path();
          newPath.nodes.addAll(path.nodes);
          newPath.nodes.add(potentialNode);
          if (potentialNode.isGateway) {
            newPath.debug("gateway");
            if (newPath.nodes.size() < minDist) {
              minDist = newPath.nodes.size();
              shortestPath = newPath;
//              newPath.debug("New shortest");
            }
            continue;
          }
          Path localShortest = potentialNode.findShortestPathToGateway(newPath);
          if (localShortest != null && localShortest.nodes.size() < minDist) {
            minDist = localShortest.nodes.size();
            shortestPath = localShortest;
//            path.debug("New shortest");
          }
        }
        if (shortestPath != null) {
          shortestPath.debug("shortest path");
        } else {
          path.debug("no path found from");
        }
        return shortestPath;
      }

    }
    
    static class Link {
      public Link(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
        node1.links.add(this);
        node2.links.add(this);
      }

      Node node1;
      Node node2;
    }
}