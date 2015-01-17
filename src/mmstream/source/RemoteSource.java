package mmstream.source;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.stream.*;

import java.util.*;

public abstract class RemoteSource extends Source implements StreamExporter {
  
  public  
  RemoteSource(ProtocolCoDec c) {
    super(c);
    octetsSent = 0;
    chunksSent = 0;
    octetsReceived = 0;
    chunksReceived = 0;
    jitterEstimation = 0;
    startTime = System.currentTimeMillis();
  }


  protected void 
  fillInSenderStats(SenderStatistics stat) {
    stat.chunksSent = chunksSent;
    stat.octetsSent = octetsSent;
  }

  public abstract ReceptionStatistics 
  getReceptionStats();

  protected void 
  fillInReceptionStats(ReceptionStatistics stat) {
    stat.cname = this.getCname();
    stat.id = this.getId();
    stat.state = this.getState();

    stat.jitter = jitterEstimation;
    stat.chunksReceived = chunksReceived;
    stat.octetsReceived = octetsReceived;
  }

 protected long startTime;
 protected long jitterEstimation;
 protected long octetsSent;
 protected long chunksSent;

 protected long octetsReceived;
 protected long chunksReceived;


}
