package mmstream.stream;

import mmstream.stream.*;

import java.awt.*;

public class Println_StreamOutput implements StreamOutput  {

  public void message(String text) { System.out.println("StreamOuput.message "+text); }

  public void error(String text) { System.err.println("StreamOuput.error "+text); }
  
  public void setDataOutput(Component p) { System.out.println("StreamOuput.setDataOuput: all data will be lost! "); };
}
