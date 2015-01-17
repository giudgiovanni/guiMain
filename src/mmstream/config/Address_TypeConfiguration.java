package mmstream.config;

import mmstream.address.*;
import mmstream.session.*;
import mmstream.config.*;
import mmstream.apps.*;


public class Address_TypeConfiguration implements TypeConfiguration {
  
  public final static byte IP_BC = 1;
  public final static String IP_Name = "IP";

  @Override
  public void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception {
    synchronized(handler) {
      handler.registerType(new Type(IP_Name, IP_BC));

      handler.setInitialized(); 
    }

    configureIP(handler);
  }
  
  protected void configureIP(TypeHandlerTable handler) throws Config_Exception {
    Type t = null;
    t = handler.makeType(IP_Name, IP_BC);
    Class own = null;
    try {
      own = Class.forName("mmstream.address.IP_Address");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("Address_TypeHandler.configure "+ce.getMessage());
    }
    handler.registerClassForType(own, IP_Name, t);
  }

}
