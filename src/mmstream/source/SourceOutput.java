package mmstream.source;

import mmstream.source.*;
import mmstream.gui.*;
import mmstream.address.*;

import java.awt.*;

public interface SourceOutput  {

public abstract void message(String text);
public abstract void error(String text);

public abstract void notifyAddress(Address data, Address control);
public abstract void notifySourceDescription();
public abstract void notifyStateChange();

public abstract void notifyReceptionStatistics(ReceptionStatistics srcstat);
public abstract void notifySenderStatistics(SenderStatistics srcstat);

}
