package mmstream.util;

public class ClockSyncData extends Object {

public ClockSyncData() {
  millis = 0;
  stamp = null;
}

public ClockSyncData(NTPTimeStamp t, long m) {
  millis = m;
  stamp = t;
}
  
  public long millis;
  public NTPTimeStamp stamp;
}
