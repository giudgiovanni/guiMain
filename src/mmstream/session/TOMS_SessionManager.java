package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;

import java.util.*;

public class TOMS_SessionManager implements SessionManager {

  public TOMS_SessionManager(AppManager am) {
    appMan = am;
    sessionAgent = appMan.getSessionAgent();
    streamMan = appMan.getStreamManager();

    sessionTable = new Hashtable(8, (float)0.5);
    protoSessionTable = new Hashtable(8, (float)0.5);
    output = new Println_Output();
  }

  public void
  finish(String reason) {
    for (Enumeration e = sessionTable.keys(); e.hasMoreElements();) {
    
      Session s = (Session)(e.nextElement());
      if (s.getStateCode() != Session.FINISHED) {
	s.finish(reason);
	sessionAgent.exportSession(s, ProtoSession.LEAVE);
      }
    }
  }

  public synchronized Enumeration
  getSessions() {
    return sessionTable.keys();
  }

  public synchronized Session
  getSession(String id) {
    Session s;
    for (Enumeration e = sessionTable.keys(); e.hasMoreElements();) {
      s = (Session)(e.nextElement());
      if (id.equals(s.getId()) == true) {
	return s;
      }
    }
    return null;
  }

  public synchronized ProtoSession
  getProtoSession(String id_plus_initiator) {
    String ps;
    for (Enumeration e = protoSessionTable.keys(); e.hasMoreElements();) {
      ps = (String)(e.nextElement());
      if (id_plus_initiator.equals(ps) == true) {
	return (ProtoSession)protoSessionTable.get(ps);
      }
    }
    return null;
  }

  public synchronized void
  deleteSession(Session s, String reason) throws Session_Exception {
    SourceTable st = (SourceTable)sessionTable.remove(s);
    if (st == null)
      throw new Session_Exception("TOMS_SessionManager.deleteSession(Session,String): Session doesn't exists");

    try {
      for (Enumeration e = st.localElements(); e.hasMoreElements();) {
	StreamExporter se = (StreamExporter)(e.nextElement());
	streamMan.deleteStreamExporter(se);
      }
    }
    catch(Stream_Exception sx) { ; }
    
    try {
      for (Enumeration e = st.remoteElements(); e.hasMoreElements();) {
	StreamExporter se = (StreamExporter)(e.nextElement());
	streamMan.deleteStreamExporter(se);
      }
    }
    catch(Stream_Exception sx) { ; }

    sessionAgent.exportSession(s, ProtoSession.LEAVE);
    s.finish(reason);
  }
  
  public synchronized void
  deleteSession(String id, String reason) throws Session_Exception {
    Session s;
    for (Enumeration e = sessionTable.keys(); e.hasMoreElements();) {
      s = (Session)(e.nextElement());
      if (id.equals(s.getId()) == true) {
	this.deleteSession(s, reason);
	return;
      }
    }
    throw new Session_Exception("TOMS_SessionManager.deleteSession(String,String): Session doesn't exists");
  }

  public synchronized void
  registerSession(Session s) throws Session_Exception {
    this.registerSession(s, ProtoSession.CREATE);
  }

  protected synchronized void
  registerSession(Session s, int action) throws Session_Exception {
    if (sessionTable.get(s) != null) 
      throw new Session_Exception("TOMS_SessionManager.registerSession(Session): Session exists");
    sessionTable.put(s, new SourceTable());

    if (s.getExport() == true) {
      sessionAgent.exportSession(s, action);
    }
  }

  public synchronized int
  registerProtoSession(ProtoSession s) throws Session_Exception {
    ProtoSession old = (ProtoSession)protoSessionTable.get(s.getId()+s.getInitiator());
    if (old  != null) {
      int ret = ProtoSession.CONTINUE;
      if (!old.getParticipants().contains(s.getParticipants().firstElement())) {
	old.getParticipants().addElement(s.getParticipants().firstElement());
	ret = ProtoSession.JOIN;
      }
      if (s.getActionCode() == ProtoSession.LEAVE) {
	old.getParticipants().removeElement(s.getParticipants().firstElement());
	ret = ProtoSession.LEAVE;
      }
      return ret;
    }
    else {
      for (Enumeration e = sessionTable.keys(); e.hasMoreElements();) {
	Session tmp = (Session)(e.nextElement());
	if ((s.getId()+s.getInitiator()).equals(tmp.getId()+tmp.getInitiator())) {
	  return ProtoSession.CONTINUE;
	}
      }
      protoSessionTable.put(s.getId()+s.getInitiator(), s);
      if (s.getActionCode() == ProtoSession.LEAVE) {
	return ProtoSession.CONTINUE;
      }
      return ProtoSession.CREATE;
    }
  }

