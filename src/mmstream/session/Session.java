package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.consumer.*;
import mmstream.connection.*;
import mmstream.config.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class Session extends Object {

  public static final int NOT_READY  = 0;
  public static final int RUNNABLE   = 1;
  public static final int RUNNING    = 2;
  public static final int FINISHED   = 3;
  public static final int ERROR      = 4;
  public static final int RECEIVABLE = 5;
  public static final int SENDABLE   = 6;
  public static final int MAX_STATE  = 6;

  protected static String stateString(int s) {
    switch(s) {
    case NOT_READY   : return new String("Not ready");
    case RUNNABLE    : return new String("Runnable");
    case RUNNING     : return new String("Running");
    case RECEIVABLE  : return new String("Reception possible, Sending started");
    case SENDABLE    : return new String("Sending possible, Reception started");
    case FINISHED    : return new String("Finished");
    case ERROR       : return new String("Error");
    default:           return null;
    }    
  }


  public Session() {
    appMan = null;
    config = null;
    profile = null;
    defaultLocalSource = null;
    codec = null;
    dataCon = null;
    controlCon = null;
    state = Session.NOT_READY;
    output = new Println_SessionOutput();
    handedOutDefaultSource = false;

    producerTable = new Hashtable(5, (float)0.5);
    consumerTable = new Hashtable(5, (float)0.5);
  }

  public Session(AppManager am, Config cf)  throws Session_Exception {
    this();
    appMan = am;
    config = cf;
    profile = config.getProfile();

    codec = config.getCoDec();
    dataCon = config.getDataConnection();
    controlCon = config.getControlConnection();
    //    System.out.println("MESSAGE: Session(): data connection is "+dataCon.getTypeName()+", class is "+dataCon.getClass().getName());
    try {
      defaultLocalSource = codec.createLocalSource();
    }
    catch(CoDec_Exception ce) {
      throw new Session_Exception("Session(AppManager,Config): codec.createLocalSoure():: "+ce.getMessage());
    }

    maxBandwidth = profile.getSessionBandwidth();
    usedBandwidth = 0;
    chunkRate = profile.getChunkRate();
    payloadLength = profile.getPayloadType().getSize();

    this.setState(Session.RUNNABLE);
  }


  public void 
  startSending() throws Session_Exception {
    output.message("Message: Session.startSending() "+getId()+": state is "+getState());
    if (state != Session.RUNNABLE  && state != Session.SENDABLE)
      return;

    try {
      codec.startSending();
    }
    catch (CoDec_Exception ce) {
      this.setState(Session.ERROR);
      throw new Session_Exception("Session.startSending(): codec.startSending()::"+ce.getMessage());
    }
    output.message("Message: Session.startSending() "+getId()+": codec.startSending() succeded");

    ProducerControl pc;
    for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
      pc =(ProducerControl)(e.nextElement());
      pc.Start();
    }
  
    if (state == RUNNABLE)
      this.setState(Session.RECEIVABLE);
    else if (state == SENDABLE)
      this.setState(Session.RUNNING);
  }


  public void 
  startReception() throws Session_Exception {
    output.message("Message: Session.startReception() "+getId()+": state is "+getState());
    if (state != Session.RUNNABLE && state != RECEIVABLE)
      return;

    try {
      codec.startReceiving();
    }
    catch (CoDec_Exception ce) {
      this.setState(Session.ERROR);
      throw new Session_Exception("Session.startReception(): codec.startReception()::"+ce.getMessage());
    }
    output.message("Message: Session.startReception() "+getId()+": codec.startReception() succeded");

    ConsumerControl pc;
    for(Enumeration e = consumerTable.elements(); e.hasMoreElements(); ) {
      pc =(ConsumerControl)(e.nextElement());
      pc.Start();
    }

    if (state == RUNNABLE)
      this.setState(Session.SENDABLE);
    if (state == RECEIVABLE)
      this.setState(Session.RUNNING);
  }


  public void finish(String reason) {
    try {
      this.setState(Session.FINISHED);
    }
    catch(Session_Exception se) {;}

    ConsumerControl cc;
    for(Enumeration e = consumerTable.elements(); e.hasMoreElements(); ) {
      cc =(ConsumerControl)(e.nextElement());
      cc.Finish();
    }
    
    ProducerControl pc;
    for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
      pc =(ProducerControl)(e.nextElement());
      pc.Finish(reason, true);
    }
    
    try {
      codec.finish(reason);
    }
    catch (CoDec_Exception ce) {
      output.error("EXCEPTION: Session.finish(): codec.finish(): " + ce.getMessage());
    }  
  }



  public synchronized ControlData 
  setChunkRate(double nw) {
    ControlData max = new ControlData(), ret;
    max.setMaxBandwidth(maxBandwidth);
    max.setPayloadLength(payloadLength);
    max.setChunkRate(chunkRate);
  
    if (nw <= 0 || nw == chunkRate)
      return max;
  
    if (nw > profile.getChunkRate())
      nw = profile.getChunkRate();

    if ((nw * ((double)payloadLength)) > ((double)maxBandwidth))
      nw = ((double)maxBandwidth) / ((double)payloadLength);

    chunkRate = nw;

    ProducerControl cur;
    for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
      cur = (ProducerControl)(e.nextElement());
      ret = cur.setChunkRate(chunkRate);
      cur.getOutput().notifyControlData(ret);
    }

    max.setChunkRate(chunkRate);
    return max;
  }

  public double 
  getChunkRate() { return chunkRate; }

  public void
  setExport(boolean ex) { export = ex; }

  public boolean 
  getExport() { return export; }

  public synchronized ControlData
  setPayloadLength(long nw) {
    ControlData max = new ControlData(), ret;
    max.setMaxBandwidth(maxBandwidth);
    max.setPayloadLength(payloadLength);
    max.setChunkRate(chunkRate);
  

    if (nw <= profile.getPayloadType().getMinSize() || nw == payloadLength)
      return max;

    if (nw > profile.getPayloadType().getMaxSize())
      payloadLength = profile.getPayloadType().getMaxSize();

    payloadLength = nw;

    if ((((double)payloadLength) * chunkRate) > ((double)maxBandwidth))
      chunkRate = ((double)maxBandwidth) / ((double)payloadLength);

    ProducerControl cur;
    for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
      cur = (ProducerControl)(e.nextElement());
      ret = cur.setPayloadLength(payloadLength);
      if (cur.getChunkRate() < chunkRate)
	ret = cur.setChunkRate(chunkRate);
      cur.getOutput().notifyControlData(ret);
    }

    max.setPayloadLength(payloadLength);
    max.setChunkRate(chunkRate);
  
    return max;
  }

  public void 
  setInitiator(String val) { initiator = val; }

  public String 
  getInitiator() { return initiator; }

  public long 
  getPayloadLength() { return payloadLength; }

  public synchronized ControlData
  setMaxBandwidth(long nw, boolean adjust) {
    ControlData max = new ControlData(), ret;
    max.setMaxBandwidth(maxBandwidth);
    max.setPayloadLength(payloadLength);
    max.setChunkRate(chunkRate);
  

    if (nw <= 0 || nw == maxBandwidth)
      return max;

    ProducerControl cur;

    if (nw > profile.getSessionBandwidth())
      nw = profile.getSessionBandwidth();
  
    maxBandwidth = nw;

    if (maxBandwidth < this.getUsedBandwidth()) {
      double fact = ((double)maxBandwidth)/((double)usedBandwidth);
      for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
	cur = (ProducerControl)(e.nextElement());
	if (!cur.getState().equals("Finished")) {
	  long ww = (long)(fact * ((double)cur.getMaxBandwidth()));
	  ret = cur.setMaxBandwidth(ww);
	  cur.getOutput().notifyControlData(ret);
	  this.output.message(cur.getId()+".setBandwidth("+ww+") = "+ret.getMaxBandwidth()+", used is "+this.getUsedBandwidth());
	}
      }
    }

    else if (adjust == true) {
      if (this.getUsedBandwidth() == 0) {
	;
      }
      else {
	double fact = ((double)maxBandwidth)/((double)this.getUsedBandwidth());
	for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
	  cur = (ProducerControl)(e.nextElement());
	  if (!cur.getState().equals("Finished")) {
	    long ww = (long)(fact * ((double)cur.getMaxBandwidth()));
	    ret = cur.setMaxBandwidth(ww);
	    cur.getOutput().notifyControlData(ret);
	    this.output.message(cur.getId()+".setBandwidth("+ww+") = "+ret.getMaxBandwidth()+", used is "+this.getUsedBandwidth());
	  }
	}
      }
    }

    max.setMaxBandwidth(maxBandwidth);
    return max;
  }

  public long 
  getMaxBandwidth() { return maxBandwidth; }

  public long 
  getUsedBandwidth() { 
    usedBandwidth = 0;
    ProducerControl pc = null;
    for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
       pc= (ProducerControl)(e.nextElement());
       if (!pc.getState().equals("Finished"))
	 usedBandwidth += pc.getMaxBandwidth();
    }
    return usedBandwidth; 
  }

  public synchronized  ControlData
  setProducerBandwidth(ProducerControl prod, long bw) throws Control_Exception {
    ControlData ret = prod.getControlData();

    if (producerTable.contains(prod) == false)
      throw new Control_Exception("Session.checkProducerBandwidth: producer "+prod.getId()+" does not exists");

    if (bw < 0 || bw == ret.getMaxBandwidth())
      return ret;

    if (bw > maxBandwidth)
      bw = maxBandwidth;
    if ((bw + this.getUsedBandwidth() - ret.getMaxBandwidth()) > maxBandwidth) 
      bw = maxBandwidth - this.getUsedBandwidth() + ret.getMaxBandwidth();
    if (bw > 0) {
      return prod.setMaxBandwidth(bw);
    }
    else 
      return ret;
  }

  public synchronized  ControlData 
  setControlData(ControlData cd) {
    ControlData ret;
    ret = this.setMaxBandwidth(cd.getMaxBandwidth(), cd.getAdjust());
    ret = this.setPayloadLength(cd.getPayloadLength());
    ret = this.setChunkRate(cd.getChunkRate());
  
    return ret;
  }

  public ControlData
  getControlData() {
    ControlData ret = new ControlData();
    ret.setMaxBandwidth(this.getMaxBandwidth());
    ret.setPayloadLength(this.getPayloadLength());
    ret.setChunkRate(this.getChunkRate());
  
    return ret;
  }


  public synchronized void 
  registerProducer(ProducerControl prod, boolean start, int priority) throws Control_Exception {
    String id = prod.getId();
    if (producerTable.containsKey(id)) 
      throw new Control_Exception("Session.registerProducer:Producer exists"); 
    long prodBW = prod.getMaxBandwidth();
    if (prodBW > maxBandwidth)
      prodBW = maxBandwidth;


    long av = maxBandwidth - this.getUsedBandwidth();
    if (prodBW > av) {
      int act = 0;
      ProducerControl cur;
      for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
	cur = (ProducerControl)(e.nextElement());
	if (!cur.getState().equals("Finished"))
	  act++;
      }
      long share = maxBandwidth / (act + 1);
      if (share <= av) {
	prodBW = av;
      }
      else {
	prodBW = share;
	long target = maxBandwidth - prodBW;
	double fact = ((double)target)/((double)this.getUsedBandwidth());
	for(Enumeration e = producerTable.elements(); e.hasMoreElements(); ) {
	  cur = (ProducerControl)(e.nextElement());
	  ControlData ret;
	  long ww = (long)(fact * ((double)cur.getMaxBandwidth()));
	  ret = cur.setMaxBandwidth(ww);
	  cur.getOutput().notifyControlData(ret);
	  this.output.message(cur.getId()+".setBandwidth("+ww+") = "+ret.getMaxBandwidth()+", used is "+this.getUsedBandwidth());
	}
      }
    }

    prod.setMaxBandwidth(prodBW);

    producerTable.put(id, prod);
    priority = (priority > Thread.MAX_PRIORITY) ? Thread.MAX_PRIORITY : priority;
    priority = (priority < Thread.MIN_PRIORITY) ? Thread.MIN_PRIORITY : priority;
    prod.setPriority(priority);
    if (start) {
      prod.Start();
    }
    prod.getOutput().notifyStateChange();

    this.getUsedBandwidth();

    // ok, this is some kind'a hack,
    // but for a producer it's enough to have a StreamExporter;
    // for a session this is not enough, it needs LocalSources...
    // but as a Session we know that the StreamExporter IS a LocalSource!
    LocalSource ls = (LocalSource)prod.getStreamExporter();
    ls.setProducer(prod);
    try {
      appMan.registerLocalSource(codec, ls);
    }
    catch(AppManager_Exception ae) {
      this.output.error("EXCEPTION: Session.registerProducer(): appMan.registerLocalSource()::"+ae.getMessage());
      ae.printStackTrace();
    }
    ls.output.notifyStateChange();
  }

  public synchronized  void 
  registerConsumer(ConsumerControl cons, int priority) throws Control_Exception {
    String id = cons.getId();
    if (consumerTable.containsKey(id)) 
      throw new Control_Exception("Session.registerConsumer:Consumer exists");
    consumerTable.put(id, cons);
    priority = (priority > Thread.MAX_PRIORITY) ? Thread.MAX_PRIORITY : priority;
    priority = (priority < Thread.MIN_PRIORITY) ? Thread.MIN_PRIORITY : priority;
    cons.setPriority(priority);
    cons.getOutput().notifyStateChange();

    this.output.message("registerConsumer: "+id);
  }


  public String getId() { return id;}
  public void setId(String i) { id = i;}

  public void setOutput(SessionOutput so) { output = so; }
  public SessionOutput getOutput() { return output; }
  
  public AppManager getAppManager() { return appMan; }
  public void setAppManager(AppManager v) { appMan = v; }

  public LocalSource getDefaultLocalSource() { return defaultLocalSource; }
  public void setDefaultLocalSource(LocalSource v) { defaultLocalSource = v; }

  public LocalSource getNewLocalSource() throws Session_Exception { 
    if (handedOutDefaultSource == false) {
      handedOutDefaultSource = true;
      return defaultLocalSource;
    }

    LocalSource ret = null;
    try {
      ret = codec.createLocalSource();
    }
    catch(CoDec_Exception ce) {
      throw new Session_Exception("Session(AppManager,Config): codec.createLocalSoure():: "+ce.getMessage());
    }
    return ret; 
  }

  public Config getConfig() { return config; }
  public void setConfig(Config v) { config = v; }

  public Profile getProfile() { return profile; }
  public void setProfile(Profile v) { profile = v; }

  public ProtocolCoDec getCoDec() { return codec; }
  public void setCoDec(ProtocolCoDec v) { codec = v; }

  public Connection getDataConnection() { return dataCon; }
  public void setDataConnection(Connection v) { dataCon = v; }

  public Connection getControlConnection() { return controlCon; }
  public void setControlConnection(Connection v) { controlCon = v; }

  public String getState() { return Session.stateString(state);}
  public byte getStateCode() { return (byte)state;}

  protected void 
  setState(int s) throws  Session_Exception { 
    if (s >= 0 || s <= Session.MAX_STATE)
      state = s;
    else
      throw new Session_Exception("Session.setState(int): Unvalid state "+s);
    output.notifyStateChange();
  }

  protected void 
  setState(String s) throws  Session_Exception { 
    if (s == null)
      throw new Session_Exception("Session.setState(String): empty state ");
    for (int i = 0; i <= Session.MAX_STATE; i++) {
      if (s.equals(Session.stateString(i))) {
	state = i;
	output.notifyStateChange();
	return;
      }
    }

    throw new Session_Exception("Session.setState(String): Unvalid state "+s);
  }


  protected long usedBandwidth;
  protected long maxBandwidth;
  protected long payloadLength;
  protected double chunkRate;

  protected SessionOutput output;
  protected int state;
  protected String id;
  protected String initiator;
  protected AppManager appMan;
  protected boolean handedOutDefaultSource;
  protected LocalSource defaultLocalSource;
  protected Profile profile;
  protected Config config;
  protected Connection dataCon;
  protected Connection controlCon;
  protected ProtocolCoDec codec;

  protected Hashtable producerTable;
  protected Hashtable consumerTable;

  protected boolean export;
}
