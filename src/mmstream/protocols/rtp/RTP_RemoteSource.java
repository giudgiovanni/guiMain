package mmstream.protocols.rtp;

import mmstream.stream.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.protocols.rtp.*;

import java.util.*;

public class RTP_RemoteSource extends RemoteSource {

  private 
  RTP_RemoteSource(RTP_SourceTable st, boolean v, RTP_CoDec c) {
    super(c);
    sourceTable = st;
    inSeqNumReg = new RTP_SeqNum_Registration(this);
    transitPrior = 0;

    lastSR_NTP_Timestamp = 0; 
    lastSR_RTP_Timestamp = 0; 
    beforeLastSRarr = 0;
    lastSRarr = -1;
    lastOctetsSent = 0;
    lastOctetsReceived = 0;
    lastChunksSent = 0;
    lastChunksReceived = 0;
    lastSNRec = 0;
    valid = v;
    emptyIntervalls = new Counter(0);
    RTCPPacketCnt = new Counter(0);
    clock = new NTP_Master_Clock();
    payloadLength = this.profile.getPayloadType().getMaxSize();
    compoundOffset = 0;
    compoundSeq = -1;
    startedChunk = false;
    clock.set(profile.getClockRate(), 0L, new NTPTimeStamp(0,0));
  }


  public RTP_RemoteSource(RTP_SourceTable st, int value, boolean v, RTP_CoDec c) {
    this(st,v,c);
    SSRCintvalue = value;
    id = RTProtocol.int2String(value);
    SSRCbytevalue = int2bytes(value);
    //    this.output.message("MESSAGE: created RTP_RemoteSource with intval="+value+". byteval="+SSRCbytevalue[0]+"."+SSRCbytevalue[1]+"."+SSRCbytevalue[2]+"."+SSRCbytevalue[3]);
  }


  protected RTP_RemoteSource(RTP_SourceTable st, String value, boolean v, RTP_CoDec c) {
    this(st,v,c);
    id = value;
    try {
      SSRCintvalue = Integer.parseInt(value);
    } catch (NumberFormatException ne) {;}
    //    this.output.message("MESSAGE: created RTP_RemoteSource with intval="+value+". byteval="+SSRCbytevalue[0]+"."+SSRCbytevalue[1]+"."+SSRCbytevalue[2]+"."+SSRCbytevalue[3]);
    SSRCbytevalue = RTProtocol.int2bytes(SSRCintvalue);
  }


  public 
  RTP_RemoteSource(RTP_SourceTable st, byte[] b, int offset, boolean v, RTP_CoDec c) {
    this(st,b, offset, bytes2int(b, offset), v,c);
  }

  // No checks if b and val correspond - only for those who know what they are doing!
  protected 
  RTP_RemoteSource(RTP_SourceTable st, byte[] b, int offset, int val, boolean v, RTP_CoDec c) {
    this(st,v,c);
    SSRCbytevalue = new byte[RTProtocol.RTP_SRC_LENGTH];
    System.arraycopy(SSRCbytevalue, 0, b, offset, RTProtocol.RTP_SRC_LENGTH);
    SSRCintvalue = val;
    id = RTProtocol.int2String(val);

  }

public void finish(String reason) {
  this.setReasonForFinishing(reason);
  this.setFinished(true);
  this.setUsed(false);
  this.output.notifyStateChange();
}

public void
setProfile(Profile pro) { 
  super.setProfile(pro); 
}

  protected static int 
  bytes2int(byte[] b, int offset) {
    int val = 0;
    for(int i = 0; i < RTProtocol.RTP_SRC_LENGTH; i++) {
      val  |= ((((int)b[i+offset]) & 0xff) << ((RTProtocol.RTP_SRC_LENGTH - 1 - i)<<3));
    }
    return val;
  }
  
  protected static byte[] 
  int2bytes(int val) {
    byte[] b = new byte[RTProtocol.RTP_SRC_LENGTH];

    for(int i = 0; i < RTProtocol.RTP_SRC_LENGTH; i++) {
      b[i]= (byte)((val & (0xff << ((RTProtocol.RTP_SRC_LENGTH - 1 - i)<<3))) >>> ((RTProtocol.RTP_SRC_LENGTH - 1 - i)<<3));
    }
    return b; 
  }

