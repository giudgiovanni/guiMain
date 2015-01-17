package mmstream.consumer;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.session.*;
import mmstream.consumer.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.session.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class Byte_Consumer extends ConsumerBase implements ConsumerControl {

  public Byte_Consumer(AppManager str, Session s, String i) {
    super(str,s,i);
  }

  public Byte_Consumer(AppManager str, Session s, StreamExporter exp, String i) {
    this(str, s, i);
    se = exp;
    this.setState("Runnable");
  }

  public void 
  run() {
    int i, iL=0;
    Chunk rc = null;
    TimedObject ro = null;
    int ret;
    long lastT=0, T=0;
    
    //    this.output.message("LISTEN for stream");
     
    try { 
      if (se == null) {
	bs = (Chunk_Stream)(sessionManager.importStream(session, "mmstream.stream.Chunk_Stream"));
	se = bs.getStreamExporter();
      }
      else {
	bs = (Chunk_Stream)(streamManager.importStream(se, "mmstream.stream.Chunk_Stream"));
      }
      bs.setCopy(copy);
    }
    catch(Stream_Exception e) {
      this.output.error("EXCEPTION:Byte_Consumer.run(): (Chunk_Stream)StreamManager.inportStream(session,mmstream.stream.Chunk_Stream):: "+e.getMessage());
      e.printStackTrace();
      this.setState("Finished");
      this.output.notifyStateChange();
      this.Finish();
      return;
    }
    
    // TODO
    if (this.getState().equals("Running")) {
      this.setState("Running");
      bs.start();
    }
    else
      this.setState("Suspended");

    this.output.notifyStateChange();

    this.output.message("Connected to stream "+bs.getId()+", source "+se.getId());

    try {
      for(i = 0; true; i ++)  {
	if (pos) {
	  ro = bs.getTimedChunk();
	  rc = (Chunk)ro.object;
	  ro.sync();
	}
	else {
	  rc = bs.getChunk();
	}
	this.output.message("Round " + i + ", value " + 
				   rc.buffer[rc.data_offset]  
				   );
	// output the current chunk rate every 64 chunks
	if ((i & 0x7f) == 0) {
	  T = System.currentTimeMillis();
	  System.out.println("Round "+i+", chunk rate "+128.0F/((float)(T-lastT)) );
	  this.output.message("Round "+i+", chunk rate "+128.0F/((float)(T-lastT)) );
	  iL = i; lastT=T;
	}

      }
    }
    catch (Stream_Exception e) {
      this.output.error("EXCEPTION: Byte_Consumer.run(): Chunk_Stream.getData(): "+e.getMessage());
      e.printStackTrace();
    }
    this.setState("Finished");
    this.output.notifyStateChange();
    this.Finish();    
  }       

  public String
  getTypeName() { return Name; }

  public final static String Name = new String (Consumer_TypeConfiguration.BYTE_Name);
}


