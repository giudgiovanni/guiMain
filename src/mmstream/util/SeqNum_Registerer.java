package mmstream.util;

public class SeqNum_Registerer extends Object {
public SeqNum_Registerer() {
  highest_num = 0;
  last_num = -1;
}

public SeqNum_Registerer(int start) {
  highest_num = start;
  last_num =  - 1;
}

public synchronized void reset() {
  reset(0);

}


public synchronized void reset(int start) {
  last_num = highest_num;
  highest_num = start;
}


public synchronized int query_reset() {
  return query_reset(0);
}


public synchronized int query_reset(int start) {
  last_num = highest_num;
  highest_num = start;

  return last_num;
}


public synchronized void register(int num) {
  last_num = num;
  if (num > highest_num) {
    highest_num = num;
  }
}


public synchronized void add(int num) {
  last_num = highest_num;
  highest_num += num;
}


public synchronized int last() {
  return last_num;
}


public synchronized int highest() {
  return highest_num;
}


protected int highest_num;
protected int last_num;
}
