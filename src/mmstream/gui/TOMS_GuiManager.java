package mmstream.gui;

import mmstream.apps.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.consumer.*;
import mmstream.stream.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.gui.*;

import java.awt.*;
import java.util.*;

public class TOMS_GuiManager implements GuiManager {

  public TOMS_GuiManager(AppManager am) {
    appMan = am;
    root = new MainFrame(appMan);

    connectionOptionsGuiTable = new  Hashtable(5, (float)0.5);
    sessionMapperOptionsGuiTable = new  Hashtable(5, (float)0.5);
    payloadTypeOptionsGuiTable = new Hashtable(5, (float)0.5);
    protocolOptionsGuiTable = new Hashtable(5, (float)0.5);
    consumerOptionsGuiTable = new Hashtable(5, (float)0.5);
    producerOptionsGuiTable = new Hashtable(5, (float)0.5);
    recStatsGuiTable = new Hashtable(5, (float)0.5);
    recStatsDataGuiTable = new Hashtable(5, (float)0.5);
    recStatsLabelGuiTable = new Hashtable(5, (float)0.5);
    sendLocalStatsGuiTable = new Hashtable(5, (float)0.5);
    sendRemoteStatsGuiTable = new Hashtable(5, (float)0.5);
  }

  public synchronized Frame
  getRoot() {
    return root;
  }

  public synchronized Output
  getOutput() {
    return root;
  }

  public synchronized void 
  finish() {
    root.shutdown();
  }

