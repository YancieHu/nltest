package com.tidemedia.nntv.model;

import java.io.Serializable;
import java.util.List;

public class ImgResonseModel extends BaseResponseModel {

	private List<ImgModel> result;

	public List<ImgModel> getResult() {
		return result;
	}

	public void setResult(List<ImgModel> result) {
		this.result = result;
	}

	public static class ImgModel implements Serializable {
		private String id;
		private String title;
		private String image_url;
		private String time;

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

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

	}
}
