package de.uni_koeln.spinfo.wafs.mp3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import de.uni_koeln.spinfo.wafs.fslistener.CorruptDBException;
import de.uni_koeln.spinfo.wafs.fslistener.FileEvent;
import de.uni_koeln.spinfo.wafs.fslistener.FileEventListener;
import de.uni_koeln.spinfo.wafs.fslistener.FileVisitor;
import de.uni_koeln.spinfo.wafs.fslistener.PersistentWatcher;
import de.uni_koeln.spinfo.wafs.fslistener.data.PersistentFile;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;

/**
 * A thread-safe service which will recursively scan multiple directories for
 * mp3-files. Changes (such as new files, deleted files, and modified files)
 * will be reported to the registered {@link ChangeHandler}.
 * 
 * @author sschwieb
 * 
 */
public class Mp3DirWatcher {
	
	private static Logger logger = Logger.getLogger(Mp3DirWatcher.class);
	
	static {
		synchronized (Mp3DirWatcher.class) {
			Properties props = new Properties();
			props.setProperty("org.jaudiotagger.level", Level.OFF.toString());
			props.setProperty(".level", Level.OFF.toString());
			props.setProperty("handlers",
					"java.util.logging.ConsoleHandler,java.util.logging.FileHandler");
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				props.store(baos, null);
				byte[] data = baos.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				LogManager.getLogManager().readConfiguration(bais);
			} catch (IOException e) {
				logger.error("Setup failed!");
				throw new RuntimeException(e);
			}
		}
	}

	private ExecutorService eventQueue = Executors.newSingleThreadExecutor();
	private ChangeHandler changeHandler;
	private Map<File, PersistentWatcher> services = new HashMap<File, PersistentWatcher>();
	private FileFilter acceptFilter;

	private FileEventListener listener = new FileEventListener() {

		@Override
		public void handleEvent(FileEvent event) {
			File file = new File(event.getObject().getPath());
			switch (event.getType()) {
			case MODIFIED:
			case ADDED:
				try {
					if(file.isFile()) {
						eventQueue.submit(new UpdateTask(update(file)));
					}
				} catch (Mp3Exception e) {
					logger.error(e);
				}
				break;
			case DELETED:
				eventQueue.submit(new DeleteTask(new File(event.getObject()
						.getPath())));
				break;
			}
		}

	};
	private File dbDir;

	/**
	 * Create a new instance of this class with the given {@link ChangeHandler},
	 * which must not be <code>null</code>.
	 * 
	 * @param handler
	 */
	public Mp3DirWatcher(ChangeHandler handler,
			FileFilter acceptFilter, File dbDir) {
		this.changeHandler = handler;
		this.acceptFilter = acceptFilter;
		this.dbDir = dbDir;
	}

	class UpdateTask implements Runnable {

		private Track track;

		public UpdateTask(Track track) {
			this.track = track;
		}

		@Override
		public void run() {
			changeHandler.update(track);
		}

	}

	class DeleteTask implements Runnable {

		private File file;

		public DeleteTask(File file) {
			this.file = file;
		}

		@Override
		public void run() {
			changeHandler.deleted(file);
		}

	}

	/**
	 * Add a new directory to the service. A {@link WatchService} will be added
	 * to the directory, and a recursive scan of all subdirectories will be
	 * initiated. Both Tasks will be executed asynchroneously, such that the
	 * method returns immediately. This method is thread-safe.
	 * 
	 * If the directory is already being watched, nothing will happen.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public void addDirectory(File directory) throws IOException {
		PersistentWatcher watcher = services.get(directory);
		File dbFile = getDbFile(directory);
		if (watcher != null)
			return;
		try {
			watcher = new PersistentWatcher(directory, dbFile, acceptFilter,
					listener);
		} catch (CorruptDBException e) {
			logger.warn("Deleting corrupt database...");
			dbFile.delete();
			try {
				watcher = new PersistentWatcher(directory, dbFile,
						acceptFilter, listener);
			} catch (CorruptDBException e1) {
				throw new IOException("Failed to initialize watcher", e);
			}
		}
	}

	private File getDbFile(File directory) {
		return new File(dbDir, directory.getAbsolutePath().hashCode() + ".xml");
	}

	/**
	 * Removes the directory from the list of watched directories, and stops
	 * scanning the directory and watching changes. All currently running
	 * operations will be finished asynchronously, such that events can be
	 * propagated even after calling this method. The method is thread-safe.
	 * 
	 * @param directory
	 */
	public void removeDirectory(File directory) {
		PersistentWatcher watcher = services.remove(directory);
		try {
			watcher.shutdown(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Track update(File file) throws Mp3Exception {
			try {
					MP3File f = (MP3File) AudioFileIO.read(file);
					AbstractID3Tag abstractTag = f.getID3v2Tag();
					Track item = new Track();
					item.setLocation(file.toURI());
					item.setLastModified(file.lastModified());
					if (abstractTag == null) {
						item.setTitle(file.getName());
						item.setArtist("Unknown");
						item.setAlbum("Unknown");
					} else {
						if (abstractTag instanceof AbstractID3v2Tag) {
							AbstractID3v2Tag tag = (AbstractID3v2Tag) abstractTag;
							item.setArtist(tag.getFirst(FieldKey.ARTIST));
							item.setArtistSort(tag.getFirst(FieldKey.ARTIST_SORT));
							item.setAlbum(tag.getFirst(FieldKey.ALBUM));
							item.setAlbumSort(tag.getFirst(FieldKey.ALBUM_SORT));
							item.setAlbumArtistSort(tag
									.getFirst(FieldKey.ALBUM_ARTIST_SORT));
							item.setDiscNo(parseInt(tag.getFirst(FieldKey.DISC_NO)));
							item.setDiscTotal(parseInt(tag
									.getFirst(FieldKey.DISC_TOTAL)));
							item.setGenre(tag.getFirst(FieldKey.GENRE));
							item.setCompilation(parseBoolean(tag
									.getFirst(FieldKey.IS_COMPILATION)));
							item.setTitle(tag.getFirst(FieldKey.TITLE));
							item.setTitleSort(tag.getFirst(FieldKey.TITLE_SORT));
							item.setTrack(parseInt(tag.getFirst(FieldKey.TRACK)));
							item.setTrackTotal(parseInt(tag
									.getFirst(FieldKey.TRACK_TOTAL)));
							item.setYear(parseInt(tag.getFirst(FieldKey.YEAR)));
						} else if (abstractTag instanceof ID3v1Tag) {
							ID3v1Tag tag = (ID3v1Tag) abstractTag;
							item.setArtist(tag.getFirst(FieldKey.ARTIST));
							item.setArtistSort(tag.getFirst(FieldKey.ARTIST_SORT));
							item.setAlbum(tag.getFirst(FieldKey.ALBUM));
							item.setAlbumSort(tag.getFirst(FieldKey.ALBUM_SORT));
							item.setAlbumArtistSort(tag
									.getFirst(FieldKey.ALBUM_ARTIST_SORT));
							item.setDiscNo(parseInt(tag.getFirst(FieldKey.DISC_NO)));
							item.setDiscTotal(parseInt(tag
									.getFirst(FieldKey.DISC_TOTAL)));
							item.setGenre(tag.getFirst(FieldKey.GENRE));
							item.setCompilation(parseBoolean(tag
									.getFirst(FieldKey.IS_COMPILATION)));
							item.setTitle(tag.getFirst(FieldKey.TITLE));
							item.setTitleSort(tag.getFirst(FieldKey.TITLE_SORT));
							item.setTrack(parseInt(tag.getFirst(FieldKey.TRACK)));
							item.setTrackTotal(parseInt(tag
									.getFirst(FieldKey.TRACK_TOTAL)));
							item.setYear(parseInt(tag.getFirst(FieldKey.YEAR)));
						} else {
							logger.error("Neither ID3v1 nor ID3v2: "
									+ file.getAbsolutePath());
						}
					}
					try {
						item.setLength(f.getAudioHeader().getTrackLength());
						item.setBitRate(parseInt(f.getAudioHeader().getBitRate()));
					} catch (Exception e) {
						logger.error(
								"Failed to parse audio header "
										+ file.getAbsolutePath(), e);
					}
					return item;
			} catch (Exception e) {
				throw new Mp3Exception("Failed to parse ID3 header for " + file, e);
			}
	}

	private boolean parseBoolean(String input) {
		if (input == null)
			return false;
		return Boolean.parseBoolean(input.trim());
	}

	private int parseInt(String input) {
		if (input == null)
			return -1;
		try {
			return Integer.parseInt(input.trim());
		} catch (NumberFormatException e) {
			return -2;
		}
	}
	
	public void visitKnownTracks(final TrackVisitor visitor, final boolean stopOnError) {
		FileVisitor fileVisitor = new FileVisitor() {

			@Override
			public boolean visit(PersistentFile file) {
				try {
					return visitor.visit(update(new File(file.getPath())));
				} catch (Mp3Exception e) {
					return !stopOnError;
				}
			}
			
		};
		for (PersistentWatcher watcher : services.values()) {
			watcher.visitKnownFiles(fileVisitor);
		}
	}

}
