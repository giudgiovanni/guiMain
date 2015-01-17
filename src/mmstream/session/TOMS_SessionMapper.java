package mmstream.session;

import mmstream.session.*;
import mmstream.connection.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.apps.*;
import mmstream.util.*;

import java.util.*;

public class TOMS_SessionMapper extends Object implements SessionMapper {

  public final static byte CONNECTION_BC = 1;

  public final static byte DATA_ADDRESS_BC = 2;

  public final static byte CONTROL_ADDRESS_BC = 3;

  public final static byte PACKETSIZE_BC = 4;

  public final static byte PROTOCOL_BC = 10;

  public final static byte PAYLOADTYPE_BC = 11;

  public final static byte CHUNKSIZE_BC = 12;

  public final static byte BANDWIDTH_BC = 13;

  public final static byte ACTION_BC = 20;

  public final static byte INITIATOR_BC = 21;

  public final static byte PARTICIPANT_BC = 22;

  public final static byte ID_BC = 23;





  public int 
  getSessionDescriptionLength() {
    return SessionDescriptionLength;
  }

  
  public Chunk 
  session2Packet(Session session, int action) throws Session_Exception {
    int os = 0;
    Chunk ret = null;
    try {
      ret = new Chunk(SessionDescriptionLength);
    } catch(Chunk_Exception pae) { ; }

    // the 'header'... 
    String name = "TOMS";
    name.getBytes(0, 4, ret.buffer, os); 
    os += name.length();

    // Connection
    Connection con = session.getDataConnection();
    Type t = (Type)(conTypeHandler.queryType(con.getTypeName())); 
    if (t == null) throw new Session_Exception("SessionMapper.session2Packet: unknown Connection "+con.getTypeName());

    ret.buffer[os++] = CONNECTION_BC;
    ret.buffer[os++] = t.getByteCode();
    ret.buffer[os++] = 0;


    // Data Address...
    Address ad = con.getRemoteAddress();
    t = (Type)(addrTypeHandler.queryType(ad.getName())); 
    if (t == null) throw new Session_Exception("SessionMapper.session2Packet: unknown Address "+ad.getName());
    
    ret.buffer[os++] = DATA_ADDRESS_BC;
    ret.buffer[os++] = t.getByteCode();
    String tmp = ad.toString();
    ret.buffer[os++] = (byte)(127 & tmp.length());
    tmp.getBytes(0, (127 & tmp.length()), ret.buffer, os);
    os += (127 & tmp.length());

    // ...Control Address
    con = session.getControlConnection();
    ad = con.getRemoteAddress();
    t = (Type)(addrTypeHandler.queryType(ad.getName())); 
    if (t == null) throw new Session_Exception("SessionMapper.session2Packet: unknown Address "+ad.getName());
    
    ret.buffer[os++] = CONTROL_ADDRESS_BC;
    ret.buffer[os++] = t.getByteCode();
    tmp = ad.toString();
    ret.buffer[os++] = (byte)(127 & tmp.length());
    tmp.getBytes(0, (127 & tmp.length()), ret.buffer, os);
    os += (127 & tmp.length());

    // Connection Packet Length
    int ms = con.getMaxSize();
    ret.buffer[os++] = PACKETSIZE_BC;
    ret.buffer[os++] = (byte)((ms >>> 030) & 0xff);
    ret.buffer[os++] = (byte)((ms >>> 020) & 0xff);
    ret.buffer[os++] = (byte)((ms >>> 010) & 0xff);
    ret.buffer[os++] = (byte) (ms & 0xff);

    // Protocol/Profile
    Profile prof = session.getProfile();
    t = (Type)(protTypeHandler.queryType(prof.getName())); 
    if (t == null) throw new Session_Exception("SessionMapper.session2Packet: unknown Profile "+prof.getName());
    
    ret.buffer[os++] = PROTOCOL_BC;
    ret.buffer[os++] = t.getByteCode();
    byte[] btmp = prof.getParams();
    if (btmp != null) {
      ret.buffer[os++] = (byte)(127 & btmp.length);
      System.arraycopy(btmp, 0, ret.buffer, os, (127 & btmp.length));
      os += (127 & btmp.length);
    }
    else 
      ret.buffer[os++] = 0;

    // Payload
    PayloadType pt = prof.getPayloadType(); 
    ret.buffer[os++] = PAYLOADTYPE_BC;
    ret.buffer[os++] = pt.getByteCode();
    btmp = pt.getParams();
    if (btmp != null) {
      ret.buffer[os++] = (byte)(127 & btmp.length);
      System.arraycopy(btmp, 0, ret.buffer, os, (127 & btmp.length));
      os += (127 & btmp.length);
    }
    else
      ret.buffer[os++] = 0;

    // Action
    ret.buffer[os++] = ACTION_BC;
    ret.buffer[os++] = (byte)action;

    ret.buffer[os++] = INITIATOR_BC;
    tmp = session.getInitiator();
    int len = (127 & tmp.length());
    ret.buffer[os++] = (byte)len;
    if (len > 0) {
      tmp.getBytes(0, len, ret.buffer, os);
      os += len;
    }

    ret.buffer[os++] = PARTICIPANT_BC;
    tmp = session.getDefaultLocalSource().getCname();
    len = (127 & tmp.length());
    ret.buffer[os++] = (byte)len;
    if (len > 0) {
      tmp.getBytes(0, len, ret.buffer, os);
      os += len;
    }

    ret.buffer[os++] = ID_BC;
    tmp = session.getId();
    len = (127 & tmp.length());
    ret.buffer[os++] = (byte)len;
    if (len > 0) {
      tmp.getBytes(0, len, ret.buffer, os);
      os += len;
    }

    ret.data_length = os;
    
    return ret;
  }

