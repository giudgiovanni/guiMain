package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.config.*;

import java.awt.*;


public interface ProtocolCoDecOptions {

public String getTypeName();

public Panel getPanel(AppManager am);
public Panel getPanel(AppManager am, Profile p);

public abstract Config createConfig(PayloadType pt, Connection dataCon, Connection contCon) throws Gui_Exception;

}
