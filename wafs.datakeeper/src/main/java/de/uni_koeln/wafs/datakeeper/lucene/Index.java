package de.uni_koeln.wafs.datakeeper.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.MergePolicy.OneMerge;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;
import de.uni_koeln.wafs.datakeeper.util.FieldHelper;
import de.uni_koeln.wafs.datakeeper.util.TrackField;

public class Index {

	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
	private Writer writer;
	private Reader reader;
	private Logger logger = Logger.getLogger(getClass());
	private File luceneDir;
	private RAMDirectory writeDir;
	private RAMDirectory readDir;

	public Index(final File luceneDir) throws IOException {
		if (!luceneDir.exists()) {
			luceneDir.mkdirs();
		}
		this.luceneDir = luceneDir;
		init();
	}
	
	public Result search(final WAFSQuery q, String distinctField) throws IOException {
		if (reader == null)
			return new Result(new ArrayList<Track>(), 0, 0);
		int pageSize = q.getPageSize();
		Query query = createQuery(q);
		if (logger.isDebugEnabled()) {
			logger.debug("Query: " + query);
		}
		int currentPage = q.getCurrentPage();
		int max = pageSize * (currentPage + 1);
		List<Boolean> sortAscending = q.getSortAscending();
		List<String> sortOrder = q.getSortOrder();
		Sort sort = new Sort();
		if (sortAscending != null && sortAscending.size() > 0) {
			SortField[] sortFields = new SortField[sortAscending.size()];
			for (int i = 0; i < sortFields.length; i++) {
				String field = sortOrder.get(i);
				SortField sf = new SortField(field,
						FieldHelper.getSortTypeFor(field), sortAscending.get(i));
				sortFields[i] = sf;
			}
			sort = new Sort(sortFields);
		}
		DuplicateFilter filter = null;
		if(distinctField != null) {
			filter = new DuplicateFilter(distinctField);
		}
		TopDocs topDocs = reader.getIndexSearcher().search(query, filter, max, sort);
		if (logger.isDebugEnabled()) {
			logger.debug("Query: " + query + " returned " + topDocs.totalHits
					+ " hits.");
		}
		return toResult(topDocs, pageSize * currentPage, pageSize);
	}

	public Result search(final WAFSQuery q) throws IOException {
		return search(q, null);
	}
	
	public Result search(String field, String value, int startIndex, int length) throws IOException {
		DuplicateFilter filter = new DuplicateFilter(field);
		StandardQueryParser parser = new StandardQueryParser(analyzer);
		try {
			Query query = parser.parse(value, field);
			TopDocs topDocs = reader.getIndexSearcher().search(query, filter, (startIndex+1)*length);
			if (logger.isDebugEnabled()) {
				logger.debug("Query: " + query + " returned " + topDocs.totalHits
						+ " hits.");
			}
			return toResult(topDocs, startIndex, length);
		} catch (QueryNodeException e) {
			throw new IOException("Failed to parse query", e);
		}
	}

