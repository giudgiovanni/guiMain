package mmstream.protocols.rtp;

import mmstream.*;
import mmstream.config.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;

import java.net.*;

public abstract class RTProtocol extends Object {

  // Size of Header without any CSRCs
public  static final int  RTP_MINIMUM_HEADER_LENGTH = 12;
  // Size of Header with maximum number (15) of CSRCs
public  static final int  RTP_MAXIMUM_HEADER_LENGTH = 72;
  // Size of a SSRC/CSRC line
public  static final int  RTP_SRC_LENGTH = 4;

  //Minimum number of sequential packets for a validated source
public static final int RTP_MIN_SEQ = 2;
public static final int RTP_MAX_DROPOUT = 3000;
public static final int RTP_MAX_MISORDER = 100;

  // RTP Version
public  static final int  RTP_VERSION_MASK = 0x3 << 06;
public  static final int  RTP_VERSION =      0x2 << 06;

  // Padding 
public static final int RTP_PADDING_MASK = 0x1 << 05;
public static final int RTP_PADDING_YES =  0x1 << 05;
public static final int RTP_PADDING_NO =   0x0 << 05;

  // Extension 
public static final int RTP_EXTENSION_MASK = 0x1 << 04;
public static final int RTP_EXTENSION_YES =  0x1 << 04;
public static final int RTP_EXTENSION_NO =   0x0;

  //  CSRC Count
public static final int RTP_CC_MASK = 0xf;
public static final int RTP_CC_NONE = 0x0;

  // Marker 
public static final int RTP_MARKER_MASK = 0x1 << 07;
public static final int RTP_MARKER_YES =  0x1 << 07;
public static final int RTP_MARKER_NO =   0x0 << 07;

  // Payload Type
public static final int RTP_PT_MASK = 0x7f << 0;
public static final int RTP_PT_TEST = 0x60 << 0;

  // Sequence Number
public static final int RTP_SEQ_MOD =         0x1 << 020;
public static final int RTP_SEQ_UPPER_MASK = 0xff << 010;
public static final int RTP_SEQ_LOWER_MASK = 0xff <<   0;

  // Timestamp, SSRC, CSRC masks
public static final long RTP_UPPEST_MASK = 0xff << 030;
public static final int RTP_UPPER_MASK =  0xff << 020;
public static final int RTP_LOWER_MASK =  0xff << 010;
public static final int RTP_LOWEST_MASK = 0xff <<   0;
  
  
protected static int generate_RTP_Header(byte[] m, int headeroffset, int dataoffset, int seq_num, int timestamp, byte[] localSSRCbytes, RTP_Profile prof, int marker) throws RTP_Exception {
  if (dataoffset - headeroffset < RTP_MINIMUM_HEADER_LENGTH) 
    throw new RTP_Exception("generate_RTP_Header: Not enough space for header in packet");

  int i = headeroffset;

  // lots of these values are hardcoded for now!
  m[i++] = (byte)(RTP_VERSION | RTP_PADDING_NO | (prof.extension & RTP_EXTENSION_MASK) | RTP_CC_NONE);
  m[i++] = (byte)(RTP_MARKER_MASK & marker | prof.getPayloadType().getByteCode());

  m[i++] = (byte)((RTP_SEQ_UPPER_MASK & seq_num) >>> 010);
  m[i++] = (byte) (RTP_SEQ_LOWER_MASK & seq_num);

  m[i++] = (byte)((RTP_UPPEST_MASK & timestamp) >>> 030);
  m[i++] = (byte)((RTP_UPPER_MASK & timestamp) >>> 020);
  m[i++] = (byte)((RTP_LOWER_MASK & timestamp) >>> 010);
  m[i++] = (byte) (RTP_LOWEST_MASK & timestamp);

  System.arraycopy(localSSRCbytes, 0, m, i, RTP_SRC_LENGTH);
  i+=RTP_SRC_LENGTH;

  return (i-headeroffset);
} // generate_RTP_Header(byte [])

protected static boolean
checkHeader(byte[] m, int offset, RTP_Profile prof) {
  
  // first byte
  if ((((int)m[offset]) & RTP_VERSION_MASK) != RTP_VERSION) {
    System.err.println("ERROR: RTProtocol.checkHeader():RTP_Version "+(((int)m[offset])&RTP_VERSION_MASK)+" != "+RTP_VERSION);
    return false;
  }
//   if ((byte)(((int)m[offset]) & RTP_EXTENSION_MASK) != prof.extension) {
//     System.err.println("ERROR: RTProtocol.checkHeader():RTP_Extension "+(((int)m[offset])&RTP_EXTENSION_MASK)+" != Profile.extension "+prof.extension);
//     return false;
//   }

  // second byte
  if ((byte)(((int)m[offset+1]) & RTP_PT_MASK) != prof.getPayloadType().getByteCode()) {
    System.err.println("ERROR: RTProtocol.checkHeader():RTP_PT_Mask "+(((int)m[offset])&RTP_PT_MASK)+" != Profile.PT "+prof.getPayloadType().getByteCode());
    return false;
  }

  return true;
}



protected static String
bytes2String(byte[] b, int offset) {
  int val = 0;
  for(int i = 0; i < RTProtocol.RTP_SRC_LENGTH; i++) {
    val  |= ((((int)b[i+offset]) & 0xff) << ((RTProtocol.RTP_SRC_LENGTH - 1 - i)<<3));
  }
  return String.valueOf(val);
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

protected static String
int2String(int val) {
  return String.valueOf(val);
}
  

}

