import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Future;

public class TileResizer {
	protected final static ArrayList<TileResizeJob> queue;
	
	public static TileResizeJob add(TileResizeJob job) {
		synchronized(queue) {
			queue.add(job);
		}
		return job;
	}
	
	public static TileResizeJob add(Future<BufferedImage> task, Callback<BufferedImage> callback) {
		return add(new TileResizeJob(task, callback));		
	}

	public static void cancel(TileResizeJob job, boolean mayInterruptIfRunning) {
		synchronized(queue) {
			job.cancel(mayInterruptIfRunning);
			queue.remove(job);
		}
	}
	
	public static void cancelAll(boolean mayInterruptIfRunning) {
		synchronized(queue) {
			for (TileResizeJob job : queue) {
				job.cancel(mayInterruptIfRunning);
			}
			queue.clear();
		}
	}
	
	static {
		queue = new ArrayList<TileResizeJob>();
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		for(int i = 0 ; i < cores ; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override public void run() {
					while(true) {
						TileResizeJob job = null;
						synchronized(queue) {
							if(!queue.isEmpty()) {
								job = queue.remove(0);
							}
						}
						if(job != null) {
							job.complete();
						}
					}
				}
			});
			thread.start();
		}
	}
}