package mmstream.consumer;


import java.awt.*;
import java.awt.image.*;

public class ImageCanvas extends Canvas {

public ImageCanvas(int w, int h) {

  super();

  dcm = new DirectColorModel(8, 0xc0, 0x38, 0x07);
  
  offDimension = this.size();
  if (offDimension.width != w || offDimension.height != h) {
    offDimension.width = w; offDimension.height = h;
    this.resize(offDimension);
  }

  frontGraphics = this.getGraphics();

  offImage = null;
  offGraphics = null;
}

public void 
paint(Graphics gr) {
  // Create the offscreen graphics context, if no good one exists.
  
   if (offImage == null) {
     offImage = createImage(offDimension.width, offDimension.height);
     offGraphics = offImage.getGraphics();
   }
    
  // Paint the image on the screen.
  gr.drawImage(offImage, 0, 0, this);

}
  
public void
newImage(MemoryImageSource src) {

   if (offImage == null) {
     offImage = this.createImage(offDimension.width, offDimension.height);
     offGraphics = offImage.getGraphics();
   }
    
  // Erase the previous image.
  offGraphics.setColor(getBackground());
  offGraphics.fillRect(0, 0, offDimension.width, offDimension.height);
  offGraphics.setColor(Color.red);

  Image image = this.createImage(src);

  //Paint the frame into the image.
  offGraphics.drawImage(image, 0, 0, this);
  
  // Paint the image onto the screen.
  if (frontGraphics == null)
    frontGraphics = this.getGraphics();
  frontGraphics.drawImage(offImage, 0, 0, this);
}

  DirectColorModel dcm;
  Dimension offDimension;
  Image offImage;
  Graphics offGraphics;
  Graphics frontGraphics;
  
}

