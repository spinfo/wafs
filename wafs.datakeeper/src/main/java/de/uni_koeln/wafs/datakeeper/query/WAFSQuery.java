package de.uni_koeln.wafs.datakeeper.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WAFSQuery implements Serializable {

	private static final long serialVersionUID = -6604687030676465634L;
	private boolean exact;
	private boolean or;
	private Map<TrackField, String> values = new HashMap<TrackField, String>();
	private List<String> sortOrder;
	private List<Boolean> sortAscending;
	private int pageSize;
	private int currentPage;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<String> getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(List<String> sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<Boolean> getSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(List<Boolean> sortAscending) {
		this.sortAscending = sortAscending;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isExact() {
		return this.exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public boolean isOr() {
		return this.or;
	}

	public void setOr(boolean or) {
		this.or = or;
	}

	public Map<TrackField, String> getValues() {
		return values;
	}

	public void setValues(Map<TrackField, String> values) {
		this.values = values;
	}
	
	public void setValue(TrackField field, String value) {
		if(value == null) {
			values.remove(field);
		} else {
			values.put(field, value);
		}
	}
	
	public void removeValue(TrackField field) {
		values.remove(field);
	}
	
	public void clearValues() {
		values.clear();
	}

	@Override
	public String toString() {
		return "WAFSQuery [exact=" + exact + ", or=" + or + ", values="
				+ values + ", sortOrder=" + sortOrder + ", sortAscending="
				+ sortAscending + ", pageSize=" + pageSize + ", currentPage="
				+ currentPage + "]";
	}

	
	
}