  //  protected synchronized void 
  // no synchronized necessary, only called by our RTP_Receiver_Thread
  protected void 
  registerRTPPacket(Chunk p) {
    int i = p.header_offset;
    int newSeq = (((int)p.buffer[i+2] & 0xff)<<010) | (((int)p.buffer[i+3] & 0xff));
    if (this.inSeqNumReg.register(newSeq) == true) {
      if(this.used == true) {
	//     p.header_offset = offset;
	//     p.data_length = p.data_length-dataoffset;
	//     p.data_offset = dataoffset+offset;
	//      p.timeStamp = ((long)p.buffer[i+4] & 0xffL)<<030L | ((long)p.buffer[i+5] & 0xffL)<<020L | ((long)p.buffer[i+6] | 0xffL)<<010L | (long)p.buffer[i+7] & 0xffL;
	p.timeStamp= ((((long)p.buffer[i+4])&0xffL)<<030L) | ((((long)p.buffer[i+5])&0xffL)<<020L) | ((((long)p.buffer[i+6])&0xffL)<<010L) | (((long)p.buffer[i+7])&0xffL);
	if ( (((int)p.buffer[i+1]) & RTProtocol.RTP_MARKER_MASK) == RTProtocol.RTP_MARKER_YES) {
	  if (startedChunk) {
	    if (newSeq != (compoundSeq + 1)%RTProtocol.RTP_SEQ_MOD ) {
	      startedChunk = false;
	      return;
	    }
	    System.arraycopy(p.buffer, p.data_offset, compoundBuffer, compoundOffset, p.data_length);
	    compoundOffset += p.data_length;
	    octetsReceived += p.data_length;
	    p.header_offset = 0;
	    p.header_length = 0;
	    p.data_offset = 0;
	    p.data_length = compoundOffset;
	    p.buffer = compoundBuffer;
	  }
	  try {
	    this.exportChunk(p);
	  }
	  catch(Stream_Exception ae) {
	    System.err.println("EXCEPTION: RTP_RemoteSource.registerRTPPacket: this.exportChunk():: "+ae.getMessage());
	    ae.printStackTrace();
	  }
	  this.chunksReceived++;
	  startedChunk = false;
	}
	else {
	  if (!startedChunk) {
	    compoundBuffer = new byte[payloadLength];
	    startedChunk = true;
	    compoundOffset = 0;
	  }
	  else 
	    if (newSeq != (compoundSeq + 1)%RTProtocol.RTP_SEQ_MOD ) 
	      return;
	  try {
	    System.arraycopy(p.buffer, p.data_offset, compoundBuffer, compoundOffset, p.data_length);
	  }
	  catch(ArrayIndexOutOfBoundsException ae) {
	    System.err.println("EXCEPTION: RTP_RemoteSource.registerRTPPacket: System.arraycopy(p.buffer,"+p.data_offset+",compoundBuffer,"+compoundOffset+","+p.data_length+"):: "+ae.getMessage());
	    ae.printStackTrace();
	  }
	  compoundSeq = newSeq;
	  compoundOffset += p.data_length;
	  octetsReceived += p.data_length;
	}

	//      chunksReceived++;
    
	long transit = ((long)((((double)p.arrival)/1000D)*this.clock.getRate())) - p.timeStamp;
	long d = transit - transitPrior;
	if (transitPrior != 0L) {
	  if (d < 0) d = -d;
	  jitterEstimation +=  d - ((jitterEstimation + 8L) >>> 4);
	}
	transitPrior = transit;
	//    jitterEstimation += (1./16.) * ((float) d - jitterEstimation);

	if (this.dataAddr == null) {
	  this.dataAddr = p.sourceTransportAddress;
	  this.output.notifyAddress(dataAddr, null);
	}
	this.output.notifyReceptionStatistics(this.getReceptionStats());
      }
    }
    else if (this.used == true) {
      startedChunk = false;
      compoundOffset = 0;
      compoundBuffer = null;
    }
  }


