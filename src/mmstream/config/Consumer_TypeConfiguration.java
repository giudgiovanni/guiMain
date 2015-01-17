package mmstream.config;

import mmstream.session.*;
import mmstream.consumer.*;
import mmstream.config.*;
import mmstream.apps.*;
import mmstream.gui.*;


public class Consumer_TypeConfiguration implements ProdConsConfiguration {
  
  public final static byte BYTE_BC = 1;
  public final static String BYTE_Name = "Byte";

  public final static byte FILE_BC = 2;
  public final static String FILE_Name = "File";

  public final static byte IMAGE_BC = 3;
  public final static String IMAGE_Name = "Image";

  @Override
  public void configure(AppManager am, TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    appMan = am;
    guiMan = appMan.getGuiManager();
    synchronized(handler) {
      handler.setInitialized();

      configureByte(types, handler);
      configureFile(types, handler);
      configureImage(types, handler);
    }
  }
  
  protected void configureFile(TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    Class own = null;
    try {
      own = Class.forName("mmstream.consumer.File_Consumer");
    } catch(ClassNotFoundException ce) {;}

    Type t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_ANY_Name, Payload_TypeConfiguration.PT_ANY_BC);
    handler.registerClassForType(own, FILE_Name, t);

    try {
      guiMan.registerConsumerOptionsGui(Consumer_TypeConfiguration.FILE_Name, Class.forName("mmstream.gui.File_ConsumerOptions"));
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

  protected void configureByte(TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    Class own = null;
    try {
      own = Class.forName("mmstream.consumer.Byte_Consumer");
    } catch(ClassNotFoundException ce) {;}

    Type t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_ANY_Name, Payload_TypeConfiguration.PT_ANY_BC);
    handler.registerClassForType(own, BYTE_Name, t);

    try {
      guiMan.registerConsumerOptionsGui(Consumer_TypeConfiguration.BYTE_Name, Class.forName("mmstream.gui.Byte_ConsumerOptions"));
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

  protected void configureImage(TypeHandlerTable types, TypeHandlerTable handler) throws Config_Exception {
    Class own = null;
    try {
      own = Class.forName("mmstream.consumer.Image_Consumer");
    } catch(ClassNotFoundException ce) {;}

    Type t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_RGB8_Name, Payload_TypeConfiguration.PT_RGB8_BC);
    handler.registerClassForType(own, IMAGE_Name, t);

    t = null;
    t = types.makeType(Payload_TypeConfiguration.PT_RGB24_Name, Payload_TypeConfiguration.PT_RGB24_BC);
    handler.registerClassForType(own, IMAGE_Name, t);

    try {
      guiMan.registerConsumerOptionsGui(Consumer_TypeConfiguration.IMAGE_Name, Class.forName("mmstream.gui.Image_ConsumerOptions"));
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
