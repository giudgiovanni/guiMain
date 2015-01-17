package mmstream.stream;

import mmstream.*;
import mmstream.util.*;
import mmstream.stream.*;

public interface Stream {

public abstract boolean getCopy();
public abstract void setCopy(boolean s);


//public abstract void setImporter(StreamImporter importer);

public void importChunk(Chunk p);

//these methods return unprocessed chunks;
// the untimed version is a performance short hand for unsynchronized playout
public abstract Chunk getChunk() throws Stream_Exception;
public abstract TimedObject getTimedChunk() throws Stream_Exception;

// these methods return are intended for returning preprocessed data;
// because there are no Templates in Java (yet), the data returned formally as an Object should be
// of the Class that the results of the preprocessing belong to;
// the untimed version is a performance short hand for unsynchronized playout
public Object getData() throws Stream_Exception;
public abstract TimedObject getTimedData() throws Stream_Exception;

public abstract int available();

public void setStreamExporter(StreamExporter exporter) throws Stream_Exception;
public abstract StreamExporter getStreamExporter();

public abstract void close();

public abstract String getId();
public abstract void setId(String id);

public abstract Clock getClock();


public abstract StreamOutput getOutput();
public abstract void setOutput(StreamOutput output);

public abstract Stream getMasterStream();
public abstract void setMasterStream(Stream master);

public boolean isStarted();
public abstract void start();
public abstract void stop();

public void alert();
public abstract void flush();

}
