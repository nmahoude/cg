package bttc;

public class StateCache {
  public static final int STATE_CACHE_SIZE = 10000;

  State data[] = new State[STATE_CACHE_SIZE];
  int current;

  StateCache() {
    current = 0;
    for (int i = 0; i < STATE_CACHE_SIZE; i++) {
      data[i] = new State();
    }
  }

  boolean isFull() {
    return current == STATE_CACHE_SIZE;
  }

  State pop() {
    return data[current++];
  }
}