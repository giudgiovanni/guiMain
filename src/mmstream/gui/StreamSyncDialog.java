package mmstream.gui;

import mmstream.address.*;
import mmstream.stream.*;
import mmstream.util.*;
import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.session.*;
import mmstream.consumer.*;

import java.awt.*;
import java.util.*;

public class StreamSyncDialog extends Dialog {

  public StreamSyncDialog(AppManager am, Consumer_Dialog cd, Stream s) {
    super((am.getGuiManager()).getRoot(), "Sync Stream "+s.getId(), false);
    stream = s;
    appMan = am;
    consD = cd;

    this.setLayout(new BorderLayout(15,15));
  
    mainLO = new GridBagLayout();

    // create constraints
    g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 5;
    g_c.ipady = 15;


    // create button panel 
    checkboxPanel = new Panel();
    checkboxPanel.setLayout(new GridLayout(0,1));


    Label l = new Label("Action:", Label.CENTER);
    checkboxPanel.add(l);
    syncButton = new Button("Sync to Stream");
    checkboxPanel.add(syncButton);
    hideButton = new Button("Hide Window");
    checkboxPanel.add(hideButton);

    mainPanel = new Panel();
    mainPanel.setLayout(mainLO);

    sdPanel = new Panel();
    sdPanel.setLayout(mainLO);

    l = new Label("Stream to Sync:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridheight = 1;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    streamIdField = new TextField(stream.getId(), 12);
    streamIdField.setEditable(false);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridx = 1;
    mainLO.setConstraints(streamIdField, g_c);
    sdPanel.add(streamIdField);


    l = new Label("Master Streams:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    
    streamList = new List(5, false);
    MainFrame.fillList(streamList, 10);
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridx = 1;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(streamList, g_c);
    sdPanel.add(streamList);

    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.insets = new Insets(3, 3, 3, 3);  

    g_c.gridy = 0;
    g_c.gridx = 0;
    mainLO.setConstraints(sdPanel, g_c);
    mainPanel.add(sdPanel);

    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(checkboxPanel, g_c);
    mainPanel.add(checkboxPanel);

    this.add("Center", mainPanel);

    this.pack();
    MainFrame.emptyList(streamList, 10);

  }

  public void
  setStreams(Hashtable sT) {
    streamTable = sT;
    streamList.clear();
    for (Enumeration e = streamTable.keys(); e.hasMoreElements();) {
      String tmp = (String)(e.nextElement());
      streamList.addItem(tmp);
    }
  }

  public boolean 
  action(Event e, Object arg) {
    if (e.target == hideButton) {
      this.hide();
      return true;
    }
    else if (e.target == syncButton) {
      String tmp = streamList.getSelectedItem();
      if (tmp != null) {
	Stream str = (Stream)streamTable.get(tmp);
	stream.setMasterStream(str);
	consD.notifyStateChange();
	this.hide();
      }
      return true;
    }
    else if (e.target == streamList && e.id == Event.ACTION_EVENT) {
      if (e.arg != null) {
	Stream str = (Stream)streamTable.get(e.arg);
	stream.setMasterStream(str);
	consD.notifyStateChange();
	this.hide();
      }
      return true;
    }
    return false;
  }

  private AppManager appMan;
  private Stream stream;
  private Hashtable streamTable;
  private Consumer_Dialog consD;

  private GridBagLayout mainLO;
  protected GridBagConstraints g_c;

  private TextField  streamIdField;
  private Panel mainPanel;
  private Panel sdPanel;
  private Panel checkboxPanel;
  private List streamList;

  private Button hideButton;
  private Button syncButton;
}
