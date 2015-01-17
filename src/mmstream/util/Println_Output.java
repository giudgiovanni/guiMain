package mmstream.util;

public class Println_Output implements Output {

public void 
message(String text) {
  System.out.println(text);
}

public void 
error(String text) {
  System.err.println(text);
}

}
