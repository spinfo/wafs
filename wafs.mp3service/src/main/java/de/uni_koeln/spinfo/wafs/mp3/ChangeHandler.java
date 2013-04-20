package de.uni_koeln.spinfo.wafs.mp3;

import java.io.File;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

/**
 * This interface can be used to get informed about file-based
 * events related to a {@link Track}. 
 * 
 * @author sschwieb
 * 
 * @see Mp3DirWatcher#Mp3DirWatcher(ChangeHandler, java.io.FileFilter, File)
 *
 */
public interface ChangeHandler {

	/**
	 * Called when a track must be updated.
	 * @param track the {@link Track} to update
	 */
	void update(Track track);

	/**
	 * Called when the service detects that a file has been deleted.
	 * @param file the deleted {@link File}
	 */
	void deleted(File file);

	/**
	 * Called when a new track has been added.
	 * @param track the new {@link Track}.
	 */
	void add(Track track);

}
