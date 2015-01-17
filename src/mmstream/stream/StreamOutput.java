package mmstream.stream;

import java.awt.*;

public interface StreamOutput  {

public abstract void message(String text);
public abstract void error(String text);

public abstract void setDataOutput(Component p);


}