  public synchronized void 
  registerSessionMapperOptionsGui(String name, Class opt) throws Gui_Exception {
    if (sessionMapperOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerSessionMapperOptions: SessionMapperOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.SessionMapperOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	sessionMapperOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerSessionMapperOptions: Class "+opt.getName()+" for "+name+" does not implement SessionMapperOptions");
  }

  public synchronized void 
  registerConnectionOptionsGui(String name, Class opt) throws Gui_Exception {
    if (connectionOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerCOnnectionOptions: ConnectionOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.ConnectionOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	connectionOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerCOnnectionOptions: Class "+opt.getName()+" for "+name+" does not implement ConnectionOptions");
  }

  public synchronized void 
  registerProtocolCoDecOptionsGui(String name, Class opt) throws Gui_Exception {
    if (protocolOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerProtocolCoDecOptions: ProtocolCoDecOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.ProtocolCoDecOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	protocolOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerProtocolCoDecOptions: Class "+opt.getName()+" for "+name+" does not implement ProtocolCoDecOptions");
  }

  public synchronized void 
  registerProducerOptionsGui(String name, Class opt) throws Gui_Exception {
    if (producerOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerProducerOptions: ProducerOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.ProducerOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	producerOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerProducerOptions: Class "+opt.getName()+" for "+name+" does not implement ProducerOptions");
  }

  public synchronized void 
  registerConsumerOptionsGui(String name, Class opt) throws Gui_Exception {
    if (consumerOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerConsumerOptions: ConsumerOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.ConsumerOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	consumerOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerConsumerOptions: Class "+opt.getName()+" for "+name+" does not implement ConsumerOptions");
  }


  public synchronized void 
  registerPayloadTypeOptionsGui(String name, Class opt) throws Gui_Exception {
    if (payloadTypeOptionsGuiTable.containsKey(name) == true)
      throw new Gui_Exception("TOMS_GuiManager.registerPayloadTypeOptions: PayloadTypeOptionsGui for "+name+" exists");
    Class[] ifs = opt.getInterfaces();
    String tmp = "mmstream.gui.PayloadTypeOptions";
    for (int i = 0; i < ifs.length; i++) {
      if (tmp.equals(ifs[i].getName())) {
	payloadTypeOptionsGuiTable.put(name, opt);
	return;
      }
    }
    throw new Gui_Exception("TOMS_GuiManager.registerPayloadTypeOptions: Class "+opt.getName()+" for "+name+" does not implement ConsumerOptions");
  }



  public synchronized Hashtable 
  getConnectionOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = connectionOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, connectionOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized Hashtable 
  getSessionMapperOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = sessionMapperOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, sessionMapperOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized Hashtable 
  getPayloadTypeOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = payloadTypeOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, payloadTypeOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized Hashtable 
  getProducerOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = producerOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, producerOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized Hashtable 
  getConsumerOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = consumerOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, consumerOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized Hashtable 
  getProtocolCoDecOptionsGuis() {
    Hashtable ret = new Hashtable(5, (float)0.5);
    Enumeration e = protocolOptionsGuiTable.keys();
    Object tmp;

    for (; e.hasMoreElements();) {
      tmp = e.nextElement();
      ret.put(tmp, protocolOptionsGuiTable.get(tmp));
    }

    return ret;
  }

  public synchronized void 
  registerReceptionStatisticsDisplayGui(String name, Class ssgc) {
    recStatsGuiTable.put(name, ssgc);
  }

  public synchronized void 
  registerReceptionStatisticsDataDisplayGui(String name, Class ssgc) {
    recStatsDataGuiTable.put(name, ssgc);
  }

  public synchronized void 
  registerReceptionStatisticsLabelDisplayGui(String name, Class ssgc) {
    recStatsLabelGuiTable.put(name, ssgc);
  }

  public synchronized void 
  registerRemoteSenderStatisticsDisplayGui(String name, Class ssgc) {
    sendRemoteStatsGuiTable.put(name, ssgc);
  }

  public synchronized void 
  registerLocalSenderStatisticsDisplayGui(String name, Class ssgc) {
    sendLocalStatsGuiTable.put(name, ssgc);
  }

		
  public synchronized ReceptionStatistics_Panel 
  createReceptionStatisticsGui(String s) {
    if (s == null)
      return null;

    Class sspc = (Class)(recStatsGuiTable.get(s));
    if (sspc != null) {
      try {
	return (ReceptionStatistics_Panel)(sspc.newInstance());
      }
      catch (InstantiationException ie) {
	return null;
      }
      catch (IllegalAccessException ae) {
	return null;
      }
    }
    else
      return null;
  }


  public synchronized ReceptionStatistics_LabelPanel 
  createReceptionStatisticsLabelGui(String s) {
    if (s == null)
      return null;

    Class sspc = (Class)(recStatsLabelGuiTable.get(s));
    if (sspc != null) {
      try {
	return (ReceptionStatistics_LabelPanel)(sspc.newInstance());
      }
      catch (InstantiationException ie) {
	return null;
      }
      catch (IllegalAccessException ae) {
	return null;
      }
    }
    else
      return null;
  }


  public synchronized ReceptionStatistics_DataPanel 
  createReceptionStatisticsDataGui(String s) {
    if (s == null)
      return null;

    Class sspc = (Class)(recStatsDataGuiTable.get(s));
    if (sspc != null) {
      try {
	return (ReceptionStatistics_DataPanel)(sspc.newInstance());
      }
      catch (InstantiationException ie) {
	return null;
      }
      catch (IllegalAccessException ae) {
	return null;
      }
    }
    else
      return null;
  }


  public synchronized SenderStatistics_Panel 
  createRemoteSenderStatisticsGui(String s) {
    if (s == null)
      return null;

    Class sspc = (Class)(sendRemoteStatsGuiTable.get(s));
    if (sspc != null) {
      try {
	return (SenderStatistics_Panel)(sspc.newInstance());
      }
      catch (InstantiationException ie) {
	return null;
      }
      catch (IllegalAccessException ae) {
	return null;
      }
    }
    else
      return null;
  }

  public synchronized SenderStatistics_Panel 
  createLocalSenderStatisticsGui(String s) {
    if (s == null)
      return null;

    Class sspc = (Class)(sendLocalStatsGuiTable.get(s));
    if (sspc != null) {
      try {
	return (SenderStatistics_Panel)(sspc.newInstance());
      }
      catch (InstantiationException ie) {
	return null;
      }
      catch (IllegalAccessException ae) {
	return null;
      }
    }
    else
      return null;
  }


  public synchronized void
  deleteSession(Session sen) {
    root.deleteSessionDialog(sen);
  }

  public synchronized void
  deleteProtoSession(String sen) {
    root.deleteProtoSessionDialog(sen);
  }

  public synchronized void
  updateProtoSession(String sen) {
    root.updateProtoSessionDialog(sen);
  }

  public synchronized void
  registerSession(Session sen) {
    String tmp = sen.getId();
    Session_Dialog sd = new Session_Dialog(this, appMan, sen);
    sd.hide();

    root.notifySessionDialog(sd);
  }

  public synchronized void
  registerProtoSession(ProtoSession sen) {
    String tmp = sen.getId();
    ProtoSession_Dialog sd = new ProtoSession_Dialog(this, appMan, sen);
    sd.hide();

    root.notifyProtoSessionDialog(sd);
  }

  public synchronized void 
  registerLocalSource(Session sen, LocalSource sd) {
    root.notifyLocalSource(sen, sd);
  }

  public synchronized void 
  registerRemoteSource(Session sen, RemoteSource rs) {
    root.notifyRemoteSource(sen, rs);
  }

  public synchronized void 
  registerProducer(Session sen, ProducerControl sd) {
    root.notifyProducer(sen, sd);
  }

  public synchronized void 
  registerConsumer(Session sen, ConsumerControl sd) {
    root.notifyConsumer(sen, sd);
  }

  private AppManager appMan;

  protected Hashtable connectionOptionsGuiTable;
  protected Hashtable sessionMapperOptionsGuiTable;
  protected Hashtable payloadTypeOptionsGuiTable;
  protected Hashtable protocolOptionsGuiTable;
  protected Hashtable producerOptionsGuiTable;
  protected Hashtable consumerOptionsGuiTable;
  protected Hashtable sendRemoteStatsGuiTable;
  protected Hashtable sendLocalStatsGuiTable;
  protected Hashtable recStatsGuiTable;
  protected Hashtable recStatsLabelGuiTable;
  protected Hashtable recStatsDataGuiTable;

  private MainFrame root; 

}
