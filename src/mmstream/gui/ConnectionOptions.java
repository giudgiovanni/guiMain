package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;


public interface ConnectionOptions {

public String getTypeName();

public Panel getPanel();
public Panel getPanel(String data, int maxData);

public abstract Connection createConnection() throws Gui_Exception;

}