  protected synchronized void 
  registerRTCPPacket(Chunk p, int offset, long arr) {
    int count = ((int)p.buffer[offset] & 0xff) & RTCProtocol.RTCP_BC_MASK;
    int len, i;
    long ntpDummy, rtpDummy;
    RTCPPacketCnt.incr();

    switch (((int)p.buffer[offset+1] & 0xff) & RTCProtocol.RTCP_PT_MASK) {

    case RTCProtocol.RTCP_PT_SR:
      ntpDummy = (((long)p.buffer[offset+8] & 0xff) << 070) | (((long)p.buffer[offset+9] & 0xff) << 060) | (((long)p.buffer[offset+10] & 0xff) << 050) | (((long)p.buffer[offset+11] & 0xff) << 040) |
	(((long)p.buffer[offset+12] & 0xff) << 030) | (((long)p.buffer[offset+13] & 0xff) << 020) | (((long)p.buffer[offset+14] & 0xff) << 010) | (((long)p.buffer[offset+15] & 0xff));
    
      rtpDummy = (((long)p.buffer[offset+16] & 0xff) << 030) | (((long)p.buffer[offset+17] & 0xff) << 020) | (((long)p.buffer[offset+18] & 0xff) << 010) | (((long)p.buffer[offset+19] & 0xff));

      if (this.clock.adjust(((ntpDummy & (0xffffffffL << 040))>>>040) & 0xffffffff, ntpDummy & 0xffffffffL, rtpDummy) == true) {
 
	lastSR_NTP_Timestamp = ntpDummy;
	lastSR_RTP_Timestamp = rtpDummy;
     
	beforeLastSRarr = lastSRarr;
	lastSRarr = arr;
      
	lastChunksSent = chunksSent;
	chunksSent = ((int)p.buffer[offset+20] & 0xff) << 030 | ((int)p.buffer[offset+21] & 0xff) << 020 | ((int)p.buffer[offset+22] & 0xff) << 010 | ((int)p.buffer[offset+23] & 0xff); 
	lastOctetsSent = octetsSent;
	octetsSent  = ((int)p.buffer[offset+24] & 0xff) << 030 | ((int)p.buffer[offset+25] & 0xff) << 020 | ((int)p.buffer[offset+26] & 0xff) << 010 | ((int)p.buffer[offset+27] & 0xff); 

	this.output.notifySenderStatistics(this.getSenderStats());
      
	this.register_RTCP_RR_blocks(count, p.buffer, offset+28);
      }
      break;

    case RTCProtocol.RTCP_PT_RR:
      this.register_RTCP_RR_blocks(count, p.buffer, offset+8);
      break;

    case RTCProtocol.RTCP_PT_BYE:
      finished = true;
      this.setValid(false);
      // TODO handle the CSRC entries
      len = count << 2;
      reasonForFinishing = new String(p.buffer, 0, offset+len + 5, (int)p.buffer[offset+len+4] & 0xff);
      output.notifyStateChange();
      output.notifySourceDescription();
      break;

    case RTCProtocol.RTCP_PT_APP:
      break; // TODO

    default:
      output.error("Unexpected RTCProtocol Type " + (((int)p.buffer[offset+1] & 0xff) & RTCProtocol.RTCP_PT_MASK));
      System.exit(1);
      break;
    }

    if (controlAddr == null) {
      controlAddr = p.sourceTransportAddress;
      this.output.notifyAddress(null, controlAddr);
    }
  }

