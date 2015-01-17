package mmstream.apps;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.apps.*;
import mmstream.gui.*;
import mmstream.config.*;
import mmstream.producer.*;
import mmstream.consumer.*;

import java.util.*;

public class TOMS_AppManager implements AppManager {

  public TOMS_AppManager() {
    sessionAgent = null;
    streamManager = null;
    guiManager = null;
    unique_id = (int)(Math.random() * 1000);
    output = new Println_Output();
  }


  @Override
  public synchronized void
  registerSession(Session ses) throws AppManager_Exception {
    try {
      sessionManager.registerSession(ses);
    }
    catch(Session_Exception se) {
      throw new AppManager_Exception("AppManager.registerSession(Session): sessionManager.registerSession(Session):: " + se.getMessage());
    }
    guiManager.registerSession(ses);

    this.registerLocalSource(ses.getCoDec(), ses.getDefaultLocalSource());
  }
  
  @Override
  public synchronized void
  registerProtoSession(ProtoSession ses) throws AppManager_Exception {
    int action = ProtoSession.CONTINUE;
    try {
      action = sessionManager.registerProtoSession(ses);
    }
    catch(Session_Exception se) {
      throw new AppManager_Exception("AppManager.registerProtoSession(Session): sessionManager.registerProtoSession(Session):: " + se.getMessage());
    }
    switch(action) {
    case ProtoSession.LEAVE:
    case ProtoSession.JOIN:
      guiManager.updateProtoSession(ses.getId());
      break;
    case ProtoSession.CREATE:
      guiManager.registerProtoSession(ses);
      break;
    }
  }
 
  @Override
  public synchronized void
  joinProtoSession(ProtoSession proto, Session target) throws AppManager_Exception {
    try {
      sessionManager.joinProtoSession(proto, target);
    }
    catch (Session_Exception se) {
      throw new AppManager_Exception("TOMS_AppManager.joinProtoSession: sessionManager.joinProtoSession:: "+se.getMessage());
    }
    guiManager.deleteProtoSession(proto.getId());
    guiManager.registerSession(target);
    this.registerLocalSource(target.getCoDec(), target.getDefaultLocalSource());
  }

  @Override
  public synchronized void
  deleteSession(Session ses, String reason) throws AppManager_Exception {
    try {
      sessionManager.deleteSession(ses, reason);
    }
    catch (Session_Exception se) {
      throw new AppManager_Exception("TOMS_AppManager.deleteSession(Session,String): sessionManager.deleteSession(Session,String):: "+se.getMessage());
    }
    guiManager.deleteSession(ses);
  }

  @Override
  public synchronized void
  deleteSession(String ses, String reason) throws AppManager_Exception {
    Session s = sessionManager.getSession(ses);
    if (s != null) 
      this.deleteSession(s, reason);
    else
      throw new AppManager_Exception("TOMS_AppManager.deleteSession(String,String): session "+ses+"doesn't exists");
  }

  @Override
  public synchronized  void
  registerRemoteSource(ProtocolCoDec c, RemoteSource s) throws AppManager_Exception {
    Session ses = null;
    try {
      ses = sessionManager.registerRemoteSource(c, s);
    }
    catch(Session_Exception se) {
      throw new AppManager_Exception("AppManager.registerRemoteSource(RemoteSource): sessionManager.registerRemoteSource(RemoteSource):: " + se.getMessage());
    }
    guiManager.registerRemoteSource(ses, s);
  }

  @Override
  public synchronized  void
  registerLocalSource(ProtocolCoDec c, LocalSource s) throws AppManager_Exception {
    Session ses = null;
    try {
      ses = sessionManager.registerLocalSource(c, s);
    }
    catch(Session_Exception se) {
      throw new AppManager_Exception("AppManager.registerLocalSource(LocalSource): sessionManager.registerLocalSource(LocalSource):: " + se.getMessage());
    }
    if (ses != null)
      guiManager.registerLocalSource(ses, s);
  }

