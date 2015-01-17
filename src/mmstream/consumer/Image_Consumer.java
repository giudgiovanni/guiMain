package mmstream.consumer;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.consumer.*;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Image_Consumer extends ConsumerBase implements ConsumerControl {

  public Image_Consumer(AppManager str, Session s, String i) {
    super(str, s, i);

    profile = session.getProfile();

    byte[] params = session.getProfile().getPayloadType().getParams();
    if (params != null) {
      width = ((((int)params[0]) & 0xff) << 030) | ((((int)params[1]) & 0xff) << 020) | ((((int)params[2]) & 0xff) << 010) | (((int)params[3]) & 0xff);
      height = ((((int)params[4]) & 0xff) << 030) | ((((int)params[5]) & 0xff) << 020) | ((((int)params[6]) & 0xff) << 010) | (((int)params[7]) & 0xff);
      depth = (int)params[8];
    }
    else {
      depth = 8;
      width = (int)Math.sqrt(session.getProfile().getDataLength());
      height = (int)Math.sqrt(session.getProfile().getDataLength());
    }

    cv = new ImageCanvas(width, height);
  }


  public Image_Consumer(AppManager str, Session s, StreamExporter exp, String i) {
    this(str, s, i);
    se = exp;
    this.setState("Runnable");
  }




  public void run() {
    int i, iL=0;
    MemoryImageSource rbuf = null;
    TimedObject to = null;
    int ret;
    long lastT=0, T=0;
    try { 
      if (se == null) {
// 	this.setState("Listening");
//	this.getOutput().notifyStateChange();
	bs = (MemoryImageSource_Stream)(sessionManager.importStream(session, "mmstream.stream.MemoryImageSource_Stream"));
	se = bs.getStreamExporter(); 
// 	this.setState("Runnable");
      }
      else {
	bs = (MemoryImageSource_Stream)(streamManager.importStream(se, "mmstream.stream.MemoryImageSource_Stream"));

      }
      bs.setCopy(copy);
    }
    catch(Stream_Exception e) {
      this.output.error("EXCEPTION:Image_Consumer.run(): (Image_Stream)StreamManager.importStream(codec,mmstream.stream.MemoryImageSource_Stream):: "+e.getMessage());
      e.printStackTrace();
      this.setState("Finished");
      this.output.notifyStateChange();
      this.Finish();
      return;
    }

    if (this.getState().equals("Running")) {
      bs.start();
    }
    else
      this.setState("Suspended");

    this.output.notifyStateChange();

    this.output.message("Connected to stream "+bs.getId()+", source "+se.getId()+", width "+width+", height "+height+", depth "+depth);

    ((MemoryImageSource_Stream)bs).setDimension(new Dimension(width, height));
    if (depth == 24)
      ((MemoryImageSource_Stream)bs).setColorModel(new DirectColorModel(depth, 0xff0000, 0x00ff00, 0x0000ff));
    else if (depth == 8)
      ((MemoryImageSource_Stream)bs).setColorModel(new DirectColorModel(depth, 0xc0, 0x38, 0x07));


    this.output.setDataOutput(cv);

    try {
      for(i = 0; true; i ++)  {
	//	this.output.message("Starting Round " + i);
	if (pos) {
	  to = bs.getTimedData();
	  rbuf = (MemoryImageSource)to.object;
	  to.sync();
	}
	else {
	  rbuf = (MemoryImageSource)bs.getData();
	}
	cv.newImage(rbuf);
	//	this.output.message("Ending Round " + i);

	// output the current frame rate every 64 frames
	if ((i & 0x1f) == 0) {
	  T = System.currentTimeMillis();
	  System.out.println("Round "+i+", frame rate "+32.0F/((float)(T-lastT)) );
	  this.output.message("Round "+i+", frame rate "+32.0F/((float)(T-lastT)) );
	  iL = i; lastT=T;
	}
      }
    }                     
    catch (Stream_Exception e) {
      this.output.error("EXCEPTION: Image_Consumer.run(): MemoryImageSource_Stream.getData():: "+e.getMessage());
      e.printStackTrace();
    }
    this.setState("Finished");
    this.output.notifyStateChange();
    this.Finish();    
    
  } 
      
  public String getTypeName() { return Name; }

  protected Profile profile;
  protected int width;
  protected int height;
  protected int depth;
  protected ImageCanvas cv;
  public final static String Name = new String (Consumer_TypeConfiguration.IMAGE_Name);
}


