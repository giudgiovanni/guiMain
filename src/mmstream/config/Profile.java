package mmstream.config;

import mmstream.util.*;

public abstract class Profile extends Object {

  public abstract String getName();

  public abstract byte[] getParams();

  public abstract void configure(byte[] params, int off, int len);

  public Profile() {
    sessionBandwidth = 0;
    dataLength = 0;
    chunkRate = 0.0;
    clockRate = 0.0;
    headerLength = -1;
    payloadType = null;
  }

  public Profile(long len, long bw, double pr, PayloadType pt, double cr) {
    sessionBandwidth = bw;
    dataLength = len;
    chunkRate = pr;
    clockRate = cr;
    headerLength = -1;
    payloadType = pt;
  }

  public PayloadType
  getPayloadType() { return payloadType;}

  public void
  setPayloadType(PayloadType val) { payloadType = val;}


  public long 
  getSessionBandwidth() { return sessionBandwidth;}

  public void
  setSessionBandwidth(long val) { sessionBandwidth = val;}


  public double 
  getChunkRate() { return chunkRate;}

  public void
  setChunkRate(double val) { chunkRate = val;}

  public double 
  getClockRate() { return clockRate;}

  public void
  setClockRate(double val) { clockRate = val;}

  public long 
  getDataLength() { return dataLength;}

  public void
  setDataLength(long val) { dataLength = val;}


  public int 
  getHeaderLength() { return headerLength;}

  public void
  setHeaderLength(int val) { headerLength = val;}

  protected PayloadType payloadType;
  protected long sessionBandwidth;
  protected long dataLength;
  protected double chunkRate;
  protected double clockRate;
  protected int headerLength;


}

