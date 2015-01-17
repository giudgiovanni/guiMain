package mmstream.config;

import mmstream.session.*;
import mmstream.config.*;
import mmstream.apps.*;
import mmstream.gui.*;


public class Payload_TypeConfiguration implements TypeConfiguration {
  
  public final static byte PT_ANY_BC = 1;
  public final static String PT_ANY_Name = "ANY";

  public final static byte PT_BYTE_TEST_BC = 2;
  public final static String PT_BYTE_TEST_Name = "BYTE TEST";

  public final static byte PT_RGB8_BC = 3;
  public final static String PT_RGB8_Name = "RGB 8 bit";

  public final static byte PT_RGB24_BC = 4;
  public final static String PT_RGB24_Name = "RGB 24 bit";

  @Override
  public void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception {
    appMan = am;
    guiMan = appMan.getGuiManager();

    synchronized(handler) {
      handler.registerType(new PayloadType(PT_ANY_Name, PT_ANY_BC));
      handler.registerType(new PayloadType(PT_BYTE_TEST_Name, PT_BYTE_TEST_BC));
      handler.registerType(new PayloadType(PT_RGB8_Name, PT_RGB8_BC));
      handler.registerType(new PayloadType(PT_RGB24_Name, PT_RGB24_BC));

      handler.setInitialized();
    }

    try {
      guiMan.registerPayloadTypeOptionsGui(PT_BYTE_TEST_Name, Class.forName("mmstream.gui.ByteTest_PayloadTypeOptions"));
      guiMan.registerPayloadTypeOptionsGui(PT_RGB8_Name, Class.forName("mmstream.gui.RGB8_PayloadTypeOptions"));
      guiMan.registerPayloadTypeOptionsGui(PT_RGB24_Name, Class.forName("mmstream.gui.RGB24_PayloadTypeOptions"));
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
