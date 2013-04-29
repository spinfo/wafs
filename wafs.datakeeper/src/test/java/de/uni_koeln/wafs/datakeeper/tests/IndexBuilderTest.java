package de.uni_koeln.wafs.datakeeper.tests;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.lucene.IndexBuilder;
import de.uni_koeln.wafs.datakeeper.util.Configuration;

@Ignore("IndexBuilder is no longer required")
public class IndexBuilderTest {

	List<Track> data;

	@Test
	public void buildIndex() {
		IndexBuilder instance = IndexBuilder.getInstance();
		try {
			File dir = new File(Configuration.getInstance().getLuceneDir());
			boolean buildIndex = instance.buildIndex(data, dir);
			Assert.assertEquals(true, buildIndex);
			buildIndex = instance.buildIndex(data, dir);
			Assert.assertEquals(false, buildIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void createMocks() {
		try {
			data = new ArrayList<Track>();
			Track track1 = new Track();
			track1.setAlbum("London Calling");
			track1.setYear(1979);
			track1.setArtist("The Clash");
			track1.setTitle("London Calling");
			track1.setGenre("Punkrock");
			track1.setLocation(new URI("https://de.wikipedia.org/wiki/London_Calling"));
			track1.setBitRate(1234);
			track1.setLastModified(new Date().getTime());
			track1.setLength(1234);
			data.add(track1);

			data = new ArrayList<Track>();
			Track track2 = new Track();
			track2.setAlbum("London Calling");
			track2.setYear(1979);
			track2.setArtist("The Clash");
			track2.setTitle("Brand New Cadillac");
			track2.setGenre("Punkrock");
			track2.setLocation(new URI("https://de.wikipedia.org/wiki/London_Calling"));
			track2.setBitRate(1234);
			track2.setLastModified(new Date().getTime());
			track2.setLength(4567);
			data.add(track2);

			data = new ArrayList<Track>();
			Track track3 = new Track();
			track3.setAlbum("London Calling");
			track3.setYear(1979);
			track3.setArtist("The Clash");
			track3.setTitle("Jimmy Jazz");
			track3.setGenre("Punkrock");
			track3.setLocation(new URI("https://de.wikipedia.org/wiki/London_Calling"));
			track3.setBitRate(1234);
			track3.setLastModified(new Date().getTime());
			track3.setLength(6789);
			data.add(track3);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
