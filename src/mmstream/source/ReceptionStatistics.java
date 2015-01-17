package mmstream.source;



public class ReceptionStatistics extends Object {

public ReceptionStatistics() {
  state = null;
  fractionLost = 0;
  totalLost = 0;
  jitter = 0;
  chunksReceived = 0;
  octetsReceived = 0;
  cname = null;
  id = null;
  bandwidth = 0;
}

public ReceptionStatistics(String c, String i, String s, long fl, long l, long j, long pr, long or, double bw, double pR) {
  cname = c;
  id = i;
  state = s;
  fractionLost = fl;
  totalLost = l;
  jitter = j;
  chunksReceived = pr;
  octetsReceived = or;
  bandwidth = bw;
  chunkRate = pR;
}

public String id;
public String cname;
public String state;
public long chunksReceived;
public long octetsReceived;
public long fractionLost;
public long totalLost;
public long jitter;
public double bandwidth;
public double chunkRate;
}