  public synchronized void 
  joinProtoSession(ProtoSession proto, Session target) throws Session_Exception {
    this.registerSession(target, ProtoSession.JOIN);
    protoSessionTable.remove(proto.getId()+proto.getInitiator());
  }

  public synchronized void
  registerRemoteSource(Session d, RemoteSource s) throws Session_Exception{
    SourceTable s_tab = (SourceTable)(sessionTable.get(d));
    if (s_tab == null) 
      throw new Session_Exception("TOMS_SessionManager.registerRemoteSource(Session,RemoteSource): Session not registered");
    else if (s_tab.containsRemoteSource(s) == true)
      throw new Session_Exception("TOMS_SessionManager.registerRemoteSource(Session,RemoteSource): Source exists");
  
    try {
      streamMan.registerStreamExporter(s);
    }
    catch(Stream_Exception se) {
      throw new Session_Exception("TOMS_SessionManager.registerRemoteSource(Session,RemoteSource): streamMan.registerStreamExporter():: ");
    }
    s_tab.registerRemoteSource(s);
  }


  public synchronized Session
  registerRemoteSource(ProtocolCoDec d, RemoteSource s) throws Session_Exception{
    Enumeration e = sessionTable.keys();
    Session tmp;

    for (; e.hasMoreElements();) {
      tmp = (Session)e.nextElement();
      if (d == tmp.getCoDec()) {
	this.registerRemoteSource(tmp, s);
	return tmp;
      }
    }

    throw new Session_Exception("TOMS_SessionManager.registerRemoteSource(CoDec,RemoteSource): CoDec not registered");
  }


  public synchronized boolean
  registerLocalSource(Session d, LocalSource s) throws Session_Exception{
    SourceTable s_tab = (SourceTable)(sessionTable.get(d));
    if (s_tab == null) 
      throw new Session_Exception("TOMS_SessionManager.registerLocalSource(Session,LocalSource): Session not registered");
    else if (s_tab.containsLocalSource(s) == true) {
      if (s == d.getDefaultLocalSource())
	return false;
      else
	throw new Session_Exception("TOMS_SessionManager.registerLocalSource(Session,LocalSource): Source exists");
    }

    try {
      streamMan.registerStreamExporter(s);
    }
    catch(Stream_Exception se) {
      throw new Session_Exception("TOMS_SessionManager.registerLocalSource(Session,RemoteSource): streamMan.registerStreamExporter():: ");
    }

    s_tab.registerLocalSource(s);

    return true;
  }


  public synchronized Session
  registerLocalSource(ProtocolCoDec d, LocalSource s) throws Session_Exception{
    Enumeration e = sessionTable.keys();
    Session tmp;

    for (; e.hasMoreElements();) {
      tmp = (Session)e.nextElement();
      if (d == tmp.getCoDec()) {
	if (this.registerLocalSource(tmp, s) == true)
	  return tmp;
	else 
	  return null;
      }
    }

    throw new Session_Exception("TOMS_SessionManager.registerLocalSource(CoDec,LocalSource): CoDec not registered");
  }



  public Stream 
  importStream(Session d, Class streamclass) throws Stream_Exception {
    Stream ret;
    ProtocolCoDec c = d.getCoDec();

    RemoteSource sr = null;
    do {
    // the following line blocks until a new RemoteSource is available at the
    // CoDec of Session d
      sr = c.getRemoteSource();
    }
    while(sr.getUsed() == true);

    ret = streamMan.importStream(sr, streamclass);
    sr.setUsed(true);
    sr.output.notifyStateChange();
    return ret;
  }

  public Stream 
  importStream(Session d, String streamclassname) throws Stream_Exception {
    Class streamclass = null;
  
    try {
      streamclass = Class.forName(streamclassname);
    }
    catch (ClassNotFoundException ex) {
      throw new Stream_Exception("TOMS_SessionManger.importStream(Session,String): Class.forName("+streamclassname+"): "+ex.getMessage());
    }
    return this.importStream(d, streamclass);
  }

  public void setOutput(Output out) {
    output = out;
  }

  public Output getOutput() { return output; }

  public void setSessionAgent(SessionAgent sa) { sessionAgent = sa; }
  public SessionAgent getSessionAgent() { return sessionAgent; }
  
  private Output output;
  private AppManager appMan;
  private StreamManager streamMan;
  private SessionAgent sessionAgent;
  private Hashtable sessionTable;
  private Hashtable protoSessionTable;
}








