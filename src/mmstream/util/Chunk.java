package mmstream.util;

import mmstream.util.*;
import mmstream.address.*;

public class Chunk extends Object implements Cloneable {

public Chunk() {
  timeStamp = 0L;
  buffer = null;
  data_length = 0;
  data_offset = 0;
  header_offset = 0;
  sourceTransportAddress = null;
}


public Chunk(int size) throws Chunk_Exception {
  if (size < 0) 
    throw new Chunk_Exception("Chunk(): size < 0");

  timeStamp = 0L;
  buffer = new byte[size];
  header_length = 0;
  data_length = 0;
  data_offset = 0;
  header_offset = 0;
  sourceTransportAddress = null;
}


public Chunk(int header_s, int data_s) throws Chunk_Exception {
  if (header_s < 0 || data_s < 0) 
    throw new Chunk_Exception("Chunk(): size < 0");

  timeStamp = 0L;
  buffer = new byte[data_s+header_s];
  data_length = data_s;
  header_length = header_s;
  data_offset = header_s;
  header_offset = 0;
  sourceTransportAddress = null;
}


public Chunk(byte[] b, int offset, int header_s, int data_s) throws Chunk_Exception {
  if (header_s < 0 || data_s < 0 || offset < 0 ||
      b == null || b.length < header_s + data_s + offset) 
    throw new Chunk_Exception("Chunk(): size < 0");

  timeStamp = 0L;
  buffer = b;
  header_offset = offset;
  data_length = data_s;
  header_length = header_s;
  data_offset = header_s+offset;
}


public Chunk(byte[] b, int header_s, int data_s) throws Chunk_Exception {
  if (header_s < 0 || data_s < 0 ||
      b == null || b.length < header_s + data_s) 
    throw new Chunk_Exception("Chunk(): size < 0");

  timeStamp = 0L;
  buffer = b;
  header_length = header_s;
  data_length = data_s;
  data_offset = header_s;
  header_offset = 0;
  sourceTransportAddress = null;
}


public Chunk(byte[] b, int h_s) throws Chunk_Exception {
  if (h_s < 0 || b == null || h_s > b.length )
    throw new Chunk_Exception("Chunk(): wrong sizes");

  timeStamp = 0L;
  buffer = b;
  header_length = h_s;
  data_length = b.length - h_s;
  data_offset = h_s;
  header_offset = 0;
  sourceTransportAddress = null;
}


public Chunk(byte[] b) throws Chunk_Exception {
  if (b == null )
    throw new Chunk_Exception("Chunk(): wrong sizes");

  timeStamp = 0L;
  buffer = b;
  data_length = b.length;
  data_offset = 0;
  header_offset = 0;
  sourceTransportAddress = null;
}

public Object 
clone() {
  Chunk ret = new Chunk();
  
  
  ret.timeStamp = this.timeStamp;
  ret.header_offset = this.header_offset;
  ret.header_length = this.header_length;
  ret.data_length = this.data_length;
  ret.data_offset = this.data_offset;
  ret.arrival = this.arrival;
  try {
    ret.buffer = (byte[])(this.buffer.clone());
    ret.sourceTransportAddress = (Address)(this.sourceTransportAddress.clone());
  }
  catch(CloneNotSupportedException ce) {
    ;
  }

  return ret;
}

public long timeStamp;
public int header_offset;
public int header_length;
public byte buffer[];
public int data_length;
public int data_offset;
public Address sourceTransportAddress;
public long arrival;
}
