import java.awt.image.BufferedImage;

public class ScaledTileCacheValue {
	public int count = 0;
	public int life = 0;
	public BufferedImage image = null;
	
	public ScaledTileCacheValue(int count, int life, BufferedImage image) {
		this.count = count;
		this.life = life;
		this.image = image;
	}
	
	public int getAdjustedCount() {
		return count/TileCacheStore.LIFE + life;
	}
	
	public double getSize() {		
		double result = image.getWidth() * image.getHeight() * image.getColorModel().getPixelSize() / 8.0 / 1024.0;
		return result;
	}
}