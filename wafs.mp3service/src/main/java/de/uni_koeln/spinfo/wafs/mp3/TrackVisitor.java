package de.uni_koeln.spinfo.wafs.mp3;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public interface TrackVisitor {

	boolean visit(Track update);

}
