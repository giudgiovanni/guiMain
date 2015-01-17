package mmstream.util;

public class TimedObject {
  
public TimedObject(Object d, long to) {
  this.object = d;
  this.timeOut = to;
}

public void 
sync() {
  long tmp = timeOut - System.currentTimeMillis();
  //  System.out.println("Sleeping for "+tmp);
  if (tmp > 0L) {
    try {
      Thread.sleep(tmp);
    }
    catch (InterruptedException ie) {
      ;
    }
  }
}
 
public long
timeToSleep() {
  return(timeOut - System.currentTimeMillis());
}

public Object object;
public long timeOut;
}
