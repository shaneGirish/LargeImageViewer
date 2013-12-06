import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

public class TileResizeJob {
	public final Future<BufferedImage> task;
	public final Callback<BufferedImage> callback;

	public TileResizeJob(Future<BufferedImage> task, Callback<BufferedImage> callback) {
		this.task = task;
		this.callback = callback;
	}
	
	public boolean isDone() {
		return task.isDone();
	}
	
	public void cancel(boolean mayInterruptIfRunning) {
		task.cancel(mayInterruptIfRunning);
	}
	
	public void complete() {
		BufferedImage result = null;
		try {
			result = task.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		final BufferedImage scaledTile = result;
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				callback.invoke(scaledTile);
			}
		});
	}
}