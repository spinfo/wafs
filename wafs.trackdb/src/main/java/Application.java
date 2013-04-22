
import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.uni_koeln.spinfo.wafs.mp3.Mp3DirWatcher;
import de.uni_koeln.spinfo.wafs.mp3.TrackVisitor;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.spinfo.wafs.trackdb.TrackDB;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;
import de.uni_koeln.wafs.datakeeper.util.TrackField;


public class Application {
	
	private Logger logger = Logger.getLogger(getClass());
	
	public static void main(String[] args) {
		new Application().testIndexUsage();
	}
	
	@Test
	public void testIndexUsage() {
		try {
			final TrackDB handler = new TrackDB(new File("target/lucene"), new File("target/db"), 8000);
			handler.addWatchFolder(new File("/Volumes/zbox/iTunes/iTunes Media/Music/"));
			logger.info("Creating watcher...");
			int counter = 0;
			while(true) {
				try {
					WAFSQuery query = new WAFSQuery();
					query.setCurrentPage(0);
					query.setPageSize(10);
					query.setExact(false);
					query.setOr(false);
					Map<String, String> values = new HashMap<String, String>();
					values.put(TrackField.ARTIST.toString(), "AudioSlave");
					//values.put(TrackField.ALBUM.toString(), "dark");
					query.setValues(values);
					Result result = handler.search(query);
					if(result == null) {
						logger.info("Query failed, index not yet available...");
					} else {
						logger.info("Query result: " + result.getMaxEntries() + " entries.");
					}
					Thread.sleep(5000);
					counter++;
					logger.info("Counter: " + counter);
					if(counter == 20) {
						handler.shutdown();
						break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			logger.info("An error occurred!", e);
		}
		
		
	}

}
