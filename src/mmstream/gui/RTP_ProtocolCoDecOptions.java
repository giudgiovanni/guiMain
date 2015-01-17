package mmstream.gui;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.util.*;
import java.awt.*;


public class RTP_ProtocolCoDecOptions implements ProtocolCoDecOptions{

public RTP_ProtocolCoDecOptions() {
  panelbuilt = false;
  appMan = null;
  Options = null;
}

public String getTypeName() {
  return Name;
}

public Panel getPanel(AppManager am) {
  if (panelbuilt)
    return Options;

  appMan = am;
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


  Label l = new Label("RTP Options:", Label.CENTER);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  l = new Label("Max Session Bandwith:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  bw_tf = new TextField("30000", 15);
  bw_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(bw_tf, g_c);
  Options.add(bw_tf);

  l = new Label("Max Chunk Rate:", Label.LEFT);
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  pr_tf = new TextField("20", 10);
  pr_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(pr_tf, g_c);
  Options.add(pr_tf);

  l = new Label("Max Chunk Length:", Label.LEFT);
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  len_tf = new TextField("10000", 10);
  len_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(len_tf, g_c);
  Options.add(len_tf);

  
  l = new Label("Clock Rate:", Label.LEFT);
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  rate_tf = new TextField("1000", 10);
  rate_tf.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(rate_tf, g_c);
  Options.add(rate_tf);

  
  // the extension choice
  l = new Label("Extension Type:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  extChoice = new Choice();
  extChoice.addItem(EXT_NO_Name);
  extChoice.select(EXT_NO_Name);
  g_c.gridx = 1;
  mainLO.setConstraints(extChoice, g_c);
  Options.add(extChoice);
	
  panelbuilt = true;

  return Options;
}

public Panel getPanel(AppManager am, Profile p) {
  if (!panelbuilt)
    this.getPanel(am);

  bw_tf.setText(String.valueOf(p.getSessionBandwidth()));
  len_tf.setText(String.valueOf(p.getDataLength()));
  pr_tf.setText(String.valueOf(p.getChunkRate()));
  rate_tf.setText(String.valueOf(p.getClockRate()));

  return Options;
}

public Config createConfig(PayloadType pt, Connection dataCon, Connection contCon)     throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("RTP_ProtocolCoDecOptions.createConfig():not initialised");

  long bw;
  int len;
  float pr;
  float rate;

  try {
    bw = Long.parseLong(bw_tf.getText());
    len = Integer.parseInt(len_tf.getText());
    pr = (Float.valueOf(pr_tf.getText())).floatValue();
    rate = (Float.valueOf(rate_tf.getText())).floatValue();
  }
  catch(NumberFormatException ne) {
    throw new Gui_Exception("RTP_ProtocolCoDecOptions.createConfig(): Long.parseLong() / Integer.parseInt():: "+ne.getMessage());
  }
//   if (len < pt.getMinSize())
//     throw new Gui_Exception("RTP_ProtocolCoDecOptions.createConfig(): data length "+len+" < PayloadType.minSize() "+pt.getMinSize());
  
  int ext = 0;
  String tmp;

  tmp = extChoice.getSelectedItem();
  if (tmp.equals(EXT_NO_Name))
    ext = RTProtocol.RTP_EXTENSION_NO;

  RTP_Profile p = new RTP_Profile(ext, pt, len, bw, pr, rate);
  RTP_CoDec codec = new RTP_CoDec(dataCon, contCon, (RTP_Profile)p, appMan);
  Config config = new Config(appMan, p, codec, dataCon, contCon);
  
  return config;
}

private Panel Options;
private Choice extChoice;

private TextField bw_tf;
private TextField pr_tf;
private TextField len_tf;
private TextField rate_tf;

private boolean panelbuilt;

private AppManager appMan;

private final String EXT_NO_Name = "EXT_NO";

public final static String Name = new String(ProtocolCoDec_TypeConfiguration.RTP_Name);
}

