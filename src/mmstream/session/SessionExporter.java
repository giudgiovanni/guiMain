package mmstream.session;

import mmstream.connection.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;

import java.util.*;

public class SessionExporter extends Thread {

  public SessionExporter(AppManager app, Connection co, SessionMapper sm, long repeat) {
    this.appMan = app;
    this.connection = co;
    this.sessionMan = appMan.getSessionManager();
    this.sessionMapper = sm;
    this.repeatInt = repeat;
  }
  
public Connection getConnection() { return connection; }
  
public SessionMapper getSessionMapper() { return sessionMapper; }
  
  public void 
  run() {
    Chunk pack = null;

    while(true) {
      for (Enumeration e = sessionMan.getSessions(); e.hasMoreElements();) {
	Session tmp = (Session)(e.nextElement());
	if (tmp.getExport() && tmp.getStateCode() != Session.FINISHED) {
	  try {
	    pack = sessionMapper.session2Packet(tmp, ProtoSession.CONTINUE);
	  } 	    
	  catch(Session_Exception ce) {
	    appMan.finish("EXCEPTION: SessionMapper error");
	    ce.printStackTrace();
	    appMan.finish("Send Error");
	  }
	  try {
	    connection.send(pack);
	  }
	  catch(Connection_Exception ce) {
	    appMan.getOutput().error("EXCEPTION: SessionExporter: connection.send():: "+ce.getMessage());
	    ce.printStackTrace();
	    appMan.finish("Send Error");
	  }
	}
      }
      try {
	this.sleep(repeatInt);
      }
      catch(InterruptedException ie) {;}
    }
  }

  protected AppManager appMan;
  protected SessionMapper sessionMapper;
  protected Connection connection;
  protected SessionManager sessionMan;
  protected long repeatInt;
}
