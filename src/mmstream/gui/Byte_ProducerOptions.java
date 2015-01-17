package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.config.*;

import java.awt.*;


public class Byte_ProducerOptions implements ProducerOptions{

  public Byte_ProducerOptions() {
    panelbuilt = false;
    Options = null;
    appMan = null;
  }

  public String getTypeName() {
    return Name;
  }

  public Panel 
  getPanel(AppManager am) {
    appMan = am;

    if (panelbuilt)
      return Options;

    GridBagLayout mainLO = new GridBagLayout();
    Options = new Panel();
    Options.setLayout(mainLO);

    GridBagConstraints g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 5;
    g_c.ipady = 5;


    Label l = new Label("Byte Options:", Label.CENTER);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(l, g_c);
    Options.add(l);

    l = new Label("Max Value:", Label.LEFT);
    g_c.anchor = GridBagConstraints.WEST;
    g_c.gridy++;
    g_c.gridwidth = 1;
    mainLO.setConstraints(l, g_c);
    Options.add(l);
    maxField = new TextField("127", 5);
    maxField.setEditable(true);
    g_c.gridx = 1;
    mainLO.setConstraints(maxField, g_c);
    Options.add(maxField);
  
    repeatBox = new Checkbox("Repeat Sending");
    repeatBox.setState(true);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(repeatBox, g_c);
    Options.add(repeatBox);

    panelbuilt = true;
    return Options;
  }

  public ProducerControl 
  createProducer(Session ses)     throws Gui_Exception {
    if (panelbuilt == false) 
      throw new Gui_Exception("Byte_ProducerOptions.createProducer():not initialised");

    int len;
    try {
      len = Integer.parseInt(maxField.getText());
    }
    catch(NumberFormatException ne) {
      throw new Gui_Exception("Byte_ProducerOptions.createProducer(): Integer.parseInt():: "+ne.getMessage());
    }

    Byte_Producer producer = null;
    try {
      producer = new Byte_Producer(ses, ses.getNewLocalSource(), (byte)len, repeatBox.getState(), String.valueOf(appMan.getUniqueId()));
    }
    catch (Session_Exception se) {
      throw new Gui_Exception("Byte_ProducerOptions.createProducer(Session): Byte_Producer():: "+se.getMessage());
    }
    return producer;
  }

  private Panel Options;

  private boolean panelbuilt;

  private AppManager appMan;
  private TextField maxField;
  private Checkbox repeatBox;
  public static final String Name = new String(Producer_TypeConfiguration.BYTE_Name);
}

