package mmstream.gui;

import mmstream.source.*;
import mmstream.gui.*;
import mmstream.util.*;
import mmstream.session.*;
import mmstream.stream.*;
import mmstream.apps.*;
import mmstream.producer.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.stream.*;
import mmstream.config.*;

import java.awt.*;
import java.util.*;


public class NewProducer_Dialog extends Dialog {

  public NewProducer_Dialog(AppManager am, Session ses) {
    
    super((am.getGuiManager()).getRoot(), "New Producer", false);

    appMan = am;
    guiMan = appMan.getGuiManager();
    session = ses;

    Hashtable prodGuiTable = guiMan.getProducerOptionsGuis();
    prodOptionsTable = new Hashtable(5, (float).5);

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



    // the producer choice
    Panel ccp = new Panel();
    ccp.setLayout(mainLO);
    Label l = new Label("Type:", Label.LEFT);
    mainLO.setConstraints(l, l_c);
    ccp.add(l);
    prodChoice = new Choice();
    l_c.gridy = 1;
    mainLO.setConstraints(prodChoice, l_c);
    ccp.add(prodChoice);


    // the producer options
    CardLayout optLO = new CardLayout();
    prodOptions = new Panel();
    prodOptions.setLayout(optLO);
 
    Vector cls = appMan.getProducerTypeHandlerTable().queryClasses(session.getProfile().getPayloadType());
    if (cls != null) { 
      for (Enumeration e = cls.elements(); e.hasMoreElements();) {
	TypeHandler th = (TypeHandler)(e.nextElement());
	String name = th.getTypeName();
	prodChoice.addItem(name);
	Class tmp = (Class)prodGuiTable.get(name);
	ProducerOptions tmpin = null;
	try {
	  tmpin = (ProducerOptions)tmp.newInstance();
	}
	catch(IllegalAccessException ie) {;}
	catch(InstantiationException ie) {;}
	prodOptionsTable.put(tmpin.getTypeName(), tmpin);
	prodOptions.add(tmpin.getTypeName(), tmpin.getPanel(appMan));
      }
      prodChoice.select(0);
      optLO.first(prodOptions);  
    }

    Vector anycls = appMan.getProducerTypeHandlerTable().queryClasses(appMan.getPayloadTypeHandlerTable().queryType(Payload_TypeConfiguration.PT_ANY_Name));
    if (anycls != null) { 
      for (Enumeration e = anycls.elements(); e.hasMoreElements();) {
	TypeHandler th = (TypeHandler)(e.nextElement());
	String name = th.getTypeName();
	prodChoice.addItem(name);
	Class tmp = (Class)prodGuiTable.get(name);
	ProducerOptions tmpin = null;
	try {
	  tmpin = (ProducerOptions)tmp.newInstance();
	}
	catch(IllegalAccessException ie) {;}
	catch(InstantiationException ie) {;}
	prodOptionsTable.put(tmpin.getTypeName(), tmpin);
	prodOptions.add(tmpin.getTypeName(), tmpin.getPanel(appMan));
      }
      prodChoice.select(0);
      optLO.first(prodOptions);  
    }
    if (prodChoice.countItems() == 0) {
      session.getOutput().error("No Producers for PayloadType "+session.getProfile().getPayloadType().getName());
      okButton.disable();
    }
    


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
    
    l = new Label("Priority:", Label.LEFT);
    l_c.gridy++;
    l_c.gridwidth = 1;
    l_c.anchor = GridBagConstraints.WEST;
    mainLO.setConstraints(l, l_c);
    generalOptions.add(l);
    ptf = new TextField(String.valueOf(Thread.NORM_PRIORITY + 1), 2);
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

    // all four (ccp, prodOptions, generalOptions) come together
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
    mainLO.setConstraints(prodOptions, l_c);
    p.add(prodOptions);

    l_c.gridwidth = 2;
    l_c.gridx = 0; l_c.gridy++;
    mainLO.setConstraints(generalOptions, l_c);
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
	this.disable();
	ProducerControl prod = null;
	ProducerOptions c = (ProducerOptions)prodOptionsTable.get(prodChoice.getSelectedItem());
	try {
	  prod = c.createProducer(session);
	}
	catch (Gui_Exception ce) {
	  System.err.println("EXCEPTION: NewProducer_Dialog.action(): ProducerOptions.createProducer():: "+ ce.getMessage());
	  ce.printStackTrace();
	  this.enable();
	  return true;
	}

	int prio = Thread.NORM_PRIORITY;
	try {
	  prio = Integer.parseInt(ptf.getText());
	}
	catch(NumberFormatException ne) {
	  System.err.println("EXCEPTION: NewProducer_Dialog.action(): Integer.parseInt():: "+ne.getMessage());
	  ne.printStackTrace();
	  this.enable();
	  return true;
	}

	try {
	  appMan.registerProducer(session, prod, startBox.getState(), prio);
	}
	catch(AppManager_Exception ce) {
	  System.err.println("EXCEPTION: NewProducer_Dialog.action(): appMan.registerProducer()::"+ce.getMessage());
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
      if (e.target == prodChoice) {
	((CardLayout)prodOptions.getLayout()).show(prodOptions, (String)arg);
      }
      return true;
    }
  
    return false;
  }

  private  Hashtable prodOptionsTable;
  private AppManager appMan;
  private GuiManager guiMan;
  private GridBagLayout mainLO;
  private Session session;

  private Choice prodChoice;
  private Panel prodOptions;
  private Panel generalOptions;

  private Checkbox startBox;
  private TextField ptf;

  private Button okButton;
  private Button cancelButton;
  private Panel buttonPanel;


}

