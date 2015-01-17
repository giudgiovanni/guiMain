package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;
import mmstream.protocols.*;
import mmstream.protocols.rtp.*;
import mmstream.stream.*;
import mmstream.config.*;
import mmstream.session.*;

import java.awt.*;
import java.util.*;


public class TOMS_SessionMapperOptions implements SessionMapperOptions {

public TOMS_SessionMapperOptions() {
  panelbuilt = false;
}

public String getName() {
  return Name;
}

public Panel getPanel() {

  if (panelbuilt)
    return Options;

  GridBagConstraints g_c = new GridBagConstraints();
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.fill = GridBagConstraints.NONE;
  g_c.gridwidth = 1;
  g_c.gridheight = 1;
  g_c.gridx = 0;
  g_c.gridy = 0;
  g_c.ipadx = 5;
  g_c.ipady = 5;

  Options = new Panel();
  GridBagLayout mainLO = new GridBagLayout();
  Options.setLayout(mainLO);

  Label l = new Label("TOMS Options:", Label.CENTER);
  g_c.anchor = GridBagConstraints.CENTER;
  g_c.gridwidth = GridBagConstraints.REMAINDER;
  mainLO.setConstraints(l, g_c);
  Options.add(l);

  panelbuilt = true;

  return Options;
}

public SessionMapper createSessionMapper(AppManager am) throws Gui_Exception {
  if (panelbuilt == false) 
    throw new Gui_Exception("TOMS_SessionMapperOptions.createSessionMapper():not initialised");

  TOMS_SessionMapper sm = null;
  try {
    sm = new TOMS_SessionMapper(am);
  }
  catch (Exception ge) {
    throw new Gui_Exception("TOMS_SessionMapperOptions.createSessionMapper(): TOMS_SessionMapper():: "+ge.getMessage());
  }


  return sm;
}

private Panel Options;

private boolean panelbuilt;

public static final String Name = new String(SessionMapper_TypeConfiguration.TOMS_MAPPER_Name);
}
