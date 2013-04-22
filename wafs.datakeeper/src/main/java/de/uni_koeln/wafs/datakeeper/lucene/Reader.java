package de.uni_koeln.wafs.datakeeper.lucene;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

class Reader {

	private Directory directory;
	private DirectoryReader reader;
	private IndexSearcher searcher;

	public Reader(final Directory directory) throws IOException {
		this.directory = directory;
		init();
	}

	private void init() throws IOException {
		this.reader = DirectoryReader.open(directory);
		this.searcher = new IndexSearcher(reader);
	}

	public IndexSearcher getIndexSearcher() {
		return this.searcher;
	}

	public void close() throws IOException {
		this.reader.close();
	}

}
