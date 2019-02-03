package bttc;

import java.util.ArrayList;
import java.util.List;

public class StateFactory {

  List<StateCache> cache = new ArrayList<>();
  int current;

  StateFactory() {
    current = 0;
    cache.add(new StateCache());
  }

  State getNewGrid() {
    if (cache.get(current).isFull()) {
      current++;
      if (current >= cache.size()) {
        cache.add(new StateCache());
      } else {
        cache.get(current).current = 0;
      }
    }
    return cache.get(current).pop();
  }

  void reset() {
    current = 0;
    cache.get(0).current = 0;
  }

  int capacity() {
    return cache.size() * StateCache.STATE_CACHE_SIZE;
  }

  int size() {
    return cache.get(current).current + current * StateCache.STATE_CACHE_SIZE;
  }
}