package com.tidemedia.nntv.model;

public class Video {
	private String url;
	private String title;
	private int error;
	private String label;
	private String labelurl;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelurl() {
		return labelurl;
	}

	public void setLabelurl(String labelurl) {
		this.labelurl = labelurl;
	}

}
