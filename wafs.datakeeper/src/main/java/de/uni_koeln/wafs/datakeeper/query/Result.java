package de.uni_koeln.wafs.datakeeper.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.uni_koeln.spinfo.wafs.mp3.data.Track;

public class Result implements Serializable {

	private static final long serialVersionUID = -1728137145193646942L;
	private List<Track> entries;
	private int maxEntries;
	private int pageSize;
	

	public Result(List<Track> entries, int maxEntries, int pageSize) {
		this.entries = entries;
		this.maxEntries = maxEntries;
		this.pageSize = pageSize;
	}
	
	public Result() {
		this(new ArrayList<Track>(), 0, 0);
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<Track> getEntries() {
		return entries;
	}

	public void setEntries(List<Track> entries) {
		this.entries = entries;
	}

	public int getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}


}
