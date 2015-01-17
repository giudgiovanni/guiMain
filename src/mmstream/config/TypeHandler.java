package mmstream.config;

import mmstream.config.*;

public class TypeHandler extends Object {

  public TypeHandler(String n, Class cls) {
    name = n;
    clss = cls;
  }

  public String
  getTypeName() {
    return name;
  }

  public Class
  getHandlerClass() {
    return clss;
  }

  protected Class clss;
  protected String name;
}
