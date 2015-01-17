package mmstream.stream;

import mmstream.util.*;
import mmstream.stream.*;

import java.util.*;

public interface StreamManager {

  public abstract void finish(String reason);

  public abstract void registerStream(Stream s) throws Stream_Exception;
  public abstract Hashtable getStreams();
  
  public abstract void registerStreamExporter(StreamExporter se) throws Stream_Exception;
  public abstract void deleteStreamExporter(StreamExporter se) throws Stream_Exception;
  public abstract Enumeration getStreamExporters();

  public Stream importStream(StreamExporter e, String streamclassname) throws Stream_Exception;
  public Stream importStream(StreamExporter e, Class streamclass) throws Stream_Exception;

  public abstract void setOutput(Output out);
  public abstract Output getOutput();
}








