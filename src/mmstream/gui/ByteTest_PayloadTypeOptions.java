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

import java.util.*;
import java.awt.*;


public class ByteTest_PayloadTypeOptions implements PayloadTypeOptions{

public ByteTest_PayloadTypeOptions() {
  panelbuilt = false;
  Options = null;
}

public String getTypeName() {
  return Name;
}

public Panel getPanel() {
  if (panelbuilt)
    return Options;

  GridBagLayout mainLO = new GridBagLayout();
  Options = new Panel();
  Options.setLayout(mainLO);

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;
  g_c.ipadx = 5;
  g_c.ipady = 5;


  Label l = new Label("ByteTest Options:", Label.CENTER);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  panelbuilt = true;

  return Options;
}

public Panel getPanel(PayloadType pt) {
  return this.getPanel();
}


public PayloadType createPayloadType(AppManager am)     throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("ByteTest_PayloadTypeOptions.createPayloadType():not initialised");

  Type pt = null;
  pt = am.getPayloadTypeHandlerTable().queryType(Name);
//  pt = SessionMapper.payloadTypeHandler.queryType(Name);
  if (pt == null) 
    throw new Gui_Exception("ByteTest_PayloadTypeOptions.createPayload(): no Type for "+Name);

  PayloadType ret = new PayloadType(pt.getName(), pt.getByteCode(), null, 1, -1, 100);

  return ret;
}

private Panel Options;
private boolean panelbuilt;

public static final String Name = new String(Payload_TypeConfiguration.PT_BYTE_TEST_Name);
}

