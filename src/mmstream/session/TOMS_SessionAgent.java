
package mmstream.session;

import mmstream.address.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;

import java.util.*;

public class TOMS_SessionAgent implements SessionAgent {

  public TOMS_SessionAgent(AppManager app) {
    this.appMan = app;

    importTable = new Hashtable(4, (float)0.5);
    exportTable = new Hashtable(4, (float)0.5);

  }

  public void
  finish() {
    for (Enumeration e = importTable.keys(); e.hasMoreElements();) {
         ((SessionImporter)(e.nextElement())).stop();
    }
  }

  public synchronized void
  exportSession(Session s, int action) {
    Chunk send = null;
    SessionExporter sexp;
    for (Enumeration e = exportTable.elements(); e.hasMoreElements();) {
      sexp = (SessionExporter)(e.nextElement());
      try {
	send = sexp.getSessionMapper().session2Packet(s, action);
      }
      catch(Session_Exception se){
	System.err.println("EXCEPTION: TOMS_SessionAgent.exportSession: SessionMapper.session2Packet("+s.getId()+")::"+se.getMessage());
	se.printStackTrace();
	System.exit(1);
      }
      if (send != null) {
	try {
	  sexp.getConnection().send(send);
	} catch (Connection_Exception se) {
	  System.err.println("EXCEPTION: TOMS_SessionAgent.exportSession: connection.send(()::"+se.getMessage());
	  se.printStackTrace();
	  System.exit(1);
	}
      }
    }
  }


  public synchronized void
  addSessionExport(Connection con, SessionMapper sm, long repeatIntervall) throws Session_Exception {
    if (exportTable.containsKey(con) == true)
      throw new Session_Exception("TOMS_SessionAgent.addSessionExport(): Connection exists!");
    SessionExporter si = new SessionExporter(appMan, con, sm, repeatIntervall);
    exportTable.put(con, si);
    si.start();
    //    System.out.println("MESSAGE: started SessionExporter on "+con.getLocalAddress().toString());
  }

  public synchronized void
  addSessionImport(Connection con, SessionMapper sm) throws Session_Exception {
    if (importTable.containsKey(con) == true) 
      throw new Session_Exception("TOMS_SessionAgent.addSessionImport(): Connection exists!");

    SessionImporter si = new SessionImporter(appMan, this, con, sm);
    importTable.put(con, si);
    si.start();
    //    System.out.println("MESSAGE: started SessionImporter on "+con.getLocalAddress().toString());
  }

  public synchronized void 
  deleteSessionImport(Connection con) {
    if (importTable.containsKey(con) == true) {
      SessionImporter si = (SessionImporter)importTable.get(con);
      si.stop();
      con.close();
      importTable.remove(con);
    }
  }

  public synchronized void 
  deleteSessionExport(Connection con) {
    if (exportTable.containsKey(con) == true) {
      SessionExporter si = (SessionExporter)exportTable.get(con);
      si.stop();
      con.close();
      exportTable.remove(con.getRemoteAddress().toString());
    }
  }

    

  public Connection getExportConnection(Address ad) {
    for (Enumeration e = exportTable.keys(); e.hasMoreElements();) {
      Connection c = (Connection)(e.nextElement());
      if (ad.equals(c.getRemoteAddress()) ||
	  ad.equals(c.getLocalAddress())) {
	return c;
      }
    }
    return null;
  }

  public Connection getImportConnection(Address ad) {
    for (Enumeration e = importTable.keys(); e.hasMoreElements();) {
      Connection c = (Connection)(e.nextElement());
      if (ad.equals(c.getRemoteAddress()) ||
	  ad.equals(c.getLocalAddress())) {
	return c;
      }
    }
    return null;
  }

  public SessionMapper getSessionMapper(Address ad) {
    for (Enumeration e = importTable.keys(); e.hasMoreElements();) {
      Connection c = (Connection)(e.nextElement());
      if (ad.equals(c.getRemoteAddress()) ||
	  ad.equals(c.getLocalAddress())) {
	return ((SessionImporter)(importTable.get(c))).getSessionMapper();
      }
    }
    return null;
  }

 protected AppManager appMan;
 protected SessionMapper sessionMapper;
 protected Hashtable exportTable;
 protected Hashtable importTable;
}
