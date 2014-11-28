package com.tidemedia.nntv.model;

import java.io.Serializable;
import java.util.List;

public class ColumnResponseModel extends BaseResponseModel implements Serializable {
	
	private List<Column> result;
	
	public List<Column> getResult() {
		return result;
	}

	public void setResult(List<Column> result) {
		this.result = result;
	}

	public static class Column implements Serializable {
		private String id;
		private String title;
		private String image_url;
		private String intro;
		private boolean hassingle;

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

		public String getIntro() {
			return intro;
		}

		public void setIntro(String intro) {
			this.intro = intro;
		}

		public boolean isHassingle() {
			return hassingle;
		}

		public void setHassingle(boolean hassingle) {
			this.hassingle = hassingle;
		}

	}
}
