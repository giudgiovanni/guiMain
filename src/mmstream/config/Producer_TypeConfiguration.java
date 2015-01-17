package mmstream.config;

import mmstream.session.*;
import mmstream.producer.*;
import mmstream.config.*;
import mmstream.apps.*;
import mmstream.gui.*;


public class Producer_TypeConfiguration implements ProdConsConfiguration {
  
  public final static byte BYTE_BC = 1;
  public final static String BYTE_Name = "Byte";

  public final static byte FILE_BC = 2;
  public final static String FILE_Name = "File";

  @Override
  public void configure(AppManager am, TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    appMan = am;
    guiMan = appMan.getGuiManager();
    synchronized(handler) {
      handler.setInitialized();

      configureByte(types,handler);
      configureFile(types,handler);
    }
  }
  
  protected void configureByte(TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    Class own = null;
    try {
      own = Class.forName("mmstream.producer.Byte_Producer");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("Producer_TypeHandler.configureByte() "+ce.getMessage());
    }
    
    Type t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_RGB8_Name, Payload_TypeConfiguration.PT_RGB8_BC);
    handler.registerClassForType(own, BYTE_Name, t);

    t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_RGB24_Name, Payload_TypeConfiguration.PT_RGB24_BC);
    handler.registerClassForType(own, BYTE_Name, t);

    t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_BYTE_TEST_Name, Payload_TypeConfiguration.PT_BYTE_TEST_BC);
    handler.registerClassForType(own, BYTE_Name, t);

    try {
      guiMan.registerProducerOptionsGui(Producer_TypeConfiguration.BYTE_Name, Class.forName("mmstream.gui.Byte_ProducerOptions"));
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

  protected void configureFile(TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    Class own = null;
    try {
      own = Class.forName("mmstream.producer.File_Producer");
    } catch(ClassNotFoundException ce) {
      throw new Config_Exception("Producer_TypeHandler.configureFile() "+ce.getMessage());
    }
    
    Type t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_ANY_Name, Payload_TypeConfiguration.PT_ANY_BC);
    handler.registerClassForType(own, FILE_Name, t);
    
    try {
      guiMan.registerProducerOptionsGui(Producer_TypeConfiguration.FILE_Name, Class.forName("mmstream.gui.File_ProducerOptions"));
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
