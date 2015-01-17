package mmstream.config;

import mmstream.config.*;
import mmstream.apps.*;


public interface ProdConsConfiguration {
  
  public abstract void configure(AppManager am, TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception;

}
