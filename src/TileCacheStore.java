import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TileCacheStore {
	public static final int MAX_MEMORY = 1000 * 1000;
	protected static int LIFE = 0;
	protected static int SIZE = 0;
	protected static HashMap<ScaledTileCacheKey, ScaledTileCacheValue> cache = new HashMap<ScaledTileCacheKey, ScaledTileCacheValue>();
	
	public static BufferedImage get(Dimension dimension, Tile tile) {
		ScaledTileCacheKey key = new ScaledTileCacheKey(dimension, tile);
		ScaledTileCacheValue tileCache = cache.get(key);
		
		if(tileCache != null) {
			++tileCache.count;
			tileCache.life = ++LIFE;
			return tileCache.image;
		}
		
		return null;
	}

	public static BufferedImage put(Dimension dimension, Tile tile, BufferedImage image) {
		ScaledTileCacheKey key = new ScaledTileCacheKey(dimension, tile);
		ScaledTileCacheValue tileCache = new ScaledTileCacheValue(1, ++LIFE, image);
		SIZE += tileCache.getSize();
		
		cache.put(key, tileCache);
		
		trim();
		
		return tileCache.image;
	}
	
	private static Comparator<Map.Entry<ScaledTileCacheKey, ScaledTileCacheValue>> comparator = new Comparator<Map.Entry<ScaledTileCacheKey, ScaledTileCacheValue>>() {
		@Override public int compare(Map.Entry<ScaledTileCacheKey, ScaledTileCacheValue> o1, Map.Entry<ScaledTileCacheKey, ScaledTileCacheValue> o2) {
			return o2.getValue().getAdjustedCount() - o1.getValue().getAdjustedCount();
		}
    };
	
	public static void trim() {
		if(SIZE > MAX_MEMORY) {
			ArrayList<Map.Entry<ScaledTileCacheKey,ScaledTileCacheValue>> sorted_list = new ArrayList<Map.Entry<ScaledTileCacheKey,ScaledTileCacheValue>>(cache.entrySet());
		    Collections.sort(sorted_list, comparator);
		    
		    for (int i = 0; i < sorted_list.size(); i++) {
		    	Map.Entry<ScaledTileCacheKey,ScaledTileCacheValue> entry = sorted_list.get(i);
		    	ScaledTileCacheValue tileCache = entry.getValue();
		    	
		    	cache.remove(entry.getKey());
		    	SIZE -= tileCache.getSize();
		    	
		    	tileCache.image.flush();
		    	tileCache.image = null;
		    	
		    	if(SIZE > 0.75 * MAX_MEMORY) {
		    		break;
		    	}
			}
		}
	}
}