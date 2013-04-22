package de.uni_koeln.wafs.datakeeper.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static final String LUCENE_DIR = "lucene.dir";
	private Properties properties;
	private static Configuration instance;

	private Configuration() throws IOException {
		properties = new Properties();
		FileInputStream input = null;
		try {
			properties.load(new FileInputStream("config/wafs.properties"));
		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	public synchronized static Configuration getInstance() {
		if (instance == null) {
			try {
				instance = new Configuration();
			} catch (IOException e) {
				throw new RuntimeException(
						"Failed to initialize configuration", e);
			}
		}
		return instance;
	}

	public String getLuceneDir() {
		return properties.getProperty(LUCENE_DIR);
	}

	public void setLuceneDir(String dir) {
		properties.put(LUCENE_DIR, dir);
	}

}
