package cgcollections.arrays;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FastArray<T> implements List<T> {
  public final T elements[];
  public int length;
  
  @SuppressWarnings("unchecked")
  public FastArray(Class<T> c, int maxSize) {
    elements = (T[]) Array.newInstance(c, maxSize);
    length = 0;
  }

  /**
   * warning no bound check
   */
  public T get(int i) {
    return elements[i];
  }
  
  /**
   * warning no bound check
   */
  @Override
  public boolean add(T t) {
    elements[length++] = t;
    return true;
  }

  @Override
  public int indexOf(Object t) {
    for (int i=0;i<length;i++) {
      if (elements[i] == t) {
        return i;
      }
    }
    return -1;
  }
  /**
   * warning : suppose the order is not important
   * @param t
   * @return 
   */
  public boolean remove(Object t) {
    int indexOf = indexOf(t);
    if (indexOf != -1) {
      remove(indexOf);
      return true;
    }
    return false;
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

  @Override
  public T remove(int index) {
    if (index != length-1) {
      elements[index] = elements[length-1];
    }
    length--;
    return elements[length+1];
  }

  public boolean isEmpty() {
    return length==0;
  }

  @Override
  /**
   * Warning super slow methods ! 
   * in critical parts, use elements directly
   */
  public Iterator<T> iterator() {
    return new FastArrayIterator<T>(this);
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public T[] toArray() {
    return elements;
  }

  @SuppressWarnings({ "unchecked", "hiding" })
  @Override
  public <T> T[] toArray(T[] a) {
    return (T[]) elements;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public T set(int index, T element) {
    T old = elements[index];
    elements[index] = element;
    return old;
  }

  @Override
  public void add(int index, T element) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int lastIndexOf(Object o) {
    for (int i=length-1;i>=0;i--) {
      if (elements[i] == o) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public ListIterator<T> listIterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    // TODO Auto-generated method stub
    return null;
  }
}
