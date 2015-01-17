package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;

import java.awt.*;

public abstract class ReceptionStatistics_Panel extends Panel {

public ReceptionStatistics_Panel() {
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


  Label l = new Label("Chunks Received:", Label.LEFT);
  g_c.gridwidth = 1;
  g_c.anchor = GridBagConstraints.WEST;
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  chunksReceivedField = new TextField(10);
  chunksReceivedField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(chunksReceivedField, g_c);
  this.add(chunksReceivedField);

  l = new Label("Octets Received:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  octetsReceivedField = new TextField(10);
  octetsReceivedField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(octetsReceivedField, g_c);
  this.add(octetsReceivedField);

  l = new Label("Chunk Rate:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  chunkRateField = new TextField(10);
  chunkRateField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(chunkRateField, g_c);
  this.add(chunkRateField);


  l = new Label("Bandwidth:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  bandwidthField = new TextField(10);
  bandwidthField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(bandwidthField, g_c);
  this.add(bandwidthField);


  l = new Label("Fraction Lost:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  fractionLostField = new TextField(10);
  fractionLostField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(fractionLostField, g_c);
  this.add(fractionLostField);

  l = new Label("Total Lost:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  totalLostField = new TextField(10);
  totalLostField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(totalLostField, g_c);
  this.add(totalLostField);


  l = new Label("Jitter:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  jitterField = new TextField(10);
  jitterField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(jitterField, g_c);
  this.add(jitterField);



//   l = new Label("Xxx:", Label.LEFT);
//   g_c.gridx = 0;
//   g_c.gridy = nextY++;
//   mainLO.setConstraints(l, g_c);
//   this.add(l);
//   xxxField = new TextField(10);
//   xxxField.setEditable(false);
//   g_c.gridx = 1;
//   mainLO.setConstraints(xxxField, g_c);
//   this.add(xxxField);

}

public abstract void displayStats(ReceptionStatistics s);

protected void displayBaseStats(ReceptionStatistics s) {
  chunksReceivedField.setText(String.valueOf(s.chunksReceived));
  octetsReceivedField.setText(String.valueOf(s.octetsReceived));
  fractionLostField.setText(String.valueOf(((float)s.fractionLost)/256.0));
  totalLostField.setText(String.valueOf(s.totalLost));
  chunkRateField.setText(String.valueOf(s.chunkRate));
  bandwidthField.setText(String.valueOf(s.bandwidth));
  jitterField.setText(String.valueOf(s.jitter));
}

protected int nextY;
protected GridBagLayout mainLO;

// private TextField xxxField;

protected TextField chunksReceivedField;
protected TextField octetsReceivedField;
protected TextField fractionLostField;
protected TextField totalLostField;
protected TextField chunkRateField;
protected TextField bandwidthField;
protected TextField jitterField;
public Label titleLabel;
}
