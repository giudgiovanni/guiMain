package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.session.*;

import java.awt.*;


public interface SessionMapperOptions {

public String getName();

public Panel getPanel();

public abstract SessionMapper createSessionMapper(AppManager am) throws Gui_Exception;

}
