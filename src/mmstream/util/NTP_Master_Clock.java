package mmstream.util;

import java.util.*;

public class NTP_Master_Clock extends NTP_Clock implements Master_Clock{
  public NTP_Master_Clock() {
    super();

    slaves = new Vector(5,5);
  }

  public synchronized boolean
  adjust(NTPTimeStamp ntp, long stamp) {
    boolean ret = super.adjust(ntp, stamp);

    if (ret) {
      for (Enumeration e = slaves.elements(); e.hasMoreElements();) {
	//       ((Clock)(e.nextElement())).synchronize(NTPticks, stamp);
	((Clock)(e.nextElement())).set(this.getRate(), stamp, ntp);
      }
    }
    return ret;
  }

  public synchronized Clock
  getSlave() {
    NTP_Clock ret = new NTP_Clock();

    slaves.addElement(ret);
    
    ret.lastStamp = this.lastStamp;    
    ret.lastNTP = this.lastNTP;
    //    ret.ticks2stamps_rate = this.ticks2stamps_rate;    
    ret.millis2stamps_rate = this.millis2stamps_rate;    

    return ret;
  }

  protected Vector slaves;
}
