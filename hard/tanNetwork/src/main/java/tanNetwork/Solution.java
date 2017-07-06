package tanNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Solution {
  static Station stations[];
  
  public static class Station implements Comparable<Station> {
    int index;
    String id;
    String name;
    double longitude;
    double latitude;
    
    double gScore = Double.POSITIVE_INFINITY;
    double fScore = Double.POSITIVE_INFINITY;

    List<Station> to = new ArrayList<>();
    
    public Station(int i) {
      index = i;
    }

    public void read(Scanner in) {
      String stopName = in.nextLine();
      String infos[] = stopName.split(",");
      id = infos[0].substring(9).trim();
//      System.err.println("read station  :"+id);
      name = infos[1].substring(1, infos[1].length()-1);
      longitude = Math.PI * Double.parseDouble(infos[3]) / 180.0; 
      latitude = Math.PI * Double.parseDouble(infos[4]) / 180.0;
    }

    public static double heuristicLength(Station from, Station to) {
      double x = (to.longitude-from.longitude) * Math.cos (0.5*(from.latitude + to.latitude));
      double y = to.latitude - from.latitude;
      return Math.sqrt(x*x+y*y) * 6371;
    }

    @Override
    public int compareTo(Station o) {
      return Double.compare(fScore, o.fScore);
    }
  }
  
  public static void main(String args[]) {
      Scanner in = new Scanner(System.in);
      String startPoint = in.next().trim().substring(9);
      String endPoint = in.next().trim().substring(9);
//      System.err.println("Path to find : "+startPoint+" to "+endPoint);
      int N = in.nextInt();
      if (in.hasNextLine()) {
          in.nextLine();
      }
      stations = new Station[N];
      for (int i = 0; i < N; i++) {
        Station station = new Station(i);
        station.read(in);
        stations[i] = station;
      }
      
      int M = in.nextInt();
      if (in.hasNextLine()) {
          in.nextLine();
      }
      for (int i = 0; i < M; i++) {
          String route[] = in.nextLine().split(" ");
          Station from = findStation(route[0].trim().substring(9));
          Station to = findStation(route[1].trim().substring(9));
//          System.err.println("Reading stations : "+route[0].substring(9)+" -> "+route[1].substring(9));
          from.to.add(to);
      }

      Station from = findStation(startPoint);
      Station to = findStation(endPoint);
      
      List<Station> path = astar(from, to);
      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      if (path.isEmpty()) {
        System.out.println("IMPOSSIBLE");
      } else {
        for (Station station : path) {
          System.out.println(station.name);
        }
      }
  }

  private static List<Station> astar(Station from, Station to) {
    int cameFrom[] = new int[stations.length];
    for (int i=0;i<stations.length;i++) {
      cameFrom[i] = -1;
    }
      
    List<Station> closedSet = new ArrayList<>();
    PriorityQueue<Station> openSet = new PriorityQueue<>();
    openSet.add(from);
    
    from.gScore = 0.0;
    from.fScore = Station.heuristicLength(to , from);
    
    while (!openSet.isEmpty()) {
      Station current = openSet.remove();
      if (current == to) {
        return constructPath(cameFrom, current);
      }
      openSet.remove(current);
      closedSet.add(current);
      
      for (Station neighbor : current.to) {
        if (closedSet.contains(neighbor)) {
          continue;
        }
        if (!openSet.contains(neighbor)) {
          openSet.add(neighbor);
        }
        double gScore = current.gScore + Station.heuristicLength(current, neighbor);
        if (gScore > neighbor.gScore) continue;
        
        cameFrom[neighbor.index] = current.index;
        neighbor.gScore = gScore;
        neighbor.fScore = gScore + Station.heuristicLength(neighbor, to);
      }
    }
    return new ArrayList<>();
  }




  private static List<Station> constructPath(int[] cameFrom, Station current) {
    List<Station> path = new ArrayList<>();
    path.add(path.size(), current);
    while (cameFrom[current.index] != -1) {
      current = stations[cameFrom[current.index]];
      path.add(0, current);
    }
    return path;
  }




  private static Station findStation(String string) {
    for (Station station : stations) {
      if (string.equals(station.id)) {
        return station;
      }
    }
//    System.err.println("Can't find station : "+string);
    return null;
  }
}