package ww.paths;

import java.io.BufferedReader;
import java.io.FileReader;

public class Biconnected {
  private int n; // number of vertex in this graph
  private Node[] graph; // adjacent list for all vertex
  private int num; // current dfn
  private int[] dfn; // depth first number for all vertex
  private int[] low; // earliest ancestor through back edge
  private Stack stack;
  public Biconnected(int vertex) {
      n = vertex;
      graph = new Node[n];
  }

  public void init() {
      dfn = new int[n];
      low = new int[n];
      for (int i = 0; i < n; i++) {
          dfn[i] = low[i] = -1;
      }
      num = 0;
      stack = new Stack();
  }
  public void bicon() {
      init();
      bicon(0, -1);
  }
  private void bicon(int check, int parent) {
      int[] tt;
      dfn[check] = low[check] = num++;
      for (Node adj = graph[check]; adj != null; adj = adj.link) {
          int w = adj.vertex;
          if (parent != w && dfn[w] < dfn[check]) { // back edge or child
              stack.push(check, w);
          }
          if (dfn[w] < 0) { // w is a child of check
              bicon(w, check);
              // check
              low[check] = (low[check] < low[w]) ? low[check] : low[w];
              if (parent >=0 && low[w] >= dfn[check]) { // �p�Gcheck�_��,w�N�S�����^��W�h�F
                  do {
                      tt = stack.pop();
                      System.out.print(" <"+tt[0]+","+tt[1]+">");
                  } while (!(tt[0] == check && tt[1] == w));
                  System.out.println();
              }
          } else if (w != parent) { // w is a grand parent of check(back edge)
              low[check] = (low[check] < dfn[w]) ? low[check] : dfn[w];
          }
      }
      if (parent < 0) {
          while (!stack.isEmpty()) {
              tt = stack.pop();
              System.out.print(" <"+tt[0]+","+tt[1]+">");
          }
          System.out.println();
      }
  }
  // add nodes to adjacent list
  public void add(int x, int y, int cost) {
      Node tt = new Node();
      tt.vertex = y;
      tt.link = graph[x];
      graph[x] = tt;
      tt = new Node();
      tt.vertex = x;
      tt.link = graph[y];
      graph[y] = tt;
  }
  
  public static void main(String[] argv) throws Exception {
      BufferedReader in = new BufferedReader(new FileReader(argv[0]));
      Biconnected g = new Biconnected(Integer.parseInt(in.readLine()));
      String s;
      while ((s = in.readLine()) != null) {
          int sep1 = s.indexOf(" ");
          int sep2 = s.indexOf(" ", sep1 + 1);
          g.add(Integer.parseInt(s.substring(0, sep1)), Integer.parseInt(s.substring(sep1+1,sep2)), Integer.parseInt(s.substring(sep2+1,s.length())));
      }
      g.bicon();
  }
}