package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.consumer.*;
import mmstream.config.*;

import java.awt.*;


public class Image_ConsumerOptions implements ConsumerOptions{

  public Image_ConsumerOptions() {
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


    Label l = new Label("Image Options:", Label.CENTER);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(l, g_c);
    Options.add(l);

    panelbuilt = true;
    return Options;
  }

  public ConsumerControl 
  createConsumer(Session ses)     throws Gui_Exception {
    if (panelbuilt == false) 
      throw new Gui_Exception("Image_ConsumerOptions.createConsumer():not initialised");

    Image_Consumer consumer = null;
    consumer = new Image_Consumer(appMan, ses, String.valueOf(appMan.getUniqueId()));
    return consumer;
  }

  public ConsumerControl 
  createConsumer(Session ses, StreamExporter exp)     throws Gui_Exception {
    if (panelbuilt == false) 
      throw new Gui_Exception("Image_ConsumerOptions.createConsumer():not initialised");

    return new Image_Consumer(appMan, ses, exp, String.valueOf(appMan.getUniqueId()));
  }

  private Panel Options;
  private boolean panelbuilt;

  private AppManager appMan;
  public static final String Name = new String(Consumer_TypeConfiguration.IMAGE_Name);
}

