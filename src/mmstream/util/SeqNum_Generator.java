package mmstream.util;

public class SeqNum_Generator extends Object {
public SeqNum_Generator (int mod) {
  modulo = mod;
  value = 0;
  last_val = 0;
}

public SeqNum_Generator (int mod, int start) {
  modulo = mod;
  if (start > 0) {
    value = start;
    last_val = start - 1;
  }
  else {
    value = 0;
    last_val = 0;
  }
}

public synchronized void reset() {
  last_val = value;
  value = 0;
}

public synchronized void reset(int start) {
  last_val = value;
  value = start;
}

public synchronized int last() {
  return last_val;
}

public synchronized int next() {
  last_val = value++;
  if (value == modulo)
    value = 0;
  
  return value;
}

private int modulo;
private int value;
private int last_val;
}
