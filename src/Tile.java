import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr.Method;

public class Tile {
	final int width, height;
	final BufferedImage data;
	final ConcurrentHashMap<Integer, BufferedImage> cache = new ConcurrentHashMap<Integer, BufferedImage>();
	final ConcurrentHashMap<Integer, Integer> cacheHitRatio = new ConcurrentHashMap<Integer, Integer>();
	
	public Tile(BufferedImage data) {
		this.width = data.getWidth();
		this.height = data.getHeight();
		this.data = data;
		cache.put(1, this.data);
	}
	
	private Integer getKey(double scale) {
		return (int) Math.floor(scale * 1000);		
	}
	
	public BufferedImage getScaledTile(final double scale) {
		final Integer key = getKey(scale);
		
		if(!cache.containsKey(scale)) {
			int newWidth = (int) (width * scale);
			int newHeight = (int) (height * scale);
			TileResizer.add(
				AsyncScalr.resize(data, Method.ULTRA_QUALITY, newWidth, newHeight),
				new Callback<BufferedImage>() {
					@Override void invoke(BufferedImage result) {
						cache.put(key, result);
						cacheHitRatio.put(key, 1);
					}
				}
			);
		} else {
			cacheHitRatio.put(key, cacheHitRatio.get(key) + 1);
		}
		return cache.get(key);
	}
}
