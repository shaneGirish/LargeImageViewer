import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class LargeImageViewer extends JFrame {
	protected JComponent component;

	public LargeImageViewer(String url) throws IOException {
		long t1, t2;

		t1 = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File(url));
		t2 = System.currentTimeMillis();
		System.out.println("Image loaded in " + (t2 - t1) + " milliseconds.");

		component = new InnerComponent(image);
		this.add(component);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			new LargeImageViewer("Edinburgh.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

@SuppressWarnings("serial")
class InnerComponent extends JComponent implements MouseListener, MouseMotionListener {
	protected BufferedImage tiles[][];

	protected final int tileSize;

	protected int width, height, rows, cols;

	protected Point anchor, dragStart;

	protected double scale = 1;

	public InnerComponent(BufferedImage image) {
		this(image, 100);
	}

	public InnerComponent(BufferedImage image, int tileSize) {
		if (tileSize < 50) {
			throw new IllegalArgumentException("Specified tile size is not valid.");
		} else {
			this.tileSize = tileSize;
		}
		calculateTiles(padImage(image));
		addMouseListener(this);
		addMouseMotionListener(this);

	}

	protected BufferedImage padImage(BufferedImage image) {
		int old_width = image.getWidth();
		int old_height = image.getHeight();

		width = old_width + tileSize - old_width % tileSize;
		height = old_height + tileSize - old_height % tileSize;
		
		anchor = new Point(width/2, height/2);

		BufferedImage paddedImage = new BufferedImage(width, height, image.getType());
		Graphics2D graphics = (Graphics2D) paddedImage.getGraphics();
		graphics.drawImage(image, 0, 0, old_width, old_height, null);
		graphics.dispose();

		image.flush();
		image = null;

		return paddedImage;
	}

	protected void calculateTiles(BufferedImage image) {
		rows = height / tileSize;
		cols = width / tileSize;
		tiles = new BufferedImage[cols][rows];

		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				tiles[x][y] = image.getSubimage(tileSize * x, tileSize * y, tileSize, tileSize);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		int screen_width = this.getWidth();
		int screen_height = this.getHeight();
				
		int rows_on_screen = (int) Math.ceil(screen_width / (tileSize * scale)) + 1;
		int cols_on_screen = (int) Math.ceil(screen_height / (tileSize * scale)) + 1;
		
		int first_tile_index_x = (int) Math.floor((anchor.x - (screen_width * scale)/2)/tileSize);
		int first_tile_index_y = (int) Math.floor((anchor.y - (screen_height * scale)/2)/tileSize);
		
		int last_tile_index_x = first_tile_index_x + cols_on_screen;
		int last_tile_index_y = first_tile_index_y + rows_on_screen;
		
		int max_anchor_x = width - screen_width/2;
		int max_anchor_y = height - screen_height/2;
		int min_anchor_x = width - screen_width/2;
		int min_anchor_y = height - screen_height/2;
		
		if(anchor.x > max_anchor_x) {
			first_tile_index_x -= last_tile_index_x - rows + 1;
			last_tile_index_x = rows - 1;
			anchor.x = max_anchor_x;
		}
		
		if(anchor.y > max_anchor_y) {
			first_tile_index_y -= last_tile_index_y - cols + 1;
			last_tile_index_y = cols - 1;
			anchor.y = max_anchor_y;
		}
		
		if(first_tile_index_x < 0) {
			last_tile_index_x -= first_tile_index_x;
			first_tile_index_x = 0;
			anchor.x = screen_width/2;
		}
		
		if(first_tile_index_y < 0) {
			last_tile_index_y -= first_tile_index_y;
			first_tile_index_y = 0;
			anchor.y = screen_height/2;
		}
		
		int x, y, x_cood, y_cood, tile_pos_x, tile_pos_y;
		
		for (x = 0 ; x < rows_on_screen; x++) {
			for (y = 0; y < cols_on_screen; y++) {
				tile_pos_x = first_tile_index_x + x;
				tile_pos_y = first_tile_index_y + y;
				
				if(tile_pos_x >= cols || tile_pos_y >= rows) {
					break;
				}
				
				x_cood = tile_pos_x * tileSize - anchor.x + screen_width/2;
				y_cood = tile_pos_y * tileSize - anchor.y + screen_height/2;
				
				g2d.drawImage(tiles[tile_pos_x][tile_pos_y], x_cood, y_cood, null);
			}
		}
		super.paintComponent(g);
	}

    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent event) {}
	@Override public void mouseClicked(MouseEvent event) {}
    @Override public void mouseEntered(MouseEvent event) {}
    @Override public void mouseReleased(MouseEvent event) {}
    
    @Override
    public void mousePressed(MouseEvent event) {
    	dragStart = event.getPoint();
    }   

    @Override
    public void mouseDragged(MouseEvent event) {
    	Point dragEnd = event.getPoint();
    	anchor.x -= dragEnd.x - dragStart.x;
    	anchor.y -= dragEnd.y - dragStart.y;
    	dragStart = dragEnd;
    	repaint();
    }
}