package mmstream.gui;

import mmstream.apps.*;
import mmstream.session.*;
import mmstream.producer.*;
import mmstream.consumer.*;
import mmstream.stream.*;
import mmstream.gui.*;
import mmstream.source.*;
import mmstream.util.*;

import java.awt.*;
import java.util.*;

public interface GuiManager {

// serves as the central application Output
public Output getOutput();

// shutdown the GUI
public void finish();

// methods for notifying events to the user
public void registerSession(Session sen);
public void deleteSession(Session sen);

public void registerProtoSession(ProtoSession sen);
public void updateProtoSession(String sen);
public void deleteProtoSession(String sen);

public void registerLocalSource(Session sen, LocalSource os);
public void registerRemoteSource(Session sen, RemoteSource rs);

public void registerProducer(Session sen, ProducerControl s);
public void registerConsumer(Session sen, ConsumerControl s);


// Dialogs need a parent
public Frame getRoot();

// methods for registering and querying GUI components for 
// configuring and displaying architecture components
public void registerSessionMapperOptionsGui(String name, Class conclass) throws Gui_Exception;
public Hashtable getSessionMapperOptionsGuis();

public void registerConnectionOptionsGui(String name, Class conclass) throws Gui_Exception;
public Hashtable getConnectionOptionsGuis();

public void registerProtocolCoDecOptionsGui(String name, Class protclass) throws Gui_Exception;
public Hashtable getProtocolCoDecOptionsGuis();


public void registerPayloadTypeOptionsGui(String name, Class payclass) throws Gui_Exception;
public Hashtable getPayloadTypeOptionsGuis();

public void registerProducerOptionsGui(String name, Class prodclass) throws Gui_Exception;
public Hashtable getProducerOptionsGuis();

public void registerConsumerOptionsGui(String name, Class consclass) throws Gui_Exception;
public Hashtable getConsumerOptionsGuis();

public void registerReceptionStatisticsDisplayGui(String name, Class ssgc);
public ReceptionStatistics_Panel createReceptionStatisticsGui(String s);

public void registerReceptionStatisticsLabelDisplayGui(String name, Class ssgc);
public ReceptionStatistics_LabelPanel createReceptionStatisticsLabelGui(String s);

public void registerReceptionStatisticsDataDisplayGui(String name, Class ssgc);
public ReceptionStatistics_DataPanel createReceptionStatisticsDataGui(String s);

public void registerRemoteSenderStatisticsDisplayGui(String name, Class ssgc);
public SenderStatistics_Panel createRemoteSenderStatisticsGui(String s);

public void registerLocalSenderStatisticsDisplayGui(String name, Class ssgc);
public SenderStatistics_Panel createLocalSenderStatisticsGui(String s);
}

