package de.uni_koeln.wafs.datakeeper.tests.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import de.uni_koeln.spinfo.wafs.mp3.Mp3Writer;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public class DummyTrackGenerator {

	public static Logger logger = Logger.getLogger(DummyTrackGenerator.class);
	
	/**
	 * Helper method to simulate an existing mp3 archive. When called the first time for a given directory, it automatically generates 
	 * several files with different tags, and returns the list of generated {@link Track}s. If called again, it will return a cached
	 * version of the tracks (thus return very fast, as the creation process is skipped).
	 * @param args
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	public static List<Track> createDummyTracks(File mp3Dir) throws JAXBException, IOException {
		JAXBContext ctx = JAXBContext.newInstance(TrackList.class);
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		if(mp3Dir.exists()) {
			if(!mp3Dir.isDirectory()) {
				throw new RuntimeException("Parameter mp3Dir must either be a directory or not exist!");
			}
			File[] files = mp3Dir.listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".mp3");
				}
			});
			if(files.length > 0) {
				logger.info("Skipping dummy track creation, as there already are some mp3 files");
				TrackList list = (TrackList) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(new File(mp3Dir, "tracks.xml")), "UTF-8"));
				return list.getTracks();
			}
		}
		TrackList list = (TrackList) unmarshaller.unmarshal(new InputStreamReader(DummyTrackGenerator.class.getClassLoader().getResourceAsStream("tracks.xml"), "UTF-8"));
		InputStream input = DummyTrackGenerator.class.getClassLoader().getResourceAsStream("01 MIMELLO 1.mp3");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = input.read(buffer);
		while (len != -1) {
			output.write(buffer, 0, len);
		    len = input.read(buffer);
		}
		input.close();
		output.close();
		byte[] data = output.toByteArray();
		List<Track> tracks = list.getTracks();
		mp3Dir.mkdirs();
		for (Track track : tracks) {
			logger.info("Creating track " + track);
			File file = new File(mp3Dir, UUID.randomUUID().toString()+".mp3");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			track.setLocation(file.toURI().toString());
			Mp3Writer.update(track);
		}
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(list, new OutputStreamWriter(new FileOutputStream(new File(mp3Dir, "tracks.xml")), "UTF-8"));
		return tracks;
	}

}
