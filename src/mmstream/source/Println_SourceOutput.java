package mmstream.source;

import mmstream.util.*;
import mmstream.source.*;
import mmstream.gui.*;
import mmstream.address.*;

import java.awt.*;

public class Println_SourceOutput extends Println_Output implements SourceOutput {

public void 
message(String text) { System.out.println("SourceOutput.message: "+text); }
public void 
error(String text) { System.err.println("SourceOutput.error: "+text); }

public void 
notifyAddress(Address data, Address control) { System.out.println("New Address arrived"); }


public void 
notifySourceDescription() { System.out.println("New Source info arrived"); }

public void 
notifyStateChange() { System.out.println("Source state changed"); }


public void 
notifyReceptionStatistics(ReceptionStatistics src) { System.out.println("New remote statistics arrived for source"+ src.id); }

public void 
notifySenderStatistics(SenderStatistics src) { System.out.println("New local statistics arrived"); }
  
}
