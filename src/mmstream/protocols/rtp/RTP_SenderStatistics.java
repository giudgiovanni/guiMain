package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;

import java.util.*;


public class RTP_SenderStatistics extends SenderStatistics {

public 
RTP_SenderStatistics() {
  super();
  lastSR = 0;
  extHiSeq = 0;
}

public 
RTP_SenderStatistics(long p, long o, float bw, long ehs, long lsr, double pr) {
  super(p, o, bw, pr);

  extHiSeq = ehs;
  lastSR = lsr;
}

public long extHiSeq;
public long lastSR;
}
