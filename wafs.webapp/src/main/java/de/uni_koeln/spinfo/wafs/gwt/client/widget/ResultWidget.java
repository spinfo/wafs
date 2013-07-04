package de.uni_koeln.spinfo.wafs.gwt.client.widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Thumbnail;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.uni_koeln.spinfo.wafs.gwt.shared.MusicServiceAsync;
import de.uni_koeln.spinfo.wafs.mp3.data.Track;
import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.TrackField;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;

public class ResultWidget extends Composite {

	private static RWUiBinder uiBinder = GWT.create(RWUiBinder.class);

	interface RWUiBinder extends UiBinder<Widget, ResultWidget> {
	}

	private Logger logger = Logger.getLogger(getClass().getName());

	@UiField
	Form form;

	@UiField
	TextBox searchField;

	@UiField
	Button submitButton;

	@UiField
	VerticalPanel panel;

	private MusicServiceAsync service;

	public ResultWidget(MusicServiceAsync service) {
		initWidget(uiBinder.createAndBindUi(this));
		this.service = service;
		submitButton.setText("Suchen");
		addstyles();
	}

	private void addstyles() {
		form.getElement().addClassName("form-search");
		searchField.getElement().addClassName("input-medium search-query");
	}

	@UiHandler("searchField")
	void onKeyUp(KeyUpEvent e) {
		if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			search(searchField.getText());
		}
	}

	@UiHandler("submitButton")
	void onClick(ClickEvent e) {
		search(searchField.getText());
	}

	private void search(String input) {
		if (input != null) {
			clearPanel();
			WAFSQuery query = createQuery(input);
			service.query(query, new AsyncCallback<Result>() {

				public void onFailure(Throwable throwable) {
					logger.log(Level.WARNING, "Query failed", throwable);
				}

				public void onSuccess(Result result) {
					displayResults(result);
				}

			});
		}
	}

	private WAFSQuery createQuery(String input) {
		WAFSQuery query = new WAFSQuery();
		query.setValue(TrackField.ALBUM, input);
		query.setPageSize(10);
		logger.info("Querying for " + query);
		return query;
	}

	private void clearPanel() {
		panel.clear();
	}

	private void displayResults(Result result) {
		List<Track> entries = result.getEntries();
		Thumbnails tns = new Thumbnails();
		for (Track track : entries) {
			Thumbnail tn = new Thumbnail();
			tn.setSize(4);
			TrackDataWrapper tdw = new TrackDataWrapper();
			tdw.setAlbum(track.getAlbum());
			tdw.setTrackTitle(track.getTitle());
			tdw.setArtist(track.getArtist());
			tdw.setBitrate(track.getBitRate());
			tn.add(tdw);
			tns.add(tn);
		}
		panel.add(tns);
	}

}
