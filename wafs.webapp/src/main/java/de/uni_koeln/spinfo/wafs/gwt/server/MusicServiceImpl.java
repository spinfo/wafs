package de.uni_koeln.spinfo.wafs.gwt.server;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uni_koeln.spinfo.wafs.WAFSInitializer;
import de.uni_koeln.spinfo.wafs.gwt.shared.MusicService;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;

@Service("musicService")
public class MusicServiceImpl implements MusicService {
	
	@Autowired
	WAFSInitializer dbManager;
	

	private Logger logger = Logger.getLogger(getClass());
	
	public String sayHello() {
		logger.info("Hello!");
		return "Hello!";
	}
	
	public Result query(WAFSQuery query) {
		try {
			return dbManager.search(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
