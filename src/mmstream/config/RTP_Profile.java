package mmstream.config;

import mmstream.util.*;
import mmstream.apps.*;
import mmstream.config.*;
import mmstream.protocols.rtp.*;

public class RTP_Profile extends Profile {

  private static String Name;

  static {
    Name = new String(ProtocolCoDec_TypeConfiguration.RTP_Name);
  }


  // Various RTCP constants
  public static final float RTCP_BANDWITH_FRACTION = (float)0.05;
  public static final float RTCP_MIN_TIME = 5;
  public static final float RTCP_SENDER_BW_FRACTION = (float)0.25;
  public static final float RTCP_RCVR_BW_FRACTION = 1 - RTCP_SENDER_BW_FRACTION;
  public static final float RTCP_SIZE_GAIN = (float)1 / 16;
  public static final float RTCP_MAX_EMPTY_INTERVALS = 5;

  public RTP_Profile(int ext, PayloadType pt, long len, long bw, double pr, double cr) {
    super(len, bw, pr, pt, cr);

    extension = ext;
  }

  public RTP_Profile() {
    super();

    extension = 0;
  }

  @Override
  public String getName() {
    return Name;
  }

  public int
  getExtension() { return extension;}

  public void
  setExtension(int val) { extension = val;}

  @Override
  public byte[]
  getParams() {
    byte[] ret = new byte[33];
    int off = 0;
    for (int i = 7; i >= 0; i--)
      ret[off++] = (byte)((dataLength & (0xffL << i*8)) >>> i*8);
    for (int i = 7; i >= 0; i--)
      ret[off++] = (byte)((sessionBandwidth & (0xffL << i*8)) >>> i*8);
    for (int i = 7; i >= 0; i--)
      ret[off++] = (byte)((((long)chunkRate) & (0xffL << i*8)) >>> i*8);
    for (int i = 7; i >= 0; i--)
      ret[off++] = (byte)((((long)clockRate) & (0xffL << i*8)) >>> i*8);
    ret[off++] = (byte)(extension & 127);

    return ret;
  }

  @Override
  public void 
  configure(byte[] params, int off, int len) {
    dataLength = 0;
    for (int i = 7; i >= 0; i--)
      dataLength |= (((long)(params[off++] & 0xff)) << i*8);
    sessionBandwidth = 0;
    for (int i = 7; i >= 0; i--)
      sessionBandwidth |= (((long)(params[off++] & 0xff)) << i*8);
    long dummy = 0;
    for (int i = 7; i >= 0; i--)
      dummy |= (((long)(params[off++] & 0xff)) << i*8);
    chunkRate = (double)dummy;
    dummy = 0;
    for (int i = 7; i >= 0; i--)
      dummy |= (((long)(params[off++] & 0xff)) << i*8);
    clockRate = (double)dummy;
    extension = params[off++];
  }

  public int extension;
}
