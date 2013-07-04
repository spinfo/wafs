package de.uni_koeln.spinfo.wafs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.uni_koeln.spinfo.wafs.mp3.CoverDB;
import de.uni_koeln.spinfo.wafs.mp3.NoImageAvailableException;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.spinfo.wafs.trackdb.TrackDB;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;
import de.uni_koeln.wafs.datakeeper.tests.util.DummyTrackGenerator;


@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WAFSInitializer {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private TrackDB trackDB;
	
	private CoverDB coverDB;
	
	/**
	 * @throws IOException 
	 * @see PostConstruct
	 */
	@PostConstruct
	public void startupCompleted() throws IOException {
		logger.info("Preparing WAFS Music Player...");
		File configFile = new File("wafs_config/wafs.properties");
		Properties properties = new Properties();
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
		properties.load(input);
		File watchFolder = new File(properties.getProperty("mp3.dir"));
		if(!watchFolder.exists()) {
			try {
				DummyTrackGenerator.createDummyTracks(watchFolder);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Loaded configuration: " + properties);
		input.close();
		logger.info("Preparing TrackDB...");
		trackDB = new TrackDB(new File(properties.getProperty("lucene.dir")), new File(properties.getProperty("db.dir")), Integer.parseInt(properties.getProperty("commit.interval")));
		trackDB.addWatchFolder(watchFolder);
		coverDB = new CoverDB(new File(properties.getProperty("cover.dir")));
		logger.info("WAFS Music Player initialized.");
	}

	public Result search(WAFSQuery query) throws IOException {
		return trackDB.search(query);
	}

	public InputStream getCover(Track track) throws NoImageAvailableException {
		return coverDB.getCover(track);
	}

	
	
}
