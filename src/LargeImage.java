import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class LargeImage {
	protected final Tile[][] tiles;
	protected final int width, height, rows, cols;
	
	protected final HashMap<Integer, LargeImageMap> scaledMaps = new HashMap<Integer, LargeImageMap>();
	
	public LargeImage(BufferedImage image) {
		this(image, 100);
	}
	
	public LargeImage(BufferedImage image, double tileSize) {
		if (tileSize < 50) {
			throw new IllegalArgumentException("Tile size has to be more than 50.");
		}
		
		width = image.getWidth();
		height = image.getHeight();
		
		rows = (int) Math.round(height / tileSize);
		cols = (int) Math.round(width / tileSize);
		
		tiles = new Tile[cols][rows];
		
		tileImage(image);
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
	
	protected LargeImageMap getScaledMap(double scale) {
		int key = DoubleKey.getKey(scale);
		
		LargeImageMap map = scaledMaps.get(key);
		if(map == null) {
			map = new LargeImageMap(this, scale);
			scaledMaps.put(key, map);
		}
		
		return map;
	}
	
	protected void tileImage(BufferedImage image) {
		int tileWidth = (int) Math.floor((double) width / cols);
		int tileHeight = (int) Math.floor((double) height / rows);
		
		int x, y, w, h, x_pos, y_pos;
		BufferedImage tile;
		Point position;

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
				position = new Point(x_pos, y_pos);
				
				tiles[x][y] = new Tile(position, tile);
			}
		}
	}
}
