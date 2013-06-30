package de.uni_koeln.spinfo.wafs.mp3;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public class Mp3Writer {
	
	/**
	 * <strong>Currently untested - use with caution!</strong>
	 * @param track
	 * @throws IOException
	 * @throws  
	 */
	public static void update(Track track) throws IOException  {
		
		try {
			File file = new File(new URI(track.getLocation()));
			if(!file.exists()) throw new IOException("The track does not exist");
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			setIfNotNull(tag, FieldKey.ALBUM, track.getAlbum());
			setIfNotNull(tag, FieldKey.ALBUM_ARTIST, track.getAlbumArtist());
			setIfNotNull(tag, FieldKey.ALBUM_ARTIST_SORT, track.getAlbumArtistSort());
			setIfNotNull(tag, FieldKey.ALBUM_SORT, track.getAlbumSort());
			setIfNotNull(tag, FieldKey.ARTIST, track.getArtist());
			setIfNotNull(tag, FieldKey.BPM, track.getBPM()+"");
			setIfNotNull(tag, FieldKey.ARTIST_SORT, track.getArtistSort());
			setIfNotNull(tag, FieldKey.DISC_NO, track.getDiscNo()+"");
			setIfNotNull(tag, FieldKey.DISC_TOTAL, track.getDiscTotal()+"");
			setIfNotNull(tag, FieldKey.GENRE, track.getGenre());
			setIfNotNull(tag, FieldKey.IS_COMPILATION, track.isCompilation()+"");
			setIfNotNull(tag, FieldKey.TITLE, track.getTitle());
			setIfNotNull(tag, FieldKey.TITLE_SORT, track.getTitleSort());
			setIfNotNull(tag, FieldKey.TRACK, track.getTrack()+"");
			setIfNotNull(tag, FieldKey.TRACK_TOTAL, track.getTrackTotal()+"");
			setIfNotNull(tag, FieldKey.YEAR, track.getYear()+"");
			setIfNotNull(tag, FieldKey.MUSICBRAINZ_ARTISTID, track.getMusicBrainzArtistID());
			setIfNotNull(tag, FieldKey.MUSICBRAINZ_DISC_ID, track.getMusicBrainzDiscID());
			setIfNotNull(tag, FieldKey.MUSICBRAINZ_TRACK_ID, track.getMusicBrainzTrackID());
			f.commit();
		} catch (KeyNotFoundException
				| CannotReadException | TagException | ReadOnlyFileException
				| InvalidAudioFrameException | URISyntaxException | CannotWriteException e) {
			throw new IOException("Failed to update track", e);
		}

	}

	private static void setIfNotNull(Tag tag, FieldKey key, String value) throws KeyNotFoundException, FieldDataInvalidException {
		if(value != null) {
			tag.setField(key, value);
		}
	}

}
