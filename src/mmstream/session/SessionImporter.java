package mmstream.session;

import mmstream.connection.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;

public class SessionImporter extends Thread {

  public SessionImporter(AppManager app, SessionAgent parent, Connection co, SessionMapper sm) {
    this.appMan = app;
    this.connection = co;
    this.header = this.connection.getHeaderSize();
    this.parent = parent;
    this.sessionMapper = sm;
  }
  
public Connection getConnection() { return connection; }
  
public SessionMapper getSessionMapper() { return sessionMapper; }
  
  public void 
  run() {
    Chunk pack = null;
    int len = sessionMapper.getSessionDescriptionLength(); 

  main_loop:
    for(;true;) {
      try {
	pack = connection.receive(len);
      } catch(Connection_Exception ce) {
	appMan.getOutput().error("EXCEPTION: SessionImporter: connection.receive(int):: "+ce.getMessage());
	appMan.finish("Reception Error");
      }
      if (parent.getExportConnection(pack.sourceTransportAddress) != null) {
	continue main_loop; // ignore packet
      }
      ProtoSession newSession = null;
      try {
	newSession = sessionMapper.packet2ProtoSession(pack);
      }
      catch(Session_Exception se) { 
	appMan.getOutput().error("EXCEPTION: SessionImporter: sessionMapper.packet2ProtoSession(int):: "+se.getMessage());
      }

      if (newSession != null) {
	try {
	  appMan.registerProtoSession(newSession);
	} catch(AppManager_Exception ae) {
	  appMan.getOutput().error("EXCEPTION: SessionImporter: appMan.registerUnjoinedSession(Session):: "+ae.getMessage());
	}	  
      }
    }
  }

  protected AppManager appMan;
  protected SessionMapper sessionMapper;
  protected SessionAgent parent;
  protected Connection connection;
  protected int header;  

}
