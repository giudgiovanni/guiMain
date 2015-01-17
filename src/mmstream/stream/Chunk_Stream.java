package mmstream.stream;

import mmstream.stream.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;

public class Chunk_Stream extends StreamBase implements Stream {

  public 
  Chunk_Stream() {
    super();
  }

  public 
  Chunk_Stream(StreamExporter str) throws Stream_Exception {
    super(str);
  }

  // for a 'Chunk' Stream, there is no preprocessing necessary
  public Object
  getData() throws Stream_Exception {
    return this.getChunk();
  }


  // for a 'Chunk' Stream, there is no preprocessing necessary
  public TimedObject
  getTimedData() throws Stream_Exception {
    return this.getTimedChunk();
  }
}
