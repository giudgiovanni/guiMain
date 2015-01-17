package mmstream.config;

import mmstream.session.*;
import mmstream.config.*;
import mmstream.apps.*;
import mmstream.gui.*;


public class ProtocolCoDec_TypeConfiguration implements TypeConfiguration {
  
public final static byte RTP_BC = 1;
public final static String RTP_Name = "RTP";

@Override
public void configure(AppManager am, TypeHandlerTable handler) throws Config_Exception {
  appMan = am;
  guiMan = appMan.getGuiManager();
  synchronized(handler) {
    handler.registerType(new Type(RTP_Name, RTP_BC));

    handler.setInitialized(); 
  }
    
  configureRTP(handler);
}
  
protected void configureRTP(TypeHandlerTable handler) throws Config_Exception {
  Type t = null;
  t = handler.makeType(RTP_Name, RTP_BC);
  Class own = null;
  try {
    own = Class.forName("mmstream.config.RTP_Profile");
  } catch(ClassNotFoundException ce) {
    throw new Config_Exception("ProtocolCoDec_TypeHandler.configure "+ce.getMessage());
  }
  handler.registerClassForType(own, RTP_Name, t);

  try {
    guiMan.registerProtocolCoDecOptionsGui(RTP_Name, Class.forName("mmstream.gui.RTP_ProtocolCoDecOptions"));
    guiMan.registerReceptionStatisticsDisplayGui(RTP_Name, Class.forName("mmstream.gui.RTP_ReceptionStatistics_Panel"));
    guiMan.registerReceptionStatisticsDataDisplayGui(RTP_Name, Class.forName("mmstream.gui.RTP_ReceptionStatistics_DataPanel"));
    guiMan.registerReceptionStatisticsLabelDisplayGui(RTP_Name, Class.forName("mmstream.gui.RTP_ReceptionStatistics_LabelPanel"));
    guiMan.registerRemoteSenderStatisticsDisplayGui(RTP_Name, Class.forName("mmstream.gui.RTP_Remote_SenderStatistics_Panel"));
    guiMan.registerLocalSenderStatisticsDisplayGui(RTP_Name, Class.forName("mmstream.gui.RTP_Local_SenderStatistics_Panel"));
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
