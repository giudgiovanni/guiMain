package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.protocols.rtp.*;

import java.awt.*;

public class RTP_ReceptionStatistics_Panel extends ReceptionStatistics_Panel {

public RTP_ReceptionStatistics_Panel() {
  super();

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.WEST;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridheight = 1;

  Label l = new Label("ExtHiSeq:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  extHiSeqField = new TextField(10);
  extHiSeqField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(extHiSeqField, g_c);
  this.add(extHiSeqField);


  l = new Label("DelayLastSR:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy = nextY++;
  mainLO.setConstraints(l, g_c);
  this.add(l);
  delayLastSRField = new TextField(10);
  delayLastSRField.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(delayLastSRField, g_c);
  this.add(delayLastSRField);





}

public void displayStats(ReceptionStatistics s) {
  super.displayBaseStats(s);
  RTP_ReceptionStatistics rs = (RTP_ReceptionStatistics)s;
  extHiSeqField.setText(String.valueOf(rs.extHiSeq));
  delayLastSRField.setText(String.valueOf(rs.delayLastSR));
}


private TextField extHiSeqField;
private TextField delayLastSRField;
}
