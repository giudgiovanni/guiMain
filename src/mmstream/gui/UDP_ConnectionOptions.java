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


public class UDP_ConnectionOptions implements ConnectionOptions {

public UDP_ConnectionOptions() {
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
  g_c.ipadx = 5;
  g_c.ipady = 5;

  Options = new Panel();
  GridBagLayout mainLO = new GridBagLayout();
  Options.setLayout(mainLO);

  Label l = new Label("UDP Options:", Label.CENTER);
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  // the remote partner address
  l = new Label("Remote:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  l = new Label("Host Name:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  host = new TextField("localhost", 20);
  host.setEditable(true);
  g_c.anchor = GridBagConstraints.WEST;
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
  rdp_tf = new TextField("666", 5);
  rdp_tf.setEditable(true);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 1;
  mainLO.setConstraints(rdp_tf, g_c);
  Options.add(rdp_tf);

  // the local address
  l = new Label("Local:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridwidth = 1;
  g_c.gridy++;
  g_c.gridx = 0;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  l = new Label("Port:", Label.LEFT);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  Options.add(l);
  ldp_tf = new TextField("666", 5);
  ldp_tf.setEditable(true);
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 1;
  mainLO.setConstraints(ldp_tf, g_c);
  Options.add(ldp_tf);

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
  if (st.countTokens() != 2) {
    System.err.println("ERROR: countTokens() != 2 in data String "+data);
    return null;
  }
  host.setText(st.nextToken());
  String port = st.nextToken();
  ldp_tf.setText(port);
  rdp_tf.setText(port);

  md_tf.setText(String.valueOf(maxData));

  return Options;
}
  
public Connection createConnection() throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("UDP_ConnectionOptions.createConnection():not initialised");

  UDP_Connection con = null;
  int ldp=0, rdp=0, md=0;
  try {
    ldp = Integer.parseInt(ldp_tf.getText());
    rdp = Integer.parseInt(rdp_tf.getText());
    md  = Integer.parseInt(md_tf.getText());
  }
  catch(NumberFormatException ne) {
    throw new Gui_Exception("UDP_ConnectionOptions.createConnection(): Integer.parseInt():: "+ne.getMessage());
  }

  try {
    con = new UDP_Connection(ldp, host.getText(), rdp, md);
  }
  catch (Exception ge) {
    throw new Gui_Exception("UDP_ConnectionOptions.createConnection(): UDP_Connection("+","+ldp+","+host.getText()+","+rdp+",):: "+ge.getMessage());
  }


  return con;
}

private Panel Options;
private TextField host;
private TextField rdp_tf;
private TextField ldp_tf;
private TextField md_tf;

private boolean panelbuilt;

public static final String Name = new String(Connection_TypeConfiguration.UDP_Name);
}
