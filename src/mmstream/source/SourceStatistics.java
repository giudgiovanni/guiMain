package mmstream.source;



public class SourceStatistics extends Object {

public SourceStatistics() {
  state = null;
  fractionLost = 0;
  totalLost = 0;
  jitter = 0;
  chunksClaimed = 0;
  octetsClaimed = 0;
  chunksReceived = 0;
  octetsReceived = 0;
  cname = null;
  id = null;
}

public SourceStatistics(String c, String i, String s, byte fl, int l, int j, int p, int o, int pr, int or) {
  cname = c;
  id = i;
  state = s;
  fractionLost = fl;
  totalLost = l;
  jitter = j;
  chunksClaimed = p;
  octetsClaimed = o;
  chunksReceived = pr;
  octetsReceived = or;
}

public String id;
public String cname;
public String state;
public int chunksClaimed;
public int octetsClaimed;
public int chunksReceived;
public int octetsReceived;
public byte fractionLost;
public int totalLost;
public int jitter;
}
