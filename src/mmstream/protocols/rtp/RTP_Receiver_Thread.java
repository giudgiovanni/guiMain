package mmstream.protocols.rtp;

import mmstream.address.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.config.*;
import mmstream.stream.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;


public class RTP_Receiver_Thread extends Thread {

public RTP_Receiver_Thread(ThreadGroup par, Connection con, RTP_SourceTable ssrctab, RTP_Profile prof) {
  super(par, "RTP_Receiver_Thread");

  setDaemon(true);

  connection = con;
  sourceTable = ssrctab;
  profile = prof;

} // RTP_Receiver_Thread()

public void run() {
  byte[] packet = null;
  Chunk pck = null;
  RTP_RemoteSource ssrc = null;
  int cc = 0;
  int lowerHeaderSize = connection.getHeaderSize();
  int pack_size = (int)(RTProtocol.RTP_MAXIMUM_HEADER_LENGTH + profile.getDataLength()+lowerHeaderSize);
  int offset = 0;
  int extension = 0;
  Address localAddr = connection.getLocalAddress();

main_loop:
  while(true) {
    packet = null;
    ssrc = null;
    yield();

    // TODO check der Laenge von gelesen Bloecken
    
    // read the common header part
    try {
      pck = connection.receive(pack_size);
    }
    catch (Connection_Exception e)  {
      System.out.println("EXCEPTION: RTP_Receiver_Thread.run(): header=connection.receive(int): "+e.getMessage());
      e.printStackTrace();
      //      System.exit(1);
      continue main_loop;
    }

    // 'performance booster' ;-)
    packet = pck.buffer;
    offset = pck.data_offset;

    // header validity check
    if (RTProtocol.checkHeader(packet, offset, profile) == false) {
      // TODO Behandlung fehlerhafter Pakete
      System.err.println("ERROR: RTP_Receiver_Thread.run():RTProtocol.checkHeader() failed");
      System.exit(1);
    }

    // search SourceTable for received SSRC
    try { 
      ssrc = sourceTable.getRemoteSource(packet, offset + 8);
    }
    catch (RTP_Exception e) { // collides wit local SSRC
      if (localAddr.equals(pck.sourceTransportAddress)) {
	continue; // ignore packet
      }
      System.err.println("EXCEPTION: RTP_Receiver_Thread.run(): SourceTable.getRemoteSource(byte[],int): "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    // HACK
    if (ssrc == null) 
      continue main_loop;

    //    System.out.println("MESSAGE: received RTP packet from source "+ssrc.SSRCintvalue);
    // get potential CSRCs
    cc = ((int)packet[offset]) & RTProtocol.RTP_CC_MASK;

    // potential header extensions
    extension = ((int)packet[offset]) & RTProtocol.RTP_EXTENSION_MASK;
    if (extension != profile.extension) {
      System.err.println("ERROR: RTP_Receiver_Thread: Packet Header Extension "+extension+" != Profile.extension "+profile.extension);
    }
    
    pck.header_offset = pck.data_offset;
    pck.header_length = RTProtocol.RTP_MINIMUM_HEADER_LENGTH + cc * RTProtocol.RTP_SRC_LENGTH;
    // add the length of potential extension
    if (extension != RTProtocol.RTP_EXTENSION_NO) {
      offset = pck.header_offset + pck.header_length;
      pck.header_length += (((((int)packet[offset+2] & 0xff)<<010) | (((int)packet[offset+3] & 0xff))) +1) << 2;
    }

    pck.data_offset = pck.header_length+pck.header_offset;
    pck.data_length = pck.data_length - pck.header_length;

    ssrc.registerRTPPacket(pck);
  }
}



private Connection connection;
private RTP_SourceTable sourceTable;
private RTP_Profile profile;

} // class RTP_Receiver_Thread




