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

import java.net.*;
import java.io.*;
import java.util.*;

public class Byte_Producer extends ProducerBase implements ProducerControl {

  public Byte_Producer(Session ses, StreamExporter os, byte max, boolean rep, String id) throws Session_Exception {
    //  super(tg, "Byte_Producer");
    super(ses,os,id);
    maxval = max;
    repeat = rep;
  }


public String getTypeName() { return Name; }

  public void run() {
    this.output.message("Byte_Producer() started");
    long lastT=0, T=0;
    byte i;
    int j;
    long cst = 0;
    long wakeuptime = System.currentTimeMillis();
    //    this.output.message("Byte_Producer(): length is "+payloadLength+", maxval is "+ maxval+", freq "+sleeptime);
    //    byte[] sbuf = new byte[lowerHeaderSize+(int)session.getProfile().getPayloadType().getMaxSize()];
    Chunk sbuf = null;
    long maxPlace = lowerHeaderSize + payloadLength;
    try {
      do {
	for(i = 0; i < maxval; i ++)  {
	  maxPlace = lowerHeaderSize + payloadLength;
	  sbuf = new Chunk(lowerHeaderSize, (int)payloadLength);
	  for(j = lowerHeaderSize; j < maxPlace; sbuf.buffer[j++] = i);
	  
	  cst = this.sleeptime - (System.currentTimeMillis() - wakeuptime);
	  if (cst > 0) {
	    try { 
	      Thread.sleep(cst);
	    }
	    catch (InterruptedException ie) {
	      //	    this.output.error("interrupted Sleep");
	      ;
	    }
	  }
	  wakeuptime = System.currentTimeMillis();
	  //	  localSource.put(sbuf, lowerHeaderSize, (int)payloadLength);
	  streamExporter.exportChunk(sbuf);
	}
	// output the current chunk rate every 64 chunks
	T = System.currentTimeMillis();
	System.out.println("Round "+i+", chunk rate "+maxval/((float)(T-lastT)) );
	this.output.message("Round "+i+", chunk rate "+maxval/((float)(T-lastT)) );
	lastT=T;
	//     if (Thread.interrupted()) {
	//       session.output.error("Thread interrupted");
	//    }
      } while(repeat == true);
    } 
    catch(Stream_Exception ce) {
      this.output.error("EXCEPTION: Byte_Producer.run: streamExporter.exportChunk():: "+ce.getMessage());
      ce.printStackTrace();
      this.Finish("Error", true);    
    }
    catch(Chunk_Exception ce) {
      this.output.error("EXCEPTION: Byte_Producer.run: new Chunk("+lowerHeaderSize+","+payloadLength+"):: "+ce.getMessage());
      ce.printStackTrace();
      this.Finish("Error", true);    
    }

    this.Finish("Data sent", true);    
	
  }       

  protected byte maxval;
  protected boolean repeat;
  public final static String Name = new String (Producer_TypeConfiguration.BYTE_Name);
}



