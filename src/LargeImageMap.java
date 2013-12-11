import java.awt.Dimension;
import java.awt.Point;


public class LargeImageMap {
	public final Point[][] positions;
	public final Dimension[][] dimensions;
	public final Dimension bounds;
	
	public LargeImageMap(LargeImage image, double scale) {
		positions = new Point[image.cols][image.rows];
		dimensions = new Dimension[image.cols][image.rows];

		Tile[][] tiles = image.tiles;
		
		int w = (int) (tiles[0][0].width * scale);
		int h = (int) (tiles[0][0].height * scale);
		
		int width = w, height = h;
		int x,y;
		
		positions[0][0] = new Point();
		dimensions[0][0] = new Dimension(w, h);
		
		for (x = 1; x < image.cols; x++) {
			w = (int) (tiles[x][0].width * scale);
			h = (int) (tiles[x][0].height * scale);
			
			width += w;
			
			positions[x][0] = new Point(positions[x-1][0].x + dimensions[x-1][0].width, positions[x-1][0].y);
			dimensions[x][0] = new Dimension((int) (tiles[x][0].width * scale), (int) (tiles[x][0].height * scale));
		}
		
		for (y = 1; y < image.rows; y++) {
			w = (int) (tiles[0][y].width * scale);
			h = (int) (tiles[0][y].height * scale);
			
			height += h;
			
			positions[0][y] = new Point(positions[0][y-1].x, positions[0][y-1].y + dimensions[0][y-1].height);
			dimensions[0][y] = new Dimension((int) (tiles[0][y].width * scale), (int) (tiles[0][y].height * scale));
		}
		
		
		for (x = 1; x < image.cols; x++) {
			for (y = 1; y < image.rows; y++) {
				w = (int) (tiles[x][y].width * scale);
				h = (int) (tiles[x][y].height * scale);
				
				positions[x][y] = new Point(
					positions[x][y-1].x,
					positions[x-1][y].y
				);
				dimensions[x][y] = new Dimension(w, h);
			}
		}
		
		this.bounds = new Dimension(width, height);
	}
}
