package de.uni_koeln.spinfo.wafs.gwt.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import de.uni_koeln.spinfo.wafs.gwt.client.widget.ResultWidget;
import de.uni_koeln.spinfo.wafs.gwt.shared.MusicService;
import de.uni_koeln.spinfo.wafs.gwt.shared.MusicServiceAsync;

public class Wafs implements EntryPoint {

	private Logger logger = Logger.getLogger(getClass().getName());

	private MusicServiceAsync service;

	private ResultWidget resultWidget;

	public void onModuleLoad() {
		logger.info("Loading module...");
		service = GWT.create(MusicService.class);
		resultWidget = new ResultWidget(service);
		RootPanel.get("results_panel").add(resultWidget);
	}

}
