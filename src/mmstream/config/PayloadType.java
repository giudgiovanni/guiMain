package mmstream.config;

public class PayloadType extends Type implements Cloneable {
  
  public PayloadType(String nm, byte bc, byte[] p, int min, int max, int normal) {
    super(nm, bc, p);
    size = normal;
    minSize = min;
    maxSize = max;
  }

  public PayloadType(String nm, byte bc) {
    super(nm, bc);
    size = 0;
    minSize = 0;
    maxSize = 0;
  }

  @Override
  public Object 
  clone() {
    byte[] p = null;
    if (this.params != null) {
      p = new byte[this.params.length];
      System.arraycopy(this.params, 0, p, 0, this.params.length);
    }
    return new PayloadType(new String(this.name), this.byteCode, p, this.size, this.minSize, this.maxSize);
  }


  public void setSize(int s) {  size = s; }
  public int getSize() { return size; }

  public void setMinSize(int s) {  minSize = s; }
  public int getMinSize() { return minSize; }

  public void setMaxSize(int s) {  maxSize = s; }
  public int getMaxSize() { return maxSize; }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj.getClass() != this.getClass())
      return false;
    PayloadType pt = (PayloadType)obj;
    if (name.equals(pt.name) && byteCode == pt.byteCode) {
      return true;
//       if (this.params == null && pt.params == null)
// 	return true;
//       if (this.params != null && pt.params != null) {
// 	if (pt.params.length == this.params.length) {
// 	  for(int i = 0; i < this.params.length; i++)
// 	    if (pt.params[i] != this.params[i])
// 	      return false;
// 	  return true;
// 	}
//       }
    }
    return false;
  }

 protected int size;
 protected int minSize;
 protected int maxSize;
}
