package mmstream.config;

public class Type extends Object implements Cloneable {
  
  public Type(String nm, byte bc, byte[] p) {
    name = nm;
    byteCode = bc;
    params = p;
  }

  public Type(String nm, byte bc) {
    name = nm;
    byteCode = bc;
    params = null;
  }

  @Override
  public Object 
  clone() {
    byte[] p = null;
    if (this.params != null) {
      p = new byte[this.params.length];
      System.arraycopy(this.params, 0, p, 0, this.params.length);
    }
    return new Type(new String(this.name), this.byteCode, p);
  }

  public byte[] getParams() { return params; }
  public void setParams(byte[] pa) { params = pa; }

  public String getName() { return name; }
  public byte getByteCode() { return byteCode; }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
//     if (obj.getClass() != this.getClass())
//       return false;
    Type pt = null;
    try {
      pt = (Type)obj;
    }
    catch (ClassCastException ce) {
      return false;
    }
    if (name.equals(pt.getName()) &&
	byteCode == pt.getByteCode())
      return true;
    else
      return false;
  }

  protected byte[] params;
  protected String name;
  protected byte byteCode;
}
