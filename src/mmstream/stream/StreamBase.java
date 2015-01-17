package mmstream.stream;

import mmstream.stream.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;

public abstract class StreamBase implements Stream {

  public 
  StreamBase() {
    chunk_queue = new Chunk_Queue(); 
    exporter = null;
    lock = new Lock(false);

    master = null;
    id = null;
    output = new Println_StreamOutput();
    copy = false;
    clock = null;

    // HACK
    maxNum = 25;
  }

  public 
  StreamBase(StreamExporter str) throws Stream_Exception {
    this();
    this.setStreamExporter(str);
  }

  public void 
  start() {
    Object first = chunk_queue.peak();
    if (first != null) {
      //      System.out.println("calling startLocal "+ ((Chunk)first).timeStamp+", "+((Chunk)first).buffer[((Chunk)first).data_offset]);
      clock.startLocalNow(((Chunk)first).timeStamp);
    }
    else {
      //      System.out.println("calling startLocalOnFirst");
      clock.startLocalOnFirst();
    }
    lock.open();
    //    importer.start();
  }

  public boolean
  isStarted() {
    return lock.query();
  }

  public void 
  stop() {
    //    importer.stop();
    lock.close();
  }

  public void 
  flush() {
    //    importer.flush();
    chunk_queue.clear();
    if (lock.query()) {
      Object first = chunk_queue.peak();
      if (first != null) {
	//      System.out.println("calling startLocal "+ ((Chunk)first).timeStamp+", "+((Chunk)first).buffer[((Chunk)first).data_offset]);
	clock.startLocalNow(((Chunk)first).timeStamp);
      }
      else {
	//    System.out.println("calling startLocalOnFirst");
	clock.startLocalOnFirst();
      }
    }
  }


  public Chunk 
  getChunk() throws Stream_Exception {
    lock.check();
    if((exporter == null) || 
       (chunk_queue.available() == 0 && 
	exporter.getValid() == false))
      throw new Stream_Exception("StreamBase.getChunk(): Exporter not valid");
    
    synchronized(chunk_queue) {
      while(chunk_queue.available() == 0) {
	try {
	  chunk_queue.wait();
	}
	catch(InterruptedException ie) {
	  ;
	}
	if(chunk_queue.available() == 0 && exporter.getValid() == false) {
	  System.err.println("ERROR: Exporter is not valid");
	  throw new Stream_Exception("StreamBase.getChunk(): Exporter not valid anymore");
	}
      }
    }

    return chunk_queue.extract();
  }


  public abstract Object
  getData() throws Stream_Exception;


  public TimedObject
  getTimedChunk() throws Stream_Exception {
    Chunk p = this.getChunk();
    return new TimedObject(p, clock.stamp2LocalMillis(p.timeStamp));
  }


  public abstract TimedObject
  getTimedData() throws Stream_Exception;


  public int available() {
    return chunk_queue.available();
  }

  public void
  alert() {
    //    System.err.println("ATTENTION: alert called on StreamBase");
    synchronized(chunk_queue) {
      chunk_queue.notifyAll();
    }
  }

  public void
  importChunk(Chunk p) {
    synchronized(chunk_queue) {
      if (chunk_queue.available() < maxNum)
	chunk_queue.insert(p);
    }
  }

  public StreamExporter
  getStreamExporter() {
    return exporter;
  }
  
  public void 
  setStreamExporter(StreamExporter exp) throws Stream_Exception {
    //    super.setExporter(exporter);
    if (exp.getValid() == true) {
      exporter = exp;
      //      exporter.registerStream(this);
    }
    else 
      throw new Stream_Exception("Chunk_Stream.setStreamExporter(): exporter not usable");

    clock = exporter.createSlaveClock();
  }

  
  public void close() {
    if (exporter != null)
      exporter.closeStream(this);
    chunk_queue.clear();
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id;}

  public Clock getClock() { return clock; }

  public StreamOutput getOutput() { return output; }
  public void setOutput(StreamOutput output) { this.output = output; }

  public Stream getMasterStream() { return master; }
  public void setMasterStream(Stream m) {
    master = m;
    if (master != null)
      clock.synchronizeStart(master.getClock().getClockSyncData());
  }

  public void setStoreSize(int c) { maxNum = c; }
  public int getStoreSize() { return maxNum; }

  public void setCopy(boolean c) { copy = c; }
  public boolean getCopy() { return copy; }

  protected StreamOutput output;
  protected String id;
  protected StreamExporter exporter;
  protected Stream master;
  protected boolean copy;
  protected Clock clock;
  protected Lock lock;
  protected Chunk_Queue chunk_queue; 
  protected int maxNum;
}
