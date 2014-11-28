package com.tidemedia.nntv.model;

import java.io.Serializable;

public class NewsModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String image_url;
	boolean is_video;
	private String video_url;
	private String time;
	private String content;
	private String share_url;

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public boolean isIs_video() {
		return is_video;
	}

	public void setIs_video(boolean is_video) {
		this.is_video = is_video;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
