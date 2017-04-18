package cotc.utils;

public class FastArray <T> {
  public Object elements[];
  public int FE;
  
  public FastArray(int maxSize) {
    elements = new Object[maxSize];
    FE = 0;
  }

  @SuppressWarnings("unchecked")
  public T get(int i) {
    return (T) elements[i];
  }
  
  public void add(T t) {
    elements[FE++] = t;
  }
  
  /**
   * warning : suppose the order is not important
   * @param t
   */
  public void remove(T t) {
    for (int i=0;i<FE;i++) {
      if (elements[i] == t) {
        elements[i] = elements[FE--];
        return;
      }
    }
  }
  public void clear() {
    FE = 0;
  }
  
  public void copyFrom(FastArray<T> src) {
    System.arraycopy(src.elements, 0, this.elements, 0, src.FE);
    this.FE = src.FE;
  }
  public int size() {
    return FE;
  }

  public void removeAt(int index) {
    if (index != FE-1) {
      elements[index] = elements[FE-1];
    }
    FE--;
  }
}
