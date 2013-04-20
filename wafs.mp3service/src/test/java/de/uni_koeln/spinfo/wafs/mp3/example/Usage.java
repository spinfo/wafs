package de.uni_koeln.spinfo.wafs.mp3.example;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.log4j.Logger;

import de.uni_koeln.spinfo.wafs.mp3.ChangeHandler;
import de.uni_koeln.spinfo.wafs.mp3.CoverDB;
import de.uni_koeln.spinfo.wafs.mp3.Mp3DirWatcher;
import de.uni_koeln.spinfo.wafs.mp3.Mp3Exception;
import de.uni_koeln.spinfo.wafs.mp3.TrackVisitor;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public class Usage {

	private static Logger logger = Logger.getLogger(Usage.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File coverDir = new File("covers");
		coverDir.mkdirs();
		final CoverDB covers = new CoverDB(coverDir);
		
		// As MacOS sucks, there might be problems with umlauts etc.
		// If so, add an environment option: LC_CTYPE UTF-8
		
		// General usage: 
		// a) Create a change handler to deal with events
		
		ChangeHandler handler = new ChangeHandler() {
			
			@Override
			public void update(Track item) {
				logger.info("Updating track " + item);
				covers.storeCover(item);
			}
			
			@Override
			public void deleted(File file) {
				logger.info("File deleted: " + file);
			}

			@Override
			public void add(Track track) {
				logger.info("Added track: " + track);
				covers.storeCover(track);
			}
		};
		
		// b) initialize a directory service
		
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".mp3");
			}
		};
		File dbDir = new File("db");
		dbDir.mkdirs();
		Mp3DirWatcher service = new Mp3DirWatcher(handler, filter, dbDir);
		
		// c) add or remove directories
		
		try {
			service.addDirectory(new File("E:\\Heino"));
			
			service.visitKnownTracks(new TrackVisitor() {
				
				@Override
				public boolean visit(Track update) {
					// TODO: Check if file is already indexed, add if not
					return true;
				}
			}, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
