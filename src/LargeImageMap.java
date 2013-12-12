import java.awt.Dimension;
import java.awt.Point;

public class LargeImageMap {
	public final Point[][] positions;
	public final Dimension[][] dimensions;
	public final Dimension bounds;
	
	private void fillWidth(LargeImage image, int difference) {
		int x, y, i, diff;
		
		for(y = 0; y < image.rows; y++) {
			i = 0;
			diff = difference;
			while(diff > 0) {
				++dimensions[i][y].width;
				for (x = ++i; x < image.cols; x++) {
					++positions[x][y].x;
				}
				--diff;
			}
		}
	}
	
	private void fillHeight(LargeImage image, int difference) {
		int x, y, i, diff;
		
		for(x = 0; x < image.cols; x++) {
			i = 0;
			diff = difference;
			while(diff > 0) {
				++dimensions[x][i].height;
				for (y = ++i; y < image.rows; y++) {
					++positions[x][y].y;
				}
				--diff;
			}
		}
	}
	
	public LargeImageMap(LargeImage image, double scale) {
		positions = new Point[image.cols][image.rows];
		dimensions = new Dimension[image.cols][image.rows];

		Tile[][] tiles = image.tiles;
		
		int w = (int) (tiles[0][0].width * scale);
		int h = (int) (tiles[0][0].height * scale);
		
		int width = w, height = h;
		int x, y;
		
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
		
		w = (int) (image.width * scale);
		h = (int) (image.height * scale);
		
		fillWidth(image, w - width);
		fillHeight(image, h - height);
		
		this.bounds = new Dimension(w, h);

		/*System.out.println(scale);
		System.out.println(this.bounds);
		System.out.println(new Dimension((int) (image.width * scale), (int) (image.height * scale)));
		System.out.println();*/
	}
}
