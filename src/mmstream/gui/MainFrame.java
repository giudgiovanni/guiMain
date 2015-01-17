package mmstream.gui;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.apps.*;
import mmstream.gui.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.producer.*;
import mmstream.consumer.*;

import java.awt.*;
import java.util.*;

public class MainFrame extends Frame implements Output {

  // from interface Output

  public void 
  error(String text) {
    errorArea.appendText(text + "\n");
  }

  public void 
  message(String text) {
    messageArea.appendText(text + "\n");
  }


  public void 
  shutdown() {
    if (exportDialog != null) 
      exportDialog.dispose();
    if (newSessionDialog != null) 
      newSessionDialog.dispose();
    for (Enumeration e = sessionDialogTable.elements(); e.hasMoreElements();) {
      Session_Dialog tmp = (Session_Dialog)(e.nextElement());
      tmp.dispose();
    }
    for (Enumeration e = protoSessionDialogTable.elements(); e.hasMoreElements();) {
      ProtoSession_Dialog tmp = (ProtoSession_Dialog)(e.nextElement());
      tmp.dispose();
    }
    this.dispose();
  }  


  public synchronized void
  deleteProtoSessionDialog(String sen) {
    ProtoSession_Dialog sd = (ProtoSession_Dialog)protoSessionDialogTable.get(sen);
    if (sd != null) {
      protoSessionDialogTable.remove(sen);
      int max = protoSessionList.countItems();
      for (int i = 0; i < max; i++) {
	if (sen.equals(protoSessionList.getItem(i)) == true) {
	  protoSessionList.delItem(i);
	  break;
	}
      }
    }
  }

  public synchronized void
  updateProtoSessionDialog(String sen) {
    ProtoSession_Dialog sd = (ProtoSession_Dialog)protoSessionDialogTable.get(sen);
    if (sd != null) {
	sd.notifyStateChange();
    }
  }

  public synchronized void
  deleteSessionDialog(Session sen) {
    String tmp = sen.getId();
    Session_Dialog sd = (Session_Dialog)sessionDialogTable.get(tmp);
    if (sd != null) {
      sessionDialogTable.remove(tmp);
      int max = sessionList.countItems();
      for (int i = 0; i < max; i++) {
	if (tmp.equals(sessionList.getItem(i)) == true) {
	  sessionList.delItem(i);
	  break;
	}
      }
    }
  }

  public synchronized void
  notifySessionDialog(Session_Dialog sd) {
    String tmp = sd.getId();
    sessionList.addItem(tmp);
    sessionList.getParent().layout();
    sessionDialogTable.put(tmp, sd);
  }

  public synchronized void
  notifyProtoSessionDialog(ProtoSession_Dialog sd) {
    String tmp = sd.getId();
    protoSessionList.addItem(tmp);
    protoSessionList.getParent().layout();
    protoSessionDialogTable.put(tmp, sd);
  }

  public synchronized void 
  notifyRemoteSource(Session s, RemoteSource sd) {
    String tmp = s.getId();
    Session_Dialog ssd = (Session_Dialog)sessionDialogTable.get(tmp);
    if (ssd != null) {
      ssd.notifyRemoteSource(sd);
    }
  }

  public synchronized void 
  notifyLocalSource(Session s, LocalSource sd) {
    String tmp = s.getId();
    Session_Dialog ssd = (Session_Dialog)sessionDialogTable.get(tmp);
    if (ssd != null) {
      ssd.notifyLocalSource(sd);
    }
  }

  public synchronized void 
  notifyConsumer(Session s, ConsumerControl sd) {
    String tmp = s.getId();
    Session_Dialog ssd = (Session_Dialog)sessionDialogTable.get(tmp);
    if (ssd != null) {
      ssd.notifyConsumer(sd);
    }
  }


  public synchronized void 
  notifyProducer(Session s, ProducerControl sd) {
    String tmp = s.getId();
    Session_Dialog ssd = (Session_Dialog)sessionDialogTable.get(tmp);
    if (ssd != null) {
      ssd.notifyProducer(sd);
    }
  }




