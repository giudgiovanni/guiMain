package mmstream.source;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.producer.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.stream.*;

import java.util.*;

public abstract class LocalSource extends Source implements StreamExporter {
  
public  
LocalSource(ProtocolCoDec c) {
  super(c);

  octetsSent = new Counter();
  chunksSent = new Counter();
  startTime = 0;
  send = true;
}

// public abstract void put(byte[] m, int reserved, int length, long timeStamp) throws CoDec_Exception;

// public abstract void put(byte[] m, int reserved, int length) throws CoDec_Exception;

public void
exportChunk(Chunk p) throws Stream_Exception {
  if (p.timeStamp == 0) 
    p.timeStamp = clock.getStamp();
    
  if (send) {
    try {
      //      this.putChunk(p.buffer, p.data_offset, p.data_length, p.timeStamp);
      codec.transmitChunk(this, p);
    }
    catch(CoDec_Exception ce) {
      throw new Stream_Exception("LocalSource.exportChunk(): this.putChunk():: "+ce.getMessage());
    }
    this.output.notifySenderStatistics(this.getSenderStats());
  }
  super.exportChunk(p);
}  

public void setSendPermission(boolean v) {
  send = v;
}

public boolean getSendPermission() {
  return send;
}


protected void 
fillInSenderStats(SenderStatistics stat) {
  stat.chunksSent = chunksSent.query();
  stat.octetsSent = octetsSent.query();
}

  
  // utilities
protected long startTime;
public Counter octetsSent;
public Counter chunksSent;
protected boolean send;
}
