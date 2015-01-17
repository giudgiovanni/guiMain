package mmstream.gui;

import mmstream.address.*;
import mmstream.stream.*;
import mmstream.util.*;
import mmstream.source.*;
import mmstream.gui.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.apps.*;

import java.awt.*;

public class Producer_Dialog extends Dialog implements ProducerOutput {

  public void message(String text) {
    messageArea.appendText(text+"\n");
  }

  public void error(String text) {
    errorArea.appendText(text+"\n");
  }

  public void 
  notifyStateChange() {
    stateField.setText(producer.getState());
  }

  public void 
  notifyControlData(ControlData cd) {
    bwScroll.setValue((int)(session.getProfile().getSessionBandwidth()-cd.getMaxBandwidth()));
    plScroll.setValue((int)(session.getProfile().getPayloadType().getMaxSize()-cd.getPayloadLength()));
    prScroll.setValue((int)(session.getProfile().getChunkRate()-cd.getChunkRate()));
  }

  public Producer_Dialog(AppManager am, ProducerControl s, Session ses) {
    super((am.getGuiManager()).getRoot(), "Producer "+s.getId(), false);
    producer = s;
    appMan = am;
    guiMan = am.getGuiManager();
    this.setLayout(new BorderLayout(15,15));
    session = ses;
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
    checkboxPanel.setLayout(new GridLayout(0,1));


    Label l = new Label("Show:", Label.CENTER);
    checkboxPanel.add(l);
    sdCheckbox = new Checkbox("Producer Data");
    sdCheckbox.setState(true);
    checkboxPanel.add(sdCheckbox);
    outCheckbox = new Checkbox("Output");
    outCheckbox.setState(true);
    checkboxPanel.add(outCheckbox);


    l = new Label("Action:", Label.CENTER);
    checkboxPanel.add(l);
    startButton = new Button("Start");
    checkboxPanel.add(startButton);
    stopButton = new Button("Stop");
    checkboxPanel.add(stopButton);
    finishButton = new Button("Finish");
    checkboxPanel.add(finishButton);
    hideButton = new Button("Hide Window");
    checkboxPanel.add(hideButton);

    mainPanel = new Panel();
    mainPanel.setLayout(mainLO);

    sdPanel = new Panel();
    sdPanel.setLayout(mainLO);

    l = new Label("Producer Data:", Label.CENTER);
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.gridheight = 1;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.anchor = GridBagConstraints.CENTER;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
  
    l = new Label("Producer Id:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridwidth = 1;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    TextField t = new TextField(producer.getId(), 12);
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
    t = new TextField(producer.getStreamExporter().getId(), 12);
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

    l = new Label("Type:", Label.LEFT);
    g_c.gridwidth = 1;
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    sdPanel.add(l);
    t = new TextField(producer.getTypeName(), 12);
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
    stateField = new TextField(producer.getState(), 12);
    stateField.setEditable(false);
    g_c.gridx = 1;
    mainLO.setConstraints(stateField, g_c);
    sdPanel.add(stateField);

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

    // Control
    controlPanel = new Panel();
    controlPanel.setLayout(mainLO);
    l = new Label("Producer Control:", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.weightx = 0;    
    g_c.weighty = 0;    
    g_c.fill = GridBagConstraints.NONE;
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
    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(bwScroll, g_c);
    controlPanel.add(bwScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weightx = 0;
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
    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(prScroll, g_c);
    controlPanel.add(prScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weightx = 0;
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

    plScroll = new Scrollbar(Scrollbar.VERTICAL, 0, 1, (int)(session.getProfile().getPayloadType().getMinSize()),(int)(session.getProfile().getPayloadType().getMaxSize())); 
    plScroll.setLineIncrement((int)(session.getProfile().getPayloadType().getMaxSize()/100 + 1));
    plScroll.setPageIncrement((int)(session.getProfile().getPayloadType().getSize()/10 + 1));
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.gridy++;
    mainLO.setConstraints(plScroll, g_c);
    controlPanel.add(plScroll);

    l = new Label("0", Label.CENTER);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.fill = GridBagConstraints.NONE;
    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    controlPanel.add(l);
 
 
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.insets = new Insets(3, 3, 3, 3);  

    g_c.gridy = 0;
    g_c.gridx = 0;
    mainLO.setConstraints(sdPanel, g_c);
    mainPanel.add(sdPanel);

    g_c.gridy++;
    mainLO.setConstraints(checkboxPanel, g_c);
    mainPanel.add(checkboxPanel);

    g_c.weightx = 10;
    g_c.weighty = 10;
    g_c.gridheight = 2;
    g_c.gridy = 0;
    g_c.gridx++;
    g_c.fill = GridBagConstraints.VERTICAL;
    mainLO.setConstraints(controlPanel, g_c);
    mainPanel.add(controlPanel);

    g_c.gridheight = 1;
    g_c.gridy = 0;
    g_c.gridx++;
    g_c.weightx = 10;    
    g_c.weighty = 10;    
    g_c.fill = GridBagConstraints.BOTH;
    mainLO.setConstraints(messPanel, g_c);
    mainPanel.add(messPanel);

    g_c.gridy++;
    mainLO.setConstraints(errPanel, g_c);
    mainPanel.add(errPanel);



    // everything is placed in the dialog
    //  this.add("West", checkboxPanel);
    this.add("Center", mainPanel);

    this.pack();
    this.notifyControlData(producer.getControlData());
    producer.setOutput(this);

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
	this.message("trying setBandwidth("+nv+")");
	ControlData ret;
	try {
	  ret = session.setProducerBandwidth(producer,nv);
	}
	catch(Control_Exception ce) {
	  this.error("EXCEPTION: "+ce.getMessage());
	  return true;
	}
	this.message("setBandwidth("+nv+") = "+ret.getMaxBandwidth());
	this.notifyControlData(ret);
	return true;
      }
    }
    else if (evt.target == plScroll) {
      switch (evt.id) {
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	long nv = session.getProfile().getPayloadType().getMaxSize() - ((Integer)evt.arg).longValue();
	this.message("trying setPayloadLength("+nv+")");
	ControlData ret = producer.setPayloadLength(nv);
	this.message("setPayloadLength("+nv+") = "+ret.getPayloadLength());
	this.notifyControlData(ret);
	return true;
      }
    }
    else if (evt.target == prScroll) {
      switch (evt.id) {
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	double nv = session.getProfile().getChunkRate() - ((Integer)evt.arg).doubleValue();
	this.message("trying setChunkRate("+nv+")");
	ControlData ret = producer.setChunkRate(nv);
	this.message("setChunkRate("+nv+") = "+ret.getChunkRate());
	this.notifyControlData(ret);
	return true;
      }
    }
    return super.handleEvent(evt);
  }


  public boolean 
  action(Event e, Object arg) {
    if (e.target == hideButton) {
      this.hide();
      return true;
    }
    if (e.target == startButton) {
      this.message("Start called");
      //      session.startProducer(producer);
      producer.Start();
      return true;
    }
    if (e.target == finishButton) {
      this.message("Finish called");
      producer.Finish("User requested", true);
      return true;
    }
    if (e.target == stopButton) {
      this.message("Stop called");
      producer.Stop();
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

    return false;
  }

  private AppManager appMan;
  private GuiManager guiMan;
  private Session session;

  private ProducerControl producer;

  private GridBagLayout mainLO;
  private FlowLayout btLO;
  protected GridBagConstraints g_c;

  private TextField  stateField;
  private TextArea  messageArea;
  private TextArea  errorArea;

  private Panel mainPanel;
  private Panel sdPanel;
  private Panel messPanel;
  private Panel errPanel;

  private Panel checkboxPanel;
  private Checkbox outCheckbox;
  private Checkbox sdCheckbox;

  private Panel controlPanel;
  private Scrollbar bwScroll;
  private Scrollbar plScroll;
  private Scrollbar prScroll;

  private Button hideButton;
  private Button finishButton;
  private Button stopButton;
  private Button startButton;

}
