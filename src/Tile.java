import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

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
		TileCacheStore.put(new Dimension(width, height), this, this.data);
	}
	
	public BufferedImage getScaledTile(Dimension dimension) {
		BufferedImage scaledTile = TileCacheStore.get(dimension, this);
		if(scaledTile == null) {
			/*TileResizer.add(
				AsyncScalr.resize(data, Method.ULTRA_QUALITY, Mode.FIT_EXACT, newWidth, newHeight),
				new Callback<BufferedImage>() {
					@Override void invoke(BufferedImage result) {
						cache.put(key, result);

						for (TileResizeListener listener : listeners) {
							listener.tileResized(self, scale);
						}
					}
				}
			);
			return Scalr.resize(data, Method.SPEED, Mode.FIT_EXACT, newWidth, newHeight);
			*/
			scaledTile = Scalr.resize(data, Method.QUALITY, Mode.FIT_EXACT, dimension.width, dimension.height);
			TileCacheStore.put(dimension, this, scaledTile);
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
