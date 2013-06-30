package de.uni_koeln.wafs.datakeeper.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.query.TrackField;
import de.uni_koeln.wafs.datakeeper.util.FieldHelper;
import de.uni_koeln.wafs.datakeeper.util.Logging;

public class IndexBuilder {

	private static IndexBuilder INSTANCE = new IndexBuilder();
	private NIOFSDirectory indexDirectory;
	private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
	private Logger LOGGER = Logging.getInstance().getLogger(IndexBuilder.class);

	private IndexBuilder() {
	}

	public static IndexBuilder getInstance() {
		return INSTANCE;
	}

	public boolean buildIndex(final List<Track> data, final File directory)
			throws IOException {
		if (!indexExists()) {
			LOGGER.info("Building Lucene index...");
			setDirectory(directory);
			build(data);
			return true;
		} else {
			LOGGER.info("Lucene directory is already set.");
			return false;
		}
	}

	private void setDirectory(final File directory) throws IOException {
		this.indexDirectory = new NIOFSDirectory(directory);
	}

	private boolean indexExists() {
		if (indexDirectory != null) {
			return indexDirectory.getDirectory().exists();
		} else
			return false;
	}

	private void build(final List<Track> data) {
		try {
			Date begin = new Date();
			IndexWriter writer = initIndexWriter();
			for (Track track : data) {
				addTrackToIndex(track, writer);
			}
			writer.close();
			LOGGER.info("Indexing prepared: "
					+ (new Date().getTime() - begin.getTime()) + " ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addTrackToIndex(final Track t, IndexWriter writer) {
		try {
			Document doc = new Document();
			addField(doc, TrackField.ALBUM, t.getAlbum());
			addField(doc, TrackField.ALBUM_ARTIST, t.getAlbumArtist());
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
			writer.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addField(Document doc, TrackField field, Object value) {
		if (value == null)
			return;
		List<Field> fields = FieldHelper.populateFields(field, value);
		for (Field f : fields) {
			doc.add(f);
		}
	}

	private IndexWriter initIndexWriter() throws IOException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(
				Version.LUCENE_42, analyzer);
		if (!indexExists()) {
			writerConfig.setOpenMode(OpenMode.CREATE);
		} else {
			writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		writerConfig.setRAMBufferSizeMB(512.0);
		return new IndexWriter(indexDirectory, writerConfig);
	}

}
