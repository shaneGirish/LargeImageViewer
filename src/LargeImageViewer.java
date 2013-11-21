import java.io.File;
import java.io.IOException;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class LargeImageViewer extends JFrame {
	protected JComponent component;

	public LargeImageViewer(String url) throws IOException {
		BufferedImage image = ImageIO.read(new File(url));

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
class InnerComponent extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
	protected BufferedImage[][] tiles, cache;

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
		addMouseWheelListener(this);
	}

	protected BufferedImage padImage(BufferedImage image) {
		int old_width = image.getWidth();
		int old_height = image.getHeight();

		width = old_width + tileSize - old_width % tileSize;
		height = old_height + tileSize - old_height % tileSize;

		anchor = new Point(width / 2, height / 2);

		BufferedImage paddedImage = new BufferedImage(width, height, image.getType());
		Graphics2D graphics = (Graphics2D) paddedImage.getGraphics();
		graphics.drawImage(image, 0, 0, old_width, old_height, null);
		graphics.dispose();

		image.flush();
		image = null;

		return paddedImage;
	}
	
	protected void cleanCache() {
		if(cache != null) {
			int i,j;
			for (i = 0 ; i < cols ; i++) {
				for (j = 0 ; j < rows ; j++) {
					BufferedImage image = cache[i][j];
					if(image != null) {
						image.flush();
					}
				}
			}
		}
		cache = new BufferedImage[cols][rows];
	}

	protected void calculateTiles(BufferedImage image) {
		rows = height / tileSize;
		cols = width / tileSize;
		tiles = new BufferedImage[cols][rows];
		cleanCache();

		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				tiles[x][y] = image.getSubimage(tileSize * x, tileSize * y, tileSize, tileSize);
				
			}
		}
	}

	protected void checkAnchor(int screen_width, int screen_height) {
		int max_anchor_x = (int) (width - screen_width / scale / 2);
		int max_anchor_y = (int) (height - screen_height / scale / 2);
		int min_anchor_x = (int) (screen_width / scale / 2);
		int min_anchor_y = (int) (screen_height / scale / 2);

		if (anchor.x > max_anchor_x) {
			anchor.x = max_anchor_x;
		} else if (anchor.x < min_anchor_x) {
			anchor.x = min_anchor_x;
		}

		if (anchor.y > max_anchor_y) {
			anchor.y = max_anchor_y;
		} else if (anchor.y < min_anchor_y) {
			anchor.y = min_anchor_y;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int screen_width = this.getWidth();
		int screen_height = this.getHeight();

		checkAnchor(screen_width, screen_height);

		int rows_on_screen = (int) Math.ceil(screen_width / (tileSize * scale)) + 1;
		int cols_on_screen = (int) Math.ceil(screen_height / (tileSize * scale)) + 1;

		int first_tile_index_x = (int) Math.floor(((anchor.x*scale) - screen_width / 2) / (tileSize*scale));
		int first_tile_index_y = (int) Math.floor(((anchor.y*scale) - screen_height / 2) / (tileSize*scale));

		int last_tile_index_x = first_tile_index_x + rows_on_screen;
		int last_tile_index_y = first_tile_index_y + cols_on_screen;

		int x, y, x_cood, y_cood;

		for (x = first_tile_index_x; x < last_tile_index_x; x++) {
			for (y = first_tile_index_y; y < last_tile_index_y; y++) {
				if (x >= cols || y >= rows || x < 0 || y < 0) {
					continue;
				}

				x_cood = (int) (x * tileSize * scale - anchor.x * scale + screen_width / 2);
				y_cood = (int) (y * tileSize * scale - anchor.y * scale + screen_height / 2);
				
				BufferedImage image = tiles[x][y];
				int scaledTileSize = (int) (tileSize * scale);

				if(scale != 1.0) {
					BufferedImage cachedImage = cache[x][y];
					if(cachedImage != null) {
						image = cachedImage;
					} else {
						BufferedImage tmp = new BufferedImage(scaledTileSize, scaledTileSize, image.getType());
				        Graphics2D tmpG = tmp.createGraphics();
				        tmpG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				        tmpG.drawImage(image, 0, 0, scaledTileSize, scaledTileSize, null);
				        tmpG.dispose();
				        
				        image = tmp;
				        cache[x][y] = tmp;
					}
				}
				
				g2d.drawImage(image, x_cood, y_cood, null);
			}
		}
		super.paintComponent(g);
	}

	@Override public void mouseMoved(MouseEvent event) {}
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
		anchor.x -= (dragEnd.x - dragStart.x) / scale;
		anchor.y -= (dragEnd.y - dragStart.y) / scale;
		dragStart = dragEnd;
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		scale *= Math.pow(2.0, -event.getWheelRotation());
		//scale -= event.getWheelRotation() * 0.075;
		cleanCache();
		repaint();
	}
}