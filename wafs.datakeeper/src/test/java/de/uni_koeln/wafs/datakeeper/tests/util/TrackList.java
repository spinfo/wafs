package de.uni_koeln.wafs.datakeeper.tests.util;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

@XmlRootElement
public class TrackList {
	
	private List<Track> tracks;

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

}
