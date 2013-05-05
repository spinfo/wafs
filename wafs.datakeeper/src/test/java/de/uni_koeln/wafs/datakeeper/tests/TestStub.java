package de.uni_koeln.wafs.datakeeper.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.lucene.Index;
import de.uni_koeln.wafs.datakeeper.tests.util.DummyTrackGenerator;

public class TestStub {
	
	private static final File mp3Dir = new File("target/mp3s");
	private static final File luceneDir = new File("target/lucene");
	private static Logger logger = Logger.getLogger(TestStub.class);
	
	@BeforeClass
	public static void initialize() throws JAXBException, IOException {
		List<Track> tracks = DummyTrackGenerator.createDummyTracks(mp3Dir);
		Index index = new Index(luceneDir);
		int indexed = 0;
		for (Track track : tracks) {
			if(index.findTrack(track.getLocation()) == null) {
				index.add(track);
				indexed++;
			}
		}
		index.commit();
		logger.info("Indexed " + indexed + " new files, index now contains " + index.getSize() + " tracks.");
	}

	@Test
	public void test() {
		// TODO: Write some tests!
	}
	
}
