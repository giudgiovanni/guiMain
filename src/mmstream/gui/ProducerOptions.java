package mmstream.gui;

import mmstream.apps.*;
import mmstream.session.*;
import mmstream.producer.*;

import java.awt.*;


public interface ProducerOptions {

public abstract String getTypeName();

public abstract Panel getPanel(AppManager am);

public abstract ProducerControl createProducer(Session ses) throws Gui_Exception;

}
