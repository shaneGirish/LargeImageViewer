import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
class LargeImageViewer extends JScrollPane {
	protected LargeImageComponent map;
	
	public LargeImageViewer(String url) throws IOException {
		super(new LargeImageComponent(url));
		map = (LargeImageComponent) this.getViewport().getView();
	}

	public static void main(String[] args) {
		try {
			JFrame window = new JFrame();
			window.add(new LargeImageViewer("Edinburgh.jpg"));
			window.setSize(800, 600);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
			window.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}