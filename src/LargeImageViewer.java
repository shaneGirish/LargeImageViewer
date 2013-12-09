import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
class LargeImageViewer extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, TileResizeListener {
	public static void main(String[] args) {
		try {
			JFrame window = new JFrame();
			window.add(new LargeImageViewer("wallpaper.jpg"));
			window.setSize(800, 600);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
			window.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected LargeImage image;
	
	protected Point dragStart;
	protected double scale = 1;

	public LargeImageViewer(String url) throws IOException {
		image = new LargeImage(ImageIO.read(new File(url)));
		image.anchor = new Point(image.width/2, image.height/2);
		
		image.addTileResizeListener(this);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		repaint();
	}
	
	private Point getLowLimit(int width, int height) {
		return new Point((int) Math.floor(image.anchor.x - width/scale/2.0), (int) Math.floor(image.anchor.y - height/scale/2.0));
	}
	
	private Point getHighLimit(int width, int height) {
		return new Point((int) Math.ceil(image.anchor.x + width/scale/2.0), (int) Math.ceil(image.anchor.y + height/scale/2.0));
	}
	
	private Point getStartTile(Point low_limit) {
		Tile tile;
		Point position;
		int x, y;
		
		for (x = 0; x < image.cols; x++) {
			position = image.tilePos[x][0];
			tile = image.tiles[x][0];
			
			if((position.x + tile.width) > low_limit.x) {
				break;
			}
		}
		
		for (y = 0; y < image.rows; y++) {
			position = image.tilePos[0][y];
			tile = image.tiles[0][y];

			if((position.y + tile.height) > low_limit.y) {
				break;
			}
		}
		
		return new Point(x, y);
	}
	
	private Point getEndTile(Point high_limit) {
		Point position;
		int x, y;

		for (x = image.cols - 1; x >= 0; --x) {
			position = image.tilePos[x][0];
			
			if(position.x <= high_limit.x) {
				break;
			}
		}
		
		for (y = image.rows - 1; y >= 0 ; --y) {
			position = image.tilePos[0][y];

			if(position.y <= high_limit.y) {
				break;
			}
		}
		
		return new Point(x, y);
	}
	
	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int width = this.getWidth();
		int height = this.getHeight();
		
		checkAnchor(width, height);
		
		Point low_limit = getLowLimit(width, height);
		Point high_limit = getHighLimit(width, height);
		
		Point start_tile = getStartTile(low_limit);
		Point end_tile = getEndTile(high_limit);
		
		Tile tile = image.tiles[start_tile.x][start_tile.y];
		Point position = image.tilePos[start_tile.x][start_tile.y];
		int x_cood = (int) ((position.x - image.anchor.x)*scale + width / 2);
		int y_cood = (int) ((position.y - image.anchor.y)*scale + height / 2);
		int y_cood_bckp = y_cood;
		BufferedImage scaledTile = null;
		int x, y;
		
		for (x = start_tile.x; x <= end_tile.x; x++) {
			for (y = start_tile.y; y <= end_tile.y; y++) {
				position = image.tilePos[x][y];
				tile = image.tiles[x][y];
				
				scaledTile = tile.getScaledTile(scale);
				g2d.drawImage(scaledTile, x_cood, y_cood, null);
				//g2d.drawRect(x_cood, y_cood, scaledTile.getWidth(), scaledTile.getHeight());
				
				y_cood += scaledTile.getHeight();
			}
			x_cood += scaledTile.getWidth();
			y_cood = y_cood_bckp;
		}
	}
	
	protected void checkAnchor(int screen_width, int screen_height) {
		int max_anchor_x = (int) (image.width - screen_width / scale / 2);
		int max_anchor_y = (int) (image.height - screen_height / scale / 2);
		int min_anchor_x = (int) (screen_width / scale / 2);
		int min_anchor_y = (int) (screen_height / scale / 2);

		if (image.anchor.x > max_anchor_x) {
			image.anchor.x = max_anchor_x;
		} else if (image.anchor.x < min_anchor_x) {
			image.anchor.x = min_anchor_x;
		}

		if (image.anchor.y > max_anchor_y) {
			image.anchor.y = max_anchor_y;
		} else if (image.anchor.y < min_anchor_y) {
			image.anchor.y = min_anchor_y;
		}
	}
	
	@Override public void mousePressed(MouseEvent event) {
		dragStart = event.getPoint();
	}

	@Override public void mouseDragged(MouseEvent event) {
		Point dragEnd = event.getPoint();
		image.anchor.x -= (dragEnd.x - dragStart.x) / scale;
		image.anchor.y -= (dragEnd.y - dragStart.y) / scale;
		dragStart = dragEnd;
		repaint();
	}

	@Override public void mouseWheelMoved(MouseWheelEvent event) {
		scale -= event.getWheelRotation() * 0.075;
		scale = Math.max(scale, 0.075);
		scale = Math.min(scale, 3);
		repaint();
	}
	
	@Override public void mouseMoved(MouseEvent event) {}
	@Override public void mouseExited(MouseEvent event) {}
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}

	@Override public void tileResized(Tile tile, double scale) {
		repaint();
	}
}