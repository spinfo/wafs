package de.uni_koeln.spinfo.wafs.mp3;

import java.net.URI;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

/**
 * A visitor which can be used to visit all known files managed by a {@link Mp3DirWatcher}.
 * @author sschwieb
 * @see Mp3DirWatcher#visitKnownTracks(TrackVisitor, boolean)
 *
 */
public interface TrackVisitor {

	/**
	 * This method will be called if {@link TrackVisitor#updateRequired(URI, long)} returns
	 * <code>true</code> for the {@link Track} with the respective URI.
	 * @param update the track to update
	 * @return <code>false</code> to tell the {@link Mp3DirWatcher} to stop visiting
	 * tracks, otherwise <code>true</code>. 
	 */
	boolean visit(Track update);

	/**
	 * Must return <code>true</code> if the {@link Track} defined by <code>path</code>
	 * requires an update (for instance, if it is not yet stored in a database, or if
	 * the stored information is outdated), and must return <code>false</code> otherwise.
	 * If it returns <code>true</code>, {@link TrackVisitor#visit(Track)} will be
	 * called afterwards.
	 * <br/>
	 * Please note that generating a {@link Track} from an {@link URI} is an expensive operation,
	 * which should be avoided.
	 * 
	 * @param path the absolute path of a track
	 * @param lastModified the last modification time stamp of the track, in milli seconds
	 * @return
	 */
	boolean updateRequired(URI path, long lastModified);

}
