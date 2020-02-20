package lcm.ai.beam;

import java.util.Comparator;
import java.util.TreeSet;

public class FixedSizeSortedSet<E> extends TreeSet<E>
{
  private static final long serialVersionUID = 1L;
  
  private final Comparator<? super E> _comparator;
  private final int _maxSize;
  
  public FixedSizeSortedSet(int maxSize)
  {
    this(null, maxSize);
  }
  
  public FixedSizeSortedSet(Comparator<? super E> comparator, int maxSize)
  {
    super(comparator);
    _comparator = comparator;
    _maxSize = maxSize;
  }

  public E addWithEviction(E e) {
    if(size() >= _maxSize) {
      E smallest = last();
      int comparison;
      if(_comparator == null) comparison = ((Comparable<E>)e).compareTo(smallest);
      else comparison = _comparator.compare(e, smallest);
      if(comparison > 0) {
        remove(smallest);
        super.add(e);
        return smallest;
      }
      return e;
    }
    else {
      super.add(e);
      return null;
    }
  }

  @Override 
  public boolean add(E e)
  {
    if(size() >= _maxSize)
    {
      E smallest = last();
      int comparison;
      if(_comparator == null) comparison = ((Comparable<E>)e).compareTo(smallest);
      else comparison = _comparator.compare(e, smallest);
      if(comparison > 0)
      {
        remove(smallest);
        return super.add(e);
      }
      return false;
    }
    else
    {
      return super.add(e);
    }
  }

}