package com.tidemedia.nntv.model;

import java.util.List;

public class SubColumnResponseModel extends BaseResponseModel {
	
	private SubColumnResponse result;
	
	public SubColumnResponse getResult() {
		return result;
	}
	public void setResult(SubColumnResponse result) {
		this.result = result;
	}
	public class SubColumnResponse{
		private List<SubColumn> list;

		public List<SubColumn> getList() {
			return list;
		}

		public void setList(List<SubColumn> list) {
			this.list = list;
		}
	}
	public class SubColumn {
		private String id;
		private String title;
		private String image_url;
		private String intro;
		private boolean is_video;
		private String video_url;
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
		public String getIntro() {
			return intro;
		}
		public void setIntro(String intro) {
			this.intro = intro;
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
}
