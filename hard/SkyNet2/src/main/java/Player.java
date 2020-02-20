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

            Node agentNode = nodes.get(SI);

            // find the closest gateway should be sufficient
            Path path = agentNode.findShortestPathToGateway();
            
            Node before = path.nodes.get(path.nodes.size()-2);
            Node gateway = path.nodes.get(path.nodes.size()-1);
            Link theLinkToBreak = before.findLinkTo(gateway);
            before.links.remove(theLinkToBreak);
            gateway.links.remove(theLinkToBreak);
            
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
        String debugString = "";
        for (Node node :nodes) {
          debugString+=""+node.index+"->";
        }
        
        System.err.println(message+ " / Elts:("+nodes.size() + ") / Length: "+pathLength()+" "+debugString);
      }
      
      public int isBetterThan(Path shortestPath) {
        // check the gateway road to this path end
        Node gateway = nodes.get(nodes.size()-2);
        int countGatewayTo = 0;
        for (Link l : gateway.links) {
          countGatewayTo += (l.node1.isGateway || l.node2.isGateway) ? 1 : 0;
        }
        Node otherGateway = shortestPath.nodes.get(shortestPath.nodes.size()-2);
        int countGatewayToOther = 0;
        for (Link l : otherGateway.links) {
          countGatewayToOther += (l.node1.isGateway || l.node2.isGateway) ? 1 : 0;
        }
        return Integer.compare(countGatewayToOther, countGatewayTo );
      }
      public int pathLength() {
        int countGateway = 0;
        for (Node node : nodes) {
          if (node.isGateway) {
            continue;
          }
          for (Link l : node.links) {
            countGateway += (l.node1.isGateway || l.node2.isGateway) ? 1 : 0;
          }
        }
        return nodes.size()-countGateway;
      }
    }
    static int bestMinDist;
    
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
        bestMinDist = Integer.MAX_VALUE;

        Path path = new Path();
        path.nodes.add(this);
        List<Path> allPaths = findAllPathToGateway(path);
        allPaths.sort(new Comparator<Path>() {
          @Override
          public int compare(Path o1, Path o2) {
            if (o1.pathLength() > o2.pathLength()) return 1;
            if (o1.pathLength() < o2.pathLength()) return -1;
            
            return Integer.compare(o1.nodes.size(), o2.nodes.size());
          }
        });
//        for (Path p : allPaths) {
//          p.debug("one choice ");
//        }
        Path chosenPath = allPaths.get(0);
        chosenPath.debug("chosen Path");
        return chosenPath;
      }

      public List<Path> findAllPathToGateway(Path path) {
        if (path.pathLength() > bestMinDist) {
          return Collections.emptyList();
        }
        List<Path> allPaths = new ArrayList<>();
        for (Link link : this.links) {
          Node potentialNode = link.node1 != this ? link.node1 : link.node2;
          if (path.contains(potentialNode)) {
            continue;
          }
          Path newPath = new Path();
          newPath.nodes.addAll(path.nodes);
          newPath.nodes.add(potentialNode);
          if (potentialNode.isGateway) {
            allPaths.add(newPath);
            bestMinDist = Math.min(bestMinDist, newPath.pathLength());
          } else {
            List<Path> pathsFromHere = potentialNode.findAllPathToGateway(newPath);
            allPaths.addAll(pathsFromHere);
          }
        }
        return allPaths;
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