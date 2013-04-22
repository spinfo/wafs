package de.uni_koeln.wafs.datakeeper.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

class Writer {

	private IndexWriter writer;
	private Directory directory;

	public Writer(final Directory directory) throws IOException {
		this.directory = directory;
		init();
	}

	private void init() throws IOException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(
				Version.LUCENE_42, new StandardAnalyzer(Version.LUCENE_42));
		writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		writerConfig.setRAMBufferSizeMB(128.0);
		this.writer = new IndexWriter(directory, writerConfig);
	}
	
	public IndexWriter getIndexWriter() {
		return this.writer;
	}

}
