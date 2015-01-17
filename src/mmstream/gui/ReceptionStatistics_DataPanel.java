package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;

import java.awt.*;

public abstract class ReceptionStatistics_DataPanel extends Panel {

public ReceptionStatistics_DataPanel() {
  super();

  nextY = 0;
  // create constraints and layout
  mainLO = new GridBagLayout();
  GridBagConstraints g_c = new GridBagConstraints();
  this.setLayout(mainLO);

  g_c.fill = GridBagConstraints.NONE;
  g_c.gridheight = 1;
  g_c.gridwidth = 1;
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 0;
  g_c.gridy = nextY++;

  receiverIdField = new TextField(20);
  receiverIdField.setEditable(false);
  mainLO.setConstraints(receiverIdField, g_c);
  this.add(receiverIdField);

  g_c.gridy = nextY++;
  receiverCnameField = new TextField(20);
  receiverCnameField.setEditable(false);
  mainLO.setConstraints(receiverCnameField, g_c);
  this.add(receiverCnameField);

  g_c.gridy = nextY++;
  fractionLostField = new TextField(20);
  fractionLostField.setEditable(false);
  mainLO.setConstraints(fractionLostField, g_c);
  this.add(fractionLostField);

  g_c.gridy = nextY++;
  totalLostField = new TextField(20);
  totalLostField.setEditable(false);
  mainLO.setConstraints(totalLostField, g_c);
  this.add(totalLostField);

  g_c.gridy = nextY++;
  jitterField = new TextField(20);
  jitterField.setEditable(false);
  mainLO.setConstraints(jitterField, g_c);
  this.add(jitterField);



//   g_c.gridy = nextY++;
//   xxxField = new TextField(20);
//   xxxField.setEditable(false);
//   mainLO.setConstraints(xxxField, g_c);
//   this.add(xxxField);

}

public abstract void displayStats(ReceptionStatistics s);

protected void displayBaseStats(ReceptionStatistics s) {
  fractionLostField.setText(String.valueOf(s.fractionLost));
  totalLostField.setText(String.valueOf(s.totalLost));
  jitterField.setText(String.valueOf(s.jitter));
}

protected int nextY;
protected GridBagLayout mainLO;

// private TextField xxxField;

protected TextField receiverCnameField;
protected TextField receiverIdField;
protected TextField fractionLostField;
protected TextField totalLostField;
protected TextField jitterField;
}
