package mmstream.config;

import mmstream.config.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.connection.*;
import mmstream.stream.*;
import mmstream.protocols.*;
import mmstream.address.*;

public class Config extends Object {

public Config(AppManager am, Profile p, ProtocolCoDec c, Connection dc, Connection cc) {
  appMan = am;
  codec = c;
  dataCon = dc;
  controlCon = cc;
  prof = p;
  
}


public ProtocolCoDec
getCoDec() { return codec; }

public Connection
getControlConnection() { return controlCon; }

public Connection
getDataConnection() { return dataCon; }

public Profile
getProfile() { return prof; }

public AppManager
getAppManager() { return appMan; }

 protected AppManager appMan;
 protected ProtocolCoDec codec;
 protected Connection dataCon;
 protected Connection controlCon;
 protected Profile prof;
}
