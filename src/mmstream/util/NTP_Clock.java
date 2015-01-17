package mmstream.util;

import java.util.Date;

public class NTP_Clock extends Object implements Clock {

public NTP_Clock() {
  lastNTP = new NTPTimeStamp();
  reset();
  startOnFirst = false;
}

public synchronized ClockSyncData 
getClockSyncData() {
  ClockSyncData ret =  new ClockSyncData(this.stamp2NTP(startStamp), startMillis);
  //  System.out.println("NTP_Clock.getCSD: millis "+ret.millis+", NTP.secs "+ret.stamp.secs+", NTP.fract "+ret.stamp.fract+", startStamp "+startStamp);
  return ret;
}

public synchronized void 
synchronizeStart(ClockSyncData csd) {
  startStamp = this.NTP2Stamp(csd.stamp);
  startMillis = csd.millis;
  //  System.out.println("NTP_Clock.synchronizeStart: millis "+csd.millis+", NTP.secs "+csd.stamp.secs+", NTP.fract "+csd.stamp.fract+", startStamp "+startStamp);
  startOnFirst = false;
}

public double
getRate() {
  return 1000.0D/millis2stamps_rate;
}


public synchronized void
set(ClockSetData csd) {
  this.set(csd.rate, csd.stamp, csd.ntp);
}

public synchronized void
set(double r, long stamp, NTPTimeStamp ntp) {
  //  ticks2stamps_rate = rateFact/r;
  //  millis2stamps_rate = fact_millis2ticks * ticks2stamps_rate;
  millis2stamps_rate = 1000.0D/r;
  lastStamp = stamp;
  lastNTP = ntp;
  //  System.out.println("NTP_Clock initialised to rate "+millis2stamps_rate+", lastStamp "+lastStamp);
}

public synchronized boolean
adjust(NTPTimeStamp ntpstamp, long stamp) {
  if (stamp <= lastStamp || ntpstamp.millis <= lastNTP.millis)
    return false;


  if (lastStamp != 0L && lastNTP.millis != 0L) {
    double newRate = (ntpstamp.millis - lastNTP.millis) / (stamp - lastStamp); 
    if (millis2stamps_rate > 0L)
      millis2stamps_rate = (millis2stamps_rate + 2 * newRate) / 3;
    else 
      millis2stamps_rate = newRate;
    //    millis2stamps_rate = fact_millis2ticks * ticks2stamps_rate;
  }
  lastStamp = stamp;
  lastNTP = ntpstamp;

  return true;
}

public synchronized boolean
adjust(long secs, long fract, long stamp) {
  return this.adjust(new NTPTimeStamp(secs,fract), stamp);
}

// public synchronized long 
// NTP2Stamp(long NTPticks) {
//   long diff = NTPticks - lastNTPticks;
//   if (diff == 0L)
//     return lastStamp;
//   return (lastStamp + ((long)(((double)diff)/ticks2stamps_rate)));
// }

public synchronized long 
NTP2Stamp(NTPTimeStamp NTPstamp) {
  long diff = NTPstamp.millis - lastNTP.millis;
  long ret;
  if (diff == 0L)
    ret = lastStamp;
  else
    ret = (lastStamp + ((long)(((double)diff)/millis2stamps_rate)));
  //  System.out.println("NTP2Stamp: secs "+Long.toString(NTPstamp.secs)+", fract "+Long.toString(NTPstamp.fract)+", millis "+Long.toString(NTPstamp.millis)+", ret "+Long.toString(ret));
  return ret;
}

public synchronized NTPTimeStamp
stamp2NTP(long stamp) {
  long diff = stamp - lastStamp;

  if (diff == 0L)
    return lastNTP;

  return (NTPTimeStamp.fromMillis(lastNTP.millis + ((long)(millis2stamps_rate*((double)diff))) ));
}
  
public synchronized long
stamps2DeltaLocalMillis(long stamp, long oldStamp) {
  double diff = stamp - oldStamp;

  if (diff <= 0D)
    return -1L;

  return (long)(diff * millis2stamps_rate);
}
  
public synchronized long
stamp2LocalMillis(long stamp) {
  if (startOnFirst == true) {
    startStamp = stamp;
    startMillis = System.currentTimeMillis();
    startOnFirst = false;
//    System.out.println("CLOCK: stame2LocalMillis: startOnFirst at "+startMillis+", "+startStamp);
    return startMillis;
  }

  double diff = stamp - startStamp;
  long ret =  (long)(diff * millis2stamps_rate) + startMillis;
  //  System.out.println("CLOCK: stamp2LocalMillis startStamp"+startStamp+", stamp "+stamp+", startMillis "+startMillis+", rate "+millis2stamps_rate+", ret "+ret);

  return ret;
}
  
public long 
getStamp() {
  if (millis2stamps_rate != 0.0D)
    return this.NTP2Stamp(NTPTimeStamp.fromMillis(System.currentTimeMillis()));
  else 
    return 0L;
}

public synchronized void
reset() {
  lastNTP.millis = 0L;
  lastNTP.secs = 0L;
  lastNTP.fract = 0L;;
  lastStamp = 0L;
  //  ticks2stamps_rate = 0.0;
  millis2stamps_rate = 0.0D;
}

public synchronized boolean
startLocalNow(long startstamp) {
  this.startOnFirst = false;
  this.startMillis = System.currentTimeMillis();
  this.startStamp = startstamp;

  return true;
}

public synchronized boolean
startLocalAt(long startstamp, long starttime) {
  startOnFirst = false;
  this.startMillis = starttime;
  this.startStamp = startstamp;

  return true;
}

public synchronized void
startLocalOnFirst() {
  startOnFirst = true;
}

protected NTPTimeStamp lastNTP;
protected long lastStamp;

  //protected double ticks2stamps_rate;
protected double millis2stamps_rate;

  //protected static final double rateFact = (double)(0xffffffffL+1L);
  //protected static final double fact_millis2ticks = 1000.0 / ((double)(1L + 0xffffffffL));

protected long startMillis;
protected long startStamp;

protected boolean startOnFirst;
}
