package de.uni_koeln.spinfo.wafs.mp3;

import java.net.URI;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public interface TrackVisitor {

	boolean visit(Track update);

	boolean updateRequired(URI path, long lastModified);

}
