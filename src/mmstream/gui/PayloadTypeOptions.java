package mmstream.gui;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.config.*;

import java.awt.*;


public interface PayloadTypeOptions {

public String getTypeName();

public Panel getPanel(PayloadType pt);
public Panel getPanel();

public abstract PayloadType createPayloadType(AppManager am) throws Gui_Exception;

}
