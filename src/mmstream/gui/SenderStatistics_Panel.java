package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;

import java.awt.*;

public abstract class SenderStatistics_Panel extends Panel {

public SenderStatistics_Panel() {
  super();

  nextY = 0;
  // create constraints and layout
  mainLO = new GridBagLayout();
  GridBagConstraints g_c = new GridBagConstraints();
  this.setLayout(mainLO);

  titleLabel = new Label(" ", Label.CENTER);
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  g_c.gridheight = 1;
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.anchor = GridBagConstraints.CENTER;
  mainLO.setConstraints(titleLabel, g_c);
  this.add(titleLabel);
  
  Label l = new Label("Sent Chunks:", Label.LEFT);
  g_c.gridwidth = 1;
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  chunksSentField = new TextField(20);
  chunksSentField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(chunksSentField, g_c);
  this.add(chunksSentField);

  l = new Label("Sent Octets:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  octetsSentField = new TextField(20);
  octetsSentField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(octetsSentField, g_c);
  this.add(octetsSentField);


  l = new Label("Chunk Rate:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  chunkRateField = new TextField(20);
  chunkRateField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(chunkRateField, g_c);
  this.add(chunkRateField);

  l = new Label("Bandwidth:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  bandwidthField = new TextField(20);
  bandwidthField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(bandwidthField, g_c);
  this.add(bandwidthField);


//   l = new Label("Xxx:", Label.LEFT);
//   g_c.gridx = 0;
//   g_c.gridy = nextY++;
//   mainLO.setConstraints(l, g_c);
//   this.add(l);
//   xxxField = new TextField(20);
//   xxxField.setEditable(false);
//   g_c.gridx = 1;
//   mainLO.setConstraints(xxxField, g_c);
//   this.add(xxxField);

}

public abstract void displayStats(SenderStatistics s);

protected void displayBaseStats(SenderStatistics s) {
  chunksSentField.setText(String.valueOf(s.chunksSent));
  chunkRateField.setText(String.valueOf(s.chunkRate));
  bandwidthField.setText(String.valueOf(s.bandwidth));
  octetsSentField.setText(String.valueOf(s.octetsSent));
}

protected int nextY;
protected GridBagLayout mainLO;

// private TextField xxxField;

protected TextField chunkRateField;
protected TextField bandwidthField;
protected TextField octetsSentField;
protected TextField chunksSentField;
public Label titleLabel;
}
