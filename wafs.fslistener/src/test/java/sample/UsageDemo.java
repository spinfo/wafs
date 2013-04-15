package sample;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.uni_koeln.spinfo.wafs.fslistener.CorruptDBException;
import de.uni_koeln.spinfo.wafs.fslistener.FileEvent;
import de.uni_koeln.spinfo.wafs.fslistener.FileEventListener;
import de.uni_koeln.spinfo.wafs.fslistener.PersistentWatcher;

public class UsageDemo {

	private static Logger logger = Logger.getLogger(UsageDemo.class);


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		FileFilter filter = new FileFilter() {
			 
			@Override
			public boolean accept(File pathname) {
				return true;
			}
		};
		File projectHome = new File("test").getAbsoluteFile().getParentFile();
		try { 
			PersistentWatcher watcher = new PersistentWatcher(projectHome, new File("db.xml"), filter, new FileEventListener() {
				 
				@Override
				public void handleEvent(FileEvent event) {
					logger.info("Received event " + event);
				}
			});
//			Thread.sleep(10000);
//			watcher.shutdown(20000);
		} catch (CorruptDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		}
	}

}
