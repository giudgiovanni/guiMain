package mmstream.consumer;

import mmstream.apps.*;
import mmstream.session.*;
import mmstream.stream.*;

public abstract class ConsumerBase extends Thread implements ConsumerControl {

  public ConsumerBase(AppManager am, Session s, String i) {
    streamManager = am.getStreamManager();
    sessionManager = am.getSessionManager();
    session = s;
    id = i;
    se = null;
    bs = null;

    this.setOutput(new Println_ConsumerOutput());
    pos = false;
    copy = false;
    this.setState("Not ready");
  }

  public ConsumerBase(AppManager str, Session s, StreamExporter exp, String i) {
    this(str, s, i);
    se = exp;
    this.setState("Runnable");
  }

  @Override
  public synchronized void Finish() {
    if (bs != null)
      bs.close();
    this.setState("Finished");
    this.output.notifyStateChange();
  }


  @Override
  public abstract void run();

  @Override
  public synchronized void 
  Start() {
    if (state.equals("Running") || state.equals("Finished"))
      return;
    if (bs != null)
      bs.setMasterStream(null);
    if (state.equals("Runnable")) {
      this.setState("Running");
      this.start(); // start the Thread
    }
    if (state.equals("Not ready")) {
      this.setState("Running");
      this.start();
    }
    if (state.equals("Suspended")) {
      this.setState("Running");
      this.resume();
    }
    this.output.notifyStateChange();
  }

  @Override
  public synchronized void 
  Stop() {
    if (state.equals("Suspended") || 
// 	state.equals("Listening") || 
	state.equals("Finished"))
      return;
    if (bs != null)
      bs.setMasterStream(null);
    if (state.equals("Runnable") || state.equals("Running")) {
      this.suspend();
      this.setState("Suspended");
    }
    this.output.notifyStateChange();
  }


 
  @Override
  public String getId() { return id; }

  public Stream 
  getStream() { return bs; }


  public StreamExporter 
  getStreamExporter() { 
    return se;
  }

  public synchronized void setState(String s) { 
    state = s; 
    if (bs != null) {
      if (state.equals("Running"))
	bs.start();
      if (state.equals("Suspended"))
	bs.stop();
      this.output.notifyStateChange();
    }
  }

  
  public String getState() { return state; }

  public synchronized ConsumerOutput getOutput() { return this.output; }
  public synchronized void setOutput(ConsumerOutput out) { this.output = out; }

  public boolean getPlayOutSync() { return pos; }
  public synchronized void setPlayOutSync(boolean s) { pos = s; }

  protected boolean pos;

  public boolean getCopy() { return copy; }
  public synchronized void setCopy(boolean s) { copy = s; if (bs != null) bs.setCopy(copy); }

  protected boolean copy;

  protected Session session;

  protected Stream bs;
  protected StreamExporter se;
  protected int priority;
  protected ConsumerOutput output;
  protected SessionManager sessionManager;
  protected StreamManager streamManager;
  protected String id;
  protected String state;
//  public final static String Name = new String (Consumer_TypeConfiguration.BYTE_Name);
}


 
