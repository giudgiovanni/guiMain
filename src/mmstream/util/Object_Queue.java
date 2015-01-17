package mmstream.util;

public class Object_Queue extends Queue {
  
public  
Object_Queue() {
  super();
}

public synchronized void 
insert(Object p) {
  insert_elem(p);
}

public synchronized Object
extract() {
  return extract_elem();
}


}

