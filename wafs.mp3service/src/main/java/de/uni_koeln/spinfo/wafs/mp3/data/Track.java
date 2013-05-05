package de.uni_koeln.spinfo.wafs.mp3.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import de.uni_koeln.spinfo.wafs.mp3.Mp3Exception;

/**
 * 
 * @author sschwieb
 * 
 */
public class Track implements Serializable {

	private static final long serialVersionUID = -7441826398087095124L;

	private String artist, album, albumArtist, artistSort, albumSort, albumArtistSort,
			genre, title, titleSort, musicBrainzTrackID, musicBrainzDiscID, musicBrainzArtistID;

	private long lastModified;

	private int discNo, discTotal, track, trackTotal, year, length, bitRate, bpm;

	private URI location;

	private boolean isCompilation;

	@Override
	public String toString() {
		return "Track [artist=" + artist + ", album=" + album
				+ ", albumArtist=" + albumArtist + ", artistSort=" + artistSort
				+ ", albumSort=" + albumSort + ", albumArtistSort="
				+ albumArtistSort + ", genre=" + genre + ", title=" + title
				+ ", titleSort=" + titleSort + ", musicBrainzTrackID="
				+ musicBrainzTrackID + ", musicBrainzDiscID="
				+ musicBrainzDiscID + ", musicBrainzArtistID="
				+ musicBrainzArtistID + ", lastModified=" + lastModified
				+ ", discNo=" + discNo + ", discTotal=" + discTotal
				+ ", track=" + track + ", trackTotal=" + trackTotal + ", year="
				+ year + ", length=" + length + ", bitRate=" + bitRate
				+ ", bpm=" + bpm + ", location=" + location
				+ ", isCompilation=" + isCompilation + "]";
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public int getBPM() {
		return bpm;
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	
	public String getArtistSort() {
		return artistSort;
	}

	public void setArtistSort(String artistSort) {
		this.artistSort = artistSort;
	}

	public String getAlbumSort() {
		return albumSort;
	}

	public void setAlbumSort(String albumSort) {
		this.albumSort = albumSort;
	}

	public String getAlbumArtistSort() {
		return albumArtistSort;
	}

	public void setAlbumArtistSort(String albumArtistSort) {
		this.albumArtistSort = albumArtistSort;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleSort() {
		return titleSort;
	}

	public void setTitleSort(String titleSort) {
		this.titleSort = titleSort;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public int getDiscNo() {
		return discNo;
	}

	public void setDiscNo(int discNo) {
		this.discNo = discNo;
	}

	public int getDiscTotal() {
		return discTotal;
	}

	public void setDiscTotal(int discTotal) {
		this.discTotal = discTotal;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public int getTrackTotal() {
		return trackTotal;
	}

	public void setTrackTotal(int trackTotal) {
		this.trackTotal = trackTotal;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getLength() {
		return length;
	}
	
	public String getFormattedLength() {
		int seconds = length % 60;
		return length/60 + ":" + (seconds < 10 ? "0" : "") + seconds;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public URI getLocation() {
		return location;
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	public boolean isCompilation() {
		return isCompilation;
	}

	public void setCompilation(boolean isCompilation) {
		this.isCompilation = isCompilation;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getMusicBrainzTrackID() {
		return musicBrainzTrackID;
	}

	public void setMusicBrainzTrackID(String musicBrainzTrackID) {
		this.musicBrainzTrackID = musicBrainzTrackID;
	}

	public String getMusicBrainzDiscID() {
		return musicBrainzDiscID;
	}

	public void setMusicBrainzDiscID(String musicBrainzDiscID) {
		this.musicBrainzDiscID = musicBrainzDiscID;
	}

	public String getMusicBrainzArtistID() {
		return musicBrainzArtistID;
	}

	public void setMusicBrainzArtistID(String musicBrainzArtistID) {
		this.musicBrainzArtistID = musicBrainzArtistID;
	}

	public InputStream getImage() throws Mp3Exception {
		try {
			File file = new File(location);
			AudioFile readFile = AudioFileIO.read(file);
			if (readFile instanceof MP3File) {
				MP3File f = (MP3File) AudioFileIO.read(file);
				AbstractID3Tag tag = f.getID3v2Tag();
				if (tag == null)
					tag = f.getID3v1Tag();
				ByteArrayInputStream imageData = getArtWork(tag);
				return imageData;
			}
		} catch (Exception e) {
			throw new Mp3Exception(e);
		}
		return null;

	}

	private ByteArrayInputStream getArtWork(AbstractID3Tag tag)
			throws Mp3Exception {
		try {
			List<Artwork> artworkList = null;
			if (tag instanceof ID3v1Tag) {
				artworkList = ((ID3v1Tag) tag).getArtworkList();
			}
			if (tag instanceof ID3v22Tag) {
				artworkList = ((ID3v22Tag) tag).getArtworkList();
			}
			if (tag instanceof ID3v23Tag) {
				artworkList = ((ID3v23Tag) tag).getArtworkList();
			}
			if (tag instanceof ID3v24Tag) {
				artworkList = ((ID3v24Tag) tag).getArtworkList();
			}
			for (Artwork artwork : artworkList) {
				if (artwork == null)
					continue;
				byte[] bytes = artwork.getBinaryData();
				if (bytes == null)
					continue;
				return new ByteArrayInputStream(bytes);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}


}
