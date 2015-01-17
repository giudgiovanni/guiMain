package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.config.*;
import mmstream.protocols.rtp.*;

import java.util.*;

public class RTP_LocalSource extends LocalSource {

  private 
  RTP_LocalSource(RTP_SourceTable st, boolean v, RTP_CoDec c) {
    super(c);
    sourceTable = st;
    valid = v;
    outSeqNumGen = new SeqNum_Generator(RTProtocol.RTP_SEQ_MOD, (RTProtocol.RTP_SEQ_MOD-1)&st.randomizer.nextInt()); 

    lastSRTimestamp = 0; 
    lastSRsent = 0; 
    lastOctets = 0;
    lastChunks = 0;
    clock = new NTP_Master_Clock();
    long tmp = sourceTable.randomizer.nextLong() & 0xffffL;
    //    clock.set(profile.getClockRate(), tmp, NTPTimeStamp.fromMillis(System.currentTimeMillis())).ticks;
    clock.set(profile.getClockRate(), tmp, NTPTimeStamp.fromMillis(System.currentTimeMillis()));
  }


  public RTP_LocalSource(RTP_SourceTable st, int value, boolean v, RTP_CoDec c) {
    this(st,v,c);
    SSRCintvalue = value;
    id = RTProtocol.int2String(value);
    SSRCbytevalue = RTProtocol.int2bytes(value);
    //    output.message("MESSAGE: created RTP_LocalSource with intval="+value+". byteval="+SSRCbytevalue[0]+"."+SSRCbytevalue[1]+"."+SSRCbytevalue[2]+"."+SSRCbytevalue[3]);
  }


  public 
  RTP_LocalSource(RTP_SourceTable st, byte[] b, int offset, boolean v, RTP_CoDec c) {
    this(st,b, offset, RTProtocol.bytes2int(b, offset), v, c);
  }

  // No checks if b and val correspond - only for those who know what they are doing!
  protected 
  RTP_LocalSource(RTP_SourceTable st, byte[] b, int offset, int val, boolean v, RTP_CoDec c) {
    this(st,v,c);
    SSRCbytevalue = new byte[RTProtocol.RTP_SRC_LENGTH];
    System.arraycopy(SSRCbytevalue, 0, b, offset, RTProtocol.RTP_SRC_LENGTH);
    SSRCintvalue = val;
    id = RTProtocol.int2String(val);
  }

public void
setProfile(Profile pro) { 
  super.setProfile(pro); 
//   clock.set(pro.getClockRate(), sourceTable.randomizer.nextLong(), NTPTimeStamp.fromMillis(System.currentTimeMillis()).ticks);
}


  public SenderStatistics 
  getSenderStats() {
    RTP_SenderStatistics stat = new RTP_SenderStatistics();
    super.fillInSenderStats(stat);

    stat.bandwidth = (1000.0d * (stat.octetsSent-this.lastOctets)) / ((double)(System.currentTimeMillis() - lastSRsent));
    stat.chunkRate = (1000.0d * (stat.chunksSent-this.lastChunks)) / ((double)(System.currentTimeMillis() - lastSRsent));

    stat.lastSR = (lastSRTimestamp & 0xffffffff0000L) >> 020;
    //  stat.lastSR = lastSRTimestamp;
    stat.extHiSeq = outSeqNumGen.last();
    return stat;
  }

  public void finish(String reason) {
    if (this.finished == false) {
      byte[] buffer = new byte[1024];
      int offset = RTCProtocol.generate_RR_Header(buffer, 0, this.SSRCbytevalue, 0);
    
      int temp = offset;
      String s;
      offset += RTCProtocol.RTCP_SDES_HEADER_LENGTH;
      offset += RTCProtocol.generate_SDES_Start(buffer, offset, this.SSRCbytevalue);
    
      s = this.getCname();
      if (s != null) {
	offset += RTCProtocol.generate_SDES_Item_CNAME(buffer, offset, s);
      }
      offset += RTCProtocol.generate_SDES_End(buffer, offset, offset - temp);
      RTCProtocol.generate_SDES_Header(buffer, temp, 1, offset - temp);
    
      offset += RTCProtocol.generate_BYE_Packet(buffer, offset, this.SSRCbytevalue, 0, null, reason);
    
      try {
	controlConnection.send(buffer, offset);
      }
      catch (Connection_Exception ce) {
	this.output.error("RTP_CoDec.finish(): controlConnection.send(byte[],int) failed:: "+ce.getMessage());
      }
      this.setReasonForFinishing(reason);
      this.setFinished(true);
      this.setUsed(false);
      this.output.notifyStateChange();
    }
  }

  protected RTP_SourceTable sourceTable;
  protected int SSRCintvalue;
  protected byte[] SSRCbytevalue;
  public SeqNum_Generator  outSeqNumGen;
  protected long lastSRTimestamp; 
  protected long lastSRsent; 
  protected long lastOctets;
  protected long lastChunks;
} // class RTP_LocalSource



