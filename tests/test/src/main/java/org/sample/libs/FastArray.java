package org.sample.libs;

import java.lang.reflect.Array;
import java.util.Iterator;

public class FastArray <T> implements Iterable<T> {
  public T elements[];
  public int length;
  
  
  @SuppressWarnings("unchecked")
  public FastArray(Class<T> c, int maxSize) {
    elements = (T[]) Array.newInstance(c, maxSize);
    length = 0;
  }

  public T get(int i) {
    return elements[i];
  }
  
  public void add(T t) {
    elements[length++] = t;
  }
  
  /**
   * warning : suppose the order is not important
   * @param t
   */
  public void remove(Object t) {
    for (int i=0;i<length;i++) {
      if (elements[i] == t) {
        elements[i] = elements[length-1];
        length--;
        return;
      }
    }
  }
  public void clear() {
    length = 0;
  }
  
  public void copyFrom(FastArray<T> src) {
    System.arraycopy(src.elements, 0, this.elements, 0, src.length);
    this.length = src.length;
  }
  public int size() {
    return length;
  }

  public void removeAt(int index) {
    if (index != length-1) {
      elements[index] = elements[length-1];
    }
    length--;
  }

  public boolean isEmpty() {
    return length==0;
  }

  @Override
  public Iterator<T> iterator() {
    return new FastArrayIterator<T>(this);
  }
  
  public final void iterate(final FastArrayIterate<T> function) {
    for (int i=0;i<length;i++) {
      function.apply(elements[i]);
    }
  }

}
