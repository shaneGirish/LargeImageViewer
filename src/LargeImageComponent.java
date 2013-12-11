import java.awt.Dimension;
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
import javax.swing.JViewport;

@SuppressWarnings("serial")
class LargeImageComponent extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, TileResizeListener {
	protected LargeImage image;
	
	protected Point dragStart;
	protected double scale = 1;

	public LargeImageComponent(String url) throws IOException {
		super();
		image = new LargeImage(ImageIO.read(new File(url)));
		image.addTileResizeListener(this);
		
		setMinimumSize(new Dimension(image.width, image.height));
		setPreferredSize(new Dimension(image.width, image.height));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		repaint();
	}
	
	@Override public JViewport getParent() {
		return (JViewport) super.getParent();
	}
	
	private Dimension getExtentSize() {
		return getParent().getExtentSize();
	}
	
	private Point getViewPosition() {
		return getParent().getViewPosition();
	}

	private void setViewPosition(Point point) {
		getParent().setViewPosition(point);
	}
	
	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		double width = getExtentSize().getWidth();
		double height = getExtentSize().getHeight();
		
		Point anchor = getViewPosition();
		
		Tile tile;
		Point position;
		BufferedImage scaledTile;
		boolean inX, inY;
		
		for (int x = 0; x < image.cols ; x++) {
			for (int y = 0; y < image.rows ; y++) {
				tile = image.tiles[x][y];
				position = tile.position;
				
				inX = position.x + tile.width > anchor.x && position.x < anchor.x + width;
				inY = position.y + tile.height > anchor.y && position.y < anchor.y + height;
				
				if(inX && inY) {
					scaledTile = tile.getScaledTile(scale);
					g2d.drawImage(scaledTile, position.x, position.y, null);
					g2d.drawRect(position.x, position.y, scaledTile.getWidth(), scaledTile.getHeight());
				}
			}
		}
	}

	@Override public void mousePressed(MouseEvent event) {
		dragStart = new Point(event.getXOnScreen(), event.getYOnScreen());
	}

	@Override public void mouseDragged(MouseEvent event) {
		Point dragEnd = new Point(event.getXOnScreen(), event.getYOnScreen());
		
		Point viewPosition = getViewPosition();
		Dimension extentSize = getExtentSize();
				
		int x = getViewPosition().x - (dragEnd.x - dragStart.x);
		x = Math.max(x, 0);
		x = Math.min(x, viewPosition.x + extentSize.width);
		
		int y = getViewPosition().y - (dragEnd.y - dragStart.y);
		y = Math.max(y, 0);
		y = Math.min(y, viewPosition.y + extentSize.height);
		
		setViewPosition(new Point(x, y));
		dragStart = dragEnd;
		
		//revalidate();
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