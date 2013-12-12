import java.awt.Dimension;
import java.awt.Point;

public class LargeImageMap {
	public final Point[][] positions;
	public final Dimension[][] dimensions;
	public final Dimension bounds;
	
	public LargeImageMap(LargeImage image, double scale) {
		positions = new Point[image.cols][image.rows];
		dimensions = new Dimension[image.cols][image.rows];
		
		int w_diff = (int) (image.width * scale);
		int h_diff = (int) (image.height * scale);

		Tile[][] tiles = image.tiles;
		
		int w = (int) (tiles[0][0].width * scale);
		int h = (int) (tiles[0][0].height * scale);
		
		int width = w, height = h;
		int x, y, i;
		
		positions[0][0] = new Point();
		dimensions[0][0] = new Dimension(w, h);
		
		for (x = 1; x < image.cols; x++) {
			w = (int) (tiles[x][0].width * scale);
			h = (int) (tiles[x][0].height * scale);
			
			width += w;
			
			positions[x][0] = new Point(positions[x-1][0].x + dimensions[x-1][0].width, positions[x-1][0].y);
			dimensions[x][0] = new Dimension(w,h);
		}
		
		for (y = 1; y < image.rows; y++) {
			w = (int) (tiles[0][y].width * scale);
			h = (int) (tiles[0][y].height * scale);
			
			height += h;
			
			positions[0][y] = new Point(positions[0][y-1].x, positions[0][y-1].y + dimensions[0][y-1].height);
			dimensions[0][y] = new Dimension(w,h);
		}
		
		w_diff -= width;
		h_diff -= height;
		
		i = 0;
		while(w_diff > 0) {
			++dimensions[i][0].width;
			for (x = ++i; x < image.cols; x++) {
				++positions[x][0].x;
			}
			--w_diff;
			++width;
		}
		
		i = 0;
		while(h_diff > 0) {
			++dimensions[0][i].height;
			for (y = ++i; y < image.rows; y++) {
				++positions[0][y].y;
			}
			--h_diff;
			++height;
		}
		
		for (y = 0; y < h_diff; y++) {
			++positions[y+1][0].y;
			++dimensions[y][0].height;
		}
		
		for (x = 1; x < image.cols; x++) {
			for (y = 1; y < image.rows; y++) {				
				positions[x][y] = new Point(
					positions[x][y-1].x,
					positions[x-1][y].y
				);
				dimensions[x][y] = new Dimension(
					dimensions[x-1][y].width,
					dimensions[x][y-1].height
				);
			}
		}
		
		this.bounds = new Dimension(width, height);
		
		System.out.println(scale);
		System.out.println(this.bounds);
		System.out.println(new Dimension((int) (image.width * scale), (int) (image.height * scale)));
		System.out.println();
	}
}
