package mmstream.protocols.rtp;

import mmstream.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;

import java.net.*;

public abstract class RTCProtocol extends Object {

  // Size of different Headers 
public  static final int  RTCP_MINIMUM_HEADER_LENGTH = 4;
public  static final int  RTCP_SR_HEADER_LENGTH = 28;
public  static final int  RTCP_RR_HEADER_LENGTH = 8;
public  static final int  RTCP_SDES_HEADER_LENGTH = 4;
public  static final int  RTCP_BYE_HEADER_LENGTH = 8;
public  static final int  RTCP_APP_HEADER_LENGTH = 12;


  // Size of blocks
public  static final int  RTCP_RR_BLOCK_LENGTH = 24;

  // RTP Version
public  static final int  RTP_VERSION_MASK = 0x3 << 06;
public  static final int  RTP_VERSION =      0x2 << 06;

  // Padding 
public static final int RTCP_PADDING_MASK = 0x1 << 05;
public static final int RTCP_PADDING_YES =  0x1 << 05;
public static final int RTCP_PADDING_NO =   0x0 << 05;

  //  Count
public static final int RTCP_BC_MASK = 0x1f;
public static final int RTCP_BC_NONE = 0x0;

  // Payload Type
public static final int RTCP_PT_MASK = 207;
public static final int RTCP_PT_SR =   200;
public static final int RTCP_PT_RR =   201;
public static final int RTCP_PT_SDES = 202;
public static final int RTCP_PT_BYE =  203;
public static final int RTCP_PT_APP =  204;

public static final int RTCP_SDES_CNAME = 1;
public static final int RTCP_SDES_NAME  = 2;
public static final int RTCP_SDES_EMAIL = 3;
public static final int RTCP_SDES_PHONE = 4;
public static final int RTCP_SDES_LOC   = 5;
public static final int RTCP_SDES_TOOL  = 6;
public static final int RTCP_SDES_NOTE  = 7;
public static final int RTCP_SDES_PRIV  = 8;

  // Length, Sequence Number masks
public static final int RTCP_HIGH_MASK = 0xff << 010;
public static final int RTCP_LOW_MASK =  0xff <<   0;
public static final int RTCP_HIGH_SHIFT = 010;
public static final int RTCP_LOW_MOD =   0x100;

  // Timestamp, SSRC, CSRC masks
public static final long RTCP_LONG_UPPEST_MASK = 0xff << 070;
public static final long RTCP_LONG_UPPER_MASK =  0xff << 060;
public static final long RTCP_LONG_LOWER_MASK =  0xff << 050;
public static final long RTCP_LONG_LOWEST_MASK = 0xff << 040;

public static final long RTCP_UPPEST_MASK = 0xff << 030;
public static final int RTCP_UPPER_MASK =  0xff << 020;
public static final int RTCP_LOWER_MASK =  0xff << 010;
public static final int RTCP_LOWEST_MASK = 0xff <<   0;

public static final long RTCP_LONG_UPPEST_SHIFT =  070;
public static final long RTCP_LONG_UPPER_SHIFT =   060;
public static final long RTCP_LONG_LOWER_SHIFT =   050;
public static final long RTCP_LONG_LOWEST_SHIFT =  040;

public static final int RTCP_UPPEST_SHIFT =  030;
public static final int RTCP_UPPER_SHIFT =   020;
public static final int RTCP_LOWER_SHIFT =   010;
public static final int RTCP_LOWEST_SHIFT =    0;


protected static int 
generate_SR_Header(byte[] m, int headeroffset, long chunk_count, long octet_count, long rtp_timestamp, long ntp_timestamp_int, long ntp_timestamp_fract, byte[] localSSRCbytes, int rbc) {
  //throws RTCP_Exception
  
  int i = headeroffset;
  
  m[i++] = (byte)(RTP_VERSION | RTCP_PADDING_NO | (RTCP_BC_MASK & rbc));
  
  m[i++] = (byte)(RTCP_PT_SR);
  
  int tmp = 6 * (rbc + 1);
  m[i++] = (byte)((RTCP_HIGH_MASK & tmp) >>> RTCP_HIGH_SHIFT);
  m[i++] = (byte) (RTCP_LOW_MASK & tmp);

  System.arraycopy(localSSRCbytes, 0, m, i, 4);
  i += 4; 

  

  m[i++] = (byte)((RTCP_UPPEST_MASK & ntp_timestamp_int) >>> RTCP_UPPEST_SHIFT);
  m[i++] = (byte)((RTCP_UPPER_MASK & ntp_timestamp_int) >>> RTCP_UPPER_SHIFT);
  m[i++] = (byte)((RTCP_LOWER_MASK & ntp_timestamp_int) >>> RTCP_LOWER_SHIFT);
  m[i++] = (byte) (RTCP_LOWEST_MASK & ntp_timestamp_int);
  m[i++] = (byte)((RTCP_UPPEST_MASK & ntp_timestamp_fract) >>> RTCP_UPPEST_SHIFT);
  m[i++] = (byte)((RTCP_UPPER_MASK & ntp_timestamp_fract) >>> RTCP_UPPER_SHIFT);
  m[i++] = (byte)((RTCP_LOWER_MASK & ntp_timestamp_fract) >>> RTCP_LOWER_SHIFT);
  m[i++] = (byte) (RTCP_LOWEST_MASK & ntp_timestamp_fract);

  
  m[i++] = (byte)((RTCP_UPPEST_MASK & rtp_timestamp) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & rtp_timestamp) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & rtp_timestamp) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & rtp_timestamp);

  
  m[i++] = (byte)((RTCP_UPPEST_MASK & chunk_count) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & chunk_count) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & chunk_count) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & chunk_count);

  
  m[i++] = (byte)((RTCP_UPPEST_MASK & octet_count) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & octet_count) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & octet_count) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & octet_count);

  return i - headeroffset;
  
}

