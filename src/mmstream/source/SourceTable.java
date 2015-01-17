package mmstream.source;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.stream.*;

import java.util.*;

public class SourceTable  {
public SourceTable() {
  remoteTable = new Hashtable(5, (float)0.5);
  localTable = new Hashtable(5, (float)0.5);
//   streamTable = new Hashtable(5, (float)0.5);

  randomizer = new java.util.Random();  
}

public void 
registerRemoteSource(RemoteSource s) {
  Object old = remoteTable.put(s.getId(), s);
//   if (old != null)
//     System.out.println("SourceTable: replaced Remote Source "+((RemoteSource)old).getId()+" with "+s.getId());
//   else
//     System.out.println("SourceTable: added Remote Source "+s.getId());

}

// public void 
// registerStream(Stream s) {
//   streamTable.put(s.getId(), s);
// }


public void 
registerLocalSource(LocalSource s) {
  Object old = localTable.put(s.getId(), s);
//   if (old != null)
//     System.out.println("SourceTable: replaced Local Source "+((LocalSource)old).getId()+" with "+s.getId());
//   else
//     System.out.println("SourceTable: added Local Source "+s.getId());
}


public boolean 
containsRemoteSource(RemoteSource s) {
  return remoteTable.contains(s);
}

public boolean 
containsKeyRemoteSource(String s) {
  return remoteTable.containsKey(s);
}

// public boolean 
// containsStream(Stream s) {
//   return streamTable.contains(s);
// }

public boolean 
containsKeyLocalSource(String s) {
  return localTable.containsKey(s);
}

public boolean 
containsLocalSource(LocalSource s) {
  return localTable.contains(s);
}

// public boolean 
// containsKeyStream(String s) {
//   return streamTable.containsKey(s);
// }

public RemoteSource 
queryRemoteSource(String s) {
  return (RemoteSource)remoteTable.get(s);
}

// public Stream 
// queryStream(String s) {
//   return (Stream)streamTable.get(s);
// }

public LocalSource 
queryLocalSource(String s) {
  return (LocalSource)localTable.get(s);
}

public Enumeration 
remoteElements() {
  return remoteTable.elements();
}

public Enumeration 
localElements() {
  return localTable.elements();
}

// public Enumeration 
// streamElements() {
//   return streamTable.elements();
// }

public Enumeration 
remoteKeys() {
  return remoteTable.keys();
}

public Enumeration 
localKeys() {
  return localTable.keys();
}

// public Enumeration 
// streamKeys() {
//   return streamTable.keys();
// }

public int 
remoteSize() {
  return remoteTable.size();
}

public int 
localSize() {
  return localTable.size();
}

// public int 
// streamSize() {
//   return streamTable.size();
// }

protected  Hashtable remoteTable;
// protected  Hashtable streamTable;
protected  Hashtable localTable;
public Random randomizer;
}



