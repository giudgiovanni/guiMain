package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.stream.*;
import mmstream.apps.*;
import mmstream.config.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;

import java.util.*;

public class RTP_CoDec implements ProtocolCoDec {

  private RTP_CoDec(AppManager am) {
    profile = null;

    lowerDataHeaderSize = 0;
    lowerControlHeaderSize = 0;
    dataConnection = null;
    controlConnection = null;

    appManager = am;

    SSRCTable = new RTP_SourceTable(this);

    RTPRecThreads = new ThreadGroup("RTPRecThreads");
    RTPSendThreads = new ThreadGroup("RTPSendThreads");
    receiverRTPThread = null;
    receiverRTCPThread = null;
    senderRTCPThread = null;
    startedRec = false;
    startedSend = false;
    defaultSourceHandedOut = false;
  }

  public RTP_CoDec(Connection dacon, Connection ctcon, RTP_Profile prof, AppManager am) {
    this(am);
    profile = prof;
    this.setDataConnection(dacon);
    this.setControlConnection(ctcon);
    try {
      defaultLocalSource = (SSRCTable.createLocalSource()).getId();
    }

    catch (RTP_Exception re) {
      System.err.println("EXCEPTION: RTP_CoDec():: "+re.getMessage());
    }
    receiverRTPThread = new RTP_Receiver_Thread(RTPRecThreads, dataConnection, SSRCTable, profile);

    receiverRTCPThread = new RTCP_Receiver_Thread(RTPRecThreads, controlConnection, SSRCTable);
  
    RTP_LocalSource tmp = (RTP_LocalSource)(SSRCTable.queryLocalSource(defaultLocalSource));
//     tmp.setProfile(profile);
    senderRTCPThread = new RTCP_Sender_Thread(RTPSendThreads, controlConnection, SSRCTable, tmp, profile);

    packetLength = (int)(dataConnection.getMaxSize());
    sendBuf = new byte[packetLength+lowerDataHeaderSize+ownHeaderSize];
  }




  public synchronized void startReceiving() throws RTP_Exception {
    if (startedRec == true)
      return;

    if (receiverRTPThread == null || receiverRTCPThread == null)
      throw new RTP_Exception("RTP_CoDec.startReceiving(): threads not instantiated");

    int ac = RTPRecThreads.activeCount();
    Thread[] tl = new Thread[ac];
    ac = RTPRecThreads.enumerate(tl, true);
    for (int i = 0; i < ac; i++) {
      tl[i].start();
    }
    startedRec = true;
  }


  public synchronized void startSending() throws RTP_Exception {
    if (startedSend == true)
      return;

    if (senderRTCPThread == null)
      throw new RTP_Exception("RTP_CoDec.startSending(): threads not instantiated");

    int ac = RTPSendThreads.activeCount();
    Thread[] tl = new Thread[ac];
    ac = RTPSendThreads.enumerate(tl, true);
    for (int i = 0; i < ac; i++) {
      tl[i].start();
    }
    startedSend = true;
  }


  public void finish(String reason) throws RTP_Exception {
    //   if (startedRec == false && startedSend == false)
    //     return;

    //   if (receiverRTPThread == null || receiverRTCPThread == null || senderRTCPThread == null)
    //     return;

    int ac = RTPSendThreads.activeCount();
    if (ac > 0) {
      Thread[] tl = new Thread[ac];
      ac = RTPSendThreads.enumerate(tl, true);
      for (int i = 0; i < ac; i++) {
	tl[i].stop();
      }
    }

    ac = RTPRecThreads.activeCount();
    if (ac > 0) {
      Thread[] tl = new Thread[ac];
      ac = RTPRecThreads.enumerate(tl, true);
      for (int i = 0; i < ac; i++) {
	tl[i].stop();
      }
    }

    for (Enumeration e = SSRCTable.localElements(); e.hasMoreElements();) {
      ((RTP_LocalSource)(e.nextElement())).finish(reason);
    }
    
    if (controlConnection != null) {
      controlConnection.close();
      controlConnection = null;
    }
    if (dataConnection != null) {
      dataConnection.close();
      dataConnection = null;
    }
    startedRec = false;
    startedSend = false;
  }

