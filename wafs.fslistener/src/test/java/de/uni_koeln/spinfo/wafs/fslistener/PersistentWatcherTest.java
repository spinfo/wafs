package de.uni_koeln.spinfo.wafs.fslistener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uni_koeln.spinfo.wafs.fslistener.FileEvent.Type;
import de.uni_koeln.spinfo.wafs.fslistener.data.PersistentFile;

public class PersistentWatcherTest {
	
	private List<File> toDelete;
	
	private List<FileEvent> eventList;
	
	private File toWatch;
	private File dbFile;
	
		
	@Test(expected = IOException.class)
	public void testNotExistingToWatchFile() throws CorruptDBException, IOException, NoSuchFileException{
		toWatch = new File("notexisting.testfile");
		PersistentWatcher watcher = new PersistentWatcher(toWatch, dbFile, filter, new FileEventListener() {				 
			@Override
			public void handleEvent(FileEvent event) {
				eventList.add(event);
				logger.info("Received event " + event);
			}
		});
	}	
	
	private static Logger logger = Logger.getLogger(PersistentWatcherTest.class);

	private FileFilter filter = new FileFilter() {			  
		@Override
		public boolean accept(File pathname) {
			if(pathname.getAbsolutePath().endsWith(".testfile"))
				return true;
			return false;
		}
	};
		
		
}
