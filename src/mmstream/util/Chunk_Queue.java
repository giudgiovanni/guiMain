package mmstream.util;

public class Chunk_Queue extends Queue {
  
public  
Chunk_Queue() {
  super();
}

public synchronized void 
insert(Chunk p) {
  insert_elem(p);
}

public synchronized Chunk
extract() {
  return (Chunk)(extract_elem());
}


}

