package mmstream.producer;

import mmstream.producer.*;
import mmstream.util.*;
import mmstream.session.*;

public interface ProducerOutput extends Output {

public abstract void message(String text);
public abstract void error(String text);

public abstract void notifyStateChange();

public abstract void notifyControlData(ControlData cd); 

}
