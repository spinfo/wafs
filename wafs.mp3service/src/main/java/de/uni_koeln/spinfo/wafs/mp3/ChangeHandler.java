package de.uni_koeln.spinfo.wafs.mp3;

import java.io.File;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

/**
 * 
 * @author sschwieb
 *
 */
public interface ChangeHandler {

	/**
	 * Called to update the given track.
	 * @param item
	 */
	void update(Track item);

	/**
	 * Called when the service detects that a file has been deleted.
	 * @param file
	 */
	void deleted(File file);

	void add(Track track);

}
