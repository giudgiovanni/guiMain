package mmstream.gui;

import mmstream.source.*;
import mmstream.address.*;
import mmstream.util.*;
import mmstream.gui.*;
import mmstream.session.*;
import mmstream.apps.*;

import java.awt.*;
import java.util.*;

public class RemoteSource_Dialog extends Dialog implements SourceOutput {

  public void message(String text) {
    messageArea.appendText(text+"\n");
  }

  public void error(String text) {
    errorArea.appendText(text+"\n");
  }

  public void notifyStateChange() {
    stateField.setText(source.getState());
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
    //   Address tmp = source.getDataAddress();
    //   if (tmp != null)
    //     dataAddrField.setText(tmp.toString());
    //   tmp = source.getControlAddress();
    //   if (tmp != null)
    //     controlAddrField.setText(tmp.toString());
  }

  public void notifySenderStatistics(SenderStatistics sourceStats) {
    if (sendStatsDisplay != null)
      sendStatsDisplay.displayStats(sourceStats);
  }

  public void notifyReceptionStatistics(ReceptionStatistics sourceStats) {
    if (recStatsDisplay != null)
      recStatsDisplay.displayStats(sourceStats);
  }

  public RemoteSource_Dialog(AppManager am, RemoteSource s, Session se) {
    super((am.getGuiManager()).getRoot(), "Remote Source "+s.getId(), false);

    session = se;
    source = s;
    consDialog = null;
  
    appMan = am;
    guiMan = am.getGuiManager();
    dataOutput = null;
    sendStatsDisplay = null;
    recStatsDisplay = null;

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
    g_c.ipadx = 5;
    g_c.ipady = 5;


    // create button panel 
    checkboxPanel = new Panel();
    checkboxPanel.setLayout(new GridLayout(0, 1));

    Label l = new Label("Show:", Label.CENTER);
    checkboxPanel.add(l);
    sdCheckbox = new Checkbox("Source Data");
    sdCheckbox.setState(true);
    checkboxPanel.add(sdCheckbox);
    udCheckbox = new Checkbox("User Data");
    udCheckbox.setState(true);
    checkboxPanel.add(udCheckbox);
    ssCheckbox = new Checkbox("Sender Stats");
    ssCheckbox.setState(true);
    checkboxPanel.add(ssCheckbox);
    rsCheckbox = new Checkbox("Reception Stats");
    rsCheckbox.setState(true);
    checkboxPanel.add(rsCheckbox);
    outCheckbox = new Checkbox("Output");
    outCheckbox.setState(true);
    checkboxPanel.add(outCheckbox);

    l = new Label("Action:", Label.CENTER);
    checkboxPanel.add(l);
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
    cnameField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(cnameField, g_c);
    udPanel.add(cnameField);

    l = new Label("Name:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    nameField = new TextField(source.getName(), 12);
    nameField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(nameField, g_c);
    udPanel.add(nameField);

    l = new Label("Tool:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    toolField = new TextField(source.getTool(), 12);
    toolField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(toolField, g_c);
    udPanel.add(toolField);

    l = new Label("Email:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    emailField = new TextField(source.getEmail(), 12);
    emailField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(emailField, g_c);
    udPanel.add(emailField);

    l = new Label("Phone:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    phoneField = new TextField(source.getPhone(), 12);
    phoneField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(phoneField, g_c);
    udPanel.add(phoneField);

    l = new Label("Loc:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    locField = new TextField(source.getLoc(), 12);
    locField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(locField, g_c);
    udPanel.add(locField);

    l = new Label("Note:", Label.LEFT);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    udPanel.add(l);
    noteField = new TextField(source.getNote(), 12);
    noteField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(noteField, g_c);
    udPanel.add(noteField);

    //   l = new Label("Xxx:", Label.LEFT);
    //   g_c.gridx = 0;
    //   g_c.gridy++;
    //   mainLO.setConstraints(l, g_c);
    //   p1.add(l);
    //   xxxField = new TextField(source.getXxx(), 12);
    //   xxxField.setEditable(false);
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

    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.insets = new Insets(3, 3, 3, 3);  

    g_c.gridy = 0;
    g_c.gridx = 0;
    mainLO.setConstraints(sdPanel, g_c);
    mainPanel.add(sdPanel);

    g_c.gridx++;
    sendStatsDisplay = guiMan.createRemoteSenderStatisticsGui((source.getCoDec()).getProtocolName());
    if (sendStatsDisplay != null) {
      sendStatsDisplay.titleLabel.setText("Remote Sender Stats");
      mainLO.setConstraints(sendStatsDisplay, g_c);
      mainPanel.add(sendStatsDisplay);
    }

    g_c.gridx++;
    recStatsDisplay = guiMan.createReceptionStatisticsGui((source.getCoDec()).getProtocolName());
    if (recStatsDisplay != null) {
      recStatsDisplay.titleLabel.setText("Local Reception Stats");
      mainLO.setConstraints(recStatsDisplay, g_c);
      mainPanel.add(recStatsDisplay);
    }

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
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx++;
    mainLO.setConstraints(checkboxPanel, g_c);
    mainPanel.add(checkboxPanel);



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
    else if (e.target == hideButton) {
      this.hide();
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
    else if (e.target == udCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(udPanel);
      else
	mainPanel.add(udPanel);
      this.pack();
      return true;
    }
    else if (e.target == rsCheckbox) {
      if (recStatsDisplay != null) {
	if (Boolean.FALSE.equals((Boolean)arg))
	  mainPanel.remove(recStatsDisplay);
	else
	  mainPanel.add(recStatsDisplay);
	this.pack();
      }
      return true;
    }
    else if (e.target == ssCheckbox) {
      if (sendStatsDisplay != null) {
	if (Boolean.FALSE.equals((Boolean)arg))
	  mainPanel.remove(sendStatsDisplay);
	else
	  mainPanel.add(sendStatsDisplay);
	this.pack();
      }
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
    return false;
  }

  private AppManager appMan;
  private GuiManager guiMan;

  private RemoteSource source;
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
  // private TextField xxxField;

  private TextField stateField;
  private TextArea  messageArea;
  private TextArea  errorArea;

  private Panel mainPanel;
  private Panel sdPanel;
  private Panel udPanel;
  private Panel outPanel;
  private Component dataOutput;

  private Panel checkboxPanel;

  private Button consButton;
  private Button hideButton;

  private Checkbox sdCheckbox;
  private Checkbox udCheckbox;
  private Checkbox ssCheckbox;
  private Checkbox rsCheckbox;
  private Checkbox outCheckbox;

  private ReceptionStatistics_Panel recStatsDisplay;
  private SenderStatistics_Panel sendStatsDisplay;
  private NewConsumer_Dialog consDialog;
}
