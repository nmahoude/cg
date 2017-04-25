package cgcollections.cache;

import java.lang.reflect.Array;

public class FastCache <T> {
  final T elements[];
  final int size;
  int currentFreeIndex;
  
  @SuppressWarnings("unchecked")
  /**
   * T need an empty constructor
   * @param c
   * @param size
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public FastCache(Class<T> c,int size)  {
    this.size = size;
    this.elements = (T[]) Array.newInstance(c, size);
    try {
      for (int i=0;i<size;i++) {
        elements[i] = c.newInstance();
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("T needs an empty constructor");
    }
    currentFreeIndex = size;
  }
  
  public T get() {
    return elements[--currentFreeIndex];
  }
  
  public void freeAll() {
    currentFreeIndex=size;
  }
}
