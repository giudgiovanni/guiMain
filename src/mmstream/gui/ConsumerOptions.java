package mmstream.gui;

import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.consumer.*;

import java.awt.*;


public interface ConsumerOptions {

public abstract String getTypeName();

public abstract Panel getPanel(AppManager am);

public abstract ConsumerControl createConsumer(Session ses) throws Gui_Exception;

public abstract ConsumerControl createConsumer(Session ses, StreamExporter exp) throws Gui_Exception;

}