  // Constructor
  public MainFrame(AppManager am) {

    super("TOMS");
    newSessionDialog = null;

    appMan = am;
    guiMan = appMan.getGuiManager();

    
    sessionDialogTable = new Hashtable(5, (float)0.5);
    protoSessionDialogTable = new Hashtable(5, (float)0.5);

    // the menu bar
    menuBar = new MenuBar();
    this.setMenuBar(menuBar);

    // file menu
    fileMenu = new Menu("File");
    quitButton = new MenuItem("Quit");
    fileMenu.add(quitButton);
    menuBar.add(fileMenu);
  
    // session menu
    sessionMenu = new Menu("Session");
    newSessionMenuButton = new MenuItem("New");
    sessionMenu.add(newSessionMenuButton);
    menuBar.add(sessionMenu);

    // options menu
    optionsMenu = new Menu("Options");
    exportMenuButton = new MenuItem("Export&Import");
    optionsMenu.add(exportMenuButton);
    menuBar.add(optionsMenu);

    // some layout
    FlowLayout btLO = new FlowLayout(FlowLayout.CENTER, 15, 15);
    GridBagLayout gbl = new GridBagLayout();
    BorderLayout bdLO = new BorderLayout(15, 15);

    this.setLayout(bdLO);

    GridBagConstraints g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 5;
    g_c.ipady = 5;

    Panel p = new Panel();
    p.setLayout(gbl);

    Panel p1a = new Panel();
    p1a.setLayout(gbl);

    Label l = new Label("Sessions:", Label.CENTER);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    gbl.setConstraints(l, g_c);
    p1a.add(l);

    sessionList = new List(5, false);
    fillList(sessionList, 10);
    g_c.gridheight = GridBagConstraints.REMAINDER;
    g_c.gridx = 0;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy = 1;
    gbl.setConstraints(sessionList, g_c);
    p1a.add(sessionList);

    Panel p1b = new Panel();
    p1b.setLayout(gbl);

    l = new Label("Proto Sessions:", Label.CENTER);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.gridheight = 1;
    g_c.weightx = 0;
    g_c.weighty = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 0;
    g_c.gridy = 0;
    gbl.setConstraints(l, g_c);
    p1b.add(l);

    protoSessionList = new List(5, false);
    fillList(protoSessionList, 10);
    g_c.gridheight = GridBagConstraints.REMAINDER;
    g_c.weighty = 10;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridy = 1;
    gbl.setConstraints(protoSessionList, g_c);
    p1b.add(protoSessionList);

    Panel p2 = new Panel();
    p2.setLayout(gbl);

    g_c.anchor = GridBagConstraints.CENTER;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;

    l = new Label("Messages:", Label.CENTER);
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.weighty = 0;
    g_c.weightx = 0;
    g_c.fill = GridBagConstraints.NONE;
    gbl.setConstraints(l, g_c);
    p2.add(l);
    messageArea = new TextArea(5,10);
    messageArea.setEditable(false);
    g_c.gridy = 1;
    g_c.weighty = 10;
    g_c.weightx = 10;
    g_c.fill = GridBagConstraints.BOTH;
    gbl.setConstraints(messageArea, g_c);
    p2.add(messageArea);

    l = new Label("Errors:", Label.CENTER);
    g_c.weighty = 0;
    g_c.weightx = 0;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridx = 1;
    g_c.gridy = 0;
    gbl.setConstraints(l, g_c);
    p2.add(l);
    errorArea = new TextArea(5, 10);
    errorArea.setEditable(false);
    g_c.gridy = 1;
    g_c.fill = GridBagConstraints.BOTH;
    g_c.weighty = 10;
    g_c.weightx = 10;
    gbl.setConstraints(errorArea, g_c);
    p2.add(errorArea);

    g_c.insets = new Insets(5,5,5,5);
    g_c.anchor = GridBagConstraints.NORTH;
    g_c.gridwidth = 1;
    g_c.gridheight = GridBagConstraints.REMAINDER;
    g_c.fill = GridBagConstraints.VERTICAL;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.weightx = 0;
    gbl.setConstraints(p1b, g_c);
    p.add(p1b);

    g_c.gridx++;
    gbl.setConstraints(p1a, g_c);
    p.add(p1a);

    g_c.gridwidth = GridBagConstraints.REMAINDER;
    g_c.weightx = 10;
    g_c.gridx++;
    g_c.fill = GridBagConstraints.BOTH;
    gbl.setConstraints(p2, g_c);
    p.add(p2);

    Panel bp = new Panel();
    bp.setLayout(btLO);
    newSessionButton = new Button("New Session");
    bp.add(newSessionButton);
    joinSessionButton = new Button("Join Session");
    bp.add(joinSessionButton);
    showProto = new Button("Show ProtoSession");
    bp.add(showProto);
    showSession = new Button("Show Session");
    bp.add(showSession);
    deleteSession = new Button("Delete Session");
    bp.add(deleteSession);

    this.add("Center", p);
    this.add("South", bp);


    this.resize(new Dimension(600, 400));
    this.pack();
    emptyList(sessionList, 10);
    emptyList(protoSessionList, 10);
//     fillList(sessionList, 10);
//     fillList(protoSessionList, 10);
//     emptyList(sessionList, 10);
//     emptyList(protoSessionList, 10);
    exportDialog = null;

  }

