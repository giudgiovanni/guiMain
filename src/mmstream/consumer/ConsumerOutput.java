package mmstream.consumer;

import mmstream.consumer.*;
import mmstream.util.*;

import java.awt.*;


public interface ConsumerOutput extends Output {

public abstract void message(String text);
public abstract void error(String text);

public abstract void setDataOutput(Component p);

public abstract void notifyStateChange();

}
