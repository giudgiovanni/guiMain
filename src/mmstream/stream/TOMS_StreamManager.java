package mmstream.stream;

import mmstream.source.*;
import mmstream.connection.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;

import java.util.*;

public class TOMS_StreamManager implements StreamManager {

  public TOMS_StreamManager(AppManager am) {
    appMan = am;
    output = new Println_Output();
    streamExporterTable = new Hashtable(8, (float).5);
    streamTable = new Hashtable(8, (float).5);
  }

  public void
  finish(String reason) {
    for (Enumeration e = streamExporterTable.elements(); e.hasMoreElements();) {
    
      StreamExporter s = (StreamExporter)(e.nextElement());
      s.finish(reason);
    }
  }

  public synchronized void 
  deleteStreamExporter(StreamExporter se) throws Stream_Exception {
    if (!streamExporterTable.containsKey(se.getId())) {
      throw new Stream_Exception("TOMS_StreamManager.registerStreamExporter(): StreamExporter "+se.getId()+" already registered");
    }
    streamExporterTable.remove(se.getId());
  }

  public synchronized void 
  registerStreamExporter(StreamExporter se) throws Stream_Exception {
    if (streamExporterTable.containsKey(se.getId())) {
      throw new Stream_Exception("TOMS_StreamManager.registerStreamExporter(): StreamExporter "+se.getId()+" already registered");
    }
    streamExporterTable.put(se.getId(), se);
  }

  public synchronized Enumeration
  getStreamExporters() {
    return streamExporterTable.keys();
  }

public synchronized void
registerStream(Stream s) throws Stream_Exception{
    if (streamTable.containsKey(s.getId())) {
      throw new Stream_Exception("TOMS_StreamManager.registerStream(): Stream "+s.getId()+" already registered");
    }
    streamTable.put(s.getId(), s);
  }

  public synchronized Hashtable
  getStreams() {
    return (Hashtable)streamTable.clone();
  }
  
  public Stream 
  importStream(StreamExporter e, String streamclassname) throws Stream_Exception {
    Class streamclass;
  
    try {
      streamclass = Class.forName(streamclassname);
    }
    catch (ClassNotFoundException ex) {
      throw new Stream_Exception("TOMS_StreamManger.importStream(StreamExporter,String): Class.forName("+streamclassname+"): "+ex.getMessage());
    }
    return importStream(e, streamclass);
  }

  public Stream 
  importStream(StreamExporter ex, Class streamclass) throws Stream_Exception {
    if (ex.getValid() != true) 
      throw new Stream_Exception("TOMS_StreamExporter not valid");
    
    
    String stream_name = new String("mmstream.stream.Stream");
    Stream ret;
    Class[] inflist = streamclass.getInterfaces();
    for (int i = 0; i < inflist.length; i++) {
      if (stream_name.compareTo(inflist[i].getName()) == 0) {
	try {
 	  ret = (Stream)streamclass.newInstance();
	  ret.setStreamExporter(ex);
	  ret.setId(Integer.toString(appMan.getUniqueId()));
	  ex.registerStream(ret);
	}
	catch(Stream_Exception se) {
	  throw new Stream_Exception("TOMS_StreamManger.importStream(StreamExporter,Class): ret.setExporter(): "+se.getMessage());
	}
	catch(InstantiationException ie) {
	  throw new Stream_Exception("TOMS_StreamManger.importStream(StreamExporter,Class): "+streamclass.getName()+".newInstance(): "+ie.getMessage());
	}
	catch(IllegalAccessException ie) {
	  throw new Stream_Exception("TOMS_StreamManger.importStream(StreamExporter,Class): "+streamclass.getName()+".newInstance(): "+ie.getMessage());
	}

	try {
 	  appMan.registerStream(ret);
 	}
 	catch (AppManager_Exception se) {
 	  throw new Stream_Exception("TOMS_StreamManger.importStream(StreamExporter,Class): appMan.registerStream(): "+se.getMessage());
 	}

	return ret;
      }
    }
    throw new Stream_Exception("TOMS_StreamManager.importStream(StreamExporter,Class): Class "+streamclass.getName()+" does not support Interface Stream");
  }



  public void setOutput(Output out) {
    output = out;
  }

  public Output getOutput() { return output; }

  private Output output;
  private AppManager appMan;
  private Hashtable streamExporterTable;
  private Hashtable streamTable;
}
