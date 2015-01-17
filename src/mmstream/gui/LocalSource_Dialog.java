package mmstream.gui;

import mmstream.address.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.gui.*;
import mmstream.apps.*;

import java.awt.*;
import java.util.*;

public class LocalSource_Dialog extends Dialog implements SourceOutput {

  public void message(String text) {
    messageArea.appendText(text+"\n");
  }

  public void error(String text) {
    errorArea.appendText(text+"\n");
  }

  public void notifyStateChange() {
    stateField.setText(source.getState());
    if (producer == null)
      producer = source.getProducer();
    if (producer != null) {
      prodTypeField.setText(producer.getTypeName());
      prodIdField.setText(producer.getId());
    }
  }

  public void notifyAddress(Address data, Address control) {
    if (data != null)
      dataAddrField.setText(data.toString());
    if (control != null)
      controlAddrField.setText(control.toString());
  }

  public void notifySourceDescription() {
    cnameField.setText(source.getCname());
    nameField.setText(source.getName());
    toolField.setText(source.getTool());
    emailField.setText(source.getEmail());
    phoneField.setText(source.getPhone());
    locField.setText(source.getLoc());
    noteField.setText(source.getNote());
  }


  public void notifyReceptionStatistics(ReceptionStatistics sourceStats) {
    ReceptionStatistics_DataPanel rs = (ReceptionStatistics_DataPanel)(receiverTable.get(sourceStats.id));
    if (rs == null) {
      boolean vis = this.isShowing();
      rs = guiMan.createReceptionStatisticsDataGui((source.getCoDec()).getProtocolName());
      rs.receiverIdField.setText(sourceStats.id);
      rs.receiverCnameField.setText(sourceStats.cname);
      g_c.gridwidth = 1;
      g_c.gridheight = GridBagConstraints.REMAINDER;
      g_c.gridy = 1;
      g_c.gridx = ++statsGridX;
      mainLO.setConstraints(rs, g_c);
      recPanel.add(rs);
      recPanel.validate();
      this.validate();
      this.pack();
      if (vis)
	this.show();
      else
	this.hide();
      receiverTable.put(sourceStats.id, rs);
    }
    rs.displayStats(sourceStats);

    return;
  }
  

  public void notifySenderStatistics(SenderStatistics sourceStats) {
    if (sendStatsDisplay != null)
      sendStatsDisplay.displayStats(sourceStats);
  }
  

