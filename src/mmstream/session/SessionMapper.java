package mmstream.session;

import mmstream.session.*;
import mmstream.connection.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.apps.*;
import mmstream.util.*;

import java.util.*;

public interface SessionMapper {

public abstract int 
  getSessionDescriptionLength();

public abstract Chunk 
  session2Packet(Session session, int action) throws Session_Exception;

public ProtoSession
  packet2ProtoSession(Chunk pack) throws Session_Exception;

}


  
