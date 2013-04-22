package de.uni_koeln.wafs.datakeeper.tests;

import junit.framework.Assert;

import org.junit.Test;

import de.uni_koeln.wafs.datakeeper.util.TrackField;

public class FieldTest {
	
	@Test
	public void stringToLowerCase() {
		String string = TrackField.ALBUM.toString();
		Assert.assertEquals("album", string);
		string = TrackField.ARTIST.toString();
		Assert.assertFalse(string.equals("ARTIST"));
	}

}
