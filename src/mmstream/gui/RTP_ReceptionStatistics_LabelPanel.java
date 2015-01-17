package mmstream.gui;

import mmstream.util.*;
import mmstream.apps.*;
import mmstream.protocols.rtp.*;

import java.awt.*;

public class RTP_ReceptionStatistics_LabelPanel extends ReceptionStatistics_LabelPanel {

public RTP_ReceptionStatistics_LabelPanel() {
  super();

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.WEST;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridheight = 1;

  Label l = new Label("ExtHiSeq:", Label.LEFT);
  g_c.ipady = 7;
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);


  l = new Label("LastSR:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);

  l = new Label("DelayLastSR:", Label.LEFT);
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);





}

}
