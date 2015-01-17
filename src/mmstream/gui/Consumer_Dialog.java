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

public class Consumer_Dialog extends Dialog implements ConsumerOutput {

  public void message(String text) {
    messageArea.appendText(text+"\n");
  }

  public void error(String text) {
    errorArea.appendText(text+"\n");
  }

  public void 
  notifyStateChange() {
    String tmp = consumer.getState();
    stateField.setText(tmp);
    if (stream == null) {
      stream = consumer.getStream();
      if (stream!= null)
	streamIdField.setText(stream.getId());
    }
    if (stream != null) {
      master = stream.getMasterStream();
      if (master!= null)
	masterStreamIdField.setText(master.getId());
    }
    
    if (exporter == null)
      exporter = consumer.getStreamExporter();
    if (exporter!= null) {
      exporterIdField.setText(exporter.getId());
      cnameField.setText(exporter.getCname());
    }
    
  }

  public void setDataOutput(Component p) {
    if (dataOutput == null) {
      dataOutput = p;
      g_c.gridwidth = GridBagConstraints.REMAINDER;
      g_c.gridheight = GridBagConstraints.REMAINDER;
      g_c.gridx = 0;
      g_c.gridy = 1;
      mainLO.setConstraints(dataOutput, g_c);
      dataPanel.add(dataOutput);
      this.pack();
    }

  }

