package mmstream.gui;

import mmstream.gui.*;
import mmstream.apps.*;
import mmstream.stream.*;
import mmstream.session.*;
import mmstream.consumer.*;
import mmstream.config.*;

import java.awt.*;
import java.io.*;


public class File_ConsumerOptions implements ConsumerOptions{

  public File_ConsumerOptions() {
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
    Options = new FileCOPanel(appMan, this);
    Options.setLayout(mainLO);

    GridBagConstraints g_c = new GridBagConstraints();
    g_c.anchor = GridBagConstraints.CENTER;
    g_c.fill = GridBagConstraints.NONE;
    g_c.gridwidth = 1;
    g_c.gridheight = 1;
    g_c.gridx = 0;
    g_c.gridy = 0;
    g_c.ipadx = 5;
    g_c.ipady = 10;


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

    fileDialog = null;
    panelbuilt = true;
    return Options;
  }

  public ConsumerControl 
  createConsumer(Session ses)     throws Gui_Exception {
    if (panelbuilt == false || fileDialog == null) 
      throw new Gui_Exception("File_ConsumerOptions.createConsumer():not initialised");

    String name = fn.getText();
    if (name == null)
      throw new Gui_Exception("File_ConsumerOptions.createProducer():no file selected");
    File file = new File(name);
    File_Consumer consumer = null;
    consumer = new File_Consumer(appMan, ses, String.valueOf(appMan.getUniqueId()), file);
    return consumer;
  }

  public ConsumerControl 
  createConsumer(Session ses, StreamExporter exp)     throws Gui_Exception {
    if (panelbuilt == false || fileDialog == null) 
      throw new Gui_Exception("File_ConsumerOptions.createConsumer():not initialised");
    String name = fn.getText();
    if (name == null)
      throw new Gui_Exception("File_ConsumerOptions.createProducer():no file selected");
    File file = new File(name);
    return new File_Consumer(appMan, ses, exp, String.valueOf(appMan.getUniqueId()), file);
  }

  protected void 
  setFileName(String name) {
    fn.setText(name);
  }
  protected String  
  getFileName() {
    return fn.getText();
  }


  private FileCOPanel Options;

  private boolean panelbuilt;

  private AppManager appMan;
  public static final String Name = new String(Consumer_TypeConfiguration.FILE_Name);

  private TextField fn;
  protected Button fileButton;
  protected FileDialog fileDialog;
}

class FileCOPanel extends Panel {

protected FileCOPanel(AppManager am, File_ConsumerOptions fco) {
  fileCO = fco;
  appMan = am;
}

public boolean 
action(Event e, Object arg) {
  if (e.target instanceof Button) {
    if (e.target == fileCO.fileButton) {
      if (fileCO.fileDialog == null) {
	fileCO.fileDialog = new FileDialog(appMan.getGuiManager().getRoot(), "Store File", FileDialog.SAVE);
      }
      String tmp = fileCO.getFileName();
      if (tmp != null) {
	fileCO.fileDialog.setDirectory(tmp);
	//	System.out.println("LOOK setted file name "+tmp);
      }
      fileCO.fileDialog.pack();
      fileCO.fileDialog.show();
      String filename = fileCO.fileDialog.getFile();
      String dir = fileCO.fileDialog.getDirectory();
      if (dir != null && filename != null)
	fileCO.setFileName(dir+filename);
      return true;
    }
  }
  return false;
}

  File_ConsumerOptions fileCO;
  private AppManager appMan;
}


