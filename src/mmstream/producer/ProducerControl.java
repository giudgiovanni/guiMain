package mmstream.producer;

import mmstream.util.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.producer.*;

public interface ProducerControl extends Runnable {

public abstract ControlData setMaxBandwidth(long nw);
public abstract long getMaxBandwidth();
public abstract long getUsedBandwidth();

public abstract ControlData setPayloadLength(long nw);
public abstract long getPayloadLength();

public abstract ControlData setChunkRate(double nw);
public abstract double getChunkRate();

public abstract ControlData setControlData(ControlData cd);
public abstract ControlData getControlData();

public abstract String getState();
// public abstract void setState(String s);

public abstract int getPriority();
public abstract void setPriority(int s);

public abstract String getTypeName();
public abstract String getId();
//public abstract LocalSource getSource();
public abstract StreamExporter getStreamExporter();

public abstract ProducerOutput getOutput();
public abstract void setOutput(ProducerOutput out);

public abstract void Stop();
public abstract void Start();
public abstract void Finish(String reason, boolean finishStreamExporter);
}