protected static int 
generate_RR_Header(byte[] m, int headeroffset, byte[] localSSRCbytes, int rbc) 
       //throws RTCP_Exception 
{
  
  int i = headeroffset;

  m[i++] = (byte)(RTP_VERSION | RTCP_PADDING_NO | (RTCP_BC_MASK & rbc));

  m[i++] = (byte)(RTCP_PT_RR);

  int tmp = 6 * rbc + 1;
  m[i++] = (byte)((RTCP_HIGH_MASK & tmp) >>> 010);
  m[i++] = (byte) (RTCP_LOW_MASK & tmp);

  System.arraycopy(localSSRCbytes, 0, m, i, 4);
  i += 4; 

  return i - headeroffset;  
}

protected static int 
generate_RR_Block(byte[] m, int headeroffset, byte[] SSRCbytes, long frLost, long lost, long ext_seq, long jitter, long lastSR, long delayLastSR) {

  int i = headeroffset;
  
  System.arraycopy(SSRCbytes, 0, m, i, 4);
  i += 4; 

  m[i++] = (byte)(0xff & frLost);
  m[i++] = (byte)((RTCP_UPPER_MASK & lost) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & lost) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & lost);

  
  m[i++] = (byte)((RTCP_UPPEST_MASK & ext_seq) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & ext_seq) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & ext_seq) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & ext_seq);

  m[i++] = (byte)((RTCP_UPPEST_MASK & jitter) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & jitter) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & jitter) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & jitter);

  m[i++] = (byte)((RTCP_UPPEST_MASK & lastSR) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & lastSR) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & lastSR) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & lastSR);

  m[i++] = (byte)((RTCP_UPPEST_MASK & delayLastSR) >>> 030);
  m[i++] = (byte)((RTCP_UPPER_MASK & delayLastSR) >>> 020);
  m[i++] = (byte)((RTCP_LOWER_MASK & delayLastSR) >>> 010);
  m[i++] = (byte) (RTCP_LOWEST_MASK & delayLastSR);

  return i - headeroffset;  
}

protected static int 
generate_SDES_Header(byte[] m, int headeroffset, int desc, int len) 
       //throws RTCP_Exception 
{
  int i = headeroffset;

  m[i++] = (byte)(RTP_VERSION | RTCP_PADDING_NO | (RTCP_BC_MASK & desc));

  m[i++] = (byte)(RTCP_PT_SDES);
  
//   if (len % 4 != 0)
//     System.out.println("URGENT: generate_SDES_Header: len % 4 != 0");

  int tmp = (len - 4) / 4;
  m[i++] = (byte)((RTCP_HIGH_MASK & tmp) >>> 010);
  m[i++] = (byte) (RTCP_LOW_MASK & tmp);

  return i - headeroffset;
}

private static int
generate_SDES_Item(byte[] m, int offset, int mask, String item) {

  int i = offset;
  int len = item.length();
  if (len > RTCP_LOW_MASK)
    len = RTCP_LOW_MASK;
    
  m[i++] = (byte)mask;
  m[i++] = (byte)(len);
  item.getBytes(0, len, m, i);
  i += len;
  
  return i - offset;
}


protected static int
generate_SDES_Item_CNAME(byte[] m, int offset, String cname) {
  return generate_SDES_Item(m, offset, RTCP_SDES_CNAME, cname);
}

protected static int
generate_SDES_Item_NAME(byte[] m, int offset, String name) {
  return generate_SDES_Item(m, offset, RTCP_SDES_NAME, name);
}

protected static int
generate_SDES_Item_EMAIL(byte[] m, int offset, String email) {
  return generate_SDES_Item(m, offset, RTCP_SDES_EMAIL, email);
}

protected static int
generate_SDES_Item_PHONE(byte[] m, int offset, String phone) {
  return generate_SDES_Item(m, offset, RTCP_SDES_PHONE, phone);
}

protected static int
generate_SDES_Item_LOC(byte[] m, int offset, String loc) {
  return generate_SDES_Item(m, offset, RTCP_SDES_LOC, loc);
}