	private Query createQuery(WAFSQuery q) {
		Map<TrackField, String> values = q.getValues();
		List<TrackField> fields = new ArrayList<TrackField>(values.keySet());
		boolean exact = q.isExact();
		boolean or = q.isOr();
		StringBuilder sb = new StringBuilder();
		StandardQueryParser parser = new StandardQueryParser(analyzer);
		parser.setAllowLeadingWildcard(false);

		for (int i = 0; i < fields.size(); i++) {
			TrackField field = fields.get(i);
			String value = values.get(field);

			if (exact && FieldHelper.isString(field.toString())) {
				sb.append(field + "_exact:\"" + value + "\"");
			} else {
				sb.append(field + ":\"" + value + "\"");
			}
			if (i < fields.size() - 1) {
				if (or)
					sb.append(" OR ");
				else {
					sb.append(" AND ");
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Created Query String: " + sb);
		}
		try {
			return parser.parse(sb.toString(), TrackField.ARTIST.toString());
		} catch (QueryNodeException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Result toResult(TopDocs docs, int startIndex, int pageSize)
			throws IOException {
		List<Track> tracks = new ArrayList<Track>();
		final ScoreDoc[] scoreDocs = docs.scoreDocs;
		for (int i = startIndex; i < scoreDocs.length
				&& i < startIndex + pageSize; i++) {
			Document doc = reader.getIndexSearcher().doc(scoreDocs[i].doc);
			Track t = toTrack(doc);
			tracks.add(t);
		}
		return new Result(tracks, docs.totalHits, pageSize);
	}

	private Track toTrack(Document doc) {
		Track t = new Track();

		t.setAlbum(doc.get(TrackField.ALBUM.toString()));
		t.setAlbumArtistSort(doc.get(TrackField.ALBUM_ARTIST_SORT.toString()));
		t.setAlbumSort(doc.get(TrackField.ALBUM_SORT.toString()));
		t.setArtist(doc.get(TrackField.ARTIST.toString()));
		t.setArtistSort(doc.get(TrackField.ARTIST_SORT.toString()));
		t.setGenre(doc.get(TrackField.GENRE.toString()));
		t.setTitle(doc.get(TrackField.TITLE.toString()));
		t.setTitleSort(doc.get(TrackField.TITLE_SORT.toString()));

		String track = doc.get(TrackField.TRACK.toString());
		if (track != null)
			t.setTrack(Integer.parseInt(track));
		String trackTotal = doc.get(TrackField.TRACK_TOTAL.toString());
		if (trackTotal != null)
			t.setTrackTotal(Integer.parseInt(trackTotal));
		String discNo = doc.get(TrackField.DISC_NO.toString());
		if (discNo != null)
			t.setDiscNo(Integer.parseInt(discNo));
		String discTotal = doc.get(TrackField.DISC_TOTAL.toString());
		if (discTotal != null)
			t.setDiscTotal(Integer.parseInt(discTotal));
		String bitrate = doc.get(TrackField.BITRATE.toString());
		if (bitrate != null)
			t.setBitRate(Integer.parseInt(bitrate));
		String length = doc.get(TrackField.LENGTH.toString());
		if (length != null)
			t.setLength(Integer.parseInt(length));
		String year = doc.get(TrackField.YEAR.toString());
		if (year != null)
			t.setYear(Integer.parseInt(year));
		String lastModified = doc.get(TrackField.LAST_MODIFIED.toString());
		if (lastModified != null)
			t.setLastModified(Long.parseLong(lastModified));
		String uri = doc.get(TrackField.LOCATION + "_exact".toString());
		if (uri != null) {
			try {
				t.setLocation(new URI(uri));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return t;
	}

	public void update(Set<Track> tracks) throws IOException {
		if (tracks == null || tracks.size() <= 0)
			return;
		URI[] toRemove = new URI[tracks.size()];
		tracks.toArray(toRemove);
		remove(toRemove);
		for (Track t : tracks) {
			Document doc = createDoc(t);
			writer.getIndexWriter().updateDocument(
					new Term(TrackField.LOCATION.toString(), t.getLocation()
							.toString().toString()), doc);
		}
		writer.getIndexWriter().commit();
	}

	private Document createDoc(Track t) {
		Document doc = new Document();
		addField(doc, TrackField.ALBUM, t.getAlbum());
		addField(doc, TrackField.ALBUM_ARTIST_SORT, t.getAlbumArtistSort());
		addField(doc, TrackField.ALBUM_SORT, t.getAlbumSort());
		addField(doc, TrackField.ARTIST, t.getArtist());
		addField(doc, TrackField.ARTIST_SORT, t.getArtistSort());
		addField(doc, TrackField.GENRE, t.getGenre());
		addField(doc, TrackField.TITLE, t.getTitle());
		addField(doc, TrackField.TITLE_SORT, t.getTitleSort());
		addField(doc, TrackField.YEAR, t.getYear());
		addField(doc, TrackField.BITRATE, t.getBitRate());
		addField(doc, TrackField.DISC_NO, t.getDiscNo());
		addField(doc, TrackField.DISC_TOTAL, t.getDiscTotal());
		addField(doc, TrackField.LAST_MODIFIED, t.getLastModified());
		addField(doc, TrackField.LENGTH, t.getLength());
		addField(doc, TrackField.LOCATION, t.getLocation().toString());
		addField(doc, TrackField.TRACK, t.getTrack());
		addField(doc, TrackField.TRACK_TOTAL, t.getTrackTotal());
		return doc;
	}

	private void addField(Document doc, TrackField field, Object value) {
		if (value == null)
			return;
		List<Field> fields = FieldHelper.populateFields(field, value);
		for (Field f : fields) {
			doc.add(f);
		}
	}

	public Track findTrack(URI path) throws IOException {
		Term term = new Term(TrackField.LOCATION.toString() + "_exact",
				path.toString());
		if (reader == null)
			return null;
		TopDocs result = reader.getIndexSearcher().search(new TermQuery(term),
				1);
		if (result.totalHits == 0)
			return null;
		ScoreDoc scoreDoc = result.scoreDocs[0];
		return toTrack(reader.getIndexSearcher().doc(scoreDoc.doc));
	}

	public void remove(final URI... locations) throws IOException {
		Term[] terms = new Term[locations.length];
		for (int i = 0; i < locations.length; i++) {
			terms[i] = new Term(TrackField.LOCATION.toString() + "_exact",
					locations[i].toString());
		}
		writer.getIndexWriter().deleteDocuments(terms);
	}

	public void dropIndex() throws IOException {
		writer.getIndexWriter().deleteAll();
		writer.getIndexWriter().commit();
	}

	private void init() throws IOException {
		logger.info("Initializing index...");

		File[] indices = luceneDir.listFiles();
		Arrays.sort(indices, new Comparator<File>() {

			public int compare(File o1, File o2) {
				return Long.compare(o2.lastModified(), o1.lastModified());
			}
		});
		for (File indexDir : indices) {
			if (indexDir.isDirectory()) {
				logger.info("Checking dir " + indexDir);
				try {
					File oldLock = new File(indexDir, "write.lock");
					if (oldLock.exists()) {
						oldLock.delete();
					}
					Directory directory = new NIOFSDirectory(indexDir);
					writeDir = new RAMDirectory(directory, IOContext.DEFAULT);
					directory.close();
					this.writer = new Writer(writeDir);
					readDir = new RAMDirectory(writeDir, IOContext.DEFAULT);
					this.reader = new Reader(readDir);
					logger.info("Existing Index loaded from directory "
							+ indexDir);
					break;
				} catch (Exception e) {
					logger.warn("Failed to load index from " + indexDir
							+ ", continue...");
				}
			}
		}
		if (writeDir == null) {
			logger.warn("No index found, creating new...");
			writeDir = new RAMDirectory();
			this.writer = new Writer(writeDir);
		}
		logger.info("Index initialized.");
	}

	public void add(Track item) throws IOException {
		addTrackToIndex(item, writer.getIndexWriter());
	}

	private void addTrackToIndex(final Track t, IndexWriter writer)
			throws IOException {
		Document doc = new Document();
		addFields(doc, TrackField.ALBUM, t.getAlbum());
		addFields(doc, TrackField.ALBUM_ARTIST, t.getAlbumArtist());
		addFields(doc, TrackField.ALBUM_ARTIST_SORT, t.getAlbumArtistSort());
		addFields(doc, TrackField.ALBUM_SORT, t.getAlbumSort());
		addFields(doc, TrackField.ARTIST, t.getArtist());
		addFields(doc, TrackField.ARTIST_SORT, t.getArtistSort());
		addFields(doc, TrackField.GENRE, t.getGenre());
		addFields(doc, TrackField.TITLE, t.getTitle());
		addFields(doc, TrackField.TITLE_SORT, t.getTitleSort());
		addFields(doc, TrackField.YEAR, t.getYear());
		addFields(doc, TrackField.BITRATE, t.getBitRate());
		addFields(doc, TrackField.DISC_NO, t.getDiscNo());
		addFields(doc, TrackField.DISC_TOTAL, t.getDiscTotal());
		addFields(doc, TrackField.LAST_MODIFIED, t.getLastModified());
		addFields(doc, TrackField.LENGTH, t.getLength());
		addFields(doc, TrackField.LOCATION, t.getLocation().toString());
		addFields(doc, TrackField.TRACK, t.getTrack());
		addFields(doc, TrackField.TRACK_TOTAL, t.getTrackTotal());
		writer.addDocument(doc);
	}

	private void addFields(Document doc, TrackField field, Object value) {
		if (value == null)
			return;
		List<Field> fields = FieldHelper.populateFields(field, value);
		for (Field f : fields) {
			doc.add(f);
		}
	}

	public void commit() throws IOException {
		// Step 1: Commit all changes
		writer.getIndexWriter().forceMerge(1);
		writer.getIndexWriter().waitForMerges();
		writer.getIndexWriter().commit();
		// Step 2: Create a new file directory and copy the
		// current index
		File newDir = new File(luceneDir, System.currentTimeMillis() + "");
		NIOFSDirectory fileDir = new NIOFSDirectory(newDir);
		for (String file : writeDir.listAll()) {
			writeDir.copy(fileDir, file, file, IOContext.DEFAULT);
		}
		// Step 3: Create a new ram directory for searches
		RAMDirectory readDir = new RAMDirectory(writeDir, IOContext.DEFAULT);
		fileDir.close();
		// Step 4: Create a new reader for the new directory
		if (reader == null) {
			reader = new Reader(readDir);
		} else {
			Reader oldReader = reader;
			reader = new Reader(readDir);
			oldReader.close();
			if (this.readDir != null) {
				this.readDir.close();
			}
			this.readDir = readDir;
		}
		// Step 5: Delete all outdated index directories
		File[] oldDirs = luceneDir.listFiles();
		for (File old : oldDirs) {
			if (old.getName().equals(newDir.getName())) {
				continue;
			}
			deleteDir(old);
		}
	}

	private void deleteDir(File toDelete) {
		if (toDelete.isDirectory()) {
			File[] files = toDelete.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDir(file);
				}
				file.delete();
			}
		}
		toDelete.delete();
	}

	public int getSize() {
		if (reader == null)
			return 0;
		return reader.getIndexSearcher().getIndexReader().numDocs();
	}

}
