import java.util.*;

public class SolutionOld {
  static Scanner y = new Scanner(System.in);

  static int i() {
    return y.nextInt();
  }

  public static void main(String a[]) {
    int d, c, j, l, n = i(), p[] = new int[n];
    i();
    i();
    i();
    p[n - 1] = i();
    i();
    i();
    l = i();
    while (l-- > 0)
      p[i()] = i();
    for (;;) {
      c = i();
      j = i();
      d = y.next().charAt(0) == 'R' ? 1 : -1;
      System.out.println(c != -1 && d * j > d * p[c] ? "BLOCK" : "WAIT");
    }
  }
}