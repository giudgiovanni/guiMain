package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.config.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;

import java.util.*;

public class RTCP_Sender_Thread extends Thread {


public RTCP_Sender_Thread(ThreadGroup par, Connection con, RTP_SourceTable sourcetab, RTP_LocalSource dos, RTP_Profile prof) {
  super(par, "RTCP_Sender_Thread");

  setDaemon(true);
  
  profile = prof;
  connection = con;
  sourceTable = sourcetab;
  defaultLocalSource = dos;

  firstRTCPPacket = true;
} // RTCP_Sender_Thread()


public void run() {

  int len, temp, rbc, seq, lowerHeaderSize = connection.getHeaderSize();
  long octs ,pcks;
  Enumeration localSources;
  Enumeration remoteSources;
  String s;

  RTP_RemoteSource remoteEntry;
  RTP_ReceptionStatistics stat;
  RTP_LocalSource actSource;

  byte[] buffer = new byte[1024];
  boolean sendSomething = false;

global_loop:
  while (true) {

    rbc = 0;
    len = 0;

    localSources = sourceTable.localElements();
  local_source_loop:
    while (localSources.hasMoreElements()) {
      sendSomething = false;
      actSource = (RTP_LocalSource)(localSources.nextElement());
      synchronized(actSource) {
	len = 0;
	octs =actSource.octetsSent.query();
	
	if (octs != actSource.lastOctets) {
	  pcks =actSource.chunksSent.query();
	  len = RTCProtocol.RTCP_SR_HEADER_LENGTH;
	  sendSomething = true;
	  //	  System.out.println("RTCP_Sender: generating SR from Source "+actSource.getId());
	}
	else {
	  if (actSource == defaultLocalSource) {
	    pcks = 0;
	    len = RTCProtocol.RTCP_RR_HEADER_LENGTH;
	    //	    System.out.println("RTCP_Sender: generating RR from Source "+actSource.getId());
	  }
	  else {
	    //	    System.out.println("RTCP_Sender: sending nothing for Source "+actSource.getId());
	    continue local_source_loop;
	  }
	}
	rbc = 0;
	if (actSource == defaultLocalSource) { // RR blocks are only sent for defaultLocalSource
	  for (remoteSources = sourceTable.remoteElements() ; remoteSources.hasMoreElements();) {
	    remoteEntry = ((RTP_RemoteSource)(remoteSources.nextElement()));
	    if (remoteEntry.getValid() == true && remoteEntry.getFinished() ==   false) {
	      stat = (RTP_ReceptionStatistics)remoteEntry.getReceptionStats();
	      if (stat.extHiSeq != remoteEntry.lastSNRec) {
		rbc++;
		len += RTCProtocol.generate_RR_Block(buffer, len, 
						     remoteEntry.SSRCbytevalue,
						     stat.fractionLost, stat.totalLost,
						     stat.extHiSeq,
						     stat.jitter,
						     stat.lastSR,
						     stat.delayLastSR);
		remoteEntry.emptyIntervalls.reset(0);
		remoteEntry.lastSNRec = stat.extHiSeq;
		remoteEntry.lastOctetsReceived = stat.octetsReceived;
		remoteEntry.lastChunksReceived = stat.chunksReceived;
		remoteEntry.lastRRsent = System.currentTimeMillis();
	      }
	      else { // no RTP packet arrived
		if (remoteEntry.RTCPPacketCnt.query_reset(0) > 0) // a RTCP packet arrived
		  remoteEntry.emptyIntervalls.reset(0);
		else {
		  remoteEntry.emptyIntervalls.incr();
		  if (remoteEntry.emptyIntervalls.query() >= RTP_Profile.RTCP_MAX_EMPTY_INTERVALS) {
		    remoteEntry.setValid(false);
		    remoteEntry.output.notifyStateChange();
		  }
		}
	      }
	    } // if valid && !finished
	    
	  } // for(remoteTable.elements)
	}
	
	if (octs != actSource.lastOctets) {
	  long ts = 0;
	  NTPTimeStamp ntpstamp = NTPTimeStamp.fromMillis(System.currentTimeMillis());
	  ts = ((NTP_Master_Clock)(actSource.getMasterClock())).NTP2Stamp(ntpstamp);
	  //       long now = actSource.lastSRsent - timeshift;
	  //       long secs = now / 1000;
	  //       long frac = (now - (secs * 1000))*fract_fact;
	  //       actSource.lastSRTimestamp = frac + secs*0xffffffffL;
	  //       actSource.lastSRTimestamp = ntpstamp.ticks;
	  actSource.lastSRsent = ntpstamp.millis;
	  actSource.lastSRTimestamp = (ntpstamp.secs << 040) | ntpstamp.fract;
	  //	  System.out.println("RTCP_Sender: putting in SR NTP.secs "+ntpstamp.secs+",NTP.fract "+ntpstamp.fract+",RTP stamp "+ts);
	  RTCProtocol.generate_SR_Header(buffer, 0, pcks, octs, ts, ntpstamp.secs, ntpstamp.fract, actSource.SSRCbytevalue, rbc);
	  actSource.lastOctets = octs;
	  actSource.lastChunks = pcks;
	}    
	else { // defaultLocalSource == actSource
	  RTCProtocol.generate_RR_Header(buffer, 0, actSource.SSRCbytevalue, rbc);
	}
	
	temp = len;
	len += RTCProtocol.RTCP_SDES_HEADER_LENGTH;
	
	len += RTCProtocol.generate_SDES_Start(buffer, len, actSource.SSRCbytevalue);
	
	s = actSource.getCname();
	if (s != null) {
	  len += RTCProtocol.generate_SDES_Item_CNAME(buffer, len, s);
	}
      
	if (actSource == defaultLocalSource) {
	  s = actSource.getName();
	  if (s != null) {
	    len += RTCProtocol.generate_SDES_Item_NAME(buffer, len, s);
	  }
	}
	len += RTCProtocol.generate_SDES_End(buffer, len, len - temp);
	RTCProtocol.generate_SDES_Header(buffer, temp, 1, len - temp);
	
	// send the new RTCP packet 
	try {
	  connection.send(buffer, lowerHeaderSize, len);
	}
	catch (Connection_Exception ce) {
	  System.err.println("EXCEPTION: RTCP_Sender_Thread.run(): Connection.sendControl(byte[],int): "+ce.getMessage());
	  ce.printStackTrace();
	  System.exit(1);
	}
    
	sourceTable.registerRTCPPacketSize(len);
      }
    }
    // sleep until it's time for the next RTCP round
    
    waitForNextSendingTime(rbc, sendSomething, len);

  } // while(true)
} // RTCP_Sender_thread.run()

private void 
waitForNextSendingTime(int num_senders, boolean local_sent, int last_packet_len) {  long bed_time;
  long started;
  long slept;
  long cycle_time;
  long duration;
  boolean sleepy;
  int n, members = sourceTable.remoteSize() + sourceTable.localSize();
  float minTime = RTP_Profile.RTCP_MIN_TIME;;
  float rtcpBW = profile.getSessionBandwidth() * RTP_Profile.RTCP_BANDWITH_FRACTION;
  float t;

  if (firstRTCPPacket) 
    minTime /= 2;
  n = members;

  if (num_senders > 0 && num_senders < (int)((float)members * RTP_Profile.RTCP_SENDER_BW_FRACTION)) {
    if (local_sent) {
      rtcpBW *= RTP_Profile.RTCP_SENDER_BW_FRACTION;
      n = num_senders;
    }
    else {
      rtcpBW *= RTP_Profile.RTCP_RCVR_BW_FRACTION;
      n -= num_senders;
    }
  }

  t = sourceTable.getAvgRTCPPacketSize() * (float)n / rtcpBW;
  if (t < RTP_Profile.RTCP_MIN_TIME)
    t = RTP_Profile.RTCP_MIN_TIME;

  t *= (sourceTable.randomizer.nextFloat() + .5);
  
  cycle_time = (long)(t*1000);
  //  System.out.println("STATUS: RTCP_Sender sleeping for "+cycle_time+" ms");
  
  sleepy = true;
  bed_time = System.currentTimeMillis();
  duration = cycle_time;

  while(sleepy) {
    sleepy = false;
    try {
      sleep(duration);
    }
    catch (InterruptedException ie) {
      slept = System.currentTimeMillis() - bed_time;
      if (slept < (9 * cycle_time / 10)) {
	sleepy = true;
	duration = cycle_time - slept;
      }
    }
  } // while(sleepy)
  
} // waitForNextSendingTime()

private Connection connection;
private RTP_SourceTable sourceTable;
private RTP_LocalSource defaultLocalSource;
private boolean firstRTCPPacket;
private RTP_Profile profile;

} // class RTCP_Sender_Thread


