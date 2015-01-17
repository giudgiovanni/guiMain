package mmstream.util;

public class ClockSetData extends Object {

public ClockSetData() {
  rate = 0.0;
  ntp = new NTPTimeStamp();
  stamp = 0;
}

public ClockSetData(double r, NTPTimeStamp n, long s) {
  rate = r;
  ntp = n;
  stamp = s;
}
  
  public double rate;
  public NTPTimeStamp ntp;
  public long stamp;
}
