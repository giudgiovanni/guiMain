package mmstream.gui;

import mmstream.util.*;
import mmstream.apps.*;

import java.awt.*;

public abstract class ReceptionStatistics_LabelPanel extends Panel {

public ReceptionStatistics_LabelPanel() {
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
  
  Canvas dummy = new Canvas();
  dummy.resize(1, 3);
  g_c.gridy = nextY++;
  mainLO.setConstraints(dummy, g_c);
  this.add(dummy);

  g_c.ipady = 7;
  
  Label l = new Label("Receiver Id:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);

  l = new Label("Receiver Cname:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);

  l = new Label("Fraction Lost:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);

  l = new Label("Total Lost:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);


  l = new Label("Jitter:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);



//   l = new Label("Xxx:", Label.LEFT);
//   g_c.gridx = 0;
//   g_c.gridy = nextY++;
//   mainLO.setConstraints(l, g_c);
//   this.add(l);

}

protected int nextY;
protected GridBagLayout mainLO;

// private TextField xxxField;

}
