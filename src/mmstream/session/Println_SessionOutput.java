package mmstream.session;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.session.*;

public class Println_SessionOutput extends Println_Output implements SessionOutput {

public void 
notifyLocalSource(LocalSource s) {
  System.out.println("Local Source "+s.getId());
}
public void 
notifyRemoteSource(RemoteSource s) {
  System.out.println("Remote Source "+s.getId());
}
public void notifyStateChange() {
  System.out.println("Session state changed");
}

public void
notifyControlData(ControlData cd) { System.out.println("New session ControlData arrived"); }
}
