package mmstream.producer;

import mmstream.producer.*;
import mmstream.util.*;
import mmstream.session.*;

public class Println_ProducerOutput extends Println_Output implements ProducerOutput {

public void notifyStateChange() {
  System.out.println("Producer state changed");
}

public void
notifyControlData(ControlData cd) { System.out.println("New producer ControlData arrived"); }

}
