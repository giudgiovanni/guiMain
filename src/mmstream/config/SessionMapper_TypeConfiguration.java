package mmstream.config;

import mmstream.session.*;
import mmstream.config.*;
import mmstream.gui.*;
import mmstream.apps.*;


public class SessionMapper_TypeConfiguration implements TypeConfiguration {
  
  public final static byte TOMS_MAPPER_BC = 1;
  public final static String TOMS_MAPPER_Name = "TOMS Mapper";

  @Override
  public void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception {
    appMan = am;
    guiMan = appMan.getGuiManager();

    synchronized(handler) {
      handler.registerType(new Type(TOMS_MAPPER_Name, TOMS_MAPPER_BC));
      handler.setInitialized(); 
    }

    configureTOMS_MAPPER(handler);
  }
  
  protected void configureTOMS_MAPPER(TypeHandlerTable handler) throws Config_Exception {
    Type t = null;
    t = handler.makeType(TOMS_MAPPER_Name, TOMS_MAPPER_BC);
    Class own = null;
    try {
      own = Class.forName("mmstream.session.TOMS_SessionMapper");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("SessionMapper_TypeHandler.configure "+ce.getMessage());
    }
    handler.registerClassForType(own, TOMS_MAPPER_Name, t);
    try {
      guiMan.registerSessionMapperOptionsGui(TOMS_MAPPER_Name, Class.forName("mmstream.gui.TOMS_SessionMapperOptions"));
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
