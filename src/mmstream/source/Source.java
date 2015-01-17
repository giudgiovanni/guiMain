package mmstream.source;


import mmstream.util.*;
import mmstream.source.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.stream.*;
import mmstream.connection.*;
import mmstream.producer.*;

import java.util.*;

public abstract class Source extends Object implements StreamExporter {
  
public  
Source(ProtocolCoDec c) {
  valid = false;
  used = false;
  finished = false;
  sender = false;

  dataAddr = null;
  controlAddr = null;
  dataConnection = null;
  controlConnection = null;
  codec = c;
  id = null;
  cname = null;
  name = null;
  tool = null;
  phone = null;
  email = null;
  note = null;
  loc = null;
  reasonForFinishing = null;

  output = new Println_SourceOutput();

  producer = null;
  streamList = new Vector(5, 5);
  this.setProfile(codec.getProfile());
}

public abstract void finish(String reason);

public void  // no synchronized necessary because the Vector streamList is synchronized
registerStream(Stream si) throws Stream_Exception {
  if (valid == false)
    throw new Stream_Exception("Source.registerStream(): this not valid");
  used = true;

  streamList.addElement(si);
}

public void
closeStream(Stream si) {
  streamList.removeElement(si);
  if (streamList.size() == 0) {
    used = false;
  }    
}

public void
exportChunk(Chunk p) throws Stream_Exception {
  Stream importer;

  for (Enumeration e = streamList.elements() ; e.hasMoreElements();) {
    importer = (Stream)(e.nextElement());
    if (importer.getCopy() == true)
      importer.importChunk((Chunk)(p.clone()));
    else
      importer.importChunk(p);
  }
}  

public ProducerControl getProducer() {
  return producer;
}

public void setProducer(ProducerControl prod) {
  producer = prod;
}


public abstract SenderStatistics 
getSenderStats();

protected abstract void 
fillInSenderStats(SenderStatistics stat);

public void
setUsed(boolean val) { used = val; }

public void
setFinished(boolean val) { 
  finished = val; 
  if (val == true) {
    Stream importer = null;
    for (Enumeration e = streamList.elements() ; e.hasMoreElements();) {
      importer = (Stream)(e.nextElement());
      importer.alert();
    }
  }
}

public void
setValid(boolean val) { 
  valid = val; 
  if (val == false) {
    Stream importer = null;
    for (Enumeration e = streamList.elements() ; e.hasMoreElements();) {
      importer = (Stream)(e.nextElement());
      System.err.println("ERROR: Source: invalidating importer importing from "+importer.getStreamExporter().getId());
      importer.alert();
    }
  }
}

public void
setDataAddress(Address val) { dataAddr = val; }

public void
setControlAddress(Address val) { controlAddr = val; }

public void
setDataConnection(Connection val) { dataConnection = val; }

public void
setControlConnection(Connection val) { controlConnection = val; }

public boolean
getUsed() { return used; }

public boolean
getFinished() { return finished; }

public boolean
getValid() { return valid; }

public Address
getDataAddress() { return dataAddr; }

public Address
getControlAddress() { return controlAddr; }

public Connection
getDataConnection() { return dataConnection; }

public Connection
getControlConnection() { return controlConnection; }

public ProtocolCoDec 
getCoDec() { return codec; }

public Profile
getProfile() { return profile; }

public void
setProfile(Profile pro) { profile = pro; }

public void 
setId(String val) { id = val; }

public void 
setCname(String val) { cname = val; }

public void 
setName(String val) { name = val; }

public void 
setPhone(String val) { phone = val; }

public void 
setEmail(String val) { email = val; }

public void 
setLoc(String val) { loc = val; }

public void 
setNote(String val) { note = val; }

public void 
setTool(String val) { tool = val; }
 
public void 
setReasonForFinishing(String val) { reasonForFinishing = val; } 

public String 
getId() { return id; }

public String 
getState() { 
  if (finished == true) return ("Finished");
  if (used == true)     return ("Used");
  if (valid == true)    return ("Valid");
  return ("Not valid");
}

public String 
getCname() { return cname; }

public String 
getName() { return name; }

public String 
getPhone() { return phone; }

public String 
getEmail() { return email; }

public String 
getLoc() { return loc; }

public String 
getNote() { return note; }

public String 
getTool() { return tool; } 


public String 
getReasonForFinishing() { return reasonForFinishing; } 

public Master_Clock getMasterClock() { return clock; }
public Clock createSlaveClock() { return clock.getSlave(); }
  
protected Master_Clock clock;
public ProducerControl producer;

protected Address dataAddr;
protected Address controlAddr;
protected Connection dataConnection;
protected Connection controlConnection;
protected Profile profile;
protected ProtocolCoDec codec;

protected boolean finished;  
protected boolean valid;  
protected boolean used;  

protected boolean sender;

public SourceOutput output;

protected String tool;
protected String note;
protected String loc;
protected String email;
protected String phone;
protected String name;
protected String id;
protected String cname;

protected String reasonForFinishing;
protected Vector streamList;
}
