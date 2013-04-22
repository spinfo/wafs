package de.uni_koeln.wafs.datakeeper.util;

public enum TrackField {
	ARTIST, ALBUM, GENRE, TITLE, LAST_MODIFIED, YEAR, LENGTH, BITRATE, LOCATION, 
	ARTIST_SORT, ALBUM_SORT, ALBUM_ARTIST_SORT, TITLE_SORT, DISC_NO, DISC_TOTAL, 
	TRACK, TRACK_TOTAL, IS_COMPILATION;
	
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	
}
