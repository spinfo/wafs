package de.uni_koeln.wafs.datakeeper.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

	public static void main(String[] args) throws IOException {
		// Verzeichnis des Lucene-Indexes
		String indexDir = "demo/index";
		// In diesem Verzeichnis liegen die zu indexierenden Daten als .txt
		String data = "demo/texts";
		// Datei mit Stopwörter
		String stopWords = "demo/texts/stop words/stopwords.txt";

		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir, stopWords);
		try {
			int numIndexed = indexer.index(data);
			indexer.close();
			long end = System.currentTimeMillis();
			System.out.println("Indexing took " + (end - start) + " ms for "
					+ numIndexed + " files.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private IndexWriter writer;

	public Indexer(final String indexDir, final String stopWords)
			throws IOException {
		// Erzeuge einer Directory. Sie repräsentiert den Speicherort des
		// Indexes
		Directory dir = new NIOFSDirectory(new File(indexDir));
		Collection<String> c = getStopWords(stopWords);
		CharArraySet set = new CharArraySet(Version.LUCENE_42, c, true);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42, set);
		IndexWriterConfig writerConfig = new IndexWriterConfig(
				Version.LUCENE_42, analyzer);
		// Erzeuge den IndexWriter
		writer = new IndexWriter(dir, writerConfig);
	}

	private Collection<String> getStopWords(String stopWords) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				stopWords)));
		String line;
		Collection<String> c = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			String[] split = line.split(" ");
			for (String token : split) {
				token = token.replaceAll("[^\\w]", "");
				c.add(token);
			}
		}
		br.close();
		return c;
	}

	private int index(String dataDir) throws Exception {
		// Auflisten aller Dateien im übergebenen Verzeichnis
		File[] files = new File(dataDir).listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			// Filtern der zu indexierenden Dateien (nicht rekursiv)
			if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()
					&& acceptFile(f)) {
				indexFile(f);
			}
		}
		// Gibt die Anzahl der indexierten Dateien zurrück.
		return writer.numDocs();
	}

	private boolean acceptFile(File f) {
		// Gibt true zurrück, wenn Datei auf .txt endet
		return f.getName().endsWith(".txt");
	}

	private Document getDocument(File f) throws Exception {
		// Erzeugt ein neues Document-Objekt
		Document doc = new Document();
		// Erzeugt einen BufferedReader. Dieser ließt den Text zeilenweise
		// ein. Der StringBuilder fügt die Zeilen zu einem String zusammen.
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		// BufferedReader wird geschlossen
		br.close();
		// Dem oben erzeugten Document werden Felder übergeben. Der erste
		// Parameter des Feldes ist ein Schlüssel, der zweite der konkrete Wert.
		doc.add(new TextField("contents", sb.toString(), Store.YES));
		doc.add(new StringField("filename", f.getCanonicalPath(), Store.YES));
		return doc;
	}

	private void indexFile(File f) throws Exception {
		System.out.println("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		if (doc != null) {
			// Der IndexWriter fügt das Dokument zum Index
			writer.addDocument(doc);
		}
	}

	private void close() throws IOException {
		// Der IndexWriter wird geschlossen
		writer.close();
	}

}
