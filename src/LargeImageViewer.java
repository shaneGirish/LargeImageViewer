import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
@SuppressWarnings("serial")
public class LargeImageViewer extends JFrame {
  protected JComponent component;
  protected int rows, cols, tileSize;
  protected BufferedImage tiles[][];
  
  public LargeImageViewer() {
    component = new InnerComponent();
    component.setSize(800, 600);

    this.setSize(800, 600);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setVisible(true);
  }

  public static void main(String[] args) {
    LargeImageViewer viewer = new LargeImageViewer();
    try {
      viewer.loadImage("Edinburgh.jpg");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadImage(String url) throws IOException {
    long t1, t2;
    tileSize = 100;
    File file = new File(url);

    System.out.println("Loading file: " + url + " (" + (file.exists()?"exists":"missing") + ")");
    t1 = System.currentTimeMillis();
    BufferedImage image = ImageIO.read(file);
    int old_w = image.getWidth();
    int old_h = image.getHeight();
    int w = old_w + tileSize - old_w%tileSize;
    int h = old_w + tileSize - old_w%tileSize;
    BufferedImage expandedImage = new BufferedImage(w, h, image.getType());
    Graphics2D graphics = (Graphics2D) expandedImage.getGraphics();
    graphics.drawImage(image, 0, 0, old_w, old_h, null);
    graphics.dispose();
    image.flush();
    image = null;
    t2 = System.currentTimeMillis();
    System.out.println("Image loaded in " + (t2 - t1) + " milliseconds.");
    t1 = System.currentTimeMillis();
    rows = h/tileSize;
    cols = w/tileSize;


    tiles = new BufferedImage[rows][cols];
    for (int x = 0; x < rows; x++) {
      for (int y = 0; y < cols; y++) {
        tiles[x][y] = expandedImage.getSubimage(tileSize * y, tileSize * x, tileSize, tileSize);
      }  
    }
    t2 = System.currentTimeMillis();
    System.out.println("Image split in " + (t2 - t1) + " milliseconds.");
    t1 = System.currentTimeMillis();
    //    for (int x = 0; x < rows; x++) {
    //      for (int y = 0; y < cols; y++) {
    //        ImageIO.write(tiles[x][y], "jpg", new File("/home/zacfer/Pictures/img " + x + "," + y + ".jpg"));
    //      }  
    //    }
    this.add(component);
    component.repaint();

    t2 = System.currentTimeMillis();
    System.out.println("Image saved in " + (t2 - t1) + " milliseconds.");
  }



private class InnerComponent extends JComponent {
	protected int offsetX = 0;
	protected int offestY = 0;
    @Override
    public void paintComponent(Graphics g) { 
      Graphics2D g2d = (Graphics2D) g;
      
      
      
      
      
      
      int centerRow = rows/2, 
          centerCol = cols/2, 
          tileCols = this.getWidth() / tileSize, 
          tileRows = this.getHeight() / tileSize,
          centerTileRow = tileRows / 2,
          centerTileCol = tileCols / 2,
          tileCenter = tileSize / 2;
      
      int tmpX, tmpY;
      System.out.println(centerRow + " " + centerCol + " " + tileCols + " " + tileRows);
      for (int x = 0; x < tileRows; x++) {
        for (int y = 0; y < tileCols; y++) {
          tmpY = centerCol - centerTileCol + y;
          tmpX = centerRow - centerTileRow + x;
          g2d.drawImage(tiles[tmpX][tmpY], tileSize * y, tileSize * x, null);
        }
      }
      super.paintComponent(g);
    }
  }

}