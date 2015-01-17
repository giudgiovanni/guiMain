package mmstream.gui;

import mmstream.source.*;
import mmstream.gui.*;
import mmstream.util.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;
import mmstream.consumer.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;
import java.util.*;


public class NewConsumer_Dialog extends Dialog {

public void setSources(Hashtable rst) {
  sourceTable = rst;
  
  
  if (sourceList.countItems() > 0)
    sourceList.clear();

  for (Enumeration e = sourceTable.keys(); e.hasMoreElements();) {
    String s = (String)(e.nextElement());
    sourceList.addItem(s);
  }
  if (sourceList.countItems() > 0) {
    sourceList.select(0);
    sourceList.enable();
    listenBox.setState(false);
  }
  else {
    sourceList.disable();
    listenBox.setState(true);
  }
}

public NewConsumer_Dialog(AppManager am, Session ses) {
    
    super((am.getGuiManager()).getRoot(), "New Consumer", false);

    appMan = am;
    guiMan = appMan.getGuiManager();
    session = ses;

    Hashtable consGuiTable = guiMan.getConsumerOptionsGuis();
    consOptionsTable = new Hashtable(5, (float).5);

    sourceTable = null;
    

    this.setLayout(new BorderLayout(15,15));
  
    mainLO = new GridBagLayout();
    FlowLayout btLO = new FlowLayout(FlowLayout.CENTER, 10, 10);


    GridBagConstraints l_c = new GridBagConstraints();
    l_c.anchor = GridBagConstraints.WEST;
    l_c.fill = GridBagConstraints.NONE;
    l_c.gridheight = 1;
    l_c.gridwidth  = GridBagConstraints.REMAINDER;
    l_c.gridx = 0;
    l_c.gridy = 0;
    l_c.insets = new Insets(5, 5, 5, 5);

        // create button panel 
    buttonPanel = new Panel();
    buttonPanel.setLayout(btLO);

    okButton = new Button("Ok");
    buttonPanel.add(okButton);
    cancelButton = new Button("Cancel");
    buttonPanel.add(cancelButton);



    // the consumer choice
    Panel ccp = new Panel();
    ccp.setLayout(mainLO);
    Label l = new Label("Type:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    ccp.add(l);
    consChoice = new Choice();
    l_c.gridy = 1;
    mainLO.setConstraints(consChoice, l_c);
    ccp.add(consChoice);


    // the consumer options
    CardLayout optLO = new CardLayout();
    consOptions = new Panel();
    consOptions.setLayout(optLO);
 
    Vector cls = appMan.getConsumerTypeHandlerTable().queryClasses(session.getProfile().getPayloadType());
    if (cls != null) {
      for (Enumeration e = cls.elements(); e.hasMoreElements();) {
	TypeHandler th = (TypeHandler)(e.nextElement());
	String name = th.getTypeName();
	Class tmp = (Class)consGuiTable.get(name);
	consChoice.addItem(name);
	ConsumerOptions tmpin = null;
	try {
	  tmpin = (ConsumerOptions)tmp.newInstance();
	}
	catch(IllegalAccessException ie) {;}
	catch(InstantiationException ie) {;}
	consOptionsTable.put(tmpin.getTypeName(), tmpin);
	consOptions.add(tmpin.getTypeName(), tmpin.getPanel(appMan));
      }
      consChoice.select(0);
      optLO.first(consOptions);  
    }

    Vector anycls = appMan.getConsumerTypeHandlerTable().queryClasses(appMan.getPayloadTypeHandlerTable().queryType(Payload_TypeConfiguration.PT_ANY_Name));
    if (cls != null) {
      for (Enumeration e = anycls.elements(); e.hasMoreElements();) {
	TypeHandler th = (TypeHandler)(e.nextElement());
	String name = th.getTypeName();
	Class tmp = (Class)consGuiTable.get(name);
	consChoice.addItem(name);
	ConsumerOptions tmpin = null;
	try {
	  tmpin = (ConsumerOptions)tmp.newInstance();
	}
	catch(IllegalAccessException ie) {;}
	catch(InstantiationException ie) {;}
	consOptionsTable.put(tmpin.getTypeName(), tmpin);
	consOptions.add(tmpin.getTypeName(), tmpin.getPanel(appMan));
      }
      consChoice.select(0);
      optLO.first(consOptions);  
    }
    else {
      session.getOutput().error("No Consumers for PayloadType "+session.getProfile().getPayloadType().getName());
      okButton.disable();
    }


    generalOptions = new Panel();
    generalOptions.setLayout(mainLO);

    l_c.gridx = 0;
    l_c.gridy = 0;
    l_c.anchor = GridBagConstraints.CENTER;
    l_c.gridwidth = 1;
    l_c.gridheight = 1;

    l = new Label("Source Options:", Label.CENTER);
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);

    l = new Label("Sources:", Label.CENTER);
    l_c.gridwidth = GridBagConstraints.REMAINDER;
    l_c.gridx++;
    l_c.gridy = 0;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    sourceList = new List(5, false);
    MainFrame.fillList(sourceList, 10);
    l_c.gridy++;
    l_c.weighty = 10;    
    l_c.fill = GridBagConstraints.VERTICAL;
    mainLO.setConstraints(sourceList, l_c);
    generalOptions.add(sourceList);

    listenBox = new Checkbox("Listen for new souce");
    listenBox.setState(true);
    l_c.weighty = 0;    
    l_c.fill = GridBagConstraints.NONE;
    l_c.gridy++;
    mainLO.setConstraints(listenBox, l_c);
    generalOptions.add(listenBox);

    l = new Label("Import Options:", Label.CENTER);
    l_c.gridy++;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    
    copyBox = new Checkbox("Get separate copy of data");
    copyBox.setState(false);
    l_c.gridy++;
    mainLO.setConstraints(copyBox, l_c);
    generalOptions.add(copyBox);

    posBox = new Checkbox("Playout Sync");
    posBox.setState(true);
    l_c.gridy++;
    mainLO.setConstraints(posBox, l_c);
    generalOptions.add(posBox);

    l = new Label("General Options:", Label.CENTER);
    l_c.gridy++;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);

