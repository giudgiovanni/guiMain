package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.session.*;

public interface SessionOutput extends Output {

public abstract void message(String text);
public abstract void error(String text);

public abstract void notifyLocalSource(LocalSource s);
public abstract void notifyRemoteSource(RemoteSource s);

public abstract void notifyStateChange();

public abstract void notifyControlData(ControlData cd);
}
