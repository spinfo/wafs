package de.uni_koeln.spinfo.wafs.gwt.client.widget;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TrackDataWrapper extends Composite {

	private static TrackDataWrapperUiBinder uiBinder = GWT
			.create(TrackDataWrapperUiBinder.class);

	interface TrackDataWrapperUiBinder extends
			UiBinder<Widget, TrackDataWrapper> {
	}
	
	@UiField
	Image image;

	@UiField
	SpanElement album;

	@UiField
	SpanElement artist;

	@UiField
	SpanElement tracktitle;

	@UiField
	SpanElement bitrate;

	public TrackDataWrapper() {
		initWidget(uiBinder.createAndBindUi(this));
		addStyles();
	}

	private void addStyles() {
		image.getElement().addClassName("img-polaroid");
	}

	public void setImage(String url) {
		this.image.setUrl(url);
	}
	
	public void setImage(ImageResource resource) {
		this.image.setResource(resource);
	}
	
	public void setAlbum(String album) {
		this.album.setInnerText(album);
	}

	public void setArtist(String artist) {
		this.artist.setInnerText(artist);
	}

	public void setTrackTitle(String tracktitle) {
		this.tracktitle.setInnerText(tracktitle);
	}

	public void setBitrate(int bitrate) {
		this.bitrate.setInnerText(String.valueOf(bitrate) + " kBits/s");
	}

}
