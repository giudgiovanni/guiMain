package mmstream.consumer;

import mmstream.util.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.consumer.*;
import mmstream.source.*;

public interface ConsumerControl extends Runnable {

public abstract String getState();
// public abstract void setState(String s);

public abstract int getPriority();
public abstract void setPriority(int s);

public abstract boolean getPlayOutSync();
public abstract void setPlayOutSync(boolean s);

public abstract boolean getCopy();
public abstract void setCopy(boolean s);

public abstract String getTypeName();
public abstract String getId();
public abstract Stream getStream();
public abstract StreamExporter getStreamExporter();

public abstract ConsumerOutput getOutput();
public abstract void setOutput(ConsumerOutput out);

public abstract void Finish();
public abstract void Start();
public abstract void Stop();
}
