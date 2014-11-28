package com.tidemedia.nntv.model;

import java.util.List;

public class LiveResponseModel extends BaseResponseModel {

	private List<Live> result;

	public List<Live> getResult() {
		return result;
	}

	public void setResult(List<Live> result) {
		this.result = result;
	}

	public class Live {
		private String id;
		private String isgb;
		private String title;
		private String image_url;
		private String video_url;
		private String time;
		private String gb_photo;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIsgb() {
			return isgb;
		}

		public void setIsgb(String isgb) {
			this.isgb = isgb;
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

		public String getGb_photo() {
			return gb_photo;
		}

		public void setGb_photo(String gb_photo) {
			this.gb_photo = gb_photo;
		}
	}
}
