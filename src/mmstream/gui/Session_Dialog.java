package mmstream.gui;

import mmstream.source.*;
import mmstream.gui.*;
import mmstream.util.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.producer.*;
import mmstream.consumer.*;

import java.awt.*;
import java.util.*;


public class Session_Dialog extends Dialog implements SessionOutput {

  // from interface SessionOutput
  public void message(String text) {
    messageArea.appendText(text+"\n");
  }

  public void error(String text) {
    errorArea.appendText(text+"\n");
  }

  public synchronized void 
  notifyRemoteSource(RemoteSource s) {
    String tmp = s.getId();
    sourceTable.put(tmp, s);
    remoteSourceDialogTable.put(tmp, new RemoteSource_Dialog(appMan, s, session));
    remoteSourceList.addItem(tmp);
    remoteSourceList.getParent().layout();
  }

  public synchronized void 
  notifyConsumer(ConsumerControl s) {
    String tmp = s.getId();
    consDialogTable.put(tmp, new Consumer_Dialog(appMan, s, session));
    consList.addItem(tmp);
    consList.getParent().layout();
  }


  public synchronized void 
  notifyLocalSource(LocalSource s) {
    String tmp = s.getId();
    sourceTable.put(tmp, s);
    localSourceDialogTable.put(tmp, new LocalSource_Dialog(appMan, s, session));
    localSourceList.addItem(tmp);
    localSourceList.getParent().layout();
  }

  public synchronized void 
  notifyProducer(ProducerControl s) {
    String tmp = s.getId();
    prodDialogTable.put(tmp, new Producer_Dialog(appMan, s, session));
    prodList.addItem(tmp);
    prodList.getParent().layout();
  }


  public void 
  notifyStateChange() {
    stateField.setText(session.getState());
  }
  

  public void 
  notifyControlData(ControlData cd) {
    bwScroll.setValue((int)(session.getProfile().getSessionBandwidth()-cd.getMaxBandwidth()));
    plScroll.setValue((int)(session.getProfile().getPayloadType().getMaxSize()-cd.getPayloadLength()));
    prScroll.setValue((int)(session.getProfile().getChunkRate()-cd.getChunkRate()));
  }

  public String getId() {
    return session.getId();
  }

  // public void
  // finishSession(String reason) {
  //   session.finish(reason);
  // }

  public Session_Dialog(GuiManager gm, AppManager am, Session sen) {
    super(gm.getRoot(), "Session "+sen.getId()+" Data", false);

    session = sen;

    appMan = am;
    guiMan = gm;

    consDialog = null;
    prodDialog = null;

    localSourceDialogTable = new Hashtable(5, (float)0.5);
    prodDialogTable = new Hashtable(5, (float)0.5);
    consDialogTable = new Hashtable(5, (float)0.5);
    sourceTable = new Hashtable(10, (float)0.5);
    remoteSourceDialogTable = new Hashtable(5, (float)0.5);

    this.setLayout(new BorderLayout(15,15));
  
    mainLO = new GridBagLayout();
    FlowLayout btLO = new FlowLayout(FlowLayout.CENTER, 15, 15);


    // create constraints for general use
    GridBagConstraints g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 3;
    g_c.ipady = 3;
    g_c.insets = new Insets(3,3,3,3);


    // create button panel 
    buttonPanel = new Panel();
    buttonPanel.setLayout(mainLO);

    //   Panel = new Panel();
    //   Panel.setLayout(LO);

    Panel showPanel = new Panel();
    showPanel.setLayout(btLO);

    Label l = new Label("Show:", Label.CENTER);
    showPanel.add(l);

    localButton = new Button("Local Sources");
    showPanel.add(localButton);

    prodButton = new Button("Producers");
    showPanel.add(prodButton);

    remoteButton = new Button("Remote Sources");
    showPanel.add(remoteButton);

    consButton = new Button("Consumers");
    showPanel.add(consButton);

    hideButton = new Button("Hide Window");
    showPanel.add(hideButton);

    Panel actionPanel = new Panel();
    actionPanel.setLayout(btLO);

    l = new Label("Action:", Label.CENTER);
    actionPanel.add(l);

    startReceptionButton = new Button("Start Reception");
    actionPanel.add(startReceptionButton);

    startSendingButton = new Button("Start Sending");
    actionPanel.add(startSendingButton);

    finishButton = new Button("Finish Session");
    actionPanel.add(finishButton);

    newProducerButton = new Button("New Producer");
    actionPanel.add(newProducerButton);

    newConsumerButton = new Button("New Consumer");
    actionPanel.add(newConsumerButton);

    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(actionPanel, g_c);
    buttonPanel.add(actionPanel);

    g_c.gridy++;
    mainLO.setConstraints(showPanel, g_c);
    buttonPanel.add(showPanel);


    Panel p = new Panel();
    p.setLayout(mainLO);

    Panel p1a = new Panel();
    p1a.setLayout(mainLO);

    l = new Label("Session Info:", Label.CENTER);
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);

