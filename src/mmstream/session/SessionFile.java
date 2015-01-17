package mmstream.session;

import mmstream.util.*;
import mmstream.session.*;


import java.io.*;
import java.util.*;


public class SessionFile {

public SessionFile(Session ses, String name) {
  this(ses, new File(name));
}

public SessionFile(Session ses, File f) {
  file = f;
  fos = null;
  fis = null;
  session = ses;
}

public void close() throws Session_Exception {
  file = null;
  try {
    if (fis != null) {
      fis.close();
      fis = null;
    }
    if (fos != null) {
      fos.close();
      fos = null;
    }
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.close(): fs.close():: "+ioe.getMessage());
  }
}

public synchronized void
resetReading() throws Session_Exception {
  if (fos != null)
    throw new Session_Exception("SessionFile.resetReading: File already initialised for writing");

  if (fis == null)
    throw new Session_Exception("SessionFile.resetReading: File not initialised for reading");

  try {
    fis.seek(dataPos);
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.resetReading: fis.seek("+dataPos+"):: "+ioe.getMessage());
  }
}


public synchronized ClockSetData
startReading() throws Session_Exception {
  if (fos != null)
    throw new Session_Exception("SessionFile.startReading: File already initialised for writing");

  if (fis != null)
    throw new Session_Exception("SessionFile.startReading: File already initialised for reading");

  try {
    fis = new RandomAccessFile(file, "r");
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.startReading: fis=new RandomAccessFile():: "+ioe.getMessage());
  }

  String str = null;
  long l = 0;
  int i = 0;

  str = this.readString();  // should be MMSTREAM
  if (!str.equals("MMSTREAM"))
    throw new Session_Exception("SessionFile.startReading(): not a MMSTREAM file");

  str = this.readString();  // id
  //  System.out.println("Session id is "+str);

  str = this.readString();  // initiator
  //  System.out.println("Session initiator is "+str);

  str = this.readString();  // payload type
  if (!str.equals(session.getProfile().getPayloadType().getName()))
    throw new Session_Exception("SessionFile.startReading(): file payload type "+str+" equals not session payload type "+session.getProfile().getPayloadType().getName());

  l = this.readLong();  // time
  //  System.out.println("Session date is "+(new Date(l)).toString());

  ClockSetData csd = new ClockSetData();
  csd.rate = this.readLong();  // clock rate
  long secst = this.readLong();  // ticks
  long fractt = this.readLong();  // ticks
  csd.ntp = new NTPTimeStamp(secst, fractt);
  csd.stamp = this.readLong();  // equivalent stamp

  //  System.out.println("Rate is "+l+", "+((double)l));

  try {
    dataPos = fis.getFilePointer();
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.startReading(): fis.getFilePointer():: "+ioe.getMessage());
  }

  return (csd);
}

public synchronized void
finishWriting(ClockSetData csd) throws Session_Exception {    
  try {
    fos.seek(ratePos);
    fos.writeLong((long)csd.rate);
    fos.writeLong(csd.ntp.secs);
    fos.writeLong(csd.ntp.fract);
    fos.writeLong(csd.stamp);
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.finishWriting():: "+ioe.getMessage());
  }
}

public synchronized void 
startWriting() throws Session_Exception {
  ratePos = 0;
  if (fos != null)
    throw new Session_Exception("SessionFile.startWriting: File already initialised for writing");

  if (fis != null)
    throw new Session_Exception("SessionFile.startWriting: File already initialised for reading");

  try {
    fos = new RandomAccessFile(file, "rw");
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.startWriting: fos=new RandomAccessFile():: "+ioe.getMessage());
  }
    
  try {
    this.writeString("MMSTREAM");
    this.writeString(session.getId());
    this.writeString(session.getInitiator());
    this.writeString(session.getProfile().getPayloadType().getName());
    
    this.writeLong(System.currentTimeMillis());
    ratePos = fos.getFilePointer();
    this.writeLong(0); // dummy space for playout rate
    this.writeLong(0); // dummy space for ticks
    this.writeLong(0); // dummy space for ticks
    this.writeLong(0); // dummy space for stamp
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.startWriting(): fos.write():: "+ioe.getMessage());
  }
}


public boolean readChunk(Chunk p) throws Session_Exception {
  try {
    if (fis.length() == fis.getFilePointer())
      return false;
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.readChunk(): fis.getFilePointer()/fis.length():: "+ioe.getMessage());
  }

  try {
    p.timeStamp = this.readLong();
    p.data_length = this.readInt();
    if ((p.data_length + p.data_offset) > p.buffer.length)
      p.buffer = new byte[p.data_length + p.data_offset];
    int r = 0;
    int cr;
    while(r < p.data_length) {
      cr = fis.read(p.buffer, r, p.data_length - r);
      if (cr == -1)
	throw new Session_Exception("SessionFile.readChunk(): unexpected EOF reached");
      r += cr;
    }
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.readChunk():: ");
  }
  //  System.out.println("read "+p.data_length+" bytes from chunk at "+p.data_offset+", tS "+p.timeStamp);
  
  return true;
}

public void writeChunk(Chunk p) throws Session_Exception {
  if (fos == null)
    throw new Session_Exception("SessionFile.startWriting: File not initialised for writing");
  try {
    this.writeLong(p.timeStamp);
    this.writeInt(p.data_length);
    fos.write(p.buffer, p.data_offset, p.data_length);
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.startWriting():: "+ioe.getMessage());
  }
  // System.out.println("wrote "+p.data_length+" bytes from chunk at "+p.data_offset);
}
  
protected long readLong() throws Session_Exception {
//   long ret = 0;
//   for (short i = 7; i >= 0; i--) {
//     ret |= ((((long)fis.read()) & 0xffL) <<  (8*i));
//   }
//   return ret;
  long ret = 0;
  long tmp;
  try {
    for (short i = 7; i >= 0; i--) {
      tmp = fis.read();
      if (tmp == -1)
	throw new Session_Exception("SessionFile.readLong(): fis.read(): unexpected EOF reached");
      ret |= ((tmp& 0xffL) <<  (8*i));
    }
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.readLong(): fis.read():: "+ioe.getMessage());
  }
  
  //  System.out.println(" =L "+ret);
  return ret;
}

protected int readInt() throws Session_Exception {
  int ret = 0;
  int tmp;
  try {
    for (short i = 3; i >= 0; i--) {
      tmp = fis.read();
      if (tmp == -1)
	throw new Session_Exception("SessionFile.readInt(): fis.read(): unexpected EOF reached");
      ret |= ((tmp& 0xff) <<  (8*i));
    }
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.readInt(): fis.read():: "+ioe.getMessage());
  }

  //  System.out.println(" =I "+ret);
  return ret;
}

protected long writeLong(long ct) throws IOException {
  for (short i = 7; i >= 0; i--)
    fos.write((int) ((ct & (0xffL << (8*i))) >>> (8*i)));
  return 8;
}

protected long writeInt(int ct) throws IOException {
  for (short i = 3; i >= 0; i--)
    fos.write((int) ((ct & (0xff << (8*i))) >>> (8*i)));
  return 4;
}

protected String readString() throws Session_Exception {
  String ret = new String(""); 
  int len = 0;
  try {
    len = this.fis.read();
    if (len == -1)
      throw new Session_Exception("SessionFile.readString(): unexpected EOF reached");    
    if (len > 0) {
      if (len > buf.length)
	buf = new byte[len];
      int r = 0;
      int cr;
      while(r < len) {
	cr = fis.read(buf, r, len - r);
	if (cr == -1)
	  throw new Session_Exception("SessionFile.readString(): unexpected EOF reached");
	r += cr;
      }
    }
  }
  catch(IOException ioe) {
    throw new Session_Exception("SessionFile.readString(): fis.read():: "+ioe.getMessage());
  }
  
  ret = new String(buf, 0, 0, len);
  
  return ret;
}

protected long writeString(String str) throws IOException {
  int len = (127 & str.length());
  this.fos.write((byte)len);
  if (len > 0) {
    if (len > buf.length)
      buf = new byte[len];
    str.getBytes(0, len, buf, 0);
    fos.write(buf, 0, len);
  }
  return 1+len;
}

  File file;
  RandomAccessFile fos;
  RandomAccessFile fis;
  byte buf[] = new byte[256];
  Session session;
  long ratePos;
  long dataPos;
}

   
