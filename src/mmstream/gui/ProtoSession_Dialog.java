package mmstream.gui;

import mmstream.source.*;
import mmstream.gui.*;
import mmstream.util.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;
import java.util.*;


public class ProtoSession_Dialog extends Dialog implements Output {

  // from interface SessionOutput
public void message(String text) {
  messageArea.appendText(text+"\n");
}

public void error(String text) {
  errorArea.appendText(text+"\n");
}

public void 
notifyStateChange() {
  partTF.setText("");
  for (Enumeration e = protoSession.getParticipants().elements(); e.hasMoreElements();) {
    String tmp = (String)(e.nextElement());
    partTF.appendText(tmp+"\n");
  }
}
  

public String getId() {
  return protoSession.getId();
}

public ProtoSession_Dialog(GuiManager gm, AppManager am, ProtoSession sen) {
  super(gm.getRoot(), "Session "+sen.getId()+" Data", false);

  protoSession = sen;

  appMan = am;
  guiMan = gm;


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
  g_c.ipadx = 2;
  g_c.ipady = 2;


  // create button panel 
  buttonPanel = new Panel();
  buttonPanel.setLayout(btLO);

  joinButton = new Button("Join Session");
  buttonPanel.add(joinButton);

  hideButton = new Button("Hide Window");
  buttonPanel.add(hideButton);



  Panel p = new Panel();
  p.setLayout(mainLO);

  Panel p1a = new Panel();
  p1a.setLayout(mainLO);

  Label l = new Label("Session Info:", Label.CENTER);
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
  TextField t = new TextField(protoSession.getId(), 15);
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
  t = new TextField(protoSession.getInitiator(), 15);
  t.setEditable(false);
  g_c.gridx = 1;
  mainLO.setConstraints(t, g_c);
  p1a.add(t);

//   l = new Label("Action:", Label.LEFT);
//   g_c.gridx = 0;
//   g_c.gridy++;
//   mainLO.setConstraints(l, g_c);
//   p1a.add(l);
//   stateField = new TextField(protoSession.getAction(), 15);
//   stateField.setEditable(false);
//   g_c.gridx = 1;
//   mainLO.setConstraints(stateField, g_c);
//   p1a.add(stateField);

  l = new Label("Connection:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  p1a.add(l);

  Hashtable conGuiTable = guiMan.getConnectionOptionsGuis();
  Class tmp = (Class)conGuiTable.get(protoSession.getConnectionType().getName());
  if (tmp != null) {
    ConnectionOptions dataConOpts = null;
    ConnectionOptions controlConOpts = null;
    try {
      dataConOpts = (ConnectionOptions)tmp.newInstance();
      controlConOpts = (ConnectionOptions)tmp.newInstance();
    }
    catch(IllegalAccessException ie) { return;}
    catch(InstantiationException ie) { return;}
    g_c.gridx = 1;
    //    Panel tmppanel = conOpts.getPanel(protoSession.getDataAddress(), protoSession.getControlAddress(), protoSession.getDataMaxSize(), protoSession.getDataMaxSize());
    //    Panel tmppanel = dataConOpts.getPanel(protoSession.getDataAddress(), protoSession.getDataMaxSize());
    conOpts = new ConnectionPanel(dataConOpts, protoSession.getDataAddress(), protoSession.getDataMaxSize(),
						   controlConOpts, protoSession.getControlAddress(), protoSession.getDataMaxSize());
    mainLO.setConstraints(conOpts, g_c);
    p1a.add(conOpts);
  }

  l = new Label("Payload Type:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  p1a.add(l);

  Hashtable payGuiTable = guiMan.getPayloadTypeOptionsGuis();
  tmp = (Class)payGuiTable.get(protoSession.getPayloadType().getName());
  if (tmp != null) {
    payOpts = null;
    try {
      payOpts = (PayloadTypeOptions)tmp.newInstance();
    }
    catch(IllegalAccessException ie) { return;}
    catch(InstantiationException ie) { return;}
    g_c.gridx = 1;
    Panel tmppanel = payOpts.getPanel(protoSession.getPayloadType());
    if (tmppanel != null) {
      mainLO.setConstraints(tmppanel, g_c);
      p1a.add(tmppanel);
    }
  }



  l = new Label("Protocol:", Label.LEFT);
  g_c.gridx = 0;
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  p1a.add(l);

  Hashtable protGuiTable = guiMan.getProtocolCoDecOptionsGuis();
  tmp = (Class)protGuiTable.get(protoSession.getProtocolType().getName());
  if (tmp != null) {
    protOpts = null;
    try {
      protOpts = (ProtocolCoDecOptions)tmp.newInstance();
    }
    catch(IllegalAccessException ie) { return;}
    catch(InstantiationException ie) { return;}
    g_c.gridx = 1;
    Panel tmppanel = protOpts.getPanel(appMan, protoSession.getProfile());
    if (tmppanel != null) {
      mainLO.setConstraints(tmppanel, g_c);
      p1a.add(tmppanel);
    }
  }



  Panel p1b = new Panel();
  p1b.setLayout(mainLO);
  g_c.anchor = GridBagConstraints.NORTH;
  g_c.gridwidth = 1;
  g_c.gridheight = GridBagConstraints.REMAINDER;
  g_c.gridx = 0;
  g_c.gridy = 0;

  l = new Label("General Options:", Label.LEFT);
  mainLO.setConstraints(l, g_c);
  p1b.add(l);

  exportBox = new Checkbox("Export Session");
  exportBox.setState(true);
  g_c.gridheight = 1;
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  g_c.gridx++;
  mainLO.setConstraints(exportBox, g_c);
  p1b.add(exportBox);
  
  l = new Label("Participants:", Label.LEFT);
  g_c.gridy++;
  mainLO.setConstraints(l, g_c);
  p1b.add(l);
  partTF = new TextArea(5, 15);
  for (Enumeration e = protoSession.getParticipants().elements(); e.hasMoreElements();) {
    String tmpstr = (String)(e.nextElement());
    partTF.appendText(tmpstr);
  }
  partTF.setEditable(false);
  g_c.gridy++;
  mainLO.setConstraints(partTF, g_c);
  p1b.add(partTF);

  g_c.insets = new Insets(3,3,3,3);
  g_c.anchor = GridBagConstraints.NORTH;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;
  mainLO.setConstraints(p1a, g_c);
  p.add(p1a);
  g_c.gridy++;
  mainLO.setConstraints(p1b, g_c);
  p.add(p1b);
  // everything is placed in the dialog
  this.add("South", buttonPanel);
  this.add("Center", p);

  this.pack();
}

public boolean 
action(Event e, Object arg) {
  if (e.target == joinButton) {
	Connection dacon = null;
	Connection ctcon = null;
	Config cfg = null;
	PayloadType plt = null;
	Session target = null;
	try {
	  dacon = conOpts.dataCO.createConnection();
	  ctcon = conOpts.controlCO.createConnection();
	}
	catch (Gui_Exception ce) {
	  System.err.println("EXCEPTION: ProtoSessionDialog.action(): ConnectionOptions.createConnection():: "+ ce.getMessage());
	  ce.printStackTrace();
	  return true;
	}
	try {
	  plt = payOpts.createPayloadType(appMan);
	}
	catch (Gui_Exception pe) {
	  System.err.println("EXCEPTION: ProtoSessionDialog.action(): PayloadTypeOptions.createPayloadType():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	try {
	  cfg = protOpts.createConfig(plt, dacon,ctcon);
	}
	catch (Gui_Exception pe) {
	  System.err.println("EXCEPTION: ProtoSessionDialog.action(): ProtocolCoDecOptions.createConfig():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	try {
	  target = new Session(appMan, cfg);
	}
	catch (Session_Exception pe) {
	  System.err.println("EXCEPTION: ProtoSessionDialog.action():  new Session():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	target.setExport(exportBox.getState());
	target.setInitiator(protoSession.getInitiator());
	target.setId(protoSession.getId());
	try {
	  appMan.joinProtoSession(protoSession, target);
	}
	catch (AppManager_Exception ae) {
	  System.err.println("EXCEPTION: ProtoSessionDialog.action(): appMan.joinProtoSession():: "+ ae.getMessage());
	  ae.printStackTrace();
	  return true;
	}
	this.hide();
    return true;
  }
  else if (e.target == hideButton) {
    this.hide();
    return true;
  }
  return false;
}


private AppManager appMan;
private GuiManager guiMan;

private ProtoSession protoSession;

private GridBagLayout mainLO;
private FlowLayout btLO;

private Checkbox exportBox;
  //private TextField stateField;
private TextArea  partTF;
private TextArea  messageArea;
private TextArea  errorArea;

private PayloadTypeOptions payOpts;
private ConnectionPanel conOpts;
private ProtocolCoDecOptions protOpts;
private Panel buttonPanel;
private Button joinButton;
private Button hideButton;
private Panel controlPanel;

}



