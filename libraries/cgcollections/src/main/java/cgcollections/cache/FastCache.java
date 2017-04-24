package cgcollections.cache;

import java.lang.reflect.Array;

public class FastCache <T> {
  final T elements[];
  final int size;
  int currentFreeIndex;
  
  @SuppressWarnings("unchecked")
  public FastCache(Class<T> c,int size) {
    this.size = size;
    this.elements = (T[]) Array.newInstance(c, size);
    currentFreeIndex = size;
  }
  
  public T get() {
    return elements[--currentFreeIndex];
  }
  
  public void freeAll() {
    currentFreeIndex=size;
  }
}
