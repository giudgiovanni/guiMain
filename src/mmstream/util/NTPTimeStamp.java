package mmstream.util;

import java.util.Date;

public class NTPTimeStamp extends Object {

protected static final long timeshift = (70L * 365L + 17L)*24L*3600L;
protected static final long fracts = (0xffffffffL+1L);
protected static final double fract_fact = ((double)fracts) / 1000.0;

public NTPTimeStamp() {
  this.secs = 0L;
  this.fract = 0L;
  //  this.ticks = 0;
  this.millis = 0L;
}

public NTPTimeStamp(long secs, long fract) {
  this.secs = secs;
  this.fract = fract;
  this.millis = (this.secs - timeshift)*1000L + (long)(((double)this.fract) / NTPTimeStamp.fract_fact);
}

public static NTPTimeStamp 
fromMillis(long millis) {
  NTPTimeStamp ret = new NTPTimeStamp();
  ret.secs = millis / 1000L + timeshift;
  ret.fract = (long)(((double)(millis - ((ret.secs-timeshift) * 1000L)))*NTPTimeStamp.fract_fact);
  ret.millis = millis;
  
  return ret;
}  


    
public long fract;
public long secs;
public long millis;

}
