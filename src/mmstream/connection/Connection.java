package mmstream.connection;

import mmstream.*;
import mmstream.util.*;
import mmstream.address.*;
import mmstream.connection.*;

public interface Connection {
public abstract void close();

public abstract void send(byte[] m, int reserved, int length) throws Connection_Exception;  
public abstract void send(byte[] m, int length) throws Connection_Exception;  
public abstract void send(Chunk p) throws Connection_Exception;

public abstract int receive(byte[] m, int reserved, int length) throws Connection_Exception;
public abstract int receive(byte[] m, int length) throws Connection_Exception;
public abstract int receive(byte[] m) throws Connection_Exception;
public abstract Chunk receive(int length) throws Connection_Exception;

public abstract void reserveHeaderSize(int size) throws Connection_Exception;
public abstract int getHeaderSize();
public abstract int getMaxSize();
public abstract void setMaxSize(int ms) throws Connection_Exception;


public abstract void setRemoteAddress(Address addr) throws Connection_Exception;
public abstract Address getRemoteAddress();

public abstract Address getLocalAddress();

public abstract String getTypeName();

}
