package ww.paths;

public class Stack {
  int top;
  int[][] data = new int[100][2];
  int[] v = new int[100];
  public void push(int x, int y) {
      data[top][0] = x;
      data[top++][1] = y;
  }
  public int[] pop() {
      return data[--top];
  }
  public boolean isEmpty() {
      return top == 0;
  }
}