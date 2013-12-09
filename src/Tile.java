import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class Tile {
	protected final Tile self;
	protected final int x, y;
	protected final int width, height;
	protected final BufferedImage data;
	protected final ConcurrentHashMap<Integer, BufferedImage> cache = new ConcurrentHashMap<Integer, BufferedImage>();
	protected final ConcurrentHashMap<Integer, Integer> reqCounts = new ConcurrentHashMap<Integer, Integer>();

	protected final ArrayList<TileResizeListener> listeners = new ArrayList<TileResizeListener>();
	
	public Tile(int x, int y, BufferedImage data) {
		this.self = this;
		this.x = x;
		this.y = y;
		this.width = data.getWidth();
		this.height = data.getHeight();
		this.data = data;
		cache.put(getKey(1.0), this.data);
	}
	
	private Integer getKey(double scale) {
		return (int) Math.floor(scale * 1000);		
	}
	
	public BufferedImage getScaledTile(final double scale) {
		final Integer key = getKey(scale);
		
		reqCounts.putIfAbsent(key, 1);
		reqCounts.put(key, reqCounts.get(key) + 1);
		
		if(!cache.containsKey(key)) {
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
			BufferedImage result = Scalr.resize(data, Method.QUALITY, newWidth, newHeight);
			cache.put(key, result);
			return result;
		}
			
		return cache.get(key);
	}
}
