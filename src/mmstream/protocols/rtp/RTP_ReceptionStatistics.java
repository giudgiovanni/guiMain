package mmstream.protocols.rtp;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;
import mmstream.protocols.rtp.*;

import java.util.*;


public class RTP_ReceptionStatistics extends ReceptionStatistics {

public 
RTP_ReceptionStatistics() {
  super();
  extHiSeq = 0;
  lastSR = 0;
  delayLastSR = 0;
}

public 
RTP_ReceptionStatistics(String c, String i, String s, long fl, long l, long j, long pr, long or, double bw, double pR, long ehs, long lsr, long dlsr) {
  super(c, i, s, fl, l, j, pr, or, bw, pR);
  
  extHiSeq = ehs;
  lastSR = lsr;
  delayLastSR = dlsr;
}

public long extHiSeq;
public long delayLastSR;
public long lastSR;
}
