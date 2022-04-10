package fast.array;

public class FastByteArray {
  public final byte elements[];
  public int length;
  
  public FastByteArray(int maxSize) {
    elements = new byte[maxSize];
    length = 0;
  }

  /**
   * warning no bound check
   */
  public byte get(int i) {
    return elements[i];
  }
  
  /**
   * warning no bound check
   */
  public boolean add(byte t) {
    elements[length++] = t;
    return true;
  }

  public int indexOf(byte t) {
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
  public boolean remove(byte t) {
    int indexOf = indexOf(t);
    if (indexOf != -1) {
      removeAtIndex(indexOf);
      return true;
    }
    return false;
  }
  
  public void clear() {
    length = 0;
  }
  
  public void copyFrom(FastByteArray src) {
    System.arraycopy(src.elements, 0, this.elements, 0, src.length);
    this.length = src.length;
  }
  public int size() {
    return length;
  }

  public int removeAtIndex(int index) {
    if (index != length-1) {
      elements[index] = elements[length-1];
    }
    length--;
    return elements[length+1];
  }

  public boolean isEmpty() {
    return length==0;
  }

  public boolean contains(byte o) {
    return indexOf(o) != -1;
  }

  public byte[] toArray() {
    return elements;
  }

  public int set(int index, byte element) {
    int old = elements[index];
    elements[index] = element;
    return old;
  }

  public int lastIndexOf(byte o) {
    for (int i=length-1;i>=0;i--) {
      if (elements[i] == o) {
        return i;
      }
    }
    return -1;
  }
}
