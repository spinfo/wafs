package de.uni_koeln.spinfo.wafs.fslistener;

import de.uni_koeln.spinfo.wafs.fslistener.data.PersistentFile;

/**
 * A visitor to visit all known files managed by a {@link PersistentWatcher}.
 * @see PersistentWatcher#visitKnownFiles(FileVisitor)
 * @author sschwieb
 *
 */
public interface FileVisitor {
	
	/**
	 * Called for each visited file
	 * @param file the visited file
	 * @return <code> true</code> to continue visiting, <code>false</code> to stop. 
	 */
	public boolean visit(PersistentFile file);

}
