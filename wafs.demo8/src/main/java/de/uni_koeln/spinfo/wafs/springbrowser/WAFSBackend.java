package de.uni_koeln.spinfo.wafs.springbrowser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.uni_koeln.spinfo.wafs.trackdb.TrackDB;


@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WAFSBackend {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private TrackDB trackDB;
	
	/**
	 * @throws IOException 
	 * @see PostConstruct
	 */
	@PostConstruct
	public void startupCompleted() throws IOException {
		logger.info("Preparing WAFS Music Player...");
		Properties properties = new Properties();
		File configFile = new File("wafs_config/wafs.properties");
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
		properties.load(input);
		logger.info("Loaded configuration: " + properties);
		input.close();
		logger.info("Preparing TrackDB...");
		trackDB = new TrackDB(new File(properties.getProperty("lucene.dir")), new File(properties.getProperty("db.dir")), Integer.parseInt(properties.getProperty("commit.interval")));
		trackDB.addWatchFolder(new File(properties.getProperty("mp3.dir")));
		logger.info("WAFS Music Player initialized.");
	}

	public TrackDB getTrackDB() {
		return trackDB;
	}

	
	
}
