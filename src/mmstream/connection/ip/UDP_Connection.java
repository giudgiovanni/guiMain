package mmstream.connection.ip;

import mmstream.util.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;

import java.net.*;
import java.io.*;

public class UDP_Connection extends IP_Connection {

  private static String Name;

  static {
    Name = new String(Connection_TypeConfiguration.UDP_Name);
  }

  public 
  UDP_Connection() {
    remoteAddress = null;

    localAddress = null;

    ownHeaderSize = 0;
    upperHeaderReservation = 0;

    socket = null;
    maxSize = 60000;
  }  


  public 
  UDP_Connection(int local, String hostname, int remote, int max) throws UDP_Connection_Exception {
    this();

    maxSize = max;
    
    this.setLocalPort(local);

    try {
      remoteAddress = new IP_Address(hostname, remote);
    }
    catch(UnknownHostException e) {
      throw new UDP_Connection_Exception("UDP_Connection(int,int,String,int,int): new IP_Address(String,int):: ");
    }
  }


  public void
  close() {
    if (socket != null) {
      socket.close();
      socket = null;
    }
  }

 
  public int 
  getMaxSize() {
    return maxSize;
  }

  public  void 
  setMaxSize(int ms) throws Connection_Exception {
    if (ms < 0)
      throw new UDP_Connection_Exception("UDP_Connection.setMaxSize("+ms+"): value < 0");
    maxSize = ms;
  }

  public void 
  setLocalPort(int port) throws UDP_Connection_Exception {
    try {
      localAddress = new IP_Address(port);
    }
    catch (UnknownHostException e) {
      throw new UDP_Connection_Exception("UDP_Connection.setLocalPort: IP_Address()::"+e.getMessage());
    }
    try {
      socket = new DatagramSocket(port);
    }
    catch (SocketException e) {
      throw new UDP_Connection_Exception("UDP_Connection.setLocalPort(int): DatagramSocket()::"+e.getMessage());
    }
  }


  public void 
  send(byte[] m, int reserved, int length) throws UDP_Connection_Exception {
    if (reserved < 0 || length < 0) {
      throw new UDP_Connection_Exception("send(byte[],int,int): params < 0");
    }
    else if (reserved > 0) {
      if (m.length - reserved - length < 0) 
	throw new UDP_Connection_Exception("send(byte[],int,int): m.length < reserved+length");
      byte[] n = new byte[length];
      System.arraycopy(n, 0, m, reserved, length);
      m = n;
    }
  
    DatagramPacket p = new DatagramPacket(m, length, remoteAddress.host, remoteAddress.port);
    try {
      socket.send(p);
    }
    catch (IOException ioe) {
      throw new UDP_Connection_Exception("send(byte[],int,int): socket.send()::"+ioe.getMessage());
    }
  }

  public void 
  send(byte[] m, int length) throws UDP_Connection_Exception {
    if (length > m.length)
      throw new UDP_Connection_Exception("send(byte[],int): length > m.length");

    DatagramPacket p = new DatagramPacket(m, length, remoteAddress.host, remoteAddress.port);
    try {
      socket.send(p);
    }
    catch (IOException ioe) {
      throw new UDP_Connection_Exception("send(byte[],int): socket.send()::"+ioe.getMessage());
    }
  }


  public void 
  send(Chunk pack) throws UDP_Connection_Exception {
    byte[] n;
    int length = pack.header_length + pack.data_length;
    if (pack.header_offset > 0 || pack.data_offset != pack.header_length) {
      n = new byte[pack.data_length + length];
      System.arraycopy(n, 0, pack.buffer, pack.header_offset, pack.header_length);
      System.arraycopy(n, pack.header_length, pack.buffer, pack.data_offset, pack.data_length);
    }
    else {
      n = pack.buffer;
    }
    DatagramPacket p = new DatagramPacket(n, length, remoteAddress.host, remoteAddress.port);
    try {
      socket.send(p);
    }
    catch (IOException ioe) {
      throw new UDP_Connection_Exception("send(Chunk): socket.send()::"+ioe.getMessage());
    }
  }

  public int 
  receive(byte[] m, int length) throws UDP_Connection_Exception {
    if (length < 0 || length > m.length)
      throw new UDP_Connection_Exception("receive(byte[],int): wrong params");

    DatagramPacket p = new DatagramPacket(m, length);
    this.receive(p);
    return p.getLength();
  }



  public int 
  receive(byte[] m, int reserved, int length) throws UDP_Connection_Exception {
    if (length < 0 || length > m.length || reserved < 0 || reserved + length > m.length)
      throw new UDP_Connection_Exception("receive(byte[],int,int): wrong params");
    if (reserved == 0) 
      return this.receive(m, length);

    byte[] n = new byte[length];
    
    DatagramPacket p = new DatagramPacket(n, length);
    this.receive(p);
      
    System.arraycopy(m, reserved, n, 0, p.getLength());
    return p.getLength();
  }


  public int 
  receive(byte[] m) throws UDP_Connection_Exception {
    DatagramPacket p = new DatagramPacket(m, m.length);
    this.receive(p);
  
    return p.getLength();
  }


  public Chunk
  receive(int length) throws UDP_Connection_Exception {
    DatagramPacket p = new DatagramPacket(new byte[length], length);
    this.receive(p);
    Chunk pck;
    try {
      pck =  new Chunk(p.getData(), 0, p.getLength());
    }
    catch (Chunk_Exception e) {
      throw new UDP_Connection_Exception("UDP_Connnection.receive(int): new Chunk():: " + e.getMessage());
    }
    pck.arrival = System.currentTimeMillis();
    pck.sourceTransportAddress = new IP_Address(p.getAddress(), p.getPort());
  
    return pck;
  }

  protected void
  receive(DatagramPacket p) throws UDP_Connection_Exception {
    try {
      socket.receive(p);
    }
    catch (IOException ioe) {
      throw new UDP_Connection_Exception("receive(DatagramPacket): socket.receive()::"+ioe.getMessage());
    }
  
  }

  public boolean
  equals(Object obj) {
    if (obj == null)
      return false;

    if (obj.getClass() != this.getClass())
      return false;

    UDP_Connection uo = (UDP_Connection)obj;
    if (this.localAddress.equals(uo.localAddress) &&
	this.remoteAddress.equals(uo.remoteAddress))
      return true;
    else 
      return false;
  }

  public String getTypeName() {
    return Name;
  }


  protected DatagramSocket socket; 
  protected int maxSize;
}