  public boolean handleEvent(Event evt) {
    switch (evt.id) {
    case Event.ACTION_EVENT:

      // Quit
      if (evt.target == quitButton) {
	appMan.finish("User requested quit");
      }
      // New Session
      else if (evt.target == newSessionMenuButton || evt.target == newSessionButton) {
	if (newSessionDialog == null) {
	  newSessionDialog = new NewSession_Dialog(appMan);
	  newSessionDialog.pack();
	}
	newSessionDialog.show();
	newSessionDialog.pack();
      }
      // New Session
      else if (evt.target == exportMenuButton) {
	if (exportDialog == null) {
	  exportDialog = new ExportImport_Dialog(appMan);
	  exportDialog.pack();
	  exportDialog.hide();
	}
	exportDialog.show();
	exportDialog.pack();
      }
      // Join Session
      else if (evt.target == joinSessionButton) {
	String tmp = protoSessionList.getSelectedItem();
	if (tmp != null) {
	  ProtoSession_Dialog sd = (ProtoSession_Dialog)(protoSessionDialogTable.get(tmp));
	  if (sd != null) {
	    sd.show();
	    sd.pack();
	  }
	}
      }
      // show the session selected by double click
      else if (evt.target == sessionList) {
	if (evt.arg != null) {
	  Session_Dialog sd = (Session_Dialog)(sessionDialogTable.get(evt.arg));
	  if (sd != null) {
	    sd.show();
	    sd.pack();
	  }
	}
      }
      else if (evt.target == protoSessionList) {
	if (evt.arg != null) {
	  ProtoSession_Dialog sd = (ProtoSession_Dialog)(protoSessionDialogTable.get(evt.arg));
	  if (sd != null) {
	    sd.show();
	    sd.pack();
	  }
	}
      }
      // show the selected session
      else if (evt.target == showSession) {
	String tmp = sessionList.getSelectedItem();
	if (tmp != null) {
	  Session_Dialog sd = (Session_Dialog)(sessionDialogTable.get(tmp));
	  if (sd != null) {
	    sd.show();
	    sd.pack();
	  }
	}
      }
      else if (evt.target == showProto) {
	String tmp = protoSessionList.getSelectedItem();
	if (tmp != null) {
	  ProtoSession_Dialog sd = (ProtoSession_Dialog)(protoSessionDialogTable.get(tmp));
	  if (sd != null) {
	    sd.show();
	    sd.pack();
	  }
	}
      }
      else if (evt.target == deleteSession) {
	String tmp = sessionList.getSelectedItem();
	if (tmp != null) {
	  Session_Dialog sd = (Session_Dialog)sessionDialogTable.get(tmp);
	  if (sd != null) {
	    try {
	      appMan.deleteSession(tmp, "User requested");
	    }
	    catch(AppManager_Exception ae) {
	      this.error("Exception: "+ae.getMessage());
	    }
	  }
	}
      }
      else {
	return false;
      }
      return true;
    }
    return false;
    
  }


  // HACK...or call it work-around?
  public static void
  fillList(List list, int len) {
    String dummy = "TOMS";
    for (int i =0; i < len; i++) {
      list.addItem(dummy);
    }
  }

  public static void
  emptyList(List list, int len) {
    for (int i =0; i < len; i++) {
      list.delItem(0);
    }
  }

  private MenuBar menuBar;

  private Menu fileMenu;
  private MenuItem quitButton;

  private Menu sessionMenu;
  private MenuItem newSessionMenuButton;

  private Menu optionsMenu;
  private MenuItem exportMenuButton;

  private TextArea messageArea;
  private TextArea errorArea;
  private List sessionList;
  private List protoSessionList;
  private Button showSession;
  private Button showProto;
  private Button deleteSession;
  private Button newSessionButton;
  private Button joinSessionButton;

  private Hashtable sessionDialogTable;
  private Hashtable protoSessionDialogTable;

  private NewSession_Dialog newSessionDialog;

  private ExportImport_Dialog exportDialog;

  private AppManager appMan;
  private GuiManager guiMan;
}

