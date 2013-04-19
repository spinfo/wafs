package de.uni_koeln.spinfo.wafs.fslistener;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
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

/**
 * A Test Class for de.uni_koeln.spinfo.wafs.fslistener.PersistentWatcher
 * 
 * @author jhermes
 * 
 */
public class PersistentWatcherWithSetupTest {

	// Records files that were created in the unit tests to delete them after
	// execution
	private List<File> toDelete;

	// List of events that were thrown by the watcher
	private List<FileEvent> eventList;

	// Observed Directory
	private File toWatch;

	// Database file
	private File dbFile;

	// The tested PersistentWatcher
	private PersistentWatcher watcher;

	/**
	 * The method with the "@Before"-Annotation will be executed before any
	 * test.
	 */
	@Before
	public void setUp() {
		try {
			toDelete = new ArrayList<File>();
			eventList = new ArrayList<FileEvent>();

			toWatch = new File("test");
			toWatch.mkdir();
			dbFile = new File(toWatch.getAbsolutePath() + "/db_test.xml");
			toDelete.add(dbFile);
			watcher = new PersistentWatcher(toWatch, dbFile, filter,
					new FileEventListener() {
						@Override
						public void handleEvent(FileEvent event) {
							eventList.add(event);
							logger.info("Received event " + event);
						}
					});
		} catch (CorruptDBException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The method with the "@After"-Annotation will be executed after any test.
	 */
	@After
	public void tearDown() {

		try {
			watcher.shutdown(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (File file : toDelete) {
			System.out.println();
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * Adds a new file to the observed folder (at top level) and checks if
	 * watcher fires an "Added"-FileEvent
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testAddFileToTopLevelFolder() throws IOException,
			InterruptedException {
		// Add new file to observed folder
		File file = new File(toWatch.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		toDelete.add(file);

		// Wait for event
		boolean foundEvent = false;
		for (int i = 0; i < 10; i++) {
			for (FileEvent fe : eventList) {
				if (fe.getType().equals(Type.ADDED)) {
					File newFile = new File(fe.getObject().getPath());
					if (newFile.equals(file)) {
						foundEvent = true;
						break;
					}
				}
			}
			if (foundEvent) {
				break;
			}
			Thread.sleep(500);
		}
		Assert.assertTrue(foundEvent);

	}

	/**
	 * Adds a new file to the observed folder (into a sub folder) and checks if
	 * watcher fires an "Added"-FileEvent
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testAddFileToEmbeddedFolder() throws IOException,
			InterruptedException {
		// Add new folder to observed folder
		File folder = new File(toWatch.getAbsolutePath() + "/testfolder");
		folder.mkdir();
		// Add new File to new folder
		File file = new File(folder.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		toDelete.add(file);
		toDelete.add(folder);
		// Wait for event
		boolean foundEvent = false;
		for (int i = 0; i < 10; i++) {
			for (FileEvent fe : eventList) {
				if (fe.getType().equals(Type.ADDED)) {
					File newFile = new File(fe.getObject().getPath());
					if (newFile.equals(file)) {
						foundEvent = true;
						break;
					}
				}
			}
			if (foundEvent) {
				break;
			}
			Thread.sleep(500);
		}
		Assert.assertTrue(foundEvent);

	}

	/**
	 * Adds a new file to the observed folder, waits for a second, modifies the
	 * file and checks if watcher fires a "Modified"-FileEvent
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testFileModification() throws IOException, InterruptedException {
		// Add new file to observed folder
		File file = new File(toWatch.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		toDelete.add(file);
		Thread.sleep(1000);
		// Modify File
		FileWriter fw = new FileWriter(file);
		fw.append('a');
		fw.flush();
		fw.close();

		// Wait for event
		boolean foundEvent = false;
		for (int i = 0; i < 10; i++) {
			for (FileEvent fe : eventList) {
				if (fe.getType().equals(Type.MODIFIED)) {
					File newFile = new File(fe.getObject().getPath());
					if (newFile.equals(file)) {
						foundEvent = true;
						break;
					}
				}
			}
			if (foundEvent) {
				break;
			}
			Thread.sleep(500);
		}
		Assert.assertTrue(foundEvent);
	}

	/**
	 * Adds a new file to the observed folder, deletes it and checks if watcher
	 * fires a "Deleted"-FileEvent
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testFileDeletion() throws IOException, InterruptedException {
		// Add new file to observed folder
		File file = new File(toWatch.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		Thread.sleep(1000);
		// Delete File
		file.delete();
		// Wait for event
		boolean foundEvent = false;
		for (int i = 0; i < 10; i++) {
			for (FileEvent fe : eventList) {
				if (fe.getType().equals(Type.DELETED)) {
					File newFile = new File(fe.getObject().getPath());
					if (newFile.equals(file)) {
						foundEvent = true;
						break;
					}
				}
			}
			if (foundEvent) {
				break;
			}
			Thread.sleep(500);
		}
		Assert.assertTrue(foundEvent);
	}

	/**
	 * Adds a new file to the observed folder, waits a second, closes the
	 * PersistentWatcher, opens it again, checks if added file can be found in
	 * known files.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws CorruptDBException
	 * @throws URISyntaxException
	 */
	@Test
	public void testLoadDatabase() throws IOException, InterruptedException,
			CorruptDBException, URISyntaxException {
		// Add new file to observed folder
		File file = new File(toWatch.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		toDelete.add(file);

		// Shutdown watcher
		Thread.sleep(5000);
		watcher.shutdown(20000);

		// Start watcher again
		watcher = new PersistentWatcher(toWatch, dbFile, filter,
				new FileEventListener() {
					@Override
					public void handleEvent(FileEvent event) {
						eventList.add(event);
						logger.info("Received event " + event);
					}
				});

		// Check if watcher know the added file

		final List<File> knownFiles = new ArrayList<File>(toDelete);
		System.out.println("KF Size" + knownFiles.size());
		boolean visitKnownFiles = watcher.visitKnownFiles(new FileVisitor() {
			@Override
			public boolean visit(PersistentFile file) {

				for (File knownFile : knownFiles) {
					if (knownFile.equals(new File(file.getPath()))) {
						return true;
					}
				}
				return false;
			}

		});
		Assert.assertTrue(visitKnownFiles);
	}

	private static Logger logger = Logger
			.getLogger(PersistentWatcherWithSetupTest.class);

	private FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			if (pathname.getAbsolutePath().endsWith(".testfile"))
				return true;
			return false;
		}
	};

	@Test
	public void testFolderDeletion() throws IOException, InterruptedException,
			CorruptDBException {

		// Add new folder to observed folder
		File folder = new File(toWatch.getAbsolutePath() + "/testfolder");
		folder.mkdir();
		// Add new File to new folder
		File file = new File(folder.getAbsolutePath() + "/testfile.testfile");
		file.createNewFile();
		toDelete.add(file);
		toDelete.add(folder);

		Thread.sleep(5000);

		watcher.shutdown(2000);

		boolean delete = file.delete();
		System.out.println("Delete: " + delete);
		boolean delete2 = folder.delete();
		System.out.println("Delete2: " + delete2);
		
		watcher = new PersistentWatcher(toWatch, dbFile, filter,
				new FileEventListener() {
					@Override
					public void handleEvent(FileEvent event) {
						eventList.add(event);
						logger.info("Received event " + event);
					}
				});
		// Wait for event
		boolean foundEvent = false;
		for (int i = 0; i < 10; i++) {
			for (FileEvent fe : eventList) {
				System.out.println(fe);
				if (fe.getType().equals(Type.DELETED)) {
					File newFile = new File(fe.getObject().getPath());
					System.out.println(newFile.getPath());
					System.out.println(file.getPath());
					if (newFile.equals(new File(file.getPath()))) {
						foundEvent = true;
						break;
					}
				}
			}
			if (foundEvent) {
				break;
			}
			Thread.sleep(500);
		}
		Assert.assertTrue(foundEvent);
	}

}
