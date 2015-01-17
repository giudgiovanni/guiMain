package mmstream.connection.ip;

import mmstream.*;
import mmstream.util.*;
import mmstream.address.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;

import java.net.*;

public abstract class IP_Connection implements Connection {

@Override
public abstract void 
send(byte[] m, int reserved, int length) throws IP_Connection_Exception;  
@Override
public abstract void 
send(byte[] m, int length) throws IP_Connection_Exception;  
@Override
public abstract void 
send(Chunk chunk) throws IP_Connection_Exception;  

@Override
public abstract int 
receive(byte[] m, int reserved, int length) throws IP_Connection_Exception;
@Override
public abstract int 
receive(byte[] m, int length) throws IP_Connection_Exception;
@Override
public abstract int 
receive(byte[] m) throws IP_Connection_Exception;
@Override
public abstract Chunk 
receive(int length) throws IP_Connection_Exception;

@Override
public void 
reserveHeaderSize(int size) throws IP_Connection_Exception {
  if (size < 0) 
    throw new IP_Connection_Exception("setHeaderSize(size): size < 0");

  upperHeaderReservation = size;
}

@Override
public int 
getHeaderSize() {
  return ownHeaderSize;
}

@Override
public abstract int getMaxSize();
@Override
public abstract void setMaxSize(int ms) throws Connection_Exception;

@Override
public void 
setRemoteAddress(Address addr) throws IP_Connection_Exception {
  remoteAddress = (IP_Address)addr;
}

@Override
public Address 
getRemoteAddress() {
  return remoteAddress;
}

@Override
public Address 
getLocalAddress() {
  return localAddress;
}

@Override
public abstract String getTypeName();



protected int ownHeaderSize;
protected int upperHeaderReservation;

protected IP_Address localAddress;

protected IP_Address remoteAddress;

}
