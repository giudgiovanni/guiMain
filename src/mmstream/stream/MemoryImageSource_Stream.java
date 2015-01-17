package mmstream.stream;

import mmstream.stream.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.connection.*;

import java.awt.*;
import java.awt.image.*;

public class MemoryImageSource_Stream extends StreamBase implements Stream {

  public 
  MemoryImageSource_Stream() {
    super();

    cm = null;
    dims = null;
  }

  public 
  MemoryImageSource_Stream(StreamExporter str) throws Stream_Exception {
    super(str);
    cm = null;
    dims = null;
  }

  // this intended to do the real work
  public Object
  getData() throws Stream_Exception {
    Chunk p = this.getChunk();
    System.out.println("MemImSo_St: "+p.buffer[p.data_offset]);
    if (depth == 8)
      return new MemoryImageSource(dims.width, dims.height, cm, p.buffer, p.data_offset, dims.width);
    int size = (p.data_length * 8) / depth;
    int[] buf = new int[size];
    int j = 0;
    for (int i = 0; i < size; i++) {
      buf[i] = (((int)(p.buffer[p.data_offset + j++])) & 0xff) << 16;
      buf[i] |= (((int)(p.buffer[p.data_offset + j++])) & 0xff) << 8;
      buf[i] |= ((int)(p.buffer[p.data_offset + j++])) & 0xff;
    }
    return new MemoryImageSource(dims.width, dims.height, cm, buf, 0, dims.width);
  }

  // this intended to do the real work
  public TimedObject
  getTimedData() throws Stream_Exception {
    TimedObject r = this.getTimedChunk();
    if (depth == 8)
      r.object = new MemoryImageSource(dims.width, dims.height, cm, ((Chunk)r.object).buffer, ((Chunk)r.object).data_offset, dims.width);
    else {
      Chunk p = (Chunk)(r.object);
      int size = (p.data_length * 8) / depth;
      int[] buf = new int[size];
      int j = 0;
      for (int i = 0; i < size; i++) {
	buf[i] = ((int)(p.buffer[p.data_offset + j++])) << 16;
	buf[i] |= ((int)(p.buffer[p.data_offset + j++])) << 8;
	buf[i] |= ((int)(p.buffer[p.data_offset + j++]));
      }
      r.object = new MemoryImageSource(dims.width, dims.height, cm, buf, 0, dims.width);
    }
    return r;
  }

  public void 
  setDimension(Dimension d) { 
    this.dims = d;
  }

  public void
  setColorModel(ColorModel m) {
    this.cm = m;
    this.depth = cm.getPixelSize();
    if (this.depth != 24)
      this.depth = 8;
  }

  private int depth;
  private ColorModel cm;
  private Dimension dims;
}
