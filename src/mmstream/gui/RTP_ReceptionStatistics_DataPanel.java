package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.protocols.rtp.*;

import java.awt.*;

public class RTP_ReceptionStatistics_DataPanel extends ReceptionStatistics_DataPanel {

public RTP_ReceptionStatistics_DataPanel() {
  super();

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.WEST;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridheight = 1;

  g_c.gridx = 0;
  g_c.gridy = nextY++;
  extHiSeqField = new TextField(20);
  extHiSeqField.setEditable(false);
  mainLO.setConstraints(extHiSeqField, g_c);
  this.add(extHiSeqField);


  g_c.gridy = nextY++;
  lastSRField = new TextField(20);
  lastSRField.setEditable(false);
  mainLO.setConstraints(lastSRField, g_c);
  this.add(lastSRField);

  g_c.gridy = nextY++;
  delayLastSRField = new TextField(20);
  delayLastSRField.setEditable(false);
  mainLO.setConstraints(delayLastSRField, g_c);
  this.add(delayLastSRField);





}

public void displayStats(ReceptionStatistics s) {
  super.displayBaseStats(s);
  RTP_ReceptionStatistics rs = (RTP_ReceptionStatistics)s;
  extHiSeqField.setText(String.valueOf(rs.extHiSeq));
  lastSRField.setText(String.valueOf(rs.lastSR));
  delayLastSRField.setText(String.valueOf(rs.delayLastSR));
}


private TextField extHiSeqField;
private TextField lastSRField;
private TextField delayLastSRField;
}
