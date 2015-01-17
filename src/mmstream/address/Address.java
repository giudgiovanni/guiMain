package mmstream.address;

import mmstream.util.*;

public interface Address extends Cloneable {
  public Object clone();
  public String getName();
  public void configure(String a) throws Address_Exception;
}
