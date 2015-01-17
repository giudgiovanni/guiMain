package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.config.*;
import mmstream.util.*;
import mmstream.stream.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;

import java.net.*;
import java.util.*;

public class RTP_SourceTable extends SourceTable {

public 
RTP_SourceTable(RTP_CoDec cod) {
  super();

  newSourceQueue = new Object_Queue();

  codec = cod;

  avgRTCPPacketSize = 128;
}

public synchronized RTP_LocalSource 
createLocalSource() throws RTP_Exception {
  int newid;
  String tmp;
  do {
    newid = randomizer.nextInt();
    tmp = RTProtocol.int2String(newid);
  }
  while(this.containsKeyRemoteSource(tmp) != false || this.containsKeyLocalSource(tmp) != false);
  RTP_LocalSource localSource = new RTP_LocalSource(this, newid, true, codec);
  try {
    localSource.setCname(new String(System.getProperty("user.name")+"@"+(InetAddress.getLocalHost()).getHostName()));
  }
  catch(UnknownHostException e) {
    ;
  }
  localSource.setDataAddress((codec.getDataConnection()).getLocalAddress());
  localSource.setControlAddress((codec.getControlConnection()).getLocalAddress());
  localSource.setDataConnection(codec.getDataConnection());
  localSource.setControlConnection(codec.getControlConnection());
  this.registerLocalSource(localSource);
  return localSource;
}
  
  
protected synchronized RTP_RemoteSource 
getRemoteSource(byte[] inssrc, int offset) throws RTP_Exception {
  return this.getRemoteSource(RTProtocol.bytes2int(inssrc, offset));
}

protected synchronized RTP_RemoteSource 
getRemoteSource(int inssrc_int) throws RTP_Exception {
  if (inssrc_int == 0)
    return null;
  return this.getRemoteSource(RTProtocol.int2String(inssrc_int));
}

protected synchronized RTP_RemoteSource 
getRemoteSource(String inssrc) throws RTP_Exception {
  if (localTable.get(inssrc) != null)
    throw new RTP_Exception("getSource: incoming SSRC collides with local SSRC");
  
  RTP_RemoteSource ret = (RTP_RemoteSource)remoteTable.get(inssrc);
  
  if (ret == null) {
    ret = new RTP_RemoteSource(this, inssrc, false, codec);
    //    remoteTable.put(ret.getId(), ret);
    
    this.registerRemoteSource(ret);
    try {
      codec.registerRemoteSource(ret);
    }
    catch (CoDec_Exception se) {
      throw new RTP_Exception("RTP_RemoteSource.getRemoteSource(String): CoDec.registerRemoteSource: "+se.getMessage());
    }
  }
  return ret;
}


protected synchronized void 
registerSDES(byte[] m, int offset, int sc) {
  int i = offset+4, n, len, type;
  RTP_RemoteSource src = null;
  String val;
  for (n = 0; n < sc; n++) {
    try {
      src = getRemoteSource(m, i); i+=4;
    }
    catch(RTP_Exception re) {
      // TODO collision!
    }
    
    if (src == null)
      return;

    while(m[i] != 0) {
      type = (int)m[i++] & 0xff;
      len = (int)m[i++] & 0xff;
      val = new String(m, 0, i, len);

      switch(type) {
      case RTCProtocol.RTCP_SDES_CNAME:
	src.setCname(val);
	break;
      case RTCProtocol.RTCP_SDES_NAME:
	src.setName(val);
	break;
      case RTCProtocol.RTCP_SDES_EMAIL:
	src.setEmail(val);
	break;
      case RTCProtocol.RTCP_SDES_PHONE:
	src.setPhone(val);
	break;
      case RTCProtocol.RTCP_SDES_LOC:
	src.setLoc(val);
	break;
      case RTCProtocol.RTCP_SDES_TOOL:
	src.setTool(val);
	break;
      case RTCProtocol.RTCP_SDES_NOTE:
	src.setNote(val);
	break;
      case RTCProtocol.RTCP_SDES_PRIV: 
	break; //TODO
      default: break;
      }
      i += len;
    }
    i += (4 - (i%4))%4;

    src.output.notifySourceDescription();    
  }

}



protected boolean 
compareSSRCbytes(byte[] inssrc1, int offset1, byte[] inssrc2, int offset2) {

  int i;
  
  
  for (i = 0; i < RTProtocol.RTP_SRC_LENGTH; i++) {
    if(inssrc1[offset1 +i] != inssrc2[offset2 +i]) {
      return false;
    }
  } 
  
  return true;
}

protected synchronized float
getAvgRTCPPacketSize() {
  return avgRTCPPacketSize;
}

protected synchronized void
registerRTCPPacketSize(int size) {
  avgRTCPPacketSize += RTP_Profile.RTCP_SIZE_GAIN * (float)(size - avgRTCPPacketSize);
}

private RTP_CoDec codec;
private float  avgRTCPPacketSize;
protected Object_Queue newSourceQueue;
}



