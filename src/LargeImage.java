import java.awt.Point;
import java.awt.image.BufferedImage;

public class LargeImage {
	protected Tile[][] tiles;
	protected Point[][] tilePos;
	protected int width, height, rows, cols;
	protected Point anchor, dragStart;
	
	public LargeImage(BufferedImage image) {
		this(image, 100);
	}
	
	public LargeImage(BufferedImage image, double tileSize) {
		if (tileSize < 50) {
			throw new IllegalArgumentException("Tile size has to be more than 50.");
		}
		
		width = image.getWidth();
		height = image.getHeight();
		calculateTiles(image, tileSize);
	}
	
	public void addTileResizeListener(TileResizeListener listener) {
		for (Tile[] tmp : tiles) {
			for (Tile tile : tmp) {
				tile.listeners.add(listener);
			}
		}
	}
	
	public void removeTileResizeListener(TileResizeListener listener) {
		for (Tile[] tmp : tiles) {
			for (Tile tile : tmp) {
				tile.listeners.remove(listener);
			}
		}
	}
	
	protected void calculateTiles(BufferedImage image, double tileSize) {
		rows = (int) Math.round(height / tileSize);
		cols = (int) Math.round(width / tileSize);
		
		int tileWidth = (int) Math.floor((double) width / cols);
		int tileHeight = (int) Math.floor((double) height / rows);
		
		int x, y, w, h, x_pos, y_pos;
		BufferedImage tile;
		
		tiles = new Tile[cols][rows];
		tilePos = new Point[cols][rows];

		for (x = 0; x < cols; x++) {
			for (y = 0; y < rows; y++) {
				x_pos = tileWidth * x;
				y_pos = tileHeight * y;
				
				w = tileWidth;
				h = tileHeight;
				
				if(y == rows - 1) {
					h += height%tileHeight;
				}
				
				if(x == cols - 1) {
					w += width%tileWidth;
				}
				
				tile = image.getSubimage(x_pos, y_pos, w, h);
				
				tiles[x][y] = new Tile(x, y, tile);
			}
		}
		
		x_pos = 0;
		y_pos = 0;
		
		for (x = 0; x < cols; x++) {
			for (y = 0; y < rows; y++) {
				tilePos[x][y] = new Point(x_pos, y_pos);
				y_pos += tiles[x][y].height;
			}
			x_pos += tiles[x][y-1].width;
			y_pos = 0;
		}
	}
}
