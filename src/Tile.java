import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class Tile {
	protected final Tile self;
	protected final int width, height;
	protected final BufferedImage data;
	protected final Point position;
	
	protected final ArrayList<TileResizeListener> listeners = new ArrayList<TileResizeListener>();
	
	public Tile(Point position, BufferedImage data) {
		this.self = this;
		this.width = data.getWidth();
		this.height = data.getHeight();
		this.position = position;
		this.data = data;
		TileCacheStore.put(1.0, this, this.data);
	}
	
	public BufferedImage getScaledTile(double scale) {
		BufferedImage scaledTile = TileCacheStore.get(scale, this);
		if(scaledTile == null) {
			int newWidth = (int) (width * scale);
			int newHeight = (int) (height * scale);
			/*TileResizer.add(
				AsyncScalr.resize(data, Method.ULTRA_QUALITY, newWidth, newHeight),
				new Callback<BufferedImage>() {
					@Override void invoke(BufferedImage result) {
						cache.put(key, result);

						for (TileResizeListener listener : listeners) {
							listener.tileResized(self, scale);
						}
					}
				}
			);
			return Scalr.resize(data, Method.SPEED, newWidth, newHeight);
			*/
			scaledTile = Scalr.resize(data, Method.QUALITY, newWidth, newHeight);
			TileCacheStore.put(scale, this, scaledTile);
		}
			
		return scaledTile;
	}

	/*@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + width;
		return result;
	}*/

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tile))
			return false;
		
		Tile other = (Tile) obj;
		if (height != other.height)
			return false;
		if (!position.equals(other.position))
			return false;
		if (width != other.width)
			return false;
		
		return true;
	}
}
