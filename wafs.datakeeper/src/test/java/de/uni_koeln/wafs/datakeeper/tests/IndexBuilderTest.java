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
			track1.setAlbum("Electro Ghetto");
			track1.setYear(2004);
			track1.setArtist("Bushido");
			track1.setTitle("Knast oder Ruhm");
			track1.setGenre("Hiphop / Deutscher Rap");
			track1.setLocation(new URI("http://de.wikipedia.org/wiki/Electro_Ghetto"));
			track1.setBitRate(1234);
			track1.setLastModified(new Date().getTime());
			track1.setLength(1234);
			data.add(track1);

			Track track2 = new Track();
			track2.setAlbum("Vom Bordstein bis zur Skyline");
			track2.setYear(2003);
			track2.setArtist("Bushido");
			track2.setTitle("Dreckstück");
			track2.setGenre("Deutscher Rap / Gangsta-Rap");
			track2.setLocation(new URI("http://de.wikipedia.org/wiki/Vom_Bordstein_bis_zur_Skyline"));
			track2.setBitRate(3456);
			track2.setLastModified(new Date().getTime());
			track2.setLength(3456);
			data.add(track2);

			Track track3 = new Track();
			track3.setAlbum("Blockplatin");
			track3.setYear(20013);
			track3.setArtist("Haftbefehl");
			track3.setTitle("Chabos wissen wer der Babo ist");
			track3.setGenre("Deutscher Hip-Hop");
			track3.setLocation(new URI("http://de.wikipedia.org/wiki/Blockplatin"));
			track3.setBitRate(5678);
			track3.setLastModified(new Date().getTime());
			track3.setLength(5678);
			data.add(track3);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