  public synchronized void transmitChunk(LocalSource ssrc, Chunk p) throws RTP_Exception {
    if (dataConnection == null || profile == null || startedSend == false) 
      throw new RTP_Exception("RTP_CoDec.put(byte[], int, int): No Connection or profile set or not started!");
    if ((p.data_length + ownHeaderSize + lowerDataHeaderSize)> packetLength) {
      int offset = p.data_offset;
      int numP = p.data_length / packetLength;
      if (p.data_length % packetLength != 0)
	numP++;
    
      for (int i = 0; i < numP-1; i++) {
	System.arraycopy(p.buffer, offset, sendBuf, lowerDataHeaderSize+ownHeaderSize, packetLength);
	offset += packetLength;
	RTProtocol.generate_RTP_Header(sendBuf, lowerDataHeaderSize, lowerDataHeaderSize+ownHeaderSize, ((RTP_LocalSource)ssrc).outSeqNumGen.next(), (int)(p.timeStamp), ((RTP_LocalSource)ssrc).SSRCbytevalue, profile, RTProtocol.RTP_MARKER_NO);
	try {
	  dataConnection.send(sendBuf, lowerDataHeaderSize, packetLength + ownHeaderSize);
	}
	catch (Connection_Exception ce) {
	  throw new RTP_Exception("RTP_CoDec.put(byte[], int, int): Connection failed to send: "+ce.getMessage());
	}
	ssrc.octetsSent.add(packetLength);
      }
      int remainder = p.data_length + p.data_offset - offset;
      System.arraycopy(p.buffer, offset, sendBuf, lowerDataHeaderSize+ownHeaderSize, remainder);
      offset += remainder;
      RTProtocol.generate_RTP_Header(sendBuf, lowerDataHeaderSize, lowerDataHeaderSize+ownHeaderSize, ((RTP_LocalSource)ssrc).outSeqNumGen.next(), (int)(p.timeStamp), ((RTP_LocalSource)ssrc).SSRCbytevalue, profile, RTProtocol.RTP_MARKER_YES);
      try {
	dataConnection.send(sendBuf, lowerDataHeaderSize, remainder + ownHeaderSize);
      }
      catch (Connection_Exception ce) {
	throw new RTP_Exception("RTP_CoDec.put(byte[], int, int): Connection failed to send: "+ce.getMessage());
      }
      ssrc.octetsSent.add(remainder);
      ssrc.chunksSent.incr();
    }
    else {
      RTProtocol.generate_RTP_Header(p.buffer, lowerDataHeaderSize, p.header_length, ((RTP_LocalSource)ssrc).outSeqNumGen.next(), (int)(p.timeStamp), ((RTP_LocalSource)ssrc).SSRCbytevalue, profile, RTProtocol.RTP_MARKER_YES);
      try {
	dataConnection.send(p.buffer, p.header_length - ownHeaderSize, p.data_length + ownHeaderSize);
      }
      catch (Connection_Exception ce) {
	throw new RTP_Exception("RTP_CoDec.put(byte[], int, int): Connection failed to send: "+ce.getMessage());
      }
      ssrc.octetsSent.add(p.data_length);
      ssrc.chunksSent.incr();
    }
  }

  public int getHeaderSizeRequest() throws RTP_Exception {
    if (dataConnection == null)
      throw new RTP_Exception("RTP_CoDec.getHeaderSizeRequest(): No connection set!");
    return lowerDataHeaderSize + ownHeaderSize;
  }

  public synchronized void setDataConnection(Connection con) {
    //  if (connection != null || started == true)  WHY?
    dataConnection = con;
    lowerDataHeaderSize = dataConnection.getHeaderSize();
  }

  public Connection getDataConnection() {
    return dataConnection;
  }

  public synchronized void setControlConnection(Connection con) {
    controlConnection = con;
    lowerControlHeaderSize = controlConnection.getHeaderSize();
  }

  public Connection getControlConnection() {
    return controlConnection;
  }

  public Profile getProfile() { return profile; }


  protected synchronized void resetCoDec() {
  }

  public synchronized LocalSource createLocalSource() throws RTP_Exception {
    if (defaultSourceHandedOut == false) {
      defaultSourceHandedOut = true;
      return SSRCTable.queryLocalSource(defaultLocalSource);
    }
    else {
      LocalSource tmp = SSRCTable.createLocalSource();
      return tmp;
    }
  }


  public RemoteSource getRemoteSource() {
    RemoteSource src = (RemoteSource)(SSRCTable.newSourceQueue.extract());
    return src;
  }

  protected synchronized void registerRemoteSource(RemoteSource src) throws RTP_Exception {
    try {
      appManager.registerRemoteSource(this, src);
    }
    catch (AppManager_Exception e) {
      throw new RTP_Exception("RTP_CoDec.registerNewSource(): StreamManager.registerNewSource(): "+e.getMessage());
    }
  
  }

  public String getProtocolName() {
    return Name;
  }

  public static final String Name = new String(ProtocolCoDec_TypeConfiguration.RTP_Name);
  private boolean startedRec;
  private boolean startedSend;
  private boolean defaultSourceHandedOut;
  private String defaultLocalSource;

  // what we do
  protected RTP_Profile profile;
  protected int packetLength;

  // the lower layer
  protected Connection dataConnection;
  protected Connection controlConnection;
  private int lowerDataHeaderSize;
  private int lowerControlHeaderSize;
  private final int ownHeaderSize = RTProtocol.RTP_MINIMUM_HEADER_LENGTH;
  protected byte[] sendBuf;

  // we and the rest of the world
  private RTP_SourceTable SSRCTable;
  private AppManager appManager;


  // these work for us
  private ThreadGroup RTPRecThreads;
  private ThreadGroup RTPSendThreads;
  private RTP_Receiver_Thread receiverRTPThread;
  private RTCP_Receiver_Thread receiverRTCPThread;
  private RTCP_Sender_Thread senderRTCPThread;
} // class RTP_CoDec

