package fast.cache;

import java.lang.reflect.Array;

/**
 * Implementation of an object cache
 * that is not managed
 */
public class FastCache <T> {
  final T elements[];
  final int size;
  int currentFreeIndex;
  
  @SuppressWarnings("unchecked")
  /**
   * T need an empty constructor
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

  /**
   * potentially faster way to instantiate the fastCache by passing the elements
   * already filled
   * 
   * @param providedElements : the array already filled with objects
   */
  public FastCache(T providedElements[])  {
    this.size = providedElements.length;
    this.currentFreeIndex = size;
    this.elements = providedElements;
  }
  
  public T pop() {
    return elements[--currentFreeIndex];
  }

  public int reserve(int size) {
    currentFreeIndex-=size;
    return currentFreeIndex;
  }
  
  /**
   * In conjonction with reserve, 
   * allow to "leave" the elements in the cache and use them
   * 
   * @warning client should not temper with the elements in the array
   */
  public T[] getReference() {
    return elements;
  }
  
  public void reset() {
    currentFreeIndex=size;
  }
}
