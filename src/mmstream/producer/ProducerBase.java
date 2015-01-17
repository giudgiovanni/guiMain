package mmstream.producer;
 
import mmstream.source.*;
import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.protocols.*;

import java.net.*;
import java.io.*;
import java.util.*;

public abstract class ProducerBase extends Thread implements ProducerControl {

  public ProducerBase(Session ses, StreamExporter os, String id) throws Session_Exception {
    //  super(tg, "Byte_Producer");
    session = ses;
    profile = session.getProfile();
    codec = session.getCoDec();
    streamExporter = os;
    this.id =id;
    bandwidth = session.getMaxBandwidth()/2;
    chunkRate = session.getChunkRate();
    payloadLength = session.getPayloadLength();
    output = new Println_ProducerOutput();

    if (chunkRate * payloadLength > bandwidth) {
      chunkRate = ((double)bandwidth) / ((double)payloadLength);
    }
    if (chunkRate < 0.05D)
      chunkRate = 0.05D; // HACK
    //  else
    this.setSleeptime((long)(1000.0D/chunkRate));
  
    try { 
      lowerHeaderSize = codec.getHeaderSizeRequest();
    }
    catch(CoDec_Exception ce) {
      throw new Session_Exception("ProducerBase(): codec.getHeaderSizeRequest(): " + ce.getMessage());
    }
    this.setState("Runnable");
    //    this.output.message("ProducerBase(): lowerHeaderSize is "+lowerHeaderSize+", bw = "+bandwidth);
  }


  protected synchronized long getSleeptime() {
    return sleeptime;
  }

  protected synchronized void setSleeptime(long n) {
    sleeptime = n;
//     this.interrupt();
  }

  public synchronized ControlData 
  setMaxBandwidth(long nw) { 
    ControlData ret = new ControlData();
    ret.setMaxBandwidth(bandwidth);
    ret.setPayloadLength(payloadLength);
    ret.setChunkRate(chunkRate);

    if (nw < 0 || nw == bandwidth) 
      return ret;

    if (nw <= session.getMaxBandwidth())
      bandwidth = nw; 
    else
      bandwidth = session.getMaxBandwidth(); 
  
    if (bandwidth < chunkRate * payloadLength) {
      chunkRate = ((double)bandwidth) / ((double)payloadLength);
      ret.setChunkRate(chunkRate);
      if (chunkRate < 0.05D)
	chunkRate = 0.05D; // HACK
      this.setSleeptime((long)(1000.0D/chunkRate));
    }

    ret.setMaxBandwidth(bandwidth);
    return ret;
  }

  public long getMaxBandwidth() {
    return bandwidth;
  }

  public long getUsedBandwidth() {
    return bandwidth;
  }

  public synchronized ControlData 
  setChunkRate(double nw) {
    ControlData ret = new ControlData();
    ret.setMaxBandwidth(bandwidth);
    ret.setPayloadLength(payloadLength);
    ret.setChunkRate(chunkRate);

    if (nw <= 0 || nw == chunkRate)
      return ret;

    if (nw > session.getChunkRate())
      nw = session.getChunkRate(); 
  
    if ((nw * ((double)payloadLength)) > ((double)bandwidth))
      nw = ((double)bandwidth) / ((double)payloadLength);

    chunkRate = nw;

    if (chunkRate < 0.05D)
      chunkRate = 0.05D; // HACK

    this.setSleeptime((long)(1000.0D/chunkRate));

    ret.setChunkRate(chunkRate);
    return ret;
  }

  public double getChunkRate() {
    return chunkRate;
  }

  public synchronized ControlData
  setPayloadLength(long nw) {
    ControlData ret = new ControlData();
    ret.setMaxBandwidth(bandwidth);
    ret.setPayloadLength(payloadLength);
    ret.setChunkRate(chunkRate);

    if (nw <= session.getProfile().getPayloadType().getMinSize() || nw == payloadLength)
      return ret;

    if (nw > session.getPayloadLength())
      nw = session.getPayloadLength(); 
  
    payloadLength = nw;
  
    if ((((double)payloadLength) * chunkRate) > ((double)bandwidth))
      chunkRate = ((double)bandwidth) / ((double)payloadLength);
    if (chunkRate < 0.05D)
      chunkRate = 0.05D; // HACK
    this.setSleeptime((long)(1000.0D/chunkRate));

    ret.setChunkRate(chunkRate);
    ret.setPayloadLength(payloadLength);
    return ret;
  }

  public long getPayloadLength() {
    return payloadLength;
  }

  public ControlData setControlData(ControlData cd) {
    ControlData ret = new ControlData();
    ret.setMaxBandwidth((this.setMaxBandwidth(cd.getMaxBandwidth())).getMaxBandwidth());
    ret.setPayloadLength((this.setPayloadLength(cd.getPayloadLength())).getPayloadLength());
    ret.setChunkRate((this.setChunkRate(cd.getChunkRate())).getChunkRate());
  
    return ret;
  }

  public ControlData getControlData() {
    ControlData ret = new ControlData();
    ret.setMaxBandwidth(this.getMaxBandwidth());
    ret.setPayloadLength(this.getPayloadLength());
    ret.setChunkRate(this.getChunkRate());
  
    return ret;
  }

  public void setState(String s) { state = s; }
  public String getState() { return state; }

  public String getId() { return id; }

  public StreamExporter getStreamExporter() { return streamExporter; }

  public ProducerOutput getOutput() { return output; }
  public void setOutput(ProducerOutput out) { output = out; }

  public abstract void run();

  public void 
  Start() {
    if (state.equals("Running") || state.equals("Finished"))
      return;
    if (state.equals("Runnable")) {
      this.setState("Running");
      this.start();
    }
    if (state.equals("Suspended")) {
      this.setState("Running");
      this.resume();
    }
    this.output.notifyStateChange();
    return;
  }

  public void
  Stop() {
    if (state.equals("Suspended") || state.equals("Finished"))
      return;
    if (state.equals("Runnable")) {
      this.setState("Suspended");
      this.suspend();
    }
    if (state.equals("Running")) {
      this.setState("Suspended");
      this.suspend();
    }
    this.output.notifyStateChange();
    return;
  }


  public void Finish(String reason, boolean finishExporter) {
    this.setState("Finished");
    this.stop();
    this.output.notifyStateChange();
    try {
      session.setProducerBandwidth(this, 0);
    }
    catch(Control_Exception ce) {
      ;
    }
    if (finishExporter)
      streamExporter.finish(reason);
  }

  protected Session session;
  protected  Profile profile;
  protected  ProtocolCoDec codec;
  protected  StreamExporter streamExporter;
  protected  int lowerHeaderSize;
  protected long sleeptime;
  protected long bandwidth;
  protected double chunkRate;
  protected long payloadLength;
  protected String id;
  protected String state = "Runnable";
  //  public final static String Name = new String (Producer_TypeConfiguration.BYTE_Name);
  protected ProducerOutput output;

}



