package mmstream.address;

import mmstream.config.*;
import mmstream.util.*;
import mmstream.apps.*;

import java.util.*;
import java.net.*;

public class IP_Address implements Address {

  private static String Name;
  static {
    Name = new String(Address_TypeConfiguration.IP_Name);
  }

  public IP_Address() {
     hostname = null;
     host = null;
     port = 0;
  }

  public IP_Address(String hostname, int port)  throws UnknownHostException {
    this.host = InetAddress.getByName(hostname);
    this.port = port;
    this.hostname = hostname;
  }
  
  public IP_Address(InetAddress h, int p)  {
    this.host = h;
    this.port = p;
    this.hostname = h.getHostName();
  }
  
  public IP_Address(int port)  throws UnknownHostException {
    this.host = InetAddress.getLocalHost();
    this.port = port;
    this.hostname = host.getHostName();
  }

  @Override
  public boolean
  equals(Object obj) {
    if (obj == null)
      return false;

    if (obj.getClass() != this.getClass())
      return false;

    if (this.host.equals(((IP_Address)obj).host))
      if (this.port == ((IP_Address)obj).port)
	return true;

    return false;
  }

  @Override
  public String
  toString() {
    return new String(host.getHostName() + ":" + Integer.toString(port));
  }

  @Override
  public Object 
  clone() {
    IP_Address ret = null;
    try {
      ret = new IP_Address(InetAddress.getByName(this.host.getHostName()), this.port);
    }
    catch(UnknownHostException ue) {
      ;
    }
    return ret;
  }  

  @Override
  public String
  getName() { return Name; }

  public String hostname;
  public InetAddress host;
  public int port;

  @Override
  public void 
  configure(String a) throws Address_Exception {
    StringTokenizer st = new StringTokenizer(a, ":", false);
    if (st.countTokens() != 2)
      throw new Address_Exception("IP_Address.configure(): can't configure to '"+a+")");
    hostname = st.nextToken();
    try {
      port = Integer.parseInt(st.nextToken());
    }
    catch(NumberFormatException ne) {
      throw new Address_Exception("IP_Address.configure(): Integer.parseInt("+a+") ::"+ne.getMessage());
    }
    try {
      host = InetAddress.getByName(hostname);
    }
    catch(UnknownHostException ue) {
      throw new Address_Exception("IP_Address.configure(): InetAddress.getByName("+hostname+") ::"+ue.getMessage());
    }
  }
}





