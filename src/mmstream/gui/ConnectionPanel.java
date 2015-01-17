package mmstream.gui;

import mmstream.gui.*;

import java.awt.*;

public class ConnectionPanel extends Panel {
  
public ConnectionPanel(ConnectionOptions dco, ConnectionOptions cco) {

  dataCO = dco;
  controlCO = cco;

  GridBagLayout mainLO = new GridBagLayout();
  this.setLayout(mainLO);
  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;

  Label l = new Label("Data Connection", Label.CENTER);
  mainLO.setConstraints(l, g_c);
  this.add(l);

  g_c.gridy++;
  Panel p = dco.getPanel();
  mainLO.setConstraints(p, g_c);
  this.add(p);

  g_c.gridy = 0;
  g_c.gridx++;
  l = new Label("Control Connection", Label.CENTER);
  mainLO.setConstraints(l, g_c);
  this.add(l);

  g_c.gridy++;
  p = cco.getPanel();
  mainLO.setConstraints(p, g_c);
  this.add(p);

}

public ConnectionPanel(ConnectionOptions dco, String dataA, int datas, ConnectionOptions cco, String contA, int conts) {

  dataCO = dco;
  controlCO = cco;

  GridBagLayout mainLO = new GridBagLayout();
  this.setLayout(mainLO);
  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;

  Label l = new Label("Data Connection", Label.CENTER);
  mainLO.setConstraints(l, g_c);
  this.add(l);

  g_c.gridy++;
  Panel p = dco.getPanel(dataA,datas);
  mainLO.setConstraints(p, g_c);
  this.add(p);

  g_c.gridy = 0;
  g_c.gridx++;
  l = new Label("Control Connection", Label.CENTER);
  mainLO.setConstraints(l, g_c);
  this.add(l);

  g_c.gridy++;
  p = cco.getPanel(contA,conts);
  mainLO.setConstraints(p, g_c);
  this.add(p);

}

  public ConnectionOptions dataCO, controlCO;
}
