package mmstream.config;

import mmstream.config.*;
import mmstream.apps.*;


public interface TypeConfiguration {
  
  public abstract void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception;

}
