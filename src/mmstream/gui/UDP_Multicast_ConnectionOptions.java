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
import java.util.*;


public class UDP_Multicast_ConnectionOptions extends Panel implements ConnectionOptions {

public UDP_Multicast_ConnectionOptions() {
  panelbuilt = false;
}

public String getTypeName() {
  return Name;
}

public Panel getPanel() {
  if (panelbuilt)
    return Options;

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;

  Options = new Panel();
  GridBagLayout mainLO = new GridBagLayout();
  Options.setLayout(mainLO);

  Label l = new Label("UDP_Multicast Options:", Label.CENTER);
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  l = new Label("Host Number:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  host = new TextField("224.0.0.1", 20);
  //  host = new TextField("224.0.0.1", 20);
  host.setEditable(true);
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx = 1;
  mainLO.setConstraints(host, g_c);
  Options.add(host);
  
  l = new Label("Port:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  dp_tf = new TextField("6666", 5);
  dp_tf.setEditable(true);
  g_c.gridx = 1;
  mainLO.setConstraints(dp_tf, g_c);
  Options.add(dp_tf);

  l = new Label("TTL:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  ttl = new TextField("1", 2);
  ttl.setEditable(true);
  g_c.gridx = 1;
  mainLO.setConstraints(ttl, g_c);
  Options.add(ttl);
  
  lpBox = new Checkbox("Loopback");
  g_c.gridx = 2;
  mainLO.setConstraints(lpBox, g_c);
  Options.add(lpBox);
  
  l = new Label("Max Packet Size:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  md_tf = new TextField("20000", 5);
  md_tf.setEditable(true);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 1;
  mainLO.setConstraints(md_tf, g_c);
  Options.add(md_tf);

  panelbuilt = true;

  return Options;
}

public Panel getPanel(String data, int maxData) {
  if (!panelbuilt)
    this.getPanel();
  StringTokenizer st = new StringTokenizer(data, ":", false);
  if (st.countTokens() != 2)
    return null;

  host.setText(st.nextToken());
  String port = st.nextToken();
  dp_tf.setText(port);

  md_tf.setText(String.valueOf(maxData));

  return Options;
}
  
public Connection createConnection() throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("UDP_Multicast_ConnectionOptions.createConnection():not initialised");

  UDP_Multicast_Connection con = null;
  int dp=0, lt = 0, md=0;
  try {
    dp = Integer.parseInt(dp_tf.getText());
    lt = Integer.parseInt(ttl.getText());
    md  = Integer.parseInt(md_tf.getText());
  }
  catch(NumberFormatException ne) {
    throw new Gui_Exception("UDP_Multicast_ConnectionOptions.createConnection(): Integer.parseInt():: "+ne.getMessage());
  }
  boolean lb = lpBox.getState();
  try {
    con = new UDP_Multicast_Connection(dp, host.getText(), (byte)lt, lb, md);
  }
  catch (UDP_Multicast_Connection_Exception ge) {
    throw new Gui_Exception("UDP_Multicast_ConnectionOptions.createConnection(): UDP_Multicast_Connection("+dp+","+host.getText()+","+lt+","+lb+"):: "+ge.getMessage()+","+ge.getClass().getName());
  }


  return con;
}

private Panel Options;
private TextField ttl;
private TextField host;
private TextField dp_tf;
private Checkbox lpBox;
private TextField md_tf;


private boolean panelbuilt;

public static final String Name = new String(Connection_TypeConfiguration.UDP_MULTICAST_Name);
}
