package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.config.*;

import java.io.*;
import java.awt.*;


public class File_ProducerOptions implements ProducerOptions{

  public File_ProducerOptions() {
    panelbuilt = false;
    Options = null;
    appMan = null;
  }

  protected void 
  setFileName(String name) {
    fn.setText(name);
  }

  protected String  
  getFileName() {
    return fn.getText();
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
    Options = new FilePOPanel(appMan, this);
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


    Label l = new Label("File Options:", Label.CENTER);
    g_c.gridwidth = GridBagConstraints.REMAINDER;
    mainLO.setConstraints(l, g_c);
    Options.add(l);

    l = new Label("File Name:", Label.CENTER);
    g_c.gridwidth = 1;
    g_c.gridy++;
    mainLO.setConstraints(l, g_c);
    Options.add(l);
    fn = new TextField(System.getProperty("user.dir"), 15);
    fn.setEditable(false);
    g_c.gridwidth = 1;
    g_c.gridx++;
    mainLO.setConstraints(fn, g_c);
    Options.add(fn);
    fileButton = new Button("Select...");
    g_c.gridx++;
    mainLO.setConstraints(fileButton, g_c);
    Options.add(fileButton);

    headerBox = new Checkbox("Use Stored Time stamp");
    headerBox.setState(true);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(headerBox, g_c);
    Options.add(headerBox);

    repeatBox = new Checkbox("Repeat Sending");
    repeatBox.setState(true);
    g_c.gridx = 0;
    g_c.gridy++;
    mainLO.setConstraints(repeatBox, g_c);
    Options.add(repeatBox);

    fileDialog = null;
    panelbuilt = true;
    return Options;
  }

  public ProducerControl 
  createProducer(Session ses)     throws Gui_Exception {
    if (panelbuilt == false) 
      throw new Gui_Exception("File_ProducerOptions.createProducer():not initialised");

    String name = fn.getText();
    if (name == null)
      throw new Gui_Exception("File_ProducerOptions.createProducer():no file selected");
    File file = new File(name);
    File_Producer producer = null;
    try {
      producer = new File_Producer(ses, ses.getNewLocalSource(), String.valueOf(appMan.getUniqueId()), repeatBox.getState(), file, headerBox.getState());
    }
    catch (Session_Exception se) {
      throw new Gui_Exception("File_ProducerOptions.createProducer(Session): File_Producer():: "+se.getMessage());
    }
    return producer;
  }

  private Panel Options;

  private boolean panelbuilt;

  private AppManager appMan;
  private Checkbox repeatBox;
  private TextField fn;
  protected Button fileButton;
  private Checkbox headerBox;
  protected FileDialog fileDialog;

  public static final String Name = new String(Producer_TypeConfiguration.FILE_Name);
}


class FilePOPanel extends Panel {

protected FilePOPanel(AppManager am, File_ProducerOptions fpo) {
  filePO = fpo;
  appMan = am;
}

public boolean 
action(Event e, Object arg) {
  if (e.target instanceof Button) {
    if (e.target == filePO.fileButton) {
      if (filePO.fileDialog == null) {
	filePO.fileDialog = new FileDialog(appMan.getGuiManager().getRoot(), "Store File", FileDialog.LOAD);
      }
      String tmp = filePO.getFileName();
      if (tmp != null) {
	filePO.fileDialog.setDirectory(tmp);
	//	System.out.println("LOOK setted file name "+tmp);
      }
      filePO.fileDialog.pack();
      filePO.fileDialog.show();
      String filename = filePO.fileDialog.getFile();
      String dir = filePO.fileDialog.getDirectory();
      if (dir != null && filename != null)
	filePO.setFileName(dir+filename);
      return true;
    }
  }
  return false;
}

  File_ProducerOptions filePO;
  private AppManager appMan;
}

