package de.uni_koeln.wafs.datakeeper.tests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.lucene.Index;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;
import de.uni_koeln.wafs.datakeeper.util.Configuration;
import de.uni_koeln.wafs.datakeeper.util.TrackField;

public class IndexTest {

	@Test
	public void search() throws IOException {
		Index index = new Index(new File(Configuration.getInstance()
				.getLuceneDir()));
		WAFSQuery q = new WAFSQuery();
		q.setCurrentPage(0);
		q.setPageSize(50);
		Map<String, String> values = new HashMap<String, String>();
		values.put(TrackField.ARTIST.toString(), "Bushido");
		values.put(TrackField.GENRE.toString(), "Deutscher Rap");
		q.setSortAscending(Arrays.asList(false));
		q.setSortOrder(Arrays.asList("bitrate"));
		q.setValues(values);
		q.setExact(false);
		q.setOr(false);
		index.commit();
		Result result = index.search(q);
		Assert.assertNotNull(result);
		List<Track> entries = result.getEntries();
		System.out.println(entries);
	}

}
