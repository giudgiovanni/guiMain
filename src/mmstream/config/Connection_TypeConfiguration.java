package mmstream.config;

import mmstream.connection.*;
import mmstream.session.*;
import mmstream.config.*;
import mmstream.apps.*;
import mmstream.gui.*;


public class Connection_TypeConfiguration implements TypeConfiguration {
  
  public final static byte UDP_BC = 1;
  public final static String UDP_Name = "UDP";
  public final static byte UDP_MULTICAST_BC = 2;
  public final static String UDP_MULTICAST_Name = "UDP Multicast";

  @Override
  public void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception {
    appMan = am;
    guiMan = appMan.getGuiManager();

    synchronized(handler) {
      handler.registerType(new Type(UDP_Name, UDP_BC));
      handler.registerType(new Type(UDP_MULTICAST_Name, UDP_MULTICAST_BC));
      handler.setInitialized(); 
    }

    configureUDP(handler);
    configureUDP_Multicast(handler);
  }
  
  protected void configureUDP(TypeHandlerTable handler) throws Config_Exception {
    Type t = null;
    t = handler.makeType(UDP_Name, UDP_BC);
    Class own = null;
    try {
      own = Class.forName("mmstream.connection.ip.UDP_Connection");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("Connection_TypeHandler.configure "+ce.getMessage());
    }
    handler.registerClassForType(own, new String(UDP_Name), t);

    try {
      guiMan.registerConnectionOptionsGui(UDP_Name, Class.forName("mmstream.gui.UDP_ConnectionOptions"));
    }
    catch (ClassNotFoundException e) {
      System.err.println("EXCEPTION: ClassNotFoundException :: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    catch (Gui_Exception e) {
      System.err.println("EXCEPTION: Gui_Exception :: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  protected void configureUDP_Multicast(TypeHandlerTable handler) throws Config_Exception {
    Type t = null;
    t = handler.makeType(UDP_MULTICAST_Name, UDP_MULTICAST_BC);
    Class own = null;
    try {
      own = Class.forName("mmstream.connection.ip.UDP_Multicast_Connection");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("Connection_TypeHandler.configure "+ce.getMessage());
    }
    handler.registerClassForType(own, new String(UDP_MULTICAST_Name), t);

    try {
      guiMan.registerConnectionOptionsGui(UDP_MULTICAST_Name, Class.forName("mmstream.gui.UDP_Multicast_ConnectionOptions"));
    }
    catch (ClassNotFoundException e) {
      System.err.println("EXCEPTION: ClassNotFoundException :: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    catch (Gui_Exception e) {
      System.err.println("EXCEPTION: Gui_Exception :: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  AppManager appMan;
  GuiManager guiMan;
}