    l = new Label("Session Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    TextField t = new TextField(session.getId(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("Initiator:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField(session.getInitiator(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("State:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    stateField = new TextField(session.getState(), 15);
    stateField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(stateField, g_c);
    p1a.add(stateField);

    l = new Label("Connection:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField((session.getDataConnection()).getTypeName(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("Protocol:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField((session.getCoDec()).getProtocolName(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("Data Adress:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField(((session.getDataConnection()).getLocalAddress()).toString(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("Control Adress:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField(((session.getControlConnection()).getLocalAddress()).toString(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    l = new Label("Payload Type:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1a.add(l);
    t = new TextField(session.getProfile().getPayloadType().getName(), 15);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    p1a.add(t);

    Panel p1b = new Panel();
    p1b.setLayout(mainLO);

    l = new Label("StreamExporters:", Label.CENTER);
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    p1b.add(l);

    l = new Label("Local Sources:", Label.CENTER);
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1b.add(l);
    localSourceList = new List(5, false);
    MainFrame.fillList(localSourceList, 10);
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy++;
    mainLO.setConstraints(localSourceList, g_c);
    p1b.add(localSourceList);

    l = new Label("Producers:", Label.CENTER);
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1b.add(l);
    prodList = new List(5, false);
    MainFrame.fillList(prodList, 10);
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy++;
    mainLO.setConstraints(prodList, g_c);
    p1b.add(prodList);

    l = new Label("Remote Sources:", Label.CENTER);
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx++;
    g_c.gridy = 1;
    mainLO.setConstraints(l, g_c);
    p1b.add(l);
    remoteSourceList = new List(5, false);
    MainFrame.fillList(remoteSourceList, 10);
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy++;
    mainLO.setConstraints(remoteSourceList, g_c);
    p1b.add(remoteSourceList);


    l = new Label("Consumers:", Label.CENTER);
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p1b.add(l);
    consList = new List(5, false);
    MainFrame.fillList(consList, 10);
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy++;
    mainLO.setConstraints(consList, g_c);
    p1b.add(consList);


    Panel p2 = new Panel();
    p2.setLayout(mainLO);

    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;

    l = new Label("Messages:", Label.CENTER);
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    p2.add(l);
    messageArea = new TextArea(5,15);
    messageArea.setEditable(false);
    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy++;
    mainLO.setConstraints(messageArea, g_c);
    p2.add(messageArea);

    l = new Label("Errors:", Label.CENTER);
    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    p2.add(l);
    errorArea = new TextArea(5,15);
    errorArea.setEditable(false);
    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy++;
    mainLO.setConstraints(errorArea, g_c);
    p2.add(errorArea);


    // Control
    controlPanel = new Panel();
    controlPanel.setLayout(mainLO);
    l = new Label("Session Control:", Label.CENTER);
    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    l = new Label("Bandwidth:", Label.CENTER);
    g_c.gridwidth = 1;
    g_c.gridy = 1;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    l = new Label(String.valueOf(session.getProfile().getSessionBandwidth()), Label.CENTER);
    g_c.anchor = GridBagConstraints.SOUTH;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    bwScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, (int)session.getProfile().getSessionBandwidth()); 
    bwScroll.setLineIncrement((int)(session.getProfile().getSessionBandwidth()/100 + 1));
    bwScroll.setPageIncrement((int)(session.getProfile().getSessionBandwidth()/10 + 1));
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(bwScroll, g_c);
    controlPanel.add(bwScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weighty = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);
 
    l = new Label("Chunk Rate:", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridx = 1;
    g_c.gridwidth = 1;
    g_c.gridy = 1;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    l = new Label(String.valueOf(session.getProfile().getChunkRate()), Label.CENTER);
    g_c.anchor = GridBagConstraints.SOUTH;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    prScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, (int)(session.getProfile().getChunkRate())); 
    prScroll.setLineIncrement((int)(session.getProfile().getChunkRate()/100.0 + 1.0));
    prScroll.setPageIncrement((int)(session.getProfile().getChunkRate()/10.0 + 1.0));
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(prScroll, g_c);
    controlPanel.add(prScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weighty = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);
 
    l = new Label("Chunk Size:", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridx = 2;
    g_c.gridwidth = 1;
    g_c.gridy = 1;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    l = new Label(String.valueOf(session.getProfile().getPayloadType().getSize()), Label.CENTER);
    g_c.anchor = GridBagConstraints.SOUTH;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);

    plScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 1, (int)(session.getProfile().getPayloadType().getMinSize()), (int)(session.getProfile().getPayloadType().getMaxSize())); 
    plScroll.setLineIncrement((int)(session.getProfile().getPayloadType().getMaxSize()/100 + 1));
    plScroll.setPageIncrement((int)(session.getProfile().getPayloadType().getMaxSize()/10 + 1));
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(plScroll, g_c);
    controlPanel.add(plScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weighty = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);
 
    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(p1a, g_c);
    p.add(p1a);

    g_c.weightx = 0;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridx++;
    mainLO.setConstraints(p1b, g_c);
    p.add(p1b);

    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridx++;
    mainLO.setConstraints(p2, g_c);
    p.add(p2);

    g_c.gridx++;
    g_c.weightx = 0;
    g_c.weighty = 10;
    g_c.gridheight = 2;
    g_c.fill = GridBagConstraints.VERTICAL;
    mainLO.setConstraints(controlPanel, g_c);
    p.add(controlPanel);

    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.gridx = 0;
    g_c.gridy++;
    g_c.gridwidth = 3;
    g_c.gridheight = 1;
    mainLO.setConstraints(buttonPanel, g_c);
    p.add(buttonPanel);

    // everything is placed in the dialog
    //    this.add("South", buttonPanel);
    this.add("Center", p);

    p.layout();
    controlPanel.layout();
    bwScroll.layout();
    plScroll.layout();
    prScroll.layout();
    this.pack();
    MainFrame.emptyList(localSourceList, 10);
    MainFrame.emptyList(remoteSourceList, 10);
    MainFrame.emptyList(prodList, 10);
    MainFrame.emptyList(consList, 10);
    session.setOutput(this);
    this.notifyControlData(session.setControlData(session.getControlData()));
  }

  public boolean
  handleEvent(Event evt) {
    if (evt.target == bwScroll) {
      switch (evt.id) {
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	long nv = session.getProfile().getSessionBandwidth()-((Integer)evt.arg).longValue();
	ControlData ret = session.setMaxBandwidth(nv,true);
	this.message("setBandwidth("+nv+") = "+ret.getMaxBandwidth());
	this.notifyControlData(ret);
	return true;
      }
    }
    if (evt.target == plScroll) {
      switch (evt.id) {
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	long nv = session.getProfile().getPayloadType().getMaxSize() - ((Integer)evt.arg).longValue();
	ControlData ret = session.setPayloadLength(nv);
	this.message("setPayloadLength("+nv+") = "+ret.getPayloadLength());
	this.notifyControlData(ret);
	return true;
      }
    }
    if (evt.target == prScroll) {
      switch (evt.id) {
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	double nv = session.getProfile().getChunkRate() - ((Integer)evt.arg).doubleValue();
	ControlData ret = session.setChunkRate(nv);
	this.message("setChunkRate("+nv+") = "+ret.getChunkRate());
	this.notifyControlData(ret);
	return true;
      }
    }
    return super.handleEvent(evt);
  }


  public boolean 
  action(Event e, Object arg) {
    if (e.target == startReceptionButton) {
      try {
	session.startReception();
      }
      catch (Session_Exception se) {
	this.error("EXCEPTION: Session_Dialog.action(): Session.startReception():: "+ se.getMessage());
      }
      this.message("CALLBACK: Session.startReception() succeded for "+session.getId());
      stateField.setText(session.getState());
      return true;
    }
    if (e.target == startSendingButton) {
      try {
	session.startSending();
      }
      catch (Session_Exception se) {
	this.error("EXCEPTION: Session_Dialog.action(): Session.startSending():: "+ se.getMessage());
      }
      this.message("CALLBACK: Session.startSending() succeded for "+session.getId());
      stateField.setText(session.getState());
      return true;
    }
    else if (e.target == finishButton) {
      session.finish("User requested finishing of session");
      appMan.getSessionAgent().exportSession(session, ProtoSession.LEAVE);
      stateField.setText(session.getState());
      return true;
    }
    else if (e.target == hideButton) {
      this.hide();
      return true;
    }
    else if (e.target == remoteSourceList) {
      RemoteSource_Dialog sd = (RemoteSource_Dialog)(remoteSourceDialogTable.get(e.arg));
      if (sd != null)
	sd.show();
      return true;
    }
    else if (e.target == remoteButton) {
      String tmp = remoteSourceList.getSelectedItem();
      if (tmp != null) {
	RemoteSource_Dialog sd = (RemoteSource_Dialog)(remoteSourceDialogTable.get(tmp));
	if (sd != null)
	  sd.show();
      }
      return true;
    }
    else if (e.target == localSourceList) {
      LocalSource_Dialog sd = (LocalSource_Dialog)(localSourceDialogTable.get(e.arg));
      if (sd != null)
	sd.show();
      return true;
    }
    else if (e.target == localButton) {
      String tmp = localSourceList.getSelectedItem();
      if (tmp != null) {
	LocalSource_Dialog sd = (LocalSource_Dialog)(localSourceDialogTable.get(tmp));
	if (sd != null)
	  sd.show();
      }
      return true;
    }
  
    else if (e.target == consList) {
      Consumer_Dialog sd = (Consumer_Dialog)(consDialogTable.get(e.arg));
      if (sd != null)
	sd.show();
      return true;
    }
    else if (e.target == consButton) {
      String tmp = consList.getSelectedItem();
      if (tmp != null) {
	Consumer_Dialog sd = (Consumer_Dialog)(consDialogTable.get(tmp));
	if (sd != null)
	  sd.show();
      }
      return true;
    }

    else if (e.target == prodList) {
      Producer_Dialog sd = (Producer_Dialog)(prodDialogTable.get(e.arg));
      if (sd != null)
	sd.show();
      return true;
    }
    else if (e.target == prodButton) {
      String tmp = prodList.getSelectedItem();
      if (tmp != null) {
	Producer_Dialog sd = (Producer_Dialog)(prodDialogTable.get(tmp));
	if (sd != null)
	  sd.show();
      }
      return true;
    }

    else if (e.target == newProducerButton) {
      if (prodDialog == null) {
	prodDialog = new NewProducer_Dialog(appMan, session);
	prodDialog.pack();
      }
      prodDialog.show();
      return true;
    }
    else if (e.target == newConsumerButton) {
      if (consDialog == null) {
	consDialog = new NewConsumer_Dialog(appMan, session);
	consDialog.pack();
      }
      consDialog.setSources(sourceTable);
      consDialog.show();
      return true;
    }

    return false;
  }


  private AppManager appMan;
  private GuiManager guiMan;

  private Session session; 

  private GridBagLayout mainLO;
  private FlowLayout btLO;

  private List consList;
  private Hashtable consDialogTable;

  private List prodList;
  private Hashtable prodDialogTable;

  private Hashtable sourceTable;

  private List localSourceList;
  private Hashtable localSourceDialogTable;

  private List remoteSourceList;
  private Hashtable remoteSourceDialogTable;

  private TextField stateField;
  private TextArea  messageArea;
  private TextArea  errorArea;

  private Panel buttonPanel;
  private Button remoteButton;
  private Button consButton;
  private Button prodButton;
  private Button localButton;
  private Button startReceptionButton;
  private Button startSendingButton;
  private Button finishButton;
  private Button hideButton;
  private Button newConsumerButton;
  private Button newProducerButton;
  private Panel controlPanel;
  private Scrollbar bwScroll;
  private Scrollbar plScroll;
  private Scrollbar prScroll;

  private NewConsumer_Dialog consDialog;
  private NewProducer_Dialog prodDialog;
}