  public Consumer_Dialog(AppManager am, ConsumerControl s, Session ses) {
    super((am.getGuiManager()).getRoot(), "Consumer "+s.getId(), false);
    consumer = s;
    session = ses;
    stream = consumer.getStream();
    if (stream != null)
      master = stream.getMasterStream();
    exporter = consumer.getStreamExporter();
    appMan = am;
    guiMan = am.getGuiManager();
    syncDialog = null;
    dataOutput = null;
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
    g_c.ipady = 5;


    // create button panel 
    checkboxPanel = new Panel();
    checkboxPanel.setLayout(new GridLayout(0,1));


    Label l = new Label("Show:", Label.CENTER);
    checkboxPanel.add(l);
    sdCheckbox = new Checkbox("Consumer Data");
    sdCheckbox.setState(true);
    checkboxPanel.add(sdCheckbox);
    outCheckbox = new Checkbox("Messages");
    outCheckbox.setState(true);
    checkboxPanel.add(outCheckbox);
    payCheckbox = new Checkbox("Output");
    payCheckbox.setState(true);
    checkboxPanel.add(payCheckbox);


    l = new Label("Action:", Label.CENTER);
    checkboxPanel.add(l);
    startButton = new Button("Start Playback");
    checkboxPanel.add(startButton);
    stopButton = new Button("Stop Playback");
    checkboxPanel.add(stopButton);
    finishButton = new Button("Finish Playback");
    checkboxPanel.add(finishButton);
    flushButton = new Button("Flush Stream");
    checkboxPanel.add(flushButton);
    syncButton = new Button("Sync to Stream");
    checkboxPanel.add(syncButton);
    hideButton = new Button("Hide Window");
    checkboxPanel.add(hideButton);
    posBox = new Checkbox("Playout Sync");
    posBox.setState(consumer.getPlayOutSync());
    checkboxPanel.add(posBox);

    mainPanel = new Panel();
    mainPanel.setLayout(mainLO);

    sdPanel = new Panel();
    sdPanel.setLayout(mainLO);

    l = new Label("Consumer Data:", Label.CENTER);
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.gridheight = 1;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.anchor = GridBagConstraints.CENTER;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
  
    l = new Label("Consumer Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    TextField t = new TextField(consumer.getId(), 12);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    sdPanel.add(t);

    l = new Label("Source Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (exporter != null)
      exporterIdField = new TextField(exporter.getId(), 12);
    else 
      exporterIdField = new TextField(12);
    exporterIdField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(exporterIdField, g_c);
    sdPanel.add(exporterIdField);

    l = new Label("Stream Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (stream != null)
      streamIdField = new TextField(stream.getId(), 12);
    else 
      streamIdField = new TextField(12);
    streamIdField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(streamIdField, g_c);
    sdPanel.add(streamIdField);

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

    l = new Label("Cname:", Label.LEFT);
    g_c.gridwidth = 1;
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (exporter != null)
      cnameField = new TextField(exporter.getCname(), 12);
    else
      cnameField = new TextField(12);
    cnameField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(cnameField, g_c);
    sdPanel.add(cnameField);

    l = new Label("Type:", Label.LEFT);
    g_c.gridwidth = 1;
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    t = new TextField(consumer.getTypeName(), 12);
    t.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(t, g_c);
    sdPanel.add(t);

    l = new Label("State:", Label.LEFT);
    g_c.gridwidth = 1;
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    stateField = new TextField(consumer.getState(), 12);
    stateField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(stateField, g_c);
    sdPanel.add(stateField);

    l = new Label("Master Stream Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    if (master != null)
      masterStreamIdField = new TextField(master.getId(), 12);
    else 
      masterStreamIdField = new TextField(12);
    masterStreamIdField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(masterStreamIdField, g_c);
    sdPanel.add(masterStreamIdField);

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

    messPanel = new Panel();
    messPanel.setLayout(mainLO);

    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;

    l = new Label("Messages:", Label.CENTER);
    g_c.gridx = 0;
    g_c.gridy = 0;
    mainLO.setConstraints(l, g_c);
    messPanel.add(l);
    messageArea = new TextArea(6,12);
    messageArea.setEditable(false);
    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy = 1;
    mainLO.setConstraints(messageArea, g_c);
    messPanel.add(messageArea);

    errPanel = new Panel();
    errPanel.setLayout(mainLO);

    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;

    l = new Label("Errors:", Label.CENTER);
    g_c.gridy = 2;
    mainLO.setConstraints(l, g_c);
    errPanel.add(l);
    errorArea = new TextArea(6,12);
    errorArea.setEditable(false);
    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridy = 3;
    mainLO.setConstraints(errorArea, g_c);
    errPanel.add(errorArea);

    dataPanel = new Panel();
    dataPanel.setLayout(mainLO);

    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;

    l = new Label("Output:", Label.CENTER);
    mainLO.setConstraints(l, g_c);
    dataPanel.add(l);

    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.insets = new Insets(3, 3, 3, 3);  

    g_c.gridy = 0;
    g_c.gridx = 0;
    mainLO.setConstraints(sdPanel, g_c);
    mainPanel.add(sdPanel);

    g_c.gridx++;
    mainLO.setConstraints(dataPanel, g_c);
    mainPanel.add(dataPanel);

    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridx++;
    mainLO.setConstraints(messPanel, g_c);
    mainPanel.add(messPanel);

    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(checkboxPanel, g_c);
    mainPanel.add(checkboxPanel);

    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    g_c.gridx += 2;
    mainLO.setConstraints(errPanel, g_c);
    mainPanel.add(errPanel);



    // everything is placed in the dialog
    //  this.add("West", checkboxPanel);
    this.add("Center", mainPanel);

    this.pack();

    consumer.setOutput(this);
    this.notifyStateChange();
  }

  public boolean 
  action(Event e, Object arg) {
    if (e.target == hideButton) {
      this.hide();
      return true;
    }
    if (e.target == syncButton) {
      this.message("Sync called");
      if (stream == null)
	stream = consumer.getStream();
      if (stream != null) {
	Hashtable streams = appMan.getStreamManager().getStreams();
	streams.remove(stream.getId());
	if (!streams.isEmpty()) {
	  if (syncDialog == null)
	    syncDialog = new StreamSyncDialog(appMan, this, stream);
	  syncDialog.setStreams(streams);
	  syncDialog.show();
	}
      }
      return true;
    }
    if (e.target == flushButton) {
      this.message("Flush called");
      if (stream == null)
	stream = consumer.getStream();
      if (stream != null)
	stream.flush();
      return true;
    }
    if (e.target == startButton) {
      this.message("Start called");
      //      session.startConsumer(consumer);
      consumer.Start();
      return true;
    }
    if (e.target == finishButton) {
      this.message("Finish called");
      consumer.Finish();
      return true;
    }
    if (e.target == stopButton) {
      this.message("Stop called");
      consumer.Stop();
      return true;
    }
    else if (e.target == posBox) {
      consumer.setPlayOutSync(((Boolean)arg).booleanValue());
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
    else if (e.target == outCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg)) {
	mainPanel.remove(messPanel);
	mainPanel.remove(errPanel);
      }
      else {
	mainPanel.add(messPanel);
	mainPanel.add(errPanel);
      }
      this.pack();
      return true;
    }
    else if (e.target == payCheckbox) {
      if (Boolean.FALSE.equals((Boolean)arg))
	mainPanel.remove(dataPanel);
      else
	mainPanel.add(dataPanel);
      this.pack();
      return true;
    }
    return false;
  }

  private AppManager appMan;
  private GuiManager guiMan;
  private StreamSyncDialog syncDialog;
  private ConsumerControl consumer;
  private StreamExporter exporter;
  private Stream stream;
  private Stream master;
  private Session session;

  private GridBagLayout mainLO;
  protected GridBagConstraints g_c;

  private TextArea  messageArea;
  private TextArea  errorArea;
  private TextField  stateField;
  private TextField  exporterIdField;
  private TextField  streamIdField;
  private TextField  masterStreamIdField;
  private TextField  cnameField;

  private Panel mainPanel;
  private Panel sdPanel;
  private Panel messPanel;
  private Panel errPanel;
  private Panel dataPanel;

  private Component dataOutput;

  private Panel checkboxPanel;
  private Checkbox outCheckbox;
  private Checkbox payCheckbox;
  private Checkbox sdCheckbox;

  private Checkbox posBox;
  private Button hideButton;
  private Button flushButton;
  private Button syncButton;
  private Button finishButton;
  private Button stopButton;
  private Button startButton;

}