  public ProtoSession
  packet2ProtoSession(Chunk pack) throws Session_Exception {
    ProtoSession ret = new ProtoSession();
    ret.setInstantiable(true);
    int i = pack.data_offset;
    byte len = 0;
    byte[] pa = null;
    Type t = null;

        // the 'header'... 
    String name = new String(pack.buffer, 0, i, 4);
    if (!name.equals("TOMS"))
      throw new Session_Exception("TOMS_SessionAgent.packet2ProtoSession(): packet not a TOMS session description");
    i += 4;

    while(i < pack.data_offset+pack.data_length) {
      len = 0;
      pa = null;
      t = null;

      switch(pack.buffer[i++]) {

      case CONNECTION_BC:
	t = conTypeHandler.queryType(pack.buffer[i++]);
	if (t == null) {
	  t = new Type("Unknown", pack.buffer[i-1]);
	  ret.setInstantiable(false);
	}
	//	System.out.println("Connection type is "+t.getName());
	ret.setConnectionType(t);
	len = pack.buffer[i++];
	if (len > 0) {
	  pa = new byte[len];
	  System.arraycopy(pack.buffer, i, pa, 0, len);
	  i += len;
	  t.setParams(pa);
	}
	break;
	
      case PACKETSIZE_BC:
    // Connection Packet Length
	int ms = 0;
	ms = (((int)pack.buffer[i] & 0xff) << 030) | (((int)pack.buffer[i+1] & 0xff) << 020)
	  | (((int)pack.buffer[i+2] & 0xff) << 010) | ((int)pack.buffer[i+3] & 0xff);
	i+=4;
	ret.setDataMaxSize(ms);
	break;

      case DATA_ADDRESS_BC:
	t = addrTypeHandler.queryType(pack.buffer[i++]);
	if (t == null) {
	  t = new Type("Unknown", pack.buffer[i-1]);
	  ret.setInstantiable(false);
	}
	ret.setAddressType(t);
	//	System.out.println("Data Address type is "+t.getName());
	len = pack.buffer[i++];
	if (len > 0) {
	  pa = new byte[len];
	  System.arraycopy(pack.buffer, i, pa, 0, len);
	  i += len;
	  if (t != null) {
	    Vector classes = conTypeHandler.queryClasses(t);
	    if (classes == null || classes.size() == 0) {
	      ret.setInstantiable(false);
	    } 
	    ret.setDataAddress(new String(pa, 0));
	    //	    System.out.println("Control Addres is "+ret.getDataAddress());
	  }
	}
	break;

      case CONTROL_ADDRESS_BC:
	t = addrTypeHandler.queryType(pack.buffer[i++]);
	if (t == null) {
	  t = new Type("Unknown", pack.buffer[i-1]);
	  ret.setInstantiable(false);
	}
	//	System.out.println("Control Address type is "+t.getName());
	ret.setAddressType(t);
	len = pack.buffer[i++];
	if (len > 0) {
	  pa = new byte[len];
	  System.arraycopy(pack.buffer, i, pa, 0, len);
	  i += len;
	  if (t != null) {
	    Vector classes = conTypeHandler.queryClasses(t);
	    if (classes == null || classes.size() == 0) {
	      ret.setInstantiable(false);
	    } 
	    ret.setControlAddress(new String(pa, 0));
	    //	    System.out.println("Control Addres is "+ret.getControlAddress());
	  }
	}
	break;

      case PROTOCOL_BC:
	t = protTypeHandler.queryType(pack.buffer[i++]);
	if (t == null) {
	  t = new Type("Unknown", pack.buffer[i-1]);
	  ret.setInstantiable(false);
	}
	//	System.out.println("Protocol type is "+t.getName());
	ret.setProtocolType(t);
	len = pack.buffer[i++];
	if (len > 0) {
	  pa = new byte[len];
	  System.arraycopy(pack.buffer, i, pa, 0, len);
	  i += len;
	  t.setParams(pa);
	}
	if (t != null) {
	  Vector classes = protTypeHandler.queryClasses(t);
	  if (classes == null || classes.size() == 0)
	    ret.setInstantiable(false);
	  else if (classes.size() == 1) {
	    //	    System.out.println("Found 1 class: "+((TypeHandler)(classes.firstElement())).getHandlerClass().getName());
	    Profile p = null;
	    try {
	      p = ((Profile) ((TypeHandler)(classes.firstElement())).getHandlerClass().newInstance());
	    }
	    catch(IllegalAccessException ie) {
	      throw new Session_Exception("SessionManpper.packet2ProtoSession(): "+t.getName()+".newInstance() :: "+ie.getMessage());
	    }
	    catch(InstantiationException ie) {
	      throw new Session_Exception("SessionManpper.packet2ProtoSession(): "+t.getName()+".newInstance() :: "+ie.getMessage());
	    }
	    if (len > 0)
	      p.configure(pa, 0, pa.length);
	    ret.setProfile(p);
	    //	    System.out.println("Stored profile");
	  }
	  else { 
	    //	    System.out.println("Found more than 1 class: "+classes.size());
	    for (Enumeration e = classes.elements(); e.hasMoreElements();) {
	      TypeHandler tmp = (TypeHandler)(e.nextElement());
	      //	      System.out.println("Handler for "+t.getName()+" is "+tmp.getHandlerClass().getName());
	    }
	  }
	}
	break;

      case PAYLOADTYPE_BC:
	PayloadType pt = (PayloadType)(payloadTypeHandler.queryType(pack.buffer[i++]));
	if (pt == null) {
	  pt = new PayloadType("Unknown", pack.buffer[i-1]);
	  ret.setInstantiable(false);
	}
	//	System.out.println("Payload type is "+pt.getName());
	ret.setPayloadType(pt);
	len = pack.buffer[i++];
	if (len > 0) {
	  pa = new byte[len];
	  System.arraycopy(pack.buffer, i, pa, 0, len);
	  i += len;
	  pt.setParams(pa);
	}
	break;

      case ACTION_BC:
	ret.setAction(pack.buffer[i++]);
	//	System.out.println("action is "+ret.getAction());
	break;
	
      case INITIATOR_BC:
	len = pack.buffer[i++];
	if (len > 0) {
	pa = new byte[len];
	System.arraycopy(pack.buffer, i, pa, 0, len);
	i += len;
	ret.setInitiator(new String(pa, 0));
	//	System.out.println("Initiator is "+ret.getInitiator());
	}
	break;

      case PARTICIPANT_BC:
	len = pack.buffer[i++];
	if (len > 0) {
	pa = new byte[len];
	System.arraycopy(pack.buffer, i, pa, 0, len);
	i += len;
	ret.setParticipant(new String(pa, 0));
	//	System.out.println("Participant is "+(String)(ret.getParticipants().firstElement()));
	}
	break;

      case ID_BC:
	len = pack.buffer[i++];
	if (len > 0) {
	pa = new byte[len];
	System.arraycopy(pack.buffer, i, pa, 0, len);
	i += len;
	ret.setId(new String(pa, 0));
	//	System.out.println("Id is "+ret.getId());
	}
	break;
      }

    }
    Profile pf = ret.getProfile();
     if (pf != null)
       pf.setPayloadType(ret.getPayloadType());

    return ret;
  }



  public 
  TOMS_SessionMapper(AppManager am) {
    appMan = am;
    conTypeHandler = appMan.getConnectionTypeHandlerTable();
    protTypeHandler = appMan.getProtocolTypeHandlerTable();
    addrTypeHandler = appMan.getAddressTypeHandlerTable();
    payloadTypeHandler = appMan.getPayloadTypeHandlerTable();
  }

protected AppManager appMan;
protected TypeHandlerTable conTypeHandler;
protected TypeHandlerTable protTypeHandler;
protected TypeHandlerTable addrTypeHandler;
protected TypeHandlerTable payloadTypeHandler;

protected int SessionDescriptionLength = 1000;

}


  
