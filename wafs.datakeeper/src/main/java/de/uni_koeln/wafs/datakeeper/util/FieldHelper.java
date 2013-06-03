package de.uni_koeln.wafs.datakeeper.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.SortField.Type;

public class FieldHelper {
	
	private static final Map<String, Type> fieldToType = new HashMap<String, Type>();
	private static final Map<String, TrackField> stringToField = new HashMap<String, TrackField>();
	
	static {
		fieldToType.put(TrackField.ALBUM.toString(), Type.STRING);
		fieldToType.put(TrackField.ALBUM_ARTIST_SORT.toString(), Type.STRING);
		fieldToType.put(TrackField.ALBUM_SORT.toString(), Type.STRING);
		fieldToType.put(TrackField.ARTIST.toString(), Type.STRING);
		fieldToType.put(TrackField.ARTIST_SORT.toString(), Type.STRING);
		fieldToType.put(TrackField.GENRE.toString(), Type.STRING);
		fieldToType.put(TrackField.LOCATION.toString(), Type.STRING);
		fieldToType.put(TrackField.TITLE.toString(), Type.STRING);
		fieldToType.put(TrackField.TITLE_SORT.toString(), Type.STRING);
		fieldToType.put(TrackField.BITRATE.toString(), Type.INT);
		fieldToType.put(TrackField.DISC_NO.toString(), Type.INT);
		fieldToType.put(TrackField.DISC_TOTAL.toString(), Type.INT);
		fieldToType.put(TrackField.LENGTH.toString(), Type.INT);
		fieldToType.put(TrackField.TRACK.toString(), Type.INT);
		fieldToType.put(TrackField.TRACK_TOTAL.toString(), Type.INT);
		fieldToType.put(TrackField.YEAR.toString(), Type.INT);
		fieldToType.put(TrackField.LAST_MODIFIED.toString(), Type.LONG);
	}
	
	static {
		stringToField.put(TrackField.ALBUM.toString(), TrackField.ALBUM);
		stringToField.put(TrackField.ALBUM_ARTIST_SORT.toString(), TrackField.ALBUM_ARTIST_SORT);
		stringToField.put(TrackField.ALBUM_SORT.toString(), TrackField.ALBUM_SORT);
		stringToField.put(TrackField.ARTIST.toString(), TrackField.ARTIST);
		stringToField.put(TrackField.ARTIST_SORT.toString(), TrackField.ARTIST_SORT);
		stringToField.put(TrackField.GENRE.toString(), TrackField.GENRE);
		stringToField.put(TrackField.LOCATION.toString(), TrackField.LOCATION);
		stringToField.put(TrackField.TITLE.toString(), TrackField.TITLE);
		stringToField.put(TrackField.TITLE_SORT.toString(), TrackField.TITLE_SORT);
		stringToField.put(TrackField.BITRATE.toString(), TrackField.BITRATE);
		stringToField.put(TrackField.DISC_NO.toString(), TrackField.DISC_NO);
		stringToField.put(TrackField.DISC_TOTAL.toString(), TrackField.DISC_TOTAL);
		stringToField.put(TrackField.LENGTH.toString(), TrackField.LENGTH);
		stringToField.put(TrackField.TRACK.toString(), TrackField.TRACK);
		stringToField.put(TrackField.TRACK_TOTAL.toString(), TrackField.TRACK_TOTAL);
		stringToField.put(TrackField.YEAR.toString(), TrackField.YEAR);
		stringToField.put(TrackField.LAST_MODIFIED.toString(), TrackField.LAST_MODIFIED);
	}
	
	public static Type getSortTypeFor(String field) {
		return fieldToType.get(field);
	}
	
	public static TrackField getFieldTypeFor(String field) {
		return stringToField.get(field);
	}

	public static List<Field> populateFields(TrackField field, Object value) {
		switch (field) {
		case ALBUM:
		case ALBUM_ARTIST:
		case ALBUM_ARTIST_SORT:
		case ALBUM_SORT:
		case ARTIST:
		case ARTIST_SORT:
		case GENRE:
		case TITLE:
		case TITLE_SORT:
			List<Field> stringFields = new ArrayList<Field>();
			stringFields.add(new TextField(field.toString(), (String) value, Field.Store.YES));
			stringFields.add(new StringField(field.toString()+"_exact", ((String) value).toLowerCase(), Field.Store.YES));
			return stringFields;
		case BPM:
		case BITRATE:
		case DISC_NO:
		case DISC_TOTAL:
		case LENGTH:
		case TRACK:
		case TRACK_TOTAL:
		case YEAR: 
			List<Field> intFields = new ArrayList<Field>();
			intFields.add(new IntField(field.toString(), (Integer) value, Field.Store.YES));
			return intFields;
		case LAST_MODIFIED:
			List<Field> longFields = new ArrayList<Field>();
			longFields.add(new LongField(field.toString(), (Long) value, Field.Store.YES));
			return longFields;
		case MUSIC_BRAINZ_ARTIST_ID:
		case MUSIC_BRAINZ_DISC_ID:
		case MUSIC_BRAINZ_TRACK_ID:
		case LOCATION:
			List<Field> fields = new ArrayList<Field>();
			fields.add(new StringField(field.toString()+"_exact", (String) value, Field.Store.YES));
			return fields;
		default:
			throw new RuntimeException("Unsupported field type: " + field);
		}
	}

	public static boolean isString(String field) {
		return fieldToType.get(field).equals(Type.STRING);
	}
	

}
