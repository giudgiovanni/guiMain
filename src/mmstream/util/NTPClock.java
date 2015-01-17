package mmstream.util;

import java.util.Date;

public class NTPClock extends Object implements Clock {
public NTPClock() {
  reset();
  startOnFirst = false;
}

public synchronized double
getRate() {
  return rateFact/rate;
}


public synchronized void
set(ClockSyncData csd) {
  this.set(csd.rate, csd.stamp, csd.ticks);
}

public synchronized void
set(double r, long ls, long t) {
  rate = rateFact/r;
  lastStamp = ls;
  lastNTPticks = t;
}

public synchronized boolean
synchronize(long NTPticks, long stamp) {
  if (stamp <= lastStamp || NTPticks <= lastNTPticks)
    return false;


  if (lastStamp != 0L && lastNTPticks != 0L) {
    double newRate = (NTPticks - lastNTPticks) / (stamp - lastStamp); 
    if (rate > 0L)
      rate = (rate + 2 * newRate) / 3;
    else 
      rate = newRate;
  }
  lastStamp = stamp;
  lastNTPticks = NTPticks;

  return true;
}

public synchronized boolean
synchronize(long secs, long fract, long stamp) {
  return this.synchronize(fract + secs*0xffffffffL, stamp);
}

public synchronized long 
NTP2Stamp(long NTPticks) {
  long diff = NTPticks - lastNTPticks;
  if (diff == 0L)
    return lastStamp;
  return (lastStamp + ((long)(((double)diff)/rate)));
}

public synchronized long 
NTP2Stamp(NTPTimeStamp NTPstamp) {
  long diff = NTPstamp.ticks - lastNTPticks;
  if (diff == 0L)
    return lastStamp;
  return (lastStamp + ((long)(((double)diff)/rate)));
}

public synchronized long
stamp2NTP(long stamp) {
  long diff = stamp - lastStamp;

  if (diff == 0L)
    return lastNTPticks;

  // BUG?
  //  return (lastNTPticks + ((long)(rate/((double)diff))));
  return (lastNTPticks + ((long)(rate*((double)diff))));
}
  
public synchronized long
stamps2DeltaMillis(long stamp, long oldStamp) {
  double diff = stamp - oldStamp;

  if (diff <= 0)
    return -1L;

//   diff *= rate;
//   return (NTPTimeStamp.ticks2Millis(diff));
  return (long)(diff * rate * fact_ticks2millis);
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
//   diff *= rate;
//   long ret =  NTPTimeStamp.ticks2Millis(diff) + startMillis;
//   //  System.out.println("CLOCK: stame2LocalMillis: convert "+stamp+" to "+ret+", rate is "+(rate*1000)/0xffffffffL);
//   return ret;
  return (long)(diff * rate * fact_ticks2millis) + startMillis;
}
  
public synchronized long 
getStamp() {
  if (rate != 0.0)
    return this.NTP2Stamp(NTPTimeStamp.fromMillis(System.currentTimeMillis()));
  else
    return 0L;
}

public synchronized void
reset() {
  lastNTPticks = 0L;
  lastStamp = 0L;
  rate = 0.0;
}

public synchronized boolean
startLocalNow(long startstamp) {
  startOnFirst = false;
  startMillis = System.currentTimeMillis();
  this.startStamp = startstamp;
  //  System.out.println("CLOCK: startLocal: startLocal succeeded at "+startMillis);
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

protected long lastNTPticks;
protected long lastStamp;
protected double rate;
  // BUG?
  //protected double rateFact = 3600.0 * ((double)0xffffffffL);
protected static final double rateFact = (double)0xffffffffL;
protected static final double fact_ticks2millis = 1000.0 / ((double)0xffffffffL);

protected long startMillis;
protected long startStamp;
protected boolean startOnFirst;
}
