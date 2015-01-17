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


public class RGB24_PayloadTypeOptions implements PayloadTypeOptions{

public RGB24_PayloadTypeOptions() {
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


  Label l = new Label("RGB 24bit Options:", Label.CENTER);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  l = new Label("Width:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  w_tf = new TextField("100", 15);
  w_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(w_tf, g_c);
  Options.add(w_tf);

  l = new Label("Height:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  h_tf = new TextField("100", 15);
  h_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(h_tf, g_c);
  Options.add(h_tf);


  panelbuilt = true;

  return Options;
}

public Panel getPanel(PayloadType pt) {
  if (!panelbuilt)
    this.getPanel();
  byte[] params = pt.getParams();
  int w = ((((int)params[0]) & 0xff) << 030) | ((((int)params[1]) & 0xff) << 020) | ((((int)params[2]) & 0xff) << 010) | (((int)params[3]) & 0xff);
  int h = ((((int)params[4]) & 0xff) << 030) | ((((int)params[5]) & 0xff) << 020) | ((((int)params[6]) & 0xff) << 010) | (((int)params[7]) & 0xff);
  w_tf.setText(String.valueOf(w));
  h_tf.setText(String.valueOf(h));

  return Options;
}


public PayloadType createPayloadType(AppManager am)     throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("RGB24_PayloadTypeOptions.createPayloadType():not initialised");

  Type pt = null;
//  pt = SessionMapper.payloadTypeHandler.queryType(Name);
  pt = am.getPayloadTypeHandlerTable().queryType(Name);
  if (pt == null) 
    throw new Gui_Exception("RGB24_PayloadTypeOptions.createPayload(): no Type for "+Name);

  int w = 0;
  int h = 0;
  try {
    w = Integer.parseInt(w_tf.getText());
    h = Integer.parseInt(h_tf.getText());
  }
  catch(NumberFormatException ne) {
    throw new Gui_Exception("RGB24_PayloadTypeOptions.createPayloadType():  Integer.parseInt():: "+ne.getMessage());
  }
  byte[] ret = new byte[9];
  int off = 0;
  for (int i = 3; i >= 0; i--)
    ret[off++] = (byte)((w & (0xff << (8*i))) >>> (8*i));
  for (int i = 3; i >= 0; i--)
    ret[off++] = (byte)((h & (0xff << (8*i))) >>> (8*i));
  ret[off++] = 24;

  PayloadType retpt = new PayloadType(pt.getName(), pt.getByteCode(), ret, 3, w*h*3, w*h*3);

  return retpt;
}

private Panel Options;
private boolean panelbuilt;

private TextField h_tf;
private TextField w_tf;

public static final String Name = new String(Payload_TypeConfiguration.PT_RGB24_Name);
}

