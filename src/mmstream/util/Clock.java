package mmstream.util;

import java.util.Date;

public interface Clock {
  
  public double getRate();

  public void set(double rate, long stamp, NTPTimeStamp ntp);

  public void set(ClockSetData csd);

  public ClockSyncData getClockSyncData();
  public void synchronizeStart(ClockSyncData csd);
  
  public boolean adjust(long secs, long fract, long stamp);

  public boolean adjust(NTPTimeStamp ntps, long stamp);

  public long stamps2DeltaLocalMillis(long stamp, long oldStamp);
  
  public long stamp2LocalMillis(long stamp);
  
  public long getStamp();

  public void reset();

  public boolean startLocalNow(long startStamp);
  public boolean startLocalAt(long startStamp, long startMillis);
  public void startLocalOnFirst();
}
