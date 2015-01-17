package mmstream.connection.ip;

import mmstream.util.*;
import mmstream.config.*;
import mmstream.address.*;
import mmstream.connection.*;
import mmstream.connection.ip.*;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.net.*;

public class UDP_Multicast_Connection extends UDP_Connection {

  private static String Name;

  static {
    Name = new String(Connection_TypeConfiguration.UDP_MULTICAST_Name);
  }


  public 
  UDP_Multicast_Connection() {
    super();

    ttl = 1;
    loopBack = false;

  }  

  public 
  UDP_Multicast_Connection(byte t, boolean lp)  {
    this();
    ttl = t;
    loopBack = lp;
  }


  public 
  UDP_Multicast_Connection(int prt, String hostname, byte t, boolean lb, int max) throws UDP_Multicast_Connection_Exception {
    this(t, lb);

    maxSize = max;
    
    this.setLocalPort(prt);

    try {
      this.setRemoteAddress(new IP_Address(hostname, prt));
    }
    catch(UnknownHostException e) {
      throw new UDP_Multicast_Connection_Exception("UDP_Multicast_Connection("+prt+","+hostname+","+","+ttl+","+lb+"): new IP_Address(String,int):: ");
    }
  }


  public void
  close() {
    if (socket != null) {
      try {
	((java.net.MulticastSocket)socket).leaveGroup(remoteAddress.host);
      }
      catch (SocketException e) {
	System.err.println("EXCEPTION: UDP_Multicast_Connection.close(): socket.leaveGroup():: "+ e.getMessage());
      } catch (IOException ex) {
            Logger.getLogger(UDP_Multicast_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
      socket.close();
      socket = null;
    }
  }

  public void 
  setRemoteAddress(Address addr) throws UDP_Multicast_Connection_Exception {
    remoteAddress = (IP_Address)addr;
    try {
      ((java.net.MulticastSocket)socket).joinGroup(remoteAddress.host);
    }
    catch (SocketException e) {
      throw new UDP_Multicast_Connection_Exception("UDP_Multicast_Exception.setRemoteAddress(): socket.joinGroup():: "+e.getMessage());
    } catch (IOException ex) {
          Logger.getLogger(UDP_Multicast_Connection.class.getName()).log(Level.SEVERE, null, ex);
      }

  }


  public void 
  setLocalPort(int port) throws UDP_Multicast_Connection_Exception {
    try {
      localAddress = new IP_Address(port);
    }
    catch (UnknownHostException e) {
      throw new UDP_Multicast_Connection_Exception("UDP_Multicast_Connection.setLocalPort: IP_Address()::"+e.getMessage());
    }
    try {
      socket = new java.net.MulticastSocket(port);
      //    socket.setLoopBack(false);
    }
    catch (SocketException e) {
      throw new UDP_Multicast_Connection_Exception("UDP_Multicast_Connection.setLocalPort(int): MulticastSocket()::"+e.getMessage());
    } catch (IOException ex) {
          Logger.getLogger(UDP_Multicast_Connection.class.getName()).log(Level.SEVERE, null, ex);
      }
  }


  protected void
  receive(DatagramPacket p) throws UDP_Multicast_Connection_Exception {
    do {
      try {
	socket.receive(p);
      }
      catch (IOException ioe) {
	throw new UDP_Multicast_Connection_Exception("receive(DatagramPacket): socket.receive()::"+ioe.getMessage());
      }
    }

    // if loopBack is unwanted, make sure that no packet is returned that was sent through this socket 
    while((loopBack == false) && 
	  (localAddress.host.equals(p.getAddress())) && 
	  (p.getPort() == localAddress.port));
  }



  // public void 
  // send(byte[] m, int reserved, int length) throws UDP_Multicast_Connection_Exception {
  //   if (reserved < 0 || length < 0) {
  //     throw new UDP_Multicast_Connection_Exception("send(byte[],int,int): params < 0");
  //   }
  //   else if (reserved > 0) {
  //     if (m.length - reserved - length < 0) 
  //       throw new UDP_Multicast_Connection_Exception("send(byte[],int,int): m.length < reserved+length");
  //     byte[] n = new byte[length];
  //     System.arraycopy(n, 0, m, reserved, length);
  //     m = n;
  //   }
  
  //   DatagramPacket p = new DatagramPacket(m, length, remoteAddress.host, remoteAddress.port);
  //   try {
  //     ((sun.net.MulticastSocket)socket).send(p, ttl);
  //   }
  //   catch (IOException ioe) {
  //     throw new UDP_Multicast_Connection_Exception("send(byte[],int,int): socket.send()::"+ioe.getMessage());
  //   }
  // }

  // public void 
  // send(Chunk pack) throws UDP_Multicast_Connection_Exception {
  //   byte[] n;
  //   int length = pack.header_length + pack.data_length;
  //   if (pack.header_offset > 0 || pack.data_offset != pack.header_length) {
  //     n = new byte[pack.data_length + length];
  //     System.arraycopy(n, 0, pack.buffer, pack.header_offset, pack.header_length);
  //     System.arraycopy(n, pack.header_length, pack.buffer, pack.data_offset, pack.data_length);
  //   }
  //   else {
  //     n = pack.buffer;
  //   }
  //   DatagramPacket p = new DatagramPacket(n, length, remoteAddress.host, remoteAddress.port);
  //   try {
  //     ((sun.net.MulticastSocket)socket).send(p, ttl);
  //   }
  //   catch (IOException ioe) {
  //     throw new UDP_Multicast_Connection_Exception("send(Chunk): socket.send()::"+ioe.getMessage());
  //   }
  // }

  // public void 
  // send(byte[] m, int length) throws UDP_Multicast_Connection_Exception {
  //   if (length > m.length)
  //     throw new UDP_Multicast_Connection_Exception("send(byte[],int): length > m.length");

  //   DatagramPacket p = new DatagramPacket(m, length, remoteAddress.host, remoteAddress.port);
  //   try {
  //     ((sun.net.MulticastSocket)socket).send(p, ttl);
  //   }
  //   catch (IOException ioe) {
  //     throw new UDP_Multicast_Connection_Exception("send(byte[],int): socket.send()::"+ioe.getMessage());
  //   }
  // }




  public boolean
  equals(Object obj) {
    if (obj == null)
      return false;

    if (obj.getClass() != this.getClass())
      return false;

    UDP_Multicast_Connection uo = (UDP_Multicast_Connection)obj;
    if (this.localAddress.equals(uo.localAddress) &&
	this.remoteAddress.equals(uo.remoteAddress))
      return true;
    else 
      return false;
  }


  public String getTypeName() {
    return Name;
  }


  private boolean loopBack;
  private byte ttl;
}

