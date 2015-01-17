package mmstream.util;

import java.util.Date;

public interface Master_Clock extends Clock {
  
  public Clock getSlave();
}
