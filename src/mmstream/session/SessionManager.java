package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.stream.*;

import java.util.*;

public interface SessionManager {

  public abstract void finish(String reason);

  public abstract Session getSession(String id);
  public abstract Enumeration getSessions();
  public abstract ProtoSession getProtoSession(String id);

  public abstract void deleteSession(String id, String reason) throws Session_Exception;
  public abstract void deleteSession(Session s, String reason) throws Session_Exception;
  public abstract void registerSession(Session s) throws Session_Exception;

  public abstract void joinProtoSession(ProtoSession proto, Session target) throws Session_Exception;

  public abstract int registerProtoSession(ProtoSession s) throws Session_Exception;

  public abstract void registerRemoteSource(Session d, RemoteSource s) throws Session_Exception;
  public abstract Session registerRemoteSource(ProtocolCoDec d, RemoteSource s) throws Session_Exception;

  public abstract boolean registerLocalSource(Session d, LocalSource s) throws Session_Exception;
  public abstract Session registerLocalSource(ProtocolCoDec d, LocalSource s) throws Session_Exception;

  public Stream importStream(Session d, Class streamclass) throws Stream_Exception;
  public Stream importStream(Session d, String streamclassname) throws Stream_Exception;

  public abstract void setSessionAgent(SessionAgent sa);
  public abstract SessionAgent getSessionAgent();

  public abstract void setOutput(Output out);
  public abstract Output getOutput();
}








