package mmstream.main;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.config.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;

import java.net.*;
import java.io.*;

public class guiMain extends Object {

public static void main(String args[]) {
  init();
}

private static void init() {

  appMan = new TOMS_AppManager();
  guiMan = new TOMS_GuiManager(appMan);
  appMan.setGuiManager(guiMan);

  try {
    Address_TypeConfiguration addrTC = new Address_TypeConfiguration();
    addrTC.configure(appMan, appMan.getAddressTypeHandlerTable());

    Connection_TypeConfiguration conTC = new Connection_TypeConfiguration();
    conTC.configure(appMan, appMan.getConnectionTypeHandlerTable());

    ProtocolCoDec_TypeConfiguration protTC = new ProtocolCoDec_TypeConfiguration();
    protTC.configure(appMan, appMan.getProtocolTypeHandlerTable());

    Payload_TypeConfiguration payTC = new Payload_TypeConfiguration();
    payTC.configure(appMan, appMan.getPayloadTypeHandlerTable());

    Producer_TypeConfiguration prodTC = new Producer_TypeConfiguration();
    prodTC.configure(appMan, appMan.getPayloadTypeHandlerTable(), appMan.getProducerTypeHandlerTable());

    Consumer_TypeConfiguration consTC = new Consumer_TypeConfiguration();
    consTC.configure(appMan, appMan.getPayloadTypeHandlerTable(), appMan.getConsumerTypeHandlerTable());

    SessionMapper_TypeConfiguration smTC = new SessionMapper_TypeConfiguration();
    smTC.configure(appMan, appMan.getSessionMapperTypeHandlerTable());

  }
  catch (Config_Exception e) {
    System.err.println("EXCEPTION: XXX_TypeConfiguration.configure() :: "+e.getMessage());
    e.printStackTrace();
    System.exit(1);
  }

  streamMan = new TOMS_StreamManager(appMan);
  appMan.setStreamManager(streamMan);
  
  sessionMan = new TOMS_SessionManager(appMan);
  appMan.setSessionManager(sessionMan);
  
  sessionAgent = new TOMS_SessionAgent(appMan);
  appMan.setSessionAgent(sessionAgent);
  sessionMan.setSessionAgent(sessionAgent);

  mainFrame = (MainFrame)guiMan.getRoot();
  streamMan.setOutput(mainFrame);
  sessionMan.setOutput(mainFrame);
  appMan.setOutput(mainFrame);

  mainFrame.show();
  
}

private static MainFrame mainFrame;
public  static AppManager appMan;
private static GuiManager guiMan;
private static StreamManager streamMan;
private static SessionManager sessionMan;
private static SessionAgent sessionAgent;
}
