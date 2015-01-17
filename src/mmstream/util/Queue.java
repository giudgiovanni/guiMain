package mmstream.util;

public abstract class Queue extends Object {
  
  public  
  Queue() {
    last = new Queue_Elem();
    first = new Queue_Elem();
    first.next = last;
    last.next = first;
    length = 0;
  }

  // insert(SomeType) will do the work in the subclasses by calling this
  protected synchronized void
  insert_elem(Object obj) {
    Queue_Elem el = new Queue_Elem(obj);
    (last.next).next = el;
    el.next = last;
    last.next = el;
    length++;
    this.notify();
  }

  // 'SomeType extract()' will do the work in the subclasses by calling this
  protected synchronized Object
  extract_elem() {
    Queue_Elem ret;
    
    while (first.next == last) {
      try {
	this.wait();
      }
      catch(InterruptedException ie) {
	;
      }
    }
  
    ret = first.next;
    first.next = first.next.next;
    if (ret.next == last)
      last.next = first;
    length--;

    return ret.data;
  }

  public synchronized Object
  peak() {
    if (length > 0)
      return first.next.data;
    else
      return null;
  }

  public synchronized void
  clear() {
    Queue_Elem e = first.next;
    Queue_Elem t;
    // deletes references to queue elements, for gc
    while(e.next != last) {
      t = e.next;
      e.data = null;
      e.next = null;
      e = t;
    }
    first.next = last;
    last.next = first;
    length = 0;
  }

  public synchronized int
  available() {
    return length;
  }

  private int length;
  private Queue_Elem first;
  private Queue_Elem last;
}

class Queue_Elem extends Object {
  

  Queue_Elem() {
    ;
  }


  Queue_Elem(Object obj) {
    data = obj;
  }

  protected Object data;
  protected Queue_Elem next;
}
