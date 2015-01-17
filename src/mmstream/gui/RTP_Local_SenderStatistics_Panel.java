package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.protocols.rtp.*;

import java.awt.*;

public class RTP_Local_SenderStatistics_Panel extends SenderStatistics_Panel {

public RTP_Local_SenderStatistics_Panel() {
  super();

  Label l = new Label("ExtHiSeq:", Label.LEFT);
  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.WEST;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridheight = 1;

  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  extHiSeqField = new TextField(20);
  extHiSeqField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(extHiSeqField, g_c);
  this.add(extHiSeqField);

  l = new Label("LastSR:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  lastSRField = new TextField(20);
  lastSRField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(lastSRField, g_c);
  this.add(lastSRField);


}

public void displayStats(SenderStatistics s) {
  super.displayBaseStats(s);
  RTP_SenderStatistics rs = (RTP_SenderStatistics)s;
  lastSRField.setText(String.valueOf(rs.lastSR));
  extHiSeqField.setText(String.valueOf(rs.extHiSeq));
}

private TextField extHiSeqField;
private TextField lastSRField;
}