  public LocalSource_Dialog(AppManager am, LocalSource s, Session ses) {
    super((am.getGuiManager()).getRoot(), "Local Source "+s.getId(), false);

    source = s;
    session = ses;
    producer = source.getProducer();
    prodDialog = null;
    consDialog = null;

    appMan = am;
    guiMan = am.getGuiManager();
    dataOutput = null;
    sendStatsDisplay = null;

    receiverTable = new Hashtable(5, (float)0.5);

    this.setLayout(new BorderLayout(15,15));
  
    mainLO = new GridBagLayout();
    FlowLayout btLO = new FlowLayout(FlowLayout.CENTER);


    // create constraints
    g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 3;
    g_c.ipady = 3;


    // create button panel 
    checkboxPanel = new Panel();
    checkboxPanel.setLayout(new GridLayout(0, 1));

    sendCheckbox = new Checkbox("Send Data");
    sendCheckbox.setState(source.getSendPermission());
    checkboxPanel.add(sendCheckbox);
    Label l = new Label("Show:", Label.LEFT);
    checkboxPanel.add(l);
    sdCheckbox = new Checkbox("Source Data");
    sdCheckbox.setState(true);
    checkboxPanel.add(sdCheckbox);
    ssCheckbox = new Checkbox("Sender Stats");
    ssCheckbox.setState(true); 
    checkboxPanel.add(ssCheckbox);
    rsCheckbox = new Checkbox("Reception Stats");
    rsCheckbox.setState(true); 
    checkboxPanel.add(rsCheckbox);
    udCheckbox = new Checkbox("User Data");
    udCheckbox.setState(true);
    checkboxPanel.add(udCheckbox);
    outCheckbox = new Checkbox("Output");
    outCheckbox.setState(true);
    checkboxPanel.add(outCheckbox);
    payCheckbox = new Checkbox("Payload");
    payCheckbox.setState(true);
    checkboxPanel.add(payCheckbox);

    l = new Label("Action:", Label.LEFT);
    checkboxPanel.add(l);
    setButton = new Button("Set User Values");
    checkboxPanel.add(setButton);
    prodButton = new Button("Show Producer");
    checkboxPanel.add(prodButton);
    consButton = new Button("Add Consumer");
    checkboxPanel.add(consButton);
    hideButton = new Button("Hide Window");
    checkboxPanel.add(hideButton);

    mainPanel = new Panel();
    mainPanel.setLayout(mainLO);

    sdPanel = new Panel();
    sdPanel.setLayout(mainLO);

    l = new Label("Source Info:", Label.CENTER);
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.gridheight = 1;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.anchor = GridBagConstraints.CENTER;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
  
    l = new Label("Source Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    TextField t = new TextField(source.getId(), 12);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    sdPanel.add(t);

    l = new Label("Session Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    t = new TextField(session.getId(), 12);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    sdPanel.add(t);

    l = new Label("State:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    stateField = new TextField(source.getState(), 12);
    stateField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(stateField, g_c);
    sdPanel.add(stateField);

    l = new Label("Data Adress:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    Address tmp = source.getDataAddress();
    if (tmp != null)
      dataAddrField = new TextField(tmp.toString(), 12);
    else
      dataAddrField = new TextField(12);
    dataAddrField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(dataAddrField, g_c);
    sdPanel.add(dataAddrField);

    l = new Label("Control Adress:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    tmp =source.getControlAddress();
    if (tmp != null)
      controlAddrField = new TextField(tmp.toString(), 12);
    else
      controlAddrField = new TextField(12);
    controlAddrField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(controlAddrField, g_c);
    sdPanel.add(controlAddrField);

    udPanel = new Panel();
    udPanel.setLayout(mainLO);

    l = new Label("Producer Type:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (producer != null)
      prodTypeField = new TextField(producer.getTypeName(), 12);
    else
      prodTypeField = new TextField(12);
    prodTypeField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(prodTypeField, g_c);
    sdPanel.add(prodTypeField);

    l = new Label("Producer Id:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (producer != null)
      prodIdField = new TextField(producer.getId(), 12);
    else
      prodIdField = new TextField(12);
    prodIdField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(prodIdField, g_c);
    sdPanel.add(prodIdField);

    l = new Label("User Info:", Label.CENTER);
    g_c.gridx = 0;
    g_c.gridy++;
    g_c.gridheight = 1;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.anchor = GridBagConstraints.CENTER;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
  
    l = new Label("Cname:", Label.LEFT);
    g_c.gridwidth = 1;
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    cnameField = new TextField(source.getCname(), 12);
    cnameField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(cnameField, g_c);
    udPanel.add(cnameField);

    l = new Label("Name:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    nameField = new TextField(source.getName(), 12);
    nameField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(nameField, g_c);
    udPanel.add(nameField);

    l = new Label("Tool:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    toolField = new TextField(source.getTool(), 12);
    toolField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(toolField, g_c);
    udPanel.add(toolField);

    l = new Label("Email:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    emailField = new TextField(source.getEmail(), 12);
    emailField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(emailField, g_c);
    udPanel.add(emailField);

    l = new Label("Phone:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    phoneField = new TextField(source.getPhone(), 12);
    phoneField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(phoneField, g_c);
    udPanel.add(phoneField);

    l = new Label("Loc:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    locField = new TextField(source.getLoc(), 12);
    locField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(locField, g_c);
    udPanel.add(locField);

    l = new Label("Note:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    noteField = new TextField(source.getNote(), 12);
    noteField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(noteField, g_c);
    udPanel.add(noteField);

    //   l = new Label("Xxx:", Label.LEFT);
    //   g_c.gridx = 0;
    //   g_c.gridy++;
    //   mainLO.setConstraints(l, g_c);
    //   p1.add(l);
    //   xxxField = new TextField(source.getXxx(), 12);
    //   xxxField.setEditable(true);
    //   g_c.gridx = 1;
    //   mainLO.setConstraints(xxxField, g_c);
    //   p1.add(xxxField);

    outPanel = new Panel();
    outPanel.setLayout(mainLO);

    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;

    l = new Label("Messages:", Label.CENTER);
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    outPanel.add(l);
    messageArea = new TextArea(6,12);
    messageArea.setEditable(false);
    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy = 1;
    mainLO.setConstraints(messageArea, g_c);
    outPanel.add(messageArea);

    l = new Label("Errors:", Label.CENTER);
    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridy = 2;
    mainLO.setConstraints(l, g_c);
    outPanel.add(l);
    errorArea = new TextArea(6,12);
    errorArea.setEditable(false);
    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy = 3;
    mainLO.setConstraints(errorArea, g_c);
    outPanel.add(errorArea);

    recPanel = new Panel();
    recPanel.setLayout(mainLO);
    l = new Label("Remote Reception Stats:", Label.CENTER);
    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    recPanel.add(l);

    g_c.gridwidth = 1;
    g_c.gridheight = GridBagConstraints.REMAINDER;
    ReceptionStatistics_LabelPanel rs = guiMan.createReceptionStatisticsLabelGui((source.getCoDec()).getProtocolName());
    if (rs != null) {
      g_c.gridy = 1;
      g_c.gridx++;
      mainLO.setConstraints(rs, g_c);
      recPanel.add(rs);
    }
    statsGridX = ++g_c.gridx;

    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridheight = 1;
    g_c.gridwidth = 1;
    g_c.gridy = 0;
    g_c.insets = new Insets(3, 3, 3, 3);  

    g_c.gridx = 0;
    mainLO.setConstraints(sdPanel, g_c);
    mainPanel.add(sdPanel);

    sendStatsDisplay = guiMan.createLocalSenderStatisticsGui((source.getCoDec()).getProtocolName());
    if (sendStatsDisplay != null) {
      sendStatsDisplay.titleLabel.setText("Local Sender Stats");
      g_c.gridx++;
      mainLO.setConstraints(sendStatsDisplay, g_c);
      mainPanel.add(sendStatsDisplay);
    }

    g_c.gridx++;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(recPanel, g_c);
    mainPanel.add(recPanel);

    g_c.gridwidth = 1;
    g_c.gridy++;
    g_c.gridx = 0;
    mainLO.setConstraints(udPanel, g_c);
    mainPanel.add(udPanel);

    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridx++;
    mainLO.setConstraints(outPanel, g_c);
    mainPanel.add(outPanel);

    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.gridx++;
    g_c.gridheight = 1;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(checkboxPanel, g_c);
    mainPanel.add(checkboxPanel);



    g_c.gridwidth = 1;
    g_c.gridy = 0;

    // everything is placed in the dialog
    //  this.add("West", checkboxPanel);
    this.add("Center", mainPanel);

    this.pack();

    source.output = this;
  }


  public boolean 
  action(Event e, Object arg) {
    if (e.target == consButton) {
      if (consDialog == null) {
	consDialog = new NewConsumer_Dialog(appMan, session);
	consDialog.pack();
      }
      Hashtable ht = new Hashtable(1, (float)0.5);
      ht.put(source.getId(), source);
      consDialog.setSources(ht);
      consDialog.show();
      return true;
    }
    else if (e.target == prodButton) {
      if (producer != null) {
	if (prodDialog == null) {
	  ProducerOutput tmp = producer.getOutput();
	  if (tmp != null) {
	    try {
	      prodDialog = (Producer_Dialog)tmp;
	    } catch(ClassCastException ce) { ; }
	  }
	  if (prodDialog != null) 
	    prodDialog.show();
	}
      }
      return true;
    }
    if (e.target == hideButton) {
      this.hide();
      return true;
    }
    else if (e.target == setButton) {
      source.setCname(cnameField.getText());
      source.setName(nameField.getText());
      source.setTool(toolField.getText());
      source.setEmail(emailField.getText());
      source.setPhone(phoneField.getText());
      source.setLoc(locField.getText());
      source.setNote(noteField.getText());
      return true;
    }
    else if (e.target == sendCheckbox) {
      source.setSendPermission(sendCheckbox.getState());
      return true;
    }
    else if (e.target == sdCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(sdPanel);
      else
	mainPanel.add(sdPanel);
      this.pack();
      return true;
    }
    else if (e.target == ssCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(sendStatsDisplay);
      else
	mainPanel.add(sendStatsDisplay);
      this.pack();
      return true;
    }
    else if (e.target == rsCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(recPanel);
      else
	mainPanel.add(recPanel);
      this.pack();
      return true;
    }
    else if (e.target == udCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(udPanel);
      else
	mainPanel.add(udPanel);
      this.pack();
      return true;
    }
    else if (e.target == outCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(outPanel);
      else
	mainPanel.add(outPanel);
      this.pack();
      return true;
    }
    else if (e.target == payCheckbox) {
      if (dataOutput != null) {
	if (Boolean.FALSE.equals((Boolean)arg))
	  mainPanel.remove(dataOutput);
	else
	  mainPanel.add(dataOutput);
	this.pack();
      }
      return true;
    }
    return false;
  }

  private AppManager appMan;
  private GuiManager guiMan;

  private LocalSource source;
  private ProducerControl producer;
  private Session session;

  private GridBagLayout mainLO;
  private FlowLayout btLO;
  protected GridBagConstraints g_c;
  private TextField cnameField;
  private TextField nameField;
  private TextField toolField;
  private TextField emailField;
  private TextField phoneField;
  private TextField locField;
  private TextField noteField;
  private TextField dataAddrField;
  private TextField controlAddrField;
  private TextField prodTypeField;
  private TextField prodIdField;
  // private TextField xxxField;

  private TextField stateField;
  private TextArea  messageArea;
  private TextArea  errorArea;

  private Panel mainPanel;
  private Panel sdPanel;
  private Panel recPanel;
  private Panel udPanel;
  private Component dataOutput;
  private Panel outPanel;

  private int statsGridX;

  private Panel checkboxPanel;

  private Button setButton;
  private Button prodButton;
  private Button consButton;
  private Button hideButton;

  private Checkbox sendCheckbox;
  private Checkbox sdCheckbox;
  private Checkbox ssCheckbox;
  private Checkbox rsCheckbox;
  private Checkbox udCheckbox;
  private Checkbox outCheckbox;
  private Checkbox payCheckbox;

  private Hashtable receiverTable;
  private SenderStatistics_Panel sendStatsDisplay;

  private Producer_Dialog prodDialog;
  private NewConsumer_Dialog consDialog;
}