  private synchronized void
  register_RTCP_RR_blocks(int num, byte[] p, int o) {
    int offset = o;
    String ssrc;
    RTP_LocalSource localsrc;
    RTP_ReceptionStatistics srcstat;

    for(int i = 0; i < num; i++) {
      ssrc = RTProtocol.int2String(RTProtocol.bytes2int(p, offset));
      localsrc = (RTP_LocalSource)sourceTable.queryLocalSource(ssrc);
      if(localsrc != null) {
	offset += RTProtocol.RTP_SRC_LENGTH;
	srcstat = new RTP_ReceptionStatistics();
	srcstat.id = this.getId();
	srcstat.cname = this.getCname();
	srcstat.state = this.getState();

	srcstat.chunksReceived = 0;
	srcstat.octetsReceived = 0;

	srcstat.fractionLost = ((int)p[offset+RTProtocol.RTP_SRC_LENGTH]) & 0xff;
	offset++;
	srcstat.totalLost = ((int)p[offset] & 0xff) << 020 | ((int)p[offset+1] & 0xff) << 010 | ((int)p[offset+2] & 0xff);
	offset += 3;
	srcstat.extHiSeq = ((int)p[offset] & 0xff) << 030 | ((int)p[offset+1] & 0xff) << 020 | ((int)p[offset+2] & 0xff) << 010 | ((int)p[offset+3] & 0xff);
	offset += 4;
	srcstat.jitter = ((long)p[offset] & 0xff) << 030 | ((long)p[offset+1] & 0xff) << 020 | ((long)p[offset+2] & 0xff) << 010 | ((long)p[offset+3] & 0xff);
	offset += 4;
	srcstat.lastSR = ((long)p[offset] & 0xff) << 030 | ((long)p[offset+1] & 0xff) << 020 | ((long)p[offset+2] & 0xff) << 010 | ((long)p[offset+3] & 0xff);
	offset += 4;
	srcstat.delayLastSR = ((int)p[offset] & 0xff) << 030 | ((int)p[offset+1] & 0xff) << 020 | ((int)p[offset+2] & 0xff) << 010 | ((int)p[offset+3] & 0xff);
	offset += 4;
      
	if (localsrc.getUsed() == false) {
	  localsrc.setUsed(true);
	  localsrc.output.notifyStateChange();
	}
	localsrc.output.notifyReceptionStatistics(srcstat);
      }
      else
	offset += RTCProtocol.RTCP_RR_BLOCK_LENGTH;
    }
  }

  public synchronized SenderStatistics 
  getSenderStats() {
    RTP_SenderStatistics stat = new RTP_SenderStatistics();
    super.fillInSenderStats(stat);
    // HACK: zeiten falsch
    stat.bandwidth = (1000.0d*(octetsSent-lastOctetsSent)) / ((double)(lastSRarr - beforeLastSRarr));
    stat.chunkRate = (1000.0d*(chunksSent-lastChunksSent)) / ((double)(lastSRarr - beforeLastSRarr));
    stat.lastSR = (long)((lastSR_NTP_Timestamp & 0xffffffff0000L) >> 020);
    //  stat.lastSR = lastSR_NTP_Timestamp;
    return stat;
  }


  public synchronized ReceptionStatistics 
  getReceptionStats() {
    RTP_ReceptionStatistics stat = new RTP_ReceptionStatistics();
    super.fillInReceptionStats(stat);
    
    stat.jitter = stat.jitter >>> 4;

    stat.fractionLost = inSeqNumReg.fractionLost();
    stat.totalLost = inSeqNumReg.totalLost();
    long now = System.currentTimeMillis();
    stat.bandwidth = (1000.0d * (octetsReceived-lastOctetsReceived)) / ((double)(now - lastRRsent));
    stat.chunkRate = (1000.0d * (chunksReceived-lastChunksReceived)) / ((double)(now - lastRRsent));
  
    stat.extHiSeq =  inSeqNumReg.highestExt();
    stat.lastSR = (long)((lastSR_NTP_Timestamp & 0xffffffff0000L) >> 020);
    //  stat.lastSR = lastSR_NTP_Timestamp;
    stat.delayLastSR = (int)((now - lastSRarr) * 8192) / 125;
    stat.delayLastSR = (int)((now - lastSRarr) << 13) / 125;

    return stat;
  }

  protected void
  publish() {
    this.setValid(true);
    ((RTP_SourceTable)sourceTable).newSourceQueue.insert(this);
    output.notifyStateChange();

  }
			       

  private RTP_SeqNum_Registration inSeqNumReg;
  protected RTP_SourceTable sourceTable;

  private long transitPrior;

  protected long lastSR_NTP_Timestamp; 
  protected long lastSR_RTP_Timestamp; 
  protected long beforeLastSRarr; 
  protected long lastSRarr; 
  protected long lastRRsent; 
  protected long lastSNRec;
  protected long lastOctetsSent;
  protected long lastOctetsReceived;
  protected long lastChunksSent;
  protected long lastChunksReceived;

  protected Counter emptyIntervalls;       
  protected Counter RTCPPacketCnt;

  protected int SSRCintvalue;
  protected byte[] SSRCbytevalue;

  protected boolean startedChunk = false;
  protected byte[] compoundBuffer;
  protected int compoundSeq;
  protected int compoundOffset;
  protected int payloadLength;
} // class RTP_RemoteSource