  @Override
  public void registerStream(Stream s) throws AppManager_Exception
  {
    try {
      streamManager.registerStream(s);
    }
    catch(Stream_Exception se) {
      throw new AppManager_Exception("AppManager.registerStream(Stream): streamManager.registerStream(Stream):: " + se.getMessage());
    }
    //    guiManager.registerStream(s);
  }


  @Override
  public void registerProducer(Session d, ProducerControl s, boolean start, int priority) throws AppManager_Exception
  {
    try {
      d.registerProducer(s, start, priority);
    }
    catch(Control_Exception se) {
      throw new AppManager_Exception("TOMS_AppManager.registerProducer(): session.registerProducer():: "+se.getMessage());
    }
    guiManager.registerProducer(d, s);
  }


  @Override
  public void registerConsumer(Session d, ConsumerControl s, boolean start, int priority) throws AppManager_Exception
  {
    try {
      d.registerConsumer(s, priority);
    }
    catch(Control_Exception se) {
      throw new AppManager_Exception("TOMS_AppManager.registerConsumer(): session.registerConsumer():: "+se.getMessage());
    }
    guiManager.registerConsumer(d, s);
    if (start)
      //      d.startConsumer(s);
      s.Start();
  }


  @Override
  public void finish(String reason) {
    guiManager.finish();
    sessionManager.finish(reason);
    streamManager.finish(reason);
    try {
      Thread.sleep((long)3000);
    }
    catch(InterruptedException e) {
      ;
    }
    System.exit(0);
  }

  @Override
  public synchronized StreamManager
  getStreamManager() { return streamManager; }

  @Override
  public synchronized SessionManager
  getSessionManager() { return sessionManager; }

  @Override
  public void setSessionAgent(SessionAgent sm) { sessionAgent = sm; }
  @Override
  public SessionAgent getSessionAgent() { return sessionAgent; }


  @Override
  public synchronized GuiManager
  getGuiManager() { return guiManager; }

  @Override
  public synchronized void
  setStreamManager(StreamManager sm) { streamManager = sm; }

  @Override
  public synchronized void
  setSessionManager(SessionManager sm) { sessionManager = sm; }


  @Override
  public synchronized void
  setGuiManager(GuiManager gm) { guiManager = gm; }

  @Override
  public synchronized int
  getUniqueId() {
    int ret;
    ret = unique_id++;
    return ret;
  }

//   public void setSessionMapper(SessionMapper sm) { sessionMapper = sm; }
//   public SessionMapper getSessionMapper() { return sessionMapper; }

  @Override
  public void setOutput(Output out) {
    output = out;
  }

  @Override
  public Output getOutput() { return output; }

  
  private Output output;
  private int unique_id;
  protected SessionMapper sessionMapper;
  protected SessionAgent sessionAgent;
  protected StreamManager streamManager;
  protected SessionManager sessionManager;
  protected GuiManager guiManager;

  @Override
  public TypeHandlerTable
getConnectionTypeHandlerTable()  {
  return conTypeHandler;
}

  @Override
  public TypeHandlerTable
getProtocolTypeHandlerTable() {
  return protTypeHandler;
}

  @Override
  public TypeHandlerTable
getAddressTypeHandlerTable() {
  return addrTypeHandler;
}

  @Override
  public TypeHandlerTable
getPayloadTypeHandlerTable() {
  return payloadTypeHandler;
}

  @Override
  public TypeHandlerTable
getConsumerTypeHandlerTable() {
  return consumerTypeHandler;
}

  @Override
  public TypeHandlerTable
getProducerTypeHandlerTable() {
  return producerTypeHandler;
}

  @Override
  public TypeHandlerTable
getSessionMapperTypeHandlerTable() {
  return sessionMapperTypeHandler;
}

  public final TypeHandlerTable conTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable protTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable addrTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable payloadTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable consumerTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable sessionMapperTypeHandler = new TypeHandlerTable();
  public final TypeHandlerTable producerTypeHandler = new TypeHandlerTable();
}


