package mmstream.protocols.rtp;

import mmstream.address.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;


public class RTCP_Receiver_Thread extends Thread {

public RTCP_Receiver_Thread(ThreadGroup par, Connection con, RTP_SourceTable ssrctab) {
  super(par, "RTCP_Receiver_Thread");

  setDaemon(true);

  connection = con;
  sourceTable = ssrctab;


} // RTCP_Receiver_Thread()

public void run() {
  byte[] buffer = null;
  Chunk pck = null;
  RTP_RemoteSource ssrc = null, r_ssrc = null;
  int cc, len_s, len_t = 0, type, rec, offset;
  boolean initial = true, sdes_nec = false;
  Address localAddr = connection.getLocalAddress();
  
main_loop:     // loops over whole Chunks (RTCP compound packets) received from the ControlConnection
  while(true) {
    pck = null;
    buffer = null;
    ssrc = null;
    r_ssrc = null;

    this.yield();

    // read the common header part
    try {
      pck = connection.receive(1024); 
    }
    catch (Connection_Exception e)  {
      System.err.println("EXCEPTION: RTCP_Receiver_Thread.run(): pck=connection.receive(int):: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

    rec = pck.data_length;
    buffer = pck.buffer;
    offset = 0;
    len_s = 0;
    len_t = 0;
    // header validity check
    if (RTCProtocol.checkHeader(buffer, 0) == false) {
      // TODO Behandlung fehlerhafter Pakete
      System.err.println("ERROR: RTCP_Receiver_Thread.run(): RTCProtocol.checkHeader(byte[],int) failed");
      System.exit(1);
    }

    //    System.out.println("MESSAGE: RTCP_Receiver_Thread.run(): rec="+rec);

  packet_loop:  // loops over the RTCP packets the form a compound packet
    while (offset < rec) {

      type = ((int)buffer[offset + 1]&0xff) & RTCProtocol.RTCP_PT_MASK;
      len_s = 4*((((int)buffer[offset + 2]&0xff)<<010) | ((int)buffer[offset + 3]&0xff));
      //      System.out.println("MESSAGE: RTCP_Receiver_Thread.run(): type="+type+", len_s="+len_s);

      switch (type) {
	
      case RTCProtocol.RTCP_PT_SR:
      case RTCProtocol.RTCP_PT_RR:
	if (initial) {
	  initial = false;
	}	
	else {
	  if (sdes_nec == true) {
	    System.err.println("ERROR: RTCP compound packet missing SDES packet");
	  }
	  sourceTable.registerRTCPPacketSize(len_t);
	  //	  yield();
	}
	sdes_nec = true;
	len_t = len_s + RTCProtocol.RTCP_MINIMUM_HEADER_LENGTH;
	// search SourceTable for received SSRC
	try { 
	  r_ssrc = sourceTable.getRemoteSource(buffer, offset+RTCProtocol.RTCP_MINIMUM_HEADER_LENGTH);
	}
	catch (RTP_Exception e) { // collides wit local SSRC
	  if (localAddr.equals(pck.sourceTransportAddress)) {
	    sdes_nec = false;
	    continue main_loop; // ignore packet
	  }			
	  System.err.println("EXCEPTION: RTCP_Receiver_Thread.run(): RTP_SourceTable.getRemoteSource():: "+e.getMessage()+"localAddr "+localAddr.toString()+", remoteAddr "+pck.sourceTransportAddress.toString());
	  e.printStackTrace();
	  System.exit(1);
	}
	//	System.out.println("MESSAGE: received RTCP "+type+" packet from source "+r_ssrc.SSRCintvalue);
	// HACK
	if (r_ssrc == null) {
	    sdes_nec = false;
	    continue main_loop; // ignore packet
	}	  
	//OPT
	r_ssrc.registerRTCPPacket(pck, offset, pck.arrival);
	break;

      case  RTCProtocol.RTCP_PT_BYE:
      case  RTCProtocol.RTCP_PT_APP:
	if (!initial) {
	  try {
	    ssrc = sourceTable.getRemoteSource(buffer, offset+RTCProtocol.RTCP_MINIMUM_HEADER_LENGTH); //OPT
	  }
	  catch (RTP_Exception e) { // collides wit local SSRC
	    if (localAddr.equals(pck.sourceTransportAddress)) {
	      sdes_nec = false;
	      continue main_loop; // ignore packet
	    }
	    System.err.println("EXCEPTION: RTCP_Receiver_Thread.run(): RTP_SourceTable.getRemoteSource():: "+e.getMessage());
	    e.printStackTrace();
	    System.exit(1);
	  }
	  if (ssrc == null) {
	    sdes_nec = false;
	    continue main_loop; // ignore packet
	  }	  
	  if (r_ssrc == ssrc) {
	    len_t +=len_s + RTCProtocol.RTCP_MINIMUM_HEADER_LENGTH;
	    ssrc.registerRTCPPacket(pck, offset, pck.arrival);
	  }
	  else
	    System.err.println("ERROR: RTCP  packet missing RR/SR packet");
	}
	else
	  System.err.println("ERROR:RTCP packet missing RR/SR packet");
	
	break;

      case  RTCProtocol.RTCP_PT_SDES:
	if (!initial) {
	  len_t +=len_s + RTCProtocol.RTCP_MINIMUM_HEADER_LENGTH;
	  sourceTable.registerSDES(buffer, offset, ((int)buffer[offset]&0xff) & RTCProtocol.RTCP_BC_MASK);      
	  sdes_nec = false;
	}
	else
	  System.err.println("ERROR: RTCP packet missing RR/SR packet");
	
	break;
      }
      offset += len_s + 4;
	
    }

  } // while(true)

}



private Connection connection;
private RTP_SourceTable sourceTable;

} // class RTCP_Receiver_Thread




