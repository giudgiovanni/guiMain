package mmstream.source;



public class SenderStatistics extends Object {

public SenderStatistics() {
  chunksSent = 0;
  octetsSent = 0;
  bandwidth = 0;
}

public SenderStatistics(long p, long o, double bw, double pr) {
  chunksSent = p;
  octetsSent = o;
  bandwidth = bw;
  chunkRate = pr;
}

public long chunksSent;
public long octetsSent;
public double bandwidth;
public double chunkRate;
}
