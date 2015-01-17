package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.address.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.connection.*;
import mmstream.config.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class ProtoSession extends Object {

  public static final int CREATE	= 0;
  public static final int JOIN	        = 1;
  public static final int CONTINUE	= 2;
  public static final int LEAVE		= 3;
  public static final int MAX_ACTION	= 3;

  protected static String actionString(int s) {
    switch(s) {
    case JOIN   : return new String("Join");
    case CONTINUE   : return new String("Continue");
    case LEAVE   : return new String("Leave");
    }	
    return null;
  }

  public ProtoSession() {
    dataAddress = null;
    controlAddress = null;
    protocolType = null;
    addressType = null;
    connectionType = null;
    payloadType = null;
    output = new Println_Output();
    profile = null;
    action = ProtoSession.JOIN;
    instantiable = false;
    len = 0;
  }

  public String getId() { return id;}
  public void setId(String i) { id = i;}

  public void 
  setInitiator(String val) { initiator = val; }
  
  public void 
  setParticipant(String val) { partners = new Vector(); partners.addElement(val); }
  
  public void 
  addParticipant(String val) { partners.addElement(val); }
  
  public Vector 
  getParticipants() { return partners; }
  
  public String 
  getInitiator() { return initiator; }


  public PayloadType getPayloadType() { return payloadType; }
  public void setPayloadType(PayloadType v) { payloadType = v; }

  public Output getOutput() { return output; }
  public void setOutput(Output v) { output = v; }

  public boolean getInstantiable() { return instantiable; }
  public void setInstantiable(boolean v) { instantiable = v; }

  public String getAction() { return ProtoSession.actionString(action);}
  public int getActionCode() { return (int)action;}

  protected void setAction(int s) throws  Session_Exception { 
    if (s >= 0 || s <= ProtoSession.MAX_ACTION)
      action = s;
    else
      throw new Session_Exception("ProtoSession.setAction(int): Unvalid action "+s);
  }

  protected void setAction(String s) throws  Session_Exception { 
    if (s == null)
      throw new Session_Exception("ProtoSession.setAction(String): empty action ");
    for (int i = 0; i <= ProtoSession.MAX_ACTION; i++) {
      if (s.equals(ProtoSession.actionString(i))) {
	action = i;
	return;
      }
    }
    throw new Session_Exception("ProtoSession.setAction(String): Unvalid action "+s);
  }
  
  public void setConnectionType(Type t) { connectionType = t; }
  public Type getConnectionType() { return connectionType; }

  public void setProtocolType(Type t) { protocolType = t; }
  public Type getProtocolType() { return protocolType; }

  public void setAddressType(Type t) { addressType = t; }
  public Type getAddressType() { return addressType; }

  public void setDataAddress(String t) { dataAddress = t; }
  public String getDataAddress() { return dataAddress; }

  public void setDataMaxSize(int ms) { len = ms; }
  public int getDataMaxSize() { return len; }

  public void setControlAddress(String t) { controlAddress = t; }
  public String getControlAddress() { return controlAddress; }

  public Profile getProfile() { return profile; }
  public void setProfile(Profile v) { profile = v; }

  protected boolean instantiable;
  protected int action;
  protected String id;
  protected PayloadType payloadType;
  protected Profile profile;
  protected Output output;
  protected String dataAddress;
  protected String controlAddress;
  protected int len;
  protected Type connectionType;
  protected Type protocolType;
  protected Type addressType;
  protected String initiator;
  protected Vector partners;
}
