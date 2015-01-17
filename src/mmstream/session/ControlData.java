package mmstream.session;

public class ControlData {

public ControlData() {
  chunkRate = 0;
  bandwidth = 0;
  payloadLength = 0;
  adjust = false;
}
public void setAdjust(boolean nw) { adjust = nw; }
public boolean getAdjust() { return adjust; }

public void setMaxBandwidth(long nw) { bandwidth = nw; }
public long getMaxBandwidth() { return bandwidth; }

public void setPayloadLength(long nw) { payloadLength = nw; }
public long getPayloadLength() { return payloadLength; }

public void setChunkRate(double pr) { chunkRate = pr; }
public double getChunkRate() { return chunkRate; }

protected double chunkRate;
protected long payloadLength;
protected long bandwidth;
protected boolean adjust;
}
