package mmstream.consumer;

import mmstream.consumer.*;
import mmstream.util.*;

import java.awt.*;

public class Println_ConsumerOutput extends Println_Output implements ConsumerOutput {

  public void notifyStateChange() {
    System.out.println("Consumer state changed");
  }
  public void setDataOutput(Component p) { System.out.println("Println_ConsumerOuput.setDataOuput: all data will be lost! "); };

}
