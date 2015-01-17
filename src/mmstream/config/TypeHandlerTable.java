package mmstream.config;

import mmstream.config.*;

import java.util.*;

public class TypeHandlerTable extends Object {

  public TypeHandlerTable() {
    typeHandlerTable = new Hashtable(8, (float)0.5);
    initialized = false;
  }

  private synchronized void
  checkInit() {
    while(initialized == false) {
      try {
	this.wait();
      }
      catch(InterruptedException ie) {;}
    }
  }

  public synchronized Enumeration
  getTypes() {
    checkInit();
    return typeHandlerTable.keys();
  }

  public synchronized void 
  registerType(Type pt) throws Config_Exception {
    for (Enumeration e = typeHandlerTable.keys(); e.hasMoreElements();) {
      Type ret = (Type)(e.nextElement());
      if (pt.getByteCode() == ret.getByteCode() || pt.getName().equals(ret.getName()))
	throw new Config_Exception("TypeHandlerTable.registerType(): Type "+pt.getName()+" exists");
    }
    
    typeHandlerTable.put(pt, new Vector(2,2));
    //    System.out.println("TYPES: registered Type "+pt.getName()+", "+pt.getByteCode());
//     if (pt.getByteCode() > highest)
//       highest = pt.getByteCode();
  }
			   

  public synchronized Type
  makeType(String name, byte code) throws Config_Exception {
    checkInit();
    for (Enumeration e = typeHandlerTable.keys(); e.hasMoreElements();) {
      Type ret = (Type)(e.nextElement());
      if (name.equals(ret.getName()) && code == ret.getByteCode()) {
	//	System.out.println("TYPES: makeType found "+ret.getName()+", "+ret.getByteCode());
	return ret;
      }
      if (name.equals(ret.getName()) || code == ret.getByteCode()) {
	//	System.out.println("TYPES: makeType found "+ret.getName()+", "+ret.getByteCode());
	throw new Config_Exception("TypeHandlerTable.makeType("+name+","+code+"found Type("+ret.getName()+","+ret.getByteCode()+")");
      }
    }
    Type pt = new Type(name, code);
    typeHandlerTable.put(pt, new Vector(2,2));
    //    System.out.println("TYPES: makeType found nothing, made new Type "+pt.getName()+", "+pt.getByteCode());
    return pt;
  }
			   

  public synchronized Type
  queryType(String name) {
    checkInit();
    Type cur = null;
    for (Enumeration e = typeHandlerTable.keys(); e.hasMoreElements();) {
      cur = (Type)(e.nextElement());
      if (name.equals(cur.getName())) {
	return (Type)cur.clone();
      }
    }
    return null;
  }

  public synchronized Type 
  queryType(byte byteCode) {
    checkInit();
    Type cur = null;
    for (Enumeration e = typeHandlerTable.keys(); e.hasMoreElements();) {
      cur = (Type)(e.nextElement());
      if (byteCode == cur.getByteCode()) {
	return (Type)cur.clone();
      }
    }
    return null;
  }
  
  public synchronized void 
  registerClassForType(Class cls, String Name, Type pt) throws Config_Exception {
    checkInit();
    TypeHandler nt = new TypeHandler(Name, cls);
    Vector classes = this.queryClasses(pt);
    if (classes != null) {
      if (classes.contains(nt))
	throw new Config_Exception("TypeHandlerTable.registerClassForType(): Class "+cls.getName()+" exists");
      //      System.out.println("TYPES: registerClass found "+pt.getName()+", "+pt.getByteCode());
    }
    else {
      classes = new Vector(2,2);
      typeHandlerTable.put(pt, classes);
      //      System.out.println("TYPES: registerClass found nothing for "+pt.getName()+", "+pt.getByteCode());
    }
    //    System.out.println("TYPES: registerClass adding "+Name+", "+cls.getName()+" for "+pt.getName()+", "+pt.getByteCode());
    classes.addElement(nt);
  }

  public synchronized Vector
  queryClasses(Type pt) {
    checkInit();
    for (Enumeration e = typeHandlerTable.keys(); e.hasMoreElements();) {
      Type ret = (Type)(e.nextElement());
      if (pt.getByteCode() == ret.getByteCode() || pt.getName().equals(ret.getName()))
	return (Vector)typeHandlerTable.get(ret);
    }
    return null;
  }


  public synchronized void setInitialized() { 
    initialized = true; 
    this.notifyAll();
  }

//   protected byte highest = -127;
  protected boolean initialized;

  protected Hashtable typeHandlerTable;
}


