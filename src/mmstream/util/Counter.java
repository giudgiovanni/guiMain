package mmstream.util;

public class Counter extends Object {
public Counter() {
  count = 0;
}

public Counter(long start) {
  count = start;
}


public synchronized void reset(long start) {
  count = start;
}

public synchronized void reset() {
  count = 0;
}


public synchronized void add(long val) {
  count += val;
}

public synchronized void incr() {
  count++;
}



public synchronized long query() {
  return count;
}

public synchronized long query_reset() {
  return query_reset(0); 
}

public synchronized long query_reset(int start) {
  long tmp = count;
  count = start;
  return tmp;
}

private long count;
}
