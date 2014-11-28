package com.tidemedia.nntv.model;

import java.util.List;

public class ImgResModel extends BaseResponseModel {
	
	private List<ImgDetail> result;
	
	public List<ImgDetail> getResult() {
		return result;
	}

	public void setResult(List<ImgDetail> result) {
		this.result = result;
	}

	public class ImgDetail{
		private String title;
		private String image_url;
		private String intro;
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
	}
}
