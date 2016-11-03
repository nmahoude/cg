package hypersonic.utils;

import java.util.ArrayDeque;
import java.util.Deque;

public class Cache<T> {
  Deque<T> available = new ArrayDeque<>();
  
  public void retrocede(T t) {
    available.push(t);
  }

  public void push(T t) {
    available.push(t);
  }

  public final boolean isEmpty() {
    return available.isEmpty();
  }

  public T pop() {
    return available.pop();
  }
}
