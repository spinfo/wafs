package de.uni_koeln.wafs.datakeeper.tests.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.tests.util.DummyTrackGenerator;

public class TestStub {
	
	private static final File mp3Dir = new File("target/mp3s");
	//private static final File luceneDir = new File("target/lucene");
	//private static Logger logger = Logger.getLogger(TestStub.class);
	private static List<Track> tracks;
	
	@BeforeClass
	public static void initialize() throws JAXBException, IOException {
		tracks = DummyTrackGenerator.createDummyTracks(mp3Dir);
	}

	@Test
	public void testAddTracks() throws Exception {
		DemoIndex index = createDemoIndex();
		int size = index.getSize();
		Assert.assertEquals(tracks.size(), size);
	}

	private DemoIndex createDemoIndex() throws IOException {
		DemoIndex index = new DemoIndex();
		for (Track track : tracks) {
			index.add(track);
		}
		index.commit();
		return index;
	}
	
	@Test
	public void testQuery() throws Exception {
		DemoIndex index = createDemoIndex();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		QueryParser parser = new QueryParser(Version.LUCENE_42, "", analyzer);
		Query query = parser.parse("(album:Back to mine)");
		List<Track> result = index.query(query);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
		for (Track track : result) {
			System.out.println("Track: " + track);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
