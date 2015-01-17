package mmstream.apps;

import mmstream.source.*;
import mmstream.apps.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.config.*;
import mmstream.consumer.*;
import mmstream.producer.*;
import mmstream.gui.*;

import java.util.*;

public interface AppManager {

  public abstract void deleteSession(Session ses, String reason) throws AppManager_Exception;
  public abstract void deleteSession(String id, String reason) throws AppManager_Exception;
  public abstract void registerSession(Session ses) throws AppManager_Exception;

  public abstract void registerProtoSession(ProtoSession ses) throws AppManager_Exception;
  public abstract void joinProtoSession(ProtoSession proto, Session target) throws AppManager_Exception;
  public abstract void registerRemoteSource(ProtocolCoDec c, RemoteSource s) throws AppManager_Exception;
  public abstract void registerLocalSource(ProtocolCoDec c, LocalSource s) throws AppManager_Exception;

  public abstract void registerStream(Stream s) throws AppManager_Exception;

  public abstract void registerConsumer(Session d, ConsumerControl s, boolean start, int priority) throws AppManager_Exception;
  public abstract void registerProducer(Session d, ProducerControl s, boolean start, int priority) throws AppManager_Exception;

  public abstract void setSessionAgent(SessionAgent sm);
  public abstract SessionAgent getSessionAgent();
  
//   public abstract void setSessionMapper(SessionMapper sm);
//   public abstract SessionMapper getSessionMapper();

  public abstract void setStreamManager(StreamManager sm);
  public abstract StreamManager getStreamManager();

  public abstract void setSessionManager(SessionManager sm);
  public abstract SessionManager getSessionManager();

  public abstract void setGuiManager(GuiManager gm);
  public abstract GuiManager getGuiManager();

  public abstract int getUniqueId();

  public abstract void finish(String reason);

  public abstract void setOutput(Output out);
  public abstract Output getOutput();

public abstract TypeHandlerTable
getSessionMapperTypeHandlerTable();

public abstract TypeHandlerTable
getConnectionTypeHandlerTable();

public abstract TypeHandlerTable
getProtocolTypeHandlerTable();

public abstract TypeHandlerTable
getAddressTypeHandlerTable();

public abstract TypeHandlerTable
getPayloadTypeHandlerTable();

public abstract TypeHandlerTable
getConsumerTypeHandlerTable();

public abstract TypeHandlerTable
getProducerTypeHandlerTable();
}
