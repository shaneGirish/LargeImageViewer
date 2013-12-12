import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
	
	protected static double scaleIncrement = 0.1;

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
	
	private boolean aboveTilingThreshold() {
		double extentArea = getExtentSize().getWidth() * getExtentSize().getHeight();
		double imageArea = image.width * image.height * scale * scale;
		
		return extentArea / imageArea > 0.5;
	}
	
	@Override public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		if(aboveTilingThreshold()) {
			Dimension dimension = new Dimension(
				(int) (image.width * scale),
				(int) (image.height * scale)
			);
			BufferedImage scaledTile = image.original.getScaledTile(dimension);
			g2d.drawImage(scaledTile, 0, 0, null);
		} else {
			drawMap(g2d, image.getScaledMap(scale));			
		}
	}
	
	private void drawMap(Graphics2D g2d, LargeImageMap map) {
		Tile tile;
		Point position;
		boolean inX, inY;
		Dimension dimension;
		BufferedImage scaledTile;

		Point anchor = getViewPosition();
		
		double width = getExtentSize().getWidth();
		double height = getExtentSize().getHeight();
		
		for (int x = 0; x < image.cols ; x++) {
			for (int y = 0; y < image.rows ; y++) {
				tile = image.tiles[x][y];
				dimension = map.dimensions[x][y];
				position = map.positions[x][y];
				
				inX = position.x + dimension.width > anchor.x && position.x < anchor.x + width;
				inY = position.y + dimension.height > anchor.y && position.y < anchor.y + height;
				
				if(inX && inY) {
					scaledTile = tile.getScaledTile(dimension);
					g2d.drawImage(scaledTile, position.x, position.y, null);
					//g2d.drawRect(position.x, position.y, scaledTile.getWidth(), scaledTile.getHeight());
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
		
		repaint();
	}
	
	@Override public void setSize(Dimension d) {
	    setPreferredSize(d);
		super.setSize(d);
	}

	@Override public void mouseWheelMoved(MouseWheelEvent event) {
		Double scaleRatio = scale;
		scale -= event.getWheelRotation() * scaleIncrement;
		
		scale = Math.max(scale, 0.1);
		scale = Math.min(scale, 3.0);
		
		scaleRatio = scale / scaleRatio;
		
		if(aboveTilingThreshold()) {
			Tile tile = image.original;
			setSize(new Dimension((int) (tile.width * scale), (int) (tile.height * scale)));
		} else {
			LargeImageMap map = image.getScaledMap(scale);
		    setSize(map.bounds);
		}
		
		Rectangle viewRect = getParent().getViewRect();
		getParent().setViewPosition(
			new Point(
				(int) ((viewRect.x + viewRect.width/2) * scaleRatio - viewRect.width/2),
				(int) ((viewRect.y + viewRect.height/2) * scaleRatio - viewRect.height/2)
			)
		);

		repaint();
	}
	
	@Override public void mouseMoved(MouseEvent event) {}
	@Override public void mouseExited(MouseEvent event) {}
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}

	@Override public void tileResized(Tile tile) {
		repaint();
	}
}