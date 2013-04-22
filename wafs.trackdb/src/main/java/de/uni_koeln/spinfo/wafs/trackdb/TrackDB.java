package de.uni_koeln.spinfo.wafs.trackdb;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.uni_koeln.spinfo.wafs.fslistener.PersistentWatcher;
import de.uni_koeln.spinfo.wafs.mp3.ChangeHandler;
import de.uni_koeln.spinfo.wafs.mp3.Mp3DirWatcher;
import de.uni_koeln.spinfo.wafs.mp3.TrackVisitor;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.lucene.Index;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;

/**
 * 
 * @author sschwieb
 *
 */
public class TrackDB {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	private final Index index;
	
	private final Timer commitTimer;
	
	private volatile int indexModified;
	
	private final Object writeLock = new Object(); 
	
	private final Mp3DirWatcher watcher;
	
	private volatile boolean stopped = false;
	
	private final FileFilter acceptFilter = new FileFilter() {
		
		public boolean accept(File pathname) {
			return pathname.getName().toLowerCase().endsWith(".mp3") && !pathname.getName().startsWith(".");
		}
	};
	
	private ChangeHandler handler = new ChangeHandler() {
		
		/**
		 * Updates the Track in the index, by first removing any
		 * track with the same location and than adding it.
		 * @see Index#remove(URI...)
		 * @see Index#add(Track)
		 */
		public void update(Track item) {
			try {
				logger.debug("Updating track " + item.getLocation());
				synchronized(writeLock) {
					index.remove(item.getLocation());
					index.add(item);
					indexModified++;
				}
			} catch (Exception e) {
				logger.error("Failed to update track " + item, e);
			}
		}
		
		/**
		 * Add a new Track to the index.
		 * @see Index#add(Track)
		 */
		public void add(Track item) {
			try {
				logger.debug("Adding track " + item.getLocation());
				synchronized(writeLock) {
					index.add(item);
					indexModified++;
				}
			} catch (Exception e) {
				logger.error("Failed to add track " + item, e);
			}
		}

		/**
		 * Remove the track from the index
		 * @see Index#remove(URI...)
		 */
		public void deleted(File file) {
			try {
				synchronized(writeLock) {
					logger.debug("Removing file " + file.toURI());
					index.remove(file.toURI());
					indexModified++;
				}
			} catch (IOException e) {
				logger.error("Failed to delete track " + file.toURI(), e);
			}
		}
	};
	
	/**
	 * Create a new Track DB.
	 * @param indexDir the directory to store the {@link Index}
	 * @param dbDir the directory to store the database one or more {@link PersistentWatcher}
	 * @param commitInterval interval in which the index will be updated, in milli seconds
	 * @throws IOException
	 */
	public TrackDB(File indexDir, File dbDir, long commitInterval) throws IOException {
		logger.info("Initializing track db...");
		index = new Index(indexDir);
		logger.info("Index contains " + index.getSize() + " tracks.");
		// Create a new Timer which will periodically be executed, to check
		// if the index has been modified and should be committed.
		commitTimer = new Timer("commit");
		indexModified = 0;
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				try {
					synchronized(writeLock) {
						if(indexModified > 0) {
							try {
								logger.info("Committing " + indexModified + " index changes...");
								index.commit();
								indexModified = 0;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					// Catch everything, otherwise the timer would stop
					logger.error("Exception in timer task!", e);
				}
			}
		};
		commitTimer.scheduleAtFixedRate(task, 1000, commitInterval);
		watcher = new Mp3DirWatcher(handler , acceptFilter , dbDir);
		logger.info("Initialization completed, commit interval is " + commitInterval + " ms.");
	}
	
	/**
	 * Stops all services, and commits the index. This method <strong>must</strong>
	 * be executed during shutdown, to ensure that the current state of the database
	 * is persisted.
	 */
	public void shutdown() {
		logger.info("Initializing shutdown...");
		stopped  = true;
		watcher.shutdown();
		commitTimer.cancel();
		try {
			index.commit();
		} catch (IOException e) {
			logger.error("Exception while committing index", e);
		}
		logger.info("Shutdown completed.");
	}
	
	
	/**
	 * Performs a search on the {@link Index}.
	 * @param q
	 * @return
	 * @throws IOException
	 */
	public Result search(WAFSQuery q) throws IOException {
		return index.search(q);
	}

	/**
	 * Returns the {@link Track} with the given URI, or
	 * <code>null</code> if the track does not exist, or if
	 * an error occurred.
	 * @param path the URI of the Track, as returned by {@link Track#getLocation()}
	 * @return
	 */
	public Track findTrack(URI path) {
		try {
			return index.findTrack(path);
		} catch (IOException e) {
			logger.warn("Exception while searching for track " + path,e);
			return null;
		}
	}

	/**
	 * Adds a new watch folder to the database. Changes in this folder will be
	 * reflected in the index, by registering the folder through {@link Mp3DirWatcher#addDirectory(File)}.
	 * If a database for this folder already exists, the index will asynchronously be synchronized
	 * with the help of a {@link TrackVisitor}, such that this method returns immediately.
	 * 
	 * @param mp3BaseDir
	 * @throws IOException
	 */
	public void addWatchFolder(final File mp3BaseDir) throws IOException {
		logger.info("Adding directory " + mp3BaseDir);
		watcher.addDirectory(mp3BaseDir);
		Thread visitThread = new Thread("visit") {
			
			@Override
			public void run() {
				logger.info("Updating db for directory " + mp3BaseDir);
				watcher.visitKnownTracks(new TrackVisitor() {
					
					public boolean visit(Track update) {
						handler.update(update);
						return !stopped;
					}

					public boolean updateRequired(URI path, long lastModified) {
						Track track = findTrack(path);
						if(track == null) {
							logger.debug("Track " + path + " must be added.");
							return true;
						}
						if(track.getLastModified() != lastModified) {
							logger.debug("Track " + path + " must be updated.");
							return true;
						}
						return false;
					}
				}, false);
				if(stopped) {
					logger.info("Interrupting DB update for directory " + mp3BaseDir + " - index contains " + index.getSize() + " tracks.");
				} else {
					logger.info("DB update for directory " + mp3BaseDir + " completed - index contains " + index.getSize() + " tracks.");
				}
			}
			
		};
		visitThread.start();
	}

}
