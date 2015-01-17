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

public class File_Producer extends ProducerBase implements ProducerControl {

  public File_Producer(Session ses, StreamExporter os, String id, boolean rep, File fl, boolean sh) throws Session_Exception {
    super(ses, os, id);

    csd= new ClockSetData();
    repeat = rep;
    file = fl;

    fis = new SessionFile(session, file);
    csd = fis.startReading();
    streamExporter.getMasterClock().set(csd.rate, csd.stamp, NTPTimeStamp.fromMillis(System.currentTimeMillis()));
  }

public String getTypeName() { return Name; }

  public void run() {
    this.output.message("File_Producer() started");
    byte i;
    int j;
    
    int len = 0;
    
    //    this.output.message("File_Producer(): length is "+payloadLength+", freq "+sleeptime);
    Chunk sbuf = null;
    try {
      sbuf = new Chunk(lowerHeaderSize+(int)payloadLength);
    } catch(Chunk_Exception pe) {;}
    sbuf.data_offset = lowerHeaderSize;

    try {
      long wakeuptime = System.currentTimeMillis();
      do {
	fis.resetReading();
	while (fis.readChunk(sbuf)) {
	  try { 
	    Thread.sleep(this.getSleeptime() - System.currentTimeMillis() + wakeuptime);
	  }
	  catch (InterruptedException ie) {
	    //	    this.output.error("interrupted Sleep");
	    ;
	  }
	  wakeuptime = System.currentTimeMillis();
	  streamExporter.exportChunk(sbuf);
	  //     if (Thread.interrupted()) {
	  //       session.output.error("Thread interrupted");
	  //    }
	  sbuf = new Chunk(lowerHeaderSize+(int)payloadLength);
	}
      } while(repeat == true);
    }
    catch(Chunk_Exception pe) {;}
    catch(Stream_Exception ce) {
      this.output.error("EXCEPTION: File_Producer.run: streamExporter.exportChunk():: "+ce.getMessage());
      ce.printStackTrace();
      this.Finish("Error", true);    
    }
    catch (Session_Exception se) {
      this.output.error("EXCEPTION: File_Producer.run: :: "+se.getMessage());
      se.printStackTrace();
      this.Finish("Error", true);    
    }

    this.Finish("Data sent", true);    
  }
  
  public void Finish(String reason, boolean finishExporter) {
    super.Finish(reason, finishExporter);
    try {
      fis.close();
    }
    catch (Session_Exception se) {
      this.output.error("EXCEPTION: File_Producer.finish(): fis.close():: "+se.getMessage());
      se.printStackTrace();
    }
  }

  protected boolean repeat;
  public final static String Name = new String (Producer_TypeConfiguration.FILE_Name);

  private boolean useHeader;
  private File file;
  private SessionFile fis;
  protected ClockSetData csd;
}



