package mmstream.stream;

import mmstream.*;
import mmstream.util.*;
import mmstream.stream.*;
import mmstream.producer.*;

public interface StreamExporter {
  public abstract void
  finish(String reason);

  public abstract void  
  registerStream(Stream si) throws Stream_Exception;

  public abstract void
  closeStream(Stream si);

  public abstract void
  exportChunk(Chunk p) throws Stream_Exception;

  public ProducerControl getProducer();

  public void setProducer(ProducerControl prod);

  public abstract boolean
  getValid();

  public abstract String
  getId(); 

  public abstract String
  getCname(); 

  public abstract Master_Clock 
  getMasterClock();

  public abstract Clock 
  createSlaveClock();

}