protected static int
generate_SDES_Item_TOOL(byte[] m, int offset, String tool) {
  return generate_SDES_Item(m, offset, RTCP_SDES_TOOL, tool);
}

protected static int
generate_SDES_Item_NOTE(byte[] m, int offset, String note) {
  return generate_SDES_Item(m, offset, RTCP_SDES_NOTE, note);
}


protected static int
generate_SDES_Item_PRIV(byte[] m, int offset, String prefix, String value) {
  int i = offset;
  int len_p = prefix.length();
  if (len_p+1 > RTCP_LOW_MASK)
    len_p = RTCP_LOW_MASK - 1;
  int len = len_p + 1;
  int len_v = value.length() & RTCP_LOW_MASK;
  if (len + len_v > RTCP_LOW_MASK)
    len_v = RTCP_LOW_MASK - len;
  len += len_v;
  m[i++] = (byte)(len);

  
  m[i++] = (byte)RTCP_SDES_PRIV;
  m[i++] = (byte)(len_p);
  prefix.getBytes(0, len_p, m, i);
  i += len_p;
  
  value.getBytes(0, len_v, m, i);
  i += len_v;
  
  return i - offset;
}


protected static int
generate_SDES_Start(byte[] m, int offset, byte[] SSRCbytes) {

  System.arraycopy(SSRCbytes, 0, m, offset, 4);
  
  return 4;
}

protected static int
generate_SDES_End(byte[] m, int offset, int len) {
  int i;
  
  i = 4 - (len % 4);

  for(int n = 0; n < i; n++)
    m[offset + n] = 0;

  return i;
}

protected static int 
generate_BYE_Packet(byte[] m, int headeroffset, byte[] localSSRCbytes, int scc, byte[][] csrcbytes, String text) 
     //throws RTCP_Exception 
{
  int j, i = headeroffset;
  int len = text.length();
  if (len > RTCP_LOW_MASK)
    len = RTCP_LOW_MASK;

  m[i++] = (byte)(RTP_VERSION | RTCP_PADDING_NO | (RTCP_BC_MASK & (scc+1)));

  m[i++] = (byte)(RTCP_PT_BYE);

  int tmp = ((scc+1)<<2) + (len + 1);
  if (tmp%4 != 0)
    tmp = (tmp /4)+1;
  else
    tmp = tmp / 4;

  m[i++] = (byte)((RTCP_HIGH_MASK & tmp) >>> 010);
  m[i++] = (byte) (RTCP_LOW_MASK & tmp);

  System.arraycopy(localSSRCbytes, 0, m, i, 4);
  i += 4; 

  for (j = 0; j < scc; j++) {
    System.arraycopy(csrcbytes[j], 0, m, i, 4);
    i += 4;
  }

  m[i++] = (byte)len;
  text.getBytes(0, len, m, i);
  i += len; 

  tmp = 4-((len+1)%4);
  if (tmp != 4)
    for(j = 0; j < tmp; j++)
      m[i++] = 0;

  return i - headeroffset;  
}


protected static int 
generate_APP_Packet(byte[] m, int headeroffset, byte[] localSSRCbytes, int subtype, byte[] name, byte[] app_data, int len) 
{
  int j, i = headeroffset;

  m[i++] = (byte)(RTP_VERSION | RTCP_PADDING_NO | (RTCP_BC_MASK & subtype));

  m[i++] = (byte)(RTCP_PT_APP);

  int tmp = 8 + len;  // SSRC, name, app_data
  if (tmp%4 != 0)     // 4 byte alignment
    tmp = (tmp /4)+1; 
  else
    tmp = tmp / 4;

  m[i++] = (byte)((RTCP_HIGH_MASK & tmp) >>> 010);
  m[i++] = (byte) (RTCP_LOW_MASK & tmp);

  System.arraycopy(localSSRCbytes, 0, m, i, 4);
  i += 4; 

  System.arraycopy(name, 0, m, i, 4);
  i += 4; 
  
  System.arraycopy(app_data, 0, m, i, len);
  i += len; 

  tmp = 4-(len%4);
  if (tmp != 4)
    for(j = 0; j < tmp; j++)
      m[i++] = 0;

  return i- headeroffset;
}

protected static boolean
checkHeader(byte[] m, int offset) {
  int i = offset;
  
  // first byte
  if ((((int)m[i]) & RTP_VERSION_MASK) != RTP_VERSION) {
    System.err.println("ERROR: RTCProtocol.checkHeader():RTP_Version "+(((int)m[i])&RTP_VERSION_MASK)+" not correct");
    return false;
  }

  // second byte
  i++;
  if ((byte)(((int)m[i]) & RTCP_PT_MASK) != ((int)m[i])) {
    System.err.println("ERROR: RTCProtocol.checkHeader():RTCP_PT_Mask "+(((int)m[i])&RTCP_PT_MASK)+" not valid");    return false;
  }

  return true;
}

}

