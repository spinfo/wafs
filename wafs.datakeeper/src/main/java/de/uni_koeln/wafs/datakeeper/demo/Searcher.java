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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

	public static void main(String[] args) {
		// Verzeichnis des Lucene-Indexes
		String indexDir = "demo/index";
		
		// Suchbegriff
		String q = "Kampfhamster";
		
		// Stopwort... sollte nicht gefunden werden.
		// String q = "woran";
		
		// Datei mit Stopwörter. Diese sollte identisch mit der Liste sein, 
		// welche zum Indexieren verwendet wurde.
		String stopWords = "demo/texts/stop words/stopwords.txt";
		
		try {
			search(indexDir, q, stopWords);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void search(String indexDir, String q, String stopWords)
			throws IOException, ParseException {
		Directory dir = new SimpleFSDirectory(new File(indexDir));
		
		// Öffnet den Index (read-only-mode)
		DirectoryReader dirReader = DirectoryReader.open(dir);
		
		// Erzeuge einen IndexSearcher
		IndexSearcher is = new IndexSearcher(dirReader);
		
		// Der gleiche Analyzer wie aus Indexer
		Collection<String> c = getStopWords(stopWords);
		CharArraySet set = new CharArraySet(Version.LUCENE_42, c, true);
		// Der Analyzer wird verwendet, um den vom User eingegebenen Text in Terme (Term) zu überführen.
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42, set);
		
		// Überführt den vom User eigegebenen "Suchtext" in eine Lucene Query
		QueryParser parser = new QueryParser(Version.LUCENE_42, "contents", analyzer);
		Query query = parser.parse(q);
		System.out.println("QUERY: " + query);
		
		long start = System.currentTimeMillis();
		
		// IndexSearcher sucht nach der übergebenen Query und liefert die 10
		// besten Resultate
		TopDocs hits = is.search(query, 10);
		long end = System.currentTimeMillis();
		System.err.println("Took " + (end - start) + " ms to find "
				+ hits.totalHits + " document(s) that matched query '" + q
				+ "':");
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			
			// Mittels der DocID wird das entsprechende Dokument ausgelesen
			Document doc = is.doc(scoreDoc.doc);
			// alternativ...
			// Document doc = is.getIndexReader().document(scoreDoc.doc);
			
			// Filename wird ausgegeben  (key-value Prinzip)
			System.out.println(doc.get("filename"));
		}
		dirReader.close();
	}

	private static Collection<String> getStopWords(String stopWords)
			throws IOException {
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

}