    l = new Label("Priority:", Label.LEFT);
    l_c.gridy++;
    l_c.gridwidth = 1;
    l_c.anchor = GridBagConstraints.WEST;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    ptf = new TextField(String.valueOf(Thread.NORM_PRIORITY + 2), 2);
    ptf.setEditable(true);
    l_c.gridx++;
    mainLO.setConstraints(ptf, l_c);
    generalOptions.add(ptf);
    
    startBox = new Checkbox("Start immediately");
    startBox.setState(false);
    l_c.gridx = 0;
    l_c.gridy++;
    mainLO.setConstraints(startBox, l_c);
    generalOptions.add(startBox);

//     automaticBox = new Checkbox("Create same for every stream");
//     automaticBox.setState(false);
//     l_c.gridy++;
//     mainLO.setConstraints(automaticBox, l_c);
//     generalOptions.add(automaticBox);

    // all four (ccp, consOptions, generalOptions) come together
    Panel p = new Panel();
    p.setLayout(mainLO);

    l_c.insets = new Insets(15, 15, 15, 15);
    l_c.anchor = GridBagConstraints.WEST;
    l_c.gridx = 0;  l_c.gridy = 0;
    l_c.gridwidth = 1;
    mainLO.setConstraints(ccp, l_c);
    p.add(ccp);



    l_c.anchor = GridBagConstraints.CENTER;
    l_c.gridx = 1;  l_c.gridy = 0;
    mainLO.setConstraints(consOptions, l_c);
    p.add(consOptions);

    l_c.gridwidth = 2;
    l_c.gridx = 0; l_c.gridy++;
    l_c.weighty = 10;    
    l_c.fill = GridBagConstraints.VERTICAL;
    mainLO.setConstraints(generalOptions, l_c);
    p.add(generalOptions);



    // everything is placed in the dialog
    this.add("South", buttonPanel);
    this.add("Center", p);

    this.pack();
    MainFrame.emptyList(sourceList, 10);
  }

  public boolean 
  action(Event e, Object arg) {
    if (e.target instanceof Button) {
      if (e.target == okButton) {
	this.disable();
	ConsumerControl cons = null;
	ConsumerOptions c = (ConsumerOptions)consOptionsTable.get(consChoice.getSelectedItem());

	if (listenBox.getState() == true) {
	  try {
	    cons = c.createConsumer(session);
	  }
	  catch (Gui_Exception ce) {
	    System.err.println("EXCEPTION: NewConsumer_Dialog.action(): ConsumerOptions.createConsumer():: "+ ce.getMessage());
	    ce.printStackTrace();
	    this.enable();
	    return true;
	  }
	}
	else {	
	  String rsn = sourceList.getSelectedItem();
	  if (rsn != null && sourceTable != null) {
	    Source rs = (Source)sourceTable.get(rsn);
	    try {
	      cons = c.createConsumer(session, rs);
	    }
	    catch (Gui_Exception ce) {
	      System.err.println("EXCEPTION: NewConsumer_Dialog.action(): ConsumerOptions.createConsumer():: "+ ce.getMessage());
	      ce.printStackTrace();
	      this.enable();
	      return true;
	    }
	  }
	  else { 
	    this.enable();
	    return true;
	  }
	}
	cons.setCopy(copyBox.getState());
	cons.setPlayOutSync(posBox.getState());

	int prio = Thread.NORM_PRIORITY;
	try {
	  prio = Integer.parseInt(ptf.getText());
	}
	catch(NumberFormatException ne) {
	  System.err.println("EXCEPTION: NewConsumer_Dialog.action(): Integer.parseInt():: "+ne.getMessage());
	  ne.printStackTrace();
	  this.enable();
	  return true;
	}

	try {
	  appMan.registerConsumer(session, cons, startBox.getState(), prio);
	}
	catch(AppManager_Exception ce) {
	  System.err.println("EXCEPTION: NewConsumer_Dialog.action(): appMan.registerConsumer()::"+ce.getMessage());
	  ce.printStackTrace();
	  this.enable();
	  return true;
	}
	this.enable();
	this.hide();
	return true;
      }
      else if (e.target == cancelButton) {
	this.hide();
	return true;
      }
    }
    else if (e.target instanceof Choice) {
      if (e.target == consChoice) {
	((CardLayout)consOptions.getLayout()).show(consOptions, (String)arg);
      }
      return true;
    }
    else if (e.target instanceof Checkbox) {
      if (e.target == listenBox) {
	if (listenBox.getState() == true)
	  sourceList.disable();
	else
	  sourceList.enable();
      }
      return true;
    }
  
    return false;
  }


  private  Hashtable consOptionsTable;
  private AppManager appMan;
  private GuiManager guiMan;
  private Session session;

  private GridBagLayout mainLO;
  
  private Choice consChoice;
  private Panel consOptions;
  private Panel generalOptions;

  private Checkbox automaticBox;
  private Checkbox copyBox;
  private Checkbox posBox;
  private Checkbox listenBox;
  private Checkbox startBox;
  private TextField ptf;

  private Button okButton;
  private Button cancelButton;
  private Panel buttonPanel;

  private List sourceList;
  private Hashtable sourceTable;
}

