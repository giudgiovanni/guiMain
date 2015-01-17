package mmstream.util;

public class Lock extends Object {
  
  public Lock() {
    this.val = false;
  }

  public Lock(boolean val) { 
    this.val = val;
  }

  public synchronized void
  open() {
    this.val = true;
    this.notifyAll();
  }

  public synchronized void
  close() {
    this.val = false;
    this.notifyAll();
  }

  public synchronized void
  check() {
    while (this.val == false) {
      try {
	this.wait();
      }
      catch(InterruptedException ie) {;}
    }
  }

  public synchronized boolean
  query() { return val; }

  private boolean val;
}
    
  
