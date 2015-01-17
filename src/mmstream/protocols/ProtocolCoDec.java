package mmstream.protocols;

import mmstream.source.*;
import mmstream.util.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.connection.*;


public interface ProtocolCoDec {

public abstract void startReceiving() throws CoDec_Exception;
public abstract void startSending() throws CoDec_Exception;
public abstract void finish(String reason) throws CoDec_Exception;

// public abstract void put(LocalSource ssrc, byte[] m, int reserved, int length) throws CoDec_Exception;

public abstract void transmitChunk(LocalSource ssrc, Chunk p) throws CoDec_Exception;

//public abstract void registerRemoteSource(RemoteSource src) throws CoDec_Exception;
public abstract RemoteSource getRemoteSource();

public abstract void setDataConnection(Connection con);
public abstract Connection getDataConnection();

public abstract void setControlConnection(Connection con);
public abstract Connection getControlConnection();

public abstract LocalSource createLocalSource() throws CoDec_Exception;
public abstract int getHeaderSizeRequest() throws CoDec_Exception;

public abstract Profile getProfile();

public abstract String getProtocolName();
}



