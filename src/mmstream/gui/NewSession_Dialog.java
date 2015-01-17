package mmstream.gui;

import mmstream.util.*;
import mmstream.gui.*;
import mmstream.session.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;
import java.net.*;
import java.util.*;


public class NewSession_Dialog extends Dialog {

  public NewSession_Dialog(AppManager am) {
    super((am.getGuiManager()).getRoot(), "New Session", false);

    appMan = am;
    guiMan = appMan.getGuiManager();
  

    Hashtable conGuiTable = guiMan.getConnectionOptionsGuis();
    conOptionsTable = new Hashtable(5, (float).5);

    Hashtable payloadGuiTable = guiMan.getPayloadTypeOptionsGuis();
    payloadOptionsTable = new Hashtable(5, (float).5);

    Hashtable protGuiTable = guiMan.getProtocolCoDecOptionsGuis();
    protOptionsTable = new Hashtable(5, (float).5);;

    //    sessionOptionsTable = guiMan.getSessionOptionsGuis();

    this.setLayout(new BorderLayout(15,15));
  
    mainLO = new GridBagLayout();
    FlowLayout btLO = new FlowLayout(FlowLayout.CENTER, 10, 10);


    // create constraints
    // for normal lables
    GridBagConstraints l_c = new GridBagConstraints();
    l_c.anchor = GridBagConstraints.WEST;
    l_c.fill = GridBagConstraints.NONE;
    l_c.gridheight = 1;
    l_c.gridwidth  = GridBagConstraints.REMAINDER;
    l_c.gridx = 0;
    l_c.gridy = 0;


    // for ccp, pcp, conOptions protOptions
    GridBagConstraints m_c = new GridBagConstraints();
    m_c.anchor = GridBagConstraints.WEST;
    m_c.fill = GridBagConstraints.NONE;
    m_c.gridheight = 1;
    m_c.gridwidth  = 1;
    m_c.insets = new Insets(15, 15, 15, 15);

    // for general use
    GridBagConstraints g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.WEST;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 5;
    g_c.ipady = 5;


    // create button panel 
    buttonPanel = new Panel();
    buttonPanel.setLayout(btLO);

    okButton = new Button("Ok");
    buttonPanel.add(okButton);
    cancelButton = new Button("Cancel");
    buttonPanel.add(cancelButton);



    // the connection choice
    Panel ccp = new Panel();
    ccp.setLayout(mainLO);
    Label l = new Label("Connection:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    ccp.add(l);
    conChoice = new Choice();
    g_c.gridy = 1;
    mainLO.setConstraints(conChoice, g_c);
    ccp.add(conChoice);


    // the payloadType choice
    Panel plp = new Panel();
    plp.setLayout(mainLO);
    l = new Label("PayloadType:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    plp.add(l);
    payloadChoice = new Choice();
    for (Enumeration e = payloadGuiTable.keys(); e.hasMoreElements();) {
      String tmp = (String)(e.nextElement());
      payloadChoice.addItem(tmp);
    }
    payloadChoice.select(0);
    mainLO.setConstraints(payloadChoice, g_c);
    plp.add(payloadChoice);


    // the protocol choice
    Panel pcp = new Panel();
    pcp.setLayout(mainLO);
    l = new Label("Protocol:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    pcp.add(l);
    protChoice = new Choice();
    for (Enumeration e = protGuiTable.keys(); e.hasMoreElements();) {
      String tmp = (String)(e.nextElement());
      protChoice.addItem(tmp);
    }
    protChoice.select(0);
    mainLO.setConstraints(protChoice, g_c);
    pcp.add(protChoice);



    // the connection options
    CardLayout optLO = new CardLayout();
    conOptions = new Panel();
    conOptions.setLayout(optLO);
 
    for (Enumeration e = conGuiTable.keys(); e.hasMoreElements();) {
      String name = (String)(e.nextElement());
      Class tmp = (Class)conGuiTable.get(name);
      ConnectionOptions tmpinc = null;
      ConnectionOptions tmpind = null;
      ConnectionPanel tmpp = null;
      try {
	tmpind = (ConnectionOptions)tmp.newInstance();
	tmpinc = (ConnectionOptions)tmp.newInstance();
      }
      catch(IllegalAccessException ie) {;}
      catch(InstantiationException ie) {;}
      tmpp = new ConnectionPanel(tmpind, tmpinc);
      conChoice.addItem(name);
      conOptionsTable.put(tmpinc.getTypeName(), tmpp);
      conOptions.add(tmpinc.getTypeName(), tmpp);
    }
    conChoice.select(0);
    optLO.first(conOptions);  
  


    // the payloadType options
    optLO = new CardLayout();
    payloadOptions = new Panel();
    payloadOptions.setLayout(optLO);

    for (Enumeration e = payloadGuiTable.keys(); e.hasMoreElements();) {
      String name = (String)(e.nextElement());
      Class tmp = (Class)(payloadGuiTable.get(name));
      PayloadTypeOptions tmpin = null;
      try {
	tmpin = (PayloadTypeOptions)(tmp.newInstance());
      }
      catch(IllegalAccessException ie) {;}
      catch(InstantiationException ie) {;}
      payloadOptionsTable.put(tmpin.getTypeName(), tmpin);
      payloadOptions.add(tmpin.getTypeName(), tmpin.getPanel());
    }
    optLO.first(payloadOptions);  


    // the protocol options
    optLO = new CardLayout();
    protOptions = new Panel();
    protOptions.setLayout(optLO);

    for (Enumeration e = protGuiTable.keys(); e.hasMoreElements();) {
      String name = (String)(e.nextElement());
      Class tmp = (Class)protGuiTable.get(name);
      ProtocolCoDecOptions tmpin = null;
      try {
	tmpin = (ProtocolCoDecOptions)tmp.newInstance();
      }
      catch(IllegalAccessException ie) {;}
      catch(InstantiationException ie) {;}
      protOptionsTable.put(tmpin.getTypeName(), tmpin);
      protOptions.add(tmpin.getTypeName(), tmpin.getPanel(appMan));
    }
    optLO.first(protOptions);  




    generalOptions = new Panel();
    generalOptions.setLayout(mainLO);

    l_c.gridx = 0;
    l_c.gridy = 0;
    l_c.anchor = GridBagConstraints.CENTER;
    l_c.gridwidth = GridBagConstraints.REMAINDER;
    l_c.gridheight = 1;

    l = new Label("General Options:", Label.CENTER);
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);

    String tmp = null;
    try {
      tmp = new String(System.getProperty("user.name")+"@"+(InetAddress.getLocalHost()).getHostName());
    }
    catch(UnknownHostException e) {
      ;
    }

    l = new Label("Initiator Cname:", Label.LEFT);
    l_c.gridy++;
    l_c.gridwidth = 1;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    initiator = new TextField(tmp, 20);
    initiator.setEditable(true);
    l_c.anchor = GridBagConstraints.WEST;
    l_c.gridwidth = GridBagConstraints.REMAINDER;
    l_c.gridx = 1;
    mainLO.setConstraints(initiator, l_c);
    generalOptions.add(initiator);

    exportBox = new Checkbox("Export Session");
    exportBox.setState(true);
    l_c.gridy++;
    mainLO.setConstraints(exportBox, l_c);
    generalOptions.add(exportBox);

    // all four (ccp, pcp, conOptions, protOptions) come together
    Panel p = new Panel();
    p.setLayout(mainLO);

    m_c.anchor = GridBagConstraints.WEST;
    m_c.gridx = 0;  m_c.gridy = 0;
    mainLO.setConstraints(ccp, m_c);
    p.add(ccp);

    m_c.gridwidth  = 1;
    m_c.gridx = 0;  m_c.gridy++;
    mainLO.setConstraints(pcp, m_c);
    p.add(pcp);

    m_c.gridwidth  = 1;
    m_c.gridx = 0;  m_c.gridy++;
    mainLO.setConstraints(plp, m_c);
    p.add(plp);

//     m_c.gridwidth  = 1;
//     m_c.gridx = 0;  m_c.gridy++;
//     mainLO.setConstraints(scp, m_c);
//     p.add(scp);

    m_c.anchor = GridBagConstraints.CENTER;
    m_c.gridx = 1;  m_c.gridy = 0;
    mainLO.setConstraints(conOptions, m_c);
    p.add(conOptions);

    m_c.gridx = 1;  m_c.gridy++;
    mainLO.setConstraints(protOptions, m_c);
    p.add(protOptions);

    m_c.gridx = 1;  m_c.gridy++;
    mainLO.setConstraints(payloadOptions, m_c);
    p.add(payloadOptions);

//     m_c.gridx = 1;  m_c.gridy++;
//     mainLO.setConstraints(sessionOptions, m_c);
//     p.add(sessionOptions);

    m_c.gridwidth = 2;
    m_c.gridx = 0; m_c.gridy++;
    mainLO.setConstraints(generalOptions, m_c);
    p.add(generalOptions);



    // everything is placed in the dialog
    this.add("South", buttonPanel);
    this.add("Center", p);

    this.pack();
  }

  public boolean 
  action(Event e, Object arg) {
    if (e.target instanceof Button) {
      if (e.target == okButton) {
	Connection dacon = null;
	Connection ctcon = null;
	Config cfg = null;
	PayloadType plt = null;
	Session sen = null;
	ConnectionPanel c = (ConnectionPanel)conOptionsTable.get(conChoice.getSelectedItem());
	PayloadTypeOptions pt = (PayloadTypeOptions)payloadOptionsTable.get(payloadChoice.getSelectedItem());
	ProtocolCoDecOptions p = (ProtocolCoDecOptions)protOptionsTable.get(protChoice.getSelectedItem());
	// 	  SessionOptions s = (SessionOptions)sessionOptionsTable.get(sessionChoice.getSelectedItem());
	
	try {
	  dacon = c.dataCO.createConnection();
	}
	catch (Gui_Exception ce) {
	  System.err.println("EXCEPTION: NewSessionDialog.action(): ConnectionOptions.createConnection():: "+ ce.getMessage());
	  ce.printStackTrace();
	  return true;
	}
	try {
	  ctcon = c.controlCO.createConnection();
	}
	catch (Gui_Exception ce) {
	  System.err.println("EXCEPTION: NewSessionDialog.action(): ConnectionOptions.createConnection():: "+ ce.getMessage());
	  ce.printStackTrace();
	  return true;
	}
	try {
	  plt = pt.createPayloadType(appMan);
	}
	catch (Gui_Exception pe) {
	  System.err.println("EXCEPTION: NewSessionDialog.action(): PayloadTypeOptions.createPayloadType():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	try {
	  cfg = p.createConfig(plt, dacon, ctcon);
	}
	catch (Gui_Exception pe) {
	  System.err.println("EXCEPTION: NewSessionDialog.action(): ProtocolCoDecOptions.createConfig():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	try {
	  sen = new Session(appMan, cfg);
	}
	catch (Session_Exception pe) {
	  System.err.println("EXCEPTION: NewSessionDialog.action():  new Session():: "+ pe.getMessage());
	  pe.printStackTrace();
	  return true;
	}
	sen.setExport(exportBox.getState());
	sen.setId(Integer.toString(appMan.getUniqueId()));
	sen.setInitiator(initiator.getText());
	try {
	  appMan.registerSession(sen);
	}
	catch (AppManager_Exception ae) {
	  System.err.println("EXCEPTION: NewSessionDialog.action(): appMan.registerSession():: "+ ae.getMessage());
	  ae.printStackTrace();
	  return true;
	}
	this.hide();
	return true;
      }
      else if (e.target == cancelButton) {
	this.hide();
	return true;
      }
    }
    else if (e.target instanceof Choice) {
      if (e.target == conChoice) {
	((CardLayout)conOptions.getLayout()).show(conOptions, (String)arg);
      }
      else if (e.target == payloadChoice) {
	((CardLayout)payloadOptions.getLayout()).show(payloadOptions, (String)arg);
      }
      else if (e.target == protChoice) {
	((CardLayout)protOptions.getLayout()).show(protOptions, (String)arg);
      }
//       else if (e.target == sessionChoice) {
// 	((CardLayout)sessionOptions.getLayout()).show(sessionOptions, (String)arg);
//       }
      return true;
    }
  
    return false;
  }

  private  Hashtable conOptionsTable;
  private  Hashtable payloadOptionsTable;
  private  Hashtable protOptionsTable;
  private  Hashtable sessionOptionsTable;

  private AppManager appMan;
  private GuiManager guiMan;
  private GridBagLayout mainLO;

  private Choice conChoice;
  private Choice protChoice;
  private Choice payloadChoice;
  private Choice sessionChoice;

  private Panel conOptions;
  private Panel payloadOptions;
  private Panel protOptions;
  private Panel sessionOptions;
  private Panel generalOptions;

  private Checkbox exportBox;
  private TextField initiator;

  private Button okButton;
  private Button cancelButton;
  private Panel buttonPanel;
}

