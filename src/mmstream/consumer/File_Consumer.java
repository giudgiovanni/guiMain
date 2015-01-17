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

public class File_Consumer extends ConsumerBase implements ConsumerControl {

  public File_Consumer(AppManager str, Session s, String i, File fl) {
    super(str,s,i);

    csd = new ClockSetData();

    file = fl;

    this.setOutput(new Println_ConsumerOutput());

    fos = null;
    try {
      fos = new SessionFile(session, file);
      fos.startWriting();
    }
    catch(Session_Exception ioe) {
      this.output.error("EXCEPTION:File_Consumer(): SessionFile("+file.getPath()+"):: "+ioe.getMessage());
      ioe.printStackTrace();
      try {
      	fos.close();
      }
      catch (Session_Exception ie) {;}
      System.exit(1);
    }
    copy = false;
    pos = false;
    this.setState("Not ready");

  }

  public File_Consumer(AppManager str, Session s, StreamExporter exp, String i, File fl) {
    this(str, s, i, fl);

    se = exp;
    this.setState("Runnable");
  }



  public void 
  run() {
    int i = 0;
    Chunk chunk = null;
    int ret;
    
    
    String str = null;
    int len = 0;
    byte[] buf = new byte[256];




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
      this.output.error("EXCEPTION:File_Consumer.run(): (Chunk_Stream)StreamManager.importStream(session,se,mmstream.stream.Chunk_Stream):: "+e.getMessage());
      e.printStackTrace();
      this.setState("Finished");
      this.output.notifyStateChange();
      this.Finish();
      return;
    }

    if (this.getState().equals("Running")) {
      this.setState("Running");
      bs.start();
    }
    else
      this.setState("Suspended");

    this.output.notifyStateChange();

    this.output.message("Connected to stream "+bs.getId()+", source "+se.getId());

    try {
      chunk = bs.getChunk();
      this.output.message("Read " + 0 + ", value " + chunk.buffer[12]);
      csd.stamp = chunk.timeStamp;
      for(i = 0; true; i ++)  {
	fos.writeChunk(chunk);
	this.output.message("Wrote " + i + ", value " + chunk.buffer[12]);
	chunk = bs.getChunk();
	this.output.message("Read " + i + ", value " + chunk.buffer[12]);
      }

    }
    catch (Stream_Exception e) {
      this.output.error("EXCEPTION: File_Consumer.run(): Chunk_Stream.getData(): "+e.getMessage());
      e.printStackTrace();
    }
    catch (Session_Exception e) {
      this.output.error("EXCEPTION: File_Consumer.run(): SessionFile.writeChunk(): "+e.getMessage());
      e.printStackTrace();
    }


    this.setState("Finished");
    this.output.notifyStateChange();
    this.Finish();    
  }       

  public synchronized void Finish() {
    // clean up
    try {
      csd.rate = se.getMasterClock().getRate();
      csd.ntp = new NTPTimeStamp(0,0);

      fos.finishWriting(csd);
      fos.close();
    } catch(Session_Exception ie) {;}
    if (bs != null)
      bs.close();
  }

  public String
  getTypeName() { return Name; }

  public final static String Name = new String (Consumer_TypeConfiguration.FILE_Name);
  private File file;
  private SessionFile fos;
  private ClockSetData csd;
}


