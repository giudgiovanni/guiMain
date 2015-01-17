package mmstream.gui;

import mmstream.address.*;
import mmstream.util.*;
import mmstream.gui.*;
import mmstream.session.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;
import java.util.*;


public class ExportImport_Dialog extends Dialog {

  public ExportImport_Dialog(AppManager am) {
    super((am.getGuiManager()).getRoot(), "Export & Import", false);

    appMan = am;
    guiMan = appMan.getGuiManager();
  
    exportTable = new Hashtable(5, (float).5);
    importTable = new Hashtable(5, (float).5);

    Hashtable conGuiTable = guiMan.getConnectionOptionsGuis();
    conOptionsTable = new Hashtable(5, (float).5);

    Hashtable smGuiTable = guiMan.getSessionMapperOptionsGuis();
    smOptionsTable = new Hashtable(5, (float).5);

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
    l_c.insets = new Insets(5, 5, 5, 5);

    // for ccp, scp, pcp, conOptions protOptions
    GridBagConstraints m_c = new GridBagConstraints();
    m_c.anchor = GridBagConstraints.WEST;
    m_c.fill = GridBagConstraints.NONE;
    m_c.gridheight = 1;
    m_c.gridwidth  = 1;
    m_c.insets = new Insets(15, 15, 15, 15);

    
    // create button panel 
    buttonPanel = new Panel();
    buttonPanel.setLayout(btLO);

    addButton = new Button("Add");
    buttonPanel.add(addButton);
    deleteImport = new Button("Delete Import");
    buttonPanel.add(deleteImport);
    deleteExport = new Button("Delete Export");
    buttonPanel.add(deleteExport);
    hideButton = new Button("Hide");
    buttonPanel.add(hideButton);


    // the connection choice
    Panel ccp = new Panel();
    ccp.setLayout(mainLO);
    Label l = new Label("Connection:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    ccp.add(l);
    conChoice = new Choice();
    for (Enumeration e = conGuiTable.keys(); e.hasMoreElements();) {
      String tmp = (String)(e.nextElement());
      conChoice.addItem(tmp);
      conChoice.select(0);
    }
    l_c.gridwidth  = 1;
    l_c.gridy = 1;
    mainLO.setConstraints(conChoice, l_c);
    ccp.add(conChoice);


    // the connection options
    CardLayout optLO = new CardLayout();
    conOptions = new Panel();
    conOptions.setLayout(optLO);
 
    for (Enumeration e = conGuiTable.keys(); e.hasMoreElements();) {
      String name = (String)(e.nextElement());
      Class tmp = (Class)(conGuiTable.get(name));
      ConnectionOptions tmpin = null;
      try {
	tmpin = (ConnectionOptions)tmp.newInstance();
      }
      catch(IllegalAccessException ie) {;}
      catch(InstantiationException ie) {;}
      conOptionsTable.put(tmpin.getTypeName(), tmpin);
      conOptions.add(tmpin.getTypeName(), tmpin.getPanel());
    }
    optLO.first(conOptions);  

    // the sessionMapper choice
    Panel scp = new Panel();
    scp.setLayout(mainLO);
    l = new Label("SessionMapper:", Label.LEFT);
    l_c.gridwidth  = GridBagConstraints.REMAINDER;
    l_c.gridy = 0;
    mainLO.setConstraints(l, l_c);
    scp.add(l);
    smChoice = new Choice();
    for (Enumeration e = smGuiTable.keys(); e.hasMoreElements();) {
      String tmp = (String)(e.nextElement());
      smChoice.addItem(tmp);
      smChoice.select(0);
    }
    l_c.gridwidth  = 1;
    l_c.gridy = 1;
    mainLO.setConstraints(smChoice, l_c);
    scp.add(smChoice);


    // the sessionMapper options
    smOptions = new Panel();
    smOptions.setLayout(optLO);
 
    for (Enumeration e = smGuiTable.keys(); e.hasMoreElements();) {
      String name = (String)(e.nextElement());
      Class tmp = (Class)(smGuiTable.get(name));
      SessionMapperOptions tmpin = null;
      try {
	tmpin = (SessionMapperOptions)tmp.newInstance();
      }
      catch(IllegalAccessException ie) {;}
      catch(InstantiationException ie) {;}
      smOptionsTable.put(tmpin.getName(), tmpin);
      smOptions.add(tmpin.getName(), tmpin.getPanel());
    }
    optLO.first(smOptions);  

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
    
    l = new Label("Export Interval:", Label.LEFT);
    l_c.gridwidth = 1;
    l_c.gridx = 0;
    l_c.gridy++;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    repeat_tf = new TextField("20", 15);
    repeat_tf.setEditable(true);
    l_c.gridwidth = GridBagConstraints.REMAINDER;
    l_c.gridx = 1;
    mainLO.setConstraints(repeat_tf, l_c);
    generalOptions.add(repeat_tf);

    importBox = new Checkbox("Import");
    importBox.setState(true);
    l_c.gridwidth = 1;
    l_c.gridx = 0;
    l_c.gridy++;
    mainLO.setConstraints(importBox, l_c);
    generalOptions.add(importBox);

    exportBox = new Checkbox("Export");
    exportBox.setState(true);
    l_c.gridy++;
    mainLO.setConstraints(exportBox, l_c);
    generalOptions.add(exportBox);

    // connection list
    Panel ap = new Panel();
    ap.setLayout(mainLO);

    l_c.gridx = 0;
    l_c.gridy = 0;
    l_c.anchor = GridBagConstraints.CENTER;
    l_c.gridwidth = GridBagConstraints.REMAINDER;
    l_c.gridheight = 1;
    l = new Label("Connections:", Label.CENTER);
    mainLO.setConstraints(l, l_c);
    ap.add(l);

    l = new Label("Export:", Label.CENTER);
    l_c.gridwidth = 1;
    l_c.gridy = 1;
    mainLO.setConstraints(l, l_c);
    ap.add(l);
    eList = new List(5, false);
    l_c.gridy++;
    mainLO.setConstraints(eList, l_c);
    ap.add(eList);

    l = new Label("Import:", Label.CENTER);
    l_c.gridx = 1;
    l_c.gridy = 1;
    mainLO.setConstraints(l, l_c);
    ap.add(l);
    iList = new List(5, false);
    l_c.gridy++;
    mainLO.setConstraints(iList, l_c);
    ap.add(iList);


    // all come together
    Panel p = new Panel();
    p.setLayout(mainLO);

    m_c.anchor = GridBagConstraints.WEST;
    m_c.gridx = 0; 
    m_c.gridy = 0;
    mainLO.setConstraints(ccp, m_c);
    p.add(ccp);
    m_c.anchor = GridBagConstraints.CENTER;
    m_c.gridx++;
    mainLO.setConstraints(conOptions, m_c);
    p.add(conOptions);

    m_c.anchor = GridBagConstraints.WEST;
    m_c.gridy++;
    m_c.gridx = 0; 
    mainLO.setConstraints(scp, m_c);
    p.add(scp);
    m_c.anchor = GridBagConstraints.CENTER;
    m_c.gridx++;
    mainLO.setConstraints(smOptions, m_c);
    p.add(smOptions);

    m_c.gridwidth = 2;
    m_c.gridx = 0; m_c.gridy++;
    mainLO.setConstraints(generalOptions, m_c);
    p.add(generalOptions);

    m_c.gridwidth = 1;
    m_c.gridx = 2; m_c.gridy = 0;
    mainLO.setConstraints(ap, m_c);
    p.add(ap);



    // everything is placed in the dialog
    this.add("South", buttonPanel);
    this.add("Center", p);

    this.pack();
  }

  public boolean 
  action(Event e, Object arg) {
    if (e.target instanceof Button) {
      if (e.target == addButton) {
	{
	  Connection con = null;
	  ConnectionOptions co = (ConnectionOptions)conOptionsTable.get(conChoice.getSelectedItem());
	
	  try {
	    con = co.createConnection();
	  }
	  catch (Gui_Exception ce) {
	    System.err.println("EXCEPTION: ExportImport_Dialog.action(): ConnectionOptions.createConnection():: "+ ce.getMessage());
	    ce.printStackTrace();
	    return true;
	  }
	  SessionMapper sm = null;
	  SessionMapperOptions smo = (SessionMapperOptions)smOptionsTable.get(smChoice.getSelectedItem());
	
	  try {
	    sm = smo.createSessionMapper(appMan);
	  }
	  catch (Gui_Exception ce) {
	    System.err.println("EXCEPTION: ExportImport_Dialog.action(): SessionMapperOptions.createSessionMapper():: "+ ce.getMessage());
	    ce.printStackTrace();
	    return true;
	  }
	  SessionAgent sa = appMan.getSessionAgent();
	  boolean ex = exportBox.getState();
	  boolean im = importBox.getState();
	  if (ex) {
	    long ri = 0;
	    try {
	      ri = 1000L * Long.parseLong(repeat_tf.getText());
	    }
	    catch(NumberFormatException ne) {
	      System.err.println("EXCEPTION: ExportImport_Dialog.action(): Long.parseLong():: "+ne.getMessage());
	      return true;
	    }
	    try {
	      sa.addSessionExport(con, sm, ri);
	    }
	    catch (Session_Exception ce) {
	      System.err.println("EXCEPTION: ExportImport_Dialog.action(): sessionAgent.addSessionExport():: "+ ce.getMessage());
	      ce.printStackTrace();
	      return true;
	    }
	    exportTable.put(con.getRemoteAddress().toString(), con.getRemoteAddress());
	    eList.addItem(con.getRemoteAddress().toString());
	  }
 	  if (im) {
	    try {
	      sa.addSessionImport(con, sm);
	    }
	    catch (Session_Exception ce) {
	      System.err.println("EXCEPTION: ExportImport_Dialog.action(): sessionAgent.addSessionImport():: "+ ce.getMessage());
	      ce.printStackTrace();
	      return true;
	    }
	    importTable.put(con.getLocalAddress().toString(), con.getLocalAddress());
	    iList.addItem(con.getLocalAddress().toString());
	  }
	  
	}
	this.hide();
	return true;
      }
      else if (e.target == hideButton) {
	this.hide();
	return true;
      }
    }

    else if (e.target instanceof Choice) {
      if (e.target == conChoice) {
	((CardLayout)conOptions.getLayout()).show(conOptions, (String)arg);
	return true;
      }
      if (e.target == smChoice) {
	((CardLayout)smOptions.getLayout()).show(smOptions, (String)arg);
	return true;
      }
      return false;
    }
  
    else if (e.target == deleteExport) {
      int tmp = eList.getSelectedIndex();
      String nm = eList.getSelectedItem();
      if (tmp != -1) {
	Connection con = (Connection)exportTable.get(nm);
	appMan.getSessionAgent().deleteSessionExport(con);
	exportTable.remove(nm);
	eList.delItem(tmp);
      }
      
      return true;
    }
    else if (e.target == deleteImport) {
      int tmp = iList.getSelectedIndex();
      String nm = iList.getSelectedItem();
      if (tmp != -1) {
	Connection con = (Connection)importTable.get(nm);
	appMan.getSessionAgent().deleteSessionImport(con);
	importTable.remove(nm);
	iList.delItem(tmp);
      }
      
      return true;
    }
    return false;
  }

  private  Hashtable smOptionsTable;
  private  Hashtable conOptionsTable;

  private AppManager appMan;
  private GuiManager guiMan;
  private GridBagLayout mainLO;

  private Choice smChoice;
  private Choice conChoice;

  private Panel smOptions;
  private Panel conOptions;

  private Panel generalOptions;

  private Checkbox importBox;
  private Checkbox exportBox;

  private Button addButton;
  private Button deleteImport;
  private Button deleteExport;
  private Button hideButton;
  private Panel buttonPanel;
  private TextField repeat_tf;

  private List eList;
  private List iList;

  private Hashtable exportTable;
  private Hashtable importTable;
}



